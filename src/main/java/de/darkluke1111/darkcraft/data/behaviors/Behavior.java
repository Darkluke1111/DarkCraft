package de.darkluke1111.darkcraft.data.behaviors;

import de.darkluke1111.darkcraft.data.AdvRecipe;
import de.darkluke1111.darkcraft.recipegui.MenuView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Defines callbacks which are called when a player tries to craft the parent recipe.
 */
public abstract class Behavior {
  private AdvRecipe parent;

  public Behavior(Map<String, Object> map) {

  }

  public Behavior() {

  }

  /**
   * Method to load the class before deserialisation of objects.
   */
  public static void load() {
  }

  /**
   * Callback method which is called before the player crafts the parent recipe.
   * The default Behavior is doing nothing at all.
   */
  public void preCraft(PrepareItemCraftEvent event) {
    //Default behavior: Do nothing.
  }

  /**
   * Callback method which is called right after the parent recipe was crafted.
   * It is also possible to define additional results by returning a List with them.
   *
   * @return List of additional results.
   */
  public List<ItemStack> postCraft(CraftItemEvent event) {
    return new ArrayList<>();
  }

  /**
   * Sets the parent recipe.
   */
  public void setParent(AdvRecipe parent) {
    this.parent = parent;
  }

  /**
   * Generates a MenuView for the Behavior where all important Attributes are shown.
   */
  public abstract MenuView generateView();

  /**
   * Returns the Icon with which the behavior is shown in a MenuView.
   */
  public abstract MaterialData getIcon();

  /**
   * Returns the name of the event type.
   *
   * @return The name.
   */
  public abstract String getType();

  public abstract Map<String, Object> serialize();

}
