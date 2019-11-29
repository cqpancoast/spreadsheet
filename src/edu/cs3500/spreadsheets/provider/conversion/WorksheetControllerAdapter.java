package edu.cs3500.spreadsheets.provider.conversion;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.provider.controller.WorksheetController;
import edu.cs3500.spreadsheets.view.IWorksheetView;

/**
 * Converts methods called on our provider's {@link WorksheetController} into callbacks to our
 * {@link edu.cs3500.spreadsheets.controller.WorksheetController}.
 */
public class WorksheetControllerAdapter implements WorksheetController {
  private final edu.cs3500.spreadsheets.controller.WorksheetController ourController;
  private IWorksheetView ourView;

  /**
   * Creates a {@link WorksheetControllerAdapter}.
   * @param ourController  an instance of our controller implementation
   */
  public WorksheetControllerAdapter(
      edu.cs3500.spreadsheets.controller.WorksheetController ourController) {
    this.ourController = ourController;
  }

  /**
   * Runs this controller.
   */
  public void commence() {
    this.ourController.commence();
  }

  /**
   * Adds a view reference.
   * @param ourView  an instance of our view implementation
   */
  public void setView(IWorksheetView ourView) {
    this.ourView = ourView;
  }

  @Override
  public void updateCellAt(Coord c, String content) {
    if (content.equals("")) {
      ourController.onCellContentsUpdate(c, null);
    } else {
      ourController.onCellContentsUpdate(c, content);
    }
  }

  @Override
  public Coord getSelectedCellCoord() {
    return this.ourView.getActiveCell();
  }

  @Override
  public void setSelectedCell(Coord c) {
    this.ourController.onCellSelection(c);
  }
}
