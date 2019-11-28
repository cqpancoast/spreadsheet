package edu.cs3500.spreadsheets.provider.view;

import java.io.IOException;

import edu.cs3500.spreadsheets.model.Coord;

import edu.cs3500.spreadsheets.model.ReadOnlyWorksheetModel;

/**
 * Represents a text-based worksheet view.
 */
public class TextualWorksheetView implements WorksheetView {

  private final ReadOnlyWorksheetModel model;
  private final Appendable out;

  /**
   * Constructs a new textual view.
   * @param model the model to use for this view.
   * @param out the appendable to append to.
   * @throws IllegalArgumentException if either the model or the appendable is null.
   */
  public TextualWorksheetView(ReadOnlyWorksheetModel model, Appendable out)
          throws IllegalArgumentException {
    if (model == null || out == null) {
      throw new IllegalArgumentException("The model and the appendable cannot be null.");
    }

    this.model = model;
    this.out = out;
  }

  @Override
  public void render() {
    throw new UnsupportedOperationException("This is not supported.");
  }

  @Override
  public void save() throws IOException {
    for (Coord c : this.model.getNonEmptyCells().keySet()) {
      out.append(c.toString() + " " + this.model.getBasicContentAt(c) + "\n");
    }
  }
}
