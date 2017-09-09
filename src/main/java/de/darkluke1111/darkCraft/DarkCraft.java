package de.darkluke1111.darkCraft;

import de.darkluke1111.darkCraft.data.AdvRecipe;
import de.darkluke1111.darkCraft.data.Structure;
import de.darkluke1111.darkCraft.data.behaviors.*;
import de.darkluke1111.darkCraft.recipeGUI.MenuViewFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DarkCraft extends JavaPlugin {
    public static NamespacedKey namespacedKey;
    public static DarkCraft instance;
    private CraftingManager craftingManager;
    private SelectionManager selectionManager;
    private DarkCraftListener listener;

    public void onEnable() {
        namespacedKey = new NamespacedKey(this, this.getDescription().getName());
        instance = this;
        craftingManager = new CraftingManager();
        selectionManager = new SelectionManager();
        listener = new DarkCraftListener();

        saveDefaultConfig();
        saveDefaultRecipes();
        saveDefaultStructures();
        saveReadme();

        registerSerialisableClasses();
        Bukkit.getPluginManager().registerEvents(listener, this);

        importRecipesFromConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //saveStruct
        if (command.getName().equalsIgnoreCase("saveStruct")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            if (args.length < 1) return false;
            if (SelectionManager.playerOwnsSelection(player)) {
                SelectionManager.getSelectionOfPlayer(player).convertToStructure(args[0]);
                player.sendMessage("Structure was savend from Selection.");
            }
        }
        //pasteStruct
        if (command.getName().equalsIgnoreCase("pasteStruct")) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            if (args.length < 1) return false;
            Location pos = player.getLocation();
            Structure.getStructureForName(args[0]).placeStructure(pos);
        }
        //viewRecipes
        if (command.getName().equalsIgnoreCase(("viewRecipes"))) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            MenuViewFactory.createRecipeSelectionView(AdvRecipe.getRecipes()).show(player);
        }
        //reloadStructures
        if (command.getName().equalsIgnoreCase(("reloadRecipes"))) {
            Structure.clearStructureCache();
        }
        return true;
    }

    private void registerSerialisableClasses() {
        ConfigurationSerialization.registerClass(Structure.class, Structure.class.getName());
        ConfigurationSerialization.registerClass(AdvRecipe.class, AdvRecipe.class.getName());
        ConfigurationSerialization.registerClass(ConsumeLifeBehavior.class, ConsumeLifeBehavior.class.getName());
        ConfigurationSerialization.registerClass(ConsumeExpBehavior.class, ConsumeExpBehavior.class.getName());
        ConfigurationSerialization.registerClass(ExplosionBehavior.class, ExplosionBehavior.class.getName());
        ConfigurationSerialization.registerClass(LightningBehavior.class, LightningBehavior.class.getName());
        ConfigurationSerialization.registerClass(StructureBehavior.class, StructureBehavior.class.getName());
        loadBehaviors();
    }

    private void loadBehaviors() {
        ConsumeExpBehavior.load();
        ConsumeLifeBehavior.load();
        ExplosionBehavior.load();
        LightningBehavior.load();
        StructureBehavior.load();

    }

    private void saveDefaultRecipes() {
        List<String> recipeFileNames = (List<String>) getConfig().get("recipe_files");
        for (String recipeFileName : recipeFileNames) {
            if(!new File(getDataFolder(),"recipe_" + recipeFileName + ".yml").exists()) {
                saveResource("recipe_" + recipeFileName + ".yml", false);
            }
        }
    }

    private void saveDefaultStructures() {
        List<String> structureFileNames = (List<String>) getConfig().get("structure_files");
        for (String structureFileName : structureFileNames) {
            if(!new File(getDataFolder(),"recipe_" + structureFileName + ".yml").exists()) {
                saveResource("structure_" + structureFileName + ".yml", false);
            }
        }
    }

    private void saveReadme() {
        if(!new File(getDataFolder(),"readme.txt").exists()) {
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
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        Set<String> keySet = fc.getKeys(false);
        try {
            keySet.stream().map(key -> (AdvRecipe) fc.get(key)).forEach(AdvRecipe::register);
        } catch (ClassCastException exception) {
            getLogger().warning("Was not able to read recipe File " + dataFile + "! Is it corrupted?");
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
