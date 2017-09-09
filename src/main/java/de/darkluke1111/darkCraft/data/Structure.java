package de.darkluke1111.darkCraft.data;

import de.darkluke1111.darkCraft.DarkCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents a multiblock-structure around a craftingtable.
 */
public class Structure implements ConfigurationSerializable {

    //Cachemap with all structures
    private static Map<String, Structure> structures = new HashMap<>();

    private MaterialData[][][] matrix;
    private String name;
    private int xSpan;
    private int ySpan;
    private int zSpan;
    private Vector offset;

    //region Object-Methods
    /**
     * Tests whether the Structure is present at the specified location.
     * @return true if it is present, false otherwise
     */
    @SuppressWarnings("deprecation")
    public boolean testForStructure(Location loc) {
        for (int y = 0; y < ySpan; y++) {
            for (int x = 0; x < xSpan; x++) {
                for (int z = 0; z < zSpan; z++) {
                    Location pos = loc.clone().add(offset).add(x, y, z);
                    Block block = pos.getBlock();
                    if(matrix[x][y][z].getItemType() == Material.AIR) {
                        continue;
                    }
                    if (!(block.getType() == matrix[x][y][z].getItemType()
                            && block.getData() == matrix[x][y][z].getData())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Places the Structure at the specified location.
     */
    @SuppressWarnings("deprecation")
    public void placeStructure(Location loc) {
        for (int y = 0; y < ySpan; y++) {
            for (int x = 0; x < xSpan; x++) {
                for (int z = 0; z < zSpan; z++) {
                    Location pos = loc.clone().add(offset).add(x, y, z);
                    Block block = pos.getBlock();
                    block.setType(matrix[x][y][z].getItemType());
                    block.setData(matrix[x][y][z].getData());
                }
            }
        }
    }

    /**
     * Saves the Structure to a yml file in the plugins data folder to make it persistent.
     * @return true if the operation was successfull, false otherwise
     */
    public boolean saveToDisc() throws IOException {
        File file = new File(DarkCraft.instance.getDataFolder(), "structure_" + name + ".yml");
        if (file.exists()) {
            return false;
        }
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        fc.set("contents", this);
        fc.save(file);
        return true;
    }
    //endregion

    //region Static Factory-Methods
    /**
     * Returns the structure with the given name. The name is equal to the filename of the corresponding schematic-file
     * without the extension.
     * @param structName The name of the structure.
     * @return the structureobject or null if the structure does not exist.
     */
    public static Structure getStructureForName(String structName) {
        if (structures.containsKey(structName)) {
            return structures.get(structName);
        } else {

            Structure structure = loadStructureFromDisc(structName);
            structures.put(structName, structure);
            return structure;
        }
    }

    /**
     * Creates a new Structure from a selection in the world at the specified location and dimension and saves it.
     * @param loc The location where the terrain should be copied as a Structure
     * @param name The name of the Structure
     * @param xSpan Dimension in the x-Direction
     * @param ySpan Dimension in the y-Direction
     * @param zSpan Dimension in the z-Direction
     * @return The new Structure
     */
    @SuppressWarnings("deprecation")
    public static Structure createStructureFromWorld(Location loc, String name, int xSpan, int ySpan, int zSpan) {
        MaterialData matrix[][][] = new MaterialData[xSpan][ySpan][zSpan];
        Vector offset = new Vector();
        for (int y = 0; y < ySpan; y++) {
            for (int x = 0; x < xSpan; x++) {
                for (int z = 0; z < zSpan; z++) {
                    Location pos = loc.clone().add(x, y, z);
                    Block block = pos.getBlock();
                    Material mat = block.getType();
                    byte dat = block.getData();
                    matrix[x][y][z] = new MaterialData(mat, dat);
                    if(mat == Material.WORKBENCH) {
                        offset = new Vector(-x,-y,-z);
                    }
                }
            }
        }
        Structure structure = new Structure(name, matrix, offset);
        try {
            structure.saveToDisc();
        } catch (IOException e) {
            DarkCraft.instance.getLogger().warning("Wasn't able to save Structure '" + name + "' to Disk!");
        }
        structures.put(name,structure);

        return structure;
    }

    /**
     * Clears the Structure cache.
     */
    public static void clearStructureCache() {
        structures.clear();
    }
    //endregion

    //region Private Methods
    private Structure(String name, MaterialData[][][] matrix, Vector offset) {
        this.name = name;
        this.offset = offset;
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0 || matrix[0][0].length == 0) {
            this.xSpan = 0;
            this.ySpan = 0;
            this.zSpan = 0;
            this.matrix = null;
            DarkCraft.instance.getLogger().warning("The Structure '" + name + "' has been initialized empty!");
        } else {
            this.xSpan = matrix.length;
            this.ySpan = matrix[0].length;
            this.zSpan = matrix[0][0].length;
            this.matrix = matrix;
        }
        structures.put(name, this);
    }

    private static Structure loadStructureFromDisc(String name) {
        File file = new File(DarkCraft.instance.getDataFolder(), "structure_" + name + ".yml");
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        Structure structure = (Structure) fc.get("contents");
        System.out.println(structure);
        return structure;
    }
    //endregion

    //region Serialisation Stuff
    /**
     * Constructor for deserialisation.
     */
    @SuppressWarnings("unchecked, deprecation")
    public Structure(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.xSpan = (int) map.get("xSpan");
        this.ySpan = (int) map.get("ySpan");
        this.zSpan = (int) map.get("zSpan");
        this.matrix = new MaterialData[xSpan][ySpan][zSpan];
        this.offset = new Vector((int) map.get("xOffset"), (int) map.get("yOffset"), (int) map.get("zOffset"));

        if (xSpan == 0 || ySpan == 0 || zSpan == 0) {
            this.xSpan = 0;
            this.ySpan = 0;
            this.zSpan = 0;
            this.matrix = null;
            return;
        } else {

            Map<String, Object> matrixMap = (Map<String, Object>) map.get("matrix");

            for (int y = 0; y < ySpan; y++) {
                 List<String> rows = (List<String>) matrixMap.get("layer" + y);
                for (int x = 0; x < xSpan; x++) {
                    String[] data = rows.get(x).split(", ");
                    for (int z = 0; z < zSpan; z++) {
                        Material mat = Material.getMaterial(data[z].split(":")[0]);
                        byte dat = Byte.parseByte(data[z].split(":")[1]);
                        MaterialData md = new MaterialData(mat, dat);
                        matrix[x][y][z] = md;
                    }
                }
            }
        }
        structures.put(name, this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("xSpan", xSpan);
        map.put("ySpan", ySpan);
        map.put("zSpan", zSpan);
        map.put("xOffset", offset.getBlockX());
        map.put("yOffset", offset.getBlockY());
        map.put("zOffset", offset.getBlockZ());

        Map<String, Object> matrixMap = new LinkedHashMap<>();

        for (int y = 0; y < ySpan; y++) {
            List<String> rows = new ArrayList<>();
            for (int x = 0; x < xSpan; x++) {

                StringBuilder sb = new StringBuilder();
                for (int z = 0; z < zSpan; z++) {
                    MaterialData md = matrix[x][y][z];
                    sb.append(md.getItemType());
                    sb.append(":");
                    sb.append(md.getData());
                    sb.append(", ");
                }
                rows.add(sb.toString());
            }
            matrixMap.put("layer" + y, rows);
        }
        map.put("matrix", matrixMap);

        return map;
    }
    //endregion
}
