# Cosmetic API Security Requirements

This document defines the supported security boundary for Buildscape's optional
cosmetic service.

## Client Rules

- The mod may identify a player by UUID for public cosmetic lookups.
- The mod must never read, store, log, serialize, or transmit a Minecraft,
  Microsoft, Xbox, or Mojang session credential.
- Minecraft access tokens and launcher credentials must never be sent to a
  Buildscape-owned service.
- Cosmetic requests must be asynchronous, use HTTPS, enforce timeouts, validate
  response sizes, and fail without affecting normal gameplay.
- Cosmetic responses are cached only for the current client session.

## Supported Flow

1. The client requests public cosmetic data using the player's UUID.
2. The service returns only cosmetic data intended for public display.
3. The client validates and caches the response for the current session.

Public UUID lookup cannot prove account ownership. Any future operation that
changes an account, redeems a code, or selects a server-stored cosmetic must use
a separate authorization flow that does not expose a Minecraft session token.
A suitable design is a short-lived, single-use challenge completed through a
trusted browser flow and bound to the player's UUID.

## Backend Rules

- Never accept Minecraft session credentials.
- Validate UUIDs and all request fields.
- Rate limit by client and UUID.
- Reject oversized or malformed requests.
- Return only the fields required by the cosmetic client.
- Use restricted database queries and projections.
- Keep database credentials in deployment secrets.
- Never log authentication material or private account data.

## Development Gate

Any new cosmetic authorization design requires a security review before its
client or backend code is enabled. Tests must verify that no Minecraft session
credential is accessed or included in network requests.
