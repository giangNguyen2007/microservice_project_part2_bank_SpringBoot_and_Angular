# microservice_project_part2_bank_SpringBoot_and_Angular
Microservice project - part B - banking application

This is part B of my microservice project. [part A](https://github.com/giangNguyen2007/microservice_project_ecom_ASP.NET_and_Angular)  should be consulted first (only the part A's readme gives a complete view of the project) and launched together with part B. 


## 1. Application Launch Steps

Prerequisite : the creation of Docker network and launching of rabbitMq container should have been done when launching ecommerce app in Part A.


(2) Build the images for both front and backend services:

```bash
#inside/backend-springboot
docker-compose build

#inside /frontend-angular
docker-compose build


```

The image building process involves compiling the source code for all microservices, thus this can take several minutes to finish.


(3) Launch the containers

```bash
#inside/backend-springboot
docker-compose up -d

#inside /frontend-angular
docker-compose up -d

```

The frontend is accessible at localhost:80

# 2. Frontend 

The frontend allows user to perform the following actions:
- Register and login as normal user 
- Create account
- Create Deposit Transactions 
- Create Transfer Transaction: It is possible to transfer fund between account of different users
- Validate payment request : pending payment request sent from ecommerce app, to be validated or refused.

Internal transactions (deposit & transfer) are executed immediately by the API.
External transaction (created from payment request validation) may take a while to finish (to simulate real world external payment). The transaction status is "PENDING" at creation, before being changed to "CONFIRMED". 

The account page shows the history of transactions (pending or executed).

# 3. Backend

## 3.1 Overview

The application is composed of 4 microservices, each one being deployed inside a container:
+ Gateway : all other services are exposed via the gateway url 
+ UserAPI : handles user register and login
+ AccountAPI: comprises 3 controllers for CRUD operations on:
    - Accounts: bank account
    - Transactions: deposit, transfer or external payment transactions
    - Incoming Transaction: payment requests sent by ecommerce app, to be validated by account owner before becoming effective external payment transaction
+ PaymentAPI: manages payment requests and processing


It is in the late stage of the project that I decided to add the Incoming-Transaction controller, making the AccountAPI handle too many responsabilities. For improvement perspective, Transactions operations could have been moved into a separate API. On the other hand, that will lead to a lot of inter-service communication between AccountAPI and TransactionAPI.

To avoid creating too many database containers, I have replaced most of MySQL databases, chosen initially, by Sqlite, which allows storing database file inside the API container.


![Spring app Endpoints](./bankApp_ASP.NET-SPRING%20Backend%20General%20Schema.drawio.png)


## 3.2 Technical points

I would like to hightlight the folollwing technical points :
- Separation of concern: separate service layers for database interaction, grpc & rabbitMQ communication are handled in separate services in each API
- Separate error handling code using custom Exception => coherent and easy to maintain error handling
- Authentication is performed at Gateway level, which decodes Jwt token then attachs user info to the Request Object. At Downstream API, request passes through authentication Interceptor before reaching controller.  
- Any database services' function involving more than one database operations is annotated with @Transaction
- To avoid negative bank account total: For external transactions (which are not executed immediately but has to pass by `PaymentAPI`), the payment amount is deducted first from `bank account` and added into a `reserved account`, which will be consummed upon receiving success response from PaymentAPI. (this is an inexact simulation of real life mechanism. In retrospective, I could have done better by making the `PaymentAPI` send request to `AccountAPI` to consume the reserved account)


## 3.3 Inter-Service Communications

GRPC communication between `AccountAPI` (client) and `UserAPI` : upon POST account/transaction requests, the AccountAPI sends user info request to UserAPI to ensure valid user ID (double check, in addition to jwt authentication, for more security)

RabbitMq communication between `AccountAPI` and `PaymentAPI` : To execute external payment transaction, a new payment request is sent to `PaymentAPI`






