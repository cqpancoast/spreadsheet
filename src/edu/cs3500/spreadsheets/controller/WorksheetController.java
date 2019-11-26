package edu.cs3500.spreadsheets.controller;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.view.IWorksheetView;
import edu.cs3500.spreadsheets.view.TextualWorksheetView;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A concrete implementation of a {@link IWorksheetController} that works for all combinations of
 * model and view implementations. This controller is also a {@link FeatureListener}, and will
 * listen for callbacks from views that have installed it as one.
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
  public void commence() {
    this.view.render();
  }

  @Override
  public void onCellSelection(Coord c) {
    this.view.setActiveCell(c);
    this.view.render();
  }

  @Override
  public void onCellDeselection() {
    this.view.setActiveCell(null);
    this.view.render();
  }

  @Override
  public void onCellContentsUpdate(Coord c, String s) {
    this.model.set(c.col, c.row, s);
    this.view.render();
  }

  @Override
  public void save() {
    try {
      new TextualWorksheetView(this.model,
          new BufferedWriter(new FileWriter("savedFile.gOOD"))).render();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void quit() {
    System.exit(0);
  }
}
