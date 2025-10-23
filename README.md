Kafka MicroServices Project (OrderService and PaymentService)

This Project demonstrates Inter Service Communication using kafka in between two micro services

1) Order Service acts as producer (send orders to paymentService with or without image file .png)
2) Payment Service acts as consumer(Receives Orders , process Payment and Acknowledge producer As succesfull Payment for the received Order)

Sample Output for the Project

# In Postman 
Post http://localhost:8081/Order/upload
in Body ->form Data
  key             value
  orderId         200
  orderName       Laptop
  orderPrice      49000
  file            Icon128.pg


# On OrderService Side
 # SAMPLE OUTPUT
Order sent to order-topic with id: 300
Receive ACK Acknowledgement from PaymentService payment sucessfull with Id300
Order sent to order-topic with id: 400
Receive ACK Acknowledgement from PaymentService payment sucessfull with Id400
Order sent to order-topic with id: 500
Receive ACK Acknowledgement from PaymentService payment sucessfull with Id500

# On PyamentService Side

 # SAMPLE OUTPUT
Received Order from Order-topic: 400
Received Order Name: Hp
Received Order Price: 40000.0
File saved to receiveded_icon128.png     
Pay Rupees 40000.0
40000
Received Order from Order-topic: 500
Received Order Name: Hp
Received Order Price: 60000.0
File saved to receiveded_icon128.png
Pay Rupees 60000.0
60000
Technologies Used
1) Java version 21
2) SpringBoot version 3X
3) Apache Kafka 3.9.1 with scala 2.13 
4) Zookeeper
5) Maven
6) Json Serialization and deserialization (spring kafka)

steps

Goto website Apache kafka 
Download kafka version 3.9.1 with scala 2.13
Unzip the folder and extract somewhere 
Copy the path of the folder

Open Command Prompt 
go to kafka directory
cd c:\Kafka

Run Zookeeper 
# for windows
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
# or on Linux/Mac:
bin/zookeeper-server-start.sh config/zookeeper.properties

Open Another command prompt 
go to kafka Directory
cd C:\kafka

Run Kafka Broker
# for windows
.\bin\windows\kafka-server-start.bat .\config\server.properties
# or on Linux/Mac:
bin/kafka-server-start.sh config/server.properties

Open Another Command prompt 
go to Kafka Directory

## keep the above two Command windows open

Create Topics 
# order-topic with 3 partitions
.\bin\windows\kafka-topics.bat --create --topic order-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# payment-topic with 3 partitions
.\bin\windows\kafka-topics.bat --create --topic payment-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1


Steps for spring Microservices Creation

# Goto spring Initializer 
Name the project as OrderProducer
Add Dependencies
springWeb
lombok to reduce Boilerplate code
Apache kafka
Download the folder

# Micro Service 1- Order Producer
generates orders and sent to Topic  "Order-Topic"
If order has file It Encodes the File data Into Base64 String 
# Main file 
# OrderProducer
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

# Micro Service 2- Payment service
consumes Orders from the topic Order-topic , Decodes file, process Payments and give acknowledgement back to the OrderService
# Main File
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

Project flow
1 OrderService generates Orders Object(Order Id, OrderName, OrderPrice, Base64 data)
2.Kafka stores the messages in one of the partitions of the order-Topic
2 PaymentService Consumes Order Object from the order Topic,decodes The file WHich is in BAse64, Save it locally, Process Payments and  Acknowledge OrderServices
4.all communication happens throw kafka.










