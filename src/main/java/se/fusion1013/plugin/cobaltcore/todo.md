# ITEMS
- ~~Load item categories from files~~
- ~~Load rarities from files~~
- ~~Refactor Item Loading into separate class from manager~~
  - ~~ItemLoader~~
  - ~~EnchantmentLoader~~
  - ~~BaseItemLoader~~
  - ~~RarityLoader~~
  - ~~CategoryLoader~~
  - ~~LoreLoader~~
  - ~~AttributeLoader~~
  - ~~MetaEditorLoader (???)~~
  - ~~ItemComponentLoader~~
  - Implement JSON compatibility for all of them
- ~~Move item rarity to separate manager (Or possibly merge all of this type into "Category" thingy)~~
- Load items from json files
- ~~Check file type before attempting to load item (Currently tries to load json using yml parser)~~
- ~~Use new file loading system~~

# ENCOUNTERS
- ~~Use new file loading system~~

# ENTITIES
- Load entities from files

# WORLD
- Remove procedural structure generation system
- Rework custom blocks to use chunk based blocks

# ITEM COMPONENTS
- Charge Component
  - Change charge time for charge component to use seconds instead of ticks
  - ~~Activate immediately option~~

# ACTIONS
- ~~Potion effect action~~