// a class for autogenerating help files from source markup
// a special multiline comment is used: /*@ ... @*/
// or, alternatively, single line comments may be started by //@ ...
// plus some other conventions
// After class name you can write
/*@
shortDesc: description of the method
longDesc: longer one
seeAlso: refs
issues: other aspects
testvar: a name of a instance var
classtestvar: a name of a class var
instDesc: the name of the instance methods section
longInstDesc: a comment related to the instance methods section
@*/
//
// After each method you can write
/*@  
desc: a description of the method
argName: for each arg. Default value is added automatically
ex: multiline example
@*/
// started after 04/11/07
// andreavalle, updated by mc

AutoClassHelper3 {

	var class, path, openByHelpSystem;
	var parser, classDoc, cmDict, imDict ;
	var shortDesc, longDesc, seeAlso, issues, instDesc, longInstDesc ;
	var varList ;
	var doctype, head, preface, examples ;

	*new { arg undocumentedClass, path, openByHelpSystem=false ;
			^super.newCopyArgs(undocumentedClass, path, openByHelpSystem).createDir;
	}
	createDir {
		path = path ? class.class.filenameSymbol.asString.dirname +/+ "Help";
		
		if (thisProcess.platform.isKindOf(UnixPlatform).not) {
			("*¥do not know how to create a new folder on this platform").warn
		} {
			("mkdir -p -v" + path.escapeChar($ )).unixCmd({|res|
				if (res != 0) { ("*¥ could not create directory:" + path).warn; 
					path = nil; 
				 }{
					path = path +/+ class.name ++ ".html";
				};
//this.logln("path:" + path);
				this.init;
			}, true);
		}
	}
	
	init {
		parser = DocParser3.new(class).parseDox ;
		varList = List.new ;
		// <head> tag
		head = "
<head>
<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">
<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">
<title></title>
<meta name=\"Generator\" content=\"Cocoa HTML Writer\">
<meta name=\"CocoaVersion\" content=\"824.42\">
<style type=\"text/css\">
p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 12.0px Helvetica}
p.p2 {margin: 0.0px 0.0px 0.0px 0.0px; font: 12.0px Helvetica; min-height: 14.0px}
p.p3 {margin: 0.0px 0.0px 0.0px 0.0px; font: 9.0px Monaco; min-height: 12.0px}
p.p4 {margin: 0.0px 0.0px 0.0px 0.0px; font: 14.0px Helvetica}
p.p5 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 9.0px Monaco; min-height: 12.0px}
p.p6 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 12.0px Helvetica}
p.p7 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 12.0px Helvetica; min-height: 14.0px}
p.p8 {margin: 0.0px 0.0px 0.0px 85.0px; text-indent: -85.0px; font: 12.0px Helvetica}
p.p9 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 9.0px Monaco; color: #d40000}
p.p10 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 9.0px Monaco}
p.p11 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 14.0px Helvetica}
p.p12 {margin: 0.0px 0.0px 0.0px 57.0px; text-indent: -57.0px; font: 14.0px Helvetica; min-height: 17.0px}
p.p13 {margin: 0.0px 0.0px 0.0px 85.0px; text-indent: -85.0px; font: 12.0px Helvetica; min-height: 14.0px}
p.p14 {margin: 0.0px 0.0px 0.0px 0.0px; font: 9.0px Monaco; color: #d40000}
p.p15 {margin: 0.0px 0.0px 0.0px 0.0px; font: 9.0px Monaco}
span.s1 {font: 18.0px Helvetica}
span.s2 {color: #1200c4}
span.s3 {color: #1200c4}
span.s4 {color: #000000}
span.s5 {color: #1200c4}
span.s6 {color: #d40000}
span.s7 {font: 12.0px Helvetica; color: #000000}
span.s8 {color: #0000ff}
span.s9 {color: #1200c4}
span.Apple-tab-span {white-space:pre}
</style>
</head>
" ;

			doctype = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
		
		if ( path.isNil, {
			GUI.current.dialog.savePanel({ arg newPath ;
				 path = newPath ; {this.makeHelp}.defer })
			}, { this.makeHelp }) ;

	}

	makeHelp {
		var title = class.name.asString ;
		var preface = this.createPreface ;
		var classMethodBlock = this.createClassMethodBlock ;
		var getterSetter = this.createcGetterSetterBlock + this.createiGetterSetterBlock ;
		var instanceMethodBlock = this.createInstanceMethodBlock ;
		var doc, content ;
		
		var helpfile = Document.allDocuments.select{|doc| doc.path == path }; //mc
		if (helpfile.notEmpty) {helpfile[0].close} ; //mc
		
		// here we put together all the stuff
		content = doctype +
				 "<hmtl>\n"+
				 head+
				 "<body>\n"+
				 preface +
				 classMethodBlock +
				 getterSetter +
				instanceMethodBlock +
				// examples+
				 "</body>"+
				 "</html>" ;
		File.new(path, "w")
				.write(content)
				.close ;
		// and reopen thru class.openHelpFile
		// open works if the path is a place where SC looks for Help files
		if (openByHelpSystem) {class.openHelpFile} { {Document.open(path)}.defer(0.5) }
	}

	createPreface {
		var superclasses, parents = "" ;
		this.createClassDocBlock ;
		// here only for the special case of Object
		superclasses = if ( class == Object, { nil }, { class.superclasses.reverse }) ;
		superclasses.do({arg item ; parents = parents+item+":" }) ;
		^preface = "
<p class=\"p1\"><span class=\"s1\"><b>SomeClass<span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></b></span><b>shortDesc</b><span class=\"s1\"><b><span class=\"Apple-tab-span\">	</span></b></span></p>
<p class=\"p2\"><br></p>
<p class=\"p1\"><b>Inherits from: </b><b>Parents</b></p>
<p class=\"p3\"><br></p>
<p class=\"p1\">longDesc</p>
<p class=\"p2\"><br></p>
<p class=\"p1\"><b>See also:</b> seeAlso</p>
<p class=\"p2\"><br></p>
<p class=\"p4\"><b>Other Issues</b></p>
<p class=\"p2\"><br></p>
<p class=\"p1\">issues</a></p>
<p class=\"p2\"><br></p>
"			.replace("SomeClass", class.name.asString)
			.replace("Parents", parents[..parents.size-2])
			.replace("shortDesc", shortDesc)
			.replace("longDesc", longDesc)
			.replace("issues", issues)
			.replace("seeAlso", seeAlso)

	}

	createClassMethodBlock {
		var isEx, methodTitle ;
		var classMethods = "
<p class=\"p4\"><b>Creation / Class Methods</b></p>
<p class=\"p5\"><br></p>
" ;
		var method, name, args, def, txt, ex, default ;
		//mc cmDict.keys.asArray.sort.do({ arg key ;
		parser.classMethodList.do({|method|
			isEx = false ;
			//mc method = cmDict[key][0] ;
			name = method.name ;
			
			// this.logln("method, name:" + [method, name]);
			
			methodTitle = "
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span>name</b></p>
"				.replace("name", "*"++name+this.makeName(method)) ;
			//mc if ( cmDict[key][1] != "", {classMethods = classMethods + methodTitle }) ;
			if ( parser.mDox[method] != "", {
				classMethods = classMethods + "<p class=\"p5\"><br></p>\n" + methodTitle }) ;
			//mc args = cmDict[key][1].split($\n).reject({|i| i.size < 2}) ;
			args = parser.mDox[method].split($\n).reject({|i| 
				i.any{|c| c.isAlphaNum || (c == $() || (c == $)) }.not }) ;
//this.logln("args" + [args, args.size]);
//args.do{|i| i.size.postln};
			//args[..args.size-2].do({ arg line ;
			args.do({|line, i|
//this.logln("mLine" + line);
				case	{ line.contains("desc:") }
						{ 	def = line.split($:)[0] ;
							txt = line.split($:)[1] ;
							classMethods = classMethods + "
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></b>desc</p>
" 					.replace("desc", txt)
							 }
					{ line.contains("ex:") }
						{ 	isEx = true ;
							def = line.split($:)[0] ;
							txt = line.split($:)[1] ;
							classMethods = classMethods +
							/*
"
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></p>
<p class=\"p9\"><span class=\"s4\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></span>// Example</p>
<p class=\"p10\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>ex</p>
"							*/
"
<p class=\"p10\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>ex</p>
"
 					.replace("ex", txt)
							}

					{ line.contains("desc:").not && isEx }
						{ 	classMethods = classMethods +
"<p class=\"p10\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>ex</p>"
 					.replace("ex", line);
 					/*
 					if (args.size-1 == i) { 
	 					classMethods = classMethods + "<p class=\"p5\"><br></p>\n";
	 				}
	 				*/
							}

					{ line.contains("desc:").not }
						{
							def = line.split($:)[0] ;
							default = this.getDefault(method, def) ;
							txt = line.split($:)[1] ? " ";
							if (txt.last != $-) { default = ""} {
								 default = " Default value is <b>"++default++"</b>."};
							classMethods = classMethods +
"
<p class=\"p8\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><b>def </b>- txtdfv</p>
" 							.replace("def", def)
							.replace("txt", txt).replace("dfv", default);

							}
			});
		}) ;
		^(classMethods+"<p class=\"p5\"><br></p>\n")

	}

	makeName { arg method ;
		var name = method.argumentString;
		if (name == "this") {^""} { 
			name = name.replace("this, ", "").split($,).collect({|i| i.split($=)[0]}).asString ;
			^"("++name[2..name.size-3]++")"
		}
	}
	

	getDefault { arg method, name ;
		var mString = method.argumentString.replace(" ", "") ;
		var pos ;
		var next ;
		name = name.reject( { arg i; i.isAlphaNum.not } ) ;
		pos = mString.find(name) ;
		if ( pos.notNil, {
			 next = mString[pos+name.size] ;
				if (next == $=, { mString = mString[pos+name.size..].split($=)[1] ;
								^mString = mString.split($,)[0] },
							{^"nil"})
			}, {^"nil"})
	}

	createInstanceMethodBlock {
		var isEx, methodTitle ;
		var instanceMethods = "<p class=\"p11\"><b>instDesc</b></p>
<p class=\"p12\"><br></p>
<p class=\"p6\">longInstDesc</p>
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span></p>
" 			.replace("instDesc", instDesc)
			.replace("longInstDesc", longInstDesc)
			;
		var method, name, args, def, txt, ex, default ;
		//mc imDict.keys.asArray.sort.do({ arg key ;
		parser.instMethodList.do({|method|
		 	isEx = false ;
			//mc method = imDict[key][0] ;
			name = method.name ;
			methodTitle = "<p class=\"p5\"><br></p>\n"+
			"
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span>name</b></p>
"				.replace("name", name++" "++this.makeName(method)) ;
			//mc if ( imDict[key][1] != "", { instanceMethods = instanceMethods + methodTitle }) ;
			if ( parser.mDox[method] != "", { instanceMethods = instanceMethods + methodTitle }) ;
			//mc args = imDict[key][1].split($\n).reject({|i| i.size < 2}) ;
			args = parser.mDox[method].split($\n).reject({|i| // i.any{|c| c.isAlphaNum}.not }) ;
				i.any{|c| c.isAlphaNum || (c == $() || (c == $)) }.not }) ;
//this.logln("args" + [args, args.size]);
//args.do{|i| i.size.postln};
			//args[..args.size-2].do({ arg line ;
			args.do({|line, i|
			// args[..args.size-2].do({ arg line ;
				case	{ line.contains("desc:") }
						{ 	def = line.split($:)[0] ;
							txt = line.split($:)[1] ;
							instanceMethods = instanceMethods + "
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></b>desc</p>
" 					.replace("desc", txt)
							 }
					{ line.contains("ex:") }
						{ 	isEx = true ;
							def = line.split($:)[0] ;
							txt = line.split($:)[1] ;
							instanceMethods = instanceMethods +
/*
"
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></p>
<p class=\"p9\"><span class=\"s4\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></span>// Example</p>
<p class=\"p10\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>ex</p>
"
*/
"
<p class=\"p10\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>ex</p>
"
 					.replace("ex", txt);
 					if (args.size-1 == i) { 
	 					instanceMethods = instanceMethods + "<p class=\"p5\"><br></p>\n";
	 				}
							}

					{ line.contains("desc:").not && isEx }
						{
							instanceMethods = instanceMethods +
"<p class=\"p10\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>ex</p>"
 					.replace("ex", line)
							}


					{ line.contains("desc:").not }
						{ 	def = line.split($:)[0] ;							default = this.getDefault(method, def) ;
							txt = line.split($:)[1] ? " ";
							if (txt.last != $-) { default = ""} {
								 default = " Default value is <b>"++default++"</b>."};
							instanceMethods = instanceMethods +
"
<p class=\"p8\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><b>def </b>- txtdfv</p>
" 							.replace("def", def).replace("txt", txt).replace("dfv", default);

							}
			});
		}) ;
		^(instanceMethods+"<p class=\"p5\"><br></p>\n")
	}


	createClassDocBlock {
		// var args = classDoc.split($\n).reject({|i| i.size < 2}) ;
		var args, loc;
		var firstDocFile = class.class.filenameSymbol;
		var cLoc = List[firstDocFile] ++ parser.cDox.keys.remove(firstDocFile);
//this.logln("cLoc:" + cLoc);
		# shortDesc, longDesc, seeAlso, issues, instDesc, longInstDesc = Array.fill(6, "");
		cLoc.do{|key, i| 
			if (i == 0) {loc = ""} {loc = "<br><br>Extension<br>"++key+" adds:<br><br>" };
			args = parser.cDox[key].split($\n).reject({|i| i.size < 2}) ;
	//very quick hack:		
			args.do({ arg line ;
				case	{ line.contains("shortDesc:") }
						{ shortDesc = shortDesc ++ loc ++ line.split($:)[1] ; }
					{ line.contains("longDesc:") }
						{ longDesc = longDesc ++ loc ++ line.split($:)[1] ; }
					{ line.contains("seeAlso:") }
						{ seeAlso = seeAlso ++ loc ++ line.split($:)[1] ; }
					{ line.contains("issues:") }
						{ issues = issues ++ loc ++ line.split($:)[1] ; }
					{ line.contains("instDesc:") }
						{ instDesc = instDesc ++ loc ++ line.split($:)[1] ; }
					{ line.contains("longInstDesc:") }
						{ longInstDesc = longInstDesc ++ loc ++ line.split($:)[1] ; }
					// residual: allows to create the varList
					{ line.contains("longInstDesc:").not }
						{ varList.add([ line.split($:)[0]
							.select({|i| i.isAlphaNum })
							.asSymbol
							, line.split($:)[1] ]); }
				}) ;
		}
	}


	createcGetterSetterBlock {
		var getterSetterBlock = "
<p class=\"p11\"><b>Accessing Class Variables</b></p>
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span></p>
" ;	
		var getters = parser.cGetters.collect(_.name) ? [];
		var setters = parser.cSetters.collect(_.name) ? [];
		var name, comment, line, match, key ;
		
		varList.do{|pair| 
		//	comment = varDict[key] ;
			#key, comment = pair;
			line = "" ;
			match = false;
			
			line = if ( getters.includes(key), { match = true;
"
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span>*someVar</b></p>
"				.replace("someVar", key.asString);
			},{""}) ;
			if ( setters.includes((key++"_").asSymbol), {  line = line +
"
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span>*someVar_</b></p>
"				.replace("someVar", key.asString);
				match = true;
			}) ;
			if (match) {
				getterSetterBlock = getterSetterBlock + line +
"
<p class=\"p6\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>"+comment+"</p>
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></p>
"
			}
		} ;
		^getterSetterBlock + "
<p class=\"p7\"><b><span class=\"Apple-tab-span\">	</span></b><span class=\"Apple-tab-span\">	</span></p>
"
	}
	
	createiGetterSetterBlock {
		var getterSetterBlock = "
<p class=\"p11\"><b>Accessing Instance Variables</b></p>
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span></p>
" ;	
		var getters = parser.iGetters.collect(_.name) ? [];
		var setters = parser.iSetters.collect(_.name) ? [];
		var name, comment, line, match, key ;
		
		varList.do{|pair| 
		//	comment = varDict[key] ;
			#key, comment = pair;
			line = "" ;
			match = false;
			
			line = if ( getters.includes(key), { match = true;
"
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span>someVar</b></p>
"				.replace("someVar", key.asString);
			},{""}) ;
			if ( setters.includes((key++"_").asSymbol), {  line = line +
"
<p class=\"p6\"><b><span class=\"Apple-tab-span\">	</span>someVar_</b></p>
"				.replace("someVar", key.asString);
				match = true;
			}) ;
			if (match) {
				getterSetterBlock = getterSetterBlock + line +
"
<p class=\"p6\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span>"+comment+"</p>
<p class=\"p7\"><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span><span class=\"Apple-tab-span\">	</span></p>
"
			}
		} ;
		^getterSetterBlock + "
<p class=\"p7\"><b><span class=\"Apple-tab-span\">	</span></b><span class=\"Apple-tab-span\">	</span></p>
"
	}


}


ACH : AutoClassHelper3 {
}