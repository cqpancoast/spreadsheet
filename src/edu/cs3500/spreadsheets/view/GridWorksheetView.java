package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
  private HashMap<Integer, HashMap<Integer, CellPanel>> cells;
  private int maxRows;
  private int maxCols;

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

    this.setTitle("Spreadsheet GUI");
    this.setVisible(true);

    this.maxRowsCols(model.getMaxRows(), model.getMaxColumns());

    // populate the cells field with CellPanels
    this.cells = new HashMap<>();
    for (int i = 1; i <= maxRows; i++) {
      HashMap<Integer, CellPanel> innerMap = new HashMap<>();
      for (int j = 1; j <= maxCols; j++) {
        innerMap.put(j, new CellPanel(model, i, j, false));
      }
      cells.put(i, innerMap);
    }

    this.setLayout(new BorderLayout());

    // grid panel showing the active cells
    JPanel grid = new JPanel();
    grid.setLayout(new GridLayout(maxRows + 1, maxCols + 1));
    grid.setBorder(BorderFactory.createEmptyBorder(0,15,15,15));

    // button panel to increase number of rows or columns
    JPanel buttons = new JPanel();
    buttons.setLayout(new FlowLayout());
    buttons.add(new JLabel("Adjust grid size:"));
    buttons.add(new JButton("add rows"));
    buttons.add(new JButton("add cols"));

    // add all necessary panels to the frame
    this.add(grid, BorderLayout.CENTER);
    this.add(buttons, BorderLayout.PAGE_START);

    // add LabelPanels and CellPanels to the grid
    for (int i = 0; i <= maxRows; i++) {
      for (int j = 0; j <= maxCols; j++) {
        if (i == 0 && j == 0) {
          grid.add(new LabelPanel(""));
        }
        else if (i == 0) {
          grid.add(new LabelPanel(Coord.colIndexToName(j)));
        }
        else if (j == 0) {
          grid.add(new LabelPanel(Integer.toString(i)));
        }
        else {
          grid.add(cells.get(i).get(j));
        }
      }
    }

    this.pack();
  }

  @Override
  public void render() {
    this.repaint();
  }

  /**
   * Sets the number of rows and columns in the grid to the given number of rows and columns,
   * or three: whichever value is greater.
   *
   * @param rows  the desired amount of rows
   * @param cols  the desired amount of columns
   */
  private void maxRowsCols(int rows, int cols) {
    maxRows = Math.max(3, rows);
    maxCols = Math.max(3, cols);
  }
}
