package com.example.PaymentService.Consumer;


import com.example.PaymentService.Model.Order;
import com.example.PaymentService.producer.PaymentProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

@Service
public class orderConsumer {
    private final PaymentProducer paymentProducer;
    public orderConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }
    @KafkaListener(topics="order-topic",groupId = "payment-service-group")
   public void receiveOrder(Order order) throws IOException {
        System.out.println("Received Order from Order-topic: " + order.getOrderId());
        System.out.println("Received Order Name: " + order.getOrderName());
        System.out.println("Received Order Price: " + order.getOrderPrice());
        if(order.getFileData()!=null&&order.getFileName()!=null) {
            byte[] bytes= Base64.getDecoder().decode(order.getFileData());
            String outFileName = "receiveded_"+order.getFileName();
            Files.write(Paths.get(outFileName), bytes);
            System.out.println("File saved to " + outFileName);
        }
        boolean check=OrderProcessor(order.getOrderPrice());
       // String ack="Acknowledgement from PaymentService payment sucessfull"+order.getOrderId();
        if(check) {
            String ack="Acknowledgement from PaymentService payment sucessfull with Id"+order.getOrderId();
            paymentProducer.sendAck(ack);
        }
        else {
            System.out.println("Payment failed");
        }
    }
    public static boolean OrderProcessor(double price)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Pay Rupees "+price);
        double orderPrice = sc.nextDouble();
        if(orderPrice!=price)
        {
            throw new IllegalArgumentException("Order Price is Invalid");
        }
        else
            return true;
    }
}