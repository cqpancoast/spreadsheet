package edu.cs3500.spreadsheets.provider.model;

import edu.cs3500.spreadsheets.model.Coord;
import java.util.List;

/**
 * A read-only version of the Worksheet Model.
 */
public interface ReadOnlyWorksheetModel {

  /**
   * Gets the unevaluated content of the given cell.
   * @param c the coordinate of the cell to look at.
   * @return the String of the unevaluated content of the cell.
   */
  String getBasicContentAt(Coord c);

  /**
   * Gets the evaluated content of the given cell.
   * @param c the coordinate of the cell to look at.
   * @return the String of the evaluated content of the cell.
   */
  String getEvaluatedString(Coord c);

  /**
   * Returns all non-empty cells in this worksheet.
   * @return the coordinate of all non-empty cells in this worksheet.
   * @throws IllegalArgumentException if the coordinate is not a within the current bounds of the
   *      spreadsheet.
   */
  List<Coord> getNonEmptyCells();

  /**
   * All of the supported functions in this spreadsheet.
   * @return a supported function if the String is a supported function, null if not supported.
   */
  Function<List<Value>, Value> getSupportedFunctions(String s);

  /**
   * Gets the number of rows in the spreadsheet.
   * @return the number of rows in this spreadsheet.
   */
  int getNumRow();

  /**
   * Gets the number of columns in the spreadsheet.
   * @return the number of columns in this spreadsheet.
   */
  int getNumCol();
}
