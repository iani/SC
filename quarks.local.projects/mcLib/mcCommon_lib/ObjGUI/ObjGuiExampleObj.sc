ObjGuiExampleObj { // has named instances
	classvar <all, uniqueSuffix="V0";
	
	var <name;
	
	*initClass {
		all = IdentityDictionary.new
	}
	*at { |name| ^all[name] }
	*new{|name|
		name ?? { all[name] !? { ^all[name] } };
		^super.newCopyArgs(this.uniqueCopyNameSuffix(name ? \noName).asSymbol).init
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
	init {
		all.put(name, this);
		//this.logln("all:" + all)	
	}
}

/*
a = ObjGui.new
a.numItems

ObjGuiExampleObj("someName")
ObjGuiExampleObj("someOtherName")
10.do{ ObjGuiExampleObj.new} 
ObjGuiExampleObj.all

*/