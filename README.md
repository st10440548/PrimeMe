# PrimeMe

PrimeMe is a native Android self-improvement application designed to help users organise their daily focus, goals and personal development.

## Features

- Google Sign-In authentication using Firebase
- Home dashboard with Prime Score and Today's Focus
- Goal management
- Cloud-based goal retrieval and creation
- Settings and user preferences
- Input validation
- Native Android UI using Jetpack Compose
- REST API integration
- Azure PostgreSQL data storage
- Automated testing and GitHub Actions CI

## Technology Stack

### Android
- Kotlin
- Jetpack Compose
- Android SDK
- Firebase Authentication
- Google Sign-In / Credential Manager
- Retrofit
- Gson

### Backend
- ASP.NET Core Web API
- C#
- RESTful API
- PostgreSQL

### Cloud
- Microsoft Azure App Service
- Azure Database for PostgreSQL Flexible Server

### Development and Version Control
- Git
- GitHub
- GitHub Actions

## Application Architecture

PrimeMe follows a layered architecture:

```text
Android UI (Jetpack Compose)
        ↓
ViewModel / Presentation
        ↓
Repository / Data Layer
        ↓
Retrofit REST API Client
        ↓
ASP.NET Core REST API
        ↓
Azure PostgreSQL Database
