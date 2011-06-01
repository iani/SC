/*
//------Disk1
(
		~avosL = Buffer.cueSoundFile(s, "sounds/_Evfer/avostosL.aif", 0, 1);
		
		
		SynthDef("diskMono", { |out=0, bufnum=0, loop=1, pan=0|
			var din;
			
			din = DiskIn.ar(1, bufnum, loop);
			din = Pan2.ar(din, pan);
			
			Out.ar(out, din);
		}).send(s);
		)
		(
		~disk1 = Synth.head(~piges, "diskMono", 
					[
					 \out, ~mainBus, 
					 \bufnum, ~avosL
					]
				);
		)

(
RastMakam.load;
Globals.tempo;
Globals.scales;
Globals.groups;
Globals.buses;

)
(
NcMainVol.load;
ChClean.load;
ChReverb.load;
ChDelay.load;
ChRlpf.load;
ChWah.load;
ChFlow.load;

Gendai.load;
AutSynth.load;
Baxx.load;
Kaos.load;
Ses1.load;
Ses2.load;
Avos.load;

)


(
NcMainVol.play;
ChClean.play;
ChReverb.play;
ChDelay.play;
ChRlpf.play;
ChWah.play;
)
*/


Avos {
	*loadBuf{
		var s;
		s= Server.default;
		
		~avosL = Buffer.cueSoundFile(s, "sounds/_Evfer/avostosL.aif", 0, 1);
	
	}
	
	*load{
		var s;
		s= Server.default;
		
		SynthDef("diskMono", { |out=0, bufnum=0, loop=1, pan=0, vol = 0.1,
			att = 1.1, sus = 2.0, rls = 3.9, gate = 1 |
		
			var in, osc, env, ses;
		
			var din;

			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: 1), gate, doneAction: 2);
			
			din = DiskIn.ar(1, bufnum, loop);
			din = Pan2.ar(din, pan);
			
			Out.ar(out, din*env*vol);
		}).send(s);	
	
	
		~aVolSpec = ControlSpec(0.0, 1.0, \lin);
		~aVol = OSCresponderNode(nil, '/outs/avosvol', { |t,r,m| 
			var n1;
			n1 = (m[1]);
			
			~avos.set(\vol, ~aVolSpec.map(n1));
		
		}).add;
	
		~av1= OSCresponderNode(nil, '/outs/avos', { |t,r,m| 
			if (m[1] == 1) {
			Avos.loadBuf;
			Avos.play;
			}{
			Avos.unload;
			}
		}).add;
	}
	*play{
		
		~avos = Synth.head(~piges, "diskMono", 
			[
			 \out, ~mainBus, 
			 \bufnum, ~avosL
			]
		);	
	}	
	*unload{
		~avos.release(5);
		~avosL.free;
	}
}


