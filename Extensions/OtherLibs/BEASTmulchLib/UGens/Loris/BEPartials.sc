BEPartials {
	var <partialList;

	*new { arg sdif;	
		^super.new.init(sdif);
	}
	
	init { arg sdif;	
		partialList = sdif.readFramesToPartials.collect({|item|
			BEPartial.newFrom(item);
		});		
	}
	
	copy {
		^super.copy.partialList_(partialList.copy)
	}
	
	partialList_{|list|
		partialList = list;
	}
	
	at {|index| ^partialList[index] }
	
	size { ^partialList.size; }
	
	dur {
		var dur = 0;	
		partialList.do({|item| 
			var end;
			end = item[1].sum + item[0]; // duration
			dur = dur.max(end);
		});
		^dur;
	}
	
	// fades in or out partials with non-zero start and/or end amps
	fadeInOut {
		var fadein = 0.001, fadeout = 0.001; // loris standard
		var extraPhase;
		partialList = partialList.collect({ arg partial;
			// fadein
			if(partial.amps.first > 0,{
				partial.startTime = partial.startTime - fadein; // roll back starttime slightly
				// roll back phase
				extraPhase = partial.phases.first - (2pi * partial.freqs.first * fadein);
				partial.phases = partial.phases.insert(0, extraPhase.mod(2pi));
				partial.times = partial.times.insert(0, fadein); // short fadein time segment
				partial.amps = partial.amps.insert(0, 0); // amp zero
				partial.freqs = partial.freqs.insert(0, partial.freqs.first); // extra freq
				partial.bandwidths = partial.bandwidths.insert(0, partial.bandwidths.first); // extra bw
			});
			
			// fadeout
			if(partial.amps.last > 0,{
				// extra phase
				extraPhase = partial.phases.last + (2pi * partial.freqs.last * fadeout);
				partial.phases = partial.phases.add(extraPhase.mod(2pi));
				partial.times = partial.times.add(fadeout); // short fadeout segment
				partial.amps = partial.amps.add(0); // amp zero
				partial.freqs = partial.freqs.add(partial.freqs.last); // extra freq
				partial.bandwidths = partial.bandwidths.add(partial.bandwidths.last); // extra bw
			});
			partial
		});
	
	}
	
	ar {| stretch = 1, pitch = 1, bw = 1|
		var envs, recipStretch, oldStretch;
		this.fadeInOut; // fade in and out non-zero partial starts and ends
		
		partialList.do({ arg partial, i;
			var starttime, times, amps, phases, numSegs, theseEnvs, phaseEnv, thisStretch;
			starttime = partial.startTime;
			// correct times for fadeins by compensating for stretch
			numSegs = partial.times.size;
			times = Array.new(numSegs);
			amps = partial.amps;
			phases = Array.new(numSegs + 1);
			
			thisStretch = stretch.value;
			// if stretch is a shared UGen no sense in creating multiple divide UGens
			if(thisStretch != oldStretch, {
				recipStretch = stretch.reciprocal;
			});
			oldStretch = thisStretch;
			amps.do({|amp, j|
				if(j < numSegs, {
					if(amp == 0, {
						// null amps are phase reset points
						phases = phases.add(partial.phases[j]);
						// keep fadein times constant under stretch so that onset phase
						// is correct once start amp is reached
						times = times.add(partial.times[j] * recipStretch)
					}, {
						phases = phases.add(-inf); // otherwise ignore instantaneous phase
						times = times.add(partial.times[j]);
					});
				});
			});
			phases = phases.add(inf); // this partial is done
			
			// freq, bw, amp
			theseEnvs = [Env(partial.freqs, times), Env(partial.bandwidths, times), 
				Env(amps, times)];
			
			theseEnvs = theseEnvs
				.collect({|env, j|
					var levelScale = 1;
					if(j == 0, {levelScale = pitch.value});
					if(j == 1, {levelScale = bw.value}); 
					
					if(starttime > 0, {env = env.delay(starttime)});
				
					EnvGen.ar(env, levelScale: levelScale, 
						timeScale: thisStretch); 
			});
			
			// now add phasegen
			
			if(starttime > 0, {
				// initial -inf ensures reset on first partial
				phaseEnv = Env([-inf] ++ phases, [starttime] ++ times);
			}, {
				phaseEnv = Env(phases, times);
			});
			
			// freq, phase, bw, amp as in BEOsc
			theseEnvs = theseEnvs.insert(1, LorisPhaseGen.ar(phaseEnv, timeScale: stretch));
			
			envs = envs.addAll(theseEnvs);
		});

		^envs.unlace(4);
	}
	
}

// just an Array with some convenience methods
BEPartial[slot] : Array {
	
	*new { ^super.new(6) }
	
	*newFrom {|array|
		if(array.size != 6, {"Wrong size data for a BEPartial".error; ^ nil});
		^super.newFrom(array);
	}
	
	startTime { ^this[0] }
	
	times { ^this[1] }
	
	freqs { ^this[2] }
	
	phases { ^this[3] }
	
	bandwidths { ^this[4] }
	
	amps { ^this[5] }
	
	startTime_ {|new| this[0] = new }
	
	times_ {|new| this[1] = new }
	
	freqs_ {|new| this[2] = new }
	
	phases_ {|new| this[3] = new }
	
	bandwidths_ {|new| this[4] = new }
	
	amps_ {|new| this[5] = new }
	
	dur { ^this.times.sum + this.startTime }
	
	numBreakPoints { ^this.freqs.size }
	
}