package com.example.kafka_Micro.controller;

import com.example.kafka_Micro.Model.Order;
import com.example.kafka_Micro.Producer.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;


@RestController
@RequestMapping("/Order")
public class OrderController {
    private OrderProducer orderProducer;
    @Autowired
    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }
    @GetMapping
    public String Working()
    {
        return "Hello World!";
    }
    @PostMapping
    public void sendOrder(@RequestBody Order order) {
       orderProducer.sendOrder(order);
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("orderId") int orderId,@RequestParam("orderName") String orderName,@RequestParam("orderPrice") double orderPrice,@RequestParam("file") MultipartFile file) throws IOException {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderName(orderName);
        order.setOrderPrice(orderPrice);
        order.setFileName(file.getOriginalFilename());
        order.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));
        orderProducer.sendOrder(order);
        return "File send to order-topic: " + order.getOrderId();
    }
}
