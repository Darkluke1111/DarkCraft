package de.darkluke1111.darkcraft.recipegui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickAction {

  void act(InventoryClickEvent event, MenuItem item);
}
