# Expense Tracker Application

A secure and well-structured **Expense & Income Management System** built using **Spring Boot MVC**, **HTML,CSS & JS**, and **MySQL**.  
This application helps users manage daily expenses and income while maintaining a clear view of their financial balance.

---

## Overview

The Expense Tracker application is designed to provide users with a simple yet powerful way to record, categorize, and analyze financial transactions.  
It follows the MVC architecture and implements best practices in backend development, security, and database management.

---

## Features

### Authentication & Security
- User registration and login
- Session-based authentication
- Password encryption using BCrypt
- CSRF protection
- Google OAuth login support (work in progress)

### Expense Management
- Add, update, and delete expenses
- Assign expenses to categories
- Record date and time for each transaction
- User-specific expense data isolation

### Income Management
- Add income entries
- Automatic balance calculation  
  **Remaining Balance = Total Income − Total Expenses**

### Category Management
- Predefined categories stored in the database
- Dynamic category dropdown population
- Category-based expense filtering

### Filtering & Viewing
- Filter transactions by category
- View all transactions for the logged-in user
- Clean and readable UI using Bootstrap

---

## Technology Stack

**Backend**
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security

**Frontend**
- Thymeleaf
- HTML5
- CSS3
- Bootstrap

**Database**
- MySQL

**Tools**
- IntelliJ IDEA
- Maven
- Git & GitHub


## Project Structure

