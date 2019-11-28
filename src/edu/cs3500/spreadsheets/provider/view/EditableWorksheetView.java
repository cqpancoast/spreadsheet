package edu.cs3500.spreadsheets.provider.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JFrame;
import edu.cs3500.spreadsheets.controller.WorksheetController;
import edu.cs3500.spreadsheets.provider.model.ReadOnlyWorksheetModel;

/**
 * Represents a Worksheet view that the user can edit.
 * This view works with a controller to manipulate the model.
 */
public class EditableWorksheetView extends JFrame implements WorksheetView {

  private final ReadOnlyWorksheetModel model;
  private final WorksheetController controller;

  /**
   * Constructs a new visual view layout.
   * @param model the model to visualise with this view.
   * @throws IllegalArgumentException if the given model is null.
   */
  public EditableWorksheetView(ReadOnlyWorksheetModel model,
                               WorksheetController controller) throws IllegalArgumentException {
    super("michaelsoft XL");

    if (model == null || controller == null) {
      throw new IllegalArgumentException("The model cannot be null");
    }

    this.model = model;
    this.controller = controller;

    // Set layout manager
    this.setLayout(new BorderLayout());

    // Create swing component
    ScrollableWorksheetPanel cellPanel = new ScrollableWorksheetPanel(this.model,
            100,
            20);
    ToolBar toolBar = new ToolBar(model, controller);

    cellPanel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // Do nothing
      }

      @Override
      public void mousePressed(MouseEvent e) {
        controller.setSelectedCell(cellPanel.getCoordAt(e.getPoint()));
        cellPanel.setSelectedHighlight(controller.getSelectedCellCoord());
        toolBar.setEditableContent(model.getCellAt(
                controller.getSelectedCellCoord()).getBasicCellContent());
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // Do nothing
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // Do nothing
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // Do nothing
      }
    });

    // Add components
    Container c = getContentPane();
    c.add(toolBar, BorderLayout.NORTH);
    c.add(cellPanel, BorderLayout.CENTER);

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(500,500);
  }

  @Override
  public void save() throws IOException {
    return;
  }

  @Override
  public void render() {
    setVisible(true);
  }
}
