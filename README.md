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
* API Gateway - Spring Cloud Gateway
* Discovery - Spring Eureka Discovery Service
* Event Orchestration/SAGA - Kafka
* Authentication - OAuth2
* Frontend - React, Bootstrap
* Database - MongoDB
* Caching - Redis
* Montioring - Splunk, Prometheus, Grafana, Sentry
* Logging - SLf4J + LogBack, Splunk

# Architecture Overview:

## 1. API Gateway (OAuth2 Resource Server)
Entry point for all client requests. All requests go through the API Gateway
Gateway validates the user's JWT token
Gateway routes requests to appropriate services
Internal services trust requests coming from the gateway

## Auth Server (OAuth2 Authorization Server) - Current Service
Handles authentication
Issues JWTs
Manages client registrations
Coordinates with User Service for validation

## User Service
User CRUD operations
User validation for Auth Server
Profile management

## Post Service
Create/edit/delete posts
Image handling
Feed generation

## Comment Service
Manage comments on posts
Comment notifications

## Notification Service
Handle push notifications
Activity updates
Email notifications

## Media Service
## Search Service

## Kafka Event Architecture:
Services communicate asynchronously through Kafka events
No direct service-to-service HTTP calls
Event authentication/authorization handled at Kafka level

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

MongoDB is schema-less, which means you can start saving data to it without defining the schema up front. However, it is common practice to define a model class (entity) in your application to map to a MongoDB collection (in this case, the "user" collection). The schema definition happens in the application code, not in the MongoDB database itself.

```
mongosh
use myinsta-userdb
db.users.find()
```

# Authentication
For our Instagram clone with API Gateway and Kafka:

JWT is sufficient because:
You only need user authentication
All requests go through API Gateway
Services communicate via Kafka
No third-party integrations mentioned

OAuth2 would be needed if:
You wanted third-party apps to access your API
You needed different types of access (mobile, web, service)
You wanted to implement "Sign in with Instagram" for other apps

### Auth flow:
User logs in through React UI
Auth server validates credentials with User Service
Auth server issues JWT
React UI includes JWT in all API calls
API Gateway validates JWT and routes requests
Services communicate via Kafka events
Passwords are hashed (e.g., using BCrypt) and stored securely. BCrypt automatically handles the salting and the complexity of the hashing process. The salt is embedded within the hashed password, and it is used by the BCrypt algorithm to make password hashes unique even if the same password is used by different users.

Tokens are issued as JWTs by the Authorization Server.

Note:
Client: The React front-end (which could be running in the browser) will act as a "client" in the OAuth2 flow. It will send requests to your Authorization Server for tokens, passing its client_id and client_secret along with user credentials (username/password).
Client Validation: In your case, the client_id and client_secret will be the same for all requests since you're only using one client (the React app). The client_secret can be hardcoded in your back-end (Authorization Server), and you just validate the same credentials for every login request.
Since there's only one client, we can hardcode the client credentials in your ClientService. 

How It Works:
Authorization Server: This is the server responsible for authenticating the user (e.g., via username/password) and issuing an access token. This server can also issue a refresh token for obtaining new access tokens once they expire.

Resource Server: Once the authorization server issues the access token, the resource server is responsible for validating the access token and allowing or denying access to the protected resources (e.g., user data, posts, etc.) based on the token.

Client: This is the application that requests access to the protected resources. The client sends the access token to the resource server in the Authorization header of the HTTP request.

allowed.scopes=user.read,user.write,post.read,post.write,comment.read,comment.write,notification.read

# Secure Protocol
When React client sends the password to the backend, it will be in plain text unless we secure the communication between the client and the server using HTTPS (Hypertext Transfer Protocol Secure). HTTPS ensures that the data transmitted between the client (React) and the server (Spring Boot) is encrypted, preventing attackers from intercepting sensitive information like passwords. 
So I need to ensure following:
1. Enabling SSL in the application.properties or application.yml of each microservice.
2. Redirecting HTTP traffic to HTTPS, if you want to ensure that users can only access your services via secure channels.
3. Generate a self-signed certificate for development purposes. When developing locally, your browser or client may show a warning. For production, you should use a valid SSL certificate issued by a trusted certificate authority (CA) to avoid this.
4. Use HSTS to enforce HTTPS in the browser.
5. In your React client, ensure all URLs are https://.

Note: The API Gateway is the entry point for all client requests to your system. We must secure it with HTTPS as well. Although the Eureka Discovery Server typically doesn't serve sensitive data (it's primarily for service registration and discovery), it should still use HTTPS. The connection between the services and Eureka should be secure. Services should register and fetch metadata over HTTPS to ensure that no sensitive information (e.g., credentials) is sent over unencrypted channels. If all your microservices and the API Gateway use HTTPS, the Eureka server should also be configured to maintain a uniform security posture.

Note: This will generate a random 256-bit key encoded in Base64, which you can use as your JWT secret.: openssl rand -base64 32


# UUID
I will use the uuid as the primary identifier in my APIs (e.g., for fetching, updating, or deleting a user). And avoid exposing the MongoDB _id in the API responses, as it is specific to the database and less portable.

Globally Unique Identification: A UUID ensures that each user has a unique identifier that can be universally unique across systems or databases, even if they are distributed.
Decoupling from Natural Keys: Using UUIDs avoids relying on mutable or sensitive fields like usernames, emails, or phone numbers as unique keys. These fields can change over time, whereas a UUID remains constant.
Ease of Integration: UUIDs make it easier to integrate with external systems, APIs, or services that require unique identifiers.
Non-Sequential IDs: Unlike database-generated IDs (e.g., auto-increment integers), UUIDs are harder to predict, adding a layer of security for APIs where IDs are exposed.

