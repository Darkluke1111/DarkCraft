package de.darkluke1111.darkcraft;

import de.darkluke1111.darkcraft.data.AdvRecipe;
import de.darkluke1111.darkcraft.data.PersistenceManager;
import de.darkluke1111.darkcraft.data.PersistenceSerialisazionException;
import de.darkluke1111.darkcraft.data.Structure;
import de.darkluke1111.darkcraft.data.behaviors.ConsumeExpBehavior;
import de.darkluke1111.darkcraft.data.behaviors.ConsumeLifeBehavior;
import de.darkluke1111.darkcraft.data.behaviors.ExplosionBehavior;
import de.darkluke1111.darkcraft.data.behaviors.LightningBehavior;
import de.darkluke1111.darkcraft.data.behaviors.StructureBehavior;
import de.darkluke1111.darkcraft.recipegui.MenuViewFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class DarkCraft extends JavaPlugin {
  public static NamespacedKey namespacedKey;
  public static DarkCraft instance;
  private CraftingManager craftingManager;
  private SelectionManager selectionManager;
  private DarkCraftListener listener;
  private PersistenceManager persistenceManager;

  @Override
  public void onEnable() {
    namespacedKey = new NamespacedKey(this, this.getDescription().getName());
    instance = this;
    craftingManager = new CraftingManager();
    selectionManager = new SelectionManager();
    listener = new DarkCraftListener();
    persistenceManager = new PersistenceManager();

    saveDefaultConfig();
    saveDefaultRecipes();
    saveDefaultStructures();
    saveReadme();

    Bukkit.getPluginManager().registerEvents(listener, this);

    importRecipesFromConfig();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    //saveStruct
    if (command.getName().equalsIgnoreCase("saveStruct")) {
      if (!(sender instanceof Player)) {
        return true;
      }
      Player player = (Player) sender;
      if (args.length < 1) {
        return false;
      }
      if (SelectionManager.playerOwnsSelection(player)) {
        SelectionManager.getSelectionOfPlayer(player).convertToStructure(args[0]);
        player.sendMessage("Structure was savend from Selection.");
      }
    }
    //pasteStruct
    if (command.getName().equalsIgnoreCase("pasteStruct")) {
      if (!(sender instanceof Player)) {
        return true;
      }
      Player player = (Player) sender;
      if (args.length < 1) {
        return false;
      }
      Location pos = player.getLocation();
      Structure.getStructureForName(args[0]).placeStructure(pos);
    }
    //viewRecipes
    if (command.getName().equalsIgnoreCase(("viewRecipes"))) {
      if (!(sender instanceof Player)) {
        return true;
      }
      Player player = (Player) sender;
      MenuViewFactory.createRecipeSelectionView(AdvRecipe.getRecipes()).show(player);
    }
    //reloadStructures
    if (command.getName().equalsIgnoreCase(("reloadRecipes"))) {
      Structure.clearStructureCache();
    }
    return true;
  }

  private void loadBehaviors() {
    ConsumeExpBehavior.load();
    ConsumeLifeBehavior.load();
    ExplosionBehavior.load();
    LightningBehavior.load();
    StructureBehavior.load();

  }

  @SuppressWarnings("unchecked")
  private void saveDefaultRecipes() {
    List<String> recipeFileNames = (List<String>) getConfig().get("recipe_files");
    for (String recipeFileName : recipeFileNames) {
      if (!new File(getDataFolder(), "recipe_" + recipeFileName + ".yml").exists()) {
        saveResource("recipe_" + recipeFileName + ".yml", false);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void saveDefaultStructures() {
    List<String> structureFileNames = (List<String>) getConfig().get("structure_files");
    for (String structureFileName : structureFileNames) {
      if (!new File(getDataFolder(), "recipe_" + structureFileName + ".yml").exists()) {
        saveResource("structure_" + structureFileName + ".yml", false);
      }
    }
  }

  private void saveReadme() {
    if (!new File(getDataFolder(), "readme.txt").exists()) {
      saveResource("readme.txt", false);
    }
  }

  @SuppressWarnings("unchecked")
  private void importRecipesFromConfig() {
    Object obj = getConfig().get("recipe_files");
    List<String> recipeFiles = new ArrayList<>();
    if (!(obj instanceof List)) {
      getLogger().warning("Couldn't find any recipeFiles specified in the config");
    } else {
      try {
        recipeFiles = (List<String>) obj;
        System.out.println(recipeFiles);
      } catch (ClassCastException exception) {
        getLogger().warning("Couldn't read recipe filenames. Is the config file corrupt?");
      }
    }
    recipeFiles.forEach(this::importRecipeFile);

  }

  private void importRecipeFile(String dataFile) {
    File file = new File(getDataFolder(), "recipe_" + dataFile + ".yml");
    try {
      persistenceManager.loadRecipesFromFile(file);
    } catch (PersistenceSerialisazionException e) {
      e.printStackTrace();
    }
  }

  //region Getters/Setters
  public CraftingManager getCraftingManager() {
    return craftingManager;
  }

  public SelectionManager getSelectionManager() {
    return selectionManager;
  }
  //endregion
}
