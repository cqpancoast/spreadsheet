package edu.cs3500.spreadsheets.model;

import edu.cs3500.spreadsheets.sexp.Parser;
import edu.cs3500.spreadsheets.sexp.Sexp;
import edu.cs3500.spreadsheets.sexp.SexpCheckCycles;
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

  private final HashMap<Coord, String> worksheet;

  /**
   * Constructs a {@link FormulaWorksheetModel}.
   *
   * @param worksheet  the empty HashMap that will contain the Coord-to-value mappings of the
   *                   worksheet
   */
  public FormulaWorksheetModel(HashMap<Coord, String> worksheet) {
    this.worksheet = new HashMap<Coord, String>();
  }

  @Override
  public void set(Coord c, String val) {
    worksheet.put(c, val);
  }

  @Override
  public String getEval(Coord c) {
    String raw = getRaw(c);
    if (raw == null) {
      return "";
    }
    if (!raw.contains("=")) {
      return raw;
    }
    return Parser.parse(raw.substring(1)).accept(new SExpEvaluator());
  }

  @Override
  public String getRaw(Coord c) {
    String raw = worksheet.get(c);
    if (raw == null) {
      return "";
    }
    return raw;
  }

  @Override
  public int getMaxRows() {
    int max = 0;
    for (Coord c : worksheet.keySet()) {
      max = Math.max(c.row, max);
    }
    return max;
  }

  @Override
  public int getMaxColumns() {
    int max = 0;
    for (Coord c : worksheet.keySet()) {
      max = Math.max(c.col, max);
    }
    return max;
  }

  @Override
  public boolean isValid() {
    for (Coord c : worksheet.keySet()) {
      String raw = getRaw(c);
      try {
        if (!raw.contains("=")) {
          Parser.parse(raw);
        }
        if (raw.contains("=")) {
          Parser.parse(raw.substring(1));
        }
      }
      catch (IllegalArgumentException e) {
        return false;
      }

    }
    return !hasCycles();
  }

  /**
   * Returns whether or not any s-expression in the worksheet contains cycles.
   * @return whether the worksheet contains cycles
   */
  private boolean hasCycles() {
    for (Coord c : worksheet.keySet()) {
      String raw = getRaw(c);
      if (raw.contains("=")) {
        Sexp sexp = Parser.parse(raw.substring(1));
        if (sexp.accept(new SexpCheckCycles(Parser.parse(c.toString())))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * TODO explain why this is here, what it does, and what all of its individual functions do.
   */
  private static class SExpEvaluator implements SexpVisitor<String> {

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
