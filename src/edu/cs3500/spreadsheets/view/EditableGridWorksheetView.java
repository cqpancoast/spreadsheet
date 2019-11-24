package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Represents a {@link IWorksheetModel} visually, as a grid of cells. Although a worksheet is
 * infinite by definition, this view is limited to displaying everything with a column or row less
 * than the cell with the largest row and column. Bars are put at the top and the left of the view
 * that have A, B, C... and 1, 2, 3... spaced to be in line with the columns and rows (respectively)
 * that they refer to. The displayed contents of cells are the <i>evaluated</i> contents. The number
 * of displayed rows and columns will always both be three or greater, regardless of cell
 * population.
 */
public class EditableGridWorksheetView extends JFrame implements IWorksheetView {
  private final GridPanel gridPanel;
  private JButton addRow;
  private JButton addCol;

  /**
   * Creates a {@link EditableGridWorksheetView}.
   * @param model a {@link IWorksheetModel} representing a worksheet
   * @throws IllegalArgumentException if model is null
   */
  public EditableGridWorksheetView(IWorksheetModel model) {
    super();
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null.");
    }

    this.setTitle("Spreadsheet GUI");

    // grid panel showing the active cells
    this.gridPanel = new GridPanel(model, null);
    Dimension gridSize = this.gridPanel.getPreferredSize();
    JScrollPane scrollGrid = new JScrollPane(this.gridPanel);
    scrollGrid.setBorder(BorderFactory.createEmptyBorder());
    scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1100), Math.min(gridSize.height, 600)));

    // button panel to increase number of rows or columns
    JPanel buttons = new JPanel();
    this.addRow = new JButton("add row");
    this.addCol = new JButton("add column");
    buttons.setLayout(new FlowLayout());
    buttons.add(new JLabel("Adjust grid size:"));
    buttons.add(this.addRow);
    buttons.add(this.addCol);
    this.addCol.addActionListener(evt -> gridPanel.setMaxRowsCols(
        gridPanel.getMaxRowsCols().row, gridPanel.getMaxRowsCols().col + 1));
    this.addRow.addActionListener(evt -> gridPanel.setMaxRowsCols(
        gridPanel.getMaxRowsCols().row + 1, gridPanel.getMaxRowsCols().col));

    // add all necessary panels to the frame
    this.setLayout(new BorderLayout());
    this.add(scrollGrid, BorderLayout.CENTER);
    this.add(buttons, BorderLayout.PAGE_START);

    this.pack();

    // handle keyEvents by calling methods in featureListener


  }

  @Override
  public void render() {
    this.repaint();
    this.setVisible(true);
  }

  @Override
  public void setActiveCell(Coord coord) {
    this.gridPanel.setActiveCell(coord);
  }

  @Override
  public Coord getActiveCell() {
    return this.gridPanel.getActiveCell();
  }

  @Override
  public String getInputFromActiveCell() {
    JTextField textField = gridPanel.getTextField();
    return textField == null ? "" : textField.getText();
  }

  @Override
  public void addFeatureListener(FeatureListener f) {
    this.gridPanel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (getActiveCell() == null) {
          f.onCellSelection(gridPanel.pixelToCoord(e.getLocationOnScreen()));
        } else {
          f.onCellDeselection();
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }
    });
    this.gridPanel.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {

      }

      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case '\b':
            if (getActiveCell() != null) {
              //set cell contents to empty
            }
            break;
          //TODO: Arrows and movement
          case 's':
            f.save();
            break;
          case 'q':
            f.quit();
            break;
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {

      }
    });
  }
}
