package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
  public GridWorksheetView(IWorksheetModel<?> model) {
    super();
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null.");
    }

    this.setTitle("Spreadsheet GUI");
    this.setVisible(true);
    //this.setResizable(false);

    // grid panel showing the active cells
    JPanel grid = new GridPanel(model, 0, 0);
    Dimension gridSize = grid.getPreferredSize();
    JScrollPane scrollGrid = new JScrollPane(grid);
    scrollGrid.setBorder(BorderFactory.createEmptyBorder());
    scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1100), Math.min(gridSize.height, 600)));

    // button panel to increase number of rows or columns
    JPanel buttons = new JPanel();
    buttons.setLayout(new FlowLayout());
    buttons.add(new JLabel("Adjust grid size:"));
    buttons.add(new JButton("add row"));
    buttons.add(new JButton("add column"));

    // add all necessary panels to the frame
    this.setLayout(new BorderLayout());
    this.add(scrollGrid, BorderLayout.CENTER);
    this.add(buttons, BorderLayout.PAGE_START);

    this.pack();
  }

  @Override
  public void render() {
    this.repaint();
  }
}
