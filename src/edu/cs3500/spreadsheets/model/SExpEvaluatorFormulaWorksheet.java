package edu.cs3500.spreadsheets.model;

import edu.cs3500.spreadsheets.sexp.SList;
import edu.cs3500.spreadsheets.sexp.SSymbol;
import edu.cs3500.spreadsheets.sexp.Sexp;
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
  protected final FormulaWorksheetModel model;

  /**
   * Constructs a {@link SExpEvaluatorFormulaWorksheet}.
   * @param model the model that this evaluates {@link Sexp}s from.
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
  protected String errorEvaluant() {
    return errorSyntax;
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
      return this.visitBlockReference(s);
    } else if (isReference(s)) {
      return this.visitReference(s);
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
  private String visitReference(String ref) {
    if (this.hasCycles(ref)) {
      return errorCyclicRef;
    }
    List<Integer> refCoord = Coord.fromString(ref);
    String rawCellVal = this.model.getRaw(refCoord.get(0), refCoord.get(1));
    try {
      if (rawCellVal.replace(" ", "").substring(0, 2).equals("=(")) {
        return new SExpEvaluatorFormulaWorksheet(this.model).evaluate(rawCellVal);
      } else {
        return this.evaluate(rawCellVal);
      }
    } catch (Exception e) {
      return this.evaluate(rawCellVal);
    }
  }

  /**
   * Determines whether the reference represented by refString refers to itself recursively in the
   * worksheet in which the cell is instantiated.
   * @param refString a string representation of a cell reference
   * @return whether the cell refers to itself recursively
   */
  private boolean hasCycles(String refString) {
    return new SexpCheckCycles(this.model).visitSymbol(refString);
  }

  /**
   * Returns new instance of this class with the same model field. The purpose of this is to ensure
   * that
   * @return new instance of this
   */
  protected SExpEvaluatorFormulaWorksheet newInstanceOfThis() {
    return new SExpEvaluatorFormulaWorksheet(this.model);
  }


  //* FUNCTION EVALUATORS *//

  /**
   * A functional operation that accumulates a value by iterating over the arguments. Returns error
   * symbol if any of the args are errors.
   */
  private abstract static class SexpEvaluatorAccumulator<T> extends SExpEvaluatorFormulaWorksheet {

    /**
     * Creates a {@link SexpEvaluatorAccumulator}.
     * @param model the model that this evaluates cell values from
     */
    SexpEvaluatorAccumulator(FormulaWorksheetModel model) {
      super(model);
    }

    /**
     * Provides an initial value for the accumulator.
     * @return the initialized value of the accumulative variable
     */
    protected abstract T initializeValue();

    /**
     * Increments this accumulator's accumulative value by some function of evalArg.
     * @param accumulator the value to be accumulated
     * @param evalArg an evaluated argument
     */
    protected abstract void accumulate(T accumulator, String evalArg);

    @Override
    public String visitSList(List<Sexp> args) {
      T accumulator = this.initializeValue();
      for (Sexp arg : args) {
        //If the sexp is a list, then it is a command and should be evaluated by a new evaluator
        String evalArg;
        if (arg.toString().indexOf("(") == 0) {
          evalArg = new SExpEvaluatorFormulaWorksheet(super.model).evaluate(arg);
        } else {
          evalArg = this.evaluate(arg); //Otherwise, evaluate it in here
        }
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
      return accumulator.toString().stripTrailing();
    }

    @Override
    protected String visitBlockReference(String blockRef) {
      T blockAccumulator = this.initializeValue();
      BlockReferenceIterator blockRefs = new BlockReferenceIterator(blockRef);
      while (blockRefs.hasNext()) {
        String refEval = this.evaluate(blockRefs.next());
        if (isError(refEval)) {
          return refEval;
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
  private static final class SexpEvaluatorSum extends SexpEvaluatorAccumulator<StringBuilder> {

    /**
     * Constructs a {@link SexpEvaluatorSum}.
     * @param model the model that this evaluates {@link Sexp}s from.
     */
    SexpEvaluatorSum(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    protected StringBuilder initializeValue() {
      return new StringBuilder("0.0");
    }

    @Override
    protected void accumulate(StringBuilder accumulator, String evalArg) {
      Double var = (Double.parseDouble(accumulator.toString()) + Double.parseDouble(evalArg));
      accumulator.delete(0, accumulator.length());
      accumulator.append(var);
    }

    @Override
    public String visitBoolean(boolean b) {
      return "0";
    }

    @Override
    public String visitString(String s) {
      return "0";
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
  private static final class SexpEvaluatorProduct extends SexpEvaluatorAccumulator<StringBuilder> {

    /**
     * Constructs a {@link SexpEvaluatorProduct}.
     * @param model the model that this evaluates {@link Sexp}s from.
     */
    SexpEvaluatorProduct(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    protected StringBuilder initializeValue() {
      return new StringBuilder("0.0");
    }

    @Override
    protected void accumulate(StringBuilder accumulator, String evalArg) {
      double var;
      if (Double.parseDouble(accumulator.toString()) == 0.0 && evalArg.equals("nonNumeric")) {
        var = 0.0;
      }
      else if (Double.parseDouble(accumulator.toString()) == 0.0) {
        var = (1.0 * Double.parseDouble(evalArg));
      }
      else {
        var = (Double.parseDouble(accumulator.toString()) * Double.parseDouble(evalArg));
      }
      accumulator.delete(0, accumulator.length());
      accumulator.append(var);
    }

    @Override
    public String visitBoolean(boolean b) {
      return "nonNumeric";
    }

    @Override
    public String visitString(String s) {
      return "nonNumeric";
    }

    @Override
    protected final String blankCellEvaluant() {
      return "nonNumeric";
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

    /**
     * Constructs a {@link SexpEvaluatorLessThan}.
     * @param model the model that this evaluates {@link Sexp}s from.
     */
    SexpEvaluatorLessThan(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    public String visitSList(List<Sexp> args) {
      if (args.size() != 2) {
        return errorInvalidArity;
      }
      Sexp arg1 = args.get(0);
      Sexp arg2 = args.get(1);
      String arg1Eval;
      String arg2Eval;
      //If the sexp is a list, it should be evaluated by a new evaluator from the start
      if (arg1.toString().indexOf("(") == 0) {
        arg1Eval = new SExpEvaluatorFormulaWorksheet(super.model).evaluate(arg1);
      } else {
        arg1Eval = this.evaluate(arg1); //Otherwise, evaluate it in here
      }
      if (arg2.toString().indexOf("(") == 0) {
        arg2Eval = new SExpEvaluatorFormulaWorksheet(super.model).evaluate(arg2);
      } else {
        arg2Eval = this.evaluate(arg2); //Otherwise, evaluate it in here
      }
      if (arg1Eval.equals(super.blankCellEvaluant())
          || arg2Eval.equals(super.blankCellEvaluant())) {
        return errorInvalidBlankCellRef;
      }
      if (isError(arg1Eval) || isError(arg2Eval)) {
        return errorArgIsError;
      }
      try {
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

    /**
     * Constructs a {@link SexpEvaluatorEnum}.
     * @param model the model that this evaluates {@link Sexp}s from.
     */
    SexpEvaluatorEnum(FormulaWorksheetModel model) {
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
    protected final String blankCellEvaluant() {
      return "<blank>";
    }
  }

  /**
   * Given a string specifying worksheet cells that are at the corners of some region, iterates over
   * the cells in that region.
   */
  private static final class BlockReferenceIterator implements Iterator<String> {

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
    private BlockReferenceIterator(String blockRef) {
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
