import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.model.SExpEvaluatorFormulaWorksheet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

/**
 * Tests for {@link FormulaWorksheetModel}.
 */
public class FormulaWorksheetModelTest {

  private IWorksheetModel model;
  private static final String errorInvalidBlockCellRef = "!#ERROR_INVALIDBLOCKCELLREF";
  private static final String errorInvalidSymbol = "!#ERROR_INVALIDSYMBOL";
  private static final String errorInvalidCommand = "!#ERROR_INVALIDCOMMAND";
  private static final String errorCyclicRef = "!#ERROR_CYCLICREF";
  private static final String errorArgIsError = "!#ERROR_ARGISERROR";
  private static final String errorArgType = "!#ERROR_ARGTYPE";
  private static final String errorInvalidArity = "!#ERROR_ARITY";
  private static final String errorSyntax = "!#ERROR_SYNTAX";

  /**
   * Initializes a {@link FormulaWorksheetModel} with valid values roughly arranged into rows by
   * data type and testing purpose. Reference order is from larger column index to smaller column
   * index; that is, (except for specific tests to make sure that references work in the opposite
   * direction) references will always be a smaller column index referencing a larger column index.
   * This presents a nice way of dealing with functional composition: functions in column A compose
   * functions and data in column B, etc.
   */
  private void initWorksheetData() {
    this.model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 2, "0") // the cell with column 5, row 2 is blank.
        .createCell(4, 3, "3").createCell(5, 3, "2")
        .createCell(4, 4, "1.00").createCell(5, 4, "4.5")
        .createCell(4, 5, "true").createCell(5, 5, "false")
        .createCell(4, 6, "\"bees\"").createCell(5, 6, "\"friend\"")
        .createCell(4, 7, "\"true\"").createCell(5, 7, "\"7.0\"")
        .createCell(4, 8, "\"\"hey\"\"").createCell(5, 8, "\"\"")
        .createWorksheet();
  }

  /**
   * Sets the cell corresponding to cellString (as per the association of strings to row/column
   * pairs in {@link Coord}) to val.
   * @param cellString the string corresponding to the cell to be set
   * @param val the value to be set
   */
  private void setModel(String cellString, String val) {
    List<Integer> fromString = Coord.fromString(cellString);
    this.model.set(fromString.get(0), fromString.get(1), val);
  }

  /**
   * Gets the raw contents of the cell corresponding to cellString.
   * @param cellString the string corresponding to the cell to be accessed
   */
  private String getRawModel(String cellString) {
    List<Integer> fromString = Coord.fromString(cellString);
    return this.model.getRaw(fromString.get(0), fromString.get(1));
  }

  /**
   * Gets the evaluated contents the cell corresponding to cellString.
   * @param cellString the string corresponding to the cell to be accessed
   */
  private String getEvalModel(String cellString) {
    List<Integer> fromString = Coord.fromString(cellString);
    return this.model.getEval(fromString.get(0), fromString.get(1));
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
    FormulaWorksheetModel model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createWorksheet();
    assertNull(model.getRaw(4, 3));
    assertNull(model.getRaw(5, 3));
    assertEquals("", model.getEval(4, 3));
    assertEquals("", model.getEval(5, 3));
  }

  @Test
  public void builder_genericCall() {
    FormulaWorksheetModel model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 3, "3").createCell(5, 3, "2")
        .createCell(4, 4, "1.00").createCell(5, 3, "4.5")
        .createWorksheet();
    assertEquals("3", model.getRaw(4, 3));
    assertEquals("4.5", model.getRaw(5, 3));
  }

  @Test(expected = IllegalArgumentException.class) // from Coord constructor
  public void builder_negativeIndices() {
    new FormulaWorksheetModel.FormulaWorksheetBuilder().createCell(1, 1, "4")
        .createCell(4, -2, "");
  }

  @Test
  public void builder_overwrittenCells() {
    FormulaWorksheetModel model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 3, "3").createCell(5, 3, "2")
        .createCell(4, 4, "1.00").createCell(4, 3, "4.5")
        .createWorksheet();
    assertEquals("2", model.getRaw(5, 3));
    assertEquals("4.5", model.getRaw(4, 3));
  }

  @Test
  public void builder_canBuildInvalidWorksheets() {
    FormulaWorksheetModel model = new FormulaWorksheetModel.FormulaWorksheetBuilder()
        .createCell(4, 3, "3").createCell(5, 3, "2")
        .createCell(4, 4, "=(< 4)")
        .createWorksheet();
    assertEquals("2.0", model.getEval(5, 3));
    assertEquals(errorInvalidArity, model.getEval(4, 4));
  }
  // All other tests for the builder are implicit in the below test suite.

  /** Tests for {@link FormulaWorksheetModel#set(int, int, String)}. */

  @Test
  public void set_genericCall() {
    initWorksheetData();
    setModel("C2", "3");
    setModel("C3", "true");
    setModel("C4", "\"string\"");
    assertEquals("3", getRawModel("C2"));
    assertEquals("true", getRawModel("C3"));
    assertEquals("\"string\"", getRawModel("C4"));
  }

  @Test(expected = IllegalArgumentException.class) // from Coord constructor
  public void set_negativeIndices() {
    initWorksheetData();
    this.model.set(-1, 3, "");
  }

  @Test
  public void set_overwrittenCells() {
    this.initWorksheetData();
    assertEquals("0", getRawModel("D2"));
    setModel("D2", "\"newValue\"");
    assertEquals("\"newValue\"", getRawModel("D2"));
  }

  @Test
  public void set_deletedCells() {
    this.initWorksheetData();
    assertEquals("0", getRawModel("D2"));
    setModel("D2", null);
    assertNull(getRawModel("D2"));
    setModel("D2", "34");
    assertEquals("34", getRawModel("D2"));
  }

  @Test
  public void set_canMakeInvalidWorksheets() {
    initWorksheetData();
    setModel("C2", "=(< 4)");
    assertEquals("3.0", getEvalModel("D3"));
    assertEquals(errorInvalidArity, getEvalModel("C2"));
  }
  // All other tests for set are implicit in all of the below tests.

  /** Tests for {@link FormulaWorksheetModel#getEval(int, int)}. This testing section is split into
   * subsections to account for the many different types of evaluation procedures that can go on.
   * Effectively, this acts as the testing suite for {@link SExpEvaluatorFormulaWorksheet}. */

  //** VALUES **//

  //* VALUES: Things go right *//
  @Test
  public void getEval_values_double() {
    initWorksheetData();
    setModel("C2", "54");
    setModel("C3", "32.4");
    setModel("C4", "=(SUM 3 2)");
    assertEquals("54.0", getEvalModel("C2"));
    assertEquals("32.4", getEvalModel("C3"));
    assertEquals("5.0", getEvalModel("C4"));
  }

  @Test
  public void getEval_values_boolean() {
    initWorksheetData();
    setModel("C2", "true");
    setModel("C3", "false");
    assertEquals("true", getEvalModel("C2"));
    assertEquals("false", getEvalModel("C3"));
  }

  @Test
  public void getEval_values_stringGeneric() {
    initWorksheetData();
    setModel("C2", "\"yeet\"");
    setModel("C3", "\"yeehaw\"");
    assertEquals("\"yeet\"", getEvalModel("C2"));
    assertEquals("\"yeehaw\"", getEvalModel("C3"));
  }

  @Test
  public void getEval_values_stringBoolean() {
    initWorksheetData();
    setModel("C2", "\"true\"");
    setModel("C3", "\"false\"");
    assertEquals("\"true\"", getEvalModel("C2"));
    assertEquals("\"false\"", getEvalModel("C3"));
  }

  @Test
  public void getEval_values_stringDouble() {
    initWorksheetData();
    setModel("C2", "\"4.2\"");
    setModel("C3", "\"3.0\"");
    assertEquals("\"4.2\"", getEvalModel("C2"));
    assertEquals("\"3.0\"", getEvalModel("C3"));
  }

  @Test
  public void getEval_values_emptyString() {
    initWorksheetData();
    setModel("C2", "\"\"");
    assertEquals("\"\"", getEvalModel("C2"));
  }

  //* VALUES: Invalid syntax (basically just symbols) *//
  @Test
  public void getEval_values_randomSymbol() {
    initWorksheetData();
    setModel("C2", "SUM PRODUCT");
    assertEquals(errorSyntax, getEvalModel("C2"));
  }

  // Evaluation when symbols are references are covered below.
  
  //** FORMULAE **//

  //* FORMULAE: Just values *//

  @Test
  public void getEval_formulae_double() {
    initWorksheetData();
    setModel("C2", "=4");
    setModel("C3", "=3.1");
    assertEquals("4.0", getEvalModel("C2"));
    assertEquals("3.1", getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_boolean() {
    initWorksheetData();
    setModel("C2", "=true");
    setModel("C3", "=false");
    assertEquals("true", getEvalModel("C2"));
    assertEquals("false", getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_string() {
    initWorksheetData();
    setModel("C2", "=\"string\"");
    setModel("C3", "=\"street\"");
    assertEquals("\"string\"", getEvalModel("C2"));
    assertEquals("\"street\"", getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_randomSymbol() {
    initWorksheetData();
    setModel("C2", "RANDOM");
    assertEquals(errorInvalidSymbol, getEvalModel("C2"));
  }

  //* FORMULAE: Just references *//
  @Test
  public void getEval_formulae_blankReference() {
    this.initWorksheetData();
    setModel("C2", "=A1");
    assertEquals("", getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_valueReference() {
    this.initWorksheetData();
    setModel("C2", "=D6");
    assertEquals("\"bees\"", getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_formulaReference() {
    this.initWorksheetData();
    setModel("C2", "=C3");
    setModel("C3", "=(SUM 4 3.2 -1)");
    assertEquals("6.2", getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_errorPropagatesThroughReference() {
    initWorksheetData();
    setModel("C2", "=(< 4)");
    setModel("C3", "=C2");
    assertEquals(errorInvalidArity, getEvalModel("C2"));
    assertEquals(errorInvalidArity, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_differentError() {
    initWorksheetData();
    setModel("C2", "=C3");
    setModel("C3", "=C2");
    setModel("C4", "=C4");
    assertEquals(errorCyclicRef, getEvalModel("C2"));
    assertEquals(errorCyclicRef, getEvalModel("C3"));
    assertEquals(errorCyclicRef, getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_attemptedEvaluationOfReferenceBlock() {
    initWorksheetData();
    setModel("C2", "=D3:E4");
    assertEquals(errorInvalidBlockCellRef, getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_referenceCannotBeInParens() {
    initWorksheetData();
    setModel("C2", "=(D3)");
    assertEquals(errorInvalidCommand, getEvalModel("C2"));
  }

  //* FORMULAE: Functions *//

  // Functions: Things go right //
  @Test
  public void getEval_formulae_SUM_justNonReferents() {
    initWorksheetData();
    setModel("C2", "=(SUM 4 3.2 -1.0)");
    setModel("C3", "=(SUM 5 8)");
    setModel("C4", "=(SUM 4.2 -7)");
    assertEquals("6.2", getEvalModel("C2"));
    assertEquals("13.0", getEvalModel("C3"));
    assertEquals("-2.8", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_SUM_singleReferents() {
    initWorksheetData();
    setModel("C2", "=(SUM D3 5.1)");
    setModel("C3", "=(SUM -6 D4)");
    setModel("C4", "=(SUM E3 1.9)");
    assertEquals("8.1", getEvalModel("C2"));
    assertEquals("-5.0", getEvalModel("C3"));
    assertEquals("3.9", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_SUM_blockReferents() {
    initWorksheetData();
    setModel("C2", "=(SUM D3:E3)");
    setModel("C3", "=(SUM E3:E4)");
    setModel("C4", "=(SUM D3:E4)");
    setModel("C5", "=(SUM D:D)");
    setModel("C6", "=(SUM E:E)");
    setModel("C7", "=(SUM D:E)");
    assertEquals("5.0", getEvalModel("C2"));
    assertEquals("6.5", getEvalModel("C3"));
    assertEquals("10.5", getEvalModel("C4"));
    assertEquals("4.0", getEvalModel("C5"));
    assertEquals("6.5", getEvalModel("C6"));
    assertEquals("10.5", getEvalModel("C7"));
  }

  @Test
  public void getEval_formulae_SUM_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(SUM D3 E4)");
    setModel("C3", "=(SUM D3 E4 D3)");
    setModel("C4", "=C2");
    setModel("C5", "=(SUM (SUM 4 3) (PRODUCT 2.0 1))");
    assertEquals("7.5", getEvalModel("C2"));
    assertEquals("10.5", getEvalModel("C3"));
    assertEquals("7.5", getEvalModel("C4"));
    assertEquals("9.0", getEvalModel("C5"));
  }

  @Test
  public void getEval_formulae_SUM_blankOrNonNumCells() {
    initWorksheetData();
    setModel("C2", "=(SUM C1 D3)");
    setModel("C3", "=(SUM B1 C1)");
    setModel("C4", "=(SUM D3 E3 C1)");
    setModel("C5", "=(SUM)");
    setModel("C6", "=(SUM true \"street\")");
    setModel("C7", "=(SUM true 7 \"street\")");
    assertEquals("3.0", getEvalModel("C2"));
    assertEquals("0.0", getEvalModel("C3"));
    assertEquals("5.0", getEvalModel("C4"));
    assertEquals("0.0", getEvalModel("C5"));
    assertEquals("0.0", getEvalModel("C6"));
    assertEquals("7.0", getEvalModel("C7"));
  }

  @Test
  public void getEval_formulae_PRODUCT_justNonReferents() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT 4 3.2 -1.0)");
    setModel("C3", "=(PRODUCT 5 8)");
    setModel("C4", "=(PRODUCT 4.2 -7.0)");
    assertEquals("-12.8", getEvalModel("C2"));
    assertEquals("40.0", getEvalModel("C3"));
    assertEquals(-29.4, Double.parseDouble(getEvalModel("C4")), .001);
  }

  @Test
  public void getEval_formulae_PRODUCT_singleReferents() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT D3 5.1)");
    setModel("C3", "=(PRODUCT -6 D4)");
    setModel("C4", "=(PRODUCT E3 1.9)");
    assertEquals(15.3, Double.parseDouble(getEvalModel("C2")), .001);
    assertEquals("-6.0", getEvalModel("C3"));
    assertEquals("3.8", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_PRODUCT_blockReferents() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT D3:E3)");
    setModel("C3", "=(PRODUCT E3:E4)");
    setModel("C4", "=(PRODUCT D3:E4)");
    setModel("C5", "=(PRODUCT D:D)");
    setModel("C6", "=(PRODUCT E:E)");
    setModel("C7", "=(PRODUCT D:E)");
    assertEquals("6.0", getEvalModel("C2"));
    assertEquals("9.0", getEvalModel("C3"));
    assertEquals("27.0", getEvalModel("C4"));
    assertEquals("0.0", getEvalModel("C5"));
    assertEquals("9.0", getEvalModel("C6"));
    assertEquals("0.0", getEvalModel("C7"));
  }

  @Test
  public void getEval_formulae_PRODUCT_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT D3 E4)");
    setModel("C3", "=(PRODUCT D3 E4 D3)");
    setModel("C4", "=C2");
    setModel("C5", "=(PRODUCT (SUM 1 2) (PRODUCT 2 E4))");
    assertEquals("13.5", getEvalModel("C2"));
    assertEquals("40.5", getEvalModel("C3"));
    assertEquals("13.5", getEvalModel("C4"));
    assertEquals("27.0", getEvalModel("C5"));
  }

  @Test
  public void getEval_formulae_PRODUCT_blankOrNonNumCells() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT C1 D3)");
    setModel("C3", "=(PRODUCT B1 C1)");
    setModel("C4", "=(PRODUCT D3 E3 C1)");
    setModel("C5", "=(PRODUCT)");
    setModel("C6", "=(PRODUCT true \"street\")");
    setModel("C7", "=(PRODUCT true 7 \"street\")");
    assertEquals("3.0", getEvalModel("C2"));
    assertEquals("0.0", getEvalModel("C3"));
    assertEquals("6.0", getEvalModel("C4"));
    assertEquals("0.0", getEvalModel("C5"));
    assertEquals("0.0", getEvalModel("C6"));
    assertEquals("7.0", getEvalModel("C7"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_justNonReferents() {
    initWorksheetData();
    setModel("C2", "=(< 4 5)");
    setModel("C3", "=(< 3.1 2.9)");
    setModel("C4", "=(< 83 83)");
    assertEquals("true", getEvalModel("C2"));
    assertEquals("false", getEvalModel("C3"));
    assertEquals("false", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_singleReferents() {
    initWorksheetData();
    setModel("C2", "=(< D3 5)");
    setModel("C3", "=(< 3.1 D4)");
    setModel("C4", "=(< 4.5 E4)");
    assertEquals("true", getEvalModel("C2"));
    assertEquals("false", getEvalModel("C3"));
    assertEquals("false", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(< E3 D3)");
    setModel("C3", "=(< E4 E3)");
    setModel("C4", "=(< D3 D3)");
    setModel("C5", "=(< (PRODUCT 1 2.9) (SUM 3.0 2.89))");
    assertEquals("true", getEvalModel("C2"));
    assertEquals("false", getEvalModel("C3"));
    assertEquals("false", getEvalModel("C4"));
    assertEquals("true", getEvalModel("C5"));
  }

  @Test
  public void getEval_formulae_ENUM_justNonReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM 5 false \"bees\")");
    setModel("C3", "=(ENUM true 1.1 \"string\")");
    setModel("C4", "=(ENUM \"yeehaw\" 4 false)");
    setModel("C5", "=(ENUM \"\\\"double\\\"\" 9.1)");
    setModel("C6", "=(ENUM \"\\\\double\\\\\" 9.1)");
    assertEquals("5.0 false \"bees\"", getEvalModel("C2"));
    assertEquals("true 1.1 \"string\"" , getEvalModel("C3"));
    assertEquals("\"yeehaw\" 4.0 false", getEvalModel("C4"));
    assertEquals("\"\"double\"\" 9.1", getEvalModel("C5"));  // arg is error
    assertEquals("\"\\double\\\" 9.1", getEvalModel("C6"));  // error syntax
  }

  @Test
  public void getEval_formulae_ENUM_singleReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM 5 E5 \"bees\")");
    setModel("C3", "=(ENUM true E4 \"string\")");
    setModel("C4", "=(ENUM E6 4 false)");
    assertEquals("5.0 false \"bees\"", getEvalModel("C2"));
    assertEquals("true 4.5 \"string\"" , getEvalModel("C3"));
    assertEquals("\"friend\" 4.0 false", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_ENUM_blockReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM D3:D7)");
    setModel("C3", "=(ENUM D3:D4 E6:E7)");
    setModel("C4", "=(ENUM D5:E7)");
    setModel("C5", "=(ENUM D:D)");
    setModel("C6", "=(ENUM E:E)");
    setModel("C7", "=(ENUM D:E)");
    assertEquals(
        "0.0 3.0 1.0 true \"bees\" \"true\" \"\"hey\"\"", getEvalModel("C5"));
    assertEquals("2.0 4.5 false \"friend\" \"7.0\" \"\"" , getEvalModel("C6"));
    assertEquals("0.0 3.0 1.0 true \"bees\" \"true\" \"\"hey\"\" "
            + "2.0 4.5 false \"friend\" \"7.0\" \"\"", getEvalModel("C7"));
  }

  @Test
  public void getEval_formulae_ENUM_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM D3 D7)");
    setModel("C3", "=(ENUM D3 D4 E6:E7)");
    setModel("C4", "=(ENUM D5 E7)");
    setModel("C5", "=(ENUM C3 C4)");
    assertEquals("3.0 \"true\"", getEvalModel("C2"));
    assertEquals("3.0 1.0 \"friend\" \"7.0\"" , getEvalModel("C3"));
    assertEquals("true \"7.0\"", getEvalModel("C4"));
    assertEquals("3.0 1.0 \"friend\" \"7.0\" true \"7.0\"", getEvalModel("C5"));
  }

  @Test
  public void getEval_formulae_ENUM_blankCells() {
    initWorksheetData();
    setModel("C2", "=(ENUM A2 D7)");
    setModel("C3", "=(ENUM D3 A4  E6 E7)");
    setModel("C4", "=(ENUM A2 A7)");
    assertEquals("<blank> \"true\"", getEvalModel("C2"));
    assertEquals("3.0 <blank> \"friend\" \"7.0\"" , getEvalModel("C3"));
    assertEquals("<blank> <blank>", getEvalModel("C4"));
  }

  // Functions: Invalid syntax //
  @Test
  public void getEval_formulae_unrecognizedCommand() {
    this.initWorksheetData();
    setModel("C2", "=(BEES 4 \"seventy\")");
    assertEquals(errorInvalidCommand, getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_invalidBlankCellReference() {
    initWorksheetData();
    setModel("C2", "=(< D44 D5)");
    assertEquals(errorArgIsError, getEvalModel("C2"));
  }

  // Functions: Invalid arguments //
  @Test
  public void getEval_formulae_SUM_errorProp() {
    initWorksheetData();
    setModel("C2", "=(< 4)");
    setModel("C3", "=(SUM C2 3 6)");
    assertEquals(errorInvalidArity, getEvalModel("C2"));
    assertEquals(errorArgIsError, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_PRODUCT_errorProp() {
    initWorksheetData();
    setModel("C2", "=(< 4)");
    setModel("C3", "=(PRODUCT C2 3 6)");
    assertEquals(errorInvalidArity, getEvalModel("C2"));
    assertEquals(errorArgIsError, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_errorProp() {
    initWorksheetData();
    setModel("C2", "=(< 4)");
    setModel("C3", "=(< C2 6)");
    assertEquals(errorInvalidArity, getEvalModel("C2"));
    assertEquals(errorArgIsError, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_incorrectNumArgs() {
    initWorksheetData();
    setModel("C2", "=(< 4 5 7)");
    setModel("C3", "=(< 6)");
    assertEquals(errorInvalidArity, getEvalModel("C2"));
    assertEquals(errorInvalidArity, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_incorrectArgType() {
    initWorksheetData();
    setModel("C2", "=(< 4 true)");
    setModel("C3", "=(< \"string\" 6)");
    assertEquals(errorArgType, getEvalModel("C2"));
    assertEquals(errorArgIsError, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_blockReferents() {
    initWorksheetData();
    setModel("C2", "=(< D4:E4 3)");
    setModel("C3", "=(< D5:E5 false)");
    assertEquals(errorArgIsError, getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_LESSTHAN_blankCells() {
    initWorksheetData();
    setModel("C2", "=(< D2 E2)");
    setModel("C3", "=(< E2 5)");
    assertEquals(errorArgIsError, getEvalModel("C2"));
    assertEquals(errorArgIsError, getEvalModel("C3"));
  }

  @Test
  public void getEval_formulae_ENUM_errorProp() {
    initWorksheetData();
    setModel("C2", "=(< 4)");
    setModel("C3", "=(ENUM C2");
    assertEquals(errorInvalidArity, getEvalModel("C2"));
    assertEquals(errorSyntax, getEvalModel("C3"));
  }

  /** Tests for {@link FormulaWorksheetModel#getRaw(int, int)}. */
  
  @Test
  public void getRaw_doubleWithUnnecessaryDecimals() {
    initWorksheetData();
    setModel("C2", "4.0000");
    setModel("C3", "-3.00");
    assertEquals("4.0000", getRawModel("C2"));
    assertEquals("-3.00", getRawModel("C3"));
  }

  @Test
  public void getRaw_doubleWithNecessaryDecimals() {
    initWorksheetData();
    setModel("C2", "4.21");
    setModel("C3", "-3.93");
    assertEquals("4.21", getRawModel("C2"));
    assertEquals("-3.93", getRawModel("C3"));
  }

  @Test
  public void getRaw_doubleNoDecimals() {
    initWorksheetData();
    setModel("C2", "4");
    setModel("C3", "-3");
    assertEquals("4", getRawModel("C2"));
    assertEquals("-3", getRawModel("C3"));
  }

  @Test
  public void getRaw_string() {
    initWorksheetData();
    setModel("C2", "\"string\"");
    setModel("C3", "\"street\"");
    assertEquals("\"string\"", getRawModel("C2"));
    assertEquals("\"street\"", getRawModel("C3"));
  }

  @Test
  public void getRaw_boolean() {
    initWorksheetData();
    setModel("C2", "true");
    setModel("C3", "false");
    assertEquals("true", getRawModel("C2"));
    assertEquals("false", getRawModel("C3"));
  }

  @Test
  public void getRaw_singleReference() {
    initWorksheetData();
    setModel("C2", "=D3");
    setModel("C3", "=E3");
    assertEquals("=D3", getRawModel("C2"));
    assertEquals("=E3", getRawModel("C3"));
  }

  @Test
  public void getRaw_groupReference() {
    initWorksheetData();
    setModel("C2", "=D3:E3");
    setModel("C3", "=D4:E4");
    assertEquals("=D3:E3", getRawModel("C2"));
    assertEquals("=D4:E4", getRawModel("C3"));
  }

  @Test
  public void getRaw_cyclicReference() {
    initWorksheetData();
    setModel("C2", "=C3");
    setModel("C3", "=C2");
    assertEquals("=C3", getRawModel("C2"));
    assertEquals("=C2", getRawModel("C3"));
  }

  @Test
  public void getRaw_validFormula() {
    initWorksheetData();
    setModel("C2", "=(SUM 4 3)");
    setModel("C3", "=(PRODUCT 2 8 1)");
    setModel("C4", "=(< 3 6)");
    setModel("C5", "=(ENUM 3 true \"str\")");
    assertEquals("=(SUM 4 3)", getRawModel("C2"));
    assertEquals("=(PRODUCT 2 8 1)", getRawModel("C3"));
    assertEquals("=(< 3 6)", getRawModel("C4"));
    assertEquals("=(ENUM 3 true \"str\")", getRawModel("C5"));
  }

  @Test
  public void getRaw_invalidFormula() {
    initWorksheetData();
    setModel("C2", "=invalid");
    setModel("C3", "=MoReInVaLiD");
    assertEquals("=invalid", getRawModel("C2"));
    assertEquals("=MoReInVaLiD", getRawModel("C3"));
  }

  @Test
  public void getRaw_youCanPutANYTHINGInThisThing() {
    initWorksheetData();
    setModel("C2", "ewpgvjneo[vjn");
    setModel("C3", "=${ORN{O SUM aev[n");
    assertEquals("ewpgvjneo[vjn", getRawModel("C2"));
    assertEquals("=${ORN{O SUM aev[n", getRawModel("C3"));
  }

  /** Tests for {@link FormulaWorksheetModel#getMaxRows()}. */

  @Test
  public void getMaxRows_genericCall() {
    this.initWorksheetData();
    assertEquals(8, model.getMaxRows());
    model.set(100, 102, "0");
    assertEquals(102, model.getMaxRows());
  }

  @Test
  public void getMaxRows_countsBadSyntax() {
    this.initWorksheetData();
    assertEquals(8, model.getMaxRows());
    model.set(100, 102, "INVALID SYMBOL");
    assertEquals(102, model.getMaxRows());
  }

  @Test
  public void getMaxRows_noneRowsToSomeRowsToManyRowsToSomeRowsToNone() {
    model = new FormulaWorksheetModel.FormulaWorksheetBuilder().createWorksheet();
    assertEquals(0, model.getMaxRows());
    model.set(3, 3, "true");
    assertEquals(3, model.getMaxRows());
    model.set(3, 6, "10");
    assertEquals(6, model.getMaxRows());
    model.set(3, 3, null);
    assertEquals(6, model.getMaxRows());
    model.set(3001, 3001, "\"bees!!!\"");
    assertEquals(3001, model.getMaxRows());
    model.set(3001, 3002, "=(ENUMERATE C3)");
    assertEquals(3002, model.getMaxRows());
    model.set(3001, 3003, "=(ENUMERATE asdf)");
    assertEquals(3003, model.getMaxRows());
    model.set(3001, 3001, null);
    model.set(3001, 3002, null);
    model.set(3001, 3003, null);
    assertEquals(6, model.getMaxRows());
    model.set(3, 6, null);
    assertEquals(0, model.getMaxRows());
  }

  /** Tests for {@link FormulaWorksheetModel#getMaxColumns()}. */

  @Test
  public void getMaxColumns_genericCall() {
    this.initWorksheetData();
    assertEquals(5, model.getMaxColumns());
    model.set(100, 102, "0");
    assertEquals(100, model.getMaxColumns());
  }

  @Test
  public void getMaxColumns_countsBadSyntax() {
    this.initWorksheetData();
    assertEquals(5, model.getMaxColumns());
    model.set(100, 102, "INVALID SYMBOL");
    assertEquals(100, model.getMaxColumns());
  }

  @Test
  public void getMaxColumns_noneColumnsToSomeColumnsToManyColumnsToSomeColumnsToNone() {
    model = new FormulaWorksheetModel.FormulaWorksheetBuilder().createWorksheet();
    assertEquals(0, model.getMaxColumns());
    model.set(3, 3, "true");
    assertEquals(3, model.getMaxColumns());
    model.set(6, 3, "10");
    assertEquals(6, model.getMaxColumns());
    model.set(3, 3, null);
    assertEquals(6, model.getMaxColumns());
    model.set(3001, 3001, "\"bees!!!\"");
    assertEquals(3001, model.getMaxColumns());
    model.set(3002, 3001, "=(ENUMERATE C3)");
    assertEquals(3002, model.getMaxColumns());
    model.set(3003, 3001, "=(ENUMERATE asdf)");
    assertEquals(3003, model.getMaxColumns());
    model.set(3001, 3001, null);
    model.set(3002, 3001, null);
    model.set(3003, 3001, null);
    assertEquals(6, model.getMaxColumns());
    model.set(6, 3, null);
    assertEquals(0, model.getMaxColumns());
  }

  /** Tests for {@link FormulaWorksheetModel#getActiveCells()}. */

  @Test
  public void getActiveCellsEmpty() {
    this.model = new FormulaWorksheetModel.FormulaWorksheetBuilder().createWorksheet();
    assertEquals(new HashSet<Coord>(), model.getActiveCells());
  }

  @Test
  public void getActiveCells() {
    initWorksheetData();
    HashSet<Coord> set = new HashSet<Coord>();
    set.add(new Coord(4, 2));
    set.add(new Coord(4, 3));
    set.add(new Coord(5, 3));
    set.add(new Coord(4, 4));
    set.add(new Coord(5, 4));
    set.add(new Coord(4, 5));
    set.add(new Coord(5, 5));
    set.add(new Coord(4, 6));
    set.add(new Coord(5, 6));
    set.add(new Coord(4, 7));
    set.add(new Coord(5, 7));
    set.add(new Coord(4, 8));
    set.add(new Coord(5, 8));
    assertEquals(set, model.getActiveCells());
  }
}
