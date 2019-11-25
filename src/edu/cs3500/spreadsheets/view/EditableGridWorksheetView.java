package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
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
  private final JScrollPane scrollGrid;
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
    this.scrollGrid = new JScrollPane(this.gridPanel);
    scrollGrid.setBorder(BorderFactory.createEmptyBorder());
    scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1200), Math.min(gridSize.height, 650)));

    // button panel to increase number of rows or columns
    JPanel buttons = new JPanel();
    this.addRow = new JButton("add row");
    this.addCol = new JButton("add column");
    buttons.setLayout(new FlowLayout());
    buttons.add(new JLabel("Adjust grid size:"));
    buttons.add(this.addRow);
    buttons.add(this.addCol);
    this.addCol.addActionListener(this::addCol);
    this.addRow.addActionListener(this::addRow);

    // add all necessary panels to the frame
    this.setLayout(new BorderLayout());
    this.add(scrollGrid, BorderLayout.CENTER);
    this.add(buttons, BorderLayout.PAGE_START);

    this.pack();
  }

  /**
   * Adds a column to the grid view.
   * @param evt  an action event from a component intended to call this method
   */
  private void addCol(ActionEvent evt) {
    gridPanel.setMaxRowsCols(
        gridPanel.getMaxRowsCols().row, gridPanel.getMaxRowsCols().col + 1);
    Dimension gridSize = gridPanel.getPreferredSize();
    this.scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1200), Math.min(gridSize.height, 650)));
    this.pack();
    this.render();
  }

  /**
   * Adds a row to the grid view.
   * @param evt  an action event from a component intended to call this method
   */
  private void addRow(ActionEvent evt) {
    gridPanel.setMaxRowsCols(
        gridPanel.getMaxRowsCols().row + 1, gridPanel.getMaxRowsCols().col);
    Dimension gridSize = gridPanel.getPreferredSize();
    this.scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1200), Math.min(gridSize.height, 650)));
    this.pack();
    this.render();
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
    JTextField textField = this.gridPanel.getTextField();
    return textField == null || textField.getText().equals("") ? null : textField.getText();
  }

  @Override
  public void addFeatureListener(FeatureListener f) {
    this.gridPanel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (EditableGridWorksheetView.this.getActiveCell() == null) {
          Point clickPoint = e.getPoint();
          final int GRID_PANEL_BORDER = 17;
          clickPoint.translate(-GRID_PANEL_BORDER, -GRID_PANEL_BORDER); // Translate by the border surrounding gridPanel
          Coord selectedCell = EditableGridWorksheetView.this.gridPanel.pixelToCoord(clickPoint);
          if (selectedCell != null && selectedCell.col >= 1 && selectedCell.row >= 1) {
            f.onCellSelection(selectedCell);
          }
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

    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        System.out.println("Key typed:" + e.getKeyChar()); //TODO why isn't this keyboard listener listening!?
      }

      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          // Delete cell contents if backspace is pressed during cell selection
          case KeyEvent.VK_DELETE:
            if (EditableGridWorksheetView.this.getActiveCell() != null) {
              f.onCellContentsUpdate(EditableGridWorksheetView.this.getActiveCell(), null);
            }
            break;
          // Handle arrow key switching cell selection
          case KeyEvent.VK_UP:
          case KeyEvent.VK_DOWN:
          case KeyEvent.VK_LEFT:
          case KeyEvent.VK_RIGHT:
            Coord currentCoord = EditableGridWorksheetView.this.getActiveCell();
            int nextRow = currentCoord.row;
            int nextCol = currentCoord.col;
            switch (e.getKeyCode()) {
              case KeyEvent.VK_UP:
                nextRow -= 1;
                break;
              case KeyEvent.VK_DOWN:
                nextRow += 1;
                break;
              case KeyEvent.VK_LEFT:
                nextCol -= 1;
                break;
              case KeyEvent.VK_RIGHT:
                nextCol += 1;
                break;
            }
            Coord nextCoord;
            try {
              nextCoord = new Coord(nextCol, nextRow);
              f.onCellSelection(nextCoord);
              break;
            } catch (IllegalArgumentException ex) {
              break;
            }
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
