// Maintainer: hoyin1600p
import { mkdirSync, writeFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { dirname, join, resolve } from "node:path";

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
const recipeDirectory = resolve(
  scriptDirectory,
  "../src/main/resources/data/buildscape/recipes/botany_pots"
);

const sporeBlossomDefaults = {
  categories: ["dirt", "farmland", "moss"],
  growthTicks: 1200,
  rotation: "X_180"
};

const crops = [
  { id: "red_spore_blossom", ...sporeBlossomDefaults },
  { id: "cyan_spore_blossom", ...sporeBlossomDefaults },
  { id: "blue_spore_blossom", ...sporeBlossomDefaults },
  { id: "purple_spore_blossom", ...sporeBlossomDefaults },
  { id: "orange_spore_blossom", ...sporeBlossomDefaults }
];

function itemId(id) {
  return id.includes(":") ? id : `buildscape:${id}`;
}

function createCropRecipe(crop) {
  const seed = itemId(crop.seed ?? crop.id);
  const displayBlock = itemId(crop.displayBlock ?? crop.id);
  const drops = crop.drops ?? [
    {
      chance: 1.0,
      output: {
        item: itemId(crop.output ?? crop.id)
      }
    }
  ];
  const display = { block: displayBlock };

  if (crop.rotation) {
    display.rotation = crop.rotation;
  }

  return {
    type: "forge:conditional",
    recipes: [
      {
        conditions: [
          {
            type: "forge:mod_loaded",
            modid: "botanypots"
          }
        ],
        recipe: {
          type: "botanypots:crop",
          seed: {
            item: seed
          },
          categories: crop.categories,
          growthTicks: crop.growthTicks,
          display,
          drops
        }
      }
    ]
  };
}

mkdirSync(recipeDirectory, { recursive: true });

for (const crop of crops) {
  const outputPath = join(recipeDirectory, `${crop.id}.json`);
  const json = `${JSON.stringify(createCropRecipe(crop), null, 2)}\n`;
  writeFileSync(outputPath, json, "utf8");
}

console.log(`Generated ${crops.length} conditional Botany Pots recipes.`);
