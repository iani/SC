/*

FlowMic.load;

*/

FlowMic {
	classvar <action;
	*load {
	var s;
	s = Server.default;
	
	SynthDef("flowerMic",{|out, vol = 0.0, dist = 0.0, does = 6, med = 1|
		var in, amp, freq, hasFreq, snd;
		var mx, my;
		mx = MouseX.kr(1,118);
		my = MouseY.kr(0,3);
		in = Mix.new(SoundIn.ar(6));
		amp = Amplitude.kr(in, 0.05, 0.05);
		# freq, hasFreq = Pitch.kr(
							in,
							initFreq: ~c4, 
							minFreq: ~c1,
							maxFreq: 4000.0,
							execFreq: 100.0,
							maxBinsPerOctave: 16,
							median: med,
							ampThreshold: 0.02, 
							peakThreshold: 0.5,
							downSample: 1
						);
		snd = CombC.ar(LPF.ar(in, 1000), 0.1, (2 * freq).reciprocal, -6).distort * dist*my;
		does.do({
		snd = AllpassN.ar(snd, 0.040, [0.040.rand,0.040.rand], 2)
		});
		Out.ar(out, snd * vol);
	}).send(s);
		
		
///////flowerMic

		~togFlowerMic=ÊOSCresponderNode(nil,Ê'/bufP/togMicFlow', {Ê|t,r,m|Ê
			if (~flowMic.isNil) {
				~flowMic = Synth.head(~piges,"flowerMic", 
					[
					\out, [~revBus, ~dlyBus, ~wahBus, ~rlpBus]
					]
				);
			}{
				~flowMic.free;
				~flowMic = nil;
			}
		}).add;
		
		
		
		~medianFlowMicSpec = ControlSpec(0.0, 0.5, \lin);
		~doesFlowMicSpec = ControlSpec(1, 8, \lin);

		~distortFlowMicSpec = ControlSpec(0.0, 1.0, \lin);
		~distFlowMic =ÊOSCresponderNode(nil,Ê'/bufP/distAmpMic', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~flowMic.set(\vol, ~distortFlowSpec.map(n1));
		
		}).add;		


/*(		~distortFlowMicSpec = ControlSpec(0, 1, \lin);
		~distFlowMic =ÊOSCresponderNode(nil,Ê'/bufP/distAmpMic', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~flowMic.set(\dist, ~distortFlowSpec.map(n1));
		
		}).add;)*/
		
		~medFlowMic =ÊOSCresponderNode(nil,Ê'/bufP/medianMic', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~flowMic.set(\dist, ~distortFlowSpec.map(n1));
		
		}).add;

		~doesFlowMic =ÊOSCresponderNode(nil,Ê'/bufP/doesMic', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~flowMic.set(\does, ~doesFlowSpec.map(n1));
		
		}).add;

	
	
	
	}
	
	*unLoad{
	
	
	}
	
}