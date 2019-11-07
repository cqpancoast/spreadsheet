package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import java.awt.GridLayout;
import javax.swing.JFrame;

/**
 * Represents a {@link WorksheetModel} visually, as a grid of cells. Although a worksheet is
 * infinite by definition, this view is limited to displaying everything with a column or row less
 * than the cell with the largest row and column. Bars are put at the top and the left of the view
 * that have A, B, C... and 1, 2, 3... spaced to be in line with the columns and rows (respectively)
 * that they refer to. The displayed contents of cells are the <i>evaluated</i> contents. The number
 * of displayed rows and columns will always both be three or greater, regardless of cell
 * population.
 */
public class GridWorksheetView extends JFrame implements WorksheetView {
  private final WorksheetModel<?> model;

  private static final int MAX_ROWS = 100;
  private static final int MAX_COLS = 20;

  /**
   * Creates a {@link GridWorksheetView}.
   * @param model a {@link WorksheetModel} representing a worksheet
   * @throws IllegalArgumentException if model is null
   */
  public GridWorksheetView(WorksheetModel<?> model) {
    super();
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null.");
    }
    this.model = model;

    final int MAX_ROWS = model.getMaxRows();
    final int MAX_COLS = model.getMaxColumns();

    this.setLayout(new GridLayout(MAX_ROWS + 1, MAX_COLS + 1));

    for (int i = 0; i <= MAX_ROWS; i++) {
      for (int j = 0; j <= MAX_COLS; j++) {
        if (i == 0 && j == 0) {
          this.add(new LabelPanel(""));
        }
        else if (i == 0) {
          this.add(new LabelPanel(Coord.colIndexToName(j)));
        }
        else if (j == 0) {
          this.add(new LabelPanel(Integer.toString(i)));
        }
        else {
          this.add(new CellPanel());
        }
      }
    }

    this.pack();
  }

  @Override
  public void render() {
    this.repaint();
  }
}
