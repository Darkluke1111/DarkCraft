--Defining a Recipe--
Recipe Filenames must be named "recipe_<arbitaryName>.yml"

<recipeName>:
  ==: de.darkluke1111.darkcraft.data.AdvRecipe
  result:
    type: <blockType>
    amount: <amount>
  shape:
  - <3 characters>
  - <3 characters>
  - <3 characters>
  ingredients:
    <character>: <blockType>:<subID>
    <character>: <blockType>:<subID>
  behaviors:
    <behaviorName>:
      ==: de.darkluke1111.darkcraft.data.behaviors.<behaviorClass>
      <behaviorAttribute1>: <value>
      <behaviorAttribute2>: <value>
      ...
    <behaviorName>:
      ==: de.darkluke1111.darkcraft.data.behaviors.<behaviorClass>
      <behaviorAttribute1>: <value>
      <behaviorAttribute2>: <value>
      ...
    ...

Example:

test:
  ==: de.darkluke1111.darkcraft.data.AdvRecipe
  result:
    type: BEDROCK
    amount: 4
  shape:
  - A+A
  - +++
  - A+A
  ingredients:
    A: AIR:0
    +: STONE:0
  behaviors:
    Structure:
      ==: de.darkluke1111.darkcraft.data.behaviors.StructureBehavior
      structures:
      - Gold
    ConsumeExp:
      ==: de.darkluke1111.darkcraft.data.behaviors.ConsumeExpBehavior
      consumedExp: 5
      expPerItem: true
    Lightning:
      ==: de.darkluke1111.darkcraft.data.behaviors.LightningBehavior
      chance: 100
    
--Defining different Behaviors--

1.) ConsumeLife

ConsumeLife:
  ==: de.darkluke1111.darkcraft.data.behaviors.ConsumeLifeBehavior
      consumedLife: <life in half hearts>
      lifePerItem: <true/false>
      preventDeath: <true/false>
      
2.) ConsumeExp

ConsumeExp:
  ==: de.darkluke1111.darkcraft.data.behaviors.ConsumeExpBehavior
  consumedExp: <exp in orbs>
  expPerItem: <true/false>
  
3.) Lightning

Lightning:
  ==: de.darkluke1111.darkcraft.data.behaviors.LightningBehavior
  chance: <integer 1-100>
  
4.) Explosion

Explosion:
  ==: de.darkluke1111.darkcraft.data.behaviors.ExplosionBehavior
  chance: <integer 1-100>
  
5.) Structure

Structure
  ==: de.darkluke1111.darkcraft.data.behaviors.StructureBehavior
  structures:
    - <structure1>
    - <structure2>
    - ...

--IMPORTANT--
Only structures and recipes which are named in the config.yml will be loaded into the game!
    