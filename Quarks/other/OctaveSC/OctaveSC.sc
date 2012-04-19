// OctaveSC class - implements FIFO pipe-based binary communication 
// between octave and SC3, allows to use SuperCollider editor as 
// interactive editor for octave.
// 03/2006 by Thomas Hermann, Neuroinformatics Group, Bielefeld University
//
// Free to use under GPL, please cite the author/website 
// when using this library in research projects/publications.
//
// Refer to help file for documentation.
//
// Bug fixes and improvements are very welcome, send them to 
// thermann@sonification.de or thermann@techfak.uni-bielefeld.de

OctaveSC {
	classvar <>octavePath = "/sw/bin/octave";
	classvar <>arch = "ieee-be";
	var <>pipeS2O, <>pipeO2S, <>pipeCmd;
	var <>fifoS2O, <>fifoO2S, <>fifoCmd;
	var <>octaveFlags = "-i"; 
	var <>envir;
	
// [ core functions ]
	initOctaveSC {
		fifoS2O = "/tmp/sc2oct"++this.hash++".fifo";
		fifoO2S = "/tmp/oct2sc"++this.hash++".fifo";
		fifoCmd = "/tmp/cmd2oct"++this.hash++".fifo";

		"OctaveSC: create fifos...".postln;

		("mkfifo" + fifoCmd).systemCmd;
		("mkfifo" + fifoS2O).systemCmd;
		("mkfifo" + fifoO2S).systemCmd;
	}

	*new {
		^super.new.initOctaveSC;
	}

	start {
		// launch octave so that it reads input from the command pipe

		// create startup shell script file
		var f;
		f = File("/tmp/octaveSC_start.sh","w");
		f.write("#!/bin/bash \n octave"+this.octaveFlags+"<"+fifoCmd+"\n");
		f.close;
		("chmod u+x /tmp/octaveSC_start.sh").systemCmd;

		// ("/sw/bin/octave /tmp/octaveSC_start.sh").unixCmd;
		// ... causes problems, so here an alternative:
		"please call /tmp/octaveSC_start.sh from any Terminal".postln;
	}
	
	startX {
		// launch octave via an xterm (needs running X11 on the system)
		"DISPLAY".setenv(":0.0");
		
		("/usr/X11R6/bin/xterm -geometry 60x15+0+0 -bg '#f0f080' -e \" " +
		octavePath + this.octaveFlags + "<" + fifoCmd + "\"").unixCmd;
	}

	initPipes {
		"OctaveSC: open Cmd pipe".postln;
		pipeCmd = Pipe("cat >>" + fifoCmd, "w");

		"OctaveSC: open S2O pipe".postln;
		pipeS2O = Pipe("cat >>" + fifoS2O, "w");

		"OctaveSC: open O2S pipe".postln;
		pipeO2S = Pipe("cat <"  + fifoO2S, "r");

		"OctaveSC: send octave functions".postln;	

		this.eval("
			global fdO2S; 
			fdO2S = fopen(\"" ++ fifoO2S ++ "\", \"w\");
			## function to send data to sc3
			function scOUT(v);
				global fdO2S;
				fwrite(fdO2S, \[rows(v), columns(v)\], \"int32\", 0, \""++arch++"\");				fwrite(fdO2S, v', \"double\", 0, \""++arch++"\");
				fflush(fdO2S);
			endfunction;
			
			global fdS2O; 
			fdS2O = fopen(\"" ++ fifoS2O ++ "\", \"r\")
			## function to receive data from sc3
			function result = scINP ();
				global fdS2O;
	  			nr = fread(fdS2O, 1, \"int32\", 0, \""++arch++"\"); 
	  			nc = fread(fdS2O, 1, \"int32\", 0, \""++arch++"\"); 
	  			result = zeros(nr, nc);
	  			for i=1:nr;
	  				for j=1:nc;
	  					result(i, j) = fread(fdS2O, 1, \"double\", 0, \""++arch++"\");
	  					## printf(\"%d %d %f\\n\", i, j, result(i, j));
	  				endfor;
	  			endfor;
	 		endfunction;
			");
	}
	
	init {
		"OctaveSC: init: using startX until problems with this.start are fixed".postln;
		// start octave, init pipes
		this.startX; // TODO: use non-X11 as default after fixing Term problem...
		this.initPipes;		
	}
	
	quit { // tell octave process to terminate
		this.value("exit");
	}

	closePipes {
		this.quit; // close octave first, needed since pipes remain blocked else
		"OctaveSC: close pipes";
		pipeO2S.close;
		pipeS2O.close;
		pipeCmd.close;
	}

	finish {
		this.quit; // stop octave process
		this.closePipes; // close pipes
		("rm" + fifoCmd).systemCmd;
		("rm" + fifoS2O).systemCmd;
		("rm" + fifoO2S).systemCmd;
	}


// [ interaction functions ]

	cmdShell { // additional command interface
	"DISPLAY".setenv(":0.0");
	("/usr/X11R6/bin/xterm -geometry 60x15+0+300   -bg '#f0e040' -e \"cat >> "
		+ fifoCmd + "\"").unixCmd;
	}

	client { arg doc; 
		var octfn; // function to set in document to process octave evaluation shortcut
		octfn = {arg doc, key, modifiers, num;
			if((num==13) && modifiers==262401) { // this is CTRL-return
				this.eval(doc.selectedString);
			};
		};
			
		if(doc.notNil){
			if(doc.keyDownAction.notNil){
				("OctaveSC-client: overwrite keyDownAction of"+doc.title++".").postln;
			};
			doc.keyDownAction_(octfn);
		}{ // else
			if(Document.globalKeyDownAction.notNil){
				"OctaveSC-client: overwriting Document-globalKeyDownAction".postln;
			};
			Document.postln;
			Document.globalKeyDownAction_(octfn);
		}; // fi
	}

// [ low level functions to interact with octave ]
	eval {|cmdStr| 
		pipeCmd.write(cmdStr+"\n"); pipeCmd.flush;
	}

	receive { // return array of latest data sent from octave via sendSC
		// syntax: int, int, float*
		// semanics:  (nr of rows, nr of columns, data values)
		// if (nr,dim)==(1,1), a scalar is stored
		// if (nr,dim)==(1,n), a vector is stored (array)
		// if nr>1, a matrix (array of arrays) is stored
		// nr<0 is reserved for other types, e.g. strings
		
		var nr, nc, entry, result, row;
		result = [];
		nr = pipeO2S.getInt32;
		case 
			{nr==1} {
				nc = pipeO2S.getInt32;
				if (nc==1) { 
					result = pipeO2S.getDouble 
					}{ // else vector
					result = (1..nc).collect { pipeO2S.getDouble };
					}
				}
			{nr>1} {				
				nc = pipeO2S.getInt32;
				result = Array.newClear(nr);
				nr.do { |i| result[i] = (1..nc).collect { pipeO2S.getDouble }; };
				}
			{nr<0} {
				postf("OctaveSC: receive: nr=% : not yet implemented", nr);
				};
		^result;
	}
	
	send { | mat | // send func call args to octave
		var nr, nc;

		if (mat.isKindOf(SimpleNumber)){
			pipeS2O.putInt32(1);
			pipeS2O.putInt32(1);
			pipeS2O.putDouble(mat);
			pipeS2O.flush;
			^0;
		};

		if(mat.isKindOf(Array)==false) {
			"OctaveSC: send: use only scalar values, arrays or matrices (2d-arrays)".postln
			^this;
		};

		// else it is vec or matrix;
		if(mat[0].isKindOf(SimpleNumber)){ // vec
			nc = mat.size;
			pipeS2O.putInt32(1);
			pipeS2O.putInt32(nc);
			mat.do { |el| pipeS2O.putDouble(el) };
			pipeS2O.flush;
			^1;
		};
		// else it is a matrix			
		nr = mat.size;
		nc = mat[0].size;
		pipeS2O.putInt32(nr);
		pipeS2O.putInt32(nc);
		mat.do { | item | item.do { |el| pipeS2O.putDouble(el) }};
		pipeS2O.flush;
	}

// [ high level controls for OctaveSC] 
	at { | key |
		this.eval("scOUT("+ key + ");");
		^this.receive;		 
	}
	
	put { | key, value |
		this.send(value);	
		this.eval(key.asString + "= scINP;"); 
	}	

	value {|expr="", inp=nil, out=nil|
		var key, val, result=[];

		if(inp != nil) { // then parse input array
			if(inp.size.odd) {"OctaveSC: value: inp: use key, val pairs".postln;^this};
			(inp.size / 2).do { |i|
				this[ inp[2*i] ] = inp[2*i+1];
			};
		};
		// execute expression on octave
		if (expr.isKindOf(Array), { 
			"OctaveSC: string arrays not yet implemented".postln;
		},{
			this.eval(expr);
		});	
		// check if envir shall be used
			
		if(out != nil) { // then parse output array
			result = [];
			(out.size).do { |i|
				result = result ++ [ this[ out[i] ] ];
			}
		};
		^result; 
	}
}
