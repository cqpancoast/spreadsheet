# Beyond gOOD: A Spreadsheet Application for our Object Oriented Design Class. 

This README is an overview of our design. It is split into subsections that discuss the _model_, 
the _view_, and the _controller_. As the assignment over the remainder of this semester, these
sections will be filled in.

## The Model

### Interface and Implementation

Our interface for the model is `WorksheetModel`. This is a super general worksheet that has a grid
of values of a generic type and some procedure for evaluating these types to strings. That's it! We
wanted to be super general with this one.

Our implementing class is `FormulaWorksheetModel`, named as such because its cells contain 
string-represented `Sexp` formulae. (That, is, 0.0 is represented as "0.0", true is represented as
"true", "string" is represented as "/"string/"".) This class is _also_ super freaking simple — the 
complicated stuff happens in `FormulaWorksheetModel.getEval(col, row)`, which evaluates the 
string-represented `Sexps` according to `FWM`'s evaluation procedure.

### The Evaluator

In the `getEval()` method, the raw contents of the cell are immediately passed to the evaluator,
`SexpEvaluatorFormulaWorksheet`, an evaluator *explicitly coupled* to `FWM`. `SEFW` uses `Parser` to
parse the raw string into a `Sexp`. That `Sexp` is then evaluated according to the specs in the
assignment (so far as we are aware). We added a function `ENUM`, which takes in a variable number of
any argument type and list them. (A potential output could look like "1.0 true "bees"".)

Starting from the top, the evaluator class structure is as follows: `SexpEvaluator` implements
`SexpVisitor`, preserving the generic type from the visitor. `SexpEvaluator` is implemented by
`SexpEvaluatorFormulaWorksheet`, which is the typical evaluator you're expecting that evaluates the
`Sexp`s in the grid and returns a String. It is _also_ implemented by `SexpCheckCycles`, which
evaluates a `Sexp` in the grid to return a boolean. We chose this design rather than having the two
evaluators implement `SexpVisitor` directly because these is functionality shared by any evaluator
that works specifically on `FormulaWorksheetModel`s.

The functions within `SexpEvaluatorFormulaWorksheet` are visitors contained within the class itself.
All of them implement the class, but as `SUM`, `PRODUCT`, and `ENUM` all iterate over a variable 
argument SList, the best design was to create an abstract class `SexpEvaluatorAccumulator`, which 
abstracts out the shared functionality from the visitor classes for `SUM`, `PRODUCT`, and `ENUM`. 
`LESSTHAN` (which, for clarity's sake, is the name in code for the command `<` (the actual name of 
the class is `SexpEvaluatorLessThan`)) implements `SEFW` directly.

## The View

The interface for the view is `WorksheetView`. It's purpose is to visually represent a
`WorksheetModel` as a 2-D grid of cells. It's sole method `render()` processes the data of the
 model and displays it in a human-readable manner. The two implementing classes of the view are
`TextualWorksheetView` and `GridWorksheetView`.

`TextualWorksheetView` textually renders the grid by appending it to an appendable in the same
 format that the data is read in from files (cell name followed by its raw contents).

`GridWorksheetView` uses Java Swing to render the grid via graphics. The frame contains a panel of
 two buttons, to be used to increase the grid size, and a `GridPanel` containing the cells of the
 grid. The `GridPanel` draws cell labels along the top and left sides, and the evaluated contents
 of each cell in their properly corresponding cells in the grid.

## The Controller

### Interfaces

There are two interfaces involved in our implementation of the controller: `IWorksheetController`
and `FeatureListener`. In this manner, we separated `WorksheetController`'s (our only controller
implementation class) functionality as a controller from it's functionality as something that
listens for stuff to happen in a view.

### Changes to the View

After splitting the view into `EditableGridWorksheetView` and the regular read-only version
`GridWorksheetView`, we added methods to their interface `WorksheetView` that gave the ability
to add `FeatureListener`s to the both, though in `GridWorksheetView` and in `TextualWorksheetView`
those methods do nothing. In `EditableGridWorksheetView`, when a `FeatureListener` is added, the
view "hooks up" the listener to a bunch of swing events that occur. In this way, we are able to
completely divorce the controller from swing. Yippee!
