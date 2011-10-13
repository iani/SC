CodePage {
	// 2so: use Semaphore to 'control the parallel execution of post threads' 
	//  that is: make them serial and nicely pile up...
	
	/*@ shortDesc: converts UTF8 keyboard input into custom extended-ascii encodings
	longDesc: Keyboard input on a macintosh can be converted into a limited set of extended-ascii charaters. Specially designed fonts display these characters in respect of the 'CodePages' defined in the config file.<br>									<br>Overwrites methods of <b>String</b><i><br>post <br>postln</i><br>	<br>Extends <b>String</b> by the methods<i><br>enc, penc, pencln <br>asUTF8, asUTF8List, asUnicodeList <br>findCodes, includesCode <br>findUTF8, takeUTF8, keepUTF8, dropUTF8 <br>findOfCode, takeOfCode, keepOfCode, dropOfCode <br>asEncDigits, asEncNames <br>convertByAscii, convertByDigit, convertByName</i><br>				<br>Extends <b>Char</b> by the methods<i><br>asciiExt <br>utf8ByteN  <br>encDigit <br>encName</i><br>												<br>Extends <b>Number</b> by the methods<i><br>asEncUTF8, asEncName</i><br>	<br>Extends <b>Symbol</b> by the methods<i><br>asEncUTF8, asEncDigit</i><br>	<br>Extends <b>SequenceableCollection</b> by the methods<i><br>asEncUTF8, asEncName, asEncDigit</i>
	seeAlso: String, Systema
	issues: Specific fonts must be installed. <br>SuperCollider .scd files actually save (little Endian UTF-16) Unicode ==> open a .scd in TextWrangler to see and edit the codes as Symblols; Hex Dump to see UTF encoding.
	instDesc: For examples (in RTF) execute the following line:
	longInstDesc: Document.open(Document.current.path.replace("html", "rtf"))
	@*/
	/*
	ACH(CodePage)
	
	Document.listener.selectRange(33, 2); ""
	Document.listener.selectionStart;
	
	CodePage.z.postln(Document.listener.selectionStart); ""
	
	CodePage.pencCode(\pm70Voc2)
	7.do{CodePage.pencCode(\agmIns); CodePage.pencCode(\agmVoc)}
	
	*/
	
	classvar <relativeDir = "CodePage.configs", <configFile = "CodePage.config.scd";
	classvar indexedItems=#[\utf8, \count, \name]; 
	classvar globItems=#[\font, \fontSize, \color, \charsSymbolRatio];
	classvar <pages, <groups, <sources, <docs, <delta=0.005, <boxWin, <box, <cBox, startBox=false;
	classvar <>defaultFont, <>color, <font, <fontSize, <resets, <active=false, <>ready=false;
	classvar <z; // <- thesse are only for debugging with 'debugBox'
	classvar cP, ac;
	
	*initClass {
		pages = Event.new; groups = Event.new	; 
		docs = IdentityDictionary.new;
		Class.initClassTree(StartUp);
		StartUp.add({ RemoteInit(this) });
	}
	*failLoadInit {|dir| ("failed to load config file:" + configFile + "from dir:\n"++ dir).warn }
	*postLoadInit{
		font = defaultFont.name; fontSize = defaultFont.size;
		sources = IdentityDictionary.new;
		if(startBox) { this.codePostBox };
//this.debugBox;
		this.makePages;
//pages.keysValuesDo{|code, ev| this.logln("codePage:" + code, lfB:4, lfE:1); ev.keys.asList.sort.do{|key| ("\n"+code+"\t"+key+"\n\t"+pages[code][key]+"\nsrc:"+sources[code][key]+"\n").postln }};
		ready=true; // check also for menue item ??
		NotificationCenter.notify(this, \ready);
		this.saveResets;
		this.addMenueLibrary;
	}
	*makePages{ 
		var fbck, start, end;
		pages.keysDo{|code| 		//first pass
			fbck = pages[code][\fallback]; 
			if (fbck.isNil) { pages[code].put(\fbcks, List.new) }{
				pages[code].put(\fbcks, this.traceFbks(fbck, [fbck])) };
//this.logln("code, fbcks" + [code,  pages[code][\fbcks] ]);
			globItems.do{|entry| if (pages[code][entry].isNil) {
				pages[code].put(entry, this.traceGlobal(code, entry)) }
			};
			sources.put(code, IdentityDictionary.new);
			#start, end = pages[code][\range];
			indexedItems.do{|entry| if (pages[code][entry].isNil) { 
				pages[code].put(entry, Array.newClear(end-start+1)) };
				sources[code].put(entry, Array.newClear(end-start+1));
			};
		};
		pages.keysDo{|code|	 	//second pass
			#start, end = pages[code][\range];
			indexedItems.do{|entry| for (start, end) {|c, i| 
				if (pages[code][entry][i].isNil) { //this.logln("lookup:" + [code, entry, c, i]);
					sources[code][entry][i] = this.traceIndexedCode(code, entry, i) 
				}{
					sources[code][entry][i] = code
				}
			}}
		};
		pages.keysDo{|code|	 	//third pass
			#start, end = pages[code][\range];
			indexedItems.do{|entry| for (start, end) {|c, i| 
				if (pages[code][entry][i].isNil) { //this.logln("lookup:" + [code, entry, c, i]);
					pages[code][entry][i] = this.traceIndexed(code, entry, i) }
			}}
		}
	}
	*traceFbks{|code, trace|
		var fbck = pages[code][\fallback];
		if (fbck.isNil) { ^trace } { ^this.traceFbks(fbck, trace.add(fbck)) }
	}
	*traceGlobal{|code, entry|
		pages[code][\fbcks].do{|key| if(pages[key][entry].notNil) {^pages[key][entry]} }
		^nil
	}
	*traceIndexedCode{|code, entry, i|
		pages[code][\fbcks].do{|key| if(pages[key][entry][i].notNil) {^key} }
		^nil
	}
	*traceIndexed{|code, entry, i|
		pages[code][\fbcks].do{|key| if(pages[key][entry][i].notNil) {^pages[key][entry][i]} }
		^nil
	}
	*saveResets{
		resets = IdentityDictionary.new
			.put(\defaultFont, defaultFont.copy)
			.put(\encs, IdentityDictionary.new);
		pages.keysDo{|code| resets[\encs].put(code, IdentityDictionary.new);
			[\font, \fontSize].do{|key| resets[\encs][code].put(key, pages[code][key]) }
		}
	}
	*addMenueLibrary {
		Platform.case(\osx, {
			var options=Array[-10, -5, -3, -1, 1, 3, 5, 10]; 
			var en, fo, ra, msgFunc, rebuildFunc;
			SCMenuSeparator(CocoaMenuItem.default);
			cP = SCMenuGroup(CocoaMenuItem.default, "CodePage").state_(active);
			ac = SCMenuItem(cP, "active").action_( { 
				if (ac.state) { this.active_(false)
					//active = false; ac.state_(false); cP.state_(false) 
				}{	this.active_(true)
					//active = true; ac.state_(true); cP.state_(false);
				}
			}).state_(active);
			SCMenuItem(cP, "CodePostBox front").action_({ this.boxFront });
			SCMenuSeparator(cP);
			en = SCMenuGroup(cP, "change font sizes");
			fo = SCMenuGroup(cP, "set font top window");
			SCMenuSeparator(cP);
			ra = SCMenuItem(cP, "reset All").action_( {
				defaultFont = resets[\defaultFont]; 
				fontSize = defaultFont.size;
				msgFunc.value(defaultFont, false);
				resets[\encs].keys.asList.sort.do{|code|
					[\font, \fontSize].do{|key| pages[code][key] = resets[\encs][code][key] };
					this.pencCode(code)
				};
				rebuildFunc.value
			});
			
			msgFunc= {|font, select=true|
				var pos = Document.listener.selectionStart;
				var msg = ("\n"++ this + "\nNew post font:" 
					+ font.name +"("++ font.size +"pt)").postln;
				if (select) { { 	
					Document.listener.font_(font, pos, msg.size);
					Document.listener.selectRange(pos, msg.size)
				}.defer(0.1) };
			};
			
			rebuildFunc = { var all, gr;
				en.children.copy.do({|child| child.remove}); // note: you must copy here!
				fo.children.copy.do({|child| child.remove}); // note: you must copy here!
				
				all = SCMenuGroup(en, "all"); 
				options.do{|n| SCMenuItem(all, n.asString).action_({
						this.changeFontSizesBy(n);
						msgFunc.value(defaultFont);
						rebuildFunc.value
					}) 
				};
				SCMenuSeparator(en);
				gr = SCMenuGroup(en, "groups");
				groups.keys.asList.sort.do{|grp| var mi = SCMenuGroup(gr, grp.asString);
					options.do{|n| SCMenuItem(mi, n.asString)
						.action_({
							var codes = this.expandCodes(grp);
							codes.do{|code|
								this.changeEncFontsizeBy(code, n);
								this.pencCode(code)
							};
							rebuildFunc.value
						})
					}
				};
				SCMenuSeparator(en);
				pages.keys.asList.sort.do{|code| var mi = SCMenuGroup(en, code.asString);
					options.do{|n| SCMenuItem(mi, n.asString)
						.action_({ 
							this.changeEncFontsizeBy(code, n);
							this.pencCode(code);
							rebuildFunc.value
						}) 
					}
				};
				this.getFonts.do{|font| SCMenuItem(fo, ""++font.name+"("++font.size+"pt)")
					.action_({ msgFunc.value(font); Document.current.font_(font) });
				}
			};
			rebuildFunc.value;
		})
	}
	*active_ {|boolean|
		active = boolean;
		ac.state_(boolean); cP.state_(boolean);
		this.changed(\active, active)
	}
	*changeFontSizesBy {|n|
		defaultFont.size_(fontSize = defaultFont.size + n);
		pages.keysDo{|code| this.changeEncFontsizeBy(code, n) } 
	}
	*changeEncFontsizeBy{|code, n| 
		pages[code][\fontSize] = pages[code][\fontSize] + n
	}
	*pencCode{|code|
		("\n\n"++code++":\n").postln;
		pages[code][\utf8].asCompileString.pencln(code)
	}
	*getFonts{ 
		var fonts=List[defaultFont];
		pages.keys.collect{|code| [pages[code][\font], pages[code][\fontSize]] }.do{|fspec| 
			if (fonts.any{|font| ((font.name == fspec[0]) && (font.size == fspec[1]))}.not) { 
				fonts.add(Font(fspec[0], fspec[1] ? fontSize)) } };
		^fonts.sort{|a, b| a.name.size < b.name.size }
	}
	*ascii2Key{|ascii, code, searchKey|
		case 
		{ this.isCode(code).not } {^nil} 
		{ ((pages[code][\range][0] <= ascii) && (ascii <= pages[code][\range][1])).not } {^nil}
		{ ^pages[code][searchKey][ascii-pages[code][\range][0]] }
	}
	*key2Key {|item, code, key, searchKey| 
		var index;
		if (this.isCode(code).not) {^nil};
		index = pages[code][key].indexOfEqual(item);
		if (index.isNil) {^nil} { ^pages[code][searchKey][index] }
	}
	*isCode{|code| 
		if (pages.keys.includes(code)) {^true} {("encoding"+code+"not defined").warn; ^false}
	}
	*expandCodes{|codes| 
		var list = List.new;
		codes.do{|sym|
			case
			{ groups.keys.includes(sym) }{ list.addAll(groups[sym]) }
			{ pages.keys.includes(sym) }{ list.add(sym) }
			{ ("encoding or group"+sym+"not defined").warn }
		};
		^list
	}
	*enc {|str, codes|
		codes = if (codes.isNil) { groups[\default] } { this.expandCodes(codes) };
		codes.do{|code| str = this.convert(str,code)};
		^str
		/*@
		str: a String. 
		codes: code symbol or code group symbol as defined in the config file
		ex:
		Document.open(Document.current.path.replace("html", "rtf"))
		( 
		 for(186,255,{ arg i;
		var a;
		[i,a = i.asAscii,a.isAlphaNum,a.isPrint,a.isPunct,a.isControl].postln; });
		) 
		Document.listener.font_(Font("MonacoIns", 12))
		Document.listener.font_(Font("MonacoVoc", 14))
		Document.listener.font_(Font("Monaco", 9))
		@*/
	}
	*convert {|str, code|
		pages[code][\utf8].do{|codeStr, i| 
			str = str.replace(codeStr, (pages[code][\range][0]+i).asAscii)
		}
		^str
	}
	*prConvertByKey{|utf8, srcCode, trgCode, key|
	 	var index = pages[srcCode][\utf8].detectIndex{|str| str == utf8 };
	 	if (index.isNil) {^nil};
	 	^switch(key,
	 		\index, { pages[trgCode][\utf8][index] },
	 		\ascii, { pages[trgCode][\utf8][  
		 		pages[srcCode][\range][0] + index - pages[trgCode][\range][0] ] },
		 	\count, { pages[trgCode][\utf8][ pages[trgCode][\count].indexOf(
			 	pages[srcCode][\count][index]) ] },
			\name, { pages[trgCode][\utf8][ pages[trgCode][\name].indexOf(
			 	pages[srcCode][\name][index]) ] }
	 	)
	}
	*prIncludesCode{|str, code| ^pages[code][\utf8].any{|codeStr| str.contains(codeStr)} }
	*findCodes{|str, codes|
		var hits = Set.new;
		codes = if (codes.isNil) { pages.keys } { this.expandCodes(codes) };
		codes.do{|code| if (this.prIncludesCode(str, code)) {hits.add(code)} };
		^hits
		/*@
		desc: returns the encodings contained in string.
		str: a String. 
		codes: Limit the search scope. Default is to search all encodings.
		ex:
		Document.open(Document.current.path.replace("html", "rtf"))
		@*/ 
	}
	*findSpans{|str, codes, indices, sizes|
		var notmeti, prev, allhits = List.new, spans = List.new;
		var y, spsize, links = List.new;
		notmeti = indices.copy;
		codes.do{|code| var foundi, hits = List.new;
			pages[code][\utf8].do{|codeStr, j| 
				foundi = str.findAll(codeStr);
				if (foundi.notNil) { 
					hits = hits ++ foundi.collect{|i| [i, sources[code][\utf8][j]]}; 
					foundi.do{|i| notmeti.indicesOfEqual(i).do{|r| notmeti.removeAt(r)} }
				}
			};
			allhits.addAll(hits);
		};
		if (notmeti == indices) {^[[],[]]}; //if all utf8 unknown -> return empty

		notmeti.collect{|i| indices.indexOf(i)}.reverse.do{|r|
			indices.removeAt(r); sizes.removeAt(r)};
		allhits = allhits.sort{|a,b| a[0] < b[0]}.reject{|h, j| var k= allhits[j+1]; 
				if (k.notNil) { h[0] == allhits[j+1][0] } {false} }; //remove double hits
		y = str.findAll(" "); //insert spaces as "nutral element" to font and color changes
		if (y.notNil) {allhits = allhits ++ y.collect{|i| [i, \space]} };
		//reduce hits to spans:
		sizes = sizes.addFirst(-1); // only 'sizes.addFirst(-1)' does not work !!!!!!!!!!!!!!!
		prev= [0,nil]; y=0;
		allhits.sort{|a,b| a[0] < b[0]}.do{|l,x| var z = prev[0] + sizes[y] +1; //sorted again!
			if (l[1] == \space) {
				if (z != l[0]) {
					if (prev[1].notNil) { spans.add([z, nil]); prev=[0, nil] } 
				}{
					prev[0] = prev[0]+1 
				}
			}{
				if (z != l[0]) {
					if (prev[1].notNil) { spans.add([z, nil]) };
					spans.add(l);
				}{ 
					if (prev[1] != l[1]) {spans.add(l)}
				};
				prev=l.copy; y=y+1
			};
		};
		sizes = sizes.drop(1);
		y = if (allhits.last[1] == \space) 
			{ allhits.last[0]+1 } { allhits.last[0] + sizes.last + 1 };
		if (y != (str.size)) {spans.add([y, nil])};
		// add code switches to indices and sizes:
		y = spans.asArray.takeThese{|l| l[1].notNil}.collect{|l| [l[0], 0]};
		if (y.notNil) {
			#indices, sizes = ([indices, sizes].flop ++ y).sort{|a,b| a[0] < b[0]}.flop
		};
		
		spsize=spans.size; y=0;
		indices.do{|i, link| 
			if (i == spans[y][0]) { links.add(link); if (y < (spsize-1)) {y=y+1} } };
		y=0;
		indices= [indices[0]] ++ indices.drop(1).add(str.size-1).collect{|i,x| y=y+sizes[x]; i-y};
		links.do{|r, x| spans[x][0] = indices[r] };
		if (indices.first != 0) { spans.addFirst([0, nil]) };
		^spans.add(indices.last+1)
	}
	*spaceRatiosOfCodes{|str, codes| 
		var spraList=List.new;
		codes = if (codes.isNil) { groups[\default] } { this.expandCodes(codes) };
		codes.do{|code| var csRatio = pages[code][\charsSymbolRatio] ? 1;
			spraList.addAll( str.findOfCode(code).select{|range| range[2] == true}
			.collect{|range| range.copy.put(2, csRatio) } ) };
		^spraList
	}
	
	// ------- these could become instances of subclasses related to docs and views ---------------

	*allocRecPos {|doc| 
		docs.put(doc, IdentityDictionary.new.put(\selStart, 0).put(\offset, List.new).put(\size, 0)
			.put(\notSched, true).put(\docFuncs, List.new) )
	}
	*recPrtPos{|str, doc, o, utf8=false|
		var selStart = doc.selectionStart; 
		if (docs[doc].isNil) { this.allocRecPos(doc) };
//z.postln("selStart, o, str.size, sizeCount:" + [selStart, o, str.size, docs[doc][\size] ]);
		if (selStart != docs[doc][\selStart]) { 	// each new case reset:
			docs[doc].put(\selStart, selStart).put(\offset, List[]).put(\size, str.size +o)
		}{ 									// if posts are piling up:
			if (utf8) { docs[doc][\offset].addFirst(docs[doc][\size]) };
			docs[doc][\size] = docs[doc][\size] + str.size + o
		}
	}
	*fontInPlace{|str, codes, sec, doc|
		var indices, keys;
		codes = if (codes.isNil) { groups[\default] } { this.expandCodes(codes) };
		#indices, keys = this.findSpans(str, codes, *sec.flop).flop;
//this.logln("indices:\n" + indices);
//this.logln("keys:\n" + keys);
		indices = indices + ( (docs[doc][\selStart] ? 0) + (docs[doc][\offset].pop ? 0) );
		keys.pop;		
		docs[doc][\docFuncs].addFirst( this.getDocFunc(doc, indices, keys) );
		this.startInPlaceThread(doc)
	}
	*getDocFunc {|doc, indices, keys|
		^{ keys.do{|key, x|
				if (key.notNil) {
					doc.font_(Font(pages[key][\font] ? font, pages[key][\fontSize] ? fontSize), 
						indices[x], indices[x+1]-indices[x]);
					doc.stringColor_(pages[key][\color] ? color, 
						indices[x], indices[x+1]-indices[x])
				}{
					doc.font_(Font(font, fontSize), indices[x], indices[x+1]-indices[x]);
					doc.stringColor_( color, indices[x], indices[x+1]-indices[x])
				}
			};
		}
	}
	*startInPlaceThread{|doc|
//z.postln("startInPlaceThread:" +[ docs[doc][\selStart], docs[doc][\docFuncs].size]);
		if (docs[doc][\notSched]) {
			AppClock.sched(0, {
				try {
//z.postln("\n\n\tstarted !!!\n\n");
					docs[doc][\docFuncs].do{|docFunc, i| 
//z.postln("count, delta, selStarts:" + [i, i*delta, docs[doc][\selStart], doc.selectionStart ]); 
						//docFunc.value
						docFunc.defer(delta + i*delta)
					};
					docs[doc][\docFuncs].clear;
				} { |error|
					error.isKindOf(Error).if({
						"\nError fontinPlaceThread of CodePage:\n".postlnn;
						error.errorString.postlnn;
						docs[doc][\docFuncs].clear;
					});
				};
				docs[doc][\notSched] = true;
				nil
			});
			docs[doc][\notSched] = false;
		}
	}
	
	*pencView{|view, str, codes| 	//call from inside a AppClock thread
		var sec = str.findUTF8;
		if (sec.notEmpty) {
			view.string_( this.enc(str, codes) );
			this.encView(view, codes, sec, str);
		}{
		 	view.string_(str)
		}
	}
	*encView {|view, codes, sec, string| //call from inside a AppClock thread
		var indices, keys;
		var str = string ? view.string;
		sec = sec ? str.findUTF8;		
		codes = if (codes.isNil) { groups[\default] } { this.expandCodes(codes) };
		#indices, keys = this.findSpans(str, codes, *sec.flop).flop;
		keys.pop;
		this.getViewFunc(view, indices, keys).value
	}
	*getViewFunc{|view, indices, keys|
		^{ keys.do{|key, x|
				if (key.notNil) {
					view.setFont(
						Font(pages[key][\font] ? font, pages[key][\fontSize] ? fontSize), 
						indices[x], indices[x+1]-indices[x]);
					view.setStringColor(pages[key][\color] ? color, 
						indices[x], indices[x+1]-indices[x])
				}{
					view.setFont(Font(font, fontSize), indices[x], indices[x+1]-indices[x]);
					view.setStringColor( color, indices[x], indices[x+1]-indices[x])
				}
		}}
	}
	
	*codePostBox{
		boxWin = GUI.window.new("CodePage PostBox", Rect(0,0,700,350))
			.onClose_({ boxWin = nil }).front;
		boxWin.view.decorator = FlowLayout.new( boxWin.bounds );
		box = CodePagePostTopBox(boxWin, 344@350);
		cBox = CodePagePostTopBox(boxWin, 344@350);
		box.postln("..." + box + "doubling strings containing codes" 
			+ "(may get a selective post win for Logln)\n");
		cBox.postln("..." + cBox + "still showing the utf8 codes" 
			+ "(may get another selective post win for Logln)\n");
	}
	*boxFront {
		if (boxWin.isNil) { this.codePostBox }{ boxWin.getParents.last.findWindow.front }
	}
	*boxpost {|str, codes| if (boxWin.notNil) { box.penc(str, codes); cBox.post(str) }}
	*boxpostln {|str, codes| if (boxWin.notNil) { box.pencln(str, codes); cBox.postln(str) }} 
	
	
	// ------------------------------------ kept for debugging ------------------------------------
	
	*debugBox{
		var f = FlowView(nil, Rect(700,0,700,350));
		z = StatusBox(f, 700@350);
		StatusBox.clearDefault;
		z.postln("box for post debugging...\n");
	}
	
	
	
	
	
	// -------------------------------------- outdated --------------------------------------------
	
	*findSections{|str, codes|
		var notmeti, indices = List.new, sizes=List.new, spaces;
		var prev, allhits = List.new, spans = List.new;
		var y, spsize, links = List.new;
		str.do{|c, i| var n = c.utf8ByteN ? 0; if (n>0) { indices.add(i); sizes.add(n) } };
this.logln("indices:" ++ indices + "sizes:" + sizes, lfB:2, lfE:2);
		if (indices.isEmpty) {^spans } {
			notmeti = indices.copy;
			codes = if (codes.isNil) { groups[\default] } { this.expandCodes(codes) };
//this.logln("codes:" +codes);
			codes.do{|code| var foundi, hits = List.new;
				pages[code][\utf8].do{|codeStr, j| 
					foundi = str.findAll(codeStr);
					if (foundi.notNil) { 
//this.logln("code, j, codeStr.asUTF8, foundi:" + [code, j, codeStr.asUTF8, foundi]);
						hits = hits ++ foundi.collect{|i| [i, sources[code][\utf8][j]]}; 
						foundi.do{|i| notmeti.indicesOfEqual(i).do{|r| notmeti.removeAt(r)} }
//;this.logln("notmeti:" + notmeti);
					}
				};
				//allhits = allhits ++ hits.flat.collect{|i| [i, code]};
				allhits.addAll(hits);
			};
//this.logln("indices+(sizes+1):" + indices+(sizes+1));
//this.logln("allhits.sort:" + allhits.sort{|a,b| a[0] < b[0]});
		//allhits.sort{|a,b| a[0] < b[0]}.do{|l| if (prev != l[1]) {spans.add(l)}; prev=l[1] };
		
			notmeti.collect{|i| indices.indexOf(i)}.reverse.do{|r| 
				r.postln; 
				indices.removeAt(r); sizes.removeAt(r)};
this.logln("notmeti:" + notmeti);
this.logln("indices:" ++ indices);
this.logln("sizes:" ++ sizes);
			if (indices.isEmpty) {^[[],[]]};
		
			y = allhits.sort{|a,b| a[0] < b[0]}.select{|h, j| var k= allhits[j+1]; 
				if (k.notNil) { h[0] == allhits[j+1][0] } {false} };
this.logln("ambigue double triggers:" + y, lfB:2, lfE:2);
			allhits = allhits.sort{|a,b| a[0] < b[0]}.reject{|h, j| var k= allhits[j+1]; 
				if (k.notNil) { h[0] == allhits[j+1][0] } {false} };
		
			spaces = str.findAll(" ");
			if (spaces.notNil) {allhits = allhits ++ spaces.collect{|i| [i, \space]} };
this.logln("allhits.sort:" + allhits.sort{|a,b| a[0] < b[0]});
this.logln("allhits, spaces, sizes.size:"+ [allhits.size, spaces.size, sizes.size]);
			prev= [0,nil]; sizes.addFirst(-1); y=0;

			allhits.sort{|a,b| a[0] < b[0]}.do{|l,x| var z;
("\n"+x+"iteration, prev:" + prev + "curr:" + l + "y:" +y).postln;
				z = prev[0] + sizes[y] +1; //[z, l[0]].postln;

				if (l[1] == \space) {
					if (z != l[0]) {
						if (prev[1].notNil) { spans.add([z, nil]); prev=[0, nil] } 
					}{
						prev[0] = prev[0]+1 
					}
				}{
					if (z != l[0] ) {
						if (prev[1].notNil) { spans.add([z, nil]) };
						spans.add(l); y=y+1
					}{ 
						//this.logln("new code:" + [prev[1], l[1]]);
						if (prev[1] != l[1]) {spans.add(l); y=y+1}
					};
					prev=l.copy;
				};
//("spans:" + spans).postln;
			};
			sizes = sizes.drop(1);
			y = if (allhits.last[1] == \space) 
				{ allhits.last[0]+1 } { allhits.last[0] + sizes.last + 1 };
//this.logln("y, str.size" + [y, str.size]);
			if (y != (str.size)) {spans.add([y, nil])};
this.logln("spans:" + spans, lfB:3, lfE:1);
			
			//[indices, sizes].flop.postln; 
			y = spans.asArray.takeThese{|l| l[1].notNil}.collect{|l| [l[0], 0]}.postln;
			if (y.notNil) {
			//if (spaces.notNil) {
				//spaces = spaces.collect{|i| [i, 0]}.flop;
			//#indices, sizes = ([indices, sizes].flop ++ spaces).sort{|a,b| a[0] < b[0]}.flop
				#indices, sizes = ([indices, sizes].flop ++ y).sort{|a,b| a[0] < b[0]}.flop
			};
this.logln("indices:" ++ indices);
this.logln("sizes:" ++ sizes);
			spsize=spans.size; y=0;
			indices.do{|i, link| var enc;
				if (i == spans[y][0]) {
					links.add(link);
					if (y < (spsize-1)) {y=y+1}
					//while {(y < (spsize-1)) && (enc.isNil)} { y=y+1; enc=spans[y][1].postln }
				}
			};			
this.logln("links:" + links);
			y=0;
			indices= [indices[0]] ++ indices.drop(1).add(str.size-1).collect{|i,x| 
				//("i:"+i+"size:"+sizes[x]).postln;
				//("y:" + y).postln;
				y=y+sizes[x]; // ("y+size:" +y).postln; 
				(i-y)//.postln
			};
this.logln("indices:" ++ indices);
str.enc.postln;
str.enc.size.postln;
			links.do{|r, x| spans[x][0] = indices[r] };
			if (indices.first != 0) { spans.addFirst([0, nil]) };
			^spans.add(indices.last+1)
		}
	}	
}





	// --------------------------------- overwrites & extensions ----------------------------------

