# Mob Pillar Documentation

The **Mob Pillar** allows you to display any mob from Minecraft (or other mods) on top of it.
You can customize the appearance and behavior of the mob by renaming the **Spawn Egg** in an Anvil before placing it on
the pillar.

## Universal States

These keywords work on **almost any mob**!

| Keyword      | Alias            | Effect                                       |
|:-------------|:-----------------|:---------------------------------------------|
| `spin`       |                  | Makes the mob spin slowly.                   |
| `dinnerbone` | `grum`           | Makes the mob render upside down.            |
| `baby`       |                  | Renders the baby version of the mob.         |
| `saddled`    | `saddle`         | Shows the mob with a saddle.                 |
| `sheared`    | `shear`          | Shows the mob as sheared.                    |
| `chested`    | `chest`          | Shows the mob with a chest.                  |
| `sitting`    | `sit`            | Puts the mob in a sitting pose.              |
| `tamed`      | `tame`           | Renders the mob as tamed.                    |
| `angry`      | `aggro`          | Renders the mob in an angry/attacking state. |
| `charged`    | `charge`         | Renders the charged state (e.g., Creeper).   |
| `glowing`    | `glow`           | Makes the mob glow.                          |
| `fire`       |                  | Renders the mob on fire.                     |
| `frozen`     | `freeze`, `cold` | Renders the mob frozen/shaking.              |
| `hurt`       | `damage`         | Makes the mob flash red permanently.         |
| `invisible`  |                  | Makes the mob invisible.                     |

## Mob-Specific Variants

### 🐺 Wolf / Dog

* `begging` (or `beg`)
* `angry`, `tame`, `sit`, `baby`

### � Cat

* **Variants**: `tabby`, `tuxedo` (or `black`), `red` (or `orange`), `siamese`, `british`, `calico`, `persian`,
  `ragdoll`, `white`, `jellie`, `all_black` (or `midnight`)
* **States**: `sitting`, `tamed` (shows collar), `baby`

### 🦊 Fox

* **Variants**: `red` (default), `snow` (or `white`)
* **States**: `sitting`, `sleeping`, `crouching`, `baby`

### �🐻 Polar Bear

* `standing` (or `stand`, `rear`)
* `baby`

### � Panda

* **Variants**: `normal`, `lazy`, `worried`, `playful`, `brown`, `weak`, `aggressive`
* **States**: `baby`, `sitting`

### 🐸 Axolotl

* **Variants**: `lucy` (or `pink`), `wild` (or `brown`), `gold` (or `yellow`), `cyan`, `blue`
* **States**: `baby`

### 🐰 Rabbit

* **Variants**: `brown`, `white`, `black`, `spotted` (or `white_splotched`), `gold`, `salt`, `toast` (special skin),
  `killer`
* **States**: `baby`

### �🕷 Spider

* `climbing` (or `climb`)

### 🦇 Bat

* `hanging` (or `hang`, `roost`)

### 🦙 Llama

* **Variants**: `creamy`, `white`, `brown`, `gray`
* **States**: `chested` (or `chest`), `baby`

### 🐎 Horse / Donkey / Mule

* **States**: `tamed` (prevents bucking), `saddled` (shows saddle), `baby`
* **Note**: Horse colors/markings logic is complex and might partially work via NBT if specified.

### � Pig / Strider

* **States**: `saddled`

### 🐐 Goat

* `screaming`, `nohorns` (or `no_horns`)
* `baby`

### 🐑 Sheep

* **Colors**: `white`, `orange`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `light_gray`, `cyan`,
  `purple`, `blue`, `brown`, `green`, `red`, `black`
* **States**: `sheared`, `baby`

### 🍄 Mooshroom

* **Variants**: `red`, `brown`
* **States**: `baby`

### 🦜 Parrot

* **Variants**: `red` (or `cookie`), `blue`, `green`, `cyan`, `gray`
* **States**: `sitting`, `tamed`

### � Bee

* `angry`, `nectar` (has nectar), `stung` (has stung)
* `baby`

### 🧙 Evoker / Illusioner

* `casting` (or `cast`)

### 👽 Enderman

* `screaming` (or `scream`, `stare`)
* `carrying` (or `carry`, `block`)

### 👹 Creeper

* `charged` (or `powered`), `ignited` (or `ignite`)

### 👺 Wither

* `invul` (or `shield`)

### 🤖 Iron Golem

* `cracked` (or `crack`, `broken`)

### ⛄ Snow Golem

* `nopumpkin` (or `shear`)

### 🫧 Pufferfish

* `puff` (or `puffed`, `full`)

### 📦 Shulker

* `open` (or `opened`)

### 🪓 Vindicator

* `johnny`

### 🚣 Boat

* `spruce`, `birch`, `jungle`, `acacia`, `dark_oak`

### 🥋 Armor Stand

* `arms`, `nobase`, `small`

### 👨‍🌾 Villager

* **Professions**: `farmer`, `fisherman`, `shepherd`, `fletcher`, `librarian`, `cartographer`, `cleric`, `armorer`,
  `weaponsmith`, `toolsmith`, `butcher`, `leatherworker`, `mason`, `nitwit`
* **States**: `baby`

---

## Technical Details & Architecture

### Overview

The system uses `states.txt` as the authoritative reference for all valid mob names and their allowed states.

### Architecture

* **MobPillarRenderer.java**: Handles all mob state parsing and rendering logic.
* **states.txt**: Configuration file in the root directory listing valid mobs and states.
* **PillarBlockEntityRenderer.java**: Delegates mob rendering to `MobPillarRenderer`.

### State Parsing Logic

1. Load `states.txt` on first use (lazy loading).
2. Extract custom name from spawn egg NBT.
3. Split name into words.
4. Check each word against valid states for that mob type (defined in `states.txt`).
5. Apply universal states regardless of mob type.
6. Build a `MobState` object with all active states.
