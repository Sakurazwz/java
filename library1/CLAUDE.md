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

### Frontend (React + Vite, in `html/react/user/`)
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
- `model/` — JPA entities mapped to PostgreSQL tables
- `repository/` — Spring Data JPA interfaces
- `service/` — Business logic (borrow/return/renewal workflows live here)
- `controller/` — REST controllers accepting/returning DTOs
- `dto/` — Request/Response DTOs (`BookCreateRequest`, `BookUpdateRequest`, `BookResponse`, `UserRegisterRequest`, `UserResponse`, `BorrowRecordCreateRequest`, `BorrowRecordResponse`, `BorrowHistoryDTO`)
- `dto/mapper/` — `EntityMapper` component for entity ↔ DTO conversion
- `auth/` — AuthController + JWT token generation/validation + OncePerRequestFilter
- `config/` — SecurityConfig (CORS, public paths, BCrypt, stateless sessions) + DataInitializer (auto-creates default admin on first run)
- `util/` — SecurityUtils (static helpers: getCurrentUserId(), isAdmin())

**Borrow lifecycle (the core domain logic):**
1. Borrow: `BorrowRecordService.addBorrow` → creates `BorrowRecord` (returnDate = today + 90 days) → `BookService.borrowBook` decrements `count`, increments `borrowCount` → logs history
2. Renew: `BorrowRecordService.updateBorrow` → extends returnDate by 90 days → logs history
3. Return: `BorrowRecordService.deleteBorrow` → calls `BookService.returnBook` (reverses counts) → deletes record → logs history

**Security flow:** `JwtAuthenticationFilter` (runs every request) reads `Authorization: Bearer <token>`, extracts userId/role from JWT claims, sets Spring Security context with `ROLE_USER` or `ROLE_ADMIN`. Admin-only endpoints use `@PreAuthorize("hasRole('ADMIN')")`.

**DB:** PostgreSQL `book_library` on `localhost:5432`. Hibernate `ddl-auto=update` auto-creates tables. All config overridable via env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`.

### Frontend — `html/react/user/src/`

React 19 SPA using React-Bootstrap. All API calls go through `services/api.js` which wraps `fetch` with auto-injected JWT from `localStorage` and 401/403 redirect handling.

**Routing** (in `App.jsx`): `/login`, `/register`, `/` & `/books` (Books component), `/borrow` (Borrow component), `/history` (History component), `/users` (Users component, admin only). Authenticated routes redirect to `/login` if no token.

**Auth:** JWT stored in `localStorage` under `token`. User role parsed from JWT payload. Admin UI elements (add/edit/delete book buttons) shown/hidden by role check.

**Cover images:** Uploaded as files, converted to Base64 in the browser, sent as JSON strings to the backend, stored in `books.cover` (TEXT column).

### Dual frontend note
`html/src/` is a stale copy of the old frontend — `html/react/user/` is the active React SPA. Thymeleaf templates (`src/main/resources/templates/`) also exist but are vestigial. New features should only touch `html/react/user/`.

## Gotchas

- **BorrowHistory behaviour column**: Java field `behaviour` maps to DB column `behaviour` (Hibernate auto-creates from field name). The `date` field is `LocalDateTime` (not `LocalDate`).
- **BorrowRecordRepository.findByBookId** returns `List<BorrowRecord>` (not `Optional`), because multiple users can borrow the same book ISBN mapping.
- **Return book API uses DELETE** (`DELETE /api/borrow/back`) — unconventional but intentional.
- **User.borrowBook field** is unused legacy — ignore it.
- **No pagination** on book listing — all books returned in one response.
- **API base URL hardcoded** to `http://localhost:8080/api` in `html/react/user/src/services/api.js`.
- **Test DB is H2 in-memory** (test scope only), so tests don't need a running PostgreSQL.
- **`DataInitializer`** auto-creates admin/admin123 on first startup if no users exist.
- **`spring-boot-starter-validation`** is explicitly included in pom.xml — Spring Boot 3.x removed it from the web starter. Controllers use `@Valid` on DTO request bodies.
- **Maven wrapper requires PowerShell `mvnw.cmd`** on Windows — prepend `Set-Item Env:JAVA_HOME 'C:\Users\Xian\.jdks\ms-21.0.10'` before running.
