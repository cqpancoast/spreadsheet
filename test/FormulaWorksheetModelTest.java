import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.sexp.SExpEvaluatorFormulaWorksheet;
import java.util.HashMap;
import org.junit.Test;

/**
 * Tests for {@link FormulaWorksheetModel}.
 */
public class FormulaWorksheetModelTest {



  /** Test for {@link FormulaWorksheetModel#FormulaWorksheetModel(HashMap)}. */

  @Test(expected = IllegalArgumentException.class)
  public void constructor_nullArgument() {
    new FormulaWorksheetModel(null);
  }

  /** Tests for {@link FormulaWorksheetModel.FormulaWorksheetBuilder}. */

  /** Tests for {@link FormulaWorksheetModel#set(Coord, String)}. */

  @Test
  public void set_genericCall() {

  }

  /** Tests for {@link FormulaWorksheetModel#getEval(Coord)}. This testing section is split into
   * subsections to account for the many different types of evaluation procedures that can go on.
   * Effectively, this acts as the testing suite for {@link SExpEvaluatorFormulaWorksheet}. */

  /** Tests for {@link FormulaWorksheetModel#getRaw(Coord)}. */

  /** Tests for {@link FormulaWorksheetModel#getMaxRows()}. */

  /** Tests for {@link FormulaWorksheetModel#getMaxColumns()}. */

  /** Tests for {@link FormulaWorksheetModel#isValid()}. */

}