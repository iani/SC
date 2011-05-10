/*
This file is a sample example file with collected examples from the documentaion. 

It is here for testing the attachment of files org-mode style in sclog.org

Examples that contain wait messages should be executed using the keyboard shortcut provided by class code: 

Command-Shift-x (for examples involving Windows/GUI elements)
or 
Command-Shift-alt-x (optionally, for examples with no windows only)

Type these anywere in an example between two comment lines starting with //:

*/

//: ======= Examples with symbol.play and function.play

//:Setting the duration and fadeout time of a function
{ GrayNoise.ar(LFNoise2.kr([2, 3]).range(0.01, 0.1), 0, 0.2) }.play.dur(5, 4);

//:rsyncs. 
//"Boiling"

{ | freq = 400 | Resonz.ar(BrownNoise.ar(0.2), freq * [1, 2]) }.play.dur(5, 5)
	.rsyncs({ | s |
		loop { s.set(\freq, 100.rrand(5000)); exprand(0.01, 0.1).wait; }
});


// =========== Some stuff you can do with Buffer.play ==========

//:d Buffer.play;

Buffer.play; 	// plays the current buffer - or the default

//:Play with custom arguments

10 do: { Buffer.play(args: [\rate, 0.75 + 1.0.rand]); };

//:Play with custom arguments, with a delay of 0.1 between each iteration

10 do: { Buffer.play(args: [\rate, 0.75 + 1.0.rand]); 0.1.wait };

//:Play with custom function

Buffer.play({ | bufnum | BufRd.ar(1, bufnum, LFNoise1.ar(1) * BufFrames.ir(bufnum)) });

// ========= ServerPrep =========

//:Executes whenever the server boots or Cmd-. is entered: 
ServerPrep.new.addToServerTree(\test, { "SERVER TREE".postln });

//:Start a new sound each time Cmd-. is pressed, or the Server is booted.  
\test.addToServerTree({ { [LFNoise0, LFNoise2].choose.ar(500 rrand: 5000, 0.1) }.play });
 
//:Remove the above action from ServerPrep

\test.removeFromServerTree;

// ========= Window examples ========

//:a
UniqueWindow(\test).onClose({ "closed".postln; });
//:b
\test2.window.onClose({ ({ 10.rand } ! 10).postln });

//:w simple unique window

UniqueWindow(\simple) onClose: { | me | postf("The window named '%' was closed\n", me.name) };

//:y does not reopen: 

{
	8 do: { 
		UniqueWindow(\repeatsimple).onClose({ | me | postf("The window named '%' was closed\n", me.name) })
			.window.view.background  = Color.rand;
		"It's still the same window".postln;
		0.5.wait;
	};
	3.wait;
	UniqueWindow(\repeatsimple).window.close;
}.fork(AppClock);

//:c

r = {
	5 do: { | i |
		UniqueWindow(\otherWindow, {
			var w;
			w = Window.new("MyWindow", Rect(*Array.rand(4, 250, 400)));
			w.view.background = Color.rand(0.5, 1);
			StaticText(w, Rect(0, 0, 300, 50))
			.string = format("if you close me I will reappear - up to 5 times.\ncounting: % times", i + 1);
			w;
		}).front.onClose(
		{
			postf("I closed for the %th time\n", i + 1); 
			r.next;
		});
		nil.yield;
	};
	"THE END".postln;
}.fork(AppClock)


