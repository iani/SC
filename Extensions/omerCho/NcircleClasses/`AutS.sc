

/*
AutSynth.load;
*/


AutSynth {
	
	*load{
	
	var s;
	s = Server.default;

	SynthDef( \autSynth, { | out = 0, vol = 0.5, amp = 0.8, pan = 0,
		freq = 90, freqlp = 99.254, 
		sinA = 400, sinB = 110.1, brown = 0.01, saw = 4, 
		att = 1.1, sus = 2.0, rls = 3.9, gate = 1 |
		
		var in, lagFlp, env, ses;
		
		lagFlp = Lag2.kr(freqlp, 0.4);
		
		
		env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: 1), gate, doneAction: 2);
		
		in = SinOsc.ar(FSinOsc.ar(freq, brown/2**sinA/sinB, brown *2 ), 1.1);
		
		ses = SinOsc.ar(0, in, 0.1) ;
		
		ses = RLPF.ar(ses, lagFlp+brown/4, 10, 160.6, 1.4 );
		
		ses = ses.sin/2 + SinOsc.ar(lagFlp, Decay.ar(SinOsc.ar(sinA, sinB), brown.tanh, Saw.ar(saw.sin/2)));
		
		ses = Pan2.ar( ses, pan, LFDNoise3.kr(0.01, 0, 1));
		
		ses = ses *env;
		
		Out.ar(out, ses *vol);
	}).send(s);

	~autSTog= OSCresponderNode(nil, '/harmP/autS', { |t,r,m| 
		if (~autS.isNil) {
			~autS = Synth.head(~piges, \autSynth,
				[
				
				\freq, 90.0 rrand: 92.0,
				\sinA, 0.1 rrand: 12.0,
				\sinB, 1.1 rrand: 112.0,
				\out, ~mainBus
				]
			);
		}{
			~autS.release(2);
			~autS = nil;
		}
	}).add;


	~autSvolSpec = ControlSpec(0.0, 2.1, \lin);
	~autSvolume = OSCresponderNode(nil, '/harmP/autSvol', { |t,r,m| 
		var n1;
		n1 = (m[1]);
		~autS.set(\vol, ~autSvolSpec.map(n1));
	}).add;


	~autSsinASpec = ControlSpec(0.001, 112.1, \lin);
	~autSsinBSpec = ControlSpec(0.001, 112.1, \lin);
	
	~autSxy = OSCresponderNode(nil, '/harmP/autSxy', { |t,r,m| 
		var n1, n2;
		n1 = (m[1]);
		n2 = (m[2]);
		
		~autS.set(\sinA, ~autSsinASpec.map(n1));
		~autS.set(\sinB, ~autSsinBSpec.map(n2));
		 
	}).add;


	~autSsawSpec = ControlSpec(0.001, 12.1, \lin);
	~autSsaw = OSCresponderNode(nil, '/harmP/autSsaw', { |t,r,m| 
		var n1;
		n1 = (m[1]);
		~autS.set(\saw, ~autSsawSpec.map(n1));
	}).add;

	~autSbrownSpec = ControlSpec(0.001, 112.1, \lin);
	~autSbrown = OSCresponderNode(nil, '/harmP/autSbrown', { |t,r,m| 
		var n1;
		n1 = (m[1]);
		~autS.set(\brown, ~autSbrownSpec.map(n1));
	}).add;


	~autSmain = OSCresponderNode(nil, '/harmP/autSmain', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastB1 rrand: ~rastB1+1,
			\sinA, 2.0,
			\sinB, 0.001
		);
	
	}).add;

	~autS1 = OSCresponderNode(nil, '/harmP/autS1', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastB3 rrand: ~rastB3+1,
			\sinA, 2.9,
			\sinB, 0.008
		);
	
	}).add;

	~autS2 = OSCresponderNode(nil, '/harmP/autS2', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastB5 rrand: ~rastB5+1,
			\sinA, 2.0,
			\sinB, 0.001
		);
	
	}).add;
	
	~autS3 = OSCresponderNode(nil, '/harmP/autS3', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastB7 rrand: ~rastB7+1,
			\sinA, 2.0,
			\sinB, 0.001
		);
	
	}).add;
	
	~autS4 = OSCresponderNode(nil, '/harmP/autS4', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastC1 rrand: ~rastC1+1,
			\sinA, 2.0,
			\sinB, 0.001
		);
	
	}).add;

	~autS5 = OSCresponderNode(nil, '/harmP/autS5', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastC4 rrand: ~rastC4+1,
			\sinA, 2.0,
			\sinB, 0.001
		);
	
	}).add;
	
	~autS6 = OSCresponderNode(nil, '/harmP/autS6', { |t,r,m| 

		~autS.set(
			\freqlp, ~rastC5 rrand: ~rastC5+1,
			\sinA, 2.0,
			\sinB, 0.001
		);
	
	}).add;
	}

}
