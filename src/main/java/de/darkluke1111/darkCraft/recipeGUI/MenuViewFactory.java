package de.darkluke1111.darkCraft.recipeGUI;

import de.darkluke1111.darkCraft.data.AdvRecipe;
import de.darkluke1111.darkCraft.data.behaviors.Behavior;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.stream.Collectors;

public class MenuViewFactory {


    //region Factory Methods
    /**
     * Creates a new View with the given number of rows (number of columns is always 9).
     */
    public static MenuView createMenuView(int rows, String name) {
        return new MenuView(rows, name);
    }

    /**
     * Creates a MenuView for an advanced Recipe.
     */
    public static MenuView createRecipeView(AdvRecipe advRecipe) {
        final int craftingGridX = 2;
        final int craftingGridY = 0;
        final int resultX = 6;
        final int resultY = 1;

        MenuView recipeView = new MenuView(5, "Recipe view");
        MenuItem[][] craftingGrid = createCraftingGridItems(advRecipe);
        MenuItem resultItem = createResultItem(advRecipe);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                craftingGrid[x][y].addToView(recipeView, x + craftingGridX, y + craftingGridY);
            }
        }
        resultItem.addToView(recipeView, resultX, resultY);

        List<MenuItem> behaviorItems = createsBehaviorItems(recipeView, advRecipe);
        for (int i = 0; i < Math.min(9, behaviorItems.size()); i++) {
            behaviorItems.get(i).addToView(recipeView, i, 4);
        }
        return recipeView;
    }

    /**
     * Creates a view which lets a player select between a set of recipes. If he clicks on a recipe, there will be
     * generated a view for the selected recipe and will be shown to the player.
     */
    public static MenuView createRecipeSelectionView(Set<AdvRecipe> recipes) {
        MenuView recipeSelectionView = new MenuView(3, "Recipe Selection View");

        List<MenuItem> menuItems = new ArrayList<>();
        for (AdvRecipe recipe : recipes) {
            ItemStack result = recipe.getRecipe().getResult();
            Collection<ItemStack> ingredients = recipe.getRecipe().getIngredientMap().values();
            String description = ingredients.stream().map(is -> is.getType().toString()).collect(Collectors.joining(", "));

            MenuItem menuItem = MenuItem.createItem(result.getData(), result.getAmount())
                    .withDescription(description)
                    .withLeftClickAction((InventoryClickEvent e, MenuItem i) ->
                    {
                        MenuViewFactory.createRecipeView(recipe)
                                .setParentView(recipeSelectionView)
                                .setLinkToParent(new MaterialData(Material.REDSTONE_BLOCK), "Back to Recipe Selection")
                                .show(e.getViewers().get(0));

                    });

            menuItems.add(menuItem);
        }
        recipeSelectionView.fillItems(menuItems);
        return recipeSelectionView;
    }

    /**
     * Creates a view for the given Behavior.
     */
    public static MenuView createBehaviorView(Behavior behavior) {
        return behavior.generateView();
    }
    //endregion

    //region private methods
    private static MenuItem[][] createCraftingGridItems(AdvRecipe advRecipe) {
        MenuItem[][] matrix = new MenuItem[3][3];
        Map<Character, ItemStack> ingredientMap = advRecipe.getRecipe().getIngredientMap();
        String[] shape = advRecipe.getRecipe().getShape();

        for (int y = 0; y < 3; y++) {
            String row = shape[y];
            for (int x = 0; x < 3; x++) {
                Character key = row.charAt(x);
                MenuItem menuItem = MenuItem.createItem(ingredientMap.get(key).getData());
                matrix[x][y] = menuItem;
            }
        }
        return matrix;
    }

    private static MenuItem createResultItem(AdvRecipe advRecipe) {
        ItemStack result = advRecipe.getRecipe().getResult();
        return MenuItem.createItem(result.getData(), result.getAmount());
    }

    private static List<MenuItem> createsBehaviorItems(MenuView recipeView, AdvRecipe advRecipe) {
        List<MenuItem> menuItems = new ArrayList<>();
        for (Behavior beh : advRecipe.getBehaviors()) {
            MenuItem item = MenuItem
                    .createItem(beh.getIcon())
                    .withName(beh.getType())
                    .withLeftClickAction((e, i) ->
                    {
                        beh.generateView()
                                .setParentView(recipeView)
                                .setLinkToParent(new MaterialData(Material.REDSTONE_BLOCK), "Back to Recipe View")
                                .show(e.getViewers().get(0));
                    });
            menuItems.add(item);
        }
        return menuItems;
    }
    //endregion
}
