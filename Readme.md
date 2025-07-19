# Twotter
Twitter clone, because why not

## 1. Main Functionalities (User Stories)

Here are the core functionalities of the Twotter application, described from a user's perspective.

- **As a new user, I want to create an account** so that I can start using the platform.
- **As a registered user, I want to log in securely** to access my account and the application's features.
- **As a user, I want to post short messages (Twots)** to share my thoughts with others.
- **As a user, I want to view a timeline of Twots** from the people I follow to stay updated.
- **As a user, I want to follow other users** so I can see their content in my timeline.
- **As a user, I want to unfollow other users** if I am no longer interested in their content.
- **As a user, I want to view any user's profile** to see their posted Twots and basic information.
- **As a user, I want to upload profile pictures or other media** to personalize my profile and enrich my posts.

## 2. User Interaction (Use Cases)

This section describes how a user interacts with the system to achieve their goals.

| Use Case ID | Name                | Description                                                                                                                                                                          |
| ----------- | ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| UC-01       | Register Account    | A new user provides their details (e.g., username, password, email) to create a new account in the system. The system validates the information and confirms the registration.       |
| UC-02       | User Authentication | A registered user provides their credentials to log in. The system verifies the credentials and, upon success, issues a token to grant access to the application's features.         |
| UC-03       | Post Message        | An authenticated user writes and submits a new message. The system saves the message and makes it visible to other users.                                                            |
| UC-04       | View Timeline       | An authenticated user requests their timeline. The system gathers the latest messages from all users that the current user follows and presents them in reverse chronological order. |
| UC-05       | Manage Follows      | An authenticated user chooses to follow or unfollow another user. The system updates the relationship between the two users in its database.                                         |
| UC-06       | Upload Media        | An authenticated user uploads a file (e.g., an image). The system stores this file in a dedicated object storage and associates it with the user's profile or a specific post.       |

### 3. Technology Stack

- **Frontend: Vue.js**: The user interface is a completely independent Single-Page Application (SPA) built with Vue.js. Its responsibilities include rendering the user interface, managing client-side state, and initiating the authentication flow.
- **Backend Language: Kotlin**: A modern, concise, and null-safe language for the JVM.
- **Backend Framework: Ktor**: A lightweight, asynchronous web framework for Kotlin, used to build the REST API.
- **Identity and Access Management: Keycloak (OAuth2 Provider)**: The system uses Keycloak as a centralized and dedicated OAuth2 provider. It handles all aspects of user identity, including registration, login, token issuance, and security policies. This decouples user authentication from the core application logic.
- **Database: PostgreSQL with Exposed**: A powerful relational database paired with a type-safe SQL DSL.
- **File Storage: MinIO (S3 Compatible)**: An S3-compatible object storage service for user-generated media.

### 4. Architecture 

The architecture consists of three primary, decoupled components: a **Vue.js Frontend**, a **Ktor Backend (REST API)**, and a **Keycloak Identity Provider**. This separation of concerns is ideal for modern web applications.

The authentication is handled using the **OAuth2 Authorization Code Flow**, which is highly secure and well-suited for applications where the frontend (a public client) and backend (a confidential client) are separate.

#### Communication Flow for Authentication and API Access

The diagram below illustrates the complete OAuth2 flow from login to an authenticated API request.

sequenceDiagram  
    participant User  
    participant VueApp as Vue.js Frontend  
    participant Ktor as Ktor Backend  
    participant Keycloak  
  
    User->>+VueApp: 1. Clicks "Login"  
    VueApp->>+User: 2. Redirect to Keycloak  
    User->>+Keycloak: 3. Logs in with credentials  
    Keycloak-->>-User: 4. Redirect to Ktor backend <br> (callbackUrl) with auth code  
    User->>+Ktor: 5. Forwards auth code  
    Ktor->>+Keycloak: 6. Exchanges auth code for tokens <br> (Server-to-Server)  
    Keycloak-->>-Ktor: 7. Responds with Access & ID Tokens  
    Ktor-->>-User: 8. Redirect to frontend <br> (frontendLoginSuccessUrl) <br> with tokens  
    User->>+VueApp: 9. Forwards tokens  
    VueApp->>VueApp: 10. Stores tokens securely  
  
    Note right of VueApp: User is now logged in.  
  
    User->>+VueApp: 11. Triggers an API action <br> (e.g., Post Twot)  
    VueApp->>+Ktor: 12. Makes API request with <br> `Authorization: Bearer <Access-Token>`  
    Ktor->>Ktor: 13. Validates Access Token  
    Ktor->>+DB((Database)): 14. Performs business logic  
    DB-->>-Ktor:  
    Ktor-->>-VueApp: 15. API Response  
    VueApp-->>-User: 16. Updates UI  

**Flow Description:**

1. A user clicks the login button in the **Vue.js Frontend**.
2. The frontend redirects the user's browser to **Keycloak**.
3. The user authenticates directly with Keycloak.
4. Upon success, Keycloak redirects the browser to the `callbackUrl` handled by the **Ktor Backend**, including a one-time `authorization_code`.
5. The user's browser automatically sends this code to the Ktor backend.
6. The **Ktor Backend** makes a secure, direct, server-to-server request to Keycloak, exchanging the `authorization_code` (along with its `clientId` and `clientSecret`) for tokens.
7. **Keycloak** validates the request and returns an `access_token` and `id_token` to the Ktor backend.
8. The **Ktor Backend** then redirects the user's browser to the `frontendLoginSuccessUrl`, passing the tokens to the **Vue.js Frontend**.
9. The Vue app receives the tokens via the redirect.
10. The **Vue.js Frontend** securely stores the tokens (e.g., in memory) and uses them for subsequent API calls.
11. When the user performs an action, the Vue app sends a request to the Ktor backend API, adding the `access_token` to the `Authorization` header.
12. The **Ktor Backend** validates the token on every incoming request before processing it, ensuring the endpoint is secure.
## 5. Scalability

The application is designed with scalability in mind, thanks to several key architectural choices:

- **Asynchronous Processing**: By using Ktor on top of Netty, the application is fully asynchronous. This allows it to handle a large number of concurrent users with a small number of system threads, making it highly efficient and vertically scalable.
- **Stateless Authentication**: JWT-based authentication is stateless. The server does not need to store any session information for users. This means you can run multiple instances of the backend service behind a load balancer to scale horizontally without worrying about session synchronization.
- **Decoupled File Storage**: Storing user-generated files in an external object store (MinIO) decouples the application's state from the server's file system. This allows the application servers to remain stateless and enables the file storage system to be scaled independently of the application logic.
- **Efficient Database Handling**: Using a high-performance connection pool like HikariCP ensures that database connections, a common bottleneck, are managed efficiently. This prevents the application from overwhelming the database under high traffic.