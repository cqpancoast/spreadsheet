package edu.cs3500.spreadsheets.model;

import edu.cs3500.spreadsheets.sexp.Sexp;
import edu.cs3500.spreadsheets.sexp.SexpVisitor;
import java.util.ArrayList;
import java.util.List;

/**
 * This {@link SexpVisitor} determines whether the {@link Sexp} of the Coord in question is found
 * in its formula's own {@link Sexp}, either directly or through recursion, which would create a
 * cycle. The {@link SexpVisitor} returns true if there are cycles, and false if there are not
 * cycles. In the case in which a cell evaluates to an error, this will return false EVEN IF there
 * are cyclic cell references within the formula, because the evaluation procedure cannot actually
 * take place.
 */
public class SexpCheckCycles extends SexpEvaluator<Boolean> {
  private final FormulaWorksheetModel model;
  private List<String> cellsSoFar;

  /**
   * Constructs an {@link SexpVisitor} that checks for cycles in the {@link Sexp} of the given cell.
   * @param model the {@link FormulaWorksheetModel} in question
   */
  public SexpCheckCycles(FormulaWorksheetModel model) {
    this.model = model;
    this.cellsSoFar = new ArrayList<>();
  }

  @Override
  protected Boolean blankCellEvaluant() {
    return false;
  }

  @Override
  protected Boolean errorEvaluant() {
    return false;
  }

  @Override
  public Boolean visitBoolean(boolean b) {
    return false;
  }

  @Override
  public Boolean visitNumber(double d) {
    return false;
  }

  @Override
  public Boolean visitString(String s) {
    return false;
  }

  @Override
  public Boolean visitSList(List<Sexp> l) {
    for (Sexp each : l) {
      if (each.accept(this)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Boolean visitSymbol(String s) {
    if (cellsSoFar.contains(s)) {
      return true;
    }
    cellsSoFar.add(s);
    List<Integer> cellCoord = Coord.fromString(s);
    String refRawString = this.model.getRaw(cellCoord.get(0), cellCoord.get(1));
    return this.evaluate(refRawString);
  }

}
