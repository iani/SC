/* (IZ 2006-01-17) { 
Create the OSCresponders and data structures needed for receiving data from Python. 
} */

var vertexResponder; // responder 4 receiving vertex data
vertexResponder = OSCresponder(nil, \vertex, { |time, respArg, msg |
	var nameOfMsg; 	// name of OSC message received. Not used!
	var newVertex;	// the vertex event made from the data received
	var vertexID;	// index of the vertex received
	var wait;		// wait parameter in the message
	#nameOfMsg ... msg = msg;	// re-collect useful part of message in msg
	// make new vertex
	newVertex = Event.make { | me |
		msg.pairsDo { | key, val| me[key] = val; }
	};

	newVertex.postln;
	// update vertex data from msg, play vertex:
	// add new coords;
	/*
	coords = coords.add((newVertex['px'] + 1 / 2)@(newVertex['py'] + 1 / 2));
	{ sandView.value_([coords.x, coords.y]);
	}.defer;
	// create and add new vertex
	vertices = vertices.add(newVertex);
	// play sounds and update graphics
	this.playVertex(newVertex['id']);
	*/
//	["these are the coords !!!!!!!!!!!!!!", coords].postln;
});



~start = {
	vertexResponder.add;
	"Vertex data reception from Python is now ON".postln;
};

~stop = {
	vertexResponder.remove;
	"Vertex data reception from Python is now OFF".postln;
};

