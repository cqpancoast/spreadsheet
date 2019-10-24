package edu.cs3500.spreadsheets.sexp;

import java.util.List;

/**
 * The sole field of this class is an {@link Sexp} that is the parsed name of the Coord whose
 * formula is being checked for cycles.
 *
 * This {@link SexpVisitor} determines whether the {@link Sexp} of the Coord in question is found
 * in its formula's own {@link Sexp}, either directly or indirectly, which would create a cycle.
 *
 * The {@link SexpVisitor} returns true if there are cycles, and false if there are not cycles.
 */
public class SexpCheckCycles implements SexpVisitor<Boolean> {
  private final Sexp sexp;

  /**
   * Constructs an {@link SexpVisitor} that checks for cycles in the {@link Sexp} of the given cell.
   *
   * @param sexp   the name of the cell in question as an {@link Sexp}
   */
  public SexpCheckCycles(Sexp sexp) {
    this.sexp = sexp;
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
  public Boolean visitSList(List<Sexp> l) {
    for (Sexp each : l) {
      if (each.accept(new SexpCheckCycles(sexp))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Boolean visitSymbol(String s) {
    if (Parser.parse(s).equals(sexp)) {
      return true;
    }
    return Parser.parse(s).accept(new SexpCheckCycles(sexp));
  }

  @Override
  public Boolean visitString(String s) {
    return false;
  }
}
