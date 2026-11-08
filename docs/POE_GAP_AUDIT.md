# PrimeX — POE gap audit (post-submission development)

This audit distinguishes implemented functionality from the original planning specification. It is not an official mark allocation.

| Area | Verified implementation / outstanding gap |
|---|---|
| Native Android app | Kotlin/Compose app builds and has been demonstrated on a physical phone. |
| Authentication | Firebase Google Sign-In and biometric unlock tested; backend goal endpoints in stable main do not verify Firebase ID tokens. |
| Cloud API | ASP.NET Core on Azure with PostgreSQL CRUD subset: GET, POST and complete goal. |
| NoSQL | No NoSQL implementation evidenced in stable main; PostgreSQL is relational. |
| Offline | SharedPreferences read-only goal cache; not Room, no offline write queue or WorkManager synchronization. |
| Notifications | Local Android notifications; no Firebase Cloud Messaging push integration evidenced. |
| Languages | English and isiZulu available. |
| Goal management | Create, list, complete and progress; edit and secure delete not in stable release. |
| Habit/routine tracking | Not evidenced in stable release. |
| Testing | Android and API CI workflow exists; passing remote CI run not confirmed. Pure Prime Score unit tests added on this follow-up branch. |
| Play Store | Listing and privacy drafts provided; signed AAB, assets, policy review and publication remain outstanding. |
| Release | Submitted stable main and GitHub release reported by developer. |

## Security blocker
The stable backend returns and changes goals without verifying authenticated user ownership. The secure migration exists separately in draft PR #6 and requires a database backup, Azure configuration and cross-account tests before deployment. Do not merge blindly.

## Follow-up order
1. Rotate exposed database credentials; update Azure App Service.
2. Safely deploy and test user-owned goal API from PR #6 after backup.
3. Replace SharedPreferences goal cache with Room; add queued offline writes and WorkManager sync with conflict/idempotency tests.
4. Implement and test FCM token registration and server-originated push messaging if required.
5. Implement required NoSQL functionality with explicit data model and verified assessment scope.
6. Complete production signing, Play Store assets, data-safety declarations and release checks.
