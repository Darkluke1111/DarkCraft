package de.darkluke1111.darkcraft.recipegui;

import de.darkluke1111.darkcraft.util.Util;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class MenuItem {
  private boolean finished;
  private MenuView parent;
  private ItemStack item;
  private ClickAction rightAction;
  private ClickAction leftAction;

  private MenuItem(ItemStack item) {
    this.item = item;
  }

  //region Construction Methods

  /**
   * Creates a MenuItem which represents a boolean value.
   * The value can have a name and a description which are then
   * shown on the MenuItem.
   */
  @SuppressWarnings("deprecation")
  public static MenuItem createItemFromBoolean(String name, boolean value, String description) {
    MaterialData mat;
    if (value) {
      mat = new MaterialData(Material.WOOL, (byte) 13);
    } else {
      mat = new MaterialData(Material.WOOL, (byte) 14);
    }
    return createItem(mat)
        .withName(name)
        .withDescription(Boolean.toString(value))
        .withAdditionalDescription(description);
  }

  /**
   * Creates a MenuItem which represents a integer value. The value
   * can have a name and a description which are then shown on the
   * MenuItem. The integer value is also shown as itemcount, but due
   * to Bukkit limitations only if it is between 1 and 64.
   */
  public static MenuItem createItemFromInt(String name, MaterialData icon,
                                           int value, String description) {
    return createItem(icon, value)
        .withName(name)
        .withDescription(Integer.toString(value))
        .withAdditionalDescription(description);
  }

  /**
   * Creates a MenuItem with the given icon.
   */
  public static MenuItem createItem(Material mat) {
    ItemStack itemStack = new MaterialData(mat).toItemStack(1);
    return new MenuItem(itemStack);
  }

  /**
   * Creates a MenuItem with the given icon.
   */
  public static MenuItem createItem(MaterialData mat) {
    ItemStack itemStack = mat.toItemStack(1);
    return new MenuItem(itemStack);
  }

  /**
   * Creates a MenuItem with the given icon and itemcount-value.
   */
  public static MenuItem createItem(MaterialData mat, int number) {
    MenuItem item = createItem(mat);
    item.getItem().setAmount(number);
    return item;
  }

  /**
   * Adds a custom name to the MenuItem.
   *
   * @return itself for easy method chaining.
   */
  public MenuItem withName(String name) {
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    item.setItemMeta(meta);
    return this;
  }

  /**
   * Adds a description to the MenuItem. Description String is
   * automatically devided into a list of smaller Strings for
   * formatting.
   *
   * @return itself for easy method chaining.
   */
  public MenuItem withDescription(String description) {
    ItemMeta meta = item.getItemMeta();
    meta.setLore(Util.splitLines(description));
    item.setItemMeta(meta);
    return this;
  }

  /**
   * Adds more description to a MenuItem which already has a description.
   * Description String is automatically devided into a list of smaller
   * Strings for formatting.
   *
   * @return itself for easy method chaining.
   */
  public MenuItem withAdditionalDescription(String description) {
    ItemMeta meta = item.getItemMeta();
    List<String> lore = meta.getLore();
    lore.addAll(Util.splitLines(description));
    meta.setLore(lore);
    item.setItemMeta(meta);
    return this;
  }

  /**
   * Adds a function, which is called when a player right-clicks the MenuItem.
   *
   * @return itself for easy method chaining.
   */
  public MenuItem withRightClickAction(ClickAction rightAction) {
    this.rightAction = rightAction;
    return this;
  }

  /**
   * Adds a function, which is called when a player left-clicks the MenuItem.
   *
   * @return itself for easy method chaining.
   */
  public MenuItem withLeftClickAction(ClickAction leftAction) {
    this.leftAction = leftAction;
    return this;
  }

  /**
   * Adds the MenuItem to the given View at the given coordinates
   * (x from left to right, y from top to bottom).
   */
  public void addToView(MenuView view, int xpos, int ypos) {
    this.parent = view;
    view.setItemAt(xpos, ypos, this);
  }
  //endregion

  //region Click Handling

  /**
   * Handles click Events on this item.
   */
  public void handleItemClick(InventoryClickEvent event) {
    if (event.isLeftClick() && leftAction != null) {
      leftAction.act(event, this);
    }
    if (event.isRightClick() && rightAction != null) {
      rightAction.act(event, this);
    }
  }
  //endregion

  //region Getter

  /**
   * Gets the icon of the MenuItem as ItemStack.
   */
  public ItemStack getItem() {
    return item;
  }

  /**
   * Gets the view which contains the MenuItem.
   */
  public MenuView getParent() {
    return parent;
  }
  //endregion
}
