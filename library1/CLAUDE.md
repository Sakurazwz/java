# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

### Backend (Spring Boot + Maven)
```bash
# Build
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=Library1ApplicationTests

# Start the app (requires running PostgreSQL)
./mvnw spring-boot:run
```

### Frontend — React SPA (in `html/react/user/`)
```bash
cd html/react/user
npm run dev          # Dev server on localhost:5173
npm run build        # Production build
npm run lint         # ESLint
npm run preview      # Preview production build
```


## Architecture

### Backend — `src/main/java/com/gcc/library1/`

Spring Boot 3.5 / Java 21 application with stateless JWT authentication.

**Package layout by layer:**
- `model/` — JPA entities: Book (includes `count`/`borrowCount` stock tracking), User, BorrowRecord, BorrowHistory
- `repository/` — Spring Data JPA interfaces. BorrowRecord queries use `(bookId, userId)` compound lookups for multi-user borrow support.
- `service/` — Business logic (borrow/return/renewal workflows live here)
- `controller/` — REST controllers returning domain objects directly (no DTO layer)
- `auth/` — AuthController + JWT token generation/validation + OncePerRequestFilter
- `config/` — SecurityConfig (CORS, public paths, BCrypt, stateless sessions) + DataInitializer (auto-creates default admin on first run)
- `util/SecurityUtils.java` — `getCurrentUserId()` / `isAdmin()` helpers pulling userId from `Authentication.details`

**Borrow lifecycle (the core domain logic):**
- Book stock: `Book.count` = total copies in library, `Book.borrowCount` = currently lent out
- Multiple users can borrow the same book (one copy each) as long as `count > 0`
- Same user cannot borrow the same book twice (checked via `findByBookIdAndUserId`)
1. Borrow: `BorrowRecordService.addBorrow` → `BookService.borrowBook()` decrements `count`, increments `borrowCount` (throws if count ≤ 0) → creates `BorrowRecord` (returnDate = today + 90 days) → logs history
2. Renew: `BorrowRecordService.updateBorrow` → extends returnDate by 90 days → logs history
3. Return: `BorrowRecordService.deleteBorrow(bookId, userId)` → deletes record by `(bookId, userId)` → `BookService.returnBook()` increments `count`, decrements `borrowCount` → logs history

**Security flow:** `JwtAuthenticationFilter` (runs every request) reads `Authorization: Bearer <token>`, extracts userId/role from JWT claims, stores userId in `Authentication.details`, sets Spring Security context with `ROLE_USER` or `ROLE_ADMIN`. Admin-only endpoints use `@PreAuthorize("hasRole('ADMIN')")`.

**Permission model (BorrowRecordController):** All borrow endpoints use `resolveUserId()`:
- Admin: can specify any userId in the request body — can borrow/return/renew on behalf of other users
- Regular user: the request body's userId is ignored — the authenticated user's own ID from JWT is forced
- `GET /api/borrow/all` is admin-only; regular users must use `POST /api/borrow/user` (which also enforces self-only queries)

**DB:** PostgreSQL `book_library` on `localhost:5432`. Hibernate `ddl-auto=update` auto-creates tables. All config overridable via env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`.

### Frontend — React SPA (`html/react/user/src/`)

React 19 + React-Bootstrap. All API calls go through `services/api.js` which wraps `fetch` with auto-injected JWT from `localStorage` and 401/403 redirect handling.

**Routing** (in `App.jsx`): `/login`, `/register`, `/` & `/books` (Books component), `/borrow` (Borrow component). Authenticated routes redirect to `/login` if no token.

**Auth:** JWT stored in `localStorage` under `token`. User role parsed from JWT payload. Admin UI elements (add/edit/delete book buttons) shown/hidden by role check. A shared `authApi.getCurrentUser()` helper avoids duplicate JWT parsing across components.

**Cover images:** Uploaded as files, converted to Base64 in the browser, sent as JSON strings to the backend, stored in `books.cover` (TEXT column).

### Dual frontend note
Thymeleaf templates (`src/main/resources/templates/`) exist but are vestigial — the React SPA is the active UI. New features should only touch the React frontend.

## Gotchas

- **BorrowHistory DB column is misspelled** as `behavour` (not `behaviour`/`behavior`). The Java field uses `behaviour`.
- **Return book API uses DELETE** (`DELETE /api/borrow/back`) — unconventional but intentional. Now expects `{bookId, userId}` body.
- **`/api/borrow/all`** is admin-only; regular users call `POST /api/borrow/user` with their own userId.
- **User.borrowBook field** is unused legacy — ignore it.
- **No pagination** on book listing — all books returned in one response.
- **API base URL hardcoded** to `http://localhost:8080/api` in both frontend `services/api.js` files.
- **Test DB is H2 in-memory** (test scope only), so tests don't need a running PostgreSQL.
- **`DataInitializer`** auto-creates admin/admin123 on first startup if no users exist.
- **CORS** allows `localhost:3000` and `localhost:5173`.
