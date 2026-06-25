# Mobile Expense Management App

A full-stack Android application built in Java that simplifies corporate expense tracking, receipt attachment, and tier-based organizational approval workflows using Firebase.

---

## 🔑 Mock Credentials for Testing
Use these pre-configured accounts to log into the application and test the varying structural role interfaces and workflow tracking gates:

| Role | Email Address | Password | Permissions / Actions |
| :--- | :--- | :--- | :--- |
| **Employee** | `emp@company.com` | `123456` | Can file new claims, pick gallery receipts, view personal claim statuses. |
| **Manager** | `manager@company.com` | `123456` | Can see pending employee claims, issue Manager Approvals/Rejections. |
| **Finance** | `finance@company.com` | `123456` | Reviews manager-approved entries, issues final payouts to update global reports. |

---

## 🚀 Features
- **Role-Based Authentication:** Clean separation of views for Employees, Managers, and Finance teams.
- **Digital Expense Logging:** Employees can upload expense amounts, dates, descriptive metadata, and attach receipt images from their gallery.
- **Stateful Approval Pipelines:** Expense documents move statefully through successive approval gates (`Pending` → `Approved` → `Paid`).
- **Real-time Analytical Dashboard:** Financial reporting module dynamically calculates global aggregate outlays for paid claims.

## 🛠️ Tech Stack & Architecture
- **IDE:** Android Studio (Jellyfish/Modern views layout)
- **Language:** Java 8+ / XML
- **Backend Infrastructure:** Firebase Authentication & Cloud Firestore (NoSQL)

## 📁 Database Schema (Firestore)
- **`users` Collection:**
  - `uid` (Document ID): String
  - `role`: String ("employee" | "manager" | "finance")

- **`expenses` Collection:**
  - `expenseId` (Document ID): String
  - `employeeUid`: String
  - `employeeName`: String
  - `amount`: Double
  - `date`: String
  - `category`: String
  - `description`: String
  - `receiptUrl`: String
  - `status`: String ("Pending" | "Approved" | "Rejected" | "Paid")
  - `currentApprover`: String ("manager" | "finance" | "completed")
