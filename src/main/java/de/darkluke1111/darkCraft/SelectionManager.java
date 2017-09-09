package de.darkluke1111.darkCraft;

import de.darkluke1111.darkCraft.data.Selection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager {

    private static Map<Player, Selection> selections = new HashMap<>();

    public void handlePlayerSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() == Material.STICK && event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                Selection selection = new Selection(player);
                selection.setLoc1(event.getClickedBlock().getLocation());
                selections.put(player,selection);
                player.sendMessage("Selected first Location. " + selection.toString());
            }
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(selections.containsKey(player)) {
                    selections.get(player).setLoc2(event.getClickedBlock().getLocation());
                    player.sendMessage("Selected second Location. " + selections.get(player).toString());
                }
            }
        }
    }

    public void removePlayerSelection(Player player) {
        selections.remove(player);
    }

    //region Static Methods
    public static Selection getSelectionOfPlayer(Player player) {
        return selections.get(player);
    }

    public static boolean playerOwnsSelection(Player player) {
        return selections.containsKey(player);
    }
    //endregion
}