+ Post {
	*put{|item| item.asString.postD }
	*putAll{|aCollection| aCollection.asString.postD }
}

+ String {
	warnn { "WARNING:\n".postD; this.postlnD }
	
	post { if (CodePage.active) {^this.penc} {^this.postD} } //still crashes on 3.4.2 when active
	postln { if (CodePage.active) {^this.pencln } {^this.postlnD } } 	
	//post { if (CodePage.active) {^this.penc} {^this.postt} } // without .defer crashes on 3.4.2
	//postln { if (CodePage.active) {^this.pencln } {^this.postlnn } } 
	
//prPost { if (CodePage.active) {^this.penc} {^this.postt} }  // if StatusBox.sc is used
//prPostln { if (CodePage.active) {^this.pencln } {^this.postlnn } }// if StatusBox.sc is used

	postt { 
		if (CodePage.ready) { CodePage.recPrtPos(this, Document.listener, 0) }; 
		^this.prprPost 
	}
	postlnn { if (CodePage.ready) { CodePage.recPrtPos(this, Document.listener, 1) }; 
		^this.prprPostln 
	}
	// must use defer within streams ?!? to access Document -> sort out later...
	postD { 
		if (CodePage.ready) { { CodePage.recPrtPos(this, Document.listener, 0) }.defer };
		^this.prprPost 
	}
	postlnD { 
		if (CodePage.ready) { { CodePage.recPrtPos(this, Document.listener, 1) }.defer }; 
		^this.prprPostln 
	}
	prprPost { _PostString }
	prprPostln { _PostLine }
	
	penc{|codes| 
		var str, sec = this.findUTF8;
		if (sec.notEmpty) { // { //crashes with or without defer on 3.4.2
			str = CodePage.enc(this, codes);
			CodePage.recPrtPos(str, Document.listener, 0, true);
			str.prprPost; CodePage.boxpost(this, codes);
			CodePage.fontInPlace(this, codes, sec, Document.listener); //}.defer;
			^this
		}{		// branch here for efficieny and to avoid: str = this.copy
			^this.postt;
		}
	}
	pencln{|codes| 
		var str, sec = this.findUTF8;
		if (sec.notEmpty) { // { //crashes with or without defer on 3.4.2
			str = CodePage.enc(this, codes);
			CodePage.recPrtPos(str, Document.listener, 1, true);
			str.prprPostln; CodePage.boxpostln(this, codes);
			CodePage.fontInPlace(this, codes, sec, Document.listener); //}.defer;
			^this
		}{		// branch here for efficieny and to avoid: str = this.copy
			^this.postlnn;
		}
	}
	
	enc {|codes| if (this.findUTF8.isEmpty) { ^this } { ^CodePage.enc(this, codes) } }
	includesCode{|code| if (CodePage.isCode(code)) { ^this.prIncludesCode(code) } {^false } }
	prIncludesCode {|code| ^CodePage.prIncludesCode(this, code)}
	findCodes{|codes| ^CodePage.findCodes(this, codes)}
	
	spaceRatiosOfCodes {|codes| 
		if (this.findUTF8.isEmpty) { ^List.new } { ^CodePage.spaceRatiosOfCodes(this, codes) } 
	}
	encSpaceDelta{|codes| 
		var spraList = this.spaceRatiosOfCodes(codes).flop;
		if (spraList.isEmpty) { ^0 } { ^spraList[2].sum - spraList[1].sum }
	}
	raggedLeftFormat {|lineMax, codes|
		// work on this later ...
		^this 
	}
	
	asUTF8{ ^this.collect{|c| c.ascii.asHexString(2)} }
	asUTF8List {
		var byteN, indices = List.new;
		this.do{|c, i| byteN = c.utf8ByteN; if(byteN.notNil) {indices.add([i, byteN])} };
		^indices.collect{|l| this.copyRange(l[0], l[0] + l[1])}.collect{|str| str.asUTF8 }
	}
	asUnicodeList {
		var uniList = List.new;
		var byteStream = this.iter; //Routine{ this.do{|c| c.yield}};
		var i=0;
		var byteN, char = byteStream.next;
		while {char.notNil && (i < this.size)} {
			byteN = char.utf8ByteN;
			if (byteN == 0) { uniList.add(char.ascii.asBinaryDigits) } { 
				uniList.add( (char.ascii.asBinaryDigits(6-byteN) ++
					Array.fill(byteN, {byteStream.next.ascii.asBinaryDigits(6)}).flat) )
			};	
			char = byteStream.next;
			i = i+1; // for safty reasons if string is not a proper UTF-8 encoding
		};
		^uniList.collect{|l| l.convertDigits(2).asHexString}
	}
	findUTF8 {
		var sec = List.new;
		this.do{|c, i| var n = c.utf8ByteN ? 0; if (n>0) { sec.add([i,n]) } };
		^sec 
	}
	takeUTF8{
		^this.findUTF8.collect{|range| this.copyRange(range[0], range[0]+range[1]) }
	}
	keepUTF8{ ^"".catList(this.takeUTF8) }
	dropUTF8{
		var remainder= this.copy;
		this.takeUTF8.do{|str| remainder = remainder.replace(str,"") };
		^remainder
	}
	findOfCode{|code|
		if (CodePage.isCode(code).not) {^List.new};
		^this.findUTF8.collect{|range|
			range ++ this.copyRange(range[0], range[0]+range[1]).prIncludesCode(code) }
	}
	takeOfCode{|code| 
		^this.findOfCode(code).collect{|range| if(range[2].not) {nil} {
			this.copyRange(range[0], range[0]+range[1]) } }.select{|item| item.notNil}
	}
	keepOfCode{|code| ^"".catList(this.takeOfCode(code)) }
	dropOfCode{|code| 
		var remainder= this.copy;
		this.takeOfCode(code).do{|str| remainder = remainder.replace(str,"") };
		^remainder
	}	
	asEncDigits{|code|
		^this.takeOfCode(code).collect{|utf8| CodePage.key2Key(utf8, code, \utf8, \count) }
	}
	asEncNames{|code| 
		^this.takeOfCode(code).collect{|utf8| CodePage.key2Key(utf8, code, \utf8, \name) }
	}
	convertByIndex{|trgCode, codes| ^this.convertByKey(trgCode, codes, \index) }
	convertByAscii{|trgCode, codes| ^this.convertByKey(trgCode, codes, \ascii) }
	convertByDigit{|trgCode, codes| ^this.convertByKey(trgCode, codes, \count) }
	convertByName {|trgCode, codes| ^this.convertByKey(trgCode, codes, \name) }
	convertByKey{|trgCode, codes, key|
		var str;
		if (CodePage.isCode(trgCode).not) {^this};
		str = this.copy;
		codes = if (codes.isNil) { CodePage.groups[\default] } { CodePage.expandCodes(codes) };
		codes = codes.asSet.remove(trgCode);
		codes.do{|srcCode|
			this.takeOfCode(srcCode).do{|utf8| str = str.replace(utf8, 
				CodePage.prConvertByKey(utf8, srcCode, trgCode, key) ? "?!?") }
		};
		^str
	}
}

