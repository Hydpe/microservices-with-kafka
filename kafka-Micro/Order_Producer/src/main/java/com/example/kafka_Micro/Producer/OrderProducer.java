package com.example.kafka_Micro.Producer;

import com.example.kafka_Micro.Model.Order;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    private KafkaTemplate<String, Order> kafkaTemplate;
    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendOrder(Order order) {
        if(order.getOrderId() <= 0) {
            throw new IllegalArgumentException("Order Id is Invalid");
        }
        else if(order.getOrderName() ==null || order.getOrderName().equals("")) {
            throw new IllegalArgumentException("Order Name is Invalid");
        }
        else if(order.getOrderPrice() <= 0) {
            throw new IllegalArgumentException("Order Price is Invalid");
        }
        kafkaTemplate.send("order-topic", order);
        System.out.println("Order sent to order-topic with id: " + order.getOrderId());
    }

}
