# PocketTrack — Smart Expense Tracker (working CRUD MVP)

A beginner-friendly full-stack expense tracker. Backend and frontend are separate folders and communicate through REST APIs.

## Included
- Create, list, retrieve, update and delete expenses
- MySQL persistence through Spring Data JPA / Hibernate
- Input validation (title, positive amount, category, date, notes)
- Consistent 400 / 404 / 500 API error responses
- Category filtering and current-month / all-time totals
- Responsive HTML/CSS/JavaScript frontend connected with `fetch()`

## Not included yet
This is a complete runnable **single-user CRUD MVP**, not the final multi-user production product. Registration, login, password hashing, JWT authentication, per-user data ownership, budgets, advanced analytics, automated tests and Flyway migrations are the next development layer. Do not expose this unauthenticated API publicly.

## Requirements
- JDK 26 (matching the current project setup)
- Maven 3.9+
- MySQL Server running locally
- Python 3 (only to serve the static frontend; alternatively use VS Code Live Server)

## 1. Create the database
In MySQL:

```sql
CREATE DATABASE IF NOT EXISTS expense_tracker;
```

## 2. Configure the backend database credentials
Open a terminal, go to `backend`, and set environment variables for your MySQL account. Do not put your database password in source code.

macOS / zsh example:

```bash
export DB_USERNAME=root
export DB_PASSWORD='YOUR_MYSQL_PASSWORD'
```

Optional override:

```bash
export DB_URL='jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
```

## 3. Run Spring Boot

Open the `backend` folder as a Maven project in IntelliJ IDEA, wait for dependency import, then run `ExpenseTrackerApplication.java`. Alternatively, if Maven is installed, run:

```bash
cd backend
mvn spring-boot:run
```

Backend base URL: `http://localhost:8080`

Hibernate creates/updates the `expenses` table automatically for local development. `ddl-auto=update` is not the recommended production migration strategy.

## 4. Run the frontend
Open a second terminal:

```bash
cd frontend
python3 -m http.server 5500
```

Open `http://localhost:5500` in your browser. Keep the backend running in the first terminal.

## API reference
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/expenses` | List all expenses |
| GET | `/api/expenses?category=Food` | Filter by category |
| GET | `/api/expenses?startDate=2026-10-01&endDate=2026-10-31` | Filter by date range |
| GET | `/api/expenses/{id}` | Get one expense |
| POST | `/api/expenses` | Create expense |
| PUT | `/api/expenses/{id}` | Replace/update expense fields |
| DELETE | `/api/expenses/{id}` | Delete expense |
| GET | `/api/expenses/summary` | All-time and current-month totals |

Example POST body:

```json
{
  "title": "Lunch",
  "amount": 250.00,
  "category": "Food",
  "expenseDate": "2026-10-30",
  "notes": "Lunch with friends"
}
```

## Notes
- Amount is stored as `DECIMAL(12,2)` / Java `BigDecimal`, not floating-point.
- Dates use ISO format `YYYY-MM-DD`.
- The frontend runs on port 5500 and backend on 8080; CORS is enabled for the local frontend origins.
- If you already have an existing `expenses` table with a different schema, back it up before using this fresh project against the same database. Prefer a new database name for the first run.
