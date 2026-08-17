# Pen Manufacturing Management System (PMMS)

Course: EES4317 - Object-Oriented Design and Programming  
Task: Tutor-Marked Assignment (TMA)  

---

## 📌 Project Overview
This is a Java application built for the EES4317 TMA project. The system manages the basic operations of a pen manufacturing plant, such as tracking raw materials, scheduling production batches, quality checking, sales order processing, and generating system reports.

---

## 🚀 Key Features

- **Raw Material Management:** Track and update stock levels for raw materials.
- **Production Scheduling:** Create production batches and check material availability.
- **Quality Control & Inventory:** Conduct quality inspections and update finished product inventory.
- **Sales & Invoices:** Process customer orders and generate invoices.
- **Reports:** Generate system activity reports using Java threads.

---

## 🛠️ OOP Concepts & Design Patterns Used

This project applies core Object-Oriented Programming (OOP) principles:

* **Encapsulation:** Used private attributes with getters and setters in model classes (`Pen`, `Batch`, `Inventory`, `Order`).
* **Inheritance & Polymorphism:** Extended classes for different pen types and dynamic behavior.
* **Abstraction:** Separated business logic, data models, and user interfaces (`PMMSInteractiveCLI`, `PMMSDashboard`).
* **Design Patterns:** Used software design patterns (like Singleton and Factory Method) for clean code organization.
* **Multithreading:** Used background threads for running report tasks without freezing the app.

---

## 📁 Repository Structure

```text
pmms-tma-project/
│
├── exceptions/             # Custom exception classes
├── model/                  # Data models (Pen, Batch, Inventory, etc.)
├── patterns/               # Design pattern implementations
├── services/               # Main business logic
│
├── PMMSApplication.java    # Main entry point
├── PMMSDashboard.java      # Dashboard GUI
├── PMMSInteractiveCLI.java # Command line interface
└── pmms_production.txt     # Log file
