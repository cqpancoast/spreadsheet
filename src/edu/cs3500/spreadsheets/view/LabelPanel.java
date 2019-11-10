package edu.cs3500.spreadsheets.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class LabelPanel extends JPanel {
  private final String name;

  LabelPanel(String name) {
    this.name = name;
  }

  private static final int DIMENSION_WIDTH = 115;
  private static final int DIMENSION_HEIGHT = 25;

  private static final int FONT_SIZE = 14;
  private static final int STRING_X = DIMENSION_WIDTH / 2 - 5;
  private static final int STRING_Y = 16;

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D)g;

    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

    g2d.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
    g2d.setColor(Color.BLACK);
    g2d.drawString(name, STRING_X, STRING_Y);

    this.setBorder(BorderFactory.createLineBorder(Color.black));
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(DIMENSION_WIDTH, DIMENSION_HEIGHT);
  }
}
