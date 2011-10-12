// Required by AutoClassHelper3
// Parses sources in search for comments
// andreavalle, rewritten by mc

DocParser3 {
	var <targetClass, <files, <sources;
	var <classMethodList, <cGetters, <cSetters;
	var <instMethodList, <iGetters, <iSetters;
	var <indices, <cDox, <mDox;
	
	*new {|class| ^super.newCopyArgs(class).init }
		/*@ ex:
	a = DocParser3(DocParser3).parseDox;
	a.cGetters.do{|m| m.name.postln};
	a.cDox.keysValuesDo{|path, doc| ("\n"+path+"\n"+ doc + "\n\n").postln}; "".postln;
	a.mDox.keysValuesDo{|meth, doc| ("\n"+meth+"\n"+ doc + "\n\n").postln}; "".postln;
		@*/
	/*
	ACH(DocParser3)
	a = DocParser3(Systema).parseDox;
	ACH(Systema)
	ACH(AutoClassHelperTest)
	*/

	init {
		files = Set.new;
		classMethodList = targetClass.class.methods ? [];
		classMethodList.do{|m| files.add(m.filenameSymbol)};
		cGetters = classMethodList.select({|m| (targetClass.classVarNames ? [])
			.includes(m.name) });
		cSetters = classMethodList.select({|m| m.name.asString.includes($_) }) ;
		classMethodList = classMethodList.reject({|m| 
			cSetters.includes(m) || cGetters.includes(m) }) ;
//this.logln("cGetters, cSetters, classMethodList:" + targetClass);
//[cGetters,cSetters,classMethodList].do{|a| this.logln("size:" + a.size); a.do{|m| m.name.postln}};
		instMethodList = targetClass.methods ? [];
		instMethodList.do{|m| files.add(m.filenameSymbol)};
		iSetters = instMethodList.select({|m| m.name.asString.includes($_) }) ;
		iGetters = instMethodList.select({|m| targetClass.instVarNames.includes(m.name) });
		instMethodList = instMethodList.reject({|m|
			 iGetters.includes(m) || iSetters.includes(m) }) ;
//this.logln("iGetters, iSetters, instMethodList:" + targetClass);
//[iGetters,iSetters,instMethodList].do{|a| this.logln("size:" + a.size); a.do{|m| m.name.postln}};
		
		sources = IdentityDictionary.new;
		files.do{|pathSym| var file, source;
			file = File.new(pathSym.asString, "r");
			source = file.readAllString;
			file.close;
			sources.put(pathSym, source) 
		};
//sources.keysValuesDo{|f, s| [f, s].postln};
		
		indices = IdentityDictionary.new;
		files.do{|pathSym| indices.put(pathSym, List.new) };
		(classMethodList ++ instMethodList).do{|m| indices[m.filenameSymbol].add(m.charPos) };
		indices.keysValuesDo{|key, list| indices.put(key, list.add(sources[key].size-1).sort) };
//indices.keysValuesDo{|f, l| [f, l.size, l].postln}
		
	}
		
	parseMethodDox {
		var pathSym, iList, start, end;
		mDox = IdentityDictionary.new;
		(classMethodList ++ instMethodList).do{|m| 
			pathSym = m.filenameSymbol;
			iList = indices[pathSym];
			start = m.charPos; 
			end = iList[iList.indexOf(start)+1];
			mDox.put(m, this.getComment(sources[pathSym].copyRange(start, end)));
		}
	}
	parseClassDox {
		cDox = IdentityDictionary.new;
		sources.keysValuesDo{|pathSym, src| cDox.put(pathSym, this.getComment(src))}
	}
	parseDox {
		this.parseMethodDox;
		this.parseClassDox;
	}
	getComment{|str| var end;
 		var start = str.find("/*@");
 		if (start.notNil) { 
	 		end = str.find("@*/") ? 0;
 			 ^str.copyRange(start+3, end-1)//.postln
 		 }{
	 		 start = str.find("//@");
	 		 if (start.notNil) {
		 		 end = str.copyRange(start, str.size-1).find("\n") ? 0;
		 		 ^str.copyRange(start+3, start+end-1)//.postln
	 		 }{ ^"" }
	 	}
	}
}
