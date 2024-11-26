# Description
A social media photo sharing app like Instagram, emulating its core features and functionalities. This is for my own understanding and learning.

# Core Feautres
1. User Creation and Authentication - register, login, logout - JWT/OAuth
2. User Profile Management - view, edit
3. Create Post - image, title
4. Delete Post
5. Like Post
6. Search Users and Posts
7. Follow Users
8. User specific feed
9. Add/Delete/Like Comments

# Technology Stack
Backend - Java 8, Spring, Node.js
Frontend - React, Bootstrap
Messaging - Kafka
Database - MongoDB, Cassandra
Caching - Redis
Montioring - Splunk, Prometheus, Grafana, Sentry
Hosting - AWS

# Deployment Plan
Host frontend on platforms like Vercel or Netlify.
Deploy backend API on Heroku, AWS, or DigitalOcean.
Use a cloud storage service (like AWS S3) to host user-uploaded files (images and videos).

# Microservices
User Service
Post Service
Comment Service
Notification Service

# Role of the API Gateway
I will be using Spring Cloud Gateway for cross-service tasks like:

1. Path-Based Routing : I will configure Spring Cloud Gateway to route HTTP requests to different backend services based on the request path. For example, if the incoming request is /users/**, it could be forwarded to a User Service, and if the request is /posts/**, it could go to a Post Service.

2. Filters : I will add filters to modify requests or responses, for things like authentication, logging and rate limiting. I can modify headers (e.g., adding authentication tokens to requests) and transform response bodies before they are sent back to the client (e.g., change formats, mask sensitive information).

3. Authentication and Authorization : I will integrate Spring Security with Spring Cloud Gateway to centralize authentication and authorization logic at the API Gateway level. This means clients don’t need to authenticate separately with each microservice, reducing duplication of security logic across services. Spring Cloud Gateway can validate JWT tokens or perform OAuth2 authentication, ensuring that only authenticated users can access your services. CORS configuration can be managed centrally in the API Gateway. This allows the Gateway to handle cross-origin requests, ensuring that clients from different domains can securely interact with my microservices.

4. API rate limiting.

5. Routing by HTTP Method:  I will routes incoming requests to different microservices based on predicates (like path, headers, etc.) and uses service discovery for dynamic routing.

6. Metrics and Logging: I plan to track the performance and health of my microservices via Spring Cloud Gateway's integration with Micrometer and centralized logging tools.
