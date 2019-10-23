package edu.cs3500.spreadsheets.sexp;

import java.util.List;

public class SexpCheckCycles implements SexpVisitor<Boolean> {
  private final Sexp symbol;

  public SexpCheckCycles(Sexp symbol) {
    this.symbol = symbol;
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
    for (Sexp sexp : l) {
      if (sexp.accept(new SexpCheckCycles(symbol))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Boolean visitSymbol(String s) {
    return false; // TODO
  }

  @Override
  public Boolean visitString(String s) {
    return false;
  }
}
