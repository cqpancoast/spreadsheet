package edu.cs3500.spreadsheets.sexp;

import java.util.List;

/**
 * TODO explain why this is here, what it does, and what all of its individual functions do.
 */
public class SExpEvaluatorFormulaWorksheet implements SexpVisitor<Sexp> {

  @Override
  public Sexp visitBoolean(boolean b) {
    return Parser.parse(Boolean.toString(b));
  }

  @Override
  public Sexp visitNumber(double d) {
    return Parser.parse(Double.toString(d));
  }

  @Override
  public Sexp visitSList(List<Sexp> l) {
    return null;
  }

  @Override
  public Sexp visitSymbol(String s) {
    return null;
  }

  @Override
  public Sexp visitString(String s) {
    return Parser.parse("\"" + s + "\"");
  }

  private double sum(Sexp... args) {
    return 0;
  }

  private double product(Sexp... args) {
    return 0;
  }

  private boolean lessThan(Sexp... args) {
    if (args.length != 2) { // or if either != SNumber
      // TODO whatever we want to do with errant function evaluations
    }
    return false;
  }
}