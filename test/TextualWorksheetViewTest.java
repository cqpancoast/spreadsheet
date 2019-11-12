import static junit.framework.TestCase.assertTrue;

import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetReader;
import edu.cs3500.spreadsheets.view.TextualWorksheetView;
import edu.cs3500.spreadsheets.view.WorksheetView;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for {@link TextualWorksheetView}.
 */
public class TextualWorksheetViewTest {

  /**
   * Takes in a readable readFile, uses a {@link WorksheetReader} to parse it into a
   * {@link FormulaWorksheetModel}, then tests that the rendering of {@link TextualWorksheetView} is
   * the same as the original readFile.
   * @param readFileContents contents of a file to be read from by {@link WorksheetReader}
   * @throws IllegalArgumentException if readFile cannot be read from by {@link WorksheetReader}
   */
  private void renderTextHarness(String readFileContents) {

    WorksheetModel<String> model;
    try {
      model = WorksheetReader.read(new FormulaWorksheetModel.FormulaWorksheetBuilder(),
          new StringReader(readFileContents));
    } catch (IllegalStateException e) {
      throw new IllegalArgumentException("The given file cannot be read from by WorksheetReader: "
          + e.getMessage());
    }

    Appendable renderFile = new StringBuilder();
    WorksheetView view = new TextualWorksheetView(model, renderFile);
    view.render();

    Set<String> readFileLines
        = new HashSet<String>(Arrays.asList(readFileContents.split("\n")));
    Set<String> renderFileLines
        = new HashSet<String>(Arrays.asList(renderFile.toString().split("\n")));

    // Contains is used instead of checking equality because of the case where cells are written to
    // multiple times in the read file.
    assertTrue(readFileLines.containsAll(renderFileLines));
  }

  //NOTE delete if necessary
  private void renderTextHarnessFromPath(String filePath) {
    try {
      this.renderTextHarness(Files.readString(new File(filePath).toPath()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // As render calls toString directly, all tests for toString are left out.
  //NOTE: this may change in the future if render ends up doing anything other than toString

  /** Tests for {@link TextualWorksheetView#render()}. */

  @Test
  public void render_emptyFile() {
    renderTextHarness("");
  }

  @Test
  public void render_noErrorsBasicCalls() {
    renderTextHarnessFromPath("buildFiles/noErrorsBasicCalls.gOOD");
  }

  @Test
  public void render_noErrorsComplexCalls() {
    renderTextHarnessFromPath("buildFiles/noErrorsComplexCalls.gOOD");
  }

  @Test
  public void render_errorsInCalls() {
    renderTextHarnessFromPath("buildFiles/errorsInCalls.gOOD");
  }

  @Test
  public void render_overwrittenCalls() {
    renderTextHarnessFromPath("buildFiles/noErrorsBasicCallsWithOverwrites.gOOD");
  }

}
