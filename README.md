# Smart Task and Resource Planner

A lightweight full-stack task management dashboard designed to plan, track, and optimize task schedules. The system features custom-built backend data structures and manual optimization algorithms, minimizing reliance on native library utilities to showcase core software engineering principles.

## 🚀 Tech Stack
- **Frontend:** HTML5, CSS3, JavaScript (Clean, responsive layout with native Fetch API)
- **Backend:** Java, Spring Boot (REST API Architecture)
- **Data Architecture:** Standard collections integrated with customized structural pointer links.

---

## 💡 Core Architecture & Manual Algorithms

This project focuses on the manual implementation of classic computer science concepts rather than using boilerplate convenience classes:

### 1. Manual Bubble Sort Algorithm (`TaskService.java`)
- **Purpose:** Organizes tasks by priority sequence.
- **Implementation:** Built using a manual nested loop swapping architecture to perform sorting in-place on the task collections. This eliminates the reliance on standard automated library helpers, providing explicit data ordering mechanics.

### 2. Custom Stack via Linked List for Undo Operation (`Node.java`, `TaskHistoryList.java`)
- **Purpose:** Tracks task history sequentially to allow users to reverse creation steps dynamically.
- **Implementation:** Designed using a custom Single Linked List architecture (`Node` pointers) running under a functional **LIFO (Last In, First Out)** paradigm. When an element is unlinked, the data references are cleared, relying cleanly on the Java Virtual Machine's Garbage Collector for predictable memory cleanup.

### 3. Frontend Finite State Machine Layout (`index.html`)
- **Purpose:** Regulates allowed workflows for active items to prevent invalid state configurations.
- **Implementation:** Evaluates states in runtime:
    - `Pending` ➡️ Displays a **Start Working** button.
    - `Working` ➡️ Displays a **Mark Completed** button.
    - `Completed` ➡️ Locks the state down visibly showing **✔️ Done** with no remaining button references.

---

## 🛠️ Setup & Running Instructions

### Prerequisites
- Java Development Kit (JDK 17 or higher)
- Apache Maven
- Any modern web browser

### Backend System Setup
1. Open the backend source project inside your chosen IDE (e.g., IntelliJ IDEA).
2. Allow Maven dependencies to resolve completely.
3. Run the primary application entry point (`BackendsystemApplication.java`). The server boots locally on `http://localhost:8080`.

### Frontend System Setup
1. Locate the frontend layout workspace folder.
2. Open the `index.html` file directly in any browser window.
3. Use the integrated dashboard forms to dynamically add, sort, transition, and undo task items in real-time.