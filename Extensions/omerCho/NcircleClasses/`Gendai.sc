


Gendai {
	*load{
		var s;
		s = Server.default;
		
		
		SynthDef(\gendy02,{|i, k, out, amp = 0.5, gate = 1,
			mfreq = 440, 
			attime = 0.2, decaytime = 2, suslev = 4, rls = 1|
			var ses, env, mix, rlpf1, rlpf2, norm, lag;
			
			env = EnvGen.kr(Env.cutoff(1), gate, doneAction:2);
			mix = RLPF.ar(
				Gendy1.ar(
					2,
					3,
					minfreq: mfreq, 
					maxfreq: mfreq+10,
					durscale:0.0, 
					initCPs:40
				), 
				~a2,
				MouseX.kr(0.0,3.0), 
				0.2
			);
			norm = Normalizer.ar(mix, 0.5);
			lag = Lag.kr(LFNoise2.kr(0.1,1.9),0.5);
			ses = Pan2.ar(norm, lag);
		
			//ses = ses.sin;
			ses = ses*env;
			Out.ar(out, ses* amp);
		}).send(s);






		~gen1=ÊOSCresponderNode(nil,Ê'/outs/gendai1', {Ê|t,r,m|Ê
			if (m[1] == 1) {
			~gend1 = Synth.head(~piges,"gendy02", 
				[
				\out, ~mainBus, 
				\amp, 0.5,
				\mfreq, ~rastA1
				]
			);
			}{
				~gend1.release(8);
			}
		}).add;
		
		~gen2=ÊOSCresponderNode(nil,Ê'/outs/gendai2', {Ê|t,r,m|Ê
			if (m[1] == 1) {
			~gend2 = Synth.head(~piges,"gendy02", 
				[
				\out, ~mainBus, 
				\amp, 0.5,
				\mfreq, ~rastA2
				]
			);
			}{
				~gend2.release(5);
			}
		}).add;
		
		~gen3=ÊOSCresponderNode(nil,Ê'/outs/gendai3', {Ê|t,r,m|Ê
			if (m[1] == 1) {
			~gend3 = Synth.head(~piges,"gendy02", 
				[
				\out, ~mainBus, 
				\amp, 0.5,
				\mfreq, ~rastA3
				]
			);
			}{
				~gend3.release(5);
			}
		}).add;
		
		
		~gen4=ÊOSCresponderNode(nil,Ê'/outs/gendai4', {Ê|t,r,m|Ê
			if (m[1] == 1) {
			~gend4 = Synth.head(~piges,"gendy02", 
				[
				\out, ~mainBus, 
				\amp, 0.5,
				\mfreq, ~rastA4
				]
			);
			}{
				~gend4.release(5);
			}
		}).add;
		~gen5=ÊOSCresponderNode(nil,Ê'/outs/gendai5', {Ê|t,r,m|Ê
			if (m[1] == 1) {
			~gend5 = Synth.head(~piges,"gendy02", 
				[
				\out, ~mainBus, 
				\amp, 0.5,
				\mfreq, ~rastA5
				]
			);
			}{
				~gend5.release(5);
			}
		}).add;
		~gen6=ÊOSCresponderNode(nil,Ê'/outs/gendai6', {Ê|t,r,m|Ê
			if (m[1] == 1) {
			~gend6 = Synth.head(~piges,"gendy02", 
				[
				\out, ~mainBus, 
				\amp, 0.5,
				\mfreq, ~rastA7
				]
			);
			}{
				~gend6.release(5);
			}
		}).add;

	}
}

