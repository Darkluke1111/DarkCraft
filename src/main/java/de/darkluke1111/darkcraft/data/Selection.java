package de.darkluke1111.darkcraft.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class Selection {
  private Player owner;
  private Location loc1;
  private Location loc2;

  public Selection(Player owner) {
    this.owner = owner;
  }

  /**
   * Generates a Structure Object with the given name from the current
   * selection, which can then be used as for crafting structures.
   */
  public Structure convertToStructure(String name) {
    return Structure.createStructureFromWorld(
        getMinimalLocation(),
        name,
        getXSpan(),
        getYSpan(),
        getZSpan());
  }

  //region Getter/Setter

  /**
   * Returns the Location of the selections corner with the
   * lowest x,y and z value.
   */
  public Location getMinimalLocation() {
    if (loc1 == null || loc2 == null) {
      return null;
    }
    return new Location(loc1.getWorld(),
        Math.min(loc1.getBlockX(), loc2.getBlockX()),
        Math.min(loc1.getBlockY(), loc2.getBlockY()),
        Math.min(loc1.getBlockZ(), loc2.getBlockZ()));
  }

  /**
   * Returns a 3-dimensional array with a spacial representation
   * of the MaterialData in the Selection.
   */
  @SuppressWarnings("deprecation")
  public MaterialData[][][] getMaterialMatrix() {
    if (loc1 == null || loc2 == null) {
      return null;
    }
    MaterialData[][][] data = new MaterialData[getXSpan()][getYSpan()][getZSpan()];
    Location start = getMinimalLocation();

    Location iterator;
    for (int y = 0; y < getYSpan(); y++) {
      for (int x = 0; x < getYSpan(); x++) {
        for (int z = 0; z < getYSpan(); z++) {
          iterator = start.clone().add(x, y, z);
          data[x][y][z] = new MaterialData(
              iterator.getBlock().getType(),
              iterator.getBlock().getData());
        }
      }
    }
    return data;
  }

  /**
   * Returns the size of the selection in x-Direction.
   */
  public int getXSpan() {
    if (loc1 == null || loc2 == null) {
      return 0;
    }
    return Math.abs(loc1.getBlockX() - loc2.getBlockX()) + 1;
  }

  /**
   * Returns the size of the selection in y-Direction.
   */
  public int getYSpan() {
    if (loc1 == null || loc2 == null) {
      return 0;
    }
    return Math.abs(loc1.getBlockY() - loc2.getBlockY()) + 1;
  }

  /**
   * Returns the size of the selection in z-Direction.
   */
  public int getZSpan() {
    if (loc1 == null || loc2 == null) {
      return 0;
    }
    return Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) + 1;
  }

  /**
   * Gets the first corner of the Selection.
   */
  public Location getLoc1() {
    return loc1;
  }

  /**
   * Sets the first corner of the Selection.
   */
  public void setLoc1(Location loc1) {
    this.loc1 = loc1;
  }

  /**
   * Gets the second corner of the Selection.
   */
  public Location getLoc2() {
    return loc2;
  }

  /**
   * Sets the second corner of the Selection.
   */
  public void setLoc2(Location loc2) {
    this.loc2 = loc2;
  }

  /**
   * Gets the owning player of the Selection.
   */
  public Player getOwner() {
    return owner;
  }
  //endregion

  @Override
  public String toString() {
    String str1 = "(?,?,?)";
    String str2 = "(?,?,?)";
    if (loc1 != null) {
      str1 = "(" + loc1.getBlockX() + "|" + loc1.getBlockY() + "|" + loc1.getBlockZ() + ")";
    }
    if (loc2 != null) {
      str2 = "(" + loc2.getBlockX() + "|" + loc2.getBlockY() + "|" + loc2.getBlockZ() + ")";
    }
    return str1 + " to " + str2;
  }
}
