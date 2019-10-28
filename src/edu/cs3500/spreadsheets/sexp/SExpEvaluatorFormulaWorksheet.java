package edu.cs3500.spreadsheets.sexp;

import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import java.util.List;

/**
 * A visitor to evaluate string-represented {@link Sexp} values in a worksheet according to the
 * rules of {@link FormulaWorksheetModel}. If the evaluation results in an error, the returned
 * string representation of an {@link SSymbol} has the form !#ERROR_[ERRORDESCRIPTION].
 */
public class SExpEvaluatorFormulaWorksheet implements SexpVisitor<String> {

  //TODO make all the errors constant strings

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
        return new SList(l).accept(new SexpEvaluatorSum());
      case "PRODUCT":
        return new SList(l).accept(new SexpEvaluatorProduct());
      case "<":
        return new SList(l).accept(new SexpEvaluatorLessThan());
      case "ENUM":
        return new SList(l).accept(new SexpEvaluatorEnum());
      default:
        return "!#ERROR_INVALIDCOMMAND";
    }
  }

  @Override
  public String visitSymbol(String s) {
    if (this.isBlockReference(s)) {
      return this.visitBlockReference();
    } else {

    }
  }

  @Override
  public String visitString(String s) {
    return "\"" + s + "\"";
  }

  /**
   * Determines whether the given evaluated S-exp is an error.
   * @param evalArg an evaluated S-exp
   * @return whether string rep of s-exp is an error
   */
  protected boolean isError(String evalArg) {
    return false; //TODO
  }

  /**
   * Determines whether the given evaluated S-exp is a blank cell.
   * @param evalArg an evaluated S-exp
   * @return whether string rep of S-exp is of a blank cell
   */
  protected boolean isBlankCell(String evalArg) {
    return false; //TODO
  }

  //* FUNCTION EVALUATORS *//

  /**
   * Sums all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorSum extends SExpEvaluatorFormulaWorksheet {

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
        String evalArg = arg.accept(this);
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

  }

  /**
   * Multiplies all sexp arguments in the input list that can be interpreted as doubles. Blanks are
   * interpreted as zero. Returns error symbol if any of the args are errors.
   */
  private static final class SexpEvaluatorProduct extends SExpEvaluatorFormulaWorksheet {

    @Override
    public String visitSList(List<Sexp> args) {
      double productTotal = 0;
      for (Sexp arg : args) {
        String evalArg = arg.accept(this);
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

    @Override
    public String visitSList(List<Sexp> args) {
      if (args.size() != 2) {
        return "!#ERROR_WRONGARITY";
      }
      String evalArg1 = args.get(0).accept(this);
      String evalArg2 = args.get(1).accept(this);
      if (this.isBlankCell(evalArg1) || this.isBlankCell(evalArg2)) {
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

    @Override
    public String visitSList(List<Sexp> args) {
      StringBuilder enumString = new StringBuilder();
      for (Sexp arg : args) {
        String evalArg = arg.accept(this);
        if (this.isError(evalArg)) {
          return "!#ERROR_ARGISERROR";
        } else if (this.isBlankCell(evalArg)) {
          evalArg = "<blank>";
        }
        enumString.append(evalArg).append(" ");
      }
      enumString.trimToSize();
      return enumString.toString();
    }
  }

}