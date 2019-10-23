package edu.cs3500.spreadsheets.model;

import edu.cs3500.spreadsheets.sexp.Sexp;
import edu.cs3500.spreadsheets.sexp.SexpVisitor;
import java.util.HashMap;
import java.util.List;

/** //TODO make all html and pretty
 * Represents a worksheet in which the value type of non-blank cells is a formula or a value, both
 * of which are represented as strings. Blank cells are not represented, but IF USED IN A FORMULA
 * will evaluate to the string "0".
 * - A value can be a boolean, a double, or a string. To give examples, booleans are represented as
 *   "true" and "false", a double is represented as a truncated decimal number (i.e. "3", not
 *   "3.0"), and a string is represented as "\"bees\"".
 * - A formula begins with a leading equals sign. What follows can be either a function or a
 *   reference to another cell.
 *   - A reference is a symbolic representation of the coordinate(s) of another cell or collection
 *     of cells, according to the naming convention of cells in {@link Coord}. If a single cell, the
 *     reference will evaluate to the evaluated contents of the referenced cell. Attempts to
 *     directly evaluate a collection of cells is invalid syntax, but they can be included in
 *     functions, as is explained below. References within a given cell cannot reference that cell,
 *     even recursively (such as, A1 ref's B1, B1 ref's A1.). Examples: A2, C4:E3, F5:A5.
 *   - A function is a string representation of an s-expression. It can also contain other
 *     functions. Some examples: "=3", "=\"bees\"" "=(SUM 3 5)", "=(PRODUCT A2:B5 5)". Valid
 *     functions are contained in the static class {@link SExpEvaluator}.
 * Cells with contents deviating from the above prescription are invalid. Cells referencing invalid
 * cells are also invalid.
 */
public class FormulaWorksheetModel implements WorksheetModel<String> {

  HashMap<Coord, String> worksheet;

  //TODO figure out constructor — how is this actually made? Also in this talk about our field

  @Override
  public void set(Coord c, String val) {

  }

  @Override
  public String getEval(Coord c) {
    return null;
  }

  @Override
  public String getRaw(Coord c) {
    return null;
  }

  @Override
  public int getMaxRows() {
    return 0;
  }

  @Override
  public int getMaxColumns() {
    return 0;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  /**
   * TODO explain why this is here, what it does, and what all of its individual functions do.
   */
  static class SExpEvaluator implements SexpVisitor<String> {

    @Override
    public String visitBoolean(boolean b) {
      return Boolean.toString(b);
    }

    @Override
    public String visitNumber(double d) {
      return Double.toString(d);
    }

    @Override
    public String visitSList(List<Sexp> l) {
      return null;
    }

    @Override
    public String visitSymbol(String s) {
      return s; //TODO evaluate cell references
    }

    @Override
    public String visitString(String s) {
      return "\"" + s + "\"";
    }
  }

}