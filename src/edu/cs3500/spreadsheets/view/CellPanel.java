package edu.cs3500.spreadsheets.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class CellPanel extends JPanel {

  private static final int FONT_SIZE = 18;
  private static final int STRING_X = 15;
  private static final int STRING_Y = 15;
  private static final int DIMENSION_WIDTH = 100;
  private static final int DIMENSION_HEIGHT = 20;

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D)g;

    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

    g2d.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
    g2d.setColor(Color.BLACK);
    g2d.drawString("test long string for cutting shit off", STRING_X, STRING_Y);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(DIMENSION_WIDTH, DIMENSION_HEIGHT);
  }
}
