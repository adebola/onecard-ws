![Image](https://recharge.onecardnigeria.com/static/media/lightlogo.b9e05b35b3e5fb8e0fc37b6c54783b00.svg)
# Onecard Project
### This is a Java Spring Backend Project for the Onecard Project, the web frontend is available 
[here](https://recharge.onecardnigeria.com)
#### The Project is Mircroservice based and has the micro projects or services
#### Essentially Onecard is a onestop shop for recharges in Nigeria, the following services have been onboarded
##### 1. MTN Airtime & Data
##### 2. Glo Airtime & Data
##### 3. Airtel Airtime & Data
##### 4. 9Mobile Airtime & Data
##### 5. Spectranet Data
##### 6. Smile Data
##### 7. GOTV
##### 8. DSTV
##### 9. Startimes
##### 10. Jos Electric
##### 11. Eko Electric

### The application is REST based set of Microservices consisting of the following services


#### 1. Provider Service - Manages the products and fulfillment of recharges topup's etc., also manages direct connections to the respective service providers
#### 2. User Service - Manages Users on the system, in conjunction with Keycloak our OAuth server
#### 3. Payments Service - Manages Payments and interfaces with onboarded Payment Gateways
#### 4. Audit Service - Audits relevant activities to a MongoDB database
#### 5. Communications Service - Manages all external communications such as e-mail, sms etc. with 3rd parties
#### 6. Wallet Service - Manages Internal Wallets, it also serves as one of the payment gateways to the Payment Service
#### 7. Voucher Services - Manages Onecard Network agnostic generated Vouchers
#### 8. Report Service - Manages Reports
#### 9. APIUser Service - This service serves as the gateway for all 3rd Party API requests into the system
#### 10. frame-config - Java Spring Configuration Service, loads all runtime config from Github
#### 11. frame-eureka - Java Spring Discovery Service
#### 12. frame-gateway - Java Spring Gateway Service
