# PROMPTS.md

# LedgerLine – AI Prompts and Development Notes

## 1. AI Tool Used

- **ChatGPT:** Used for project planning, understanding concepts, debugging, code guidance, API testing support, deployment troubleshooting, and documentation.
- **Other development tools:** VS Code, Postman, GitHub, Render, Vercel, and pgAdmin. These were used as development and deployment tools, not AI coding assistants.

## 2. Important Prompts Used

### Project Planning
- Help me design a secure money movement application using React, Spring Boot, and PostgreSQL.
- Suggest a backend architecture for a banking application with authentication, accounts, deposits, transfers, and transaction history.
- Explain how to implement a double-entry ledger to maintain accurate financial records.

### Backend Development
- Help me implement user registration and login using Spring Security and JWT authentication.
- Explain how to implement role-based access control for ADMIN and USER roles.
- Help me create REST APIs for account management, deposits, and money transfers.
- Explain how to prevent duplicate financial transactions using idempotency keys.
- Help me implement ledger verification to ensure debit and credit entries are balanced.

### Frontend Development
- Help me connect a React frontend to a Spring Boot REST API.
- Explain how to manage JWT tokens and authenticated API requests.
- Help me build user and admin dashboards for LedgerLine.
- Help me troubleshoot frontend API connection and CORS errors.

### Testing and Deployment
- Help me test REST APIs using Postman.
- Explain how to deploy a Spring Boot application with PostgreSQL on Render.
- Help me deploy the React frontend using Vercel.
- Help me configure production environment variables and database connections.
- Help me troubleshoot deployment and connection errors.

## 3. What AI Got Wrong and What I Fixed

AI suggestions were treated as guidance, not blindly accepted. I tested the implementation and corrected issues when the suggestions did not match the actual project setup.

### 1. Local and Production Database Difference
- **AI assumption:** An admin account created in the local database could be used after deployment.
- **What I learned:** Render uses a separate production database, so local users are not automatically available in production.
- **My correction:** I identified the separate database and planned to create and verify a production admin account independently.

### 2. CORS Configuration
- **Initial issue:** The backend allowed local frontend origins but did not initially include the deployed Vercel frontend.
- **My correction:** I added the production frontend origin to the backend CORS configuration and redeployed the backend.

### 3. Local API URLs in Production
- **Initial issue:** The frontend still contained localhost API URLs.
- **My correction:** I replaced the local backend URLs with the deployed Render backend URL, tested the frontend build, and redeployed it.

### 4. Transaction Idempotency
- **Testing observation:** Repeating a deposit request with the same idempotency key returned a successful response more than once.
- **My verification:** I checked the account balance and transaction history and confirmed that the amount was credited only once.
- **Learning:** HTTP response status alone is not enough to verify idempotency; the actual financial records must also be checked.

### 5. Account Ownership and Authorization
- **Testing observation:** Accessing another user's transaction history returned 404 instead of exposing the account.
- **My verification:** I compared it with a request for the user's own account, which returned 200.
- **Learning:** The application intentionally avoids revealing another user's account information.

### 6. Production Admin Setup
- **Initial approach:** I considered reusing the local admin account.
- **Correction:** I recognized that production data is separate and that the production admin must be configured in the production database using a securely encoded password.

## 4. What I Verified Myself

I did not rely only on AI-generated suggestions. I manually tested important functionality.

- User registration and login.
- JWT-based authentication.
- Role-based access restrictions.
- Account creation and account listing.
- Deposits and money transfers.
- Transaction history.
- Double-entry ledger verification.
- Duplicate request handling using idempotency keys.
- User blocking and unblocking.
- Admin dashboard and audit logs in the local environment.
- Backend health endpoint after deployment.
- Production frontend registration and backend connectivity.

## 5. My Contribution

I was responsible for:
- Setting up the project and development environment.
- Integrating the React frontend with the Spring Boot backend.
- Configuring PostgreSQL and managing database connections.
- Running API tests using Postman.
- Debugging application and deployment errors.
- Configuring Render and Vercel deployments.
- Verifying application behavior through actual testing.
- Reviewing AI suggestions and adapting them to the project requirements.

## 6. Important Note

ChatGPT was used as a development assistant. The generated suggestions were reviewed, adapted, and tested. Final implementation decisions and verification were performed as part of the project development process.
