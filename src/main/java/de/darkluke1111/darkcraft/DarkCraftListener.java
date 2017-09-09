package de.darkluke1111.darkcraft;

import de.darkluke1111.darkcraft.recipegui.MenuView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DarkCraftListener implements Listener {

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

  /**
   * Passes InventoryDragEvents to the correspunding view if thre is a view associated
   * with the inventory.
   */
  @EventHandler
  public void onInventoryDragInteract(InventoryDragEvent event) {
    if (MenuView.isMenuViewInventory(event.getInventory())) {
      MenuView.getCorrespondingView(event.getInventory()).handleInteraction(event);
    }
  }

  /**
   * Passes InventoryClickEvents to the correspunding view if thre is a view associated
   * with the inventory.
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (MenuView.isMenuViewInventory(event.getInventory())) {
      MenuView.getCorrespondingView(event.getInventory()).handleInteraction(event);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    MenuView.handleViewClose(event.getInventory());
  }


}
