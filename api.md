\# Minecraft Cosmetic Backend – Consolidated Production Plan

This document defines the complete secure architecture for the Buildscape cosmetic system using:

\* Minecraft Session Verification (Online Mode)

\* MongoDB

\* Netlify-hosted backend (buildscapeWeb/)

\* No JWT

\* No manual login inside Minecraft

\* Single request on launch

\* Strict per‑UUID isolation



---



\# 1. CORE OBJECTIVES

The system must guarantee:

1\. No manual token input in Minecraft.

2\. No UUID-based fetch endpoints.

3\. Only authenticated UUID receives its own data.

4\. No database dumps or collection exposure.

5\. Cosmetics fetched once per launch and cached.

6\. All backend logic handled inside buildscapeWeb/.



---



\# 2. AUTHENTICATION FLOW (FINAL)

\## Step 1 – Game Launch

Mod collects:

\* UUID

\* accessToken

Sends:

POST /api/minecraft

{

"action": "authenticate",

"uuid": "...",

"accessToken": "..."

}

\## Step 2 – Backend Verification

Backend must:

1\. Verify accessToken with Mojang.

2\. Confirm verified UUID matches request UUID.

3\. Reject if invalid (401).

Never skip verification.

\## Step 3 – User Query (Strict)

Query only:

findOne(

{ uuid: verifiedUuid },

{ projection: { unlockedCosmetics: 1, selectedCosmetics: 1, \_id: 0 } }

)

Never use find() in public endpoints.

Never accept UUID from URL for lookup.

\## Step 4 – Default Cosmetics

Query separately:

find({ isDefault: true }, { projection: { \_id: 1 } })

\## Step 5 – Response

Return minimal JSON only:

{

"defaultCosmetics": \[],

"unlockedCosmetics": \[],

"selectedCosmetics": {}

}



---



\# 3. DATABASE STRUCTURE

\## users

{

"uuid": "player-uuid",

"unlockedCosmetics": \[],

"selectedCosmetics": {},

"redeemedCodes": \[]

}

Index:

createIndex({ uuid: 1 }, { unique: true })

\## cosmetics

{

"\_id": "void\_cape",

"isDefault": false,

"isCodeBased": true,

"isAdminGranted": false

}

\## redeemCodes

{

"code": "WINTER2026",

"cosmeticId": "void\_cape",

"maxUses": 100,

"usedBy": \[],

"expiresAt": 1700000000

}



---



\# 4. NETLIFY STRUCTURE (buildscapeWeb/)

netlify/functions/

\* api-user.js

\* api-minecraft.js

\* api-admin.js

\* utils/db.js

\* utils/validation.js

Rules:

\* Centralize Mongo connection in utils/db.js.

\* Reuse connection.

\* Use environment variables for DB URI.



---



\# 5. MULTI-ACTION FUNCTION POLICY

Use internal routing via "action" field.

Example:

/api/user

\* login

\* redeemCode

\* getProfile

/api/minecraft

\* authenticate

Validate action strictly.

Reject unknown actions.



---



\# 6. DATA MINIMIZATION RULE

NEVER:

\* Return full collections.

\* Return password hashes.

\* Return admin flags.

ALWAYS:

\* Use findOne with UUID filter.

\* Use projection to limit fields.



---



\# 7. PASSWORD SECURITY

Passwords must be hashed with:

\* bcrypt or argon2

Never:

\* Store plaintext

\* Use reversible encryption

\* Log passwords



---



\# 8. RATE LIMITING \& VALIDATION

All endpoints must:

\* Validate UUID format

\* Validate accessToken presence

\* Enforce HTTPS

\* Rate limit requests

\* Reject oversized or malformed JSON



---



\# 9. REDEEM CODE ATOMIC LOGIC

When redeeming:

1\. Validate code exists.

2\. Validate not expired.

3\. Validate not already redeemed.

4\. Validate usage < maxUses.

Update atomically:

\* Add cosmetic to user

\* Add UUID to usedBy

Fail entirely if any step fails.



---



\# 10. MINECRAFT DEVELOPMENT WORKFLOW

All new or refactored Minecraft networking/auth code must:

1\. Be implemented first in:

&nbsp; src/test/java

2\. Validate:

&nbsp; \* JSON structure

&nbsp; \* Async execution

&nbsp; \* Error handling

&nbsp; \* No main-thread blocking

3\. Only after stable testing move to:

&nbsp; src/main/java

If legacy auth exists in main:

\* Copy to test

\* Refactor in test

\* Replace only after validation

Optional devMode flag may allow side-loading test logic during development.

Must be disabled in production.



---



\# 11. CLIENT-SIDE RULES

On launch:

\* Authenticate once (async)

\* Cache result in memory

\* No polling

\* No per-tick calls

Fallback:

\* If auth fails → default cosmetics only



---



\# 12. FAILURE POLICY

If Mojang verification fails:

\* Return 401

\* Client disables locked cosmetics

If DB fails:

\* Return 503

\* Client loads defaults

System must fail safe.



---



\# 13. DEPLOYMENT CHECKLIST

Before production:

\* Confirm no find() without filter

\* Confirm projection used everywhere

\* Confirm rate limiting enabled

\* Confirm devMode disabled

\* Confirm environment variables set

\* Confirm no debug endpoints deployed



---



\# 14. CRITICAL NON-NEGOTIABLE RULES

NEVER:

\* Trust UUID alone

\* Accept UUID in URL for lookup

\* Expose collections

\* Skip Mojang verification

ALWAYS:

\* Verify session

\* Query with findOne({ uuid })

\* Use projection

\* Use HTTPS

\* Keep functions minimal and reusable



---



This consolidated document defines the complete secure, minimal, and production-ready architecture for the Buildscape
cosmetic backend system.



