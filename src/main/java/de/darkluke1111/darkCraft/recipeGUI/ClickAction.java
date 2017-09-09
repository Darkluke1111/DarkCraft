package de.darkluke1111.darkCraft.recipeGUI;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public interface ClickAction {

    public void act(InventoryClickEvent event, MenuItem item);
}
