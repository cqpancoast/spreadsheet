import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import edu.cs3500.spreadsheets.controller.FeatureListener;
import edu.cs3500.spreadsheets.controller.WorksheetController;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.view.EditableGridWorksheetView;
import edu.cs3500.spreadsheets.view.GridWorksheetView;
import edu.cs3500.spreadsheets.view.IWorksheetView;
import edu.cs3500.spreadsheets.view.TextualWorksheetView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * Tests for {@link WorksheetController}. Callback methods on the controller are called explicitly,
 * and the effects on the view and controller are measured.
 */
public class WorksheetControllerTest {
  private IWorksheetModel model;
  private IWorksheetView view;
  private WorksheetController controller;

  /**
   * Initializes all components of an MVC architecture.
   * - The model is a {@link FormulaWorksheetModel}
   * - The view is an {@link EditableGridWorksheetView}
   * - The controller is a {@link WorksheetController}
   * These implementations of the model and the view classes are chosen because they have the most
   * general application for testing the functionality of the controller.
   */
  private void initMVC() {
    initModel();
    initView(model);
    initController(model, view);
  }

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
   * Initializes a view with the given model.
   * @param model  any spreadsheet model
   */
  private void initView(IWorksheetModel model) {
    view = new EditableGridWorksheetView(model);
  }

  /**
   * Constructs a {@link IWorksheetModel} with some basic sample data.
   */
  private void initModel() {
    this.model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 2, "0") // the cell with column 5, row 2 is blank.
        .createCell(4, 3, "3").createCell(5, 3, "2")
        .createCell(4, 4, "1.00").createCell(5, 4, "4.5")
        .createCell(4, 5, "true").createCell(5, 5, "false")
        .createWorksheet();
  }

  /** Tests for {@link WorksheetController#setView(IWorksheetView)}. */

  @Test
  public void setViewTest() {
    initMVC();
    StringBuilder appendable = new StringBuilder();
    IWorksheetView textualView = new TextualWorksheetView(model, appendable);
    IWorksheetView visualView = new GridWorksheetView(model);
    IWorksheetView editableView = new EditableGridWorksheetView(model);
  }

  /** Tests for {@link WorksheetController#onCellSelection(Coord)} and
   * {@link WorksheetController#onCellDeselection()}. */

  @Test
  public void cellSelection_basicCases() {
    initMVC();
    assertNull(view.getActiveCell());
    controller.onCellSelection(new Coord(1, 1));
    assertEquals(new Coord(1, 1), view.getActiveCell());
    controller.onCellSelection(new Coord(4, 2));
    controller.onCellSelection(new Coord(1, 30));
    assertEquals(new Coord(1, 30), view.getActiveCell());
    controller.onCellDeselection();
    assertNull(view.getActiveCell());
    controller.onCellSelection(new Coord(1, 1));
    assertEquals(new Coord(1, 1), view.getActiveCell());
  }

  /** Tests for {@link WorksheetController#onCellContentsUpdate(Coord, String)}. */

  @Test
  public void cellContentsUpdate() {
    initMVC();
    assertEquals("0.0", model.getEval(4, 2));
    controller.onCellContentsUpdate(new Coord(4, 2), "9");
    assertEquals("9.0", model.getEval(4, 2));
    controller.onCellContentsUpdate(new Coord(4, 2), null);
    assertEquals("", model.getEval(4, 2));
  }

  /** Tests for {@link WorksheetController#save()}. */

  @Test
  public void saveToCorrectFile() {
    Path saveFilePath = Paths.get("savedFile.gOOD").toAbsolutePath();
    try {
      Files.deleteIfExists(saveFilePath);
    } catch (IOException e) {
      fail();
    }
    initMVC();
    controller.save();
    assertTrue(Files.exists(saveFilePath));
    try {
      Files.delete(saveFilePath);
    } catch (IOException e) {
      fail();
    }
  }

  /** There are no tests for {@link WorksheetController#quit()} as that exits the program, and as
   * its complicated functionality is tested implicitly by the other {@link FeatureListener}
   * tests. */

}
