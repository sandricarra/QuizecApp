# 📚 QuizecApp - Classroom Quiz Application  

QuizecApp is an Android application designed for creating and managing classroom quizzes. Teachers and students can create, share, and participate in quizzes with various question types while ensuring a secure and interactive experience.  

## 🚀 Features  

- **Quiz Creation & Management**  
  - Users can create and share quizzes using a **unique 6-character identifier**.  
  - Each quiz can include a **description and an optional image**.  
  - Questions can be **reused across different quizzes**.  

- **Question Types**  
  - ✅ **Yes/No, True/False**  
  - 🔘 **Multiple Choice (Single Answer)**  
  - ✅✅ **Multiple Choice (Multiple Answers)**  
  - 🔗 **Matching Items**  
  - 📊 **Ordering (Chronological, Hierarchical, etc.)**  
  - ✏️ **Fill-in-the-Blank (Predefined Options)**  
  - 🖼️ **Image or Concept Association**  
  - ⬜ **Fill-in-the-Missing-Words (Free Input)**  
  - 📊 Results displayed as **bar charts, pie charts, or sorted lists**.  

- **Quiz Execution & Controls**  
  - **Controlled Start**: The creator decides when students access the quiz.  
  - **Geolocation Restriction**: Option to limit participation to users near the creator’s location.  
  - **Timed Sessions**: The creator can set and manually stop the quiz timer.  
  - **Real-Time & Delayed Results**:  
    - Students can see results immediately or after the quiz ends.  
    - Creators have **real-time access** to incoming answers.  

- **User Authentication & Data Privacy**  
  - 🔒 **Users must authenticate** to prevent multiple responses.  
  - ✅ **Aggregated & anonymous results** – no personal information is shared.  

- **History & Data Management**  
  - Users can **view their quiz history** and duplicate previous questions.  
  - Creators can **edit, duplicate, or delete quizzes** before sharing them.  

## 🛠️ Technologies Used  

- **Jetpack Compose** – Modern UI framework  
- **Firebase** – Firestore/Storage for quizzes & user data  
- **User Authentication** – Secure login & session management  
- **Multi-language Support** – Default language: **English**, with Portuguese support  
- **Adaptive UI** – Works in **Portrait & Landscape** orientations  

---
