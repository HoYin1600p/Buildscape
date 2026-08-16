# Global Modding Guidelines

- When coding mod-related files, always test edge-case scenarios, not just the normal/happy path.
- When adding new blocks, register **all relevant files and resources**, from lang entries and textures to blockstates, model JSONs, loot tables, recipes, tags, rendering, and anything else required.
- When adding variants, remember how the project's resources and data are managed. Follow existing systems such as the custom recipe system instead of creating duplicate or disconnected implementations.
- Before adding new code, check whether an existing system, utility, registry, or implementation can be reused.
- Keep code clean, compact, readable, and maintainable. **No slop code, unnecessary duplication, temporary hacks, or over-engineering.**
- When changing a feature, check every system it interacts with instead of assuming the change is isolated.
- Always consider client/server separation and make sure client-only code does not leak into server/common code.
- Test missing data, invalid inputs, duplicates, empty states, large amounts of data, reloads, restarts, and other realistic edge cases where relevant.
- Fix the **root cause** of bugs instead of patching symptoms or stacking quick fixes.
- Do not leave placeholders, debug code, TODO implementations, or temporary workarounds unless explicitly requested.
- Keep changes scoped to the task and avoid modifying unrelated systems/files.
- After meaningful changes, build/test the affected functionality and verify that resources, registrations, and runtime behavior actually work.
- Prefer simple solutions that fit the existing architecture over introducing unnecessary new systems.
- **A feature is only complete when all of its required code, registrations, resources, variants, and behavior are implemented and tested.**
