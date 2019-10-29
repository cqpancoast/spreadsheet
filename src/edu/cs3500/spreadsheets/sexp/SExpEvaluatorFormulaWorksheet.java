package edu.cs3500.spreadsheets.sexp;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A visitor to evaluate string-represented {@link Sexp} values in a worksheet according to the
 * rules of {@link FormulaWorksheetModel}. If the evaluation results in an error, the returned
 * string representation of an {@link SSymbol} has the form !#ERROR_[ERRORDESCRIPTION].
 */
public class SExpEvaluatorFormulaWorksheet implements SexpVisitor<String> {

  private final FormulaWorksheetModel model;
  protected final String errorInvalidBlankCellRef = "!#ERROR_INVALIDBLANKCELLREF";
  protected final String errorInvalidBlockCellRef = "!#ERROR_INVALIDBLOCKCELLREF";
  protected final String errorInvalidSymbol = "!#ERROR_INVALIDSYMBOL";
  protected final String errorInvalidCommand = "!#ERROR_INVALIDCOMMAND";
  protected final String errorRefIsError = "!#ERROR_REFISERROR";
  protected final String errorArgIsError = "!#ERROR_ARGISERROR";
  protected final String errorArgType = "!#ERROR_ARGTYPE";
  protected final String errorInvalidArity = "!#ERROR_ARITY";
  protected final String errorSyntax = "!#ERROR_SYNTAX";

  public SExpEvaluatorFormulaWorksheet(FormulaWorksheetModel model) {
    this.model = model;
  }

  /**
   * Evaluates s according to the rules of {@link FormulaWorksheetModel}.
   * @param s a sexp
   * @return evaluated s
   */
  public String evaluate(Sexp s) { //NOTE do we really need two?
    return s.accept(this);
  }

  public String evaluate(String s) {
    if (isBlankCell(s)) {
      return this.getBlankCellValue();
    } else {
      return this.evaluate(Parser.parse(s));
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
  protected String getBlankCellValue() {
    return "";
  }

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
    if (l.size() < 1) {
      return errorSyntax;
    }
    Sexp command = l.remove(0);
    switch (command.accept(this)) {
      case "SUM":
        return new SList(l).accept(new SexpEvaluatorSum(this.model));
      case "PRODUCT":
        return new SList(l).accept(new SexpEvaluatorProduct(this.model));
      case "<":
        return new SList(l).accept(new SexpEvaluatorLessThan(this.model));
      case "ENUM":
        return new SList(l).accept(new SexpEvaluatorEnum(this.model));
      default:
        return errorInvalidCommand;
    }
  }

  @Override
  public String visitSymbol(String s) {
    if (isBlockReference(s)) {
      return this.visitBlockReference(s); //HELP BLERNER is this correct syntax for visitor pattern?
    } else if (isReference(s)) {
      return this.visitReference(s);
    } else if (isError(s)) {
      return errorRefIsError;
    } else {
      return errorInvalidSymbol;
    }
  }

  @Override
  public String visitString(String s) {
    return "\"" + s + "\"";
  }

  /**
   * Evaluates blockRef according to the rules of {@link FormulaWorksheetModel}.
   * @param blockRef a string representation of a {@link SSymbol} block reference
   * @return the evaluation of blockRef
   */
  protected String visitBlockReference(String blockRef) {
    return errorInvalidBlockCellRef;
  }

  /**
   * Evaluates ref according to the rules of {@link FormulaWorksheetModel}. Returns an error symbol
   * if the reference structure has any cycles.
   * @param ref a string representation of a {@link SSymbol} reference
   * @return the evaluation of ref
   */
  protected String visitReference(String ref) {
    return this.visitReference(ref, new ArrayList<>());
  }

  protected String visitReference(String ref, List<String> pastRefs) { //TODO figure out cycles!!!
    List<Integer> fromString = Coord.fromString(ref);
    return this.model.getEval(fromString.get(0), fromString.get(1));
  }

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


  //* FUNCTION EVALUATORS *//

  /**
   * A functional operation that accumulates a value by iterating over the arguments. Returns error
   * symbol if any of the args are errors.
   */
  private abstract static class SexpEvaluatorAccumulator<T> extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorAccumulator(FormulaWorksheetModel model) {
      super(model);
    }

    /**
     * Provides an initial value for the accumulator.
     * @return the initialized value of the accumulative variable
     */
    protected abstract T initializeValue();

    /**
     * Increments this accumulator's accumulative value by some function of evalArg.
     * @param accumulator the variable that is accumulating
     * @param evalArg an evaluated argument
     */
    protected abstract void accumulate(T accumulator, String evalArg);

    @Override
    public String visitSList(List<Sexp> args) {
      T accumulator = this.initializeValue();
      for (Sexp arg : args) {
        String evalArg = super.evaluate(arg);
        if (isError(evalArg)) {
          if (evalArg.equals(errorArgType)) {
            return evalArg;
          }
          return errorArgIsError;
        }
        try {
          this.accumulate(accumulator, evalArg);
        } catch (NumberFormatException e) {
          // Do nothing if arg not parsable as what it should be.
        }
      }
      return accumulator.toString();
    }

    @Override
    protected String visitBlockReference(String blockRef) {
      T blockAccumulator = this.initializeValue();
      BlockReferenceIterator blockRefs = new BlockReferenceIterator(blockRef);
      while (blockRefs.hasNext()) {
        String refEval = this.visitReference(blockRefs.next());
        if (isError(refEval)) {
          return errorRefIsError;
        }
        try {
          this.accumulate(blockAccumulator, refEval);
        } catch (NumberFormatException e) {
          // Do nothing if arg not parsable as what it should be.
        }
      }
      return blockAccumulator.toString();
    }
  }

