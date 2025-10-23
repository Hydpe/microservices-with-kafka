package com.example.kafka_Micro.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class paymentAckConsumer {
    @KafkaListener(topics = "payment-topic",groupId = "order-service-group")
    public void listen(String message){
        System.out.println("Receive ACK " + message);
    }
}
