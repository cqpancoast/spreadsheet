package edu.cs3500.spreadsheets.provider.conversion;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.view.IWorksheetView;
import edu.cs3500.spreadsheets.provider.view.WorksheetView;

/**
 * Converts methods called on our {@link IWorksheetView} into method calls on our provider's
 * {@link WorksheetView}.
 */
public class WorksheetViewAdapter implements IWorksheetView {
  private final WorksheetView theirView;
  private Coord activeCell;

  /**
   * Creates a {@link WorksheetViewAdapter}.
   * @param view  a provider's view
   */
  public WorksheetViewAdapter(WorksheetView view) {
    this.theirView = view;
  }

  @Override
  public void render() {
    this.theirView.render();
  }

  @Override
  public void setActiveCell(Coord coord) {
    this.activeCell = coord;
  }

  @Override
  public Coord getActiveCell() {
    return this.activeCell;
  }

  @Override
  public void addFeatureListener(FeatureListener f) {
    // Do nothing
  }
}
