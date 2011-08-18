+ Systema {
	/*@ 
	issues:Yes, there are many eventTypes to play any alternative pitch models<br><i> Event.partialEvents.playerEvent.eventTypes.keys.asList.sort </i><br> but we want to stay compatible with the default eventType 'note' <br><i> Event.partialEvents.playerEvent.eventTypes[\note] </i><br> and its corresponding pitchEvent<br><i> Event.partialEvents.pitchEvent</i> 
	@*/
	
	*overwriteDefaultEvent { // called by *initClass
		var extension;
		Class.initClassTree(Event);
		("\n
		**********************************************************************\n
		***** class Systema is overwriting Event's default pitch model  ******\n
		**********************************************************************\n
		").postln;
		
		extension = (
		
		//	logOn: false,					
			noMatchWarn: false,
			noMatchBeep: true,
	codes: #[\agmIns, \agmVoc],		//convenience for now
	instrument: \AGMI,				//convenience for now
			
			note: #{							// set root and alerts dynamically via scale
	// not working: this : if(~scale.respondsTo(\eventExtras)) { this.putAll(~scale.eventExtras) };
				if(~scale.respondsTo(\item2Degree)) { 
					~degree = try { ~scale.item2Degree(~degree, ~codes) + ~mtranspose } {nil}
				}{
					~degree = ~degree + ~mtranspose
				};
//this.logln("degree:" + ~degree);
				~degree.degreeToKey( ~scale, ~scale.respondsTo(\stepsPerOctave).if(
						{ ~scale.stepsPerOctave }, ~stepsPerOctave ) )
				?? { 			
//this.logln("degree is Nil:" + ~degree);
					if(~scale.respondsTo(\noMatchWarn)) { ~noMatchWarn = ~scale.noMatchWarn };
					if(~scale.respondsTo(\noMatchBeep)) { ~noMatchBeep = ~scale.noMatchBeep };
					if (if (~scale.respondsTo(\isInAmbit) && ~degree.notNil) 
							{~scale.isInAmbit(~degree + ~mtranspose)} {true}) {
						if (~noMatchWarn) {("\tdegree:" + d + "returned nil"
							+ try { "of" + ~scale.asString } { "" } ).warnn};
						if (~noMatchBeep) {Env.perc(0.05, 0.01, 0.4, 4).test}
					}{
						if (~noMatchWarn) {("\tdegree:" + d + "exceeds ambit"
							+ try { "of" + ~scale.asString } { "" } ).warnn};
						if (~noMatchBeep) {Env.perc(0.001, 0.2, 0.4, -8).test}
					};
					~amp = 0.0; //returns 0.0
				}
			},
			
			midinote: #{	// get the ~octave right for decending scales
//this.logln("midinote:" + ~note.value);
			if ((if (~scale.respondsTo(\octaveRatio)) { ~scale.octaveRatio } { ~octaveRatio}) < 1) 
				{
					(((~note.value + ~gtranspose + ~root) /
						~scale.respondsTo(\stepsPerOctave).if(
							{ ~scale.stepsPerOctave },
							~stepsPerOctave) + 5.0 -~octave) * 
						(12.0 * ~scale.respondsTo(\octaveRatio).if
						({ ~scale.octaveRatio }, ~octaveRatio).log2) 
							+ ~scale.respondsTo(\midiRoot).if
							({ ~scale.midiRoot }, 60.0) );
				}{
					(((~note.value + ~gtranspose + ~root) /
						~scale.respondsTo(\stepsPerOctave).if(
							{ ~scale.stepsPerOctave },
							~stepsPerOctave) + ~octave - 5.0) * 
						(12.0 * ~scale.respondsTo(\octaveRatio).if
						({ ~scale.octaveRatio }, ~octaveRatio).log2)
							+ ~scale.respondsTo(\midiRoot).if
							({ ~scale.midiRoot }, 60.0) );
				}
			}
		);

		Event.partialEvents[\pitchEvent].putAll(extension); //redundant, yes
		Event.parentEvents.default.putAll(extension); //since written only once at *initClass
		Event.reinstalldefaultParentEvent; //intricate and tricky, but otherwise unwritable
	}
}

+ Event {	
	*reinstalldefaultParentEvent { 
		// class method to set the otherwise unaccesable classvar defaultParentEvent
		defaultParentEvent = parentEvents.default;
	}

/*
checks: (if .logOn does not return nil, Systema_pitchEvent is installed)
Event.partialEvents.pitchEvent.logOn
Event.defaultParentEvent //has no getter but is copied into new Instance e.g. by method *default:
Event.default.parent.logOn
e = Event.default
e.parent.logOn

Event.default.parent.keys.asList.sort.do{|k| k.postln}
Event.partialEvents.pitchEvent.keys.asList.sort.do{|k| k.postln}
Event.parentEvents.default.keys.asList.sort.do{|k| k.postln}

().play


Pbind(\scale, [nil]).trace(\freq).play(protoEvent: Event.default.copy.put(\noMatchWarn, true))
Pbind(\scale, [0, nil], \degree, Pseq([0,1], inf),  \amp, 0.1).play //also works with amp!

Pbind(\scale, [0, 1, 2], \scaleIsCyclic, true, \degree, Pseries(-3, 1, 9) ).trace(\freq).play
Pbind(\scale, [0, 1, 2], \scaleIsCyclic, false, \degree, Pseries(-3, 1, 9) ).trace(\freq).play

Pbind(\scale, [0,nil,2], \scaleIsCyclic, true, \degree, Pseries(-3, 1, 9) ).trace(\freq).play
Pbind(\scale, [0,nil,2], \scaleIsCyclic, false, \degree, Pseries(-3, 1, 9) ).trace(\freq).play(protoEvent: Event.default.copy.putAll((noMatchWarn: true, noMatchBeep: false)))



*/
}

