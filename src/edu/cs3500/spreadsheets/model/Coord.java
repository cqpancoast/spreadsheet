package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A value type representing coordinates in a {@link IWorksheetModel}.
 */
public class Coord {
  public final int row;
  public final int col;

  /**
   * Constructs a {@link Coord}.
   * @param col this's column
   * @param row this's row
   */
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
   * @throws IllegalArgumentException if refString is malformed
   */
  public static List<Integer> fromString(String refString) throws IllegalArgumentException {
    if (!validCellName(refString)) {
      throw new IllegalArgumentException("Invalid reference string.");
    }
    String rowString = refString.replaceAll("[^0-9]", "");
    String colString = refString.replaceAll("[^A-Z]", "");
    return new ArrayList<Integer>(
          Arrays.asList(Coord.colNameToIndex(colString), Integer.parseInt(rowString)));
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
  public static boolean validCellName(String s) {
    return s.matches("[A-Z]+[0-9]+");
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
