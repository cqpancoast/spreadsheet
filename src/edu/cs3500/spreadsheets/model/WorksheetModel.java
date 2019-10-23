package edu.cs3500.spreadsheets.model;

/**
 * Represents a worksheet in a spreadsheet application, in which there is a potentially infinite
 * grid with values. Those values can be of any type, given by type param T. //HELP BLERNER how to say param T
 */
public interface WorksheetModel<T> { //HELP BLERNER good design decision to have type param here?

  /**
   * Sets the cell at coordinate c in the grid to have the value val.
   * @param c coordinate in the grid
   * @param val an unevaluated value
   */
  void set(Coord c, T val);

  /**
   * Gets the evaluated contents of the cell at coordinate c in the grid.
   * @param c coordinate in the grid
   * @return evaluated contents of cell at coord c
   */
  String getEval(Coord c);

  /**
   * Gets the unevaluated contents of the cell at coordinate c in the grid.
   * @param c coordinate in the grid
   * @return unevaluated contents of cell at coord c
   */
  T getRaw(Coord c);

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
   * Determines whether this worksheet is valid, given any implementation-specific rules, which may
   * include syntax or cyclic reference.
   * @return whether this worksheet is valid.
   */
  boolean isValid();

}
