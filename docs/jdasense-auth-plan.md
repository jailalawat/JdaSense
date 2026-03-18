# JdaSense: Authentication & Role-Based Access Control (RBAC) Plan

## 1. Overview & Roles ✅
Implement a secure, token-based (JWT) authentication system with strict Role-Based Access Control, soft-deletion, and comprehensive audit logging.

### Defined Roles:
1.  **Super Admin:** Has absolute access. Can create other Admins, Hospital Owners, and view ALL audit data globally. (Default user: `jailalawat@gmail.com` / Jai Lalawat). ✅
2.  **Hospital Owner:** Can manage (create/soft-delete) their own `Staff Users`. Can view audit data *only* for their specific hospital/staff. Cannot create Admins. ✅
3.  **Staff User (Normal User):** Can log in, use the core diagnostic app (record heart sounds), and view their own history. Cannot manage other users. ✅

---

## 2. Backend Implementation (FastAPI + DynamoDB) ✅

### A. Database Schema Updates ✅
*   **Users Table:** `user_id`, `email`, `password_hash`, `role`, `hospital_id`, `is_deleted`, `created_at`. ✅
*   **Audit Logs Table:** `log_id`, `actor_id`, `action`, `target_id`, `hospital_id`, `timestamp`. ✅

### B. New API Endpoints ✅
*   **Auth:** `POST /auth/login` -> Returns JWT Access & Refresh tokens. ✅
*   **User Management:** `POST /users`, `GET /users`, `DELETE /users/{id}` (Soft delete). ✅
*   **Audit:** `GET /audit` (Role-filtered). ✅

### C. Default Seeding ✅
*   `jailalawat@gmail.com` seeded as `Super Admin` on startup. ✅

---

## 3. Mobile App Implementation (Android/Kotlin) ✅

### A. Authentication Flow ✅
1.  **Login Screen:** Email/Password form. Stored via **EncryptedSharedPreferences**. ✅
2.  **Quick Login (Post-Auth):**
    *   **Biometric Auth:** Fingerprint/face unlock via `BiometricPrompt`. ✅
    *   **PIN Based:** 4-digit PIN fallback. ✅

### B. Role-Based UI ✅
*   **Main Navigation:** "Management Dashboard" button visible only to Admin/Owners. ✅
*   **Management Dashboard:** Activity with Users and Audit tabs created. ✅

### C. Network Interceptor ✅
*   `AuthInterceptor` attaches `Authorization: Bearer <JWT>` to every request automatically. ✅

---

## 4. Implementation Phasing ✅
*   **Phase 1:** Backend JWT logic, User/Audit Database Schema, and Default Admin Seeding. ✅
*   **Phase 2:** Backend CRUD APIs for Users and Audit Logs with Role enforcement. ✅
*   **Phase 3:** Mobile Login UI, Secure Token Storage, and Retrofit Interceptor. ✅
*   **Phase 4:** Mobile Biometric/PIN quick login. ✅
*   **Phase 5:** Mobile Management Dashboard (User CRUD & Audit Views). ✅
