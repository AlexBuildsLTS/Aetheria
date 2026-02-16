# 🌌 Aetheria: Void Horizon

![Version](https://img.shields.io/badge/Version-1.0.0-blue)
![Status](https://img.shields.io/badge/Status-In%20Development-orange)
![Engine](https://img.shields.io/badge/Engine-LibGDX%201.13.1-red)
![Backend](https://img.shields.io/badge/Backend-Supabase%203.0-green)
![Language](https://img.shields.io/badge/Language-Kotlin%202.0-purple)
![Build](https://img.shields.io/badge/Build-Gradle%208.x-lightgrey)
![Networking](https://img.shields.io/badge/Networking-Ktor%203.0-yellow)

[cite_start]**Aetheria: Void Horizon** is a high-fidelity, third-person Action MMORPG built for Android and PC[cite: 70, 106]. [cite_start]It bridges the gap between methodical MMO progression and kinetic, high-octane battle arena combat[cite: 71, 131].

---

## 🎮 Game Universe & Lore: "The Great Deletion"

[cite_start]Aetheria is not a standard fantasy world; it is a utopian simulation actively crashing[cite: 264]. [cite_start]A virus known as **"The Void"** has corrupted the source code, wiping out half the population[cite: 266, 267]. [cite_start]As an **"Ascended,"** you must manipulate corrupted code—turning glitches into magic—to reach the Core Server and reboot reality before the simulation collapses forever[cite: 268, 269].

---

## ⚔️ The Ascended (Classes)

[cite_start]Playstyles are defined by "Ascended Suits"—magitech armor that dictates your role in the Void[cite: 8].

- [cite_start]**Chrono-Vanguard (Tank/Time-Bender):** A heavy plate juggernaut that builds "Time Debt" to rewind damage and creates Stasis Fields to stop projectiles mid-air[cite: 9, 14, 16].
- [cite_start]**Flux-Weaver (Glass Cannon/Artillery):** A hard-light holographic caster using "Heat" to empower spells[cite: 17, 19, 22]. [cite_start]Features "Rift Blink" teleports and devastating Orbital Beams[cite: 23, 24].
- [cite_start]**Void-Strider (Ranger/Assassin):** A biomechanical stealth specialist utilizing dual-form weapons (Sniper/Daggers) and "Execution Protocols" for rapid burst damage[cite: 25, 27, 32].
- [cite_start]**Nanite-Medic (Support):** A tactical healer utilizing spray guns to disperse healing nanites or deadly poisons[cite: 416, 845].

---

## 🧬 Origins (Races)

[cite_start]Your biological origin before donning the Ascended Suit provides permanent passive bonuses[cite: 200]:

- [cite_start]**Terrans (Humans):** Adaptable survivors[cite: 202]. [cite_start]Gain +50% Stamina Regen and +10% Movement Speed when below 20% Health[cite: 203].
- [cite_start]**Cyber-Elves (Synthetics):** Ancient beings with metal-tipped ears and internal circuitry[cite: 206, 209]. [cite_start]Gain a 5% reduction on all Ability Cooldowns[cite: 208].
- [cite_start]**Orc-Mechs (The Forged):** Massive green-skinned frames grafted to steel[cite: 211, 213]. [cite_start]Immune to the first instance of Stun/Knockback every 30 seconds[cite: 212].
- [cite_start]**Void-Born (The Glitch-Touched):** Translucent beings with static-noise eyes[cite: 215, 218]. [cite_start]Dodge rolls travel 20% further and grant 0.5s of invulnerability[cite: 217].

---

## 🛠️ Professions & Mastery

[cite_start]Aetheria features **Active Professions** where quality is determined by player skill in mini-games[cite: 220]:

- [cite_start]**Aether-Smithing:** A rhythm-based mini-game used to shape heavy gear and melee weapons[cite: 221, 223].
- [cite_start]**Void-Alchemy:** A "Pipe Flow" puzzle used to stabilize injectors, mana cells, and grenades[cite: 226, 228].
- [cite_start]**Code-Weaving:** A pattern-matching memory game used to "hack" and enchant item stats[cite: 231, 233].
- [cite_start]**The Motherboard:** An endgame talent system where you socket "Data Chips" into a CPU/GPU grid to unlock hidden passive synergies[cite: 362, 364, 365].

---

## 🕹️ Technical Combat Mechanics

- [cite_start]**No Tab-Targeting:** Features skill-based hitboxes where melee requires cleave aim and magic requires lead prediction[cite: 72, 172, 185].
- [cite_start]**Active Defense:** Manual directional blocking and "Perfect Parry" timing windows to freeze attackers[cite: 179, 181, 182].
- [cite_start]**Weak Point System:** Critical damage multipliers (2.5x) for precise aiming at Heads or Glowing Cores[cite: 197, 254].
- [cite_start]**Movement Tech:** Slide-cancel magic allows casters to remain mobile during channeled abilities[cite: 539, 540].

---

## 🛠️ Technology Stack

| Component         | Technology          | Version    | Usage                                                                  |
| :---------------- | :------------------ | :--------- | :--------------------------------------------------------------------- |
| **Language**      | Kotlin (KMP Ready)  | **2.0.0**  | [cite_start]Main development language [cite: 70, 78]                   |
| **Game Engine**   | LibGDX              | **1.13.1** | [cite_start]Rendering, Input, Audio, Physics [cite: 68, 79]            |
| **Backend**       | Supabase-KT         | **3.0.0**  | [cite_start]Auth, Database, Realtime Websockets [cite: 69, 82]         |
| **3D Rendering**  | gdx-gltf            | 2.1.0      | [cite_start]PBR Shaders, Environment, GLB/GLTF loading [cite: 81]      |
| **Networking**    | Ktor                | 3.0.0      | [cite_start]Underlying Supabase HTTP/WS client [cite: 83]              |
| **ECS Framework** | Ashley              | 1.7.4      | [cite_start]High-performance Entity Component System [cite: 80]        |
| **Physics**       | Bullet Physics      | 1.13.1     | [cite_start]Collision detection (via LibGDX Bullet wrapper) [cite: 84] |
| **UI Framework**  | VisUI / Scene2D     | 1.5.3      | [cite_start]Skin management and responsive HUD Widgets [cite: 74, 85]  |
| **Build System**  | Gradle (Kotlin DSL) | 8.x        | Version Catalog dependency management                                  |

---

## 📂 Project Structure

```text
Aetheria/
├── android/           # Android-specific launcher and configurations [cite: 90]
├── core/              # MAIN GAME LOGIC (Shared Code) [cite: 91, 92]
│   └── src/main/kotlin/com/aetheria/mmo/
│       ├── components/    # ECS Data (Position, Velocity, Player, Network) [cite: 94, 135]
│       ├── managers/      # Singletons (NetworkManager, ResourceManager) [cite: 95, 96]
│       ├── screens/       # Game States (Login, Loading, GameWorld) [cite: 97, 98]
│       ├── systems/       # Logic (Render, Physics, NetworkSync, HUD) [cite: 99, 100, 141]
│       ├── ui/            # Scene2D Widgets (Inventory, HUD Overlay) [cite: 101, 102]
│       └── utils/         # Constants (API Keys, Configs, Mappers) [cite: 103, 104]
├── lwjgl3/            # Desktop launcher (for fast debugging) [cite: 105, 106]
└── assets/            # 3D Models (.glb), Textures, Skins, Shaders, Sounds [cite: 107, 108]
```
