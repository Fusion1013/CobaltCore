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
  <!--
  <a href="https://cdn.discordapp.com/attachments/930205704525070357/958314726004240434">
    <img src="https://cdn.discordapp.com/attachments/930205704525070357/958314726004240434/CobaltLogo1.png" alt="Logo" width="240" height="240">
  </a>
  -->

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
      <a href="#installation">Intallation</a>
    </li>
    <li>
      <a href="#systems">Systems</a>
      <ul>
        <li><a href="#custom-entities">Custom Entities</a></li>
      </ul>
    </li>
    <li>
      <a href="#roadmap">Roadmap</a>
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

## Installation
To start using Cobalt, download the jar and import it into your project. Make sure your main plugin class is implementing the `CobaltPlugin` interface.

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
The Custom Trades System contains methods for creating custom Wandering Trader trades. Trades can be created with the /trades command, or using the `CustomTradesManager`.

### Localization System
The localization system allows for simple creation and interaction with localization files. Custom localization files should be located in the `resources/lang` folder, and should follow minecraft localization naming scheme. For example, the localization file for `English United States` would be named `en_us.json`. Formatting Codes can be used in locale strings to format messages.

Example localization file:
```
{
  "prefix.core": "&7[<g:#00aaaa:#0066aa>Cobalt&7] ",
  "cobalt.player.join": "&7This server is running &3%plugin% v&3%version%",

  "connection.join": "&3%player% &7joined the game",
  "connection.quit": "&3%player% &7left the game",

  "commands.error.incorrect_syntax": "&7Incorrect Syntax",
  "command-not-implemented": "&7Command not yet implemented",
  "command-unknown": "&7Unknown command",
  "commands.player_not_found": "&7Player &3%player% &7not found"
}
```
To get a localized string from a locale file, the `LocaleManager` should be used. To broadcast a message to the entire server, use the `broadcastMessage()` method. To send a message to a specific player, use the 

#### LocaleManager Methods
* `getLocaleMessage()`: Get the specified localized string. If a `Player` is passed to this method, the locale will choose the language of that player if that file exists. If not, it will use `en_us`.
* `broadcastMessage()`: Broadcasts a message to the entire server.
* `sendMessage()`: Sends a localized message to the specified `Player`. The locale will choose the language of that player if that file exists. If not, it will use `en_us`. This message will have the prefix of the given plugin, unless otherwise specified. This prefix can be defined in the locale files, under `prefix.PLUGIN`

All the above methods can be given a `StringPlaceholder` to pass information to the locale strings.

#### String Placeholders
`StringPlaceholder`'s can be used to give information to locale strings. To use it, create a `StringPlaceholder` using the builder:
```
StringPlaceholder placeholder = StringPlaceholder.builder()
    .addPlaceholder("example_variable", exampleVariable)
    .build();
```
This variable can then be used in locale message. In this example, `example_variable` will be replaced with the value of the variable in the `StringPlaceholder` when the locale string is created:
```
"example.locale.message": "This is an example method with a variable: %example_variable%"
```

### Managers
All major Cobalt Systems are interacted with via `Manager`'s. Extend the `Manager` class to create your own manager and register it in the `reloadManagers()` method in your main class, using the `getManager(CobaltPlugin plugin, Class<T> managerClass)` method.

### Particle System
The particle system allows for creation of complex particle systems, by the use of Particle Styles and Particle Groups. Use the /cparticle command to create and interact with styles and groups, or use the `ParticleStyleManager` and the `ParticleGroupManager`. Particle styles and groups should not be created from their base classes.

#### Particle Styles
Particle Styles are used to generate the particles. Use one of the inbuilt ones, or create your own by extending the `ParticleStyle` class.
* **ParticleStyleCircle**: Generates a circle of particles, with a radius and a number of iterations. The iterations specify the number of particles that will make up the circle.
* **ParticleStyleCube**: Generates a cube of particles, with an edge length and a set number of particles per edge.
* **ParticleStyleGalactic**: Generates a glyph from the Galactic Alphabet (Enchanting Table), with a set letter and a compress. The compress value defines how big the glyph will be. (Smaller compress -> larger glyph)
* **ParticleStyleIcosphere**: Generates an icosphere, with a radius, particles per line and a set number of divisions and ticks per spawn. The divisions number specifies how many times the icosphere will be subdivided. Larger numbers can get laggy. The ticks per spawn can be used to lessen the amount of lag, by not spawning the particles every tick. 
* **ParticleStyleLine**: Generates a line from the center location, to a set location, with a particle density.
* **ParticleStylePoint**: Generates a single point.
* **ParticleStyleSphere**: Generates a sphere of particles, with a radius, density, and an in_sphere option. If in_sphere is set to false, only particles on the surface of the sphere will be generated.

All Particle Styles can be rotated relative to their center.

#### Particle Groups
Particle groups are used to group particle styles and display all of them. Styles contained within a group can be offset and rotated relative to the center of the group.

### Settings

### State Engines

<p align="right">(<a href="#top">back to top</a>)</p>

## Roadmap

### 3.0.0 - Scene System
The scene system will allow for complex, scripted interactions between different Cobalt systems.

### ?.0.0 - Extended Item System
* Item Event Interactions
* Item Enchantments

### ?.0.0 - Structure System

### ?.0.0 - GUI System

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
<!-- https://shields.io/ -->
[code-size-shield]: https://img.shields.io/github/languages/code-size/Fusion1013/CobaltCore.svg?style=for-the-badge
[contributors-shield]: https://img.shields.io/github/contributors/Fusion1013/CobaltCore.svg?style=for-the-badge
[issues-shield]: https://img.shields.io/github/issues/Fusion1013/CobaltCore.svg?style=for-the-badge
[release-shield]: https://img.shields.io/github/v/release/Fusion1013/CobaltCore.svg?style=for-the-badge

