
/* iz Tuesday; January 19, 2010: 11:22 AM

Example of how to control loops of lighting sequences from input of sensor data received via OSC from Processing with the Firmata library connected to Arduino. 


// To test: 
LightLoops.test;
// this creates a window with 5 sliders simulating the input from the 5 sensors

// To stop the test, just clost the test window. 

// Then to send test messages from simulated sensors, first start LightLoops: 
LightLoops.start;

// Then simulate the messages sent by Processing. 
// For this, try any of the messages below, in any order, ager having started LightLoops: 
NetAddr.localAddr.sendMsg('/test', 0, 100, 300, 50, 0);
NetAddr.localAddr.sendMsg('/test', 0, 500, 300, 50, 0);
NetAddr.localAddr.sendMsg('/test', 0, 0, 0, 0, 40);
NetAddr.localAddr.sendMsg('/test', 0, 0, 0, 0, 10);
/// etc.

To stop everything: 

LightLoops.stop;

*/

LightLoops {
	classvar default;
	classvar testWindow;
	
	var loopFuncs;			// Array of functions that will be played as routines to create the loops
	var <sensorValues;		// Array of last received sensor values;
	var <largest, <indexOfLargest; 	// largest sensor value, index of largest sensor value 
	var <>threshold = 10;			// sensor value threshold above which to trigger another loop
	var loopRoutines;		// the routines that create lighting movement loops by sending data to arduino via processing
	var <oscResponder;		// the responder for receiving the data
	var <>processingAddress;	// the address at which processing is listening for messages to control lights
								// default is NetAddr("127.0.0.1", 10000). 
	
	*default { | loopFuncs |
		if (default.isNil) {
			default = this.new(loopFuncs);
		};
		^default;
	}
	*new { | loopFuncs |
		^this.newCopyArgs(loopFuncs).init;
	}

	init {
		sensorValues = { 0 } ! this.loopFuncs.size;
		oscResponder = OSCresponder(nil, '/test', { | time, responder, msg |
			this.receiveSensorValues(msg[1..]);
		});
		processingAddress = NetAddr("127.0.0.1", 10000);
	}
	
	receiveSensorValues { | valueArray |
		sensorValues = valueArray;
		// to get the lartest element in the array
		#largest, indexOfLargest = sensorValues.inject([sensorValues[0], 0], { | largest, current, index |
			if (largest[0] > current) { largest } { [current, index] }
		});
		indexOfLargest = sensorValues indexOf: largest;
		format("sensor values received: %, largest: %, index of largest: %", sensorValues, largest, indexOfLargest).postln;
		if (largest >= threshold and: { this.loopRoutines[indexOfLargest].isNil }) {
			this.startLoop(indexOfLargest);
		};
	}
	
	startLoop { | indexOfLargest |
		this.stopAllRoutines;
		loopRoutines[indexOfLargest] = this.loopFuncs[indexOfLargest].fork(AppClock);
	}
	
	loopRoutines {
		if (loopRoutines.isNil) { loopRoutines = Array.newClear(this.loopFuncs.size) };
		^loopRoutines;
	}

	loopFuncs {
		if (loopFuncs.isNil) { loopFuncs = this.defaultLoopFuncs };
		^loopFuncs;
	}

	defaultLoopFuncs {
		var colors;
		colors = [	// five colors for the five sensors
			[1000, 0, 0],	// red
			[0, 1000, 0],	// green
			[0, 0, 1000],	// blue
			[1000, 1000, 0],	// yellow
			[1000, 0, 1000],	// violet
		];
		^{ | loopIndex |
			{
				var dmxMessage;
				var colorParameters;
				inf do: { | count |
					format("this is loop % playing step number %", loopIndex + 1, count + 1).postln;
					// send to Processing a message to set some color at random intensity
					colorParameters = ['/dmxColor'] ++ colors[loopIndex] ++ [1000.rand];
					format("this is what I am sending to Processing: %", colorParameters).postln;
					processingAddress.sendMsg(*colorParameters);
					0.5.wait;
				}
			}
		} ! 5;
	}

	*start { this.default.start; }
	start { oscResponder.add; }
	*stop { this.default.stop; }
	stop {
		oscResponder.remove;
		this.stopAllRoutines;
		sensorValues = { 0 } ! this.loopFuncs.size;
	}

	stopAllRoutines {
		this.loopRoutines do: { | lr, i |
			if (lr.notNil) { lr.stop };
			loopRoutines[i] = nil;
		};
	}
	
	*test {
		this.testWindow.front;
		this.start;
	}
	
	*testWindow {
		if (testWindow.isNil) { testWindow = this.makeTestWindow };
		^testWindow;
	}
	
	*makeTestWindow {
		var w, sliders, addr;
		addr = NetAddr.localAddr;
		w = Window("test sensors", Rect(10, 10, 400, 25 * this.default.loopFuncs.size + 10 max: 100));
		w.onClose = {
			testWindow = nil;
			this.default.stop;
		};
		w.view.decorator = FlowLayout(w.view.bounds, 5@5, 5@5);
		sliders = this.default.loopFuncs collect: { | lf, index |
			EZSlider(w, 
				Rect(0, 0, w.view.decorator.innerBounds.width, 20),
				format("sensor %:", index + 1),
				ControlSpec(0, 255, \linear, 1, 0),
				{ | ez |
					this.default.sensorValues[index] = ez.value;
					addr.sendMsg('/test', *this.default.sensorValues.postln);
				}
			)
		};
		^w;
	}
}

