package edu.cs3500.spreadsheets.provider.view;

import java.io.IOException;

/**
 * Represents a view for a Worksheet.
 */
public interface WorksheetView {

  /**
   * Saves the current state of the model.
   * @throws IOException if something goes wrong when saving.
   */
  void save() throws IOException;

  /**
   * Visually renders this worksheet.
   */
  void render();
}