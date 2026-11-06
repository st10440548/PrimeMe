# PrimeX — Secure backend rollout plan (not deployed)

## Security findings
The stable Azure API accepts goal requests without verifying user identity. Draft PR #6 implements Firebase Admin ID token validation and SQL owner_uid filtering. It must be reviewed and deployed only after backup and testing.

## Blocking issues before rollout
1. **Database backup and recovery:** capture a verified PostgreSQL backup and rehearse restore to a non-production database.
2. **Credentials:** rotate the previously exposed PostgreSQL password and update the Azure App Service setting. Never paste secrets into GitHub, logs, or chat.
3. **Firebase Admin:** configure FIREBASE_PROJECT_ID and service account credentials in secure Azure application settings, not repository files.
4. **Legacy records:** existing goals have no owner_uid; preserve them as unowned and hidden rather than guessing ownership.
5. **Android cache:** the post-submission Room cache is still global, not user-specific. Add UID partitioning and clear/partition cached data on sign-out before switching to authenticated per-user API. Otherwise one account may see another account's cached goals on the same phone.
6. **Client/server compatibility:** deploy verified backend first, then a compatible Android build carrying Firebase ID tokens. Do not deploy only one side.
7. **Cross-account testing:** verify two distinct Firebase accounts; account B must never read, complete or delete account A's goals.
8. **Authentication failures:** unauthenticated, invalid and revoked tokens must yield HTTP 401. Another user's goal IDs must return 404.
9. **Operational checks:** monitor Azure logs without token leakage; confirm TLS, least-privilege database credentials and recovery steps.

## Test matrix
| Scenario | Expected |
|---|---|
| No bearer token | 401 on GET/POST/PATCH/DELETE |
| Invalid bearer token | 401 |
| Revoked Firebase token | 401 |
| User A creates goal | Only A sees it |
| User B queries A's goal | Not listed |
| User B PATCH/DELETE A's goal | 404, no change |
| Legacy goal with NULL owner_uid | Retained in DB but hidden |
| A signs out; B signs in | No cached A goals shown |
| Network unavailable | User-scoped offline cache only |
| DB unavailable | No false success or destructive reset |

## Current status
**Not ready for Azure deployment.** PR #6 remains draft; PR #8 remains a separate post-submission branch. Offline write queue and automatic sync must wait for verified per-user backend and cache isolation.
