Spreadsheet View Design:
	Interface WorksheetView
		JFrame EditableWorksheetView
			The editable worksheet has a:
				ReadableWorksheetModel - Readonly model to get cell info
				WorksheetController - to talk with the model
				JPanel ScrollableWorksheetPanel - Handles the mouse input to determine when the panel is clicked on and where. It then passes that information to worksheet panel to highlight the selected color as red. It also tells the model what the currently selected cell's coordinate is.
					JPanel TopBorder - Column header for the worksheet
					JPanel SideBorder - Row header for the worksheet
					JPanel WorksheetPanel - Draws Cells and highlights the currently selected cell as red. Also errors are drawn in red as well.
				JPanel ToolBar - Shows the currently selected cell's original contents. Handles input of two buttons that determine whether to pass the edited content to the controller, or to reset the input to the cells actual content.

		JFrame VisualWorksheetView
			The visual worksheet has a:
				1. ReadableWorksheetModel - Readonly model to get cell info
				2. JPanel ScrollableWorksheetPanel - This handles the scrolling of the panel.
					JPanel TopBorder - Column headers for the worksheet
					JPanel SideBorder - Row header for the worksheet
					JPanel WorksheetPanel - Draws all of the cells.


