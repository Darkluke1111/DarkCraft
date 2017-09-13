package de.darkluke1111.darkcraft.data.behaviors;

import de.darkluke1111.darkcraft.recipegui.MenuItem;
import de.darkluke1111.darkcraft.recipegui.MenuView;
import de.darkluke1111.darkcraft.recipegui.MenuViewFactory;
import de.darkluke1111.darkcraft.util.Util;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ExplosionBehavior extends Behavior {
  private final String type = "Explosion";
  private final MaterialData icon = new MaterialData(Material.TNT);

  private final String chanceDescription =
      "The chance that the recipe causes an explosion in percent (1-100).";
  private final String radiusDescription =
      "THe radius of the explosion in blocks.";
  private int chance;
  private float radius;

  public ExplosionBehavior() {
    this(100);
  }

  public ExplosionBehavior(int percentage) {
    this.chance = percentage;
    this.radius = 3.0f;
  }

  //region Serialization Stuff
  public ExplosionBehavior(Map<String, Object> map) {
    this.chance = (int) map.get("chance");
  }

  @Override
  public void preCraft(PrepareItemCraftEvent event) {
    ItemStack result = event.getInventory().getResult();
    Util.addLoreLineToItem(result, "Explodechance: " + chance + "%");
  }

  @Override
  public List<ItemStack> postCraft(CraftItemEvent event) {
    if (event.isCancelled()) {
      return new ArrayList<>();
    }
    if (Util.calcChance(chance)) {
      Location location = Util.getCraftingTableLocation(event);
      location.getWorld().createExplosion(location, radius);
    }
    return new ArrayList<>();
  }

  @Override
  public MenuView generateView() {
    MenuView view = MenuViewFactory.createMenuView(2, "Explosion Behavior");
    MenuItem.createItemFromInt("Explosion Chance",
        new MaterialData(Material.TNT),
        chance,
        chanceDescription)
        .addToView(view, 0, 0);
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

  public void setRadius(float radius) {
    this.radius = radius;
  }
  //endregion

  public void setChance(int chance) {
    this.chance = chance;
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("chance", chance);
    return map;
  }
  //endregion
}
