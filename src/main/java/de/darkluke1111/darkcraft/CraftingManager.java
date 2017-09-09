package de.darkluke1111.darkcraft;

import de.darkluke1111.darkcraft.data.AdvRecipe;
import de.darkluke1111.darkcraft.data.behaviors.Behavior;
import java.util.List;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;


public class CraftingManager {

  /**
   * Handles the event when a player prepares to craft an advanced recipe.
   */
  public void handleAdvancedRecipePreparation(PrepareItemCraftEvent event) {
    Recipe recipe = event.getRecipe();

    //Getting the wrapperclass for the recipe (if it exists)
    AdvRecipe advRecipe = AdvRecipe.getAdvRecipe(recipe);
    //Test whether the recipe was an anvanced one, return if not
    if (advRecipe == null) {
      return;
    }

    //Calling callbacks
    List<Behavior> behaviors = advRecipe.getBehaviors();
    for (Behavior bhv : behaviors) {
      bhv.preCraft(event);
    }
  }

  /**
   * handles the event when a player crafts an advanced recipe.
   */
  public void handleAdvancedRecipeCrafting(CraftItemEvent event) {
    //Getting the wrapperclass for the recipe (if it exists)
    Recipe recipe = event.getRecipe();
    AdvRecipe advRecipe = AdvRecipe.getAdvRecipe(recipe);
    //Test whether the recipe was an anvanced one, return if not
    if (advRecipe == null) {
      return;
    }

    //Calling callbacks
    List<Behavior> behaviors = advRecipe.getBehaviors();
    for (Behavior bhv : behaviors) {
      bhv.postCraft(event);
    }
  }
}
