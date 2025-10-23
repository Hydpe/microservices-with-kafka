package com.example.PaymentService.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
      private KafkaTemplate<String,String> kafkaTemplate;
      public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate) {
          this.kafkaTemplate = kafkaTemplate;
      }
      public void sendAck(String ack) {
          kafkaTemplate.send("payment-topic", ack);
      }
}