+ Char {
	utf8ByteN {
		var byte = this.ascii;
		case
		{ (byte & 2r10000000) == 2r00000000 } { ^0 }
		{ (byte & 2r11100000) == 2r11000000 } { ^1 }
		{ (byte & 2r11110000) == 2r11100000 } { ^2 }
		{ (byte & 2r11111000) == 2r11110000 } { ^3 }
		{ (byte & 2r11111100) == 2r11111000 } { ^4 }
		{ (byte & 2r11111110) == 2r11111100 } { ^5 };
		^nil		
	}
	asciiExt { var n = this.ascii; if (n.isNegative) {^n+256} {^n} }
	
	encDigit{|code| ^CodePage.ascii2Key(this.asciiExt, code, \count) }
	encName{|code| ^CodePage.ascii2Key(this.asciiExt, code, \name) }
	/*
	for (0, 256) {|i| [i.asAscii.ascii, i.asAscii.asciiExt].postln}
	$ÿ.agmDigit //but LITERAL CHAR input does not work !! (because of editor)
	$ü.agmDigit //but LITERAL CHAR input does not work !! (because of editor)
	consult RTF help file what to write instead (just can't write it here):
	CodePage.openHelpFile
	*/
}	

+ Number {
	asEncUTF8{|code| ^CodePage.key2Key(this, code, \count, \utf8) }
	asEncName{|code| ^CodePage.key2Key(this, code, \count, \name) }
	
	/*
	CodePage.openHelpFile
	*/
}

