package edu.cs3500.spreadsheets.provider.view;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.provider.model.ReadOnlyWorksheetModel;

/**
 * Represents a worksheet panel in the worksheet view.
 */
public class WorksheetPanel extends JPanel {

  private final ReadOnlyWorksheetModel model;
  private int horizontalScroll;
  private int verticalScroll;

  private final int cellWidth;
  private final int cellHeight;

  private int rows;
  private int cols;

  private Coord selected;

  /**
   * Creates a new view of the given read-only model.
   * @param model the model to render in this worksheet.
   * @throws IllegalArgumentException if the given model is null.
   */
  public WorksheetPanel(ReadOnlyWorksheetModel model, int rows, int cols,
                        int cellWidth, int cellHeight) {
    if (model == null) {
      throw new IllegalArgumentException("The model cannot be null");
    }

    if (cellWidth < 0 || cellHeight < 0) {
      throw new IllegalArgumentException("Cell width and height must be positive");
    }

    if (rows < 0 || cols < 0) {
      throw new IllegalArgumentException("The number of rows and columns must be positive");
    }

    this.model = model;
    this.horizontalScroll = 0;
    this.verticalScroll = 0;
    this.cellWidth = cellWidth;
    this.cellHeight = cellHeight;
    this.rows = rows;
    this.cols = cols;

    this.selected = null;
  }

  /**
   * Adjusts this panels x-offset and redraws this panel.
   * @param x the given offset for this panel.
   */
  public void scrollToXPos(int x) {
    horizontalScroll = x;
    repaint();
  }

  /**
   * Adjusts this panels y-offset and redraws this panel.
   * @param y the given offset for this panel.
   */
  public void scrollToYPos(int y) {
    verticalScroll = y;
    repaint();
  }

  /**
   * Returns the coordinate of a cell at the given position.
   * @param pos the position on the panel.
   * @return the nearest Coordinate to the given position.
   */
  public Coord getCoordAt(Point pos) {
    return new Coord((int)Math.floor((float)pos.x / (float)cellWidth) + horizontalScroll + 1,
            (int)Math.floor((float)pos.y / (float)cellHeight) + verticalScroll + 1);
  }

  /**
   * Sets the currently selected cell to be outlined.
   * @param c the coordinate of the cell to be selected.
   */
  public void setSelectedCell(Coord c) {
    this.selected = c;
  }

  public void setWorksheetSize(int numRows, int numCols) {
    this.rows = numRows;
    this.cols = numCols;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;

    g2d.setBackground(new Color(1.0f,0,0,0.5f));

    int cellsX = Math.max(model.getNumCol(), rows);
    int cellsY = Math.max(model.getNumRow(), cols);

    g2d.setPaint(Color.red);
    g2d.drawRect(-horizontalScroll * cellWidth, -verticalScroll * cellHeight,
            cellsX * cellWidth, cellsY * cellHeight);

    for (int x = 0; x < cellsX; x++) {
      for (int y = 0; y < cellsY; y++) {
        int cellX = (x - horizontalScroll) * cellWidth;
        int cellY = (y - verticalScroll) * cellHeight;

        if (cellX >= 0 && cellY >= 0 && cellX < 5000 && cellY < 5000) {
          // Draw blank cell back
          g2d.setPaint((selected != null
                  && selected.col - 1 == x
                  && selected.row - 1 == y) ? Color.darkGray : Color.lightGray);
          g2d.fillRect(cellX, cellY, cellWidth, cellHeight);
          g2d.setPaint((selected != null
                  && selected.col - 1 == x
                  && selected.row - 1 == y) ? Color.lightGray : Color.white);
          g2d.fillRect(cellX + 1, cellY + 1, cellWidth - 2, cellHeight - 2);

          // If this cell is nonempty draw its contents
          Coord c = new Coord(x + 1, y + 1);
          if (model.getNonEmptyCells().contains(c)) {
            g2d.setPaint(Color.black);

            g2d.drawString(this.model.getEvaluatedString(c),
                      cellX + 5,
                      cellY + 5 + cellHeight / 2);
          }
        }
      }
    }
  }
}
