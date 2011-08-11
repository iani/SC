LetterPhrase {
	var <>letter, <>x_coor, <>y_coor, <>duration, <>rate, <>playDur = 0.5, <>freq = 400, <>amp = 0.3, <>pan=0.0;
	var time = 0, messageName = "/hello", node;
	var onDur, offDur, letterChooseAction;
	var letterArray, digitArray, charArray;

	*new { |letter, x_coor, y_coor, duration, rate, playDur = 0.5, freq = 400, amp = 0.3, pan = 0.0|
		^super.newCopyArgs(letter, x_coor, y_coor, duration, rate, playDur, freq, amp, pan).init.play;
	}

	init {
		onDur = playDur / rate; 
		offDur = 1/rate  - onDur;

		letterArray = (65..90);
		digitArray = (48..57);
		charArray = letterArray++digitArray;

		letterChooseAction = this.letters;
	}

	letters {
		^switch (letter,
			$_, { { charArray.choose.asAscii } },
			$#,  {  {  digitArray.choose.asAscii  }  },
			$%, { {letterArray.choose.asAscii } },
			{ { letter } }
		);
	}

	play {  
		^{
			while({time<duration},{
					Processing.sendMsg(messageName, letterChooseAction.value.asString,x_coor, y_coor);
					node=Synth(\character,[\freq,freq,\amp,amp,\gate,1,\pan,pan]);
					onDur.wait;
					Processing.sendMsg(messageName,"",x_coor, y_coor);
					node.set(\gate,0);
					offDur.wait;
					time = 1/rate + time;	//look this point again. Needs a condition on the denominator
					node.free
			});
		}.fork;
	}
}

WordPhrase { 
	var <>word, <>orientation, <>firstLetterX_coor, <>firstLetterY_coor, <>delayTimePercentages, 
		<>phraseDur, <>rates, <>onDurs, <>freqs, <>amps, <>pans;

	var delayTimesAction, ratesAction, onDurAction, freqAction, ampAction, panAction;

	*new {| word, orientation, firstLetterX_coor, firstLetterY_coor, delayTimePercentages, 
		phraseDur, rates, onDurs, freqs, amps, pans  |

		^super.newCopyArgs(word, orientation, firstLetterX_coor, firstLetterY_coor, delayTimePercentages, 
			phraseDur, rates, onDurs, freqs, amps, pans).init.play;
	}

	init {
		delayTimesAction = this.selectAnAction(delayTimePercentages);
		ratesAction = this.selectAnAction(rates);
		onDurAction = this.selectAnAction(onDurs);
		freqAction = this.selectAnAction(freqs);
		ampAction = this.selectAnAction(amps);
		panAction = this.selectAnAction(pans);
	}

	selectAnAction { arg instanceVariable;
		
		^switch( instanceVariable.class,
			Array, { instanceVariable.at(_) },
			Function, { instanceVariable },
			{ {instanceVariable} }
		)
	}

	play {
		word do: { | char, count |
			{
			(delayTimesAction.(count)*phraseDur).wait;
			
			LetterPhrase(
				char, 
				firstLetterX_coor + if( orientation == \vertical, {0}, {count} ), 
				firstLetterY_coor + if( orientation == \vertical, {count}, {0} ), 
				phraseDur - ( delayTimesAction.(count)*phraseDur ), 
				ratesAction.(count), 
				onDurAction.(count), 
				freqAction.(count), 
				ampAction.(count), 
				panAction.(count)
			); 
			}.fork;
		}
	}
}

LetterPhraseWithFunctions {
	var <>letter, <>x_coor, <>y_coor, <>duration, <>rate, <>playDur, <>grainDur;
	var <>soundBuffer, <>bufPosition, <>grainEnvBuf, <>out, <>amp, <>pan;

	var time = 0, messageName = "/hello", node, i = 0;
	var onDur, offDur, letterChooseAction;
	var letterArray, digitArray, charArray;
	var x, y;

	var playDurAction, grainDurAction, soundBufferAction, bufPositionAction;
	var grainEnvAction, ampAction, panAction, rateAction;

	*new {| letter, x_coor, y_coor, duration, rate, playDur, grainDur, 
		soundBuffer, bufPosition, grainEnvBuf, out, amp, pan  |

		^super.newCopyArgs(letter, x_coor, y_coor, duration, rate, playDur, grainDur, 
			soundBuffer, bufPosition, grainEnvBuf, out, amp, pan).init.play;
	}

	init {
		playDurAction = this.selectAnAction(playDur);
		grainDurAction = this.selectAnAction(grainDur);
		soundBufferAction = this.selectAnAction(soundBuffer);
		bufPositionAction = this.selectAnAction(bufPosition);
		grainEnvAction = this.selectAnAction(grainEnvBuf);
		panAction = this.selectAnAction(pan);
		ampAction = this.selectAnAction(amp);
		rateAction = this.selectAnAction(rate);

		letterArray = (65..90);
		digitArray = (48..57);
		charArray = letterArray++digitArray;

		letterChooseAction = this.letters;
	}

	selectAnAction { arg instanceVariable;
		
		^switch( instanceVariable.class,
			Array, { instanceVariable.at(_) },
			Function, { instanceVariable },
			{ {instanceVariable} }
		)
	}

	letters {
		^switch (letter,
			$_, { { charArray.choose.asAscii } },
			$#,  {  {  digitArray.choose.asAscii  }  },
			$%, { {letterArray.choose.asAscii } },
			{ { letter } }
		);
	}

	play {  
		^{
			node = Synth(\grainGrid,
				 [ \out, out, \buffer, soundBuffer, \triggerRate, rateAction.(i), \grainDur, grainDurAction.(i),
					\playbackRate, 1, \bufPosition, bufPositionAction.(i), \amp, ampAction.(i), \pan, panAction.(i),
					\envbuf, grainEnvAction.(i) ]
			);
			
			while({time<duration},{
					x = playDurAction.(i) / rateAction.(i);
					y = 1 / rateAction.(i);

					Processing.sendMsg(messageName, letterChooseAction.value.asString,x_coor, y_coor);
					
					x.wait;
					Processing.sendMsg(messageName,"",x_coor, y_coor);
					( y - x ).wait;
					time = y + time;	//look this point again. Needs a condition on the denominator
					i = i + 1;
					node.setn(\triggerRate, rateAction.(i), \grainDur, grainDurAction.(i),\bufPosition, bufPositionAction.(i), 
						\amp, ampAction.(i), \pan, panAction.(i));
			});
			node.free;
		}.fork;
	}
}

WordPhraseWithFunctions {
	var <>word, <>orientation, <>firstLetterX_coor, <>firstLetterY_coor, <>phraseDur;
	var <>delayTimePercentages, <>rates, <>playDurs, <>grainDurs, <>soundBuffers;
	var <>bufPositions, <>grainEnvBufs, <>amps, <>pans, <>out;

	var delayTimesAction, ratesAction, onDurAction, ampAction, panAction, playDurAction;
	var grainDurAction, soundBufferAction, bufPositionAction, grainEnvAction;

	var delayPercentage;

	*new { | word, orientation, firstLetterX_coor, firstLetterY_coor, phraseDur, 
		delayTimePercentages, rates, playDurs, grainDurs, soundBuffers, 
		bufPositions, grainEnvBufs, amps, pans, out = 0 |

		^super.newCopyArgs(word, orientation, firstLetterX_coor, firstLetterY_coor, phraseDur,
			delayTimePercentages, rates, playDurs, grainDurs, soundBuffers,
			bufPositions, grainEnvBufs, amps, pans, out
		).init.play;
	}

	init {
		delayTimesAction = this.selectAnAction(delayTimePercentages);
		grainDurAction = this.selectAnAction(grainDurs);
		soundBufferAction = this.selectAnAction(soundBuffers);
		bufPositionAction = this.selectAnAction(bufPositions);
		grainEnvAction = this.selectAnAction(grainEnvBufs);
		ratesAction = this.selectAnAction(rates);
		playDurAction = this.selectAnAction(playDurs);
		ampAction = this.selectAnAction(amps);
		panAction = this.selectAnAction(pans);
	}

	selectAnAction { arg instanceVariable;
		
		^switch( instanceVariable.class,
			Array, { instanceVariable.at(_) },
			Function, { instanceVariable },
			{ {instanceVariable} }
		)
	}

	play {
		word do: { | char, count |

			delayPercentage = delayTimesAction.(count);

			{
				(delayPercentage*phraseDur).wait;

				LetterPhraseWithFunctions(
					char,
					firstLetterX_coor + if( orientation == \vertical, {0}, {count} ), 
					firstLetterY_coor + if( orientation == \vertical, {count}, {0} ),
					phraseDur - ( delayPercentage*phraseDur ),
					ratesAction.(count),
					playDurAction.(count),
					grainDurAction.(count),
					soundBufferAction.(count),
					bufPositionAction.(count),
					grainEnvAction.(count),
					out,
					ampAction.(count),
					panAction.(count)
				);
			}.fork
		}
	}
}                  