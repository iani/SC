/* (IZ 2006-01-17) {
Create the OSCresponders and data structures needed for receiving data from Python. 

This version receives data as an array of numbers. The data sent from Python via OSC do not contain any tags with the name for each parameter 
} */

var params;			// list of param names for setting control spec parameters
var script, envir;		// fast access to this script and its environment
var vertexResponder;  	// responder for receiving vertex data
var scoreRules;		// list of rule functions added from conductor array of score script 

~controlSpecs = [
	[\index, 0, 10000, \linear, 0, 0],
	[\aa, -10, 10, \linear, 0, 0],
	[\ai, -10, 10, \linear, 0, 0],
	[\ax, -10, 10, \linear, 0, 0],
	[\ay, -10, 10, \linear, 0, 0],
	[\da, -10, 10, \linear, 0, 0],
	[\di, -10, 10, \linear, 0, 0],
	[\done, 0, 10000, \linear, 0, 0],
	[\laps, 0, 10000, \linear, 0, 0],
	[\left, 0, 10000, \linear, 0, 0],
	[\pr, -10, 10, \linear, 0, 0],
	[\pt, -10, 10, \linear, 0, 0],
	[\px, -10, 10, \linear, 0, 0],
	[\py, -10, 10, \linear, 0, 0],
	[\sx, -10, 10, \linear, 0, 0],
	[\sy, -10, 10, \linear, 0, 0],
	[\wait, 0, 100, \linear, 0, 0],
	[\unknown, -100, 100, \linear, 0, 0],
	[\unknown2, -100, 100, \linear, 0, 0]
];

params = ~controlSpecs.slice(nil, 0);

script = ~script;
envir = script.envir;
// store scoreRules in envir to be accessible by score script:
~scoreRules = scoreRules = List.new;

vertexResponder = OSCresponder(nil, \vertex, { |time, respArg, msg |
	var nameOfMsg; 	// name of OSC message received. Not used!
	var vertexID;	// index of the vertex received
	var wait;		// wait parameter in the message
	#nameOfMsg ... msg = msg;	// re-collect useful part of message in msg
	// store all parameters in envir, and update gui
	msg.do { | val, index | script.set(params[index], val); };
	// evaluate each rule with the environment:
	scoreRules.do { |rule| rule.(envir) };
});

~start = {
	vertexResponder.add;
	"Vertex data reception from Python is now ON".postln;
};

~stop = {
	vertexResponder.remove;
	"Vertex data reception from Python is now OFF".postln;
};

/*
0 id (serial number of vertex)
1 aa angle of accelleration vector
2 ai intensity of acceleration vector
3 ax accelleration in x
4 ay accelleration in y
5 da angle of speed vector
6 di intensity of speed vector
7 done % of total traced already / percents
8 laps time elapsed since origin of piece / seconds
9 left time to end of piece / seconds
10 pr angle of position vector of next vertex 
11 pt intensity position vector of next vertex
12 px x of next vertex
13 py y of next vertex
14 sx (speed on x axis)
15 sy (speed on y axis)
16 wait: time to trace this segment / seconds

*/