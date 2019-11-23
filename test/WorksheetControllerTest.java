import edu.cs3500.spreadsheets.controller.IWorksheetController;
import edu.cs3500.spreadsheets.controller.WorksheetController;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.view.IWorksheetView;
import org.junit.Test;

/**
 * Tests for {@link WorksheetController}. Callback methods on the controller are called explicitly,
 * and the effects on the view and controller are measured.
 */
public class WorksheetControllerTest {
  private IWorksheetController controller;

  /**
   * Initializes a controller with the given model and view.
   * @param model  any spreadsheet model
   * @param view  any spreadsheet view
   */
  private void initController(IWorksheetModel<?> model, IWorksheetView view) {
    controller = new WorksheetController(model);
    controller.setView(view);
  }

  /** Tests for {@link WorksheetController#setView(IWorksheetView)}. */

  @Test
  public void setView_() {

  }

  /** Tests for {@link WorksheetController#onCellSelection(Coord)} and
   * {@link WorksheetController#onCellDeselection()}.
   */

  @Test
  public void cellSelection_basicCases() {

  }

}
