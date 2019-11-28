//package edu.cs3500.spreadsheets.provider.view;
//
//import edu.cs3500.spreadsheets.controller.WorksheetController;
//import edu.cs3500.spreadsheets.provider.model.ReadOnlyWorksheetModel;
//import java.awt.Graphics;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//
///**
// * Represents a tool bar that allows you to edit a cells original contents and then update that
// * cell to reflect the changes made.
// */
//public class ToolBar extends JPanel {
//  private final JButton accept;
//  private final JButton cancel;
//  private final JTextField editableArea;
//
//  /**
//   * Creates a new Toolbar that has access to the given model and can talk to the given controller.
//   * @param model the model to see what cell contents are.
//   * @param controller the controller to talk to when the user interacts with this element.
//   */
//  public ToolBar(ReadOnlyWorksheetModel model, WorksheetController controller) {
//    if (model == null || controller == null) {
//      throw new IllegalArgumentException("The model and controller cannot be null.");
//    }
//
//    this.accept = new JButton("✓");
//    this.accept.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        controller.updateCellAt(controller.getSelectedCellCoord(), editableArea.getText());
//      }
//    });
//
//    this.cancel = new JButton("X");
//    this.cancel.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        editableArea.setText(
//                model.getCellAt(controller.getSelectedCellCoord()).getBasicCellContent());
//      }
//    });
//    this.editableArea = new JTextField("", 20);
//    add(this.accept);
//    add(this.cancel);
//    add(this.editableArea);
//  }
//
//  /**
//   * Sets the String of the editable content bar.
//   * @param s the string value to set the content to.
//   */
//  public void setEditableContent(String s) {
//    if (s == null) {
//      throw new IllegalArgumentException("The String content cannot be null");
//    }
//
//    this.editableArea.setText(s);
//  }
//
//  @Override
//  protected void paintComponent(Graphics g) {
//    super.paintComponent(g);
//  }
//}
