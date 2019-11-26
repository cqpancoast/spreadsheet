package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The panel that holds the grid in a {@link GridWorksheetView}.
 */
public class GridPanel extends JPanel {
  private final IWorksheetModel model;
  private Coord selected;
  private JTextField textField;
  private int maxRows;
  private int maxCols;
  private List<FeatureListener> featureListeners;

  /**
   * Constructs a {@link GridPanel}.
   * @param model the {@link IWorksheetModel} used to build the grid
   * @param selected coordinates of selected cell
   */
  GridPanel(IWorksheetModel model, Coord selected) {
    this.model = model;
    this.setMaxRowsCols(model.getMaxRows(), model.getMaxColumns());
    this.selected = selected;
    this.textField = new JTextField();
    textField.setBackground(Color.ORANGE);
    textField.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
    textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    this.add(textField);
    textField.setSize(CELL_WIDTH, CELL_HEIGHT);
    textField.setLocation(45, 0);
    this.setLayout(null);
    this.featureListeners = new ArrayList<>();
  }

  private static final int CELL_WIDTH = 115;
  private static final int CELL_HEIGHT = 25;
  private static final int FONT_SIZE = 14;

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D)g;

    if (selected != null) {
      g2d.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
      g2d.drawString(this.selected.toString() + ":", 20, 17);
    }
    for (int i = 0; i <= maxCols; i++) {
      for (int j = 0; j <= maxRows; j++) {
        if (i == 0 && j == 0) {
          drawCell(g2d, 15, 30, Color.LIGHT_GRAY, "");
        }
        else if (i == 0) {
          drawCell(g2d, 15, 30 + j * CELL_HEIGHT, Color.LIGHT_GRAY, Integer.toString(j));
        }
        else if (j == 0) {
          drawCell(g2d,  15 + i * CELL_WIDTH, 30, Color.LIGHT_GRAY, Coord.colIndexToName(i));
        }
        else {
          drawCell(g2d, 15 + i * CELL_WIDTH, 30 + j * CELL_HEIGHT,
              Color.WHITE, model.getEval(i, j));
          if (selected != null && selected.col == i && selected.row == j) {
            final int col = i;
            final int row = j;
            textField.setText(model.getRaw(i, j));
            for (FeatureListener f : this.featureListeners) {
              textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                  switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                      f.onCellContentsUpdate(new Coord(col, row), textField.getText());
                      selected = null; //Deselect the cell
                      break;
                    case KeyEvent.VK_ESCAPE:
                      selected = null; //Deselect the cell
                      break;
                  }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
              });
            }
          }
        }
      }
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(
        (maxCols + 1) * CELL_WIDTH + 30, (maxRows + 1) * CELL_HEIGHT + 45);
  }

  /**
   * Draws an individual cell of the grid.
   *
   * @param g2d  the graphic that the cells are being drawn on
   * @param x  the x coordinate of the graphic to begin the cell at
   * @param y  the y coordinate of the graphic to begin the cell at
   * @param bgColor  the background color of the cell
   * @param text  the text to be displayed on the cell
   */
  private void drawCell(Graphics2D g2d, int x, int y, Color bgColor, String text) {
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, CELL_WIDTH, CELL_HEIGHT);
    g2d.setColor(bgColor);
    g2d.fillRect(x, y, CELL_WIDTH, CELL_HEIGHT);
    g2d.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
    g2d.setColor(Color.BLACK);
    while (g2d.getFontMetrics().stringWidth(text) >= 105) {
      text = text.substring(0, text.length() - 2);
    }
    g2d.drawString(text, x + 10, y + 17);
  }

  /**
   * Sets the number of rows and columns in the grid to the given number of rows and columns,
   * or three: whichever value is greater.
   *
   * @param rows  the desired amount of rows
   * @param cols  the desired amount of columns
   */
  void setMaxRowsCols(int rows, int cols) {
    this.maxRows = Math.max(3, rows);
    this.maxCols = Math.max(3, cols);
  }

  /**
   * Returns the {@link Coord} with the greatest rows and columns in this grid.
   */
  Coord getMaxRowsCols() {
    return new Coord(this.maxCols, this.maxRows);
  }

  /**
   * Returns the text field of the active cell.
   */
  JTextField getTextField() {
    return this.textField;
  }

  /**
   * Returns the {@link Coord} of the cell that the mouse clicked.
   * @param p  the x and y position of the mouse click in pixels
   */
  Coord pixelToCoord(Point p) {
    int col = (int)p.getX() / CELL_WIDTH;
    int row = ((int)p.getY() - 15) / CELL_HEIGHT;
    return col < 1 || row < 1 ? null : new Coord(col, row);
  }

  /**
   * Sets the parameterized cell as selected in the grid.
   * @param coord  the coordinate of the cell to be selected
   */
  void setActiveCell(Coord coord) {
    this.selected = coord;
  }

  /**
   * Returns the {@link Coord} of the cell that is currently selected.
   */
  Coord getActiveCell() {
    return selected == null ? null : new Coord(selected.col, selected.row);
  }

  /**
   * Adds a FeatureListener to listen for features inside of this GridPanel.
   * @param f  a FeatureListener
   */
  void addFeatureListener(FeatureListener f) {
    this.featureListeners.add(f);
  }
}
