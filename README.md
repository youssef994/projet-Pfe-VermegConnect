# VermegConnect

<div align="center">
  <img src="https://github.com/user-attachments/assets/e4eee374-340e-4a9e-ab3a-6fe946480e3a" alt="VermegConnect Logo" style="width: 300px; height: auto; margin-bottom: 20px;" />
</div>

<div align="center">
  <h2>VermegConnect</h2>
  <p style="font-size: 16px; color: #555; max-width: 800px;">VermegConnect is a Q&A platform developed to help Vermeg employees share knowledge, solve problems, and collaborate more effectively across departments. The platform allows users to ask questions, provide answers, upvote/downvote responses, and validate the best answers, all while keeping track of activity and providing real-time notifications.</p>
</div>

## Features
<div style="margin-bottom: 20px;">
  <ul style="font-size: 16px; line-height: 1.8; color: #333;">
    <li><strong>User Authentication:</strong> JWT-based authentication for secure access.</li>
    <li><strong>Profile Management:</strong> Users can manage their profiles with detailed information.</li>
    <li><strong>Question & Answer System:</strong> Users can post questions, provide answers, and interact through upvotes/downvotes.</li>
    <li><strong>Real-Time Notifications:</strong> Notifications are sent when a post receives a new answer or when there’s an upvote/downvote.</li>
    <li><strong>Admin Dashboard:</strong> Analytics and user activity can be monitored by admins.</li>
    <li><strong>Blog Module:</strong> Users can access articles related to problem-solving or common questions.</li>
    <li><strong>Microservices Architecture:</strong> Separated services for notifications, analytics, and user management.</li>
    <li><strong>Responsive Design:</strong> Accessible on both desktop and mobile devices.</li>
  </ul>
</div>

## Architecture
<div align="center">
  <img src="https://github.com/user-attachments/assets/39953e1f-5f9f-4b33-a6e6-f63b46aa04d1" alt="Architecture Diagram" style="max-width: 80%; height: auto; margin-bottom: 40px;" />
</div>

## Technologies Used
<div style="font-size: 16px; color: #333; margin-bottom: 30px;">
  <ul>
    <li><strong>Frontend:</strong> Angular</li>
    <li><strong>Backend:</strong> Spring Boot</li>
    <li><strong>Database:</strong> PostgreSQL</li>
    <li><strong>Authentication:</strong> JWT (JSON Web Tokens)</li>
    <li><strong>Real-Time Communication:</strong> WebSockets</li>
    <li><strong>Charting Library:</strong> ng2-charts, chart.js</li>
    <li><strong>Notification Service:</strong> WebSocket-based notifications</li>
  </ul>
</div>

## Screenshots

Below are some screenshots of the platform:

### Authentication

<div style="display: flex; gap: 20px; justify-content: center;">
  <img src="https://github.com/user-attachments/assets/547f786b-f4aa-4c6d-bb43-6d9f9c22166d" alt="Login Page" style="width: 48%; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);" />
  <img src="https://github.com/user-attachments/assets/e94fae4a-8b28-4160-bfb6-d9e734860497" alt="Register Page" style="width: 48%; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);" />
</div>

### Homepage

<div style="display: flex; gap: 20px; justify-content: center; margin-top: 20px;">
  <img src="https://github.com/user-attachments/assets/5335e211-3099-4472-9c22-c85b8bbeb19d" alt="Homepage Screenshot" style="width: 48%; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);" />
</div>

### Question Posting

<div style="display: flex; gap: 20px; justify-content: center; margin-top: 20px;">
  <img src="https://github.com/user-attachments/assets/85aeb9d6-eac8-4c9e-a9e5-e2933ffb3046" alt="Post Creation" style="width: 48%; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);" />
  <img src="https://github.com/user-attachments/assets/aa69ff8f-75aa-46a9-9c55-3e566422e635" alt="Post Details" style="width: 48%; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);" />
</div>

### Admin Dashboard

<div style="display: flex; gap: 20px; justify-content: center; margin-top: 20px;">
  <img src="https://github.com/user-attachments/assets/d5d3fba0-c853-451a-923c-a71c015997e9" alt="Admin Dashboard" style="width: 48%; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);" />
</div>

---

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login user and retrieve JWT.
- `POST /api/auth/register` - Register a new user.

### Questions & Answers
- `GET /api/questions` - Get all questions.
- `POST /api/questions` - Post a new question.
- `GET /api/questions/{id}` - Get a specific question and its answers.
- `POST /api/answers` - Post an answer to a question.
- `PUT /api/answers/{id}/upvote` - Upvote an answer.
- `PUT /api/answers/{id}/downvote` - Downvote an answer.

### User Management
- `GET /api/users/{id}` - Get user details.
- `PUT /api/users/{id}` - Update user profile.

### Notifications
- `GET /api/notifications` - Get all notifications for the logged-in user.

### Analytics
- `GET /api/analytics/platform-stats` - Get platform statistics.
- `GET /api/analytics/user-stats/{id}` - Get user-specific statistics.
