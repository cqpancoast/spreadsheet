package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
  private final IWorksheetModel model;
  private final GridPanel gridPanel;
  private Coord selected;
  private final JScrollPane scrollGrid;
  private JTextField textField;
  private JLabel textLabel;

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
    this.model = model;
    this.selected = null;

    this.setTitle("Spreadsheet GUI");

    // grid panel showing the active cells
    this.gridPanel = new GridPanel(model);
    Dimension gridSize = this.gridPanel.getPreferredSize();
    this.scrollGrid = new JScrollPane(this.gridPanel);
    scrollGrid.setBorder(BorderFactory.createEmptyBorder());
    scrollGrid.setPreferredSize(
        new Dimension(Math.min(gridSize.width, 1200), Math.min(gridSize.height, 650)));

    // button panel to increase number of rows or columns
    JPanel buttons = new JPanel();
    JButton addRow = new JButton("add row");
    JButton addCol = new JButton("add column");
    buttons.setLayout(new FlowLayout());
    buttons.add(new JLabel("Adjust grid size:"));
    buttons.add(addRow);
    buttons.add(addCol);
    addCol.addActionListener(this::addCol);
    addRow.addActionListener(this::addRow);

    // text field to enter changes to selected cells
    JPanel editPanel = new JPanel();
    editPanel.setLayout(new FlowLayout());
    textLabel = new JLabel();
    this.textLabel.setFont(new Font("TimesRoman", Font.PLAIN, 14));
    this.textField = new JTextField(20);
    this.textField.setFont(new Font("TimesRoman", Font.PLAIN, 14));
    this.textField.setBorder(javax.swing.BorderFactory.createLineBorder(Color.ORANGE));
    editPanel.add(this.textLabel);
    editPanel.add(this.textField);

    // buttons and editing field in one panel
    JPanel buttonsAndEdit = new JPanel();
    buttonsAndEdit.setLayout(new BorderLayout());
    buttonsAndEdit.add(buttons, BorderLayout.PAGE_START);
    buttonsAndEdit.add(editPanel, BorderLayout.CENTER);

    // add all necessary panels to the frame
    this.setLayout(new BorderLayout());
    this.add(scrollGrid, BorderLayout.CENTER);
    this.add(buttonsAndEdit, BorderLayout.PAGE_START);

    this.pack();

    this.setFocusable(true);
    this.requestFocus();
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
    this.requestFocus();
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
    this.requestFocus();
  }

  @Override
  public void render() {
    this.repaint();
    this.setVisible(true);
  }

  @Override
  public void setActiveCell(Coord coord) {
    this.gridPanel.setActiveCell(coord);
    if (coord != null) {
      textLabel.setText(gridPanel.getActiveCell().toString() + ": ");
      textField.setText(model.getRaw(coord.col, coord.row));
      selected = coord;
      gridPanel.setActiveCell(coord);
    } else {
      selected = null;
      gridPanel.setActiveCell(null);
      textField.setText("");
    }
  }

  @Override
  public Coord getActiveCell() {
    return this.gridPanel.getActiveCell();
  }

  @Override
  public void addFeatureListener(FeatureListener f) {
    this.gridPanel.addFeatureListener(f);
    this.gridPanel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (EditableGridWorksheetView.this.getActiveCell() == null) {
          Point clickPoint = e.getPoint();
          final int gridPanelBorder = 17;
          // Translate by the border surrounding gridPanel
          clickPoint.translate(-gridPanelBorder, -gridPanelBorder);
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
        // This mouseListener doesn't listen for this mouseEvent
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // This mouseListener doesn't listen for this mouseEvent
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // This mouseListener doesn't listen for this mouseEvent
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // This mouseListener doesn't listen for this mouseEvent
      }
    });

    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        // This keyListener doesn't listen for this keyEvent
      }

      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          // Delete cell contents if backspace is pressed during cell selection
          case KeyEvent.VK_BACK_SPACE:
            Coord active = EditableGridWorksheetView.this.getActiveCell();
            if (active != null) {
              f.onCellContentsUpdate(active, null);
              EditableGridWorksheetView.this.setActiveCell(null);
              EditableGridWorksheetView.this.setActiveCell(active);
              EditableGridWorksheetView.this.requestFocus();
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
              default:
                // unreachable code
            }
            Coord maxCoord = EditableGridWorksheetView.this.gridPanel.getMaxRowsCols();
            if (!(nextCol < 1 || nextRow < 1 || nextCol > maxCoord.col || nextRow > maxCoord.row)) {
              Coord nextCoord = new Coord(nextCol, nextRow);
              f.onCellSelection(nextCoord);
            }
            break;
          case KeyEvent.VK_S:
            f.save();
            break;
          case KeyEvent.VK_Q:
            f.quit();
            break;
          default:
            // do nothing
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // This keyListener doesn't listen for this keyEvent
      }
    });

    this.textField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        // This keyListener doesn't listen for this keyEvent
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (selected != null) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
              f.onCellContentsUpdate(new Coord(selected.col, selected.row), textField.getText());
              f.onCellDeselection();
              break;
            case KeyEvent.VK_ESCAPE:
              f.onCellDeselection();
              break;
            default:
              // do nothing
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // This keyListener doesn't listen for this keyEvent
      }
    });
  }
}
