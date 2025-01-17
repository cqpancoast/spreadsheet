package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import java.io.IOException;
import java.util.Set;

/**
 * Represents a {@link IWorksheetModel} textually, saving this representation to some appendable.The
 * representation is an unordered set of cell-value pairs, with the scheme "[cell] [value]" for each
 * line in the file. The cell is the string representation of a cells position in the grid according
 * to the convention in {@link Coord}, while value is the <i>unevaluated</i> contents of that cell.
 */
public class TextualWorksheetView implements IWorksheetView {
  private final IWorksheetModel model;
  private final Appendable appendable;

  /**
   * Creates a {@link TextualWorksheetView}.
   * @param model a {@link IWorksheetModel} representing a worksheet
   * @param appendable the place that this view prints to
   * @throws IllegalArgumentException if model or appendable is null
   */
  public TextualWorksheetView(IWorksheetModel model, Appendable appendable) {
    if (model == null || appendable == null) {
      throw new IllegalArgumentException("Model and appendable cannot be null.");
    }
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
  public void setActiveCell(Coord coord) {
    // Textual class doesn't support a notion of active cells
  }

  @Override
  public Coord getActiveCell() {
    return null;
  }

  @Override
  public void addFeatureListener(FeatureListener f) {
    // Textual class doesn't need featureListeners
  }

  @Override
  public String toString() {

    StringBuilder viewString = new StringBuilder();
    Set<Coord> activeCells = this.model.getActiveCells();
    for (Coord coord : activeCells) {
      int col = coord.col;
      int row = coord.row;
      if (model.getRaw(col, row) != null) {
        viewString.append(Coord.colIndexToName(col)).append(row).append(" ");
        viewString.append(model.getRaw(col, row));
        viewString.append("\n");
      }
    }

    return viewString.toString().trim();
  }
}
