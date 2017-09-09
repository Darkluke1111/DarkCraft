package de.darkluke1111.darkCraft.data.behaviors;

import de.darkluke1111.darkCraft.data.Structure;
import de.darkluke1111.darkCraft.recipeGUI.MenuItem;
import de.darkluke1111.darkCraft.recipeGUI.MenuView;
import de.darkluke1111.darkCraft.recipeGUI.MenuViewFactory;
import de.darkluke1111.darkCraft.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public class StructureBehavior extends Behavior {
    private final String type = "Structure";
    private final MaterialData icon = new MaterialData(Material.WORKBENCH);

    private final String structNamesDescription =
            "All crafting Structures with which the Recipe can be crafted.";
    private Set<String> structNames;

    public StructureBehavior(Set<String> structNames) {
        this.structNames = structNames;
    }

    @Override
    public void preCraft(PrepareItemCraftEvent event) {
        if(!testForStructures(Util.getCraftingTableLocation(event))) {
            ItemStack item = new ItemStack(Material.BARRIER);
            Util.addLoreLineToItem(item,"You don't have the required Structure!");
            event.getInventory().setResult(item);
        }
    }

    @Override
    public List<ItemStack> postCraft(CraftItemEvent event) {
        if(!testForStructures(Util.getCraftingTableLocation(event))) {
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
        MenuView view = MenuViewFactory.createMenuView(2,"Structure Behavior");
        MenuItem.createItem(Material.WORKBENCH).withName("Possible crafting structures").withDescription(structNames.toString()).addToView(view,0,0);
        return view;
    }

    @Override
    public MaterialData getIcon() {
        return icon;
    }

    //region Private Methods
    private boolean testForStructures(Location location) {
        for(String name : structNames) {
            Structure structure = Structure.getStructureForName(name);
            if(structure.testForStructure(location)) {
                return true;
            }
        }
        return false;
    }
    //endregion

    //region Serialisation Stuff
    public StructureBehavior(Map<String, Object> map) {
        structNames = new HashSet<>((List<String>) map.get("structures"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(this.getClass()));
        map.put("structures", new ArrayList<>(structNames));

        return map;
    }
    //endregion
}
