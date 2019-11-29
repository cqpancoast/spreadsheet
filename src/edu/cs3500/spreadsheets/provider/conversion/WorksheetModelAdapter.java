package edu.cs3500.spreadsheets.provider.conversion;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.provider.model.Function; //NOTE
import edu.cs3500.spreadsheets.provider.model.ReadOnlyWorksheetModel;
import edu.cs3500.spreadsheets.provider.model.Value;
import edu.cs3500.spreadsheets.provider.model.WorksheetModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts method calls to the provider's {@link ReadOnlyWorksheetModel} into method calls on our
 * own {@link IWorksheetModel}. The {@link ReadOnlyWorksheetModel} is used instead of the more
 * general {@link WorksheetModel} because the only component of theirs being integrated is the view,
 * which only needs the read-only version. If the controller was being integrated as well, this
 * class would implement {@link WorksheetModel}.
 */
public class WorksheetModelAdapter implements ReadOnlyWorksheetModel {
  private final IWorksheetModel ourModel;

  /**
   * Creates a {@link WorksheetModelAdapter}.
   * @param ourModel  an instance of our model
   */
  public WorksheetModelAdapter(IWorksheetModel ourModel) {
    this.ourModel = ourModel;
  }

  @Override
  public String getBasicContentAt(Coord c) {
    return this.ourModel.getRaw(c.col, c.row);
  }

  @Override
  public String getEvaluatedString(Coord c) {
    return this.ourModel.getEval(c.col, c.row);
  }

  @Override
  public List<Coord> getNonEmptyCells() {
    return new ArrayList<>(this.ourModel.getActiveCells());
  }

  @Override
  public Function<List<Value>, Value> getSupportedFunctions(String s) { //TODO: consider
    return null;
  }

  @Override
  public int getNumRow() {
    return this.ourModel.getMaxRows();
  }

  @Override
  public int getNumCol() {
    return this.ourModel.getMaxColumns();
  }
}
