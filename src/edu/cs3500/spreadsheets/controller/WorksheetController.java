package edu.cs3500.spreadsheets.controller;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.view.IWorksheetView;

/**
 * A concrete implementation of a {@link IWorksheetController} that works for all combinations of
 * model and view implementations.
 */
public class WorksheetController implements IWorksheetController, FeatureListener {
  private final IWorksheetModel model;
  private IWorksheetView view;

  /**
   * Creates a {@link WorksheetController}.
   * @param model  a spreadsheet model
   */
  public WorksheetController(IWorksheetModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Received null model");
    }
    this.model = model;
  }

  @Override
  public void setView(IWorksheetView view) {
    this.view = view;
    this.view.addFeatureListener(this);
  }

  @Override
  public void go() {

  }

  @Override
  public void onCellSelection(Coord c) {
    view.setActiveCell(c);
  }

  @Override
  public void onCellDeselection() {
    view.setActiveCell(null);
  }

  @Override
  public void onCellContentsUpdate(Coord c, String s) {
    model.set(c.col, c.row, s);
  }

  @Override
  public void save() {

  }

  @Override
  public void quit() {
    System.exit(0);
  }
}
