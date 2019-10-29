package edu.cs3500.spreadsheets.sexp;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A visitor to evaluate string-represented {@link Sexp} values in a worksheet according to the
 * rules of {@link FormulaWorksheetModel}. If the evaluation results in an error, the returned
 * string representation of an {@link SSymbol} has the form !#ERROR_[ERRORDESCRIPTION].
 */
public class SExpEvaluatorFormulaWorksheet implements SexpVisitor<String> {

  private final FormulaWorksheetModel model;
  //TODO make all the errors constant strings

  public SExpEvaluatorFormulaWorksheet(FormulaWorksheetModel model) {
    this.model = model;
  }

  /**
   * Evaluates s according to the rules of {@link FormulaWorksheetModel}.
   * @param s a sexp
   * @return evaluated s
   */
  public String evaluate(Sexp s) { //NOTE something's weird here
    if (this.isBlankCell(s)) {
      return this.getBlankCellValue();
    } else {
      return s.accept(this);
    }
  }

  public String evaluate(String s) {
    return this.evaluate(Parser.parse(s));
  }

  /**
   * Determines whether the given S-exp is a blank cell.
   * @param s an S-exp
   * @return whether string rep of S-exp is of a blank cell
   */
  protected final boolean isBlankCell(Sexp s) {
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
      return "!#ERROR_SYNTAX";
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
        return "!#ERROR_INVALIDCOMMAND";
    }
  }

  @Override
  public String visitSymbol(String s) {
    if (this.isBlockReference(s)) {
      return this.visitBlockReference(s); //HELP BLERNER is this correct syntax for visitor pattern?
    } else if (this.isReference(s)) {
      return this.visitReference(s);
    } else if (this.isError(s)) {
      return "!#ERROR_REFISERROR";
    } else {
      return "!#ERROR_INVALIDSYMBOL";
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
    return "!#ERROR_INVALIDBLOCKCELLREF";
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
  protected final boolean isBlockReference(String s) {
    String[] refs = s.split(":");
    if (refs.length != 2) {
      return false;
    }
    for (String ref : refs) {
      if (!this.isReference(ref)) {
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
  protected final boolean isReference(String s) {
    return Coord.validReferenceName(s);
  }

  /**
   * Determines whether the given evaluated S-exp is an error.
   * @param evalArg an evaluated S-exp
   * @return whether string rep of s-exp is an error
   */
  protected final boolean isError(String evalArg) {
    String[] splitMaybeError = evalArg.split("_");
    return splitMaybeError[0].equals("!#ERROR");
  }


  //* FUNCTION EVALUATORS *//

  /**
   * Sums all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorSum extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorSum(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    public String visitBoolean(boolean b) {
      return "!#ERROR_ARGTYPE";
    }

    @Override
    public String visitNumber(double d) {
      return String.valueOf(d);
    }

    @Override
    public String visitSList(List<Sexp> args) {
      double sumTotal = 0;
      for (Sexp arg : args) {
        String evalArg = this.evaluate(arg);
        if (this.isError(evalArg)) {
          if (evalArg.equals("!#ERROR_ARGTYPE")) {
            return evalArg;
          }
          return "!#ERROR_ARGISERROR";
        }
        try {
            sumTotal += Double.parseDouble(evalArg);
          } catch (NumberFormatException e) {
            // Do nothing if arg not parsable as a double.
          }
        }
      return String.valueOf(sumTotal);
    }

    @Override
    public String visitString(String s) {
      return "!#ERROR_ARGTYPE";
    }

    @Override
    protected final String getBlankCellValue() {
      return "0";
    }

    @Override
    protected String visitBlockReference(String blockRef) {
      double blockSumTotal = 0;
      BlockReferenceIterator blockRefs = new BlockReferenceIterator(blockRef);
      while (blockRefs.hasNext()) {
        String ref = blockRefs.next();
        String refEval = this.evaluate(ref);
        if (this.isError(refEval)) {
          return "!#ERROR_REFISERROR";
        }
        blockSumTotal += Double.parseDouble(refEval);
      }
      return String.valueOf(blockSumTotal);
    }

  }

  /**
   * Multiplies all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorProduct extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorProduct(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    public String visitSList(List<Sexp> args) {
      double productTotal = 0;
      for (Sexp arg : args) {
        String evalArg = this.evaluate(arg);
        if (this.isError(evalArg)) {
          return "!#ERROR_ARGISERROR";
        }
        try {
          productTotal *= Double.parseDouble(evalArg);
        } catch (NumberFormatException e) {
          // Do nothing if arg not parsable as a double.
        }
      }
      return String.valueOf(productTotal);
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
  private final static class SexpEvaluatorLessThan extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorLessThan(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    public String visitSList(List<Sexp> args) {
      if (args.size() != 2) {
        return "!#ERROR_WRONGARITY";
      }
      Sexp arg1 = args.get(0);
      Sexp arg2 = args.get(1);
      if (this.isBlankCell(arg1) || this.isBlankCell(arg2)) {
        return "!#ERROR_INVALIDBLANKCELLREF";
      }
      if (this.isError(evalArg1) || this.isError(evalArg2)) {
        return "!#ERROR_ARGISERROR";
      } try {
        return String.valueOf(Double.parseDouble(evalArg1) < Double.parseDouble(evalArg2));
      } catch (NumberFormatException e) {
        return "!#ERROR_WRONGARGTYPE";
      }
    }
  }

  /**
   * Lists all of the args separated by spaces. Returns error symbol if any of the args are errors.
   */
  private final static class SexpEvaluatorEnum extends SExpEvaluatorFormulaWorksheet {

    public SexpEvaluatorEnum(FormulaWorksheetModel model) {
      super(model);
    }

    @Override
    public String visitSList(List<Sexp> args) {
      StringBuilder enumString = new StringBuilder();
      for (Sexp arg : args) {
        String evalArg = arg.accept(this);
        if (this.isError(evalArg)) {
          return "!#ERROR_ARGISERROR";
        } else if (this.isBlankCell(arg)) { //NOTE why is it always false!?
          evalArg = "<blank>";
        }
        enumString.append(evalArg).append(" ");
      }
      enumString.trimToSize();
      return enumString.toString();
    }
  }

  /**
   * Given a string specifying worksheet cells that are at the corners of some region, iterates over
   * the cells in that region.
   */
  private final static class BlockReferenceIterator implements Iterator<String> { //TODO

    private final int startCol, startRow, endCol, endRow;
    private int col, row;

    BlockReferenceIterator(String blockRef) {
      //TODO
      col = Math.min(this.startCol, this.endCol);
      row = Math.min(this.startRow, this.endRow);
    }

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public String next() {
      return null;
    }
  }
}