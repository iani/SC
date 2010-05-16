
var window;
var script;
var algorithm;
var current_vector;
var wait_interval;

var load_button;
var graphics_view;

script = ~script;


window = Window("Sand", Rect(10, 10, 500, 550).fromRight.fromTop).front;
window onClose: { window = nil };
window.view.decorator = FlowLayout(window.view.bounds);
load_button = Button(window, Rect(0, 0, 200, 20));
load_button.states = [["load drawing algorithm"]];
load_button.action = {
	Dialog.getPaths({ | paths |
		algorithm = paths.first.loadPaths.first;
		if (algorithm.isKindOf(SequenceableCollection)) {
			algorithm = Pseq(algorithm, 1).asStream;
		};
	});
};

graphics_view = UserView(window, window.view.decorator.innerBounds);
graphics_view.background = Color.white;

~script.routine = {
	while {
		current_vector = algorithm.next).notNil
	}{
		0.1.wait;
		
	}
};



~script onClose: { 
	if(window.notNil)	{ window.close; }
};