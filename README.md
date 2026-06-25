# Mobile Expense Management App

A full-stack Android application built in Java that simplifies corporate expense tracking, receipt attachment, and tier-based organizational approval workflows using Firebase.

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
