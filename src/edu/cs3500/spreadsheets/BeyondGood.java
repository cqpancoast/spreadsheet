package edu.cs3500.spreadsheets;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetReader;
import edu.cs3500.spreadsheets.sexp.SexpEvaluator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * The main class for our program.
 */
public class BeyondGood {
  /**
   * The main entry point.
   * @param args any command-line arguments
   */
  public static void main(String[] args) {

    if (!wellFormedCommand(args)) {
      System.out.println("Malformed command arguments. You're better than this.");
      return;
    }

    String fileName = args[1];
    String cellName = args[3];

    WorksheetModel<String> model;
    try {
      model = WorksheetReader.read(new FormulaWorksheetModel.FormulaWorksheetBuilder(),
          new BufferedReader(new FileReader(new File(fileName))));
    } catch (Exception e) {
      System.out.println("Error creating worksheet model:\n" + e.getMessage());
      return;
    }

    try {
      evaluateCellInWorksheet(model, cellName);
    } catch (Exception e) {
      System.out.println("Error during cell evaluation process:\n" + e.getMessage());
    }

  }

  /**
   * Determine whether main's args follow the format: "-in some-filename -eval some-cellname".
   * @param args main args
   * @return whether args follow the correct format
   */
  private static boolean wellFormedCommand(String[] args) {
    return args.length == 4
        && args[0].equals("-in")
        && args[2].equals("-eval")
        && Coord.validCellName(args[3]);
  }

  /**
   * Evaluates a cell in the worksheet and prints the result if there are no errors within the
   * worksheet. If there are errors in a worksheet, don't evaluate the cell, but rather print
   * messages of the form "Error in cell Z42: ..." for every errored cell in the model.
   * @param model a {@link WorksheetModel}
   * @param evalCellName a string representation of the cell to evaluate's position in the
   *                     worksheet grid
   */
  private static void evaluateCellInWorksheet(WorksheetModel<String> model, String evalCellName) {
    List<Integer> evalCellPosn = Coord.fromString(evalCellName);
    Coord evalCellCoord = new Coord(evalCellPosn.get(0), evalCellPosn.get(1));
    boolean errorInWorksheet = false;
    for (Coord coord : model.getActiveCells()) {
      String cellEval = model.getEval(coord.col, coord.row);
      if (SexpEvaluator.isError(cellEval)) {
        errorInWorksheet = true;
        System.out.println("Error in cell " + coord.toString() + ": " + cellEval);
      }
    }
    if (!errorInWorksheet) {
      System.out.println(model.getEval(evalCellCoord.col, evalCellCoord.row));
    }
  }

}
