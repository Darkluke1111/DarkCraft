package de.darkluke1111.darkCraft.data.behaviors;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LightningBehavior extends Behavior {
    private final String type = "Lightning";
    private final MaterialData icon = new MaterialData(Material.FIRE);


    private final String chanceDescription =
            "The chance thet a lightening strike strikes into the crafting table in percent (1-100).";
    private int chance;

    public LightningBehavior() {
        this(100);
    }

    public LightningBehavior(int chance) {
        this.chance = chance;
    }

    @Override
    public void preCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        Util.addLoreLineToItem(result, "Lightningchance: " + chance + "%");
    }

    @Override
    public List<ItemStack> postCraft(CraftItemEvent event) {
        if (event.isCancelled()) return new ArrayList<>();
        if (Util.calcChance(chance)) {
            Location location = Util.getCraftingTableLocation(event);
            location.getWorld().strikeLightning(location);
        }
        return new ArrayList<>();
    }

    @Override
    public MenuView generateView() {
        MenuView view = MenuViewFactory.createMenuView(2, "Lightning Behavior");
        MenuItem.createItemFromInt("Lightning Chance", new MaterialData(Material.FIRE), chance, chanceDescription).addToView(view, 0, 0);
        return view;
    }

    @Override
    public MaterialData getIcon() {
        return icon;
    }

    //region Getter/Setter
    @Override
    public String getType() {
        return type;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
    //endregion

    //region Serialisation Stuff
    public LightningBehavior(Map<String, Object> map) {
        this.chance = (int) map.get("chance");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(this.getClass()));
        map.put("chance", chance);
        return map;
    }
    //endregion
}