/*

NeyDef.load;

*/

NeyDef {
	classvar <action;
	*load {
	
	
	~neyDefOsc=ÊOSCresponderNode(nil,Ê'/bufP/neyDef', {Ê|t,r,m|Ê
		if (~neyDef.isNil) {
			~neyDef = Ndef(\verb, {
				var input, output, delrd, sig, deltimes;
				input = Pan4.ar(SoundIn.ar(6,1), FSinOsc.kr(0.3, -0.25, 0.25), FSinOsc.kr(0.4, -0.1, 0.1) ); // buffer playback, panned 
				
				// Read 4-channel delayed signals back from the feedback loop
				delrd = LocalIn.ar(4); 
				
				// This will be our eventual output, which will also be recirculated
				output = input + delrd[[0,1]];
				
				// Cross-fertilise the four delay lines with each other:
				sig = [output[0]+output[1], output[0]-output[1], delrd[2]+delrd[3], delrd[2]]; 
				sig = [sig[0]+sig[2], sig[1]+sig[3], sig[0]-sig[2], sig[1]-sig[3]]; 
				// Attenutate the delayed signals so they decay: 
				sig = sig * [0.4, 0.37, 0.333, 0.3];
				
				// Here we give delay times in milliseconds, convert to seconds, 
				// then compensate with ControlDur for the one-block delay 
				// which is always introduced when using the LocalIn/Out fdbk loop 
				deltimes = [201, 243, 265, 377] * 0.001 - ControlDur.ir;
				
				// Apply the delays and send the signals into the feedback loop
				LocalOut.ar(DelayC.ar(sig, deltimes, deltimes)); 
				// Now let's hear it:
				Out.ar(0, output);
		}).play;
		}{
			~neyDef.free;
			~neyDef = nil;
		}
	}).add;
		

	
	
	}
	
	*unLoad{
	
	~neyDefOsc.free;
	
	}
	
}