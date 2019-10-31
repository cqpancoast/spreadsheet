package edu.cs3500.spreadsheets;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetReader;
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
   * Determine whether main's args follow the format: -in some-filename -eval some-cellname
   * @param args main args
   * @return whether args follow the correct format
   */
  private static boolean wellFormedCommand(String[] args) {
    return args.length == 4
        && args[0].equals("-in")
        && args[2].equals("-eval")
        && Coord.validCellName(args[3]);
  }

//  /**
//   * Creates a {@link WorksheetModel}, where each line of a file specifies the contents of a cell in
//   * the grid. Throws an exception if any of those lines aren't well formed.
//   * @param fileName relative path of the file to be read
//   * @return a created {@link WorksheetModel}
//   * @throws IllegalArgumentException if a line in file isn't well formed
//   */
//  private static WorksheetModel<String> createWorksheetModel(String fileName)
//      throws IllegalArgumentException {
//    BufferedReader reader;
//    try {
//      reader = new BufferedReader(new FileReader(new File(fileName)));
//    } catch (FileNotFoundException e) {
//      throw new IllegalArgumentException("File not found.");
//    }
//    WorksheetBuilder<FormulaWorksheetModel> modelBuilder = new FormulaWorksheetBuilder();
//    String fileLine = null;
//    while(true) {
//      try {
//        fileLine = reader.readLine();
//        if (fileLine == null)
//          break;
//      } catch (IOException e) {
//        //Do nothing; file is done being read from.
//      }
//      populateCell(fileLine, modelBuilder);
//    }
//    return modelBuilder.createWorksheet();
//  }
//
//  /**
//   * Populates a cell in the {@link FormulaWorksheetModel} being built by the
//   * {@link FormulaWorksheetBuilder} if fileLine is of the form "COL/ROW CONTENTS", throwing an
//   * {@link IllegalArgumentException} otherwise.
//   * @param fileLine a file line, ideally of the form "COL/ROW CONTENTS"
//   * @param modelBuilder a builder for a {@link FormulaWorksheetModel}
//   * @throws IllegalArgumentException if fileLine isn't well formed
//   */
//  private static void populateCell(String fileLine,
//      WorksheetBuilder<FormulaWorksheetModel> modelBuilder)
//      throws IllegalArgumentException {
//    int splitIndex = fileLine.indexOf(" ");
//    String cellString = fileLine.substring(0, splitIndex);
//    String contentString = fileLine.substring(splitIndex + 1);
//    if (fileLine.length() != 2) {
//      throw new IllegalArgumentException("Invalid file: more than two arguments per line.");
//    }
//    List<Integer> colAndRow = Coord.fromString(cellString);
//    modelBuilder.createCell(colAndRow.get(0), colAndRow.get(1), contentString);
//  }

  /**
   * Evaluates a cell in the worksheet and prints the result if there are no errors within the
   * worksheet. If there are errors in a worksheet, don't evaluate the cell, but rather print
   * messages of the form "Error in cell Z42: ..." for every errored cell in the model.
   * @param model a {@link WorksheetModel}
   * @param evalCellName a string representation of the cell to evaluate's position in the
   *                     worksheet grid
   */
  private static void evaluateCellInWorksheet(WorksheetModel<String> model, String evalCellName) {
    List<Integer> colAndRow = Coord.fromString(evalCellName);
    boolean errorInWorksheet = false;
    for (Coord cellCoord : worksheet) {
      String cellEval = model.getEval(col, row);
      if (cellEval.split(" ")[0].equals("!#ERROR")) {
        errorInWorksheet = true;
        System.out.println("Error in cell " + cellName + ": " + cellEval);
      }
    }
    if (!errorInWorksheet) {
      System.out.println(model.getEval(col, row));
    }
  }

}
