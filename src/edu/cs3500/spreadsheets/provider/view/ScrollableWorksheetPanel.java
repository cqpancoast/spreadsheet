//package edu.cs3500.spreadsheets.provider.view;
//
//import java.awt.GridBagLayout;
//import java.awt.GridBagConstraints;
//import java.awt.Point;
//import java.awt.event.AdjustmentEvent;
//import java.awt.event.AdjustmentListener;
//import javax.swing.JPanel;
//import javax.swing.JScrollBar;
//import edu.cs3500.spreadsheets.model.Coord;
//import edu.cs3500.spreadsheets.provider.model.ReadOnlyWorksheetModel;
//
///**
// * Represents a Worksheet panel that scrolls.
// */
//public class ScrollableWorksheetPanel extends JPanel {
//
//  private final ReadOnlyWorksheetModel model;
//  private WorksheetPanel worksheetPanel;
//  private JScrollBar horizontal;
//  private JScrollBar vertical;
//  private TopBorder top;
//  private SideBorder side;
//
//  private final int cellWidth;
//  private final int cellHeight;
//
//  private int sheetWidth;
//  private int sheetHeight;
//
//  /**
//   * Constructs a new scrollable panel with the given model.
//   * @param model the given model.
//   * @throws IllegalArgumentException if the given model is null.
//   */
//  ScrollableWorksheetPanel(ReadOnlyWorksheetModel model, int cellWidth, int cellHeight) {
//    if (model == null) {
//      throw new IllegalArgumentException("The model cannot be null");
//    }
//
//    if (cellWidth < 0 || cellHeight < 0) {
//      throw new IllegalArgumentException("Cell width and height must be positive");
//    }
//
//    this.model = model;
//    this.cellWidth = cellWidth;
//    this.cellHeight = cellHeight;
//    this.sheetWidth = 200;
//    this.sheetHeight = 200;
//
//    top = new TopBorder(this.model.getNumCol());
//    side = new SideBorder(this.model.getNumRow());
//    horizontal = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1,
//            0, Math.max(this.model.getNumCol(), sheetWidth));
//    vertical = new JScrollBar(JScrollBar.VERTICAL, 0, 1,
//            0, Math.max(this.model.getNumRow(), sheetHeight));
//
//    horizontal.addAdjustmentListener(new AdjustmentListener() {
//      @Override
//      public void adjustmentValueChanged(AdjustmentEvent e) {
//        worksheetPanel.scrollToXPos(e.getValue());
//        top.scrollToYPos(e.getValue());
//
//        if (horizontal.getValue() > sheetWidth - 10) {
//          expandWorksheetView(sheetWidth + 10, sheetHeight);
//          horizontal.setMaximum(sheetWidth);
//        }
//      }
//    });
//
//    vertical.addAdjustmentListener(new AdjustmentListener() {
//      @Override
//      public void adjustmentValueChanged(AdjustmentEvent e) {
//        worksheetPanel.scrollToYPos(e.getValue());
//        side.scrollToYPos(e.getValue());
//
//        if (vertical.getValue() > sheetHeight - 10) {
//          expandWorksheetView(sheetWidth, sheetHeight + 10);
//          vertical.setMaximum(sheetHeight);
//        }
//      }
//    });
//
//    worksheetPanel = new WorksheetPanel(model,
//            this.sheetWidth,
//            this.sheetHeight,
//            this.cellWidth,
//            this.cellHeight);
//
//    setLayout(new GridBagLayout());
//    GridBagConstraints c = new GridBagConstraints();
//
//    c.fill = GridBagConstraints.BOTH;
//    c.anchor = GridBagConstraints.SOUTHWEST;
//    c.gridx = 1;
//    c.gridy = 0;
//    c.gridheight = 1;
//    c.weighty = 0;
//    c.weightx = 0;
//    add(top, c);
//
//    c.fill = GridBagConstraints.BOTH;
//    c.anchor = GridBagConstraints.NORTHEAST;
//    c.gridx = 0;
//    c.gridy = 1;
//    c.gridwidth = 1;
//    c.weightx = 0;
//    c.weighty = 0;
//    add(side, c);
//
//    c.fill = GridBagConstraints.BOTH;
//    c.anchor = GridBagConstraints.SOUTHEAST;
//    c.weightx = 1;
//    c.weighty = 1;
//    c.gridx = 1;
//    c.gridy = 1;
//
//    add(worksheetPanel, c);
//
//    c.fill = GridBagConstraints.VERTICAL;
//    c.anchor = GridBagConstraints.NORTHEAST;
//    c.gridx = 2;
//    c.gridy = 1;
//    c.weightx = 0;
//    c.weightx = 0.001f;
//    add(vertical, c);
//
//    c.fill = GridBagConstraints.HORIZONTAL;
//    c.anchor = GridBagConstraints.SOUTHWEST;
//    c.gridx = 1;
//    c.gridy = 2;
//    c.weightx = 0;
//    c.weighty = 0.001f;
//    add(horizontal, c);
//  }
//
//  /**
//   * Return the coordinate of cell that the position is over.
//   * @param pos the position to find the Coordinate of cell at.
//   * @return the Coordinate of the cell.
//   */
//  public Coord getCoordAt(Point pos) {
//    if (pos.x - cellWidth < 0 || pos.y - cellHeight < 0) {
//      return null;
//    }
//
//    return worksheetPanel.getCoordAt(new Point(pos.x - cellWidth, pos.y - cellHeight));
//  }
//
//  /**
//   * Expands the view to include the new amount of rows and columns. Does not expand in a direction
//   * if the new number is less than the old number of rows or columns.
//   * @param newRows the new number of rows.
//   * @param newCols the new number of colums.
//   */
//  public void expandWorksheetView(int newRows, int newCols) {
//    if (newRows > this.sheetWidth) {
//      this.sheetWidth = newRows;
//    }
//
//    if (newCols > this.sheetHeight) {
//      this.sheetHeight = newCols;
//    }
//
//    this.worksheetPanel.setWorksheetSize(this.sheetWidth, this.sheetHeight);
//
//    this.top.setWorksheetSize(this.sheetWidth);
//    this.side.setWorksheetSize(this.sheetHeight);
//  }
//
//  /**
//   * Highlights the Cell in red at the given coordinate.
//   * @param c the Coordinate of the cell to highlight.
//   */
//  public void setSelectedHighlight(Coord c) {
//    this.worksheetPanel.setSelectedCell(c);
//    this.repaint();
//  }
//}