  /**
   * Sums all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorSum extends SexpEvaluatorAccumulator<Double> {

    public SexpEvaluatorSum(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    protected Double initializeValue() {
      return 0.0;
    }

    @Override
    protected void accumulate(Double accumulator, String evalArg) {
      accumulator += Double.parseDouble(evalArg); //HELP never used? It's used in parent class
    }

    @Override
    public String visitBoolean(boolean b) {
      return errorArgType;
    }

    @Override
    public String visitString(String s) {
      return errorArgType;
    }

    @Override
    protected final String getBlankCellValue() {
      return "0";
    }

  }

  /**
   * Multiplies all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorProduct extends SexpEvaluatorAccumulator<Double> {

    public SexpEvaluatorProduct(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    protected Double initializeValue() {
      return 0.0;
    }

    @Override
    protected void accumulate(Double accumulator, String evalArg) {
      accumulator *= Double.parseDouble(evalArg); //HELP never used? It's used in parent class
    }

    @Override
    public String visitBoolean(boolean b) {
      return errorArgType;
    }

    @Override
    public String visitString(String s) {
      return errorArgType;
    }

    @Override
    protected final String getBlankCellValue() {
      return "0";
    }
  }

  /**
   * Determines whether the first argument is less than the second argument, if both of the
   * arguments can be interpreted as doubles. Returns error symbol if:
   * - either of the args are errors.
   * - either of the args are references are blank spaces.
   * - either size of args is anything other than two.
   * - either of the args are not interpretable as doubles.
   */
  private static final class SexpEvaluatorLessThan extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorLessThan(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    public String visitSList(List<Sexp> args) {
      if (args.size() != 2) {
        return errorInvalidArity;
      }
      String arg1Eval = super.evaluate(args.get(0));
      String arg2Eval = super.evaluate(args.get(1));
      if (isBlankCell(arg1Eval) || isBlankCell(arg2Eval)) {
        return errorInvalidBlankCellRef;
      }
      if (isError(arg1Eval) || isError(arg2Eval)) {
        return errorArgIsError;
      } try {
        return String.valueOf(Double.parseDouble(arg1Eval) < Double.parseDouble(arg2Eval));
      } catch (NumberFormatException e) {
        return errorArgType;
      }
    }

    @Override
    public String visitString(String s) {
      return errorArgType;
    }

    @Override
    public String visitNumber(double d) {
      return errorArgType;
    }

    @Override
    protected final String getBlankCellValue() {
      return errorInvalidBlankCellRef;
    }
  }

  /**
   * Lists all of the args separated by spaces. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorEnum extends SexpEvaluatorAccumulator<StringBuilder> {

    public SexpEvaluatorEnum(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    protected StringBuilder initializeValue() {
      return new StringBuilder();
    }

    @Override
    protected void accumulate(StringBuilder accumulator, String evalArg) {
      accumulator.append(evalArg).append(" ");
    }

    @Override
    protected final String getBlankCellValue() {
      return "<blank>";
    }
  }

  /**
   * Given a string specifying worksheet cells that are at the corners of some region, iterates over
   * the cells in that region.
   */
  private final static class BlockReferenceIterator implements Iterator<String> {

    private final int startCol, startRow, endCol, endRow; //HELP BLERNER it's small, but should I follow IntelliJ's advice here?
    private int col, row;

    /**
     * Creates a new {@link BlockReferenceIterator}.
     * @param blockRef a string representing a reference to a block of cells
     */
    private BlockReferenceIterator(String blockRef) { //HELP BLERNER What does private mean here?
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