# Secure goal ownership migration (PR #6)

**Do not deploy the Android build before deploying the authenticated backend. Do not merge without testing.**

## Required Azure configuration

Set `FIREBASE_PROJECT_ID` to the exact Firebase project ID for the Android app.

Set `FIREBASE_SERVICE_ACCOUNT_JSON` to a Firebase Admin service account JSON credential stored **only** in Azure App Service application settings (never in Git, screenshots or logs). Alternatively configure Application Default Credentials securely for the Azure host.

Keep `AZURE_POSTGRESQL_CONNECTIONSTRING` configured.

The API initializes Firebase Admin at startup and fails closed if credentials are missing or invalid. All `/api/goals` endpoints now require a verified Firebase ID token, passed as `Authorization: Bearer <token>`. Firebase token verification includes revocation checking. API reads and writes are scoped by verified Firebase UID.

## Existing goals and migration

At startup, the API adds nullable `owner_uid` to the existing goals table without deleting rows. Existing records have NULL ownership and are **preserved but deliberately hidden** from all authenticated accounts. They cannot be safely attributed to any particular user without reliable prior ownership records.

A database administrator can inspect these legacy rows and determine whether a separately authorized migration is appropriate. **Do not assign them to the first user who signs in.** This is a data migration, not a destructive reset.

The Android app uses per-UID offline cache keys. Previous unscoped cached goals are not read by the updated client.

## Verification before merge

1. Verify Azure Firebase Admin credentials and database backup; deploy the API.
2. Sign in as User A; create, view, complete and delete A's goal.
3. Sign in as User B; verify A's goal is invisible; B cannot PATCH or DELETE A's ID (404).
4. Verify missing, invalid and revoked tokens return 401 for all goal operations.
5. Verify existing legacy records remain in PostgreSQL and are not returned to users.
6. Verify offline cache isolation across sign-outs and different Firebase accounts.
7. Confirm Android build, .NET build and GitHub Actions; test physical device.
8. Update README and demonstration script to reflect the new semantics.

**Important:** The previous API was unauthenticated and shared all goals. This migration changes what existing users can see. It must be deployed deliberately with the Android client and a recovery plan.
