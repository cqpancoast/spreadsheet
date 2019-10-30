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

  public SExpEvaluatorFormulaWorksheet(FormulaWorksheetModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Evaluator constructor received null model.");
    }
    this.model = model;
  }

  /**
   * Evaluates sexp according to the rules of {@link FormulaWorksheetModel}.
   * @param sexp a sexp
   * @return evaluated s
   */
  public String evaluate(Sexp sexp) { //NOTE do we really need two?
    return sexp.accept(this);
  }

  /**
   * Evaluates s according to the rules of {@link FormulaWorksheetModel}, pre-processing it into a
   * form where it can be evaluated as a sexp or returning an error symbol if it cannot be.
   * @param s the raw contents of a cell
   * @return the evaluation of s
   */
  public String evaluate(String s) {
    if (isBlankCell(s)) {
      return this.getBlankCellValue();
    }
    String processedRaw = this.processRawValue(s);
    if (isError(processedRaw)) {
      return processedRaw;
    }
    try {
      Sexp parsedSexp = Parser.parse(processedRaw);
      return this.evaluate(parsedSexp);
    } catch (IllegalArgumentException e) {
      return errorSyntax;
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

  /**
   * Ensures that the raw value has the correct syntax with respect to equals signs and formulae.
   * Returns syntax error symbol if raw begins with an open paren, removes equals sign if raw begins
   * with one. THIS DOES NOT ENSURE THAT RAW IS PARSABLE, OR HAS THE CORRECT SYNTAX IN GENERAL.
   * @param raw raw contents of a cell
   * @return processed string value or error symbol
   */
  private String processRawValue(String raw) {
    if (raw.indexOf("=") == 0) {
      return raw.substring(1);
    } else if (raw.indexOf("(") == 0) {
      return errorSyntax;
    } else {
      return raw;
    }
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
    if (this.hasCycles(ref)) {
      return errorCyclicRef;
    }
    List<Integer> refCoord = Coord.fromString(ref);
    return this.model.getEval(refCoord.get(0), refCoord.get(1));
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
          this.accumulate(accumulator, evalArg); //FIXME
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


  //* CYCLE CHECKER *//

  /**
   * The sole field of this class is an {@link Sexp} that is the parsed name of the Coord whose
   * formula is being checked for cycles.
   *
   * This {@link SexpVisitor} determines whether the {@link Sexp} of the Coord in question is found
   * in its formula's own {@link Sexp}, either directly or indirectly, which would create a cycle.
   *
   * The {@link SexpVisitor} returns true if there are cycles, and false if there are not cycles.
   */
  public static class SexpCheckCycles implements SexpVisitor<Boolean> {
    private final FormulaWorksheetModel model;
    private List<String> cellsSoFar;

    /**
     * Constructs an {@link SexpVisitor} that checks for cycles in the {@link Sexp} of the given cell.
     * @param model the {@link FormulaWorksheetModel} in question
     */
    public SexpCheckCycles(FormulaWorksheetModel model) {
      this.model = model;
      this.cellsSoFar = new ArrayList<>();
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
        if (each.accept(this)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public Boolean visitSymbol(String s) {
      if (cellsSoFar.contains(s)) {
        return true;
      }
      cellsSoFar.add(s);
      List<Integer> cellCoord = Coord.fromString(s);
      String refRawString = this.model.getRaw(cellCoord.get(0), cellCoord.get(1));
      if (refRawString.substring(0, 0).equals("=")) { //TODO equals sign bullshit
        refRawString = refRawString.substring(1);
      }
      return Parser.parse(refRawString).accept(this);
    }

    @Override
    public Boolean visitString(String s) {
      return false;
    }
  }

}