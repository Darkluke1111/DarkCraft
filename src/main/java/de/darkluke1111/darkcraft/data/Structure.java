package de.darkluke1111.darkcraft.data;

import de.darkluke1111.darkcraft.DarkCraft;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Represents a multiblock-structure around a craftingtable.
 */
public class Structure implements ConfigurationSerializable {

  //Cachemap with all structures
  private static Map<String, Structure> structures = new HashMap<>();

  private MaterialData[][][] matrix;
  private String name;
  private int xspan;
  private int yspan;
  private int zspan;
  private Vector offset;

  //region Object-Methods

  //region Private Methods
  private Structure(String name, MaterialData[][][] matrix, Vector offset) {
    this.name = name;
    this.offset = offset;
    if (matrix == null || matrix.length == 0 || matrix[0].length == 0 || matrix[0][0].length == 0) {
      this.xspan = 0;
      this.yspan = 0;
      this.zspan = 0;
      this.matrix = null;
      DarkCraft.instance.getLogger().warning(
          "The Structure '" + name + "' has been initialized empty!");
    } else {
      this.xspan = matrix.length;
      this.yspan = matrix[0].length;
      this.zspan = matrix[0][0].length;
      this.matrix = matrix;
    }
    structures.put(name, this);
  }

  /**
   * Constructor for deserialisation.
   */
  @SuppressWarnings("unchecked, deprecation")
  public Structure(Map<String, Object> map) {
    this.name = (String) map.get("name");
    this.xspan = (int) map.get("xSpan");
    this.yspan = (int) map.get("ySpan");
    this.zspan = (int) map.get("zSpan");
    this.matrix = new MaterialData[xspan][yspan][zspan];
    this.offset = new Vector(
        (int) map.get("xOffset"),
        (int) map.get("yOffset"),
        (int) map.get("zOffset"));

    if (xspan == 0 || yspan == 0 || zspan == 0) {
      this.xspan = 0;
      this.yspan = 0;
      this.zspan = 0;
      this.matrix = null;
      return;
    } else {

      Map<String, Object> matrixMap = (Map<String, Object>) map.get("matrix");

      for (int y = 0; y < yspan; y++) {
        List<String> rows = (List<String>) matrixMap.get("layer" + y);
        for (int x = 0; x < xspan; x++) {
          String[] data = rows.get(x).split(", ");
          for (int z = 0; z < zspan; z++) {
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

  /**
   * Returns the structure with the given name. The name is equal to the
   * filename of the corresponding schematic-file without the extension.
   *
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
  //endregion

  //region Static Factory-Methods

  /**
   * Creates a new Structure from a selection in the world at the specified
   * location and dimension and saves it.
   *
   * @param loc   The location where the terrain should be copied as a Structure
   * @param name  The name of the Structure
   * @param xspan Dimension in the x-Direction
   * @param yspan Dimension in the y-Direction
   * @param zspan Dimension in the z-Direction
   * @return The new Structure
   */
  @SuppressWarnings("deprecation")
  public static Structure createStructureFromWorld(Location loc,
                                                   String name, int xspan, int yspan, int zspan) {
    MaterialData[][][] matrix = new MaterialData[xspan][yspan][zspan];
    Vector offset = new Vector();
    for (int y = 0; y < yspan; y++) {
      for (int x = 0; x < xspan; x++) {
        for (int z = 0; z < zspan; z++) {
          Location pos = loc.clone().add(x, y, z);
          Block block = pos.getBlock();
          Material mat = block.getType();
          byte dat = block.getData();
          matrix[x][y][z] = new MaterialData(mat, dat);
          if (mat == Material.WORKBENCH) {
            offset = new Vector(-x, -y, -z);
          }
        }
      }
    }
    Structure structure = new Structure(name, matrix, offset);
    try {
      structure.saveToDisc();
    } catch (IOException e) {
      DarkCraft.instance.getLogger().warning(
          "Wasn't able to save Structure '" + name + "' to Disk!");
    }
    structures.put(name, structure);

    return structure;
  }

  /**
   * Clears the Structure cache.
   */
  public static void clearStructureCache() {
    structures.clear();
  }

  private static Structure loadStructureFromDisc(String name) {
    File file = new File(DarkCraft.instance.getDataFolder(), "structure_" + name + ".yml");
    FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
    Structure structure = (Structure) fc.get("contents");
    System.out.println(structure);
    return structure;
  }
  //endregion

  /**
   * Tests whether the Structure is present at the specified location.
   *
   * @return true if it is present, false otherwise
   */
  @SuppressWarnings("deprecation")
  public boolean testForStructure(Location loc) {
    for (int y = 0; y < yspan; y++) {
      for (int x = 0; x < xspan; x++) {
        for (int z = 0; z < zspan; z++) {
          Location pos = loc.clone().add(offset).add(x, y, z);
          Block block = pos.getBlock();
          if (matrix[x][y][z].getItemType() == Material.AIR) {
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
    for (int y = 0; y < yspan; y++) {
      for (int x = 0; x < xspan; x++) {
        for (int z = 0; z < zspan; z++) {
          Location pos = loc.clone().add(offset).add(x, y, z);
          Block block = pos.getBlock();
          block.setType(matrix[x][y][z].getItemType());
          block.setData(matrix[x][y][z].getData());
        }
      }
    }
  }
  //endregion

  //region Serialisation Stuff

  /**
   * Saves the Structure to a yml file in the plugins data folder to make it persistent.
   *
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

  @Override
  @SuppressWarnings("deprecation")
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("name", name);
    map.put("xspan", xspan);
    map.put("yspan", yspan);
    map.put("zspan", zspan);
    map.put("xOffset", offset.getBlockX());
    map.put("yOffset", offset.getBlockY());
    map.put("zOffset", offset.getBlockZ());

    Map<String, Object> matrixMap = new LinkedHashMap<>();

    for (int y = 0; y < yspan; y++) {
      List<String> rows = new ArrayList<>();
      for (int x = 0; x < xspan; x++) {

        StringBuilder sb = new StringBuilder();
        for (int z = 0; z < zspan; z++) {
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
