# Pen Manufacturing Management System (PMMS)

> **Course:** EES4317 - Object-Oriented Design and Programming  
> **Task:** Tutor-Marked Assignment (TMA)  

---

## 📌 Project Overview
The **Pen Manufacturing Management System (PMMS)** is a Java-based desktop application developed to model and streamline core operational workflows of a pen manufacturing plant. The system automates raw material handling, batch scheduling, quality control, sales processing, inventory management, and system report generation.

---

## 🚀 Key Features & Modules

- **Raw Material Management:** Track and update essential raw materials required for manufacturing.
- **Production Batch Scheduling:** Schedule batch processes with automated material availability verification (`Verify material availability`).
- **Quality Inspection & Inventory:** Perform quality assurance inspections (`Conduct Quality Inspection`) and update the finished goods inventory (`Update Finished Goods Inventory`).
- **Sales Order & Invoicing:** Process customer orders and auto-generate transaction invoices (`Generate invoice`).
- **System Reporting:** Generate asynchronous system performance and status reports using Java Multithreading.

---

## 🛠️ Object-Oriented Architecture & Design Patterns

This application is built adhering to strict **Object-Oriented Programming (OOP)** principles and software patterns:

* **Encapsulation:** All model properties (`Pen`, `Batch`, `Inventory`, `Order`) are safely encapsulated with appropriate access modifiers.
* **Inheritance & Polymorphism:** Extended class structures for diverse pen models and dynamic behavioral execution.
* **Abstraction:** Clean layer separation between business logic services, data models, and presentation interfaces (`PMMSInteractiveCLI`, `PMMSDashboard`).
* **Design Patterns:** Implemented core software patterns (e.g., Singleton, Factory Method) to ensure high cohesion and low coupling.
* **Multithreading:** Background execution threads for non-blocking report generation and system updates.

---

## 📁 Repository Structure

```text
pmms-tma-project/
│
├── exceptions/             # Custom application exception handling
├── model/                  # Domain Entities & Data Models
├── patterns/               # Implemented Software Design Patterns
├── services/               # Core Business Logic & Workflow Managers
│
├── PMMSApplication.java    # Application Entry Point
├── PMMSDashboard.java      # Dashboard GUI / Main Runner
├── PMMSInteractiveCLI.java # Interactive Command-Line Interface
└── pmms_production.txt     # System Transaction & Production Log
