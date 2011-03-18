/* 

** NodeArray: Array of Synths or other nodes. Each element outputs to a consecutive audio bus channel
** CtlArray: Control one named parameter for each synth in an array of synths by either mapping or setting. 
*** SetCtl: Subclass of CtlArray that sets the parameters values: aSynth.set(pmt, value)
*** MapCtl: Subclass of CtlArray that maps the parameters values: aSynth.map(pmt, bus)
**** MultiMapCtl: Allow multiple processes writing to the same bus. 

Current limitation: The size of nodeArray is limited to 128 for MapCtl instances 
that intend to use the free method. 


Synth("bphasor", [\bufnum, O@\weddella]);
a = Synth.newPaused("bphasor", [\bufnum, O@\weddella]);
b = Bus.control;
b.set(1);
a.map(\out, b.index);
a run: 1

SynthDescLib.global.browse

a = NodeArray({ | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, 1]); })
a.set(\vol, 0.1);


NodeArray({ | i | Synth(\blfn3, [\out, i.postln, \bufnum, O@\weddellb, \rate, 0.01, \vol, 0.01]); })


Synth(\blfn3, [\bufnum, O@\weddellb])

*/

NodeArray {
	classvar <>defaultSize = 43;
	var <generatorFunc, <size;
  	var <nodes;
  	
  	*new { | generatorFunc, size |
	  	^this.newCopyArgs(generatorFunc, size ? defaultSize).init;
	}

	init { nodes = generatorFunc ! size; }
	
	do { | func |
		nodes do: func;	
	}
	
	set { | param, val, index |
		index = index ?? { (0.. size - 1) };
		nodes[index].asArray do: _.set(param, val);
	}

	setF { | paramval, index |
		index = index ?? { (0.. size - 1) };
		index = index.asArray;
		nodes[index] do: { | n, i | n.set(*paramval.(index[i], i)) };
	}
	
	fade1 { | id, value = 1, lag = 1, param = \vol |
		nodes[id].set((param ++ \lag).asSymbol, lag, param, value);
	}

	fadeF { | ids, valueFunc, param = \vol |
		var value, lag;
		ids.asArray do: { | id | 
			#value, lag = valueFunc.(id);
			this.fade1(id, value, lag, param);
		}
	}

	fadeAll { | value = 1, lag = 1, param = \vol |
		nodes do: this.fade1(value, lag, param); 
	}
	
	fadeRate1 { | id = 0, value = 1, lag = 3 |
		this.fade1(id, value, lag, \rate);
	}

	fadeRateF { | id = 0, value = 1, lag = 3 |
		this.fade1(id, value, lag, \rate);
	}
	
	setRate1 { | id = 0, value = 1 |
		this.fadeRate1(value, 0);
	}
	
	fadeIn { | value = 1, time = 3 |
		nodes do: { | n | n.set(\vollag, time, \vol, value); }
	}

	fadeOut { | time = 3 |
		nodes do: { | n | n.set(\vollag, time, \vol, 0); }
	}
	
	fadeOutAndEnd { | time |
		this.fadeAll(0, time, \vol);
		{ nodes do: _.free }.defer(time + 0.1); 		
	}

	map { | param, index |
		nodes do: { | n, i | n.map(param, index + i) }	
	}
	
	moveToTail { | group |
		nodes do: _.moveToTail(group);	
	}
}

CtlArray {
	 var <nodeArray;	// array of nodes that are being controlled
	 var <ctlProcess; 	// process doing the control, can be synth(s) or routine
	 var <param = \vol;	// name of parameter being controlled (symbol)
	 *new { | nodes, ctlproc, param = \vol |
	      ^this.newCopyArgs(nodes, ctlproc, param).init;
	 }

	 init {}

//	 start {} // ???

//	 stop {}  //  free {} // ?????
}

SetCtl : CtlArray {

}

MapCtl : CtlArray {
	classvar <emptyBus;	// bus for processes to write to when they don't want to control anyone
	var <bus;	  // multichannel bus holding one channel per node in the nodeArray
       
	init {
		var index;
		if (emptyBus.isNil) { emptyBus = Bus.control(Server.default, 128); };
		bus = Bus.control(Server.default, nodeArray.size);
		index = bus.index;
		ctlProcess.set(\out, index);
		
//		nodeArray do: { | n + | n.map(param, index + i) };
	}
	
	start {
//		nodeArray do: _.run(1);	
	}

	free {
		ctlProcess.set(\out, emptyBus);
		bus.free;		
	}
	
	stop {
		this.free;
		ctlProcess.free;	
	}
}

MultiMapCtl : MapCtl {
	
}