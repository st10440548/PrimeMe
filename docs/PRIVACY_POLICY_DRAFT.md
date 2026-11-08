# PrimeX — Privacy policy (draft for review)

**Status:** Draft only. Must be reviewed and updated with the developer's actual contact information and verified data practices before publication.

PrimeX is a personal goal-tracking Android application.

## Information processed
- Google/Firebase Authentication handles sign-in and may process account identifiers and authentication information.
- Goal titles and progress are transmitted to an Azure-hosted API and stored in PostgreSQL.
- The app stores preferences and a cached copy of previously retrieved goals locally on the device.
- Biometric authentication is handled by Android's system biometric interface; PrimeX does not intentionally collect raw fingerprint or face templates.
- Local notifications may be generated following goal-related actions.

## Important current limitation
The current deployed goal API does not enforce per-user ownership of goal records. The app must not be represented as providing private, account-isolated cloud goal storage until the secure backend migration has been deployed and tested. Avoid entering sensitive personal information into goal titles.

## Third-party services
The app uses Google/Firebase for authentication and Microsoft Azure for API hosting and database storage. Their processing is governed by their respective service terms and privacy practices.

## Data retention and deletion
Cloud goal deletion and account-linked data deletion are not yet fully implemented in the stable deployed version. Do not promise in-app deletion until implemented. A verified support contact and deletion-request process must be provided before public release.

## Contact and effective date
Developer contact: **TO BE PROVIDED**
Effective date: **TO BE CONFIRMED**

This draft is not a substitute for verifying data collection, retention, legal obligations, and the Google Play Data safety form.
