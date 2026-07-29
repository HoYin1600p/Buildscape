# Optional Integrations

## Botany Pots

Botany Pots crop recipes live in:

`src/main/resources/data/buildscape/recipes/botany_pots`

Each recipe is wrapped in Forge's `mod_loaded` condition. Keep the
`botanypots:crop` recipe inside that wrapper so Buildscape remains usable
without Botany Pots installed.

Use the repository-only generator to add or update recipes:

```text
node tools/generate_botany_pots_recipes.mjs
```

Add crops to the `crops` list in that file. Common settings can be shared in a
defaults object. Individual entries can override:

- `seed`
- `displayBlock`
- `output` or `drops`
- `categories`
- `growthTicks`
- `rotation`

The generator and this document are development files and are not included in
the built mod jar.
