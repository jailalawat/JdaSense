# JdaSense: Authentication & Role-Based Access Control (RBAC) Plan

## 1. Overview & Roles
Implement a secure, token-based (JWT) authentication system with strict Role-Based Access Control, soft-deletion, and comprehensive audit logging.

### Defined Roles:
1.  **Super Admin:** Has absolute access. Can create other Admins, Hospital Owners, and view ALL audit data globally. (Default user: `jailalawat@gmail.com` / Jai Lalawat).
2.  **Hospital Owner:** Can manage (create/soft-delete) their own `Staff Users`. Can view audit data *only* for their specific hospital/staff. Cannot create Admins.
3.  **Staff User (Normal User):** Can log in, use the core diagnostic app (record heart sounds), and view their own history. Cannot manage other users.

---

## 2. Backend Implementation (FastAPI + DynamoDB)

### A. Database Schema Updates
We will expand our DynamoDB setup (or migrate to PostgreSQL if relational queries become too complex) to include:
*   **Users Table:** `user_id`, `email`, `password_hash`, `role`, `hospital_id` (if applicable), `is_deleted` (Boolean for soft-delete), `created_at`.
*   **Audit Logs Table:** `log_id`, `actor_id` (who did it), `action` (e.g., "USER_SOFT_DELETED", "DIAGNOSIS_RUN"), `target_id` (who/what was affected), `hospital_id`, `timestamp`.

### B. New API Endpoints
*   **Auth:** 
    *   `POST /auth/login` -> Returns JWT Access & Refresh tokens.
*   **User Management (Protected by JWT & Roles):**
    *   `POST /users` -> Create user (Admin creates anyone; Hospital Owner creates Staff).
    *   `GET /users` -> List users (Filters out `is_deleted=true` unless specifically requested by Admin).
    *   `DELETE /users/{id}` -> Soft deletes a user (Sets `is_deleted=true`).
*   **Audit:**
    *   `GET /audit` -> Admin gets all; Hospital Owner gets `WHERE hospital_id = own_id`.

### C. Default Seeding
On backend startup, check if `jailalawat@gmail.com` exists. If not, create it as `Super Admin`.

---

## 3. Mobile App Implementation (Android/Kotlin)

### A. Authentication Flow
1.  **Login Screen:** Email/Password form. Upon success, store the JWT securely using **EncryptedSharedPreferences**.
2.  **Quick Login (Post-Auth):**
    *   **Biometric Auth:** Use AndroidX `BiometricPrompt` to allow fingerprint/face unlock for subsequent sessions (tied to the stored JWT).
    *   **PIN/Number Based:** Allow setting a 4-digit PIN as a fallback for quick login.

### B. Role-Based UI
*   **Main Navigation:** 
    *   If `Staff User`: Goes directly to the Heart Sound Recording screen.
    *   If `Hospital Owner` or `Admin`: Shows a Bottom Navigation Bar with "Diagnostic" and "Management Dashboard".
*   **Management Dashboard:**
    *   List users.
    *   "Add User" floating action button.
    *   Swipe-to-delete (which calls the soft-delete API).
    *   Audit Log Viewer screen.

### C. Network Interceptor
*   Update Retrofit `NetworkModule.kt` to include an `AuthInterceptor` that attaches `Authorization: Bearer <JWT>` to every request automatically.

---

## 4. Implementation Phasing
*   **Phase 1:** Backend JWT logic, User/Audit Database Schema, and Default Admin Seeding.
*   **Phase 2:** Backend CRUD APIs for Users and Audit Logs with Role enforcement.
*   **Phase 3:** Mobile Login UI, Secure Token Storage, and Retrofit Interceptor.
*   **Phase 4:** Mobile Biometric/PIN quick login.
*   **Phase 5:** Mobile Management Dashboard (User CRUD & Audit Views).
