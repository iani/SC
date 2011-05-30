
/*
Kaos.load;
*/


Kaos {
	
	*load{
	
	var s;
	s = Server.default;
		
	SynthDef("abs",{|i, k, j, out, vol = 0.2, 
		mx1=0.2,  mx2=0.5,  my1=0.4, my2=1.3, 
		att = 1.1, sus = 2.0, rls = 3.9, gate = 1|
		
		var env, mul, decay, ses, comb, ses1, pulse;
		
		env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: 1), gate, doneAction: 2);
		
		pulse = Pulse.ar([
					300*mx1.exp(2),
					200*my1.exp(2),
					200*my2.exp(2),
					100*mx2.exp(2)
					],0.5);
		mul = -4*i absdif: FSinOsc.ar(1.6, 0.1*i, 0.5*i);
		comb = CombN.ar(
					Decay.ar(
						Decay.ar(pulse.sin, 0.1), 
						0.8.sin, 
						pulse/20), 
					mul, 
					mul, 
					0.1.sin/3
					).tanh/4;
		ses = comb.sin/4;
		ses1 = Pan2.ar(ses,Saw.ar(0.5,2,ses));
	
		Out.ar(out,DelayN.ar(ses1,0.4, [0.19,0.26]*comb)*vol*4);
	}).send(s);

	~mNois= OSCresponderNode(nil, '/harmP/kaos', { |t,r,m| 
		if (~mNoise.isNil) {
			~mNoise = Synth.head(~piges, \abs, [\out, ~mainBus] );
		}{
			~mNoise.release(5);
			~mNoise = nil;
		}
	}).add;

	~mx1Spec = ControlSpec(0.1, 3.0, \exp);
	~mx2Spec = ControlSpec(3.0, 0.1, \exp);
	
	~my1Spec = ControlSpec(0.1, 3.0, \exp);
	~my2Spec = ControlSpec(3.0, 0.1, \exp);
	
	~xyK = OSCresponderNode(nil, '/harmP/xyKaos', { |t,r,m| 
		var n1, n2;
		n1 = (m[1]);
		n2 = (m[2]);
		
		~mNoise.set(\mx1, ~mx1Spec.map(n1));
		~mNoise.set(\mx2, ~mx2Spec.map(n1));
	
		~mNoise.set(\my1, ~mx1Spec.map(n2));
		~mNoise.set(\my2, ~mx2Spec.map(n2));
		 
		}).add;
	
	
	~kaosVolSpec = ControlSpec(0.0, 1.1, \lin);
	~kaosVol = OSCresponderNode(nil, '/harmP/kaosvol', { |t,r,m| 
		var n1;
		n1 = (m[1]);
		~mNoise.set(\vol, ~kaosVolSpec.map(n1));
	}).add;
	
	}
}