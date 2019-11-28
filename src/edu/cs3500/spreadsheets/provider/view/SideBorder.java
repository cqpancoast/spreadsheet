package edu.cs3500.spreadsheets.provider.view;

import java.awt.Dimension;

import java.awt.Color;

import java.awt.Graphics;

import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Represents the column labels in a visual spreadsheet.
 */
public class SideBorder extends JPanel {

  private int numCols;
  private int verticalScroll;

  /**
   * Constructs a new side border with the given number of columns.
   * @param numCols the number of columns to draw
   * @throws IllegalArgumentException if the given rows is less than zero.
   */
  public SideBorder(int numCols) throws IllegalArgumentException {
    if (numCols < 0) {
      throw new IllegalArgumentException("The number of columns cannot be less than zero");
    }

    setPreferredSize(new Dimension(100, 100));
    this.numCols = numCols;
    this.verticalScroll = 0;
  }

  /**
   * Adjusts this panels y-offset and then redraws the panel.
   * @param y the new y-offset.
   */
  public void scrollToYPos(int y) {
    verticalScroll = y;
    repaint();
  }

  public void setWorksheetSize(int numCols) {
    this.numCols = numCols;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;

    g2d.setBackground(Color.red);

    int cellWidth = 100;
    int cellHeight = 20;

    for (int y = 1; y <= Math.max(numCols, 200); y++) {
      int cellY = ((y - 1) * cellHeight) - (verticalScroll * cellHeight);

      g2d.setPaint(new Color(104, 114, 135));
      g2d.fillRect(0, cellY, cellWidth, cellHeight);

      g2d.setPaint(new Color(171, 189, 222));
      g2d.fillRect(1, cellY + 1, cellWidth - 2, cellHeight - 2);

      g2d.setPaint(new Color(61, 69, 84));
      g2d.drawString(y + "", 5, cellY + 5 + cellHeight / 2);
    }
  }
}