UUID.randomUUID() generates a UUID using a random number generator. For UUID version 4, the collision probability is extremely low due to the large size of the UUID (128 bits). It has around 2^122 possible combinations, which makes the chance of a collision negligible. However, to ensure that UUID collisions are impossible, we will create a unique index in your MongoDB collection: db.users.createIndex({ uuid: 1 }, { unique: true });

# Custom Exceptions
I will create custom exceptions for different error scenarios. For instance:
1. UserNotFoundException for cases when the user is not found during login or other operations.
2. InvalidPasswordException for failed authentication due to incorrect passwords.
3. AccountLockedException for cases where the user’s account is temporarily locked due to failed login attempts.
4. EmailAlreadyTakenException for duplicate email errors during signup.

HTTP Status Codes: The app uses appropriate HTTP status codes for different error scenarios:
* 400 Bad Request for validation errors.
* 401 Unauthorized for failed login attempts.
* 404 Not Found when a user or resource is not found.
* 500 Internal Server Error for unexpected issues.

**Flow of HTTP Status Codes from the Backend to the React UI:**
Step 1: Backend API Service (User Service) Response:
When the User Service (or any other service) handles a request, it returns an HTTP response with a status code (e.g., 200 OK, 404 Not Found, 500 Internal Server Error) and the body (containing data or error messages).
For example:
200 OK indicates success and might return user data.
404 Not Found indicates that the user was not found.
401 Unauthorized indicates failed authentication.
500 Internal Server Error indicates something went wrong on the server.

Step 2: API Gateway:
The API Gateway routes requests to the appropriate backend service (e.g., User Service).
While the API Gateway may provide some additional functionality, such as authentication, rate limiting, or caching, it doesn't typically modify HTTP status codes unless you implement custom logic. It simply forwards the status code and the response body to the frontend.

Step 3: React UI:
React makes HTTP requests (e.g., via fetch or axios) to the backend API (through the API Gateway).
React then checks the HTTP status code in the response and updates the UI accordingly.
You can handle different HTTP status codes in the frontend and display appropriate UI messages or handle actions accordingly.

**Global Error Handler:**
A global error handler (via @ControllerAdvice in Spring or similar frameworks) is often used to catch exceptions across the app and return a standardized error response. This improves error consistency and helps handle issues like missing parameters or unhandled exceptions in a uniform way. The global error handler in a microservices-based architecture typically resides within each individual service, rather than in one centralized service. This allows each service to handle its specific exceptions and return appropriate error responses based on the context of the request.

# Frontend Considerations
React will be used to build the user interface (UI). The frontend interacts with the backend via the API Gateway using RESTful APIs. I am using React Router for client-side navigation and Axios/Fetch for making HTTP requests to the backend.

# User Management considerations
I will implement roles such as Admin, User, Moderator with appropriate access control policies. The API Gateway will extract the role and permission claims from the JWT token and ensure proper access control for each route. Spring Security’s method-level security annotations (@PreAuthorize) are helpful for this.

# Role of Redis for Caching:
Store Session Data in Redis. Store filter settings as a hash:
Key: user:preferences:{userId}
Value: { "theme": "dark", "sort": "newest", "language": "en" }


### API Endpoints

## 1. API Endpoint for User Registration

Method: POST

URL: /users/register

Request Body:

```json
{
  "username": "john_doe",
  "password": "password123",
  "roles": ["ROLE_USER"]
}
```

Response:
Success (201): User created successfully.
Failure (400): Validation error or duplicate username.

# Validations
In Spring Boot, you typically put validation checks for user details in the model class (e.g., UserRegistrationRequest) using Bean Validation annotations (like @NotNull, @Size, @Email, etc.), and then you can trigger the validation in the controller or service layer. If validation fails, Spring Boot automatically returns a 400 Bad Request response with the validation error messages.

MongoDB enforces a document size limit of 16 MB (16,777,216 bytes). This is the maximum size for a single document, which includes all field data. So, while individual fields like username or firstName are not inherently limited, they contribute to the overall document size. If the total document size exceeds 16 MB, MongoDB will reject the insert/update operation.

It's good practice to impose your own character limits for fields like username and firstName based on your application's needs.

Normalize Username to Lowercase Before Storing and Comparing.


# Login
1. Authorization Server Receives the Login Request:  The Authorization Server receives the username and password from the client (via the /oauth2/token endpoint).
```
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
client_id=instagram-client
client_secret=secret
username=johndoe
password=securePassword123
scope=read write

```

2. Authorization Server Calls the User Service : The Authorization Server sends an HTTP request to the User Service to validate the credentials.
```
POST /validate
Content-Type: application/json

{
    "username": "johndoe",
    "password": "securePassword123"
}
```

3. User Service Validates the Credentials : The User Service queries the users collection in the database to validate the username and password. If valid, the User Service returns the user's details (or just a confirmation response). If invalid, it returns an error.
```
{
    "status": "success",
    "userId": "12345",
    "roles": ["USER"]
}

{
    "status": "failure",
    "message": "Invalid username or password"
}

```

4. Authorization Server Generates Tokens : If the credentials are valid, the Authorization Server generates the access token and optional refresh token. If invalid, the Authorization Server returns an error response to the client.


