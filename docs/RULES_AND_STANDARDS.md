# Buildscape Development Rules & Standards

These rules are strict architectural invariants for all maintainers, contributors, and automation tools working on Buildscape.

---

## 1. Zero Stubs & Zero Temporary Hacks

- **No Stubs:** Never commit empty stub classes or `throw new UnsupportedOperationException()` methods as a substitute for implementing proper adaptation logic.
- **No Temporary Exclusions:** Do not exclude problematic files from compilation tasks (`exclude '**/*Mixin.java'`) to get builds green. If code does not compile or crashes at runtime, resolve the underlying API contract cleanly.
- **No Script-Based Workarounds:** Do not use throwaway scripts that mutate sources dynamically or bypass standard compilation pipelines.
- **No Copy-Paste Redundancy:** Do not duplicate large identical blocks of code across `v118x`, `v121x`, and `v26x`. If logic is version-neutral, move it into the neutral common package and isolate only the divergent primitives behind the VersionCluster Adapter interface.

---

## 2. Common Neutrality Rule

Code under `common/src/main/java/com/kingodogo/buildscape/` (outside `adapter/`):
1. **Never import loader packages:**
   - Prohibited: `net.minecraftforge.*`, `net.fabricmc.*`, `net.neoforged.*`.
2. **Never invoke version-divergent Minecraft methods directly:**
   - Prohibited in neutral code: `GuiGraphics`, `ItemStack.set(DataComponents...)`, `ItemStack.getTagElement(...)`, `SubmitNodeCollector`.
   - Permitted: Calling neutral wrapper methods on `Services.PLATFORM`, `MixinFactory`, or neutral factories.

---

## 3. Unified Mixin Architecture

Buildscape uses a single `buildscape.mixins.json` across ALL versions and loaders.
- **Do not create version-specific mixin files** (e.g. `ThrownTridentEntityMixin_121.java`).
- Mixin classes live exclusively in `common/src/main/java/com/kingodogo/buildscape/mixin/`.
- Every mixin that encounters divergent method signatures or bytecode across versions must:
  1. Use `@Dynamic` and `require = 0` on injected methods that exist only in certain versions.
  2. Avoid method references or lambdas capturing `this` (e.g. `this::addRenderableWidget`), which causes synthetic private method generation and Mixin validation failure (`NoClassDefFoundError: ... is invalid`).
  3. Avoid `@Inject` on inherited superclass methods if only declared on the superclass. Inject into the superclass (e.g. `AbstractContainerScreen`) and use `if ((Object) this instanceof TargetClass)`.
  4. Delegate all version-dependent logic immediately to `MixinFactory.get().<method>(...)`.
- VersionCluster-specific Mixin logic must be implemented inside `com.kingodogo.buildscape.adapter.<cluster>.MixinFactory`.

---

## 4. Registry Safety & Intrusive Holders

Modern Minecraft (1.21.1+ and 26.x / NeoForge) freezes vanilla `BuiltInRegistries` prior to mod construction and uses intrusive holders for certain registries (`BLOCK`, `ITEM`, `BLOCK_ENTITY_TYPE`, `ENTITY_TYPE`, `FLUID`).
- **Never call `Registry.register(...)` on frozen registries without lifecycle wrapping.**
- Any code that registers or instantiates blocks, items, or block entity types during initialization MUST run inside:
  ```java
  Services.PLATFORM.wrapRegistryAction(() -> {
      // Registration and instantiation logic
  });
  ```
- **Intrusive Holder Rule:** When implementing `wrapRegistryAction`, only activate `unregisteredIntrusiveHolders` on registries that actually use intrusive holders (`BLOCK`, `ITEM`, `BLOCK_ENTITY_TYPE`, `ENTITY_TYPE`, `FLUID`). Never assign synthetic intrusive holder maps to standard non-intrusive registries (`BLOCKSTATE_PROVIDER_TYPE`, `CUSTOM_STAT`, `PARTICLE_TYPE`), or Minecraft will fail with `AssertionError: Missing intrusive holder`.
- Use `PlatformAdapterBase.safeRegister(Registry<V> registry, ResourceLocation id, V value)` for registering objects into `BuiltInRegistries`.

---

## 5. Privacy & Identity Standards

- User identities, email addresses, and author handles must remain clean and aligned with project definitions.
- Never write private identity details or full personal names into tracked files or public documentation.
- Commit authoring must use standard project git author details.

---

## 6. No AI Attribution Rule

- Never add AI or agent attribution to any commit, pull request, release, or published file:
  - No `Co-Authored-By` trailers referencing AI agents.
  - No "Generated with" or "Assisted by" comments or commit trailers.
  - Keep commits authored by the designated project git author with concise, professional messages.
