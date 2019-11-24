import edu.cs3500.spreadsheets.controller.WorksheetController;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.view.IWorksheetView;
import org.junit.Test;

/**
 * Tests for {@link WorksheetController}. Callback methods on the controller are called explicitly,
 * and the effects on the view and controller are measured.
 */
public class WorksheetControllerTest {
  private WorksheetController controller;

  /**
   * Initializes a controller with the given model and view.
   * @param model  any spreadsheet model
   * @param view  any spreadsheet view
   */
  private void initController(IWorksheetModel model, IWorksheetView view) {
    controller = new WorksheetController(model);
    controller.setView(view);
  }

  /**
   * Constructs a {@link IWorksheetModel} with some basic sample data.
   * @return  a basic model
   */
  private IWorksheetModel initModel() {
    return new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 2, "0") // the cell with column 5, row 2 is blank.
        .createCell(4, 3, "3").createCell(5, 3, "2")
        .createCell(4, 4, "1.00").createCell(5, 4, "4.5")
        .createCell(4, 5, "true").createCell(5, 5, "false")
        .createWorksheet();
  }

  /** Tests for {@link WorksheetController#setView(IWorksheetView)}. */

  @Test
  public void setViewTest() {
    this.controller = new WorksheetController(initModel());
  }

  /** Tests for {@link WorksheetController#onCellSelection(Coord)} and
   * {@link WorksheetController#onCellDeselection()}.
   */

  @Test
  public void cellSelection_basicCases() {

  }

  /** Tests for {@link WorksheetController#onCellContentsUpdate(Coord, String)}. */

  /** Tests for {@link WorksheetController#save()}. */

  /** There are no tests for {@link WorksheetController#quit()} as that exits the program, and as
   * its functionality is simple. */

}
