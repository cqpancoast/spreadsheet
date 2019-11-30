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
  protected Boolean visitReference(String ref) {
    if (cellsSoFar.contains(ref)) {
      return true;
    }
    cellsSoFar.add(ref);
    List<Integer> cellCoord = Coord.fromString(ref);
    String refRawString = this.model.getRaw(cellCoord.get(0), cellCoord.get(1));
    return this.evaluate(refRawString);
  }

  @Override
  protected Boolean visitBlockReference(String blockRef) {
    BlockReferenceIterator blockIter;
    blockIter = new BlockReferenceIterator(blockRef);
    while (blockIter.hasNext()) {
      if (this.visitReference(blockIter.next())) {
        return true;
      } else {
        this.removeAllCellsAfter(blockRef);
      }
    }
    return false;
  }

  @Override
  protected Boolean visitColumnReference(String colRef) {
    return this.visitBlockReference(this.colRefToBlockRef(colRef));
  }

  /**
   * Removes all cells in cellsSoFar after the last instance of the given reference if there is one.
   * @param ref  a cell reference
   */
  private void removeAllCellsAfter(String ref) {
    int lastIndexOfRef = this.cellsSoFar.lastIndexOf(ref);
    if (lastIndexOfRef != -1) {
      this.cellsSoFar.subList(0, lastIndexOfRef);
    }
  }

  /**
   * Given a column reference, converts it to a block reference depending on the maximum row of
   * entered data in the model.
   * @param colRef a reference to one or more columns.
   * @return the corresponding block reference
   */
  private String colRefToBlockRef(String colRef) {
    String[] splitString = colRef.split(":");
    if (splitString.length != 2) {
      throw new IllegalStateException("Invalid column reference string");
    }
    return splitString[0] + "1:" + splitString[1] + model.getMaxRows();
  }
}
