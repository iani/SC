SYSTab { // has named instances
	classvar <all, uniqueSuffix="_0", <>selectName; //used by SYSTabGui
	classvar <archiveDir, <archiveFolder="SYSTab_Archive", fileEnding="systab";
	classvar <copiedSYSDir, tabCopySYSFolder="_CopiedSystemata", tabCopySYSFileMark="_tabCp_";
	classvar <copiedSYSDict;
	
	var <name, <tabP, <mlSel;  
	// var <validSYSs; //nice, but who needs this finally?
	var <drawNode, <debugNode, <prevDrawServer, <savedParentDrawEnvir, <drawServerPEkeys;
	
	*initClass {
		all = IdentityDictionary.new;
		archiveDir = this.class.filenameSymbol.asString.dirname +/+ archiveFolder;
		copiedSYSDir = archiveDir +/+ tabCopySYSFolder;
		StartUp.add({ this.updTabCopySYSNames })
	}
	*updTabCopySYSNames {
		copiedSYSDict = IdentityDictionary.new;
		PathName(copiedSYSDir).files.collect{|pN| pN.fileNameWithoutExtension}.select{|name| 
			name.contains(tabCopySYSFileMark)}.do{|fn| var key, num, split = fn.split($_);
				num = split.pop.asInt; key = split.drop(-1).join.asSymbol;
//this.logln("key, num" + [key, num]);
				if (copiedSYSDict[key].notNil) { copiedSYSDict[key].add(num) 
				}{ copiedSYSDict.put(key, SortedList[num]) } }
	}
	getTabCopyName {|sysSym|
		var num;
		if (sysSym.asString.contains(tabCopySYSFileMark)) { 
			sysSym = sysSym.asString.split($_).drop(-2).join.asSymbol };
		if (copiedSYSDict[sysSym].isNil) { copiedSYSDict[sysSym] = SortedList[1]; num = 1
			}{ num = copiedSYSDict[sysSym].last + 1; copiedSYSDict[sysSym].add(num) };
		^(sysSym.asString ++ tabCopySYSFileMark ++ num).asSymbol	}
	
	*at { |name| ^all[name] }
	*remove {|name| 
		var instance = all.removeAt(name);
		instance !? { Systema.removeDependant(instance) };
		^instance
	}
	remove { this.class.remove(name) }
	
	*new{|name, sysSymbolsOrMlSel, tabP|
		name ?? { all[name] !? { ^all[name] } };
		^super.newCopyArgs(this.uniqueCopyNameSuffix(name ? \tableau).asSymbol,
			tabP ?? { SYSTabParam.new }).init(sysSymbolsOrMlSel)
	}
	*uniqueCopyName{|sym| //this.logln("sym:" + sym);
		if (all.keys.includes(sym)) { ^this.uniqueCopyName((sym.asString++$_).asSymbol) }{ ^sym }
	}
	*uniqueCopyNameSuffix{|sym|
		var str, i;
		if (all.keys.includes(sym).not) { ^sym }{
			str = sym.asString;
			i = str.findBackwards(uniqueSuffix);
				if (i.notNil) { 
					i=i+uniqueSuffix.size; 
					^this.uniqueCopyNameSuffix((str.keep(i) ++ (str.drop(i).asInt+1)).asSymbol)
				}{ 	^this.uniqueCopyNameSuffix((str ++ uniqueSuffix ++ 1).asSymbol) }
		}
	}
	storeArgs { ^[name, mlSel, tabP] }
	save { 
		this.sysSymbols.select{|sym| Systema.at(sym).notNil }.do{|sym| Systema.at(sym).save };
		this.prWriteFile(archiveDir, fileEnding, this.asCompileString) 
	}
	prWriteFile{|dir, ext, str|
		var res, fileName, file;
		fileName = dir +/+ name ++ "." ++ ext;
		file = File(fileName.postln, "w"); 
		if (file.isOpen) { 
			res = file.write(str);
			file.close;
		};
		^res
	}
	copy {|newName|
		prevDrawServer !? { this.saveBackParams(prevDrawServer.drawEnvir) };
		^this.class.new(newName, mlSel.deepCopy, tabP.copy)
	}
	deepCopy {|newName| ^this.copy(newName).copySYSs }
	
	rename{|newName|
		var obj;
		newName = this.class.uniqueCopyNameSuffix(newName.asSymbol);
		obj = all.removeAt(name);
		name = newName;
		all.put(name, obj);
	}
	
	init {|sysSymbolsOrMlSel|
		var sysSyms;
		all.put(name, this); //this.logln("all:" + all);
		if (sysSymbolsOrMlSel.isKindOf(MultiLevelSelector)) { mlSel = sysSymbolsOrMlSel 
		}{ mlSel = MultiLevelSelector([ sysSymbolsOrMlSel.collect{|sym| SYSTabParamSys(sym) } 
			?? {[]} ]) };
		sysSyms = this.sysSymbols;
		//validSYSs = sysSyms.select{|sym| if (Systema.at(sym).notNil) { true } {
		//	Systema.load(sym.asString++"."++Systema.fileEnding, true).isKindOf(Collection) } };
		sysSyms.do{|sym| var str;  Systema.at(sym) ?? { str = sym.asString;
			if (str.contains(tabCopySYSFileMark)) {
				Systema.load(copiedSYSDir +/+ str ++ "."++ Systema.fileEnding, true)
			}{ Systema.load(str ++ "." ++ Systema.fileEnding, true) } } };
		tabP.checkDefaults(sysSyms.first);
		mlSel.sourceAtPath([]).do{|obj, i| obj.checkDefaults([obj], mlSel) };
		Systema.addDependant(this);
	}
	sysSymbols { 
		^mlSel.sourceAtPath([]).collect{|param| param.sysSymbol}
	}
	frameRate {
		^tabP.frameRate
	}
	frameRate_ {|rate, drawServer|
		if (drawServer.notNil) {ÊdrawServer.drawEnvir.parent.put(\frameRate, rate)
		}{ tabP.frameRate = rate }
	}
	addAll{|sysParamOrSysSymbol|
		var sysParams = sysParamOrSysSymbol.collect{|item| 
			if (item.isKindOf(Symbol).not) { item } { 
				// if ( this.sysSymbols.includes(item) ) { item = Systema.at(item).copy.name };
				SYSTabParamSys(item) } };
		sysParams !? { mlSel.addAll(0, sysParams); 
			sysParams.do{|param| param.checkDefaults([param], mlSel) };
			this.changed(\sysParams) }
	}
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		what.switch(
			\new, { this.updSYSTabParamSyss(args[0]) }
			,\destroy, { } //validSYSs.remove(args[0])
			,{ this.logln("unmaped update:" + [who, what, args]) })
	}
	updSYSTabParamSyss {|sym|
		var param = mlSel.sourceAtPath([]).detect{|param| param.sysSymbol == sym};
		param !? { param.updParamsFromSystema;  this.logln("updParamsFromSystema:" + sym);
			mlSel.mlDict.leafDoFrom([param], {|path, sel| sel.src.do{|p| p.updParamsFromSystema }});
			// validSYSs = validSYSs.add(sym) 
			};
	}
	saveBackParams{|drawEnvir|
		var sysCalcs = drawEnvir.sysCalcs;
		tabP.saveBackParams(drawEnvir);
		sysCalcs !? { mlSel.mlDict.leafDo{|path, sel| sel.src.do{|param| sysCalcs[param.sysSymbol] !? {
			if (param.class.respondsTo(\subEnvirSym)) { //this.logln([param, param.class.subEnvirSym]);
				param.saveBackSysParams(sysCalcs[param.sysSymbol][param.class.subEnvirSym])
			}{ param.saveBackSysParams(sysCalcs[param.sysSymbol]) }} }} };
	}
	copySYSs {
		mlSel.sourceAtPath([]).do{|param| var sysCp, sysSym = param.sysSymbol;
			if (Systema.at(sysSym).notNil) { 
				sysCp = Systema.at(sysSym).deepCopy(this.getTabCopyName(sysSym));
				sysCp.fullPath = copiedSYSDir +/+ sysCp.name ++ "." ++ Systema.fileEnding;
				param.sysSymbol = sysCp.name 
			}{ ("could not copy Systemata | missing: SYS." ++ sysSym).warn } }
	}
	
	doesNotUnderstand{|selector, value|
		var isWrite = false;
		var keys = tabP.envir.keys;
		var key = if (selector.asString.last != $_) { selector 
			}{ isWrite = true; selector.asString.drop(-1).asSymbol };
		if (keys.includes(key)) {
			if (isWrite) { tabP.envir[key] = value; ^this }{ ^tabP.envir[key] }
		}{ super.doesNotUnderstand(selector, value) }
	}
	
	buildDrawGraph{|gui, drawServer|
		this.initEnvir(drawServer);		
		tabP.buildDrawGraph([],this.makeNodes(drawServer), mlSel, this);
	}
	makeNodes {|drawServer|
		drawNode = DrawFuncGroup(drawServer);
			debugNode = DrawDebugGroup(drawServer); 
			DrawFunc(\postFrameRate, (dump: true, pfOffset: 0@0), debugNode); //only 4 developing
		^drawNode
	}
	clearDrawGraph {|drawServer, clearPE=false|
		var pE, clearedPE;
		savedParentDrawEnvir = drawServer.drawEnvir.parent;
		drawNode.remove(drawServer); drawNode = nil;   // may be called two times (by zone.onClose)
		debugNode.remove(drawServer); debugNode = nil; // but nil.remove is valid
		drawServer.clearActions;
		if (clearPE) {
			pE = drawServer.drawEnvir.parent; clearedPE = ();
			drawServerPEkeys.do{|key| clearedPE.put(key, pE[key]) };
			pE.clear; pE.putAll(clearedPE);
		}
	}
	initEnvir {|drawServer|
		drawServer.drawEnvir.parent.putAll((sysTab: this, frameRate: this.frameRate));
		if (drawServer != prevDrawServer) { // that is, if win was closed
			prevDrawServer = drawServer;
			drawServerPEkeys = drawServer.drawEnvir.parent.keys;
			savedParentDrawEnvir !? { 
				drawServer.drawEnvir.parent.keys.sect(savedParentDrawEnvir.keys)
					.do{|key| savedParentDrawEnvir.removeAt(key) };
				drawServer.drawEnvir.parent.putAll(savedParentDrawEnvir) };
		}
	}
	asFuncTarget {|funcHolder|
		funcHolder.class.name.switch(
			\Meta_DrawFuncGroup, 	{ ^drawNode }
			,\Meta_DrawDebugGroup,	{ ^debugNode }
			, { ("could not find funcHolder:" + funcHolder + "asFuncTarget").warn } )
	}
	
}
/*
a = SYSTab("someName")
a.mlSel.mlDict.postTree
a.mlSel.decomposeSources
a.asCompileString

SYS.ionian; SYS.phrygian
a = SYSTab("someName", [\ionian, \phrygian]); 
a.midiRef;
a.save;
b = "/Users/admin2/Library/Application Support/SuperCollider/Extensions/quarks_ MC/Systema/Systema_lib/SYSTab_Archive/someName.systab".load
b.asCompileString

b = SYSTab("Tropoi", [ 'hyperLydian', 'hyperAeolian', 'hyperPhrygian', 'hyperIonian', 'hyperDorian', 'lydian', 'aeolian', 'phrygian', 'ionian', 'dorian', 'hypoLydian', 'hypoAeolian', 'hypoPhrygian', 'hypoIonian', 'hypoDorian' ]);
b.save;

b = SYSTab("Tropoi", [ 'hyperLydianTropos', 'hyperAeolianTropos', 'hyperPhrygianTropos', 'hyperIonianTropos', 'hyperDorianTropos', 'lydianTropos', 'aeolianTropos', 'phrygianTropos', 'ionianTropos', 'dorianTropos', 'hypoLydianTropos', 'hypoAeolianTropos', 'hypoPhrygianTropos', 'hypoIonianTropos', 'hypoDorianTropos' ]);
b.save;

b.mlSel.mlDict.postTree
b.mlSel.decomposeSources
b.sysSymbols

b.mlSel.selector(0).src
b.mlSel.selector(1).src
b.mlSel.selector(1).src.choose.sysSymbol
b.mlSel.selector(2).src

b.mlSel.selPath(1)

b.mlSel.idle
b.mlSel.idle = false

b.mlSel.selected
b.mlSel.selected(0)
b.mlSel.selected(0).last.sysSymbol
b.mlSel.selected(1)
b.mlSel.selected(1).last.sysSymbol

b.xS
b.xS = 2
*/
