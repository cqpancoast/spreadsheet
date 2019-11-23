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



}