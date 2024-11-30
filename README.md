# 🏃‍♂️ twinCoach - Digital Twin Dashboard for Long-Distance Runners

Welcome to the **twinCoach**, an Android application built using modern development principles to empower athletes by predicting their performance in long-distance running races. This project integrates **Health Connect** to analyze training data and provide personalized recommendations for optimized performance.

---

## 🚀 Features

- **Health Connect Integration**: Access real-time training session data seamlessly.
- **Performance Prediction**: Get insights into your long-distance race performance based on current training patterns.
- **Goal-Oriented Training**: Input your race goals and receive personalized training session suggestions.
- **Interactive Dashboard**: View processed performance analytics and progress towards goals.

---

## 🛠️ Tech Stack

This project is developed using:

- **Android (Kotlin)**: The primary framework for the application.
- **MVVM Architecture**: Ensures separation of concerns and easier maintainability.
- **Retrofit**: Simplifies API communication for data retrieval and submission.
- **Clean Architecture**: Provides a scalable and testable code structure.
- **Health Connect**: Enables seamless access to health and fitness data.

---

## 📖 How It Works

1. **Data Collection**:  
   The app retrieves training session data (e.g., running distance, pace, heart rate) via **Health Connect**.

2. **Data Processing**:  
   The collected data is sent to a backend server that uses advanced algorithms to analyze performance and predict race outcomes.

3. **Insights and Recommendations**:  
   Processed results are displayed in the app, along with training session suggestions tailored to the athlete's goals and current progress.

---

## 🎯 Use Case

The **Digital Twin Dashboard** is ideal for:

- Athletes preparing for long-distance races.
- Coaches and trainers looking to optimize their athlete’s training regimen.
- Fitness enthusiasts tracking their progress and setting ambitious goals.

---

## 📸 Screenshots

![Home Screen](https://private-user-images.githubusercontent.com/100614773/335877038-c738dd9c-db07-4ed2-a023-2ae7de542874.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzI5NjA3MjIsIm5iZiI6MTczMjk2MDQyMiwicGF0aCI6Ii8xMDA2MTQ3NzMvMzM1ODc3MDM4LWM3MzhkZDljLWRiMDctNGVkMi1hMDIzLTJhZTdkZTU0Mjg3NC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTMwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTEzMFQwOTUzNDJaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0zZmFlMmE0ZDZiMTY2Y2Q5MTU1Yjg3MWRlMThjZDMyNDUyNDlkZmY5OGUzYzc0MmM0OGU1MzNmYmQ1YjFiZmVmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.r8Igs84TxH8UDUWwuxYnj0RAh95mjKkVN-81b7FKFuk)
![Sessions Screen](https://private-user-images.githubusercontent.com/100614773/331692870-3fb7aec6-9db0-4191-a791-6a6413269ca7.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzI5NjA4NTEsIm5iZiI6MTczMjk2MDU1MSwicGF0aCI6Ii8xMDA2MTQ3NzMvMzMxNjkyODcwLTNmYjdhZWM2LTlkYjAtNDE5MS1hNzkxLTZhNjQxMzI2OWNhNy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTMwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTEzMFQwOTU1NTFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT01ZTljZGUwZWQ5ZGU2ZjA1ZTlmMDQwOGZlYmI5ZDIzMTQyOTBkNWVhNjJlZmFmMjEyMzVlMWVjY2RiNmM3MGFhJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.sfO3uHQS6cMtkWO7LF9FbOqQI1HwfwyVzfQuFBfycSc)
![Sessions Screen](https://private-user-images.githubusercontent.com/100614773/331692868-c28a168d-24aa-4fe6-b293-fad634cf4dbd.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzI5NjA4NTEsIm5iZiI6MTczMjk2MDU1MSwicGF0aCI6Ii8xMDA2MTQ3NzMvMzMxNjkyODY4LWMyOGExNjhkLTI0YWEtNGZlNi1iMjkzLWZhZDYzNGNmNGRiZC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTMwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTEzMFQwOTU1NTFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT00N2U5NTczZWU4NDgwOTY5MGM4ZDM4MjUzNTkwYWFkNDFjN2E1YmVmYzE5ZGI4ODFlOTRkZjhkYzhkOGIwNWI1JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.jraVgRFNhkGpPXt-6tidXnNlhzrL-CSk2lPBMvddn-M)
![Sessions Screen](https://private-user-images.githubusercontent.com/100614773/331692864-27533323-7bfc-421f-b764-602b66699a76.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzI5NjA4NTEsIm5iZiI6MTczMjk2MDU1MSwicGF0aCI6Ii8xMDA2MTQ3NzMvMzMxNjkyODY0LTI3NTMzMzIzLTdiZmMtNDIxZi1iNzY0LTYwMmI2NjY5OWE3Ni5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTMwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTEzMFQwOTU1NTFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1mMzZlMzBiOTUzYTBiMzg2ZWRlYjM4YzljYjcwY2Y1M2I0OGMxMTQzYWVlNDE1ZmYzOTI1N2Q2ZWUxZjMxMzRmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.j1Jnt98ahOcCJjt0uiyphB57t2VZVpJD9Dk0DXSmtMY)
![Sessions Screen](https://private-user-images.githubusercontent.com/100614773/331692858-3a423333-59a7-48e2-9658-e9ee1df1c738.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzI5NjA4NTEsIm5iZiI6MTczMjk2MDU1MSwicGF0aCI6Ii8xMDA2MTQ3NzMvMzMxNjkyODU4LTNhNDIzMzMzLTU5YTctNDhlMi05NjU4LWU5ZWUxZGYxYzczOC5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTMwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTEzMFQwOTU1NTFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02MTBlNjUxOTZmNzkxNjRjNjZjYmQyY2U0MDI3NjMwODE4YmZmOGYxNDNiMzMzNWVkNGIxYTk3ODY2MDU2NWVlJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.Ptltkd00Mj7NGW4syhwHhkZAOlQS4_FXBeVbDNrDDHA)
![Progression Screen](https://private-user-images.githubusercontent.com/100614773/331874278-5ece99b4-7e98-4abb-91a7-2b81e4521db1.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzI5NjA5NzUsIm5iZiI6MTczMjk2MDY3NSwicGF0aCI6Ii8xMDA2MTQ3NzMvMzMxODc0Mjc4LTVlY2U5OWI0LTdlOTgtNGFiYi05MWE3LTJiODFlNDUyMWRiMS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTMwJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTEzMFQwOTU3NTVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1lNDliODAwNjhhMmJhZjRjMmNlYjU0YjNmM2FlOTY4ZDI0YjUwNDVmZTEwOWJkNGNiZTkwOTZiYmYwY2VlOTU3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.dCbmUNRDmjuavSaJtDkKq5Fcn5b_aJ99mU5TxA6BU7w)
