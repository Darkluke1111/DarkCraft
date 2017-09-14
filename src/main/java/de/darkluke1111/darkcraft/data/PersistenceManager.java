package de.darkluke1111.darkcraft.data;

import de.darkluke1111.darkcraft.data.behaviors.Behavior;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class PersistenceManager {
  private static final String recipeFilePrefix = "recipe_";
  private static final String behaviorPackage = "de.darkluke1111.darkcraft.data.behaviors.";
  private static final String resultTag = "result";
  private static final String shapeTag = "shape";
  private static final String ingredientTag = "ingredients";
  private static final String behaviorTag = "behaviors";

  /**
   * Loads all recipes from a recipe file and returns them as a List.
   */
  @SuppressWarnings("unchecked")
  public List<AdvRecipe> loadRecipesFromFile(File file) throws PersistenceSerialisazionException {
    if (!file.exists()) {
      throw new PersistenceSerialisazionException(
          "The recipe file with the name '"
              + file.getName()
              + "' does not exist.");
    }
    if (!file.getName().startsWith(recipeFilePrefix)) {
      throw new PersistenceSerialisazionException(
          "The recipe file with the name '"
              + file.getName()
              + "' must have the prefix '" + recipeFilePrefix + "'");
    }
    if (!file.getName().endsWith(".yml")) {
      throw new PersistenceSerialisazionException(
          "The recipe file with the name '"
              + file.getName()
              + "' must be a yaml file but file does not end with '.yml'");
    }

    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    Set<String> keys = config.getKeys(false);
    List<AdvRecipe> recipes = new ArrayList<>();

    for (String key : keys) {
      try {
        ConfigurationSection recipeSection =  config.getConfigurationSection(key);
        recipes.add(parseRecipeSection(key, recipeSection));
      } catch (ClassCastException exc) {
        System.out.println(config.get(key));
        throw new PersistenceSerialisazionException(
            "Invalid keyvalue in key '"
                + key
                + "'. Value should be a Map");
      }

    }

    recipes.forEach(AdvRecipe::register);
    return recipes;
  }

  private AdvRecipe parseRecipeSection(String recipeTag, ConfigurationSection recipeSection)
      throws PersistenceSerialisazionException {

    final ItemStack result = parseRecipeResult(recipeTag, recipeSection);
    final String[] shape = parseRecipeShape(recipeTag, recipeSection);
    final Map<Character, MaterialData> ingredientMap = parseIngredients(recipeTag, recipeSection);
    final List<Behavior> behaviors = parseBehaviors(recipeTag, recipeSection);

    return AdvRecipe.createRecipe(result, shape, ingredientMap)
        .withAllBehaviors(behaviors);
  }

  @SuppressWarnings("unchecked")
  private String[] parseRecipeShape(String recipeTag, ConfigurationSection recipeSection)
      throws PersistenceSerialisazionException {
    if (!recipeSection.contains(shapeTag)) {
      throw new PersistenceSerialisazionException(
          "Shape-tag of recipe '"
              + recipeTag
              + "' is missing.");
    }
    List<String> shapeList;
    try {
      shapeList = (List) recipeSection.getList(shapeTag);
    } catch (ClassCastException exc) {
      throw new PersistenceSerialisazionException(
          "Invalid shape-value in recipe '"
              + recipeTag
              + "'.");
    }
    if (shapeList.size() < 1 || shapeList.size() > 3) {
      throw new PersistenceSerialisazionException(
          "Invalid shape-value in recipe '"
              + recipeTag
              + "'. Shape must be a List of maximal 3 Strings");
    }
    int length = shapeList.get(0).length();
    for (String str : shapeList) {
      if (str.length() != length) {
        throw new PersistenceSerialisazionException(
            "Invalid shape-value in recipe '"
                + recipeTag
                + "'. All items in the Shape List must have an equal amount of characters");
      }
    }
    String[] shape = new String[shapeList.size()];
    shape = shapeList.toArray(shape);
    return shape;


  }

  @SuppressWarnings("unchecked")
  private ItemStack parseRecipeResult(String recipeTag, ConfigurationSection recipeSection)
      throws PersistenceSerialisazionException {
    if (!recipeSection.contains(resultTag)) {
      throw new PersistenceSerialisazionException(
          "Result-tag of recipe '"
              + recipeTag
              + "' is missing.");
    }

    try {
      Map<String, Object> resultMap = recipeSection.getConfigurationSection(resultTag).getValues(true);
      System.out.println(resultMap);
      return ItemStack.deserialize(resultMap);
    } catch (ClassCastException exc) {
      throw new PersistenceSerialisazionException(
          "Invalid result-value in recipe '"
              + recipeTag
              + "'.");
    }
  }

  @SuppressWarnings("unchecked, deprecation")
  private Map<Character, MaterialData> parseIngredients(String recipeTag,
      ConfigurationSection recipeSection) throws PersistenceSerialisazionException {

    if (!recipeSection.contains(ingredientTag)) {
      throw new PersistenceSerialisazionException(
          "Ingredient-tag of recipe '"
              + recipeTag
              + "' is missing.");
    }

    ConfigurationSection ingredientSection = recipeSection.getConfigurationSection(ingredientTag);
    Map<String, Object> temp1;
    try {
      temp1 = ingredientSection.getValues(true);
    } catch (ClassCastException exc) {
      throw new PersistenceSerialisazionException(
          "Invalid ingredient-value in recipe '"
              + recipeTag
              + "'.");
    }
    Map<Character, MaterialData> ingredientMap = new HashMap<>();

    for (Map.Entry<String, Object> ent : temp1.entrySet()) {
      String[] splitData = ((String) ent.getValue()).split(":");
      MaterialData matdata;

      switch (splitData.length) {
        case 1:
          matdata = new MaterialData(Material.getMaterial(splitData[0]));
          break;
        case 2:
          matdata = new MaterialData(
              Material.getMaterial(splitData[0]),
              Byte.parseByte(splitData[1]));
          break;
        default:
          throw new PersistenceSerialisazionException(
              "Invalid ingredient-value in recipe '"
                  + recipeTag
                  + "'.");
      }
      ingredientMap.put(ent.getKey().charAt(0), matdata);
    }
    return ingredientMap;

  }

  @SuppressWarnings("unchecked")
  private List<Behavior> parseBehaviors(String recipeTag, ConfigurationSection recipeSection)
      throws PersistenceSerialisazionException {

    if (!recipeSection.contains(behaviorTag)) {
      return new ArrayList<>();
    }
    if(!recipeSection.isConfigurationSection(behaviorTag))
      throw new PersistenceSerialisazionException(
          "Invalid behavior-value in recipe '"
          + recipeTag
          + "'");

    ConfigurationSection behaviorSection = recipeSection.getConfigurationSection(behaviorTag);

    Set<String> keys = behaviorSection.getKeys(false);
    List<Behavior> behaviors = new ArrayList<>();

    for (String key : keys) {
      try {
        System.out.println(behaviorSection.get(key));
        Map<String, Object> behaviorData = behaviorSection.getConfigurationSection(key).getValues(true);
        Behavior beh = Class.forName(behaviorPackage + key)
            .asSubclass(Behavior.class)
            .getConstructor(Map.class)
            .newInstance(behaviorData);

        behaviors.add(beh);
      } catch (ClassCastException | InstantiationException
          | ClassNotFoundException | IllegalAccessException e) {

        throw new PersistenceSerialisazionException("Invalid behavior-value in recipe '"
            + recipeTag
            + "' for behavior '"
            + key
            + "'.", e);

      } catch (NoSuchMethodException | InvocationTargetException e) {
        throw new PersistenceSerialisazionException(
            "Error while parsing behavior with name '"
                + key
                + "'.", e);
      }
    }
    return behaviors;
  }
}
