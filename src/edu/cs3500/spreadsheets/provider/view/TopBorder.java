package edu.cs3500.spreadsheets.provider.view;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import edu.cs3500.spreadsheets.model.Coord;

/**
 * Represents the row labels in a visual spreadsheet.
 */
public class TopBorder extends JPanel {

  private int numRows;
  private int horizontalScroll;

  /**
   * Constructs a new topBorder with the given number of rows.
   * @param numRows the number of rows to have in the border.
   * @throws IllegalArgumentException if the given rows is less than zero.
   */
  public TopBorder(int numRows) throws IllegalArgumentException {
    if (numRows < 0) {
      throw new IllegalArgumentException("The number of columns cannot be less than zero");
    }

    setPreferredSize(new Dimension(20, 20));
    this.numRows = numRows;
    this.horizontalScroll = 0;
  }

  /**
   * Adjusts this panels x-offset and then redraws the panel.
   * @param x the new x-offset.
   */
  public void scrollToYPos(int x) {
    horizontalScroll = x;
    repaint();
  }

  public void setWorksheetSize(int numRows) {
    this.numRows = numRows;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;

    g2d.setBackground(Color.red);

    int cellWidth = 100;
    int cellHeight = 20;

    for (int x = 1; x <= Math.max(numRows, 200); x++) {
      int cellX = ((x - 1) * cellWidth) - (horizontalScroll * cellWidth);

      g2d.setPaint(new Color(104, 114, 135));
      g2d.fillRect(cellX, 0, cellWidth, cellHeight);

      g2d.setPaint(new Color(171, 189, 222));
      g2d.fillRect(cellX + 1, 1, cellWidth - 2, cellHeight - 2);

      g2d.setPaint(new Color(61, 69, 84));
      g2d.drawString(Coord.colIndexToName(x), cellX + 5 + cellWidth / 2, cellHeight / 2 + 5);
    }
  }
}
