package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A value type representing coordinates in a {@link WorksheetModel}.
 */
public class Coord {
  public final int row;
  public final int col;

  public Coord(int col, int row) {
    if (row < 1 || col < 1) {
      throw new IllegalArgumentException("Coordinates should be strictly positive");
    }
    this.row = row;
    this.col = col;
  }

  /**
   * Converts from the A-Z column naming system to a 1-indexed numeric value.
   * @param name the column name
   * @return the corresponding column index
   */
  public static int colNameToIndex(String name) {
    name = name.toUpperCase();
    int ans = 0;
    for (int i = 0; i < name.length(); i++) {
      ans *= 26;
      ans += (name.charAt(i) - 'A' + 1);
    }
    return ans;
  }

  /**
   * Converts a 1-based column index into the A-Z column naming system.
   * @param index the column index
   * @return the corresponding column name
   */
  public static String colIndexToName(int index) {
    StringBuilder ans = new StringBuilder();
    while (index > 0) {
      int colNum = (index - 1) % 26;
      ans.insert(0, Character.toChars('A' + colNum));
      index = (index - colNum) / 26;
    }
    return ans.toString();
  }

  /**
   * Converts the given reference string into a two-element list with the structure {col, row}.
   * @param refString the string representation of a {@link Coord}
   * @return a list {col, row} corresponding to the given reference string
   * @throws IllegalArgumentException if refString is not a well formed reference string
   */
  public static List<Integer> fromString(String refString) throws IllegalArgumentException {
    String rowString = refString.replaceAll("[^0-9]", "");
    return new ArrayList<Integer>(
        Arrays.asList(Coord.colNameToIndex(refString), Integer.parseInt(rowString)));
  }

  @Override
  public String toString() {
    return colIndexToName(this.col) + this.row;
  }

  /**
   * Determines whether the symbol s is valid as a reference in a worksheet.
   * @param s string representation of a symbol
   * @return whether s is a valid reference
   */
  public static boolean validReferenceName(String s) {
    try {
      Coord.fromString(s);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Coord coord = (Coord) o;
    return row == coord.row
        && col == coord.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }
}
