---
trigger: always_on
description: Rules for registry safety, intrusive holders, and unified mixin architecture.
---

# Buildscape Agent Rules: Registry Safety & Mixins

## 1. Registry Safety & Intrusive Holders

Modern Minecraft (1.20.5+ / 1.21.1 and 26.x) enforces strict registry freezing and intrusive holders:

1. **Mandatory Lifecycle Wrapping:**
   - Any registration or instantiation of `Block`, `Item`, `BlockEntityType`, or `EntityType` must be wrapped in:
     ```java
     Services.PLATFORM.wrapRegistryAction(() -> {
         // Registration and instantiation code
     });
     ```
2. **Re-entrant / Nested Call Safety:**
   - When implementing `wrapRegistryAction`, track `prevHolders`:
     ```java
     Object[] prevHolders = new Object[registries.length];
     for (int i = 0; i < registries.length; i++) {
         if (registries[i] instanceof net.minecraft.core.MappedRegistry<?> mapped) {
             wasFrozen[i] = frozenField.getBoolean(mapped);
             if (wasFrozen[i]) frozenField.setBoolean(mapped, false);
             prevHolders[i] = holdersField.get(mapped);
             if (prevHolders[i] == null) {
                 holdersField.set(mapped, new java.util.IdentityHashMap<>());
             }
         }
     }
     ```
   - In `finally`, ONLY reset `holdersField.set(mapped, null)` if `prevHolders[i] == null`. Never clear active maps on nested exits!
3. **Targeted Intrusive Holder Registries:**
   - Intrusive holder maps must ONLY be populated on registries that use intrusive holders:
     - `BuiltInRegistries.BLOCK`
     - `BuiltInRegistries.ITEM`
     - `BuiltInRegistries.BLOCK_ENTITY_TYPE`
     - `BuiltInRegistries.ENTITY_TYPE`
     - `BuiltInRegistries.FLUID`
   - NEVER attach intrusive holder maps to standard non-intrusive registries (`BLOCKSTATE_PROVIDER_TYPE`, `CUSTOM_STAT`, `PARTICLE_TYPE`).

---

## 2. Unified Mixin Architecture

1. **Single Mixin Manifest:**
   - A single file `common/src/main/resources/buildscape.mixins.json` coordinates all mixins across all 11 loader targets.
   - Do NOT split mixin configuration files by version or loader.
2. **Dynamic Injections & Error Suppression:**
   - For methods or targets that only exist in specific Minecraft versions, annotate with:
     ```java
     @Dynamic
     @Inject(method = "...", at = @At("..."), require = 0)
     ```
3. **No Synthetic Private Method Capture:**
   - Never use method references or lambdas capturing `this` (e.g. `this::addRenderableWidget`) within mixin methods. This produces synthetic private methods that fail mixin verification (`NoClassDefFoundError: ... is invalid`).
   - Use reflection or helper delegates inside `MixinFactory.java`.
4. **Immediate Delegation:**
   - Keep mixin classes thin. Immediately forward version-sensitive logic to `MixinFactory.get().<method>()`.
