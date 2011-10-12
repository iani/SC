SYSTabParam {
	
	classvar <default;
	var <envir, <drawBDefs, <drawADefs;
	var <drawNode; //subLevels are created by mlSel
	
	*initClass { default = (); StartUp.add({this.makeDefaults}) }
	
	*new{|envir, drawBDefs, drawADefs| 
		^super.newCopyArgs( envir ?? {()}, drawBDefs, drawADefs).init
	}
	
	copy {|sysSymb| ^this.class.new(envir.copy, drawBDefs, drawADefs) }

	storeArgs {
		var saveE = ();
		this.saveBackParams(envir, saveE); //filter old saved leftovers, if any && \self avoid crash! 
		^[saveE, drawBDefs, drawADefs] 
	}
	saveBackParams{|srcE, destE| 
		destE = destE ? envir;  
		this.class.default.keys.do{|key| srcE[key] !? { destE.put(key, srcE[key]) } }
	}
	
	init {
		drawBDefs = drawBDefs ?? { List[
			\SYSTab_init, \SYSTab_ServicePsMethods, 
			\SYSTab_RefLinesBefore, \SYSTab_RefSysLinesBefore, \SYSTab_play
			,\scaledTest, \movedTest, \drawTest, \freeTest
		]};
		drawADefs = drawADefs ?? { List[			
			\SYSTab_RefLinesAfter, \SYSTab_RefSysLinesAfter, 
			\SYSTab_MouseLegend, \SYSTab_KeysLegend, \SYSTab_last
		]};
		
		envir.put(\self, this);
	}
	checkDefaults {|sym|
		Systema.at(sym) !? { this.makeParamsFromSystema(Systema.at(sym)) };
//this.logln("envir[\midiRef]" + envir[\midiRef]);
		this.class.default.keys.do{|key| envir[key] 
			?? { envir.put(key, this.class.default[key]) } };
//this.logln("envir[\midiRef]" + envir[\midiRef]);
	}		
	makeParamsFromSystema {|sys|
		envir.midiRef ?? { envir.midiRef = sys.midiRoot }
	}
	buildDrawGraph {|path, node, mlSel, sysTab|
		var drawServer = node.tree.server;
		drawNode = node;
		envir.putAll((sysTab: sysTab, drawServer: drawServer, 
			e: drawServer.drawEnvir, pE: drawServer.drawEnvir.parent));
		drawBDefs.do{|def| DrawFunc(def, envir, node) };
		mlSel.sourceAtPath(path).do{|obj| 
			obj.buildDrawGraph(path++obj, this.getNode(node), mlSel, sysTab)};
		drawADefs.do{|def| DrawFunc(def, envir, node) };
	}
	getNode {|node| ^node.class.new(node) }
	
	doesNotUnderstand{|selector, value|
		var isWrite = false;
		var keys = envir.keys;
		var key = if (selector.asString.last != $_) { selector 
			}{ isWrite = true; selector.asString.drop(-1).asSymbol };
		if (keys.includes(key)) {
			if (isWrite) { envir[key] = value; ^this }{ ^envir[key] }
		}{ DoesNotUnderstandError(this, selector, value).throw }
	}
	*makeDefaults {
		default.putAll((
			// x offset
			sysOffX: 20, frameRate: 1
			// header
			,yHroom: 4, labelHGab: 8,labelHFont: Font("Helvetica", 12), labelHColor: Color.white
			// footer
			,yFroom: 4, labelFGab: 8, footMode: 1, labelFFont: Font("ProFont", 9)
			//,labelFFont: Font("ArialNarrow", 10)
			,footModeColors: [ Color.black,
				Color.green, Color.green, Color.yellow, Color.yellow, //root, delta
				Color.new255(255, 101, 0), Color.new255(255, 101, 0), //SYS root
				Color.magenta, Color.magenta, //offset
				Color.blue, Color.blue, Color.cyan, Color.cyan] //tonal center, tonal delta
			// states
			,scale: [1.0, 1.0], move: [0.0, 0.5] //write back only if wanted? -> no, change is easy!
			,anchorMode: 0, octaveRanges: [2.2, 1.1, 0.6, 3.5, 4.0], scaleMode: 0
			,keysOn: false, mouseOn: false, outline: false, selOn: true, tcOn: true
			,selColor: Color.new255(74, 194, 255, 180), selFColor: Color.new255(252, 209, 188, 100)
			// reference lines
			,midiRef: 60, refScaleY: 1.0
			,refMode: 0, quantModes: [2, 1, 0.5, 0.25], quantMode: 1, refHideOn: false, gridOn: true
			,refXOff: 0, refXGab: 4, refYGab: 1, refFont: Font("ProFont", 9)
			,ref0Color: Color.new255(153, 255, 131), refBackColor: Color.grey(0.25)
			,quantColors: [Color.magenta(1), Color.magenta(0.5), Color.yellow(0.5), Color.grey]
			// refSys lines
			,refSysMode: 0, refSysHideOn: false, gridSysOn: true, refSysSym: \nil //nil key not there!
			,refSys0Color: Color.new255(255, 146, 64, 200), refSysBackColor: Color.grey(0.23, 0.5)
			
			//testing:
	/*
				"12222222222228".bounds(Font(n, 8))
				"12222222222228".bounds(Font(n, 8.5))
				"12222222222228".bounds(Font(n, 9))
			
				SCFont.availableFonts.do{|name| name.postln}
				n = "ArialNarrow"
				n = "AmericanTypewriter-Condensed"
				n = "Monaco"
				n = "ProFont"
				n = "ProFontX"
				
				(
		var w = Window("smoothing", Rect(100, 200, 500, 300)).front;
		w.view.background_(Color.black);
		w.drawHook = { |v|
			Pen.strokeColor = Color.grey(0.25);
			Pen.smoothing_(false);		//no anti-aliasing
			50.do{|i|
				Pen.moveTo(50@50.rrand(250));
				Pen.lineTo(250@50.rrand(250));
			};
			Pen.stroke;
	"12222222222228".drawCenteredIn(Rect(10, 10, 200, 20), Font("Monaco", 8), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 20, 200, 20), Font("Monaco", 8.5), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 30, 200, 20), Font("Monaco", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 40, 200, 20), Font("ProFont", 8), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 50, 200, 20), Font("ProFont", 8.5), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 60, 200, 20), Font("ProFont", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 70, 200, 20), Font("ProFontX", 8), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 80, 200, 20), Font("ProFontX", 8.5), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(10, 90, 200, 20), Font("ProFontX", 9), Color.yellow);


//	"12222000022228".drawCenteredIn(Rect(10, 40, 200, 20), Font("ArialNarrow", 10), Color.yellow);
//	"12222222222228".drawCenteredIn(Rect(10, 50, 200, 20), Font("AmericanTypewriter-Condensed", 10), Color.yellow);
//	"12222222222228".drawCenteredIn(Rect(10, 60, 200, 20), Font("Crystal", 10), Color.yellow);
//	"12222222222228".drawCenteredIn(Rect(10, 70, 200, 20), Font("ExcaliburMonospace", 9), Color.yellow);
//	"12222222222228".drawCenteredIn(Rect(10, 80, 200, 20), Font("Monospace821BT-Roman", 9), Color.yellow);
//	"12222222222220".drawCenteredIn(Rect(10, 90, 200, 20), Font("RailModel", 9), Color.yellow);
			Pen.smoothing_(true);		//anti-aliasing (default)
			Pen.strokeColor = Color.grey(0.25);
			50.do{|i|
				Pen.moveTo(250@50.rrand(250));
				Pen.lineTo(450@50.rrand(250));
			};
			Pen.stroke;
	"12222222222228".drawCenteredIn(Rect(210, 10, 200, 20), Font("Monaco", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 20, 200, 20), Font("ProFont", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 30, 200, 20), Font("ProFontX", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 40, 200, 20), Font("ArialNarrow", 10), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 50, 200, 20), Font("AmericanTypewriter-Condensed", 10), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 60, 200, 20), Font("Crystal", 10), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 70, 200, 20), Font("ExcaliburMonospace", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 80, 200, 20), Font("Monospace821BT-Roman", 9), Color.yellow);
	"12222222222228".drawCenteredIn(Rect(210, 90, 200, 20), Font("RailModel", 9), Color.yellow);
		};
		)
	*/		
		));
	}
}
/*
a = SYSTabParam.new
a.envir

a.checkDefaults
a.checkDefaults(SYS.archytasStoichos.name)

a.legend
a.hello

*/
