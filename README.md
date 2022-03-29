<div id="top"></div>

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
![Code Size][code-size-shield]
![Contributors][contributors-shield]
![Issues][issues-shield]
![Release][release-shield]


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://cdn.discordapp.com/attachments/930205704525070357/958314726004240434">
    <img src="https://cdn.discordapp.com/attachments/930205704525070357/958314726004240434/CobaltLogo1.png" alt="Logo" width="240" height="240">
  </a>

  <h3 align="center">CobaltCore</h3>

  <p align="center">
    The core plugin for all Cobalt plugins!
    <br />
    <a href="https://github.com/Fusion1013/CobaltCore"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/Fusion1013/CobaltCore">Wiki</a>
    ·
    <a href="https://github.com/Fusion1013/CobaltCore/issues">Report Bug</a>
    ·
    <a href="https://github.com/Fusion1013/CobaltCore/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#dependencies">Dependencies</a></li>
      </ul>
    </li>
    <li>
      <a href="#systems">Systems</a>
    </li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
CobaltCore contains a variety of helper methods and classes to aid during plugin development.

<p align="right">(<a href="#top">back to top</a>)</p>

### Dependencies
* [CommandAPI](https://github.com/JorelAli/CommandAPI)

<p align="right">(<a href="#top">back to top</a>)</p>

## Systems
Below are details about all the systems contained within CobaltCore and how to use them.

### Custom Entities
The Custom Entity System contains methods for creating and spawning custom entities. Custom Entities need to be registered through the CustomEntityManager in order to show up in game.

Example Entity:
```
public static final ICustomEntity EXAMPLE_ENTITY = CustomEntityManager.register(new CustomEntity.CustomEntityBuilder("example_entity", EntityType.ZOMBIE)
  .addExecuteOnSpawnModule(new EntityHealthModule(100))
  .addExecuteOnTickModule(new EntityBossBarModule("Example Entity", 10, BarColor.BLUE, BarStyle.SEGMENTED_6))
  .addExecuteOnDeathModule(new EntityDropModule(100, 2, new ItemStack(Material.DIAMOND), 0.2))
  .addAbilityModule(new ChargeAbility(8, 1, 5))
  .setCustomName("Example Entity")
  .build);
```
The above code creates a new `ICustomEntity` with the zombie as a base, 100 health, a bossbar with an activation range of 10 blocks, 100xp on death, a 20% chance to drop a diamond on death, the charge ability with a cooldown of 8 seconds, and with a custom name of "Example Entity". See below for a list of every `EntityModule` and `AbilityModule`, and how to create your own.

#### Entity Modules
* **EntityAmbientSoundModule**: Adds ambient sounds to the entity.
* **EntityBossBarModule**: Adds a bossbar to the entity
* **EntityDropModule**: Adds a item and/or xp drops to the entity.
* **EntityEquipmentModule**: Adds equipment to the entity.
* **EntityHealthModule**: Adds health to the entity.
* **EntityPotionEffectModule**: Adds potion effects to the entity.
* **EntityStateModule**: Adds a `StateEngine` to the entity.

#### Ability Modules
* **ChargeAbility**: Charges the nearest entity, damaging anything in its path.
* **RageAbility**: Increases the speed and strength of the entity for a few seconds.

### Custom Items
The Custom Item System contains methods for creating custom items.

### Custom Trades
The Custom Trades System contains methods for creating custom Wandering Trader trades.

### Localization System

### Managers

### Settings

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
<!-- https://shields.io/ -->
[code-size-shield]: https://img.shields.io/github/languages/code-size/Fusion1013/CobaltCore.svg?style=for-the-badge
[contributors-shield]: https://img.shields.io/github/contributors/Fusion1013/CobaltCore.svg?style=for-the-badge
[issues-shield]: https://img.shields.io/github/issues/Fusion1013/CobaltCore.svg?style=for-the-badge
[release-shield]: https://img.shields.io/github/v/release/Fusion1013/CobaltCore.svg?style=for-the-badge

