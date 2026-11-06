# PrimeX — Google Play Store readiness

Status: **preparation in progress; not published to Google Play**.

## Current verified implementation
- Android application ID: `com.primeme.app`
- Minimum SDK: 26
- Version name: 1.0; version code: 1
- Launcher icon is referenced in the manifest.
- Debug APK can be built and installed for demonstration.

## Required before a real production release
- [ ] Confirm final application name, launcher icon, and original brand assets.
- [ ] Capture screenshots from the actual application on supported device sizes.
- [ ] Create an original Play Store feature graphic.
- [ ] Review and publish a truthful, accessible privacy policy at a stable HTTPS URL.
- [ ] Complete Google Play Data safety declarations based on actual Firebase/Azure data flows.
- [ ] Configure secure release signing and protect the signing key.
- [ ] Generate and test a signed Android App Bundle (AAB); never commit the keystore.
- [ ] Check target API level and current Google Play policy requirements at submission time.
- [ ] Test account sign-in, sign-out, account-data handling, and app behaviour in release mode.
- [ ] Remove insecure cleartext network permission if not needed; review backup and data extraction rules.
- [ ] Verify backend authorization and per-user data isolation before distribution.
- [ ] Run device compatibility, accessibility, and regression checks.
- [ ] Complete store listing, content rating, contact information, app access, and release track setup.

**Important:** A debug APK and a GitHub release do not establish Play Store publication or production readiness.
