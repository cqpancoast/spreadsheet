package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.WorksheetModel;

/**
 * TODO javadoc
 */
public class TextualWorksheetView implements WorksheetView {
  private final WorksheetModel<?> model;

  /**
   * Creates a {@link TextualWorksheetView}.
   * @param model a {@link WorksheetModel} representing a worksheet.
   */
  public TextualWorksheetView(WorksheetModel<?> model) {
    this.model = model;
  }

  @Override
  public void render() {

  }
}
