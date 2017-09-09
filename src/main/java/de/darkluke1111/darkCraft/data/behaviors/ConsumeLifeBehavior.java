package de.darkluke1111.darkCraft.data.behaviors;

import de.darkluke1111.darkCraft.recipeGUI.MenuItem;
import de.darkluke1111.darkCraft.recipeGUI.MenuView;
import de.darkluke1111.darkCraft.recipeGUI.MenuViewFactory;
import de.darkluke1111.darkCraft.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsumeLifeBehavior extends Behavior {
    private final String type = "ConsumeLife";
    private final MaterialData icon = new MaterialData(Material.DIAMOND_SWORD);

    private final String consumedLifeDescription =
            "Amount of life which is consumed. Measured in half hearts.";
    private int consumedLife;

    private final String lifePerItemDescription =
            "Determines whether the consumed life scales with the number of items crafted in one crafting-process.";
    private boolean lifePerItem;

    private final String preventDeathDescription =
            "Determines whether the player can die when crafting this recipe or whether the crafting-process is automatically canceled instead.";
    private boolean preventDeath;

    public ConsumeLifeBehavior() {
        this(1);

    }

    public ConsumeLifeBehavior(int consumedLife) {
        this.consumedLife = consumedLife;
        this.preventDeath = false;
        this.lifePerItem = true;
    }

    @Override
    public void preCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if(lifePerItem) {
            Util.addLoreLineToItem(result, "Life per Item: " + consumedLife);
        } else {
            Util.addLoreLineToItem(result, "Life: " + consumedLife);
        }
    }

    @Override
    public List<ItemStack> postCraft(CraftItemEvent event) {
        if(event.isCancelled()) return  new ArrayList<>();
        Player player = Util.getCraftingPlayer(event);
        Location location = Util.getCraftingTableLocation(event);
        int amount = Util.getCraftAmount(event);

        if (preventDeath && player.getHealth() <= getTotalLife(amount)) {
            event.setCancelled(true);
        } else {
            player.damage(getTotalLife(amount));
        }
        return new ArrayList<>();
    }

    @Override
    public MenuView generateView() {
        MenuView view = MenuViewFactory.createMenuView(2,"Consume Life Behavior");
        MenuItem.createItemFromInt("Consumed Life", new MaterialData(Material.DIAMOND_SWORD), consumedLife, consumedLifeDescription).addToView(view, 0,0);
        MenuItem.createItemFromBoolean("Life per Item", lifePerItem, lifePerItemDescription).addToView(view, 1,0);
        MenuItem.createItemFromBoolean("Prevent Death", preventDeath, preventDeathDescription).addToView(view, 2,0);
        return view;
    }

    @Override
    public MaterialData getIcon() {
        return icon;
    }

    //region Setter/Getter
    @Override
    public String getType() {
        return type;
    }

    public void setLifePerItem(boolean lifePerItem) {
        this.lifePerItem = lifePerItem;
    }

    public void setPreventDeath(boolean preventDeath) {
        this.preventDeath = preventDeath;
    }
    //endregion

    //region Private-Methods
    private int getTotalLife(int itemAmount) {
        if (!lifePerItem) {
            return consumedLife;
        } else {
            return itemAmount * consumedLife;
        }
    }
    //endregion

    //region Serialisation Stuff
    public ConsumeLifeBehavior(Map<String, Object> map) {
        this.consumedLife = (int) map.get("consumedLife");
        this.lifePerItem = (boolean) map.get("lifePerItem");
        this.preventDeath = (boolean) map.get("preventDeath");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(this.getClass()));
        map.put("consumedLife", consumedLife);
        map.put("lifePerItem", lifePerItem);
        map.put("preventDeath", preventDeath);
        return map;
    }
    //endregion
}