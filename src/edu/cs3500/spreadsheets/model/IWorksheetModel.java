package edu.cs3500.spreadsheets.model;

import java.util.Set;

/**
 * Represents a worksheet in a spreadsheet application, in which there is a potentially infinite
 * grid with values. This interface supports the idea of evaluation, that is, that by some process
 * the evaluated contents of a cell will be different from its raw contents. For ease of display,
 * the evaluated contents are always Strings. Raw values of cells are strings, and the raw values of
 * cells without any values in them will always be null.
 */
public interface IWorksheetModel {

  /**
   * Sets the cell at coordinate c in the grid to have the value val.
   * @param col column in the grid
   * @param row row in the grid
   * @param val an unevaluated value
   */
  void set(int col, int row, String val);

  /**
   * Gets the evaluated contents of the cell at coordinate c in the grid.
   * @param col column in the grid
   * @param row row in the grid
   * @return evaluated contents of cell at coord c
   */
  String getEval(int col, int row);

  /**
   * Gets the unevaluated contents of the cell at coordinate c in the grid.
   * @param col column in the grid
   * @param row row in the grid
   * @return unevaluated contents of cell at coord c
   */
  String getRaw(int col, int row);

  /**
   * Returns the row number of the valued cell with the largest row coordinate.
   * @return the row number of the valued cell with the largest row coordinate
   */
  int getMaxRows();

  /**
   * Returns the column number of the valued cell with the largest column coordinate.
   * @return the column number of the valued cell with the largest column coordinate
   */
  int getMaxColumns();

  /**
   * Returns a list of {@link Coord}s for positions of active cells.
   * @return a list of coordinates of active cells in this worksheet
   */
  Set<Coord> getActiveCells();

}
