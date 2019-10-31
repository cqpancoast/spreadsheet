package edu.cs3500.spreadsheets.sexp;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;

/**
 * A visitor to evaluate string-represented {@link Sexp} values in a {@link FormulaWorksheetModel}.
 * @param <T> return type of the evaluator
 */
public abstract class SexpEvaluator<T> implements SexpVisitor<T> {

  static final String errorInvalidBlankCellRef = "!#ERROR_INVALIDBLANKCELLREF";
  static final String errorInvalidBlockCellRef = "!#ERROR_INVALIDBLOCKCELLREF";
  static final String errorInvalidSymbol = "!#ERROR_INVALIDSYMBOL";
  static final String errorInvalidCommand = "!#ERROR_INVALIDCOMMAND";
  static final String errorCyclicRef = "!#ERROR_CYCLICREF";
  static final String errorArgIsError = "!#ERROR_ARGISERROR";
  static final String errorArgType = "!#ERROR_ARGTYPE";
  static final String errorInvalidArity = "!#ERROR_ARITY";
  static final String errorSyntax = "!#ERROR_SYNTAX";

  /**
   * Evaluates sexp according to the purpose of this evaluator.
   * @param sexp a sexp
   * @return evaluated s
   */
  T evaluate(Sexp sexp) {
    return sexp.accept(this);
  }

  /**
   * Evaluates s according to the purpose of the evaluator, pre-processing s into a form where it
   * can be evaluated and handling error cells according to its needs.
   * @param s the raw contents of a cell
   * @return the evaluation of s
   */
  public T evaluate(String s) {
    if (isBlankCell(s)) {
      return this.blankCellEvaluant();
    }
    String processedRaw = processRawValue(s);
    if (isError(processedRaw)) {
      return this.errorEvaluant();
    }
    try {
      Sexp parsedSexp = Parser.parse(processedRaw);
      return this.evaluate(parsedSexp);
    } catch (IllegalArgumentException e) {
      return this.errorEvaluant();
    }
  }

  /**
   * Determines whether the given string representation of a S-exp is a blank cell.
   * @param s a string rep of a S-exp
   * @return whether string rep of S-exp is of a blank cell
   */
  private static boolean isBlankCell(String s) {
    return s == null;
  }

  /**
   * Gets what the value of a blank cell should be, according to the rules of evaluation.
   * @return value of a blank cell
   */
  protected abstract T blankCellEvaluant();

  /**
   * Returns what the value of a cell should be that has an error, according to the rules of
   * this evaluation.
   * @return the cell value
   */
  protected abstract T errorEvaluant();

  /**
   * Ensures that the raw value has the correct syntax with respect to equals signs and formulae.
   * Returns syntax error symbol if raw begins with an open paren, removes equals sign if raw begins
   * with one. THIS DOES NOT ENSURE THAT RAW IS PARSABLE, OR HAS THE CORRECT SYNTAX IN GENERAL.
   * @param raw raw contents of a cell
   * @return processed string value or error symbol
   */
  private static String processRawValue(String raw) {
    if (raw.indexOf("=") == 0) {
      return raw.substring(1);
    } else if (raw.indexOf("(") == 0) {
      return errorSyntax;
    } else {
      return raw;
    }
  }

  /**
   * Determines whether s is a block reference.
   * @param s the given string representation of an {@link SSymbol}
   * @return whether s is a block reference
   */
  static boolean isBlockReference(String s) {
    String[] refs = s.split(":");
    if (refs.length != 2) {
      return false;
    }
    for (String ref : refs) {
      if (!isReference(ref)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines whether s is a reference.
   * @param s the given string representation of an {@link SSymbol}
   * @return whether s is a reference
   */
  static boolean isReference(String s) {
    return Coord.validCellName(s);
  }

  /**
   * Determines whether the given evaluated S-exp is an error.
   * @param evalArg an evaluated S-exp
   * @return whether string rep of s-exp is an error
   */
  public static boolean isError(String evalArg) {
    String[] splitMaybeError = evalArg.split("_");
    return splitMaybeError[0].equals("!#ERROR");
  }
}
