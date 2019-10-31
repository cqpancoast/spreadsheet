package edu.cs3500.spreadsheets.model;

import edu.cs3500.spreadsheets.model.WorksheetReader.WorksheetBuilder;
import edu.cs3500.spreadsheets.sexp.SExpEvaluatorFormulaWorksheet;
import edu.cs3500.spreadsheets.sexp.Sexp;
import edu.cs3500.spreadsheets.sexp.SexpEvaluator;
import java.util.HashMap;

/**
 * Represents a worksheet in which the raw value type of non-blank cells is a string literal
 * representation of an {@link Sexp}ression. Each of the SExps in question can be a formula or a
 * value, both of which are represented as strings. There is a process of evaluation for cells in
 * which their raw SExp contents are evaluated to strings, which is discussed for both values and
 * formulae below. (Blank cells are not represented, and will normally evaluate to the empty string.
 * If used in a formula, the blank cell may evaluate to something different depending on the
 * function.) This evaluation procedure is implemented in {@link SExpEvaluatorFormulaWorksheet}.
 * - A value can be a boolean, a double, or a string. To give examples, booleans are represented as
 *   "true" and "false", a double is represented as a truncated decimal number (i.e. "3", not
 *   "3.0"), and a string is represented as "\"bees\"". Values evaluate to themselves.
 * - A formula begins with a leading equals sign. What follows can be either a function or a
 *   reference to another cell.
 *   - A reference is a symbolic representation of the coordinate(s) of another cell or collection
 *     of cells, according to the naming convention of cells in {@link Coord}. If a single cell, the
 *     reference will evaluate to the evaluated contents of the referenced cell. Attempts to
 *     directly evaluate a collection of cells is invalid syntax, but they can be included in
 *     functions, as is explained below. References within a given cell cannot reference that cell,
 *     even recursively (such as, A1 ref's B1, B1 ref's A1.). Examples: A2, C4:E3, F5:A5. Reference
 *     indexing begins from A and 1.
 *   - A function is a string representation of an equals sign and then any SExp. (For example, "=3"
 *     and "=\"bees\"", which will evaluate to "3" and "\"bees\"".) The more interesting case comes
 *     for SLists. A valid SList for a function consists of a function name and then the correct
 *     number of operands for that function, of the correct types. Valid functions are implementing
 *     classes of {@link SExpEvaluatorFormulaWorksheet} that are enclosed within it.
 * Cells with contents deviating from the above prescription are invalid. Cells referencing invalid
 * cells are also invalid.
 */
public class FormulaWorksheetModel implements WorksheetModel<String> {

  private final HashMap<Coord, String> worksheet;
  private final SexpEvaluator<String> evaluator = new SExpEvaluatorFormulaWorksheet(this);

  /**
   * Constructs a {@link FormulaWorksheetModel}.
   * @param worksheet  the empty HashMap that will contain the Coord-to-value mappings of the
   *                   worksheet
   */
  public FormulaWorksheetModel(HashMap<Coord, String> worksheet) {
    if (worksheet == null) {
      throw new IllegalArgumentException("Received null worksheet as constructor argument.");
    }
    this.worksheet = worksheet;
  }

  /**
   * If val is null, removes the entry at val.
   */
  @Override
  public void set(int col, int row, String val) {
    if (val == null) {
      this.worksheet.remove(new Coord(col, row));
    } else {
      this.worksheet.put(new Coord(col, row), val);
    }
  }

  @Override
  public String getEval(int col, int row) {
    return this.evaluator.evaluate(getRaw(col, row));
  }

  @Override
  public String getRaw(int col, int row) {
    return this.worksheet.get(new Coord(col, row));
  }

  @Override
  public int getMaxRows() {
    int max = 0;
    for (Coord c : this.worksheet.keySet()) { //HELP BLERNER one-line this? Coll.max(wksht.keySet.rows)?
      max = Math.max(c.row, max);
    }
    return max;
  }

  @Override
  public int getMaxColumns() {
    int max = 0;
    for (Coord c : this.worksheet.keySet()) {
      max = Math.max(c.col, max);
    }
    return max;
  }

  /**
   * A builder pattern for producing {@link FormulaWorksheetModel}s.
   */
  public static class FormulaWorksheetBuilder implements WorksheetBuilder<FormulaWorksheetModel> {
    private final HashMap<Coord, String> worksheet;

    public FormulaWorksheetBuilder() {
      this.worksheet = new HashMap<Coord, String>();
    }

    @Override
    public WorksheetBuilder<FormulaWorksheetModel> createCell(int col, int row, String contents) {
      this.worksheet.put(new Coord(col, row), contents);
      return this;
    }

    @Override
    public FormulaWorksheetModel createWorksheet() {
      return new FormulaWorksheetModel(this.worksheet);
    }
  }
}