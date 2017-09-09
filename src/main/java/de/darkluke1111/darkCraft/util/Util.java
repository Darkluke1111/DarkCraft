package de.darkluke1111.darkCraft.util;

import de.darkluke1111.darkCraft.data.AdvRecipe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Util {

    /**
     * Returns true with the specified probability.
     * @param percentage The probability. Should be between 0 and 100.
     * @return Randomly true or false.
     */
    public static boolean calcChance(int percentage) {
        int rand = (int) (Math.random() * 100);
        return percentage > rand;
    }

    public static void addLoreLineToItem(ItemStack is, String line) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if(lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(line);
        meta.setLore(lore);
        is.setItemMeta(meta);
    }

    /**
     * Get the amount of items the player had just crafted.
     * This method will take into consideration shift clicking &
     * the amount of inventory space the player has left.
     *
     * @param event CraftItemEvent
     * @return int: actual crafted item amount
     */
    public static int getCraftAmount(CraftItemEvent event) {

        //if event is cancelled, return 0
        if (event.isCancelled()) {
            return 0;
        }

        Player p = (Player) event.getWhoClicked();

        //If shift clicked start complicated calculations
        if (event.isShiftClick()) {
            int itemsChecked = 0;
            int possibleCreations = 1;
            int amountCanBeMade = 0;

            //Check how many items can be crafted with the materials in the inventory.
            // Save Amount into amountOfItems
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }
            int amountOfItems = event.getRecipe().getResult().getAmount() * possibleCreations;

            //Check how much emtpy Space there is in the inventory to store the results
            ItemStack i = event.getRecipe().getResult();
            for (int s = 0; s <= event.getInventory().getSize(); s++) {
                ItemStack test = p.getInventory().getItem(s);
                if (test == null || test.getType() == Material.AIR) {
                    amountCanBeMade += i.getMaxStackSize();
                    continue;
                }
                if (test.isSimilar(i)) {
                    amountCanBeMade += i.getMaxStackSize() - test.getAmount();
                }
            }

            //Return the minimum from the two calculated values
            return amountOfItems > amountCanBeMade ? amountCanBeMade : amountOfItems;
        } else {
            //If not shift clicked just return teh resultamount
            return event.getRecipe().getResult().getAmount();
        }
    }

    /**
     * Gets the location of the Craftingtable used to craft the item.
     */
    public static Location getCraftingTableLocation(InventoryEvent event) {
        return (getCraftingPlayer(event)).getTargetBlock((Set<Material>) null, 10).getLocation();
    }

    /**
     * Gets the Player involved in the crafting process.
     * @param event
     * @return The crafting player
     */
    public static Player getCraftingPlayer(InventoryEvent event) {
        return (Player) event.getViewers().get(0);
    }

    public static boolean compareRecipes(Recipe o1, AdvRecipe o2) {
        if(!(o1 instanceof ShapedRecipe)) return false;
        ShapedRecipe r1 = (ShapedRecipe) o1;
        ShapedRecipe r2 = o2.getRecipe();
        ItemStack i1 = r1.getResult();
        ItemStack i2 = r2.getResult();
        if(!i1.equals(i2)) {
            return false;
        } else {
            String[] s1 = r1.getShape();
            String[] s2 = r2.getShape();

            if(s1.length != s2.length) {
                return false;
            } else {
                for(int i = 0 ; i < s1.length ; i++) {
                    for(int j = 0 ; j < s1[i].length(); j++) {
                        ItemStack m1 = r1.getIngredientMap().get(s1[i].charAt(j));
                        ItemStack m2 = r2.getIngredientMap().get(s2[i].charAt(j));
                        if(m1 == null && m2 == null) {
                            continue;
                        }
                        if(m1 == null && m2.getType() == Material.AIR) {
                            continue;
                        }
                        if(m1 == null || m2 == null) {
                            return false;
                        }
                        if(!m1.equals(m2)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }

    public static List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        while(text.length() > 0) {
            for(int i = 50 ; i >= 0 ; i--) {
                if(i >= text.length()) {
                    lines.add(text);
                    text = "";
                    break;
                }
                if(text.charAt(i) == ' ') {
                    lines.add(text.substring(0, i));
                    text = text.substring(i + 1);
                    break;
                }
                if(i == 0) {
                    lines.add(text);
                    text = "";
                    break;
                }
            }
        }
        return lines;
    }
}
