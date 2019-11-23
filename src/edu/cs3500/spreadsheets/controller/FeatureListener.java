package edu.cs3500.spreadsheets.controller;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.view.IWorksheetView;

/**
 * An interface that enumerates the possible classes of events that this controller can receive
 * from an {@link IWorksheetView}. Provides an abstract framework for dealing with inputs in this
 * class's single method.
 */
public interface FeatureListener {

  /**
   * Handles cell selection of a particular coordinate.
   * @param c  selected coordinate in grid
   */
  void onCellSelection(Coord c);

  /**
   * Handles deselection of a currently selected cell, if there is one.
   */
  void onCellDeselection();

  /**
   * Updates the contents of a cell in the model.
   * @param c  a cell's coordinate in the grid
   * @param s  the new contents of the cell
   */
  void onCellContentsUpdate(Coord c, String s);

  /**
   * Saves the current version of the model.
   */
  void save();

  /**
   * Quits the program.
   */
  void quit();

}