# A Review of Your Code

Hey guys!

So, we did the thing and got your view to work with our model and controller. We did this by writing some adapters, in which our model and controller pretended to be your model and controller to trick your view into working with them, and your view pretended to be our view so that our controller could work with it. All in all, it wasn't a particularly difficult experience (Good job, guys!), but there were ways that it could have been made easier, which we'll expand on below. We'll also critique your code more generally, good parts and bad parts.

Please read all of this, I probably spent more time on this than I had to.

## Design Critique

### Coupling

Might as well start with the good. Your model, view, and controller don't depend upon each others' implementations whatsoever, 
which is a rather nice property to be able to count on. All they trust is that the others are able to follow the methods
specified in the interface, which is exactly how it's supposed to work.

### Encapsulation and Extensibility

The main problem that we have with this code is the model interface's reliance on implementation details. You let three things
leak through, which we'll address in turn.

- ___`InvalidCellException`___: Your model documentation doesn't have any notion of valid or invalid cells, so unless you specify what that would mean you can't have this class as a part of your logical interface. Even if you did have a notion of invalid cells, this should probably be relegated to an implementation detail. 
- ___Your cell class___: As you already have methods for getting and setting the values of particular cells, there is no reason that cells cannot be made into an implementation detail. Even though the cell class was type parametrized in a later version of the code you sent us, this still requires that your implementation _have_ a cell class. This hampers the extensibility of your code.
- ___`Function`___: Same as above, but _con sentimento_ — your interface requires this particular representation of a function and doesn't easily invite the possibility of other implementations. (For example, we wrote our functions into the parser itself.)

The takeaway is, requiring classes by putting them in the interface hampers the extensibility of your program because it prevents other possible implementations.

There are some other design decisions that I find funny, but those are covered in the documentation critque section.

## Implementation Critique

Starting with the bad this time, your view has the controller as a field, and the controller has the view as a field. Don't let your BIOS depend on Internet Explorer — there's a reason that we created the model, view, and controller in the order that we did, and that is that there are ways to have the view _not even know that the controller exists_, which would be the best possible option for dependency reasons.

Now for the good: we pretty much didn't have to look at your view implementation at all. I know that part of the point was that we were supposed to, but we just read your interfaces and built all of our adapters we had around those, and all of the view stuff worked exactly as it was supposed to with minimal cajoling on our parts. What you screw up with respect to encapsulation with the model, you make up for with the view. Good stuff!

## Documentation Critique

Basically, you need to be less vague. Rather than listing every small issue I have with your documentation, I'll riff off of the javadoc for `WorksheetModel`.

The entire text of your model interface is: _"Represents a spreadsheet that contains cells and can perform operations."_ This documentation is _not necessarily insufficient_ — with a sufficiently general interface (for example, one that has some abstract operations on some arguments and an unordered `Set` of cells) this might be enough. However, there are things that you assume in the design. For example, that cells are indexed by coordinates, that there are raw and evaulated values for cells, that there is a notion of evaluation at all, and that raw values are the things that can be acted upon during this evaluation.

I know that this might seem a little pedantic, and while I am totally known to be so this I hope that this isn't one of those cases. If I received insufficient javadoc in a case like this, I wouldn't have anything to rely upon except the code itself when figuring out what the code is. My understanding of your code relied on knowledge that I already had, and while preconceived notions in this case were super useful, there's a whole lot of cases in which they're not, and documentation gives readers an opportunity to dispel those notions.

On another subject relating to documentation, your view interface method `save()` promises to save the model in some fashion, but your implementation doesn't do that, which is a big no-no.

## Conclusion

There's also supposed to be a section called __Design/Code Limitations__, but I feel like I've already covered both in the three previous sections, so I'll end here. (If the graders need a pointer to justify this omission, check out the subsection __Encapsulation and Extensibility__.)

### The Good

You wrote some code whose interfaces didn't depend on each other, and that (critically) worked, efficiently and totally. Not only are both of these things true, but your interfaces were written well enough and had enough well-defined methods for it to be easy for us to have your view play with our model and controller.

### The Bad

Your model interface fails to encapsulate what should be implementation details, reducing extensibility by posing arbitrary limitations. Your view and controller have each other as fields in a circular relationship that could be avoided for greater clarity in design.

### The Ugly

Vagueness in javadoc makes otherwise legible code difficult to understand without prior knowledge. 

If you have any other questions, let us know! I totally understand if you don't care or just want to leave OOD behind and never think about it again, but if you'd like to talk more about your design with someone who was just elbow-deep in it, I'd be totally down.
