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
        Map<String, Object> recipeMap = (Map) config.get(key);
        recipes.add(parseRecipeMap(key, recipeMap));
      } catch (ClassCastException exc) {
        throw new PersistenceSerialisazionException(
            "Invalid keyvalue in key '"
                + key
                + "'. Value should be a Map");
      }

    }
    return recipes;
  }

  private AdvRecipe parseRecipeMap(String recipeTag, Map<String, Object> map)
      throws PersistenceSerialisazionException {

    final ItemStack result = parseRecipeResult(recipeTag, map);
    final String[] shape = parseRecipeShape(recipeTag, map);
    final Map<Character, MaterialData> ingredientMap = parseIngredients(recipeTag, map);
    final List<Behavior> behaviors = parseBehaviors(recipeTag, map);

    return AdvRecipe.createRecipe(result, shape, ingredientMap)
        .withAllBehaviors(behaviors);
  }

  @SuppressWarnings("unchecked")
  private String[] parseRecipeShape(String recipeTag, Map<String, Object> recipeMap)
      throws PersistenceSerialisazionException {
    if (!recipeMap.containsKey(shapeTag)) {
      throw new PersistenceSerialisazionException(
          "Shape-tag of recipe '"
              + recipeTag
              + "' is missing.");
    }
    List<String> shapeList;
    try {
      shapeList = (List) recipeMap.get(shapeTag);
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
  private ItemStack parseRecipeResult(String recipeTag, Map<String, Object> recipeMap)
      throws PersistenceSerialisazionException {
    if (!recipeMap.containsKey(resultTag)) {
      throw new PersistenceSerialisazionException(
          "Result-tag of recipe '"
              + recipeTag
              + "' is missing.");
    }

    try {
      Map<String, Object> resultMap = (Map) recipeMap.get(resultTag);
      return ItemStack.deserialize(resultMap);
    } catch (ClassCastException exc) {
      throw new PersistenceSerialisazionException(
          "Invalid result-value in recipe '"
              + recipeTag
              + "'.");
    }
  }

  @SuppressWarnings("unchecked, deprecation")
  private Map<Character, MaterialData> parseIngredients(
      String recipeTag,
      Map<String, Object> recipeMap)

      throws PersistenceSerialisazionException {
    if (!recipeMap.containsKey(ingredientTag)) {
      throw new PersistenceSerialisazionException(
          "Ingredient-tag of recipe '"
              + recipeTag
              + "' is missing.");
    }

    Map<String, Object> temp1;
    try {
      temp1 = ((Map<String, Object>) recipeMap.get(ingredientTag));
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
  private List<Behavior> parseBehaviors(String recipeTag, Map<String, Object> recipeMap)
      throws PersistenceSerialisazionException {

    if (!recipeMap.containsKey(behaviorTag)) {
      return new ArrayList<>();
    }

    Map<String, Object> behaviorMap;
    try {
      behaviorMap = (Map) recipeMap.get(behaviorTag);
    } catch (ClassCastException exc) {
      throw new PersistenceSerialisazionException(
          "Invalid behavior-value in recipe '"
              + recipeTag
              + "'.");
    }

    Set<String> keys = behaviorMap.keySet();
    List<Behavior> behaviors = new ArrayList<>();

    for (String key : keys) {
      try {
        Map<String, Object> behaviorData = (Map) behaviorMap.get(key);
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
