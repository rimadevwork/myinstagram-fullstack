# Description
A social media photo sharing app like Instagram, emulating its core features and functionalities. The aim is to demonstrate containerized microservices and cloud-native architectures. 

# Core Feautres
1. User Creation and Authentication - register, login, logout - JWT/OAuth
2. User Profile Management - view, edit
3. Create Post - image, title
4. Delete Post
5. Like Post
6. Search Users and Posts
7. Follow/Unfollow Users
8. User specific feed
9. Add/Delete/Like Comments

# Technology Stack
* Backend - Java 8, Spring Boot
* API Gateway - Spring
* Discovery - Spring Eureka
* Events - Kafka
* Authentication - OAuth2
* Frontend - React, Bootstrap
* Database - MongoDB
* Caching - Redis
* Montioring - Splunk, Prometheus, Grafana, Sentry
* Hosting - AWS

# Microservices
* User Service
* Post Service
* Media Service
* Comment Service
* Notification Service
* Search Service

# Seperation of Concern in Post and Media Service
Given that media is a central part of Instagram-like apps, I will practise separation of concerns to keep services more modular and easier to scale in the future.

The Post Service would still be responsible for the business logic of creating posts, storing metadata (e.g., caption, tags), and associating media files with posts. The Media Service would be responsible for handling the actual media content (e.g., images, videos).

Here’s how the interaction would work:

** Post Service:** 
When a user creates a post, they upload media (e.g., an image or video).
The Post Service sends the media file to the Media Service to be uploaded and stored (possibly to a cloud storage like AWS S3 or Google Cloud Storage).
The Media Service responds with a URL or identifier for the uploaded media file.
The Post Service then creates a new post entry in the database (MongoDB), storing the metadata (e.g., caption, timestamp) along with the media file URL or media ID returned by the Media Service.

** Media Service:** 
Handles media upload and retrieval (using cloud storage, CDN, etc.).
Might include media processing logic (e.g., resizing images or compressing videos).

Example Flow:
User uploads a post with a media file.
Post Service calls the Media Service to upload the media.
Media Service uploads the file to cloud storage and returns a URL or media ID.
Post Service creates a post entry in MongoDB with the media URL and metadata (e.g., user, caption).
When users view the post, the Post Service fetches the post data (including the media URL) and displays it.

# Role of the API Gateway
I will be using Spring Cloud Gateway for cross-service tasks like:

1. Path-Based Routing : I will configure Spring Cloud Gateway to route HTTP requests to different backend services based on the request path. For example, if the incoming request is /users/, it could be forwarded to a User Service, and if the request is /posts/, it could go to a Post Service.

2. Filters : I will add filters to modify requests or responses, for things like authentication, logging and rate limiting. I can modify headers (e.g., adding authentication tokens to requests) and transform response bodies before they are sent back to the client (e.g., change formats, mask sensitive information).

3. Authentication and Authorization : Once the user is authenticated, Spring Cloud Gateway will validate the JWT or OAuth2 token on each request. If the token is valid, it will forward the request to the appropriate microservice.
The backend microservices themselves  will not  handle user authentication. They will rely on the authentication information (e.g., user ID, roles, claims) passed through the header by the gateway.

4. CORS configuration will be managed centrally in the API Gateway. This allows the Gateway to handle cross-origin requests, ensuring that clients from different domains can securely interact with my microservices.

5. API rate limiting.

6. Routing by HTTP Method:  I will routes incoming requests to different microservices based on predicates (like path, headers, etc.) and uses service discovery for dynamic routing.

7. Metrics and Logging: I plan to track the performance and health of my microservices via Spring Cloud Gateway's integration with Micrometer and centralized logging tools.

8. Caching: Use Redis to cache frequent queries (e.g., user profile or posts) and reduce the load on your backend services.

# Global Search Approach 
I will be using MongoDB Full-Text Search to keep it simple for now. The plan is to integrate ElasticSearch and synchronize MongoDB data with ElasticSearch.

# Tracking Followers/Following Approac
1. Use Redis as a caching layer to store and quickly access follow relationships, while MongoDB stores the persistent user data. Also, create a follows collection in MongoDB to represent each follow relationship. 
2. A graph database such as Neo4j might be a good option for highly complex social graph relationships and fast lookups for queries like mutual followers, recommendations, or friendship paths.
   
# Database Considerations
I have chosen MongoDB for its flexibilty and scalibilty to handling unstructured data like posts and user profiles. Things to consider:
1. Avoid large, deeply nested documents.
2. Add indexes on frequently queried fields (e.g., userId in posts, postId in comments).
3. Ensure MongoDB replication is set up with multiple nodes - For high availability.

# Frontend Considerations
React will be used to build the user interface (UI). The frontend interacts with the backend via the API Gateway using RESTful APIs. I am using React Router for client-side navigation and Axios/Fetch for making HTTP requests to the backend.

# User Management considerations
I will implement roles such as Admin, User, Moderator with appropriate access control policies. The API Gateway will extract the role and permission claims from the JWT token and ensure proper access control for each route. Spring Security’s method-level security annotations (@PreAuthorize) are helpful for this.
