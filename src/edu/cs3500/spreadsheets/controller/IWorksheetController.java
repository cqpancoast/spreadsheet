package edu.cs3500.spreadsheets.controller;

import edu.cs3500.spreadsheets.model.IWorksheetModel;
import edu.cs3500.spreadsheets.view.IWorksheetView;

/**
 * Controls interaction between the user, an {@link IWorksheetModel}, and an {@link IWorksheetView}.
 * Ensures that the view displays the latest state of the model at every point in the user's
 * interaction.
 */
public interface IWorksheetController {

  /**
   * Runs this controller.
   */
  void go();

  /**
   * Sets the view that this controller displays to.
   * @param view  a method of viewing a spreadsheet
   */
  void setView(IWorksheetView view);

}
