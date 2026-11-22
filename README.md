# LedgerLine – Secure Digital Banking & Verifiable Transaction Ledger

A full-stack digital banking application designed to support secure account management, deposits, money transfers and verifiable financial transactions.

## 1. Problem Statement

Digital money transfers require accuracy, security and transparency. LedgerLine addresses these challenges through secure authentication, controlled account access and a double-entry transaction ledger that helps verify financial consistency.

## 2. Key Features

### User Features
- User registration and login.
- JWT-based authentication.
- Create and view bank accounts.
- Deposit money.
- Transfer money between accounts.
- View account balances.
- View transaction history.

### Financial Integrity
- Double-entry bookkeeping.
- Debit and credit ledger entries.
- Transaction verification.
- Idempotency key support.
- Database transaction management.
- Account ownership validation.
- Concurrency controls.

### Admin Features
- Role-based access control.
- View registered users.
- Block and unblock users.
- View audit logs.
- Monitor important account activities.

## 3. Technology Stack

| Component | Technologies |
|---|---|
| Frontend | React.js, JavaScript, CSS, Vite |
| Backend | Java, Spring Boot |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Build Tool | Maven |
| API Testing | Postman |
| Frontend Hosting | Vercel |
| Backend Hosting | Render |
| Version Control | Git, GitHub |

## 4. System Architecture

```text
          USER
           |
           v
    React.js Frontend
        (Vercel)
           |
        REST APIs
           |
           v
    Spring Boot Backend
        (Render)
           |
    Spring Security
       JWT Auth
           |
           v
     Service Layer
           |
    Transaction Logic
           |
           v
      PostgreSQL
      (Render DB)
           |
           v
      Ledger Entries
    Debit / Credit
```

## 5. Live Application

- **Frontend:** https://ledgerline-frontend-six.vercel.app/
- **Backend:** https://ledgerline-backend-imey.onrender.com
- **Health Check:** https://ledgerline-backend-imey.onrender.com/api/health

## 6. Project Structure

```text
LEDGERLINE/
│
├── backend/
│   └── ledgerline/
│       ├── src/
│       ├── pom.xml
│       └── mvnw
│
├── frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── docs/
├── README.md
└── .gitignore
```

## 7. Prerequisites

Install the following:

- Java JDK 21
- PostgreSQL 18
- Node.js and npm
- Git

## 8. Run Locally

### Step 1: Clone the repository

```bash
git clone https://github.com/meghanamathi/LEDGERLINE.git
cd LEDGERLINE
```

### Step 2: Configure PostgreSQL

Create a database:

```sql
CREATE DATABASE ledgerline_db;
```

Create the database user and grant appropriate permissions. Configure the database password securely.

### Step 3: Configure backend environment variables

Open PowerShell:

```powershell
cd backend/ledgerline

$env:DB_PASSWORD="YOUR_DATABASE_PASSWORD"
$env:JWT_SECRET="YOUR_SECURE_RANDOM_SECRET"
```

Use a strong random JWT secret. Never commit secrets to GitHub.

### Step 4: Start the backend

```powershell
.\mvnw.cmd spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

### Step 5: Start the frontend

Open another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open the Vite development URL shown in the terminal, usually:

```text
http://localhost:5173
```

**Note:** Configure the frontend API base URL for local development to point to `http://localhost:8080`. The deployed frontend uses the production Render backend URL.

## 9. Important API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | User login |
| GET | `/api/accounts` | View accounts |
| POST | `/api/accounts` | Create account |
| POST | `/api/deposits` | Deposit money |
| POST | `/api/transfers` | Transfer money |
| GET | `/api/accounts/{accountNumber}/transactions` | Transaction history |
| GET | `/api/ledger/verify/{transferId}` | Verify ledger |
| GET | `/api/admin/users` | View users (Admin) |
| GET | `/api/admin/audit-logs` | View audit logs (Admin) |

Protected endpoints require a valid JWT Bearer token.

## 10. Transaction Integrity

LedgerLine uses double-entry bookkeeping for money transfers.

Each successful transfer creates:
- One DEBIT ledger entry for the sender.
- One CREDIT ledger entry for the receiver.

The ledger verification service checks that both entries exist and that their amounts match.

Idempotency keys help prevent duplicate processing of repeated requests.

## 11. Testing

The following functionality has been tested during development using Postman and the local application:

- User registration and login.
- Account creation.
- Deposit processing.
- Money transfers.
- Transaction history.
- Ledger verification.
- Account ownership validation.
- Admin-only API authorization.
- User blocking and unblocking.
- Audit log retrieval.
- Repeated deposit requests using an idempotency key.

Production deployment health has also been verified.

## 12. Known Limitations and Future Improvements

- Complete production Admin account setup and verification.
- Complete end-to-end testing of all features on the deployed application.
- Expand automated integration and concurrency testing.
- Improve monitoring and production observability.
- Add additional reporting and transaction analytics.
- Plan database migration before the Render free database expiry.

## 13. Security Notes

- Passwords are stored using BCrypt hashing.
- JWT is used for authenticated API access.
- Role-based authorization protects administrative endpoints.
- Account ownership is checked before financial operations.
- Database credentials and JWT secrets must be supplied through environment variables.
- Never commit `.env` files, passwords or production secrets.

## 14. Contributors

Developed as part of the Snovasys Fresher Hiring Challenge 2026.

**Project:** LedgerLine – BalanceVault

**Repository:** https://github.com/meghanamathi/LEDGERLINE

---

LedgerLine aims to make digital money movement secure, consistent and verifiable through a reliable transaction ledger.
