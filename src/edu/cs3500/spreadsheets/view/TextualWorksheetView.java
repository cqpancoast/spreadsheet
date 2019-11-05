package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.WorksheetModel;
import java.io.IOException;

/**
 * TODO javadoc
 */
public class TextualWorksheetView implements WorksheetView {
  private final WorksheetModel<?> model;
  private final Appendable appendable;

  /**
   * Creates a {@link TextualWorksheetView}.
   * @param model a {@link WorksheetModel} representing a worksheet
   * @param appendable the place that this view prints to
   */
  public TextualWorksheetView(WorksheetModel<?> model, Appendable appendable) {
    this.model = model;
    this.appendable = appendable;
  }

  @Override
  public void render() {
    try {
      this.appendable.append(this.toString());
    } catch (IOException e) {
      throw new IllegalStateException("TextualWorksheetView's appendable object "
          + "cannot append string.");
    }
  }

  @Override
  public String toString() { //TODO
    return null;
  }
}
