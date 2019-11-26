package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Represents a {@link IWorksheetModel} visually, as a grid of cells. Although a worksheet is
 * infinite by definition, this view is limited to displaying everything with a column or row less
 * than the cell with the largest row and column. Bars are put at the top and the left of the view
 * that have A, B, C... and 1, 2, 3... spaced to be in line with the columns and rows (respectively)
 * that they refer to. The displayed contents of cells are the <i>evaluated</i> contents. The number
 * of displayed rows and columns will always both be three or greater, regardless of cell
 * population.
 */
public class GridWorksheetView extends JFrame implements IWorksheetView {

  /**
   * Creates a {@link GridWorksheetView}.
   * @param model a {@link IWorksheetModel} representing a worksheet
   * @throws IllegalArgumentException if model is null
   */
  public GridWorksheetView(IWorksheetModel model) {
    super();
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null.");
    }

    this.setTitle("Spreadsheet GUI");
    this.setVisible(true);

    // grid panel showing the active cells
    JPanel grid = new GridPanel(model);
    Dimension gridSize = grid.getPreferredSize();
    JScrollPane scrollGrid = new JScrollPane(grid);
    scrollGrid.setBorder(BorderFactory.createEmptyBorder());
    scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1100), Math.min(gridSize.height, 600)));

    // add all necessary panels to the frame
    this.setLayout(new BorderLayout());
    this.add(scrollGrid, BorderLayout.CENTER);

    this.pack();
  }

  @Override
  public void render() {
    this.repaint();
  }

  @Override
  public void setActiveCell(Coord coord) {

  }

  @Override
  public Coord getActiveCell() {
    return null;
  }

  @Override
  public void addFeatureListener(FeatureListener f) {

  }
}
