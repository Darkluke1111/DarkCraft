package de.darkluke1111.darkCraft;

import de.darkluke1111.darkCraft.data.AdvRecipe;
import de.darkluke1111.darkCraft.data.behaviors.Behavior;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

import java.util.List;

public class CraftingManager {

    public void handleAdvancedRecipePreparation(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        //Getting the wrapperclass for the recipe (if it exists)
        AdvRecipe advRecipe = AdvRecipe.getAdvRecipe(recipe);
        //Test whether the recipe was an anvanced one, return if not
        if (advRecipe == null) return;

        //Calling callbacks
        List<Behavior> behaviors = advRecipe.getBehaviors();
        for (Behavior bhv : behaviors) {
            bhv.preCraft(event);
        }
    }

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
