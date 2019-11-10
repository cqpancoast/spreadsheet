package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.WorksheetModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CellPanel extends JPanel {
  private final WorksheetModel<?> model;
  private final int row;
  private final int col;
  private final boolean selected;

  CellPanel(WorksheetModel<?> model, int row, int col, boolean selected) {
    this.model = model;
    this.row = row;
    this.col = col;
    this.selected = selected;
  }

  private static final int DIMENSION_WIDTH = 115;
  private static final int DIMENSION_HEIGHT = 25;

  private static final int FONT_SIZE = 14;
  private static final int STRING_X = 10;
  private static final int STRING_Y = 17;

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D)g;

    Color cellColor = selected ? Color.ORANGE : Color.WHITE;
    g2d.setColor(cellColor);
    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

    String displayed = selected ? model.getRaw(col, row).toString() : model.getEval(col, row);
    if (!selected) {
      g2d.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
      g2d.setColor(Color.BLACK);
      g2d.drawString(displayed, STRING_X, STRING_Y);
    }
    if (selected) {
      JTextField textField = new JTextField(displayed);
      textField.setBackground(Color.ORANGE);
      textField.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
      textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
      this.add(textField);
    }

    this.setBorder(BorderFactory.createLineBorder(Color.black));
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(DIMENSION_WIDTH, DIMENSION_HEIGHT);
  }
}
