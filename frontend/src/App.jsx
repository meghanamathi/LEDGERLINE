
import { useState, useEffect } from "react";
import "./App.css";

function App() {
  // Authentication states
  const [isRegister, setIsRegister] = useState(false);
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [auditLogs, setAuditLogs] = useState([]);
const [auditLoading, setAuditLoading] = useState(false);
const [auditError, setAuditError] = useState("");
  // Transfer states
const [showTransfer, setShowTransfer] = useState(false);
const [senderAccount, setSenderAccount] = useState("");
const [receiverAccount, setReceiverAccount] = useState("");
const [transferAmount, setTransferAmount] = useState("");
const [transferLoading, setTransferLoading] = useState(false);
const [transferSuccess, setTransferSuccess] = useState("");
const [transferError, setTransferError] = useState("");
  // Dashboard states
  const [accounts, setAccounts] = useState([]);
  const [dashboardLoading, setDashboardLoading] = useState(false);
  const [dashboardError, setDashboardError] = useState("");
// Transaction History states
const [transactions, setTransactions] = useState([]);
const [selectedHistoryAccount, setSelectedHistoryAccount] = useState("");
const [historyLoading, setHistoryLoading] = useState(false);
const [historyError, setHistoryError] = useState("");
  // Create account states
  const [showCreateAccount, setShowCreateAccount] = useState(false);
  const [accountHolderName, setAccountHolderName] = useState("");
  const [creatingAccount, setCreatingAccount] = useState(false);
  const [accountSuccess, setAccountSuccess] = useState("");
  const [accountError, setAccountError] = useState("");

  // Deposit states
  const [showDeposit, setShowDeposit] = useState(false);
  const [depositAccount, setDepositAccount] = useState("");
  const [depositAmount, setDepositAmount] = useState("");
  const [depositLoading, setDepositLoading] = useState(false);
  const [depositSuccess, setDepositSuccess] = useState("");
  const [depositError, setDepositError] = useState("");
// Admin Dashboard states
const [adminUsers, setAdminUsers] = useState([]);
const [adminLoading, setAdminLoading] = useState(false);
const [adminError, setAdminError] = useState("");
const [showAdminDashboard, setShowAdminDashboard] = useState(false);
  const [verification, setVerification] = useState(null);
const [verificationLoading, setVerificationLoading] = useState(false);
const [verificationError, setVerificationError] = useState("");
  const storedUser = localStorage.getItem("user");
  const token = localStorage.getItem("token");

  // Common API response handler
  const readResponse = async (response) => {
    const text = await response.text();

    let data ;

    try {
      data = text ? JSON.parse(text) : {};
    } catch {
      data = { message: text || "Unexpected server response" };
    }

    if (!response.ok) {
      throw new Error(
        data.message ||
        data.error ||
        "Request failed. Please try again."
      );
    }

    return data;
  };

  // Fetch user's accounts
  const fetchAccounts = async () => {
    const response = await fetch(
      "http://localhost:8080/api/accounts",
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await readResponse(response);
    setAccounts(Array.isArray(data) ? data : []);
  };
// Fetch transaction history for an account
const fetchTransactions = async (accountNumber) => {
  setHistoryLoading(true);
  setHistoryError("");

  try {
    const response = await fetch(
      `http://localhost:8080/api/accounts/${accountNumber}/transactions`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await readResponse(response);

    setTransactions(Array.isArray(data) ? data : []);
    setSelectedHistoryAccount(accountNumber);
  } catch (err) {
    setHistoryError(err.message);
    setTransactions([]);
  } finally {
    setHistoryLoading(false);
  }
};
// Verify transfer ledger
const verifyTransfer = async (transferId) => {
  setVerificationLoading(true);
  setVerificationError("");
  setVerification(null);

  try {
    const response = await fetch(
      `http://localhost:8080/api/ledger/verify/${transferId}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await readResponse(response);

    setVerification(data);
  } catch (err) {
    setVerificationError(
      err.message || "Unable to verify the transfer ledger."
    );
  } finally {
    setVerificationLoading(false);
  }
};
// Fetch all users for Admin Dashboard
const fetchAdminUsers = async () => {
  setAdminLoading(true);
  setAdminError("");

  try {
    const response = await fetch(
      "http://localhost:8080/api/admin/users",
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await readResponse(response);

    setAdminUsers(Array.isArray(data) ? data : []);
  } catch (err) {
    setAdminError(err.message);
  } finally {
    setAdminLoading(false);
  }
};
// Fetch Admin Audit Logs
const fetchAuditLogs = async () => {
  setAuditLoading(true);
  setAuditError("");

  try {
    const response = await fetch(
      "http://localhost:8080/api/admin/audit-logs",
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await readResponse(response);
    setAuditLogs(Array.isArray(data) ? data : []);
  } catch (err) {
    setAuditError(err.message);
  } finally {
    setAuditLoading(false);
  }
};
// Block or unblock a user
const updateUserStatus = async (userId, active) => {
  const action = active ? "unblock" : "block";

  const confirmed = window.confirm(
    `Are you sure you want to ${action} this user?`
  );

  if (!confirmed) return;

  setAdminLoading(true);
  setAdminError("");

  try {
    const response = await fetch(
      `http://localhost:8080/api/admin/users/${userId}/status?active=${active}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const data = await readResponse(response);

    await fetchAdminUsers();

    alert(data.message || "User status updated successfully.");
  } catch (err) {
    setAdminError(err.message);
    setAdminLoading(false);
  }
};
  useEffect(() => {
    if (!token || !storedUser) return;

    const loadAccounts = async () => {
      setDashboardLoading(true);
      setDashboardError("");

      try {
        await fetchAccounts();
      } catch (err) {
        setDashboardError(err.message);
      } finally {
        setDashboardLoading(false);
      }
    };

    loadAccounts();
  }, [token, storedUser]);

  // Login and Registration
  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");
    setLoading(true);

    const endpoint = isRegister ? "register" : "login";

    const requestBody = isRegister
      ? { fullName, email, phoneNumber, password }
      : { email, password };

    try {
      const response = await fetch(
        `http://localhost:8080/api/auth/${endpoint}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        }
      );

      const data = await readResponse(response);

      if (isRegister) {
        setSuccess("Registration successful! Please sign in.");
        setIsRegister(false);
        setPassword("");
        setShowPassword(false);
      } else {
        localStorage.setItem("token", data.token);

        localStorage.setItem(
          "user",
          JSON.stringify({
            userId: data.userId,
            fullName: data.fullName,
            email: data.email,
            role: data.role,
          })
        );

        window.location.reload();
      }
    } catch (err) {
      setError(err.message || "Unable to connect to server");
    } finally {
      setLoading(false);
    }
  };

  // Create a new bank account
  const handleCreateAccount = async (e) => {
    e.preventDefault();

    setCreatingAccount(true);
    setAccountError("");
    setAccountSuccess("");

    try {
      const response = await fetch(
        "http://localhost:8080/api/accounts",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            holderName: accountHolderName,
          }),
        }
      );

      const data = await readResponse(response);

      await fetchAccounts();

      setAccountSuccess(
        `Account created successfully! Account Number: ${data.accountNumber}`
      );

      setAccountHolderName("");
      setShowCreateAccount(false);

    } catch (err) {
      setAccountError(err.message);
    } finally {
      setCreatingAccount(false);
    }
  };

  // Deposit money into an account
  const handleDeposit = async (e) => {
    e.preventDefault();

    const amount = Number(depositAmount);

    if (!Number.isFinite(amount) || amount <= 0) {
      setDepositError("Enter a valid deposit amount.");
      return;
    }

    setDepositLoading(true);
    setDepositError("");
    setDepositSuccess("");

    try {
      const response = await fetch(
        "http://localhost:8080/api/deposits",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            accountNumber: depositAccount,
            amount,
            idempotencyKey: crypto.randomUUID(),
          }),
        }
      );

      await readResponse(response);

      // Refresh balances after deposit
      await fetchAccounts();

      setDepositSuccess(
        `₹${amount.toLocaleString("en-IN", {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })} deposited successfully into account ${depositAccount}.`
      );

      setDepositAmount("");
      setShowDeposit(false);

    } catch (err) {
      setDepositError(err.message);
    } finally {
      setDepositLoading(false);
    }
  };
// Transfer money
const handleTransfer = async (e) => {
  e.preventDefault();

  const amount = Number(transferAmount);

  if (!Number.isFinite(amount) || amount <= 0) {
    setTransferError("Enter a valid transfer amount.");
    return;
  }

  if (senderAccount === receiverAccount) {
    setTransferError("Sender and receiver accounts must be different.");
    return;
  }

  setTransferLoading(true);
  setTransferError("");
  setTransferSuccess("");

  try {
    const response = await fetch(
      "http://localhost:8080/api/transfers",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          senderAccountNumber: senderAccount,
          receiverAccountNumber: receiverAccount.trim(),
          amount,
          idempotencyKey: crypto.randomUUID(),
        }),
      }
    );

    await readResponse(response);

    // Refresh account balances
    await fetchAccounts();

    setTransferSuccess(
      `₹${amount.toLocaleString("en-IN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      })} transferred successfully!`
    );

    setTransferAmount("");
    setReceiverAccount("");
    setShowTransfer(false);

  } catch (err) {
    setTransferError(err.message);
  } finally {
    setTransferLoading(false);
  }
};
  // Logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.reload();
  };

  // Dashboard screen
  if (storedUser && token) {
    let user;

    try {
      user = JSON.parse(storedUser);
    } catch {
      localStorage.removeItem("user");
      localStorage.removeItem("token");
      window.location.reload();
      return null;
    }

    return (
      <div className="dashboard-page">

        {/* Header */}
        <header className="dashboard-header">
          <div className="brand">
            <div className="brand-icon">L</div>
            <span>LedgerLine</span>
          </div>

          <div className="dashboard-user">
  <span>{user.fullName}</span>

  {user.role === "ADMIN" && (
    <button
      onClick={() => {
        setShowAdminDashboard(!showAdminDashboard);
        if (!showAdminDashboard) {
  fetchAdminUsers();
  fetchAuditLogs();
}
      }}
    >
      {showAdminDashboard ? "User Dashboard" : "Admin Dashboard"}
    </button>
  )}

  <button onClick={handleLogout}>Logout</button>
</div>
        </header>

        {/* Main Dashboard */}
        <main className="dashboard-content">
{/* Admin Dashboard */}
{showAdminDashboard && user.role === "ADMIN" && (
  <section className="admin-section">
    <div className="section-heading">
      <div>
        <h2>Admin Dashboard</h2>
        <p>Manage LedgerLine registered users.</p>
      </div>

      <button
        className="history-refresh-button"
        onClick={fetchAdminUsers}
        disabled={adminLoading}
      >
        {adminLoading ? "Loading..." : "↻ Refresh Users"}
      </button>
    </div>

    <div className="admin-stat-card">
      <h3>Total Registered Users</h3>
      <h1>{adminUsers.length}</h1>
    </div>

    {adminError && (
      <div className="error-message">{adminError}</div>
    )}

    {adminLoading ? (
      <p>Loading users...</p>
    ) : (
      <div className="admin-users-list">
        <h3>Registered Users</h3>

        <div className="admin-table-wrapper">
          <table className="admin-table">
  <thead>
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Email</th>
      <th>Role</th>
      <th>Status</th>
      <th>Registered</th>
      <th>Action</th>
    </tr>
  </thead>

  <tbody>
    {adminUsers.map((adminUser) => (
      <tr key={adminUser.id}>
        <td>{adminUser.id}</td>
        <td>{adminUser.fullName}</td>
        <td>{adminUser.email}</td>
        <td>{adminUser.role}</td>

        <td>
          <span className={
            adminUser.active ? "user-active" : "user-blocked"
          }>
            {adminUser.active ? "Active" : "Blocked"}
          </span>
        </td>

        <td>
          {new Date(adminUser.createdAt).toLocaleDateString("en-IN")}
        </td>

        <td>
          {adminUser.role === "USER" && (
            <button
              className={
                adminUser.active
                  ? "block-user-button"
                  : "unblock-user-button"
              }
              disabled={adminLoading}
              onClick={() =>
                updateUserStatus(adminUser.id, !adminUser.active)
              }
            >
              {adminUser.active ? "Block" : "Unblock"}
            </button>
          )}

          {adminUser.role === "ADMIN" && (
            <span>Admin account</span>
          )}
        </td>
      </tr>
    ))}
  </tbody>
</table>
        </div>
      </div>
    )}{/* Admin Audit Logs */}
<div className="audit-section">

  <div className="section-heading">
    <div>
      <h2>Audit Logs</h2>
      <p>Track all banking and administrative activities.</p>
    </div>

    <button
      className="history-refresh-button"
      onClick={fetchAuditLogs}
      disabled={auditLoading}
    >
      {auditLoading ? "Loading..." : "↻ Refresh Logs"}
    </button>
  </div>

  {auditError && (
    <div className="error-message">{auditError}</div>
  )}

  {auditLoading ? (
    <p>Loading audit logs...</p>
  ) : auditLogs.length === 0 ? (
    <p>No audit logs available.</p>
  ) : (
    <div className="admin-table-wrapper">
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Action</th>
            <th>User Email</th>
            <th>Details</th>
            <th>Status</th>
            <th>Date & Time</th>
          </tr>
        </thead>

        <tbody>
          {auditLogs.map((log) => (
            <tr key={log.id}>
              <td>{log.id}</td>
              <td>{log.action.replaceAll("_", " ")}</td>
              <td>{log.userEmail}</td>
              <td>{log.details}</td>
              <td>
                <span className="user-active">
                  {log.status}
                </span>
              </td>
              <td>
                {new Date(log.createdAt).toLocaleString("en-IN")}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )}

</div>
  </section>
)}
          {!showAdminDashboard && (
  <>
    {/* Welcome */}
    <section className="dashboard-welcome">
            <div>
              <span className="welcome-label">
                YOUR FINANCIAL OVERVIEW
              </span>

              <h1>Hello, {user.fullName}!</h1>

              <p>
                Here's an overview of your LedgerLine accounts.
              </p>
            </div>
          </section>

          {/* Accounts */}
          <section className="account-section">

            <div className="section-heading">
              <div>
                <h2>Your Accounts</h2>
                <p>View your available account balances.</p>
              </div>

              <button
                className="create-account-button"
                onClick={() => {
                  setShowCreateAccount(!showCreateAccount);
                  setShowDeposit(false);
                  setShowTransfer(false);
                  setSelectedHistoryAccount("");
                  setTransactions([]);
                  setAccountError("");
                  setAccountSuccess("");
                  setDepositError("");
                  setDepositSuccess("");
                }}
              >
                {showCreateAccount ? "Cancel" : "+ Create Account"}
              </button>
            </div>

            {/* Create account messages */}
            {accountSuccess && (
              <div className="success-message">
                {accountSuccess}
              </div>
            )}

            {accountError && (
              <div className="error-message">
                {accountError}
              </div>
            )}

            {/* Create account form */}
            {showCreateAccount && (
              <form
                className="create-account-form"
                onSubmit={handleCreateAccount}
              >
                <h3>Open a New Account</h3>

                <label>Account Holder Name</label>

                <input
                  type="text"
                  placeholder="Enter account holder name"
                  value={accountHolderName}
                  onChange={(e) =>
                    setAccountHolderName(e.target.value)
                  }
                  minLength={3}
                  maxLength={100}
                  required
                />

                <button
                  type="submit"
                  className="login-button"
                  disabled={creatingAccount}
                >
                  {creatingAccount ? "Creating..." : "Create Account"}
                </button>
              </form>
            )}

            {/* Deposit messages */}
            {depositSuccess && (
              <div className="success-message">
                {depositSuccess}
              </div>
            )}

            {depositError && (
              <div className="error-message">
                {depositError}
              </div>
            )}

            {/* Deposit form */}
            {showDeposit && (
              <form
                className="create-account-form"
                onSubmit={handleDeposit}
              >
                <h3>Deposit Money</h3>

                <label>Selected Account</label>

                <input
                  type="text"
                  value={depositAccount}
                  readOnly
                />

                <label>Deposit Amount (₹)</label>

                <input
                  type="number"
                  placeholder="Enter deposit amount"
                  value={depositAmount}
                  onChange={(e) =>
                    setDepositAmount(e.target.value)
                  }
                  min="0.01"
                  max="1000000000"
                  step="0.01"
                  required
                />

                <button
                  type="submit"
                  className="login-button"
                  disabled={depositLoading}
                >
                  {depositLoading
                    ? "Processing..."
                    : "Confirm Deposit"}
                </button>

                <button
                  type="button"
                  className="cancel-button"
                  onClick={() => {
                    setShowDeposit(false);
                    setDepositAmount("");
                    setDepositError("");
                  }}
                  disabled={depositLoading}
                >
                  Cancel
                </button>
              </form>
            )}


            {/* Transfer messages */}
            {transferSuccess && (
              <div className="success-message">{transferSuccess}</div>
            )}

            {transferError && (
              <div className="error-message">{transferError}</div>
            )}

            {/* Transfer form */}
            {showTransfer && (
              <form
                className="create-account-form"
                onSubmit={handleTransfer}
              >
                <h3>Transfer Money</h3>

                <label>From Account</label>
                <input type="text" value={senderAccount} readOnly />

                <label>Receiver Account Number</label>
                <input
                  type="text"
                  placeholder="Enter receiver's account number"
                  value={receiverAccount}
                  onChange={(e) => setReceiverAccount(e.target.value)}
                  maxLength={30}
                  required
                />

                <label>Transfer Amount (₹)</label>
                <input
                  type="number"
                  placeholder="Enter transfer amount"
                  value={transferAmount}
                  onChange={(e) => setTransferAmount(e.target.value)}
                  min="0.01"
                  max="1000000000"
                  step="0.01"
                  required
                />

                <button
                  type="submit"
                  className="login-button"
                  disabled={transferLoading}
                >
                  {transferLoading ? "Processing..." : "Confirm Transfer"}
                </button>

                <button
                  type="button"
                  className="cancel-button"
                  onClick={() => {
                    setShowTransfer(false);
                    setReceiverAccount("");
                    setTransferAmount("");
                    setTransferError("");
                  }}
                  disabled={transferLoading}
                >
                  Cancel
                </button>
              </form>
            )}

            {/* Loading */}
            {dashboardLoading && (
              <p className="dashboard-message">
                Loading your accounts...
              </p>
            )}

            {/* Dashboard errors */}
            {dashboardError && (
              <div className="error-message">
                {dashboardError}
              </div>
            )}

            {/* Empty accounts */}
            {!dashboardLoading &&
              !dashboardError &&
              accounts.length === 0 && (
                <div className="empty-accounts">
                  <h3>No accounts found</h3>
                  <p>
                    Create your first LedgerLine account to get started.
                  </p>
                </div>
              )}

            {/* Account cards */}
            {!dashboardLoading && accounts.length > 0 && (
              <div className="account-grid">

                {accounts.map((account) => (
                  <div
                    className="account-card"
                    key={account.id}
                  >
                    <div className="account-card-top">
                      <span>LedgerLine Account</span>
                      <span className="account-symbol">₹</span>
                    </div>

                    <h3>{account.holderName}</h3>

                    <p className="account-number">
                      {account.accountNumber}
                    </p>

                    <div className="account-balance">
                      <span>Available Balance</span>

                      <h2>
                        {account.currency === "INR"
                          ? "₹"
                          : `${account.currency} `}

                        {Number(account.balance).toLocaleString(
                          "en-IN",
                          {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2,
                          }
                        )}
                      </h2>
                    </div>

                    {/* Deposit button */}
                    <button
                      className="deposit-button"
                      onClick={() => {
                        setDepositAccount(account.accountNumber);
                        setShowDeposit(true);
                        setShowCreateAccount(false);
                        setDepositAmount("");
                        setDepositError("");
                        setDepositSuccess("");
                        setAccountError("");
                        setAccountSuccess("");
                      }}
                    >
                      + Deposit Money
                    </button>

                    {/* Transfer button */}
                    <button
                      className="transfer-button"
                      onClick={() => {
                        setSenderAccount(account.accountNumber);
                        setShowTransfer(true);
                        setShowDeposit(false);
                        setShowCreateAccount(false);
                        setReceiverAccount("");
                        setTransferAmount("");
                        setTransferError("");
                        setTransferSuccess("");
                        setDepositError("");
                        setDepositSuccess("");
                        setAccountError("");
                        setAccountSuccess("");
                      }}
                    >
                      ↗ Transfer Money
                    </button>

                    {/* Transaction History button */}
                    <button
                      className="history-button"
                      onClick={() => {
                        setShowCreateAccount(false);
                        setShowDeposit(false);
                        setShowTransfer(false);
                        setHistoryError("");
                        fetchTransactions(account.accountNumber);
                      }}
                    >
                      ↗ View Transaction History
                    </button>

                  </div>
                ))}

              </div>
            )}

          </section>
          
            {/* Transaction History Section */}
            {selectedHistoryAccount && (
              <section className="transaction-section">
                <div className="transaction-heading">
                  <div>
                    <h2>Transaction History</h2>
                    <p>
                      Account: {selectedHistoryAccount}
                    </p>
                  </div>

                  <button
                    className="history-refresh-button"
                    onClick={() =>
                      fetchTransactions(selectedHistoryAccount)
                    }
                    disabled={historyLoading}
                  >
                    {historyLoading ? "Refreshing..." : "↻ Refresh"}
                  </button>
                </div>

                {historyLoading && (
                  <p className="dashboard-message">
                    Loading transactions...
                  </p>
                )}

                {historyError && (
                  <div className="error-message">
                    {historyError}
                  </div>
                )}

                {!historyLoading &&
                  !historyError &&
                  transactions.length === 0 && (
                    <div className="empty-accounts">
                      <h3>No transactions found</h3>
                      <p>
                        Transactions for this account will appear here.
                      </p>
                    </div>
                  )}

                {!historyLoading &&
                  !historyError &&
                  transactions.length > 0 && (
                    <div className="transaction-list">
                      {transactions.map((transaction) => (
                        <div
                          className="transaction-card"
                          key={transaction.transactionId}
                        >
                          <div className="transaction-info">
                            <div
                              className={`transaction-icon ${
                                transaction.direction === "CREDIT"
                                  ? "credit"
                                  : "debit"
                              }`}
                            >
                              {transaction.direction === "CREDIT"
                                ? "↓"
                                : "↑"}
                            </div>

                            <div>
                              <h3>{transaction.transactionType}</h3>
                              <p>{transaction.description}</p>
                              <span>
                                {new Date(
                                  transaction.createdAt
                                ).toLocaleString("en-IN", {
                                  dateStyle: "medium",
                                  timeStyle: "short",
                                })}
                              </span>
                            </div>
                          </div>

                          <div className="transaction-amount">
                            <h3
                              className={
                                transaction.direction === "CREDIT"
                                  ? "credit-text"
                                  : "debit-text"
                              }
                            >
                              {transaction.direction === "CREDIT"
                                ? "+"
                                : "-"}
                              {transaction.currency === "INR"
                                ? "₹"
                                : `${transaction.currency} `}
                              {Number(transaction.amount).toLocaleString(
                                "en-IN",
                                {
                                  minimumFractionDigits: 2,
                                  maximumFractionDigits: 2,
                                }
                              )}
                            </h3>

                            <span className="transaction-status">
                              {transaction.status}
                            </span>
                            {transaction.transactionType === "TRANSFER" && (
  <button
    type="button"
    className="verify-button"
    onClick={() => verifyTransfer(transaction.transactionId)}
    disabled={verificationLoading}
  >
    {verificationLoading ? "Verifying..." : "✓ Verify Ledger"}
  </button>
)}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}{/* Ledger Verification Result */}
{verificationLoading && (
  <p className="dashboard-message">
    Verifying ledger entries...
  </p>
)}

{verificationError && (
  <div className="error-message">
    {verificationError}
  </div>
)}

{verification && (
  <div className={`verification-result ${
    verification.balanced ? "verified" : "unbalanced"
  }`}>
    <h3>
      {verification.balanced
        ? "✓ Ledger Successfully Verified"
        : "✕ Ledger Verification Failed"}
    </h3>

    <p>{verification.message}</p>

    <div className="verification-details">
      <p>
        <strong>Transfer ID:</strong> {verification.transferId}
      </p>
      <p>
        <strong>Entry Count:</strong> {verification.entryCount}
      </p>
      <p>
        <strong>Debit Total:</strong> ₹
        {Number(verification.debitTotal).toFixed(2)}
      </p>
      <p>
        <strong>Credit Total:</strong> ₹
        {Number(verification.creditTotal).toFixed(2)}
      </p>
      <p>
        <strong>Ledger Status:</strong>{" "}
        {verification.balanced ? "BALANCED" : "NOT BALANCED"}
      </p>
    </div>
  </div>
)}
              </section>
            )}

          {/* Profile Information */}
          <section className="profile-section">
            <h2>Profile Information</h2>

            <div className="profile-details">
              <p>
                <strong>Name:</strong> {user.fullName}
              </p>

              <p>
                <strong>Email:</strong> {user.email}
              </p>

              <p>
                <strong>Role:</strong> {user.role}
              </p>
            </div>
          </section>
          </>
          )}

        </main>

        <footer className="dashboard-footer">
          © 2026 LedgerLine. Secure money movement.
        </footer>

      </div>
    );
  }

  // Login and Registration screen
  return (
    <div className="login-page">
      <div className="login-card">

        <div className="brand">
          <div className="brand-icon">L</div>
          <span>LedgerLine</span>
        </div>

        <div className="welcome-section">
          <span className="welcome-label">
            {isRegister ? "GET STARTED" : "WELCOME BACK"}
          </span>

          <h1>
            {isRegister
              ? "Create your account."
              : <>Banking made<br />simple and secure.</>}
          </h1>

          <p>
            {isRegister
              ? "Join LedgerLine and manage your money securely."
              : "Sign in to manage your accounts, move money and track every transaction with confidence."}
          </p>
        </div>

        <form onSubmit={handleSubmit}>

          {isRegister && (
            <>
              <div className="form-group">
                <label>Full name</label>

                <input
                  type="text"
                  placeholder="Enter your full name"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  minLength={3}
                  maxLength={100}
                  required
                />
              </div>

              <div className="form-group">
                <label>Phone number</label>

                <input
                  type="tel"
                  placeholder="10-digit mobile number"
                  value={phoneNumber}
                  onChange={(e) =>
                    setPhoneNumber(
                      e.target.value.replace(/\D/g, "").slice(0, 10)
                    )
                  }
                  pattern="[0-9]{10}"
                  maxLength={10}
                  required
                />
              </div>
            </>
          )}

          <div className="form-group">
            <label>Email address</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>

            <div className="password-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                minLength={isRegister ? 8 : undefined}
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={
                  showPassword ? "Hide password" : "Show password"
                }
              >
                {showPassword ? "🙈" : "👁"}
              </button>
            </div>
          </div>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          {success && (
            <div className="success-message">
              {success}
            </div>
          )}

          <button
            type="submit"
            className="login-button"
            disabled={loading}
          >
            {loading
              ? "Please wait..."
              : isRegister
                ? "Create Account"
                : "Sign In"}

            {!loading && <span>→</span>}
          </button>

        </form>

        <div className="auth-switch">
          {isRegister
            ? "Already have an account?"
            : "Don't have an account?"}

          <button
            type="button"
            onClick={() => {
              setIsRegister(!isRegister);
              setError("");
              setSuccess("");
              setPassword("");
              setShowPassword(false);
            }}
          >
            {isRegister ? "Sign In" : "Create Account"}
          </button>
        </div>

        <div className="security-note">
          <span>🔒</span>
          Your financial information is protected.
        </div>

        <footer>
          © 2026 LedgerLine. Secure money movement.
        </footer>

      </div>
    </div>
  );
}

export default App;
