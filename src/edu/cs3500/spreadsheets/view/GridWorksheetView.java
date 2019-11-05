package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.WorksheetModel;

/**
 * TODO javadoc
 */
public class GridWorksheetView implements WorksheetView {
  private final WorksheetModel<?> model;

  /**
   * Creates a {@link GridWorksheetView}.
   * @param model a {@link WorksheetModel} representing a worksheet.
   */
  public GridWorksheetView(WorksheetModel<?> model) {
    this.model = model;
  }

  @Override
  public void render() {

  }
}
