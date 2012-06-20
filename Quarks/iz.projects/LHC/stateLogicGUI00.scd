OSCresponder.removeAddr(nil);


(
SynthDef(\bit, { arg out = 0, key= 124, id= 0;
   var bit;
   bit = SendTrig.kr(Impulse.kr(2), id, KeyState.kr(key, lag:0));
   Out.kr(0, bit);
   }).add;
)
// execute each one separately
x = Synth(\bit, [\key, 123, \id, 0]);///   124.ascii -> right arrow;
y = Synth(\bit, [\key, 125, \id, 1]);  /// 125.ascii -> down arrow;
z = Synth(\bit, [\key, 124, \id, 2]);///   123.ascii -> left arrow;
///
(
var counter = 0;
var	state0 = 0;
var	state1 = 0;
var	state2 = 0;	
var win = Window("front", Rect(100, 200, 300, 400));
var alphanum = NumberBox(win, Rect(120, 40, 40, 40));
var buttons = {|i|
        Button(win, Rect(40*i+90, 350, 30, 20)).states_([
                   ["OFF", Color.green, Color.black],
                   ["ON", Color.red, Color.white],
               ])
               .action_({ arg butt;
                ("button:"++i++" "++butt.value).postln;  
               });
}.dup(3);
var numbers = {|i|
        NumberBox(win, Rect(40*i+90, 370, 30, 20));
}.dup(3);
///
var osc = OSCresponder(s.addr, '/tr', {|t, r, m|
       [t, r, m].postln;
       {
//
        buttons[m[2]].value = m[3]; //m[2] will be the sendtrig id and m[3] 
        numbers[m[2]].value = m[3];
//
			if(m[2]==0, { if(m[3]==1, { ~bit0 = 1; }, {  ~bit0 = 0; }); });
			if(m[2]==1, { if(m[3]==1, { ~bit1 = 2; }, {  ~bit1 = 0; }); });
			if(m[2]==2, { if(m[3]==1, { ~bit2 = 4; }, {  ~bit2 = 0; }); });
			// the counter machine is actually the function mod8 
			counter = (counter + ~bit0 + ~bit1 + ~bit2) % 8;
			"counter = ".post; counter.postln;
       }.defer;
	// one-to-one mapping, each non-zero number to a note 
	{SinOsc.ar(80*counter,mul:0.2)*EnvGen.ar(Env.perc, doneAction:2)}.play;
	
}).add;
///
win.onClose = {osc.remove;};
win.front;
) 
thisProcess.recompile
thisProcess.shutdown 