package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.WorksheetModel;

/**
 * A view for a worksheet in a spreadsheet application, in which there is a 2-D grid that, for each
 * cell, is blank or has a value. This view produces a representation of a {@link WorksheetModel}.
 */
public interface WorksheetView {

  /**
   * Processes a {@link WorksheetModel} to produce a representation of it. This representation can
   * be visual, textual, or just data.
   */
  void render();

}
