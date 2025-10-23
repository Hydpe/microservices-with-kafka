package com.example.kafka_Micro.Model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Order {
    private int orderId;
    private String orderName;
    private double orderPrice;

    private String fileName;
    private String fileData;
}
