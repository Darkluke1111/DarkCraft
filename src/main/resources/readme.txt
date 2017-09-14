--Defining a Recipe--
Recipe Filenames must be named "recipe_<arbitaryName>.yml"

<recipeName>:
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
      <behaviorAttribute1>: <value>
      <behaviorAttribute2>: <value>
      ...
    <behaviorName>:
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
    StructureBehavior:
      structures:
      - Gold
    ConsumeExpBehavior:
      consumedExp: 5
      expPerItem: true
    LightningBehavior:
      chance: 100
    
--Defining different Behaviors--

1.) ConsumeLife

ConsumeLifeBehavior:
      consumedLife: <life in half hearts>
      lifePerItem: <true/false>
      preventDeath: <true/false>
      
2.) ConsumeExp

ConsumeExpBehavior:
  consumedExp: <exp in orbs>
  expPerItem: <true/false>
  
3.) Lightning

LightningBehavior:
  chance: <integer 1-100>
  
4.) Explosion

ExplosionBehavior:
  chance: <integer 1-100>
  
5.) Structure

StructureBehavior
  structures:
    - <structure1>
    - <structure2>
    - ...

--IMPORTANT--
Only structures and recipes which are named in the config.yml will be loaded into the game!
    