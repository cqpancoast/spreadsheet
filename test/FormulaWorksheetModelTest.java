import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
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

  // All other constructor functionality is tested implicitly.

  /** Tests for {@link FormulaWorksheetModel#set(Coord, String)}. */

  /** Tests for {@link FormulaWorksheetModel#getEval(Coord)}. */

  /** Tests for {@link FormulaWorksheetModel#getRaw(Coord)}. */

  /** Tests for {@link FormulaWorksheetModel#getMaxRows()}. */

  /** Tests for {@link FormulaWorksheetModel#getMaxColumns()}. */

  /** Tests for {@link FormulaWorksheetModel#isValid()}. */

}