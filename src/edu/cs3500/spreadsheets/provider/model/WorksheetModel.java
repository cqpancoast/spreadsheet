package edu.cs3500.spreadsheets.provider.model;

import edu.cs3500.spreadsheets.model.Coord;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a spreadsheet that contains cells and can perform operations.
 * @param <K> the Cell implementation for this model
 */
public interface WorksheetModel<K> {
  /**
   * Returns the cell at the given location in the spreadsheet.
   * @param c the coordinate to find the cell at.
   * @return the cell at coordinate c, returns null if the cell is empty.
   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
   *      spreadsheet.
   */
  public K getCellAt(Coord c);


  /**
   * Returns the value of the evaluated cell.
   * @param c the coordinate of the cell to evaluate.
   * @return the value after evaluating the cell.
   * @throws InvalidCellException if the cell cannot be evaluated.
   */
  public Value evaluateCellAt(Coord c) throws InvalidCellException;

  /**
   * Returns the raw string content that was entered into the cell.
   * @param c the coordinate of the cell.
   * @return the string content that was entered into the cell.
   */
  public String getRawValueAt(Coord c);

  /**
   * Returns all of the cells in the bounds defined by the two coordinates.
   * @param c1 the coordinate of the top left bound.
   * @param c2 the coordinate of the bottom right bound.
   * @return all non-empty cells in the bounds as an ArrayList of cells.
   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
   *      spreadsheet.
   */
  public ArrayList<K> getCellsInArea(Coord c1, Coord c2);

  /**
   * Returns all non-empty cells in this worksheet.
   * @return all non-empty cells in this worksheet.
   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
   *      spreadsheet.
   */
  public List<Coord> getNonEmptyCells();

  /**
   * Sets the cell at the given coordinate to a blank cell.
   * @param c the coordinate to erase.
   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
   *      spreadsheet.
   */
  public void clearCell(Coord c);

  /**
   * Sets the cell at the given area to the given cell.
   * @param coord the coordinate of the cell in the spreadsheet to change.
   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
   *      spreadsheet.
   */
  public void setCellAt(Coord coord, String content);

  /**
   * Gets the number of rows in the spreadsheet.
   * @return the number of rows in this spreadsheet.
   */
  public int getNumRow();

  /**
   * Gets the number of columns in the spreadsheet.
   * @return the number of columns in this spreadsheet.
   */
  public int getNumCol();

  /**
   * All of the supported functions in this spreadsheet.
   * @return a supported function if the String is a supported function, null if not supported.
   */
  public Function<List<Value>, Value> getSupportedFunctions(String s);
}
