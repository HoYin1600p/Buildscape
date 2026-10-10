# .agent Configuration Hub

This directory contains workspace rules, automated workflows, and engineering agreements tailored for agentic coding assistants (Antigravity, Codex, Claude Code) working on **Buildscape**.

---

## Directory Layout

```
.agent/
├── rules/
│   ├── dos_and_donts.md        # Master DOs & DONTs, safety, privacy, and development rules
│   ├── architecture.md         # VersionCluster adapter boundaries and neutrality invariants
│   └── registry_and_mixins.md  # Registry lifecycle, intrusive holders, and mixin standards
├── workflows/
│   ├── build_and_verify.md     # Offline multi-VersionCluster compilation and verification workflow
│   ├── client_diagnostics.md   # Non-blocking client execution and log diagnostic workflow
│   └── migrate_feature.md      # Reference-to-common migration workflow without stubs
└── README.md                   # This hub descriptor
```

---

## Rules Loading & Precedence

- **`AGENTS.md` (Repository Root):** Auto-discovered by all AI agents upon workspace open.
- **`.agent/rules/*.md`:** Granular rules enforced across all repository directories.
- **`.agent/workflows/*.md`:** Multi-step operational procedures for common developer tasks.
