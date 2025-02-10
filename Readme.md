# Payment Integration Gateway

## Overview

The **Payment Integration Gateway** project is a robust and secure eCommerce and payment processing system designed specifically for the European market. This system enables seamless payment processing through integration with **Trustly**, providing customers access to multiple bank integrations and a streamlined payment experience.

The project focuses on creating a scalable, maintainable, and efficient payment gateway by leveraging microservices architecture, Spring Boot, and advanced validation frameworks. The primary goal is to deliver a high-performance solution to expand the customer base by offering secure and reliable payment options.

---

## Key Features

### Payment Processing and Trustly Integration
- Integrated **Trustly Payment Provider** to offer multiple bank connections, enhancing customer payment options.
- Facilitated end-to-end integration with third-party APIs using RESTful communication over HTTP.

### HMAC Security
- Implemented **HMAC (Hash-based Message Authentication Code)** to secure sensitive payment data.
- Ensures data integrity and authenticity by generating and validating message hashes during external API communication.
- Added middleware to verify incoming requests and authenticate payloads.

### Validation Framework
- Designed and developed a **modular validation framework** for the payment system using **Spring IOC** for dependency injection.
- Implemented field-level and business-level validation rules to ensure data integrity and error reduction before processing payments.

### Error Handling
- Implemented a structured error code system across all services to provide meaningful feedback and simplify debugging.
- Enhanced system reliability by proactively managing edge cases and potential integration issues.

### RESTful Communication Over HTTP
- Utilized **RESTful APIs** to enable seamless interaction between microservices and external payment systems.
- Simplified API design and implementation using Spring Boot's REST capabilities.

### Microservices Architecture
- Adopted **microservices architecture** to improve scalability, maintainability, and independent operation of components.
- Leveraged Spring Boot for rapid development, auto-configuration, and dependency management.

---



## Technologies Used

- **Programming Language**: Java
- **Framework**: Spring Boot, JDBC 
- **Security**: HMAC
- **API Communication**: REST over HTTP
- **Database**: MongoDB
- **Build Tool**: Maven


### Prerequisites
- Java 11 or higher
- Maven
- MongoDB


