package edu.cs3500.spreadsheets.model;

import edu.cs3500.spreadsheets.sexp.Parser;
import edu.cs3500.spreadsheets.sexp.SSymbol;
import edu.cs3500.spreadsheets.sexp.Sexp;
import edu.cs3500.spreadsheets.sexp.SexpVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A visitor to evaluate string-represented {@link Sexp} values in a {@link FormulaWorksheetModel}.
 * @param <T> return type of the evaluator
 */
public abstract class SexpEvaluator<T> implements SexpVisitor<T> {

  static final String errorInvalidBlankCellRef = "!#ERROR_INVALIDBLANKCELLREF";
  static final String errorInvalidBlockCellRef = "!#ERROR_INVALIDBLOCKCELLREF";
  static final String errorInvalidColumnCellRef = "!#ERROR_INVALIDCOLUMNCELLREF";
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
  T evaluate(String s) {
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

  @Override
  public T visitSymbol(String s) {
    if (isReference(s)) {
      return this.visitReference(s);
    } else if (isBlockReference(s)) {
      return this.visitBlockReference(s);
    } else if (isColumnReference(s)) {
      return this.visitColumnReference(s);
    } else {
      return this.errorEvaluant();
    }
  }

  /**
   * Evaluates ref according to the rules of {@link FormulaWorksheetModel}. Returns an error symbol
   * if the reference structure has any cycles.
   * @param ref a string representation of a {@link SSymbol} reference
   * @return the evaluation of ref
   */
  protected abstract T visitReference(String ref);

  /**
   * Evaluates blockRef according to the rules of {@link FormulaWorksheetModel}.
   * @param blockRef a string representation of a {@link SSymbol} block reference
   * @return the evaluation of blockRef
   */
  protected abstract T visitBlockReference(String blockRef);

  /**
   * Evaluates colRef according to the rules of {@link FormulaWorksheetModel}.
   * @param colRef a string representation of a {@link SSymbol} column reference
   * @return the evaluation of colRef
   */
  protected abstract T visitColumnReference(String colRef);

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
    return Coord.validCellName(refs[0]) && Coord.validCellName(refs[1]);
  }

  /**
   * Determines whether s is a column reference.
   * @param s the given string representation of an {@link SSymbol}
   * @return whether s is a column reference
   */
  static boolean isColumnReference(String s) {
    String[] refs = s.split(":");
    if (refs.length != 2) {
      return false;
    }
    return Coord.validColumnName(refs[0]) && Coord.validColumnName(refs[1]);
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

  /**
   * Given a string specifying worksheet cells that are at the corners of some region, iterates over
   * the cells in that region.
   */
  protected static final class BlockReferenceIterator implements Iterator<String> {

    private final int startCol;
    private final int startRow;
    private final int endCol;
    private final int endRow;
    private int col;
    private int row;

    /**
     * Creates a new {@link BlockReferenceIterator}.
     * @param blockRef a string representing a reference to a block of cells
     */
    BlockReferenceIterator(String blockRef) {
      if (!isBlockReference(blockRef)) {
        throw new IllegalArgumentException("Received invalid block reference string.");
      }

      String[] refs = blockRef.split(":");
      List<Integer> firstRefCoord = Coord.fromString(refs[0]);
      List<Integer> secondRefCoord = Coord.fromString(refs[1]);
      List<Integer> cols
          = new ArrayList<>(Arrays.asList(firstRefCoord.get(0), secondRefCoord.get(0)));
      List<Integer> rows
          = new ArrayList<>(Arrays.asList(firstRefCoord.get(1), secondRefCoord.get(1)));
      Collections.sort(cols);
      Collections.sort(rows);

      this.startCol = cols.get(0);
      this.startRow = rows.get(0);
      this.endCol = cols.get(1);
      this.endRow = rows.get(1);
      this.col = this.startCol;
      this.row = this.startRow;
    }

    @Override
    public boolean hasNext() {
      return this.row <= endRow;
    }

    @Override
    public String next() {
      String nextReference = Coord.colIndexToName(this.col) + this.row;
      this.col += 1;
      if (this.col > this.endCol) {
        this.col = this.startCol;
        this.row += 1;
      }
      return nextReference;
    }
  }
}
