import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

import edu.cs3500.spreadsheets.model.FormulaWorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetModel;
import edu.cs3500.spreadsheets.model.WorksheetReader;
import edu.cs3500.spreadsheets.view.TextualWorksheetView;
import edu.cs3500.spreadsheets.view.WorksheetView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
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
   * @param readFile a readable readFile that can be read from by {@link WorksheetReader}
   * @throws IllegalArgumentException if readFile cannot be read from by {@link WorksheetReader}
   */
  private void renderTextHarness(Readable readFile) {

    WorksheetModel<String> model;
    try {
      model = WorksheetReader.read(new FormulaWorksheetModel.FormulaWorksheetBuilder(), readFile);
    } catch (IllegalStateException e) {
      throw new IllegalArgumentException("The given file cannot be read from by WorksheetReader: "
          + e.getMessage());
    }

    Appendable renderFile = new StringBuilder();
    WorksheetView view = new TextualWorksheetView(model, renderFile);
    view.render();

    Set<String> readFileLines
        = new HashSet<String>(Arrays.asList(readFile.toString().split("\n")));
    Set<String> renderFileLines
        = new HashSet<String>(Arrays.asList(renderFile.toString().split("\n")));

    assertEquals(readFileLines, renderFileLines);
  }

  //NOTE delete if necessary
  private void renderTextHarness(String readFileText) {
    this.renderTextHarness(new StringReader(readFileText));
  }

  // As render calls toString directly, all tests for toString are left out.
  // NOTE: this may change in the future if render ends up doing anything other than toString

  /** Tests for {@link TextualWorksheetView#render()}. */

  @Test
  public void render_emptyFile() {
    renderTextHarness("");
  }


  @Test
  public void render_noErrorsBasicCalls() throws FileNotFoundException { //HELP is this ok or should I go with the below?
    renderTextHarness(new FileReader(new File("buildFiles/noErrorsBasicCalls.gOOD")));
  }

  @Test
  public void render_noErrorsComplexCalls() {
    try {
      renderTextHarness(new FileReader(new File("buildFiles/noErrorsComplexCalls.gOOD")));
    } catch (FileNotFoundException e) {
      fail();
    }
  }

  @Test
  public void render_errorsInCalls() {
    try {
      renderTextHarness(new FileReader(new File("buildFiles/errorsInCalls.gOOD")));
    } catch (FileNotFoundException e) {
      fail();
    }
  }

}