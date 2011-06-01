
/*
NcMainVol.load;


 PanAz.ar(
					4, 						// numChans
					ses, 					// in
					SinOsc.kr(MouseX.kr(4.0,0.001, 'linear'), -0.1,0.1), // pos
					0.5,						// level
					3.0						// width
				);

*/

NcMainVol { 

	
	*load {

		var s;
		
		s = Server.default;


		SynthDef("mainVolCtrl", { |out1, out2,  out3, out4, out5, 
			in = 0, pan = 0, amp = 0.0|
			var ses;
			ses =  In.ar(in, 2);
			ses =  PanAz.ar(
					5, 						// numChans
					ses, 					// in
					SinOsc.kr(MouseX.kr(4.0,0.001, 'linear'), -0.1,0.1), // pos
					0.5,						// level
					3.0						// width
				);
			Out.ar([out1, out2, out3, out4, out5], ses*amp);
		}).send(s);


/*

		SynthDef("mainVolCtrl", { |out1, out2,  out3, out4, out5, 
			in = 0, pan = 0, amp = 0.0|
			var ses;
			ses =  In.ar(in, 2);
			Out.ar([out1, out2, out3, out4, out5],  Pan2.ar(ses, pan, amp));
		}).send(s);

*/
		~mainVolF = OSCresponderNode(nil, '/outs/pigesVol', { |t,r,m| 
			var n1;
			n1 = (m[1]) ;
			~main.set(\amp, n1);
		}).add;



	}
	*play {
		
		~main = Synth.tail(~piges, "mainVolCtrl",
			[ 
			\in ,~mainBus, 
			[\out1, \out2, \out3, \out4, \out5],  [~limBus, ~revBus, ~dlyBus, ~rlpBus, ~wahBus]
			]
		);
		
		}

}