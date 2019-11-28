//package edu.cs3500.spreadsheets.provider.model;
//
//import edu.cs3500.spreadsheets.model.Coord;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * A read-only version of the Worksheet Model.
// */
//public interface ReadOnlyWorksheetModel {
//  /**
//   * Returns the cell at the given location in the spreadsheet.
//   * @param c the coordinate to find the cell at.
//   * @return the cell at coordinate c, returns null if the cell is empty.
//   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
//   *      spreadsheet.
//   */
//  Cell getCellAt(Coord c);
//
//  /**
//   * Gets the unevaluated content of the given cell.
//   * @param c the coordinate of the cell to look at.
//   * @return the String of the unevaluated content of the cell.
//   */
//  String getBasicContentAt(Coord c);
//
//  /**
//   * Returns the value of the evaluated cell.
//   * @param c the coordinate of the cell to evaluate.
//   * @return the value after evaluating the cell.
//   * @throws InvalidCellException if the cell cannot be evaluated.
//   */
//  Value evaluateCellAt(Coord c) throws InvalidCellException;
//
//  /**
//   * Gets the evaluated content of the given cell.
//   * @param c the coordinate of the cell to look at.
//   * @return the String of the evaluated content of the cell.
//   * @throws InvalidCellException if the cell cannot be evaluated.
//   */
//  String getEvaluatedString(Coord c) throws InvalidCellException;
//
//  /**
//   * Returns all non-empty cells in this worksheet.
//   * @return all non-empty cells in this worksheet.
//   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
//   *      spreadsheet.
//   */
//  HashMap<Coord, Cell> getNonEmptyCells();
//
//  /**
//   * All of the supported functions in this spreadsheet.
//   * @return a supported function if the String is a supported function, null if not supported.
//   */
//  Function<List<Value>, Value> getSupportedFunctions(String s);
//
//  /**
//   * Gets the number of rows in the spreadsheet.
//   * @return the number of rows in this spreadsheet.
//   */
//  int getNumRow();
//
//  /**
//   * Gets the number of columns in the spreadsheet.
//   * @return the number of columns in this spreadsheet.
//   */
//  int getNumCol();
//}
