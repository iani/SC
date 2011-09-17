SYSTabParamSys : SYSTabParam {
	
	classvar <default;	
	var <sysSymbol; //vars in super class: <envir, <drawBDefs, <drawADefs, <drawNode;
	var <>subLevelClasses;
	
	*initClass { default = (); StartUp.add({this.makeDefaults}) }

	*new{|sysSymbol, envir, drawBDefs, drawADefs| 
		^super.newCopyArgs(envir ?? {()}, drawBDefs, drawADefs).preInit(sysSymbol).init
	}
	
	copy {|sysSymb| ^this.class.new(sysSymbol, envir.copy, drawBDefs, drawADefs) }

	storeArgs {
		var saveE = ();
		this.saveBackParams(envir, saveE); //filter old saved leftovers, if any && \self avoid crash!
		^[sysSymbol, saveE, drawBDefs, drawADefs];
	}
	preInit {|sym|
		sysSymbol = sym;
		envir.putAll((self: this, sysSym: this.sysSymbol))
	}
	init {
		drawBDefs = drawBDefs ?? { List[ \SYS_basic, \SYS_HeaderFooter]};
		drawADefs = drawADefs ?? { List[]};
				
		subLevelClasses = [SYSinstParam, SYSvocParam];
	}
	sysSymbol_ {|sym| 
		sysSymbol = sym; envir.sysSym = sym 
	}
	checkDefaults {|path, mlSel|
		Systema.at(sysSymbol) !? { this.makeParamsFromSystema(Systema.at(sysSymbol)) };
//this.logln("envir before:" + envir);
		this.class.default.keys.do{|key| envir[key] 
			?? { envir.put(key, this.class.default[key]) } };
//this.logln("envir after:" + envir);
		this.checkSelector(path, mlSel)
	}
	checkSelector {|path, mlSel|
		mlSel.sourceAtPath(path) ?? { 
			mlSel.putAt(path, [subLevelClasses.collect{|class| class.new(sysSymbol) }.asList] )};
		mlSel.sourceAtPath(path).do{|obj| obj.checkDefaults(path++obj, mlSel) };
	}
	makeParamsFromSystema {|sys|
		envir.label ?? { envir.label = sys.globDict[\label] ?? { sys.name.asString }  }
	}
	updParamsFromSystema {
		this.makeParamsFromSystema(Systema.at(sysSymbol));
	}
	saveBackSysParams{|srcE| //is sysClac[sysSymbol] or sysClac[sysSymbol][subEnvirSym]
//this.logln("srcE" + [sysSymbol, srcE]);
		srcE !? { this.class.default.keys.do{|key| srcE[key] !? { envir.put(key, srcE[key]) } } }		
	}
	saveBackSysParamsToSYS{|srcE| //is sysClac[sysSymbol] --> relevant keys of SYS where ??
		// maybe provide a list of keys that would also work with makeParamsFromSystema (see above)
		// this.class.default.keys.do{|key| srcE[key] !? { envir.put(key, srcE[key]) } }
	}
	*makeDefaults {
		default.putAll((
			label: "no name", sysProtoSym: \nil //nil does not work -> nil happens to delete the key !!
			,footMode: 4
			,sysLGab: 4, sysRGab: 4, coreLGab: 27, coreRGab: 27
			,spineColor: Color.white, spineW: 1, ribColor: Color.white, selHeight: 7
			,ribWL: 1, ribWR: 1, ribWrootL: 3, ribWrootR: 3, ribExtMaxL: 15, ribExtMaxR: 15
			
			,tcSpineW: 1, tcDashes: FloatArray[5, 2], tcMarkW: 2, annoGab: 6, annoLabelFrameW: 1
			,tcLabel: "TC", tcFont: Font("ProFont", 9), tcColor: Color.blue
			,rootLabel: "R*", rootFont: Font("ProFont", 9), rootColor: Color.green
			
			,selectedDegrees: Set[], refToneColor: Color.new255(255, 0, 210);
			
		));
	}	
}
/*
a = SYSTabParamSys.new
a.envir
a.class.default
a.checkDefaults
a.checkDefaults(SYS.archytasStoichos.name)

a.legend
a.hello
*/
SYSinstParam : SYSTabParamSys {
	classvar <default, <subEnvirSym = \inst;
	*initClass { default = (); StartUp.add({this.makeDefaults}) }
	
	init {
		drawBDefs = drawBDefs ?? { List[ \SYS_anno_inst ]};
		drawADefs = drawADefs ?? { List[]};
		
		subLevelClasses = List[];
	}
	makeParamsFromSystema {|sys|
		// envir.label ?? { envir.label = sys.globDict[\label] ?? { sys.name.asString }  }
	}
	*makeDefaults {
		default.putAll((
			align: \left // will not work at saveBack -> keys must be unique
		));
	}	
}
SYSvocParam : SYSTabParamSys {
	classvar <default, <subEnvirSym = \voc;
	*initClass { default = (); StartUp.add({this.makeDefaults}) }
	
	init {
		drawBDefs = drawBDefs ?? { List[ \SYS_ano_voc ]};
		drawADefs = drawADefs ?? { List[]};
				
		subLevelClasses = List[];
	}
	makeParamsFromSystema {|sys|
		// envir.label ?? { envir.label = sys.globDict[\label] ?? { sys.name.asString }  }
	}
	*makeDefaults {
		default.putAll((
			align: \right // will not work at saveBack -> keys must be unique
		));
	}	
}

