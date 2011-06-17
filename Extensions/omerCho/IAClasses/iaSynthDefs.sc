/* Draft of simple class for synthdefs for a project 


Ia1SynthDefs.load;

*/

Ia1SynthDefs { 

	
	*load {

		var s;
		
		s = Server.default;	

//Oscilators 


		SynthDef(\kick, {	
			arg outBus=0, amp=0.8, freq = 38, decaytime = 0, mix = 0.25, room = 0.15, damp = 0.8;
			var env0, env1, env1m, out;
			
			env0 =  EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.6, 0.26], [-4, -2, -4]), doneAction:2);
			env1 = EnvGen.ar(Env.new([110, 59, 29], [0.005, 0.29], [-4, -5]));
			env1m = env1.midicps;
			
			out = LFPulse.ar(env1m, 0, 0.5, 1, -0.5);
			out = out + WhiteNoise.ar(1);
			out = LPF.ar(out, env1m*1.5, env0);
			out = out + SinOsc.ar(env1m, 0.5, env0);
			
			out = out * 1.2;
			out = out.clip2(1);
			out = FreeVerb.ar(out, // mono src
				mix, // mix 0-1
				room, // room 0-1
				damp // damp 0-1 duh
			);
			out = out * amp;
			
			Out.ar(outBus, out.dup);
		}).send(s);
		
		SynthDef(\playBuf, {| out = 0, bufnum = 0, rate = 1, amp = 1|
			Out.ar(out, 
				PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum)*rate, doneAction:2) * amp
			)
		}).send(s);
		
		SynthDef(\indust1, {|out = 0, amp = 0.8 attime = 0.01, sustain = 1.1, rlstime = 51.1, dist = 0.5 |
			var  env, mix, ses, outdc, rek, n=8;
			env =  EnvGen.ar(Env.perc(attime, rlstime), doneAction: 2, levelScale: 0.8, timeScale: sustain);
			rek = LFNoise0.kr(rrand(0.2,1.0), 1,1).squared * MouseX.kr;
			mix = Klank.ar(`[
					Array.fill(n, { exprand(1.0,20.0) }),
					nil,
					Array.fill(n, { 0.2.rand })
				],
				Blip.ar(rek, [rrand(2,5),rrand(2,5)], 0.1)
			).fold2(0.2).cubed * 12;
			mix = Mix.arFill(3, { CombL.ar(mix, 0.1, 0.03.linrand, 4.0.linrand) });
			ses = mix.distort * dist;
			8.do({ses = AllpassN.ar(ses, 0.05, [0.05.rand, 0.05.rand], 3) }) ;
			outdc = LeakDC.ar(ses);
			Out.ar(out, outdc *amp * env);
		}).send(s);
		
		SynthDef(\buf, { | out=0, bufnum = 0, gate = 1, rate = 1, startPos = 0, 
					amp = 1.0, att = 0.001, dec = 1, sust = 1, rls = 3, pan = 0, loop = 1|
			var audio;
			rate = rate * BufRateScale.kr(bufnum);
			startPos = startPos * BufFrames.kr(bufnum);
			
			audio = BufRd.ar(2, bufnum, Phasor.ar(1, rate, startPos, BufFrames.ir(bufnum)), 0, 2);
			audio = EnvGen.ar(Env.adsr(att, dec, sust, rls), gate, doneAction: 2) * audio;
			audio = Pan2.ar(audio, pan, amp);
			Out.ar(out, audio);
		}).send(s);
		
		
		SynthDef(\xorInt,{|out = 0, gate = 1,  vol = 0.8, amp = 0.5, 
			pan = 0.001, cos = 0.0001, lfbeat1 = 0.25, lfbeat2 = 0.25,
			lfn1a = 110, lfn1b = 324, lfn2a = 130.8,  lfn2b = 146,  
			rls = 1 |
			var ses, env;
			var in1, in2, comb1, combin1, comb2, combin2;
				
			in1 = Blip.ar([lfn1a, lfn1b], 2,  0.8);
			in2 = Blip.ar([lfn2a, lfn2b], 2,  1.9);
			
			comb1 = BPF.ar(	in1, 4**LFNoise0.kr(lfbeat1) * 680, 0.1,  0.4);
			comb2 = BPF.ar(	in2, 8**LFNoise0.kr(lfbeat2) * 680, 0.1,  0.4);
				
			ses = Rotate2.ar(comb1, comb2, LFSaw.kr(0.1));
			ses = {comb1+comb2};
			
			env = EnvGen.ar(Env.new([0, 1.0, 2, 0], [0.1, 0.5, rls], [1, -3, -1], 2 ), gate,  doneAction:2);
			ses = Pan2.ar(ses *SinOsc.ar( cos/pan.cos, 0, 0.4),FSinOsc.kr(pan), 0.5,ses);
			
			Out.ar(out, ses *vol/1.1 );
		}).send(s);
		
		SynthDef(\xorIntOsc,{|out = 0, gate = 1,  vol = 0.1, amp = 0.3, 
			pan = 2, cos = 10.01, lfbeat1 = 0.25, lfbeat2 = 0.25,
			lfn1a = 110, lfn1b = 324, lfn2a = 130.8,  lfn2b = 146,  
			rls = 1 |
			var ses, env;
			var in1, in2, comb1, combin1, comb2, combin2;
				
			in1 = Blip.ar([lfn1a, lfn1b], 2,  0.8);
			in2 = Blip.ar([lfn2a, lfn2b], 2,  1.9);
			
			comb1 = BPF.ar(	in1, 8**LFNoise0.kr(lfbeat1) * 680, 0.1,  0.3);
			comb2 = BPF.ar(	in2, 8**LFNoise0.kr(lfbeat2) * 680, 0.1,  0.3);
				
			ses = Rotate2.ar(comb1, comb2, LFSaw.kr(2));
			ses = {comb1+comb2};
			
			env = EnvGen.ar(Env.new([0, 1.0, 2, 0], [0.1, 0.5, rls], [1, -3, -1], 2 ), gate,  doneAction:2);
			ses = Pan2.ar(ses *SinOsc.ar( cos/pan.cos, 0, 0.4),FSinOsc.kr(pan), 0.5, ses);
			
			Out.ar(out, ses *vol/1.2 );
		}).send(s);
		
		
		
		SynthDef( \tascaleBass, { |out = 0, freq = 369.92, amp = 1, attime = 0.001, rls = 0.5, pan = 0, brown = 0.1, fSin = 0.5  |
			var in, osc, env, ses;
			env =  EnvGen.ar(Env.perc(attime, rls, 0.5, 2), doneAction: 2);
			in = FSinOsc.ar(freq, 0, brown);
			osc = SinOsc.ar(0, in, 1.8) * env;
			ses = Pan2.ar(osc, pan, FSinOsc.kr(fSin));
			Out.ar(out, ses *3 *amp);
		}).add;
		
		SynthDef( \tascale, { |out = 0, vol =1, freq = 369.92, amp = 1, attime = 0.001, rls = 0.05, pan = 0, brown = 0.1, fsin = 1.5  |
			var in, osc, env, ses;
			env =  EnvGen.ar(Env.perc(attime, rls, 0.5, 2), doneAction: 2);
			in = FSinOsc.ar(freq, 0, brown);
			osc = SinOsc.ar(0, in, 1.8) * env;
			ses = Pan2.ar(osc, pan, FSinOsc.kr(fsin));
			Out.ar(out, ses *vol *amp);
		}).send(s);



		SynthDef( \lypat01, { |out = 0, vol = 1, amp = 1, sustain = 1.1, freq = 860, 
			sin1 = 0, sin2 = 0.1, brown = 0.1, saw = 400, 
			attime = 0.01, rls = 0.5, pan = 0 |
			var in, osc, env, ses;
			env =  EnvGen.ar(Env.perc(attime, rls), doneAction: 2, levelScale: 0.4, timeScale: sustain);
			in = SinOsc.ar(FSinOsc.ar(freq, 0, brown), 0.4);
			ses = SinOsc.ar(0, in, 0.01) ;
			ses = RLPF.ar(ses, freq, 1.4, 1.6, 0.4 );
			ses = ses.sin/8 + SinOsc.ar(freq, Decay.ar(SinOsc.ar(sin1, sin2), 8.2.abs*8, Saw.ar(saw)));
			ses = Pan2.ar(ses, pan, amp);
			
			Out.ar(out, ses *vol *env );
		}).send(s);
	
		
//Effects SynyhDefs
		SynthDef("limiter",{ arg out=0, in = 0, lvl = 0.9, durt = 0.01;
			ReplaceOut.ar( out, Limiter.ar( In.ar(in, 2), lvl, durt) )
		}).send(s);
		
		
		SynthDef("reverb", { | out, in = 0, amp=0.05, roomsize = 10, revtime = 1, damping = 0.2, inputbw = 0.19, spread = 15,
				drylevel = -3, earlylevel = -9, taillevel = -11 |
			var input;
			input = In.ar(in, 2);
			Out.ar(out, GVerb.ar(
				input,
				roomsize,
				revtime,
				damping,
				inputbw,
				spread,
				drylevel.dbamp,
				earlylevel.dbamp,
				taillevel.dbamp,
				roomsize, amp) + input
			)
		}).send(s);
			
		SynthDef("delay", { |out = 0, in = 0, maxdelay = 0.25,  delay = 1.0, decay = 0.05, pan = 0, amp =0.5|
			var ses, filt;
			ses =  In.ar(in, 2);
			filt = CombN.ar(
					ses,
					maxdelay,
					delay,
					decay, 
					amp
				);
			Out.ar(out,  Pan2.ar(filt, pan));
		}).send(s);
		
		SynthDef("rlpf",{ |out = 0, amp = 0.8 in = 0, ffreq = 600, rq = 0.1, pan = 0|
			Out.ar( out, Pan2.ar(RLPF.ar( In.ar(in), ffreq, rq, amp), pan))
		}).send(s);
		
		
		SynthDef("wah", { arg out = 0, in = 0, rate = 0.5, amp = 1, pan = 0, cfreq = 1400, mfreq = 1200, rq=0.1, dist = 0.15;
			var zin, zout;
			zin = In.ar(in, 2);
			cfreq = Lag3.kr(cfreq, 0.1);
			mfreq = Lag3.kr(mfreq, 0.1);
			rq   = Ramp.kr(rq, 0.1);
			zout = RLPF.ar(zin, LFNoise1.kr(rate, mfreq, cfreq), rq, amp).distort * dist;
			Out.ar( out , Pan2.ar(zout, pan) ); 
		}).send(s);

}
}