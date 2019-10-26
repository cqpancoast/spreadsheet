import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import edu.cs3500.spreadsheets.sexp.SExpEvaluatorFormulaWorksheet;
import java.util.HashMap;
import org.junit.Test;

/**
 * Tests for {@link FormulaWorksheetModel}.
 */
public class FormulaWorksheetModelTest {

  WorksheetModel<String> model;

  /**
   * Initializes a {@link FormulaWorksheetModel} with valid values roughly arranged into rows by
   * data type and testing purpose. Reference order is from larger column index to smaller column
   * index; that is, (except for specific tests to make sure that references work in the opposite
   * direction) references will always be a smaller column index referencing a larger column index.
   * This presents a nice way of dealing with functional composition: functions in column A compose
   * functions and data in column B, etc. //TODO provide link to image on imgur or something?
   */
  private void initWorksheetData() {
    this.model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 2, "0") // the cell with column 4, row 2 is blank.
        .createCell(4, 3, "3").createCell(5, 3, "7")
        .createCell(4, 4, "1").createCell(5, 4, "4")
        .createCell(4, 5, "true").createCell(5, 5, "false")
        .createCell(4, 6, "\"bees\"").createCell(5, 6, "\"friend\"")
        .createCell(4, 7, "\"true\"").createCell(5, 7, "\"7\"")
        .createCell(4, 8, "\"\"hey\"\"").createCell(5, 8, "\"\"")
        .createWorksheet();
  }

  /** Test for {@link FormulaWorksheetModel#FormulaWorksheetModel(HashMap)}. */

  @Test(expected = IllegalArgumentException.class)
  public void constructor_nullArgument() {
    new FormulaWorksheetModel(null);
  }

  // All other tests for the constructor are implicit in the builder test suite.

  /** Tests for {@link FormulaWorksheetModel.FormulaWorksheetBuilder}. */

  @Test
  public void builder_noArgs() {

  }

  @Test
  public void builder_genericCall() {

  }

  @Test(expected = IllegalArgumentException.class) // from Coord constructor
  public void builder_negativeIndices() {

  }

  @Test
  public void builder_overwrittenCells() {

  }

  @Test
  public void builder_canBuildInvalidWorksheets() {

  }

  // All other tests for the builder are implicit in the below test suite.

  /** Tests for {@link FormulaWorksheetModel#set(Coord, String)}. */

  @Test
  public void set_genericCall() {

  }

  @Test(expected = IllegalArgumentException.class) // from Coord constructor
  public void set_negativeIndices() {

  }

  @Test
  public void set_overwrittenCells() {

  }

  @Test
  public void set_canMakeInvalidWorksheets() {

  }

  // All other tests for set are implicit in the below test suite.

  /** Tests for {@link FormulaWorksheetModel#getEval(Coord)}.
   *
   * This testing section is split into subsections to account for the many different types of
   * evaluation procedures that can go on. Effectively, this acts as the testing suite for
   * {@link SExpEvaluatorFormulaWorksheet}. */

  /** Tests for {@link FormulaWorksheetModel#getRaw(Coord)}. */

  /** Tests for {@link FormulaWorksheetModel#getMaxRows()}. */

  /** Tests for {@link FormulaWorksheetModel#getMaxColumns()}. */

  /** Tests for {@link FormulaWorksheetModel#isValid()}. */

}