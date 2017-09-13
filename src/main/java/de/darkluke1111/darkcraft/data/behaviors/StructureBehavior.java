package de.darkluke1111.darkcraft.data.behaviors;

import de.darkluke1111.darkcraft.data.Structure;
import de.darkluke1111.darkcraft.recipegui.MenuItem;
import de.darkluke1111.darkcraft.recipegui.MenuView;
import de.darkluke1111.darkcraft.recipegui.MenuViewFactory;
import de.darkluke1111.darkcraft.util.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class StructureBehavior extends Behavior {
  private final String type = "Structure";
  private final MaterialData icon = new MaterialData(Material.WORKBENCH);

  private final String structNamesDescription =
      "All crafting Structures with which the Recipe can be crafted.";
  private Set<String> structNames;

  public StructureBehavior(Set<String> structNames) {
    this.structNames = structNames;
  }

  //region Serialization Stuff
  @SuppressWarnings("unchecked")
  public StructureBehavior(Map<String, Object> map) {
    structNames = new HashSet<>((List<String>) map.get("structures"));
  }

  @Override
  public void preCraft(PrepareItemCraftEvent event) {
    if (!testForStructures(Util.getCraftingTableLocation(event))) {
      ItemStack item = new ItemStack(Material.BARRIER);
      Util.addLoreLineToItem(item, "You don't have the required Structure!");
      event.getInventory().setResult(item);
    }
  }

  @Override
  public List<ItemStack> postCraft(CraftItemEvent event) {
    if (!testForStructures(Util.getCraftingTableLocation(event))) {
      event.setCancelled(true);
    }
    return new ArrayList<>();
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public MenuView generateView() {
    MenuView view = MenuViewFactory.createMenuView(2, "Structure Behavior");
    MenuItem.createItem(Material.WORKBENCH)
        .withName("Possible crafting structures")
        .withDescription(structNames.toString())
        .addToView(view, 0, 0);
    return view;
  }

  @Override
  public MaterialData getIcon() {
    return icon;
  }
  //endregion

  //region Private Methods
  private boolean testForStructures(Location location) {
    for (String name : structNames) {
      Structure structure = Structure.getStructureForName(name);
      if (structure.testForStructure(location)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("structures", new ArrayList<>(structNames));

    return map;
  }
  //endregion
}
