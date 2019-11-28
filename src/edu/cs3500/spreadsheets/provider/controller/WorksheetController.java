package edu.cs3500.spreadsheets.provider.controller;

import edu.cs3500.spreadsheets.model.Coord;

/**
 * Represents a controller for a visual worksheet.
 * Keeps track of the currently selected cell in the view.
 */
public interface WorksheetController {

  /**
   * Sets the value of the cell at the given coordinate to the string content.
   * @param c the coordinate of the cell to update.
   * @param content the content to update that cell to.
   */
  void updateCellAt(Coord c, String content);

  /**
   * Gets the coordinate of the user's selected cell.
   * @return the Coord of the cell that the user has selected.
   */
  Coord getSelectedCellCoord();

  /**
   * Sets the currently selected cell in this spreadsheet.
   * @param c the Coord to set as selected.
   */
  void setSelectedCell(Coord c);
}
