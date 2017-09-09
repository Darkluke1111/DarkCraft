package de.darkluke1111.darkCraft.recipeGUI;

import de.darkluke1111.darkCraft.DarkCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuView {
    private static final int columns = 9;
    private static Map<Inventory, MenuView> viewMap = new HashMap<>();

    private final int rows;
    private MenuView parentView;
    private String name = "Unnamed MenuView";
    private MenuItem[] items;

    /**
     * Fills in a list of items into the view (Filled from top left corner to bottom right corner).
     * Returns the items which did not fit into the view and were therefore not added.
     *
     * @param itemList A list of MenuItems
     * @return The restlist
     */
    public List<MenuItem> fillItems(List<MenuItem> itemList) {
        int i = 0;
        List<MenuItem> remainingItems = new ArrayList<>();
        for (MenuItem item : itemList) {
            while (i < items.length) {
                if (!itemAtIndex(i)) {
                    setItemAtIndex(i, item);
                    break;
                }
            }
            if (i == items.length) {
                remainingItems.add(item);
            }
            i++;
        }
        return remainingItems;
    }

    /**
     * Shows the View to the given Player.
     */
    public void show(HumanEntity viewer) {
        Inventory inv = Bukkit.createInventory(null, getSize(), name);
        inv.setContents(getContentArray());
        viewMap.put(inv, this);
        viewer.openInventory(inv);
    }

    //region Static Methods
    /**
     * Closes the view which goes with the given Inventory.
     */
    public static void handleViewClose(Inventory inventory) {
        viewMap.remove(inventory);
    }

    /**
     * Gets the view which goes with the specified Inventory.
     */
    public static MenuView getCorrespondingView(Inventory inventory) {
        return viewMap.get(inventory);
    }

    /**
     * Checks if the given Inventory contains a MenuView.
     * @return true if the inventory contains a MenuView, false otherwise
     */
    public static boolean isMenuViewInventory(Inventory inventory) {
        return viewMap.containsKey(inventory);
    }
    //endregion

    //region Getter/Setter
    /**
     * Sets the name of the View.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the View.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Slotcount of the MenuView.
     */
    public int getSize() {
        return items.length;
    }

    /**
     * Gets the MenuItem at the given coordinates (x from left to right, y from top to bottom).
     */
    public MenuItem getItemAt(int x, int y) {
        if (y * columns + x >= items.length) {
            throw new IndexOutOfBoundsException("Tried accessing Position (" + x + "|" + y + ") but Inventory is only 9x" + rows);
        }
        return items[y * columns + x];
    }

    /**
     * Gets the MenuItem at the given index. Index is counted like slotnumbers in inventories.
     */
    public MenuItem getItemAtIndex(int index) {
        if (index >= items.length) {
            throw new IndexOutOfBoundsException("Tried accessing Index " + index + " but Inventory is only " + items.length + " big.");
        }
        return items[index];
    }

    /**
     * Checks whether there is a MenuItem at the given coordinates (x from left to right, y from top to bottom).
     */
    public boolean itemAtSlot(int x, int y) {

        return getItemAt(x, y) != null;
    }

    /**
     * Checks whether there is a MenuItem at the given index. Index is counted like slotnumbers in inventories.
     */
    public boolean itemAtIndex(int index) {
        return getItemAtIndex(index) != null;
    }

    /**
     * Sets a MenuItem to the given coordinates in the MenuView (x from left to right, y from top to bottom).
     */
    public MenuView setItemAt(int x, int y, MenuItem item) {
        if (y * columns + x >= items.length) {
            throw new IndexOutOfBoundsException("Tried accessing Position (" + x + "|" + y + ") but Inventory is only 9x" + rows);
        }
        items[y * columns + x] = item;
        return this;
    }

    /**
     * Sets a MenuItem to the given index in the MenuView. Index is counted like slotnumbers in inventories.
     */
    public MenuView setItemAtIndex(int index, MenuItem item) {
        if (index <= items.length) {
            items[index] = item;
        } else {
            throw new IndexOutOfBoundsException("Tried accessing Index " + index + " but Inventory is only " + items.length + " big.");
        }
        return this;
    }

    /**
     * Sets a parent view for the current view which enables setting "back-buttons" with the "setLinkToParent" method.
     */
    public MenuView setParentView(MenuView parentView) {
        this.parentView = parentView;
        return this;
    }

    /**
     * Inserts a "back-button" into the view witch closes the current view on left-click and shows the parent view to
     * the same player. The Button is added to the bottom-right corner of the current view, if this slot is empty,
     * otherwise the button won't be added and a warning will be printed instead into console.
     */
    public MenuView setLinkToParent(MaterialData icon, String linkName) {
        if(itemAtSlot(columns - 1 , rows - 1))  {
            DarkCraft.instance.getLogger().warning("Wasn't able to establish link icon because slot was already full!");
            return this;
        }
        if(parentView == null) {
            throw new NullPointerException("Tried to set a link to a non existent parent view");
        }
        MenuItem.createItem(icon).withName(linkName).withLeftClickAction((e,i) -> {
            parentView.show((Player) e.getViewers().get(0));
        }).addToView(this, columns - 1 , rows - 1);
        return this;
    }
    //endregion

    /**
     * Handles any Playerinteraction with the menu.
     */
    public void handleInteraction(InventoryInteractEvent iEvent) {
        iEvent.setCancelled(true);
        if (!(iEvent instanceof InventoryClickEvent)) return;
        InventoryClickEvent event = (InventoryClickEvent) iEvent;

        int slot = event.getRawSlot();
        if(slot >= getSize() || slot < 0) return;
        if (itemAtIndex(slot)) {
            getItemAtIndex(slot).handleItemClick(event);
        }
    }

    //region Private Methods
    MenuView(int rows, String name) {
        this.name = name;
        this.rows = rows;
        this.items = new MenuItem[columns * rows];
    }

    private ItemStack[] getContentArray() {
        ItemStack[] content = new ItemStack[getSize()];
        for (int i = 0; i < getSize(); i++) {
            if (items[i] != null) {
                content[i] = items[i].getItem();
            } else {
                content[i] = null;
            }
        }
        return content;
    }
    //endregion
}