+ Symbol {
	asEncUTF8{|code| ^CodePage.key2Key(this, code, \name, \utf8) }
	asEncDigit{|code| ^CodePage.key2Key(this, code, \name, \count) }
}

+ Collection {
	asEncUTF8 {|code| ^this.collect{|item| item.asEncUTF8(code) } }
	asEncName {|code| ^this.collect{|item| item.asEncName(code) } }
	asEncDigit{|code| ^this.collect{|item| item.asEncDigit(code) } }
	
	/*
	CodePage.openHelpFile
	*/
}

+ Main {
	shutdown { // at recompile, quit
		CodePage.ready = false; // crashes otherwise !!!!
		Server.quitAll;
		this.platform.shutdown;
		super.shutdown;
	}
}
	
+ Object {
	encFormat{|tabwith, argAlign, plus=false, codes, bias=0|
		var strg, size, half, encDelta;
		var align = argAlign ?? {
			case 
			{this.isKindOf(Integer)} {\r}
			{this.isKindOf(Float)} {\r}
			{this.isKindOf(Ratio)} {\c}
			{\l} 
		};
		
		if (plus) { if (this.isKindOf(Float) || this.isKindOf(Integer)) {
				if (this > 0) { strg=this.asString.addFirst($+) } } };
		
		strg = strg ?? {this.asString.replace("\t", "").replace("\r","").replace("\f","")};
		encDelta = strg.encSpaceDelta;
		if (encDelta != 0) { encDelta = encDelta + bias };
		size = strg.size + encDelta;
// this.logln("strg.size, encDelta, size" + [strg.size, encDelta, size]);
		
		if (size>tabwith) {strg = strg.copyRange(0, tabwith-2) ++ $.; size=tabwith};
		^strg = switch (align)
			{\l} {^strg ++ 31.asAscii ++ "".catList(Array.fill(tabwith-size,$ )) }
			{\r} {^"".catList(Array.fill(tabwith-size,$ )) ++ strg}
			{\c} {half = (tabwith-size) div: 2; ^"".catList(Array.fill(tabwith-size-half,$ )) 
				  ++ strg ++ 31.asAscii ++ "".catList(Array.fill(half,$ ))}
	}	
}