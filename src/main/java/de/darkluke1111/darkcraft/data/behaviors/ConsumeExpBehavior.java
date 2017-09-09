package de.darkluke1111.darkcraft.data.behaviors;

import de.darkluke1111.darkcraft.recipegui.MenuItem;
import de.darkluke1111.darkcraft.recipegui.MenuView;
import de.darkluke1111.darkcraft.recipegui.MenuViewFactory;
import de.darkluke1111.darkcraft.util.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ConsumeExpBehavior extends Behavior {
  private final String type = "ConsumeExp";
  private final MaterialData icon = new MaterialData(Material.EXP_BOTTLE);

  private final String consumeExpDescription =
      "The amount of Exp which is consumed (Measured in expOrbs, "
          + "not in levels.";

  private final String expPerItemDescription =
      "Determines whether the exp is scaled with the number of items "
          + "crafted in one crafting-process.";

  private int consumedExp;
  private boolean expPerItem;

  public ConsumeExpBehavior(int consumedExp, boolean expPerItem) {
    this.consumedExp = consumedExp;
    this.expPerItem = expPerItem;
  }

  public ConsumeExpBehavior(int consumedExp) {
    this(consumedExp, true);
  }

  //region Serialisation Stuff
  public ConsumeExpBehavior(Map<String, Object> map) {
    this.consumedExp = (int) map.get("consumedExp");
    this.expPerItem = (boolean) map.get("expPerItem");
  }

  private int getTotalExp(int itemAmount) {
    if (!expPerItem) {
      return consumedExp;
    } else {
      return itemAmount * consumedExp;
    }
  }

  @Override
  public void preCraft(PrepareItemCraftEvent event) {
    ItemStack result = event.getInventory().getResult();
    if (expPerItem) {
      Util.addLoreLineToItem(result, "Exp per Item: " + consumedExp);
    } else {
      Util.addLoreLineToItem(result, "Exp: " + consumedExp);
    }
  }

  @Override
  public List<ItemStack> postCraft(CraftItemEvent event) {
    if (event.isCancelled()) {
      return new ArrayList<>();
    }
    Player player = Util.getCraftingPlayer(event);
    int amount = Util.getCraftAmount(event);
    if (player.getTotalExperience() <= getTotalExp(amount)) {
      event.setCancelled(true);
    } else {
      player.setTotalExperience(player.getTotalExperience() - getTotalExp(amount));
    }
    return new ArrayList<>();
  }

  @Override
  public MenuView generateView() {
    MenuView view = MenuViewFactory.createMenuView(2, "Consume Experience Behavior");

    MenuItem.createItemFromInt("Consumed Experience",
        new MaterialData(Material.EXP_BOTTLE),
        consumedExp,
        consumeExpDescription)
        .addToView(view, 0, 0);

    MenuItem.createItemFromBoolean("Exp per Item",
        expPerItem,
        expPerItemDescription)
        .addToView(view, 1, 0);

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
  //endregion

  public void setExpPerItem(boolean healthPerItem) {
    this.expPerItem = expPerItem;
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
        ConfigurationSerialization.getAlias(this.getClass()));
    map.put("consumedExp", consumedExp);
    map.put("expPerItem", expPerItem);
    return map;
  }
  //endregion
}
