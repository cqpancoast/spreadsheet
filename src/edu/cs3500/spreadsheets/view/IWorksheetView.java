package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;

/**
 * A view for a worksheet in a spreadsheet application, in which there is a 2-D grid that, for each
 * cell, is blank or has a value. This view produces a representation of a {@link IWorksheetModel}.
 */
public interface IWorksheetView {

  /**
   * Processes a {@link IWorksheetModel} to produce a representation of it. This representation can
   * be visual, textual, or some other form of data.
   */
  void render();

  /**
   * Sets active cell in this {@link IWorksheetView}. If coord is null, selects no cell.
   */
  void setActiveCell(Coord coord);

  /**
   * Gets coord of active cell in this {@link IWorksheetView}. If no active cell, returns null.
   * @return active cell coord or null if no active cell
   */
  Coord getActiveCell();

  /**
   * Adds an additional listener to any that already exist that wait for activation in some manner,
   * from either user control or the view itself.
   * @param f a listener that waits for feature-specific activation from the view
   */
  void addFeatureListener(FeatureListener f);

}
