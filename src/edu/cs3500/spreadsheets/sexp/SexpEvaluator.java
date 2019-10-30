package edu.cs3500.spreadsheets.sexp;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import java.util.List;

/**
 * A visitor to evaluate string-represented {@link Sexp} values in a {@link FormulaWorksheetModel}.
 * @param <T> return type of the evaluator
 */
public abstract class SexpEvaluator<T> implements SexpVisitor<T> {

  protected static final String errorInvalidBlankCellRef = "!#ERROR_INVALIDBLANKCELLREF"; //HELP BLERNER copy-paste?
  protected static final String errorInvalidBlockCellRef = "!#ERROR_INVALIDBLOCKCELLREF";
  protected static final String errorInvalidSymbol = "!#ERROR_INVALIDSYMBOL";
  protected static final String errorInvalidCommand = "!#ERROR_INVALIDCOMMAND";
  protected static final String errorRefIsError = "!#ERROR_REFISERROR";
  protected static final String errorCyclicRef = "!#ERROR_CYCLICREF";
  protected static final String errorArgIsError = "!#ERROR_ARGISERROR";
  protected static final String errorArgType = "!#ERROR_ARGTYPE";
  protected static final String errorInvalidArity = "!#ERROR_ARITY";
  protected static final String errorSyntax = "!#ERROR_SYNTAX";

  /**
   * Evaluates sexp according to the purpose of this evaluator.
   * @param sexp a sexp
   * @return evaluated s
   */
  public T evaluate(Sexp sexp) {
    //System.out.println(sexp);
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
      return this.errorEvaluant(errorSyntax);
    }
    try {
      Sexp parsedSexp = Parser.parse(processedRaw);
      return this.evaluate(parsedSexp);
    } catch (IllegalArgumentException e) {
      return this.errorEvaluant(errorSyntax);
    }
  }

  /**
   * Determines whether the given string representation of a S-exp is a blank cell.
   * @param s a string rep of a S-exp
   * @return whether string rep of S-exp is of a blank cell
   */
  protected static boolean isBlankCell(String s) {
    return s == null;
  }

  /**
   * Gets what the value of a blank cell should be, according to the rules of evaluation.
   * @return value of a blank cell
   */
  protected abstract T blankCellEvaluant();

  /**
   * Ensures that the raw value has the correct syntax with respect to equals signs and formulae.
   * Returns syntax error symbol if raw begins with an open paren, removes equals sign if raw begins
   * with one. THIS DOES NOT ENSURE THAT RAW IS PARSABLE, OR HAS THE CORRECT SYNTAX IN GENERAL.
   * @param raw raw contents of a cell
   * @return processed string value or error symbol
   */
  protected static String processRawValue(String raw) { //TODO require that references begin with equals signs
    if (raw.indexOf("=") == 0) {
      return raw.substring(1);
    } else if (raw.indexOf("(") == 0) {
      return errorSyntax;
    } else {
      return raw;
    }
  }

  /**
   * Returns what the value of a cell should be that has an error, according to the rules of
   * this evaluation.
   * @param errorSymbol the error symbol of the cell
   * @return the cell value
   */
  protected abstract T errorEvaluant(String errorSymbol);

  /**
   * Determines whether s is a block reference.
   * @param s the given string representation of an {@link SSymbol}
   * @return whether s is a block reference
   */
  protected static boolean isBlockReference(String s) {
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
  protected static boolean isReference(String s) {
    return Coord.validReferenceName(s);
  }

  /**
   * Determines whether the given evaluated S-exp is an error.
   * @param evalArg an evaluated S-exp
   * @return whether string rep of s-exp is an error
   */
  protected static boolean isError(String evalArg) {
    String[] splitMaybeError = evalArg.split("_");
    return splitMaybeError[0].equals("!#ERROR");
  }
}
