# Online Examination System (Java Swing)

Oasis Infobyte SIP - Java Development - Task 4

## Features
- **Login** (username + password, passwords stored as SHA-256 hashes)
- **Profile update** - change display name and password before starting
- **Exam screen** - one MCQ at a time, 4 radio options, Next / Previous, numbered question buttons (green = answered)
- **Countdown timer** always visible, turns red in the last minute, **auto-submits at zero**
- **Manual submit** with confirmation dialog (shows answered / unanswered count)
- **Result screen** - score X out of Y, time taken, correct / incorrect / unanswered, per-question breakdown table
- **Session management** - closing the window during an exam asks "Are you sure you want to quit?"
- **Logout** on the result screen returns to login
- Questions are shuffled on every attempt

## Classes
`ExamApp` (main window, CardLayout) - `LoginPanel` - `ProfilePanel` - `ExamPanel` - `ResultPanel` - `User` - `UserStore` - `Question` - `QuestionBank` - `ExamResult`

## Run
```
cd src
javac *.java
java ExamApp
```
Exam length defaults to 10 minutes. To test auto-submit quickly: `java -Dexam.seconds=20 ExamApp`

## Demo accounts
`student1 / pass123` and `student2 / pass456`

## Screenshots
See the `screenshots/` folder.
