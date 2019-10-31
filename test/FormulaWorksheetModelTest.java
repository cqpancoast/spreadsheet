import static junit.framework.TestCase.assertEquals;

import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.sexp.SExpEvaluatorFormulaWorksheet;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;

/**
 * Tests for {@link FormulaWorksheetModel}.
 */
public class FormulaWorksheetModelTest {

  private WorksheetModel<String> model;
  private static final String errorInvalidBlankCellRef = "!#ERROR_INVALIDBLANKCELLREF"; //HELP BLERNER copy-paste?
  private static final String errorInvalidBlockCellRef = "!#ERROR_INVALIDBLOCKCELLREF";
  private static final String errorInvalidSymbol = "!#ERROR_INVALIDSYMBOL";
  private static final String errorInvalidCommand = "!#ERROR_INVALIDCOMMAND";
  private static final String errorRefIsError = "!#ERROR_REFISERROR";
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
   * functions and data in column B, etc. //TODO provide link to image on imgur or something?
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
  private void setModel(String cellString, String val) { //NOTE return value to be set?
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

  //TODO write some methods that initialize formulas into the model worksheet we're working with

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

  /** Tests for {@link FormulaWorksheetModel#set(int, int, String)}. */

  @Test
  public void set_genericCall() {

  }

  @Test(expected = IllegalArgumentException.class) // from Coord constructor
  public void set_negativeIndices() {

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

  }

  @Test
  public void set_canMakeInvalidWorksheets() {

  }
  // All other tests for set are implicit in all of the below tests.

  /** Tests for {@link FormulaWorksheetModel#getEval(int, int)}.
   *
   * This testing section is split into subsections to account for the many different types of
   * evaluation procedures that can go on. Effectively, this acts as the testing suite for
   * {@link SExpEvaluatorFormulaWorksheet}. */

  //** VALUES **//

  //* VALUES: Things go right *//
  @Test
  public void getEval_values_redundantDoubleReducesToInt() {

  }

  @Test
  public void getEval_values_basicallyInt() {

  }

  @Test
  public void getEval_values_double() {

  }

  @Test
  public void getEval_values_boolean() {

  }

  @Test
  public void getEval_values_stringGeneric() {

  }

  @Test
  public void getEval_values_stringBoolean() {

  }

  @Test
  public void getEval_values_stringDouble() {

  }

  @Test
  public void getEval_values_doubleString() {

  }

  @Test
  public void getEval_values_emptyString() {

  }

  //* VALUES: Invalid syntax (basically just symbols) *//
  @Test
  public void getEval_values_randomSymbol() {

  }

  @Test
  public void getEval_values_emptySymbol() {

  }
  // Evaluation when symbols are references are covered below.
  
  //** FORMULAE **//

  //* FORMULAE: Just values *//
  @Test
  public void getEval_formulae_redundantDoubleReducesToInt() {

  }

  @Test
  public void getEval_formulae_basicallyInt() {

  }

  @Test
  public void getEval_formulae_double() {

  }

  @Test
  public void getEval_formulae_boolean() {

  }

  @Test
  public void getEval_formulae_string() {

  }

  @Test
  public void getEval_formulae_randomSymbol() {

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

  }

  @Test
  public void getEval_formulae_differentError() {

  }

  @Test
  public void getEval_formulae_attemptedEvaluationOfReferenceBlock() {

  }

  @Test
  public void getEval_formulae_referenceCannotBeInParens() {

  }

  @Test
  public void getEval_formulae_justReferenceMustBeginWithEqualsSign() {

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
//    assertEquals("5.0", getEvalModel("C2"));
//    assertEquals("6.5", getEvalModel("C3"));
    assertEquals("10.5", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_SUM_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(SUM D3:E4 4.2)");
    setModel("C3", "=(SUM D3:E4 D3)");
    setModel("C4", "=(SUM (SUM D3 D4) (SUM E3 E4))");
    setModel("C5", "=C6");
    setModel("C6", "=(SUM D3 E4)");
    assertEquals("14.7", getEvalModel("C2"));
    assertEquals("13.5", getEvalModel("C3"));
    //assertEquals("10.5", getEvalModel("C4"));
    assertEquals("7.5", getEvalModel("C5"));
    assertEquals("7.5", getEvalModel("C6"));
  }

  @Test
  public void getEval_formulae_SUM_blankOrNonNumCells() {
    initWorksheetData();
    setModel("C2", "=(SUM C1 D3)");
    setModel("C3", "=(SUM B1 C1)");
    setModel("C4", "=(SUM D3 E3 C1)");
    assertEquals("3.0", getEvalModel("C2"));
    assertEquals("0.0", getEvalModel("C3"));
    assertEquals("5.0", getEvalModel("C4"));
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
    assertEquals("6.0", getEvalModel("C2"));
    assertEquals("9.0", getEvalModel("C3"));
    assertEquals("27.0", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_PRODUCT_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT D3:E4 4.2)");
    setModel("C3", "=(PRODUCT D3:E4 D3)");
    setModel("C4", "=(PRODUCT (SUM D3:E4) 13.2 (PRODUCT E3:E4))");
    setModel("C5", "=C6");
    setModel("C6", "=(PRODUCT D3 E4)");
    assertEquals("113.4", getEvalModel("C2"));
    assertEquals("81.0", getEvalModel("C3"));
    assertEquals("3207.6", getEvalModel("C4"));
    assertEquals("13.5", getEvalModel("C5"));
    assertEquals("13.5", getEvalModel("C6"));
  }

  @Test
  public void getEval_formulae_PRODUCT_blankCells() {
    initWorksheetData();
    setModel("C2", "=(PRODUCT C1 D3)");
    setModel("C3", "=(PRODUCT B1 C1)");
    setModel("C4", "=(PRODUCT D3 E3 C1)");
    assertEquals("3.0", getEvalModel("C2"));
    assertEquals("0.0", getEvalModel("C3"));
    assertEquals("6.0", getEvalModel("C4"));
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
    assertEquals("true", getEvalModel("C2"));
    assertEquals("false", getEvalModel("C3"));
    assertEquals("false", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_ENUM_justNonReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM 5 false \"bees\")");
    setModel("C3", "=(ENUM true 1.1 \"string\")");
    setModel("C4", "=(ENUM \"yeehaw\" 4 false)");
    assertEquals("5.0 false \"bees\" ", getEvalModel("C2"));
    assertEquals("true 1.1 \"string\" " , getEvalModel("C3"));
    assertEquals("\"yeehaw\" 4.0 false ", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_ENUM_singleReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM 5 E5 \"bees\")");
    setModel("C3", "=(ENUM true E4 \"string\")");
    setModel("C4", "=(ENUM E6 4 false)");
    assertEquals("5.0 false \"bees\" ", getEvalModel("C2"));
    assertEquals("true 4.5 \"string\" " , getEvalModel("C3"));
    assertEquals("\"friend\" 4.0 false ", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_ENUM_blockReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM D3:D7)");
    setModel("C3", "=(ENUM D3:D4 E6:E7)");
    setModel("C4", "=(ENUM D5:E7)");
    assertEquals("3.0 1.0 true \"bees\" \"true\" ", getEvalModel("C2"));
    assertEquals("3.0 1.0  \"friend\" \"7.0\" " , getEvalModel("C3"));
    assertEquals("true false \"bees\" \"friend\" \"true\" \"7.0\" ", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_ENUM_mixOfReferents() {
    initWorksheetData();
    setModel("C2", "=(ENUM D3 D7)");
    setModel("C3", "=(ENUM D3 D4  E6 E7)");
    setModel("C4", "=(ENUM D5 E7)");
    assertEquals("3.0 \"true\" ", getEvalModel("C2"));
    assertEquals("3.0 1.0 \"friend\" \"7.0\" " , getEvalModel("C3"));
    assertEquals("true \"7.0\" ", getEvalModel("C4"));
  }

  @Test
  public void getEval_formulae_ENUM_blankCells() {
    initWorksheetData();
    setModel("C2", "=(ENUM A2 D7)");
    setModel("C3", "=(ENUM D3 A4  E6 E7)");
    setModel("C4", "=(ENUM A2 A7)");
    assertEquals(" \"true\" ", getEvalModel("C2"));
    assertEquals("3.0  \"friend\" \"7.0\" " , getEvalModel("C3"));
    assertEquals("  ", getEvalModel("C4"));
  }

  // Functions: Invalid syntax //
  @Test
  public void getEval_formulae_unrecognizedCommand() {
    this.initWorksheetData();
    setModel("C2", "=(BEES 4 \"seventy\")");
    assertEquals(errorInvalidCommand, getEvalModel("C2"));
  }

  @Test
  public void getEval_formulae_bogusCommandLikeThing() {

  }

  @Test
  public void getEval_formulae_bogusCommandLikeThing2() {

  }

  @Test
  public void getEval_formulae_SUM_incorrect() {

  }

  // Functions: Invalid arguments //
  @Test
  public void getEval_formulae_SUM_errorProp() {

  }

  @Test
  public void getEval_formulae_SUM_incorrectNumArgs() {

  }

  @Test
  public void getEval_formulae_SUM_incorrectArgType() {

  }

  @Test
  public void getEval_formulae_PRODUCT_errorProp() {

  }

  @Test
  public void getEval_formulae_PRODUCT_incorrectNumArgs() {

  }

  @Test
  public void getEval_formulae_PRODUCT_incorrectArgType() {

  }

  @Test
  public void getEval_formulae_LESSTHAN_errorProp() {

  }

  @Test
  public void getEval_formulae_LESSTHAN_incorrectNumArgs() {

  }

  @Test
  public void getEval_formulae_LESSTHAN_incorrectArgType() {

  }

  @Test
  public void getEval_formulae_LESSTHAN_blockReferents() {

  }

  @Test
  public void getEval_formulae_LESSTHAN_blankCells() {

  }

  @Test
  public void getEval_formulae_ENUM_noArgs() {

  }

  @Test
  public void getEval_formulae_ENUM_errorProp() {

  }

  /** Tests for {@link FormulaWorksheetModel#getRaw(int, int)}. */
  
  @Test
  public void getRaw_doubleWithUnnecessaryDecimals() {

  }

  @Test
  public void getRaw_doubleWithNecessaryDecimals() {

  }

  @Test
  public void getRaw_doubleNoDecimals() {

  }

  @Test
  public void getRaw_string() {

  }

  @Test
  public void getRaw_boolean() {

  }

  @Test
  public void getRaw_singleReference() {

  }

  @Test
  public void getRaw_groupReference() {

  }

  @Test
  public void getRaw_cyclicReference() {

  }

  @Test
  public void getRaw_validFormula() {

  }

  @Test
  public void getRaw_invalidFormula() {

  }

  @Test
  public void getRaw_youCanPutANYTHINGInThisThing() {

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

}
