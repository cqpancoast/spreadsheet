package edu.cs3500.spreadsheets;

import edu.cs3500.spreadsheets.controller.WorksheetController;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.SexpEvaluator;
import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetReader;
import edu.cs3500.spreadsheets.view.EditableGridWorksheetView;
import edu.cs3500.spreadsheets.view.GridWorksheetView;
import edu.cs3500.spreadsheets.view.TextualWorksheetView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.List;

/**
 * The main class for our program.
 */
public class BeyondGood {

  /**
   * The main entry point.
   * @param args  any command-line arguments
   */
  public static void main(String[] args) {

    // Check if the command arguments are well formed
    if (!wellFormedCommand(args)) {
      System.out.println("Malformed command arguments. You're better than this.");
      return;
    }

    // Create the model from the file, if one is provided
    IWorksheetModel model = null;
    if (args[0].equals("-gui")) {
      try {
        model = WorksheetReader.read(new FormulaWorksheetModel.FormulaWorksheetBuilder(),
            new StringReader(""));
      } catch (Exception e) {
        System.out.println("Error creating worksheet model:\n" + e.getMessage());
      }
    } else if (args[0].equals("-in")) {
      String fileName = args[1];
      try {
        model = WorksheetReader.read(new FormulaWorksheetModel.FormulaWorksheetBuilder(),
            new BufferedReader(new FileReader(new File(fileName))));
      } catch (Exception e) {
        System.out.println("Error creating worksheet model:\n" + e.getMessage());
        return;
      }
    }

    // Display the correct thing given the model
    if (args[2].equals("-eval")) {
      try {
        assert model != null;
        evaluateCellInWorksheet(model, args[3]);
      } catch (Exception e) {
        System.out.println("Error in cell evaluation, man.");
      }
    } else if (args[2].equals("-save")) {
      try {
        FileWriter fileWriter = new FileWriter(new File(args[3]));
        new TextualWorksheetView(model, fileWriter).render();
        fileWriter.close();
      } catch (Exception e) {
        System.out.println("Error in saving file, man.");
      }
    } else if (args[2].equals("-gui") || args[0].equals("-gui")) {
      try {
        new GridWorksheetView(model).render();
      } catch (Exception e) {
        System.out.println("Error in displaying grid, man.");
      }
    } else if (args[2].equals("-edit") || args[0].equals("-edit")) {
      try {
        WorksheetController controller = new WorksheetController(model);
        controller.setView(new EditableGridWorksheetView(model));
        controller.go();
      } catch (Exception e) {
        System.out.println(e.getMessage());
        System.out.println("Error in displaying editable grid, man.");
      }
    }
  }

  /**
   * Determine whether main's args follow one of several formats:
   * - -in [some-filename] -eval [some-cell]
   * - -in [some-filename] -save [some-new-filename]
   * - -in [some-filename] -gui
   * - -gui
   * -in [some-filename] -edit
   * -edit
   * Does not check for validity of file name, but does check for validity of cell name.
   * @param args main args
   * @return whether args follow the correct format
   */
  private static boolean wellFormedCommand(String[] args) {
    if (args[0].equals("-gui") || args[0].equals("-edit")) {
      return args.length == 1;
    } else if (args[0].equals("-in")) {
      if (args.length == 4) {
        if (args[2].equals("-eval")) {
          return Coord.validCellName(args[3]);
        } else if (args[2].equals("-save")) {
          return true;
        }
      } else if (args.length == 3) {
        return args[2].equals("-gui") || args[2].equals("-edit");
      }
    }
    return false;
  }

  /**
   * Evaluates a cell in the worksheet and prints the result if there are no errors within the
   * worksheet. If there are errors in a worksheet, don't evaluate the cell, but rather print
   * messages of the form "Error in cell Z42: ..." for every errored cell in the model.
   * @param model a {@link IWorksheetModel}
   * @param evalCellName a string representation of the cell to evaluate's position in the
   *                     worksheet grid
   */
  private static void evaluateCellInWorksheet(IWorksheetModel model, String evalCellName) {
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
      String eval = model.getEval(evalCellCoord.col, evalCellCoord.row);
      try {
        double dub = Double.parseDouble(eval);
        System.out.print(String.format("%f", dub));
      } catch (NumberFormatException e) {
        System.out.print(eval);
      }
    }
  }
}
