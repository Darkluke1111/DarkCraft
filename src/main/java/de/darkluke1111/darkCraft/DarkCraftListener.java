package de.darkluke1111.darkCraft;

import de.darkluke1111.darkCraft.recipeGUI.MenuView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DarkCraftListener implements Listener{

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        DarkCraft.instance.getSelectionManager().handlePlayerSelection(event);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        DarkCraft.instance.getSelectionManager().removePlayerSelection(event.getPlayer());
    }

    @EventHandler
    public void onCrafting(CraftItemEvent event) {
        DarkCraft.instance.getCraftingManager().handleAdvancedRecipeCrafting(event);
    }

    @EventHandler
    public void beforeCrafting(PrepareItemCraftEvent event) {
        DarkCraft.instance.getCraftingManager().handleAdvancedRecipePreparation(event);
    }

    @EventHandler
    public void onInventoryDragInteract(InventoryDragEvent event) {
        if(MenuView.isMenuViewInventory(event.getInventory())) {
            MenuView.getCorrespondingView(event.getInventory()).handleInteraction(event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(MenuView.isMenuViewInventory(event.getInventory())) {
            MenuView.getCorrespondingView(event.getInventory()).handleInteraction(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        MenuView.handleViewClose(event.getInventory());
    }


}
