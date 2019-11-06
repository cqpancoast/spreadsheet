package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.WorksheetModel;

/**
 * Represents a {@link WorksheetModel} visually, as a grid of cells. Although a worksheet is
 * infinite by definition, this view is limited to displaying everything with a column or row less
 * than the cell with the largest row and column. Bars are put at the top and the left of the view
 * that have A, B, C... and 1, 2, 3... spaced to be in line with the columns and rows (respectively)
 * that they refer to. The displayed contents of cells are the <i>evaluated</i> contents.
 */
public class GridWorksheetView implements WorksheetView {
  private final WorksheetModel<?> model;

  /**
   * Creates a {@link GridWorksheetView}.
   * @param model a {@link WorksheetModel} representing a worksheet
   */
  public GridWorksheetView(WorksheetModel<?> model) {
    this.model = model;
  }

  @Override
  public void render() {

  }
}
