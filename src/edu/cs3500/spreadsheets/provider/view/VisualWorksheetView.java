package edu.cs3500.spreadsheets.provider.view;

import edu.cs3500.spreadsheets.model.ReadOnlyWorksheetModel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * Represents a worksheet view the renders the worksheet visually.
 */
public class VisualWorksheetView extends JFrame implements WorksheetView {

  /**
   * Constructs a new visual view layout.
   * @param model the model to visualise with this view.
   * @throws IllegalArgumentException if the given model is null.
   */
  public VisualWorksheetView(ReadOnlyWorksheetModel model) throws IllegalArgumentException {
    super("michaelsoft XL");

    if (model == null) {
      throw new IllegalArgumentException("The model cannot be null");
    }

    // Set layout manager
    setLayout(new BorderLayout());

    // Create swing component
    ScrollableWorksheetPanel cellPanel = new ScrollableWorksheetPanel(model, 100, 20);

    // Add components
    Container c = getContentPane();
    c.add(cellPanel, BorderLayout.CENTER);

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(500,500);
  }

  @Override
  public void render() {
    setVisible(true);
  }

  @Override
  public void save() throws IOException {
    throw new UnsupportedOperationException("This is not supported.");
  }
}
