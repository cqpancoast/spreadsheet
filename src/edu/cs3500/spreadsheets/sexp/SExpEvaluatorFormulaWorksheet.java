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
public class SExpEvaluatorFormulaWorksheet extends SexpEvaluator<String> {
  private final FormulaWorksheetModel model;

  /**
   * //TODO
   * @param model
   */
  public SExpEvaluatorFormulaWorksheet(FormulaWorksheetModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Evaluator constructor received null model.");
    }
    this.model = model;
  }

  @Override
  protected String blankCellEvaluant() {
    return "";
  }

  @Override
  protected String errorEvaluant(String errorSymbol) {
    return errorSymbol;
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
    Sexp command = l.get(0);
    List<Sexp> args = l.subList(1, l.size());
    switch (command.toString()) {
      case "SUM":
        return new SList(args).accept(new SexpEvaluatorSum(this.model));
      case "PRODUCT":
        return new SList(args).accept(new SexpEvaluatorProduct(this.model));
      case "<":
        return new SList(args).accept(new SexpEvaluatorLessThan(this.model));
      case "ENUM":
        return new SList(args).accept(new SexpEvaluatorEnum(this.model));
      default:
        return errorInvalidCommand;
    }
  }

  @Override
  public String visitSymbol(String s) {
    if (isBlockReference(s)) {
      String var = this.visitBlockReference(s); //NOTE println here
      System.out.println(var);
      return var;
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
    if (this.hasCycles(ref)) { //NOTE error
      return errorCyclicRef;
    }
    List<Integer> refCoord = Coord.fromString(ref);
    return this.model.getEval(refCoord.get(0), refCoord.get(1));
  }

  /**
   * Determines whether the reference represented by refString refers to itself recursively in the
   * worksheet in which the cell is instantiated.
   * @param refString a string representation of a cell reference
   * @return whether the cell refers to itself recursively
   */
  protected boolean hasCycles(String refString) {
    return new SexpCheckCycles(this.model).visitSymbol(refString);
  }


  //* FUNCTION EVALUATORS *//

  /**
   * A functional operation that accumulates a value by iterating over the arguments. Returns error
   * symbol if any of the args are errors.
   */
  private abstract static class SexpEvaluatorAccumulator<T> extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorAccumulator(FormulaWorksheetModel model) { //HELP BLERNER model == null?
      super(model);
    }

    /**
     * Provides an initial value for the accumulator.
     * @return the initialized value of the accumulative variable
     */
    protected abstract T initializeValue();

    /**
     * Increments this accumulator's accumulative value by some function of evalArg.
     * @param evalArg an evaluated argument
     */
    protected abstract T accumulate(String evalArg);

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
          accumulator = this.accumulate(evalArg);
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
          blockAccumulator = this.accumulate(refEval);
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

    Double acc;

    public SexpEvaluatorSum(FormulaWorksheetModel model) {
      super(model);
      this.acc = 0.0;
    }

    @Override
    protected Double initializeValue() {
      return 0.0;
    }

    @Override
    protected Double accumulate(String evalArg) {
      this.acc += Double.parseDouble(evalArg);
      return acc;
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
    protected final String blankCellEvaluant() {
      return "0";
    }

  }

  /**
   * Multiplies all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorProduct extends SexpEvaluatorAccumulator<Double> {

    Double acc;

    public SexpEvaluatorProduct(FormulaWorksheetModel model) {
      super(model);
      this.acc = 1.0;
    }

    @Override
    protected Double initializeValue() {
      return 0.0;
    }

    @Override
    protected Double accumulate(String evalArg) {
      this.acc *= Double.parseDouble(evalArg);
      return acc;
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
    protected final String blankCellEvaluant() {
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
    protected final String blankCellEvaluant() {
      return errorInvalidBlankCellRef;
    }
  }

  /**
   * Lists all of the args separated by spaces. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorEnum extends SexpEvaluatorAccumulator<StringBuilder> {

    StringBuilder acc;

    public SexpEvaluatorEnum(FormulaWorksheetModel model) {
      super(model);
      this.acc = new StringBuilder();
    }

    @Override
    protected StringBuilder initializeValue() {
      return new StringBuilder();
    }

    @Override
    protected StringBuilder accumulate(String evalArg) {
      this.acc.append(evalArg).append(" ");
      return acc;
    }

    @Override
    protected final String blankCellEvaluant() {
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
