package edu.cs3500.spreadsheets.sexp;

import java.util.List;

/**
 * TODO explain why this is here, what it does, and what all of its individual functions do.
 */
public class SExpEvaluatorFormulaWorksheet implements SexpVisitor<String> {

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