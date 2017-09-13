package de.darkluke1111.darkcraft.data;

import de.darkluke1111.darkcraft.DarkCraft;
import de.darkluke1111.darkcraft.data.behaviors.Behavior;
import de.darkluke1111.darkcraft.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

/**
 * Wrapper-Class for the advanced recipes to enable more functionality than with normal recipes.
 */
public class AdvRecipe {
  //Mapping for retrieving the wrapper Recipe
  private static Set<AdvRecipe> recipeSet = new HashSet<>();

  private final ShapedRecipe recipe;
  private List<Behavior> behaviors = new ArrayList<>();

  //region Static Methods

  //region Private Methods
  private AdvRecipe(ItemStack result, String[] shape, Map<Character, MaterialData> ingedientMap) {
    this.recipe = new ShapedRecipe(DarkCraft.namespacedKey, result);
    this.recipe.shape(shape);
    for (Map.Entry<Character, MaterialData> ent : ingedientMap.entrySet()) {
      this.recipe.setIngredient(ent.getKey(), ent.getValue());
    }
  }

  //region Serialization Stuff

  /**
   * Constructor for deserialisation.
   */
  @SuppressWarnings("unchecked, deprecation")
  private AdvRecipe(Map<String, Object> map) {
    final ItemStack result = ItemStack.deserialize((Map<String, Object>) map.get("result"));
    List<String> list = (List<String>) map.get("shape");
    String[] shape = new String[list.size()];
    shape = list.toArray(shape);

    //read ingredients
    Map<Character, MaterialData> ingredientMap = new HashMap<>();
    Map<String, Object> temp1 = ((Map<String, Object>) map.get("ingredients"));
    for (Map.Entry<String, Object> ent : temp1.entrySet()) {
      String[] splitData = ((String) ent.getValue()).split(":");
      MaterialData matdata = new MaterialData(
          Material.getMaterial(splitData[0]),
          Byte.parseByte(splitData[1]));
      ingredientMap.put(ent.getKey().charAt(0), matdata);
    }

    //read behaviors
    List<Behavior> behaviors = new ArrayList<>();
    if (map.get("behaviors") != null) {
      Map<String, Object> temp2 = (Map<String, Object>) map.get("behaviors");
      for (Map.Entry<String, Object> beh : temp2.entrySet()) {
        behaviors.add((Behavior) beh.getValue());

      }
    }

    this.recipe = new ShapedRecipe(DarkCraft.namespacedKey, result);
    this.recipe.shape(shape);
    for (Map.Entry<Character, MaterialData> ent : ingredientMap.entrySet()) {
      this.recipe.setIngredient(ent.getKey(), ent.getValue());
    }
    this.withAllBehaviors(behaviors);

  }

  /**
   * Returns a map representation of the object.
   */
  @SuppressWarnings("deprecation")
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("result", getRecipe().getResult().serialize());
    map.put("shape", getRecipe().getShape());

    //Add ingredients
    Map<String, Object> im = new HashMap<>();
    for (Map.Entry<Character, ItemStack> ent : getRecipe().getIngredientMap().entrySet()) {
      String key = ent.getKey().toString();
      String val = ent.getValue().getType().toString() + ":" + ent.getValue().getData().getData();
      im.put(key, val);
    }
    map.put("ingredients", im);

    //add Behaviors
    Map<String, Object> bm = new LinkedHashMap<>();
    for (Behavior behavior : behaviors) {
      bm.put(behavior.getType(), behavior.serialize());
    }
    map.put("behaviors", bm);

    return map;
  }
  //endregion

  //region Getters/Setters

  /**
   * Creates an Advanced recipe and registers it in the game.
   *
   * @param result        The crafting result of the recipe.
   * @param shape         The shape of the crafting grid represented as a string
   *                      of nine characters.
   * @param ingredientMap A Map that defines which character from the shape goes
   *                      with which ingredient item.
   * @return The Advanced recipe.
   */
  public static AdvRecipe createRecipe(ItemStack result, String[] shape,
                                       Map<Character, MaterialData> ingredientMap) {
    return new AdvRecipe(result, shape, ingredientMap);
  }

  /**
   * Returns the wrapper class for the recipe.
   *
   * @param recipe The wrapped recipe
   * @return The AdvRecipe wrapper class for the recipe or null if the recipe wasn't
   * an advanced one.
   */
  public static AdvRecipe getAdvRecipe(Recipe recipe) {
    for (AdvRecipe rec : recipeSet) {
      if (Util.compareRecipes(recipe, rec)) {
        return rec;
      }
    }
    return null;
  }

  public static Set<AdvRecipe> getRecipes() {
    return recipeSet;
  }

  /**
   * Adds a CraftingEvent to the recipe. The order in which the events are added decides in
   * which order they are checked while the crafting process.
   *
   * @param behavior The behavior which is added to the recipe.
   * @return the recipe itself for easy method-chaining.
   */
  public AdvRecipe withBehavior(Behavior behavior) {
    behavior.setParent(this);
    behaviors.add(behavior);
    return this;
  }

  /**
   * Adds a List of CraftingEvents to the recipe. The order of the List will determine in
   * which order the behaviors will be checked in the crafting process.
   *
   * @param behaviors A List of behaviors which are added to the recipe
   * @return the recipe itself for easy method-chaining.
   */
  public AdvRecipe withAllBehaviors(List<Behavior> behaviors) {
    for (Behavior behavior : behaviors) {
      behavior.setParent(this);
    }
    this.behaviors.addAll(behaviors);
    return this;
  }

  /**
   * Registers the Recipe. Without registering an Advanced Recipe it will not be craftable
   * and also won't be shown in the recipe selection views.
   */
  public void register() {
    recipeSet.add(this);
    Bukkit.getServer().addRecipe(this.getRecipe());
  }

  /**
   * Returns the wraped recipe.
   */
  public ShapedRecipe getRecipe() {
    return recipe;
  }

  /**
   * Returns a List with all corresponding events.
   */
  public List<Behavior> getBehaviors() {
    return behaviors;
  }

  //endregion
}
