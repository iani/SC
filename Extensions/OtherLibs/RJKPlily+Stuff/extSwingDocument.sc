
+SwingDocument {
	currentLine {
		^this.getSelectedLines(this.selectionStart - 1, 0);
	}

	makeWindow { | title, string, rect |
		var userView, scrollView;
		isEdited = false;
		window = JSCWindow.new(title ? "", rect ? Rect(40, 40, 600, 900))
			.toFrontAction_({ SwingDocument.current = this })
			.onClose_({
				if (isEdited) { this.closed} {this.closed}
			})			// tell Document wrapper
			;
//		userView = JSCUserView(window, window.view.bounds.insetBy( 4, 4 ))
//			.resize_(5);	
		view = JSCTextView(window, Rect(2, 0, window.bounds.width - 2, window.bounds.height))
		    	.resize_( 5 )
    			.focus( true )
    			.hasVerticalScroller_(true)
    			.hasHorizontalScroller_(true)
    			.autohidesScrollers_(true)
    			.font_(defaultFont)
    			;
//	 	userView.drawFunc = {| v |
//			view.bounds = v.bounds.width_(v.bounds.width max: SwingDocument.minimumTextViewWidth)
//		};
		if (string.notNil) { view.string = string };
		window.front;
		dataptr = window;
		// optionFlag 2^^20, command 21, ctrl 19, shift 17
		view.keyDownAction = 		{ | view, key, modifiers, unicode |
			var str, shift, ctl, opt, cmd, class, result;
		// shift - 17, ctl - 18, option - 19, cmd - 20
			shift = modifiers.bitTest(17);
			ctl = modifiers.bitTest(18);
			opt = modifiers.bitTest(19);
			cmd = modifiers.bitTest(20);
			if (verbose) { [shift, ctl, cmd, opt, key.ascii, unicode].postln };
			if (key.ascii > 3  && cmd.not) { isEdited = true };
			if ((cmd)) {
				result = view;
				str = view.selectedString;
				switch (key)
				{$k}
					{thisProcess.recompile}
				{$.}
					{CmdPeriod.run}
				{$'}
					{this.syntaxColorize }
				{$d}
					{str.openHelpFile }
				{$j}
					{	
					if (str.contains($:)) 
						{ str = str.split($:) }
						{ str = str.split($.) };
					class = str[0].asSymbol.asClass;
						if (class.notNil) {
							(class.findMethod(str[1].asSymbol) ? class).openCodeFile;
						}
					}
				{$y} { 
					if (str.contains($:)) { str = str.split($:) } { str = str.split($.) };
					Document("implementations of" + str.last, this.methodTemplates(str.last) )
					}
				{$u} { 
					if (str.contains($:)) { str = str.split($:) } { str = str.split($.) };
					Document("references to" + str.last, this.methodReferences(str.last) ) }

				{ $/}
					{  this.commentLines; isEdited = true  }
				{ 247.asAscii }		// option-cmd-/
					{  this.uncommentLines; isEdited = true  }
				{ $8 }
					{ if (shift) { this.commentRegion; isEdited = true } }
				{ 176.asAscii }		//option-cmd-*
					{ if (shift && opt) { this.uncommentRegion; isEdited = true } }

				// search and replace
				{$f}
					{ this.openFindWindow }
				{$g}
					{ if (shift) { this.findBackwards } { this.find } }

				{$n} { SwingDocument("untitled","") }
				{$o} { SwingFileBrowser( path) }
				{$s} { if (path.isNil || shift ) { SwingSaveAs(path)} { this.save(path) } }
					
// copy and paste all implemented in Java, but do not check for dirty files...
// also do not preserve formatting
//				{$c}		{}
//				{$x } 	{}
//				{$v} 	{}
				{$w} 	{|v| } // close window
					
				
			} {	
				if (shift|| ctl || cmd) {
					switch (key)
					{ 13.asAscii }
						 {
							str = this.selectedString;
							if (str.size ==0) { str = this.currentLine };
							str = str.interpret.asString;
							if (listener.notNil) { 
								listener.string_(str ++ "\n",listener.string.size,0)
							} {
								str.postln
							};
							result = view;
						} 
					{ 127.asAscii } 
						{ result = view; CmdPeriod.run }
					;
				}
			};
			result;
		};	
	}
	
	
//the following functions might be implemented in the Java source

	commentLines {
		var string, chunks, newString, incomplete, last;
		string = view.selectedString;
		incomplete = string.last != Char.nl;
		chunks = string.split(Char.nl);
		last = "\n" ++ chunks.pop;			// either a newline or a line fragment
		newString = "//" ++ chunks[0];
		chunks[1..].do { | l | newString = newString ++ "\n//" ++ l };
		view.selectedString = newString ++ last;
		defer ({this.syntaxColorize;}, 0.1);
	}
	
	uncommentLines {
		var string, chunks, newString,incomplete, last;
		string = view.selectedString;
		chunks = string.split(Char.nl);
		if (chunks[0][0..1] == "//") { newString = chunks[0][2..]} { newString = chunks[0] };
		chunks[1..].collect{ |c | 
			if (c[0..1] == "//") { newString = newString ++ "\n" ++ c[2..]  } { newString = newString ++ "\n" ++ c } 
		};
		view.selectedString = newString;
		defer ({this.syntaxColorize;}, 0.1);
	}	
	
	commentRegion {
		view.selectedString = "/*" ++ view.selectedString ++ "*/";
		defer({this.syntaxColorize}, 0.1);
	}
	
	uncommentRegion {
		if ( (view.selectedString[0..1] == "/*") &&
			(view.selectedString[view.selectedString.size -2..] == "*/")) {
			view.selectedString = view.selectedString[2..view.selectedString.size - 3];
			defer({this.syntaxColorize}, 0.1);
		};
	}

	find { | findString, ignoreCase |
		var pos;
		view.focus;
		findString = findString ? SwingDocument.findString;
		ignoreCase = ignoreCase ? SwingDocument.ignoreCase;
		if (view.selectionSize == 0) { 
			pos = view.string.find(findString, ignoreCase, view.caret)
		} {
			pos = view.string.find(findString, ignoreCase, view.caret + 1)
		};
		if (pos.isNil) {
			pos = view.string.find(findString, ignoreCase, 0) 		};
		if (pos.notNil) { 
			view.select(pos, findString.size); 
		};
		^pos
	}

	findBackwards { | findString, ignoreCase |
		var pos;
		view.focus;
		findString = findString ? SwingDocument.findString;
		ignoreCase = ignoreCase ? SwingDocument.ignoreCase;
		if (view.selectionSize == 0) { 
			pos = view.string.findBackwards(findString, ignoreCase, view.caret)
		} {
			pos = view.string.findBackwards(findString, ignoreCase, view.caret - 1)
		};
		if (pos.isNil) {
			pos = view.string.findBackwards(findString, ignoreCase, view.string.size - 1)		};
		if (pos.notNil) { 
			view.select(pos, findString.size); 
		};
		^pos
		
	}

	replace { | replaceString |
		replaceString = replaceString ? SwingDocument.replaceString;
		view.setString(replaceString, view.selectionStart, view.selectionSize);
		isEdited = true;
	}

	replaceAll { | findString, replaceString, ignoreCase |
		findString = findString ? SwingDocument.findString;
		replaceString = replaceString ? SwingDocument.replaceString;
		while { this.find(findString, ignoreCase).notNil } { this.replace(replaceString) };
		isEdited = true;
	}
	
	openFindWindow {
		var w;
		if (findWindow.isNil) {
			findWindow = Environment.make {
				~w = w = Window("Find", Rect(500,800, 600, 170), false).front;
				w.onClose_({SwingDocument.findWindow = nil});
				w.view.background_(Color(0.9,0.9,0.9));
				
				StaticText(w, Rect(0,20,125,20))
					.string_("Find:").align_(\right).font_(Font("Helvetica",16));
				TextField(w, Rect(130,20, 420, 20))
					.keyDownAction_({ | v | SwingDocument.findString = v.string})
					.action_({Document.current.find})
					.font_(Font("Helvetica",16));
				
				StaticText(w, Rect(0,50,125,20))
					.string_("Replace with:").align_(\right).font_(Font("Helvetica",16));
				TextField(w, Rect(130, 50, 420, 20))
					.keyDownAction_({ | v | SwingDocument.replaceString = v.string})
					.font_(Font("Helvetica",16));
				
				~range = Button(w, Rect(130,80,200,20))
					.states_([["Replace All Within Entire File"], ["Replace All Within Selected Region"]]);
				
				~case = Button(w, Rect(350,80,200,20))
					.action_({| v | 
						if (v.value ==0) 
							{ SwingDocument.ignoreCase = true } 
							{ SwingDocument.ignoreCase = false } 
					})
					.states_([["Ignore Case"], ["Use Case"]]);
				
				Button(w, Rect(40, 110, 100, 20))
					.states_([["Replace All"]])
					.action_({ Document.current.replaceAll} );
				Button(w, Rect(150, 110, 100, 20))
					.action_({ Document.current.replace })
					.states_([["Replace One"]]);
				Button(w, Rect(260, 110, 100, 20))
					.action_({ Document.current.replace;  Document.current.find})
					.states_([["Replace&Find"]]);
				Button(w, Rect(370, 110, 100, 20))
					.action_({ Document.current.findBackwards })
					.states_([["Previous"]]);
				Button(w, Rect(480, 110, 100, 20))
					.action_({ Document.current.find })
					.states_([["Next"], ["Next"]]);
			}
		};	
		findWindow[\document] = this;
		findWindow[\w].front;
	}
	
// the following are langauge specific functions that should remain as language methods

	methodReferences { | name |
		// this will not find method calls that are compiled with special byte codes such as 'value'.
		var out, references;
		out = CollStream.new;
		references = Class.findAllReferences(name.asSymbol);
		if (references.notNil, {
			out << "References to '" << name << "' :\n";
			references.do({ arg ref; out << "   " << ref.asString << "\n"; });
		},{
			out << "\nNo references to '" << name << "'.\n";
		});
		^out.collection
	}
	methodTemplates { | name |
		// this constructs the method templates when cmd-Y is pressed in the Lang menu.
		var out, namestrings, sizes, methods, position;
		name = name.asSymbol;
		methods = Class.allClasses.collect{ | c | 
			c.methods.select{| m| m.name == name}.first 
		}.select(_.notNil);
		namestrings = methods.collect{| m | (m.ownerClass.name.asString ++ ": " ++ m.name.asString) };
		sizes = namestrings.collect(_.size);
		position = sizes.maxItem + 2;

		out = CollStream.new;
		out << "Implementations of '" << name << "' :\n";
		methods.do { |m, i |
			out << namestrings[i] << String.fill(position - sizes[i], Char.space);
			
			if (m.argNames.isNil or: { m.argNames.size == 1 }) {
				if (name.isSetter, { out << "(val)"; });
			}{
				if (name.asString.at(0).isAlpha) { out << " ("  };
				m.argNames[1..m.argNames.size -2].do { | name | out << name << ", " };
				out << m.argNames.last;
				if (name.asString.at(0).isAlpha) {  out << ") " };
			};
			out.nl;
		};
		if (methods.size == 0) {
			out << "\nNo implementations of '" << name << "'.\n";
		};

		^out.collection
	}
	
	openCodeFile { | str |
		var class;
		if (str.contains(":")) 
			{ str = str.split($:) }
			{ str = str.split($.) };
		class = str[0].asSymbol.asClass;
		if (class.notNil) {
			(class.findMethod(str[1].asSymbol) ? class).openCodeFile;
		}
	}
	
	syntaxColorize {
		var startPos, curPos, char, prevChar, nest;
		var stream;
		startPos = 0;
		curPos = `0;
		char = Char.space;
		stream = Routine { var string, offset;
			if (view.selectionSize !=0) {
				offset = view.selectionStart;
				string = view.selectedString
			} {
				offset = 0;
				string = view.string;
			};
			string.do { | c, i | curPos.value = i + offset; c.yield };
		};
//		view.setStringColor(Document.theme[\textColor], startPos, view.selectionStart, view.selectionSize);
		while {
			prevChar = char;
			char = stream.next ?? { ^this };
			case		
				{ char == $$ }
					{ char = stream.next }	
				{ char.isDecDigit }
			 	 {	
			 	 	startPos = curPos.value;
					while ({
						char = stream.next;
						char.notNil && { char.isDecDigit || (char == $.) || (char == $r) }
					});
					view.setStringColor(Document.theme[\numberColor], startPos, curPos.value - startPos);
					startPos = curPos.value;
				 }
				{ char == $/ }
				 { 
					startPos = curPos.value;
					char =  stream.next;
					case
						{ char == $/ } 
						 {
							while({
								char = stream.next;
								char.notNil && { char != Char.nl }
							});
							view.setStringColor(Document.theme[\commentColor], startPos, curPos.value - startPos);
							startPos = curPos.value;
						 }
						{ char == $*}
						 {	 
							nest = 1;
								while({
									char = stream.next;
									case
										{ char == $* } 
										{
											char = stream.next;
											if (char == $/) { 
											    	nest = nest - 1;
											}
										} 
										{ char == $/ }
										{
											char = stream.next;
											if (char == $*) { 
											    nest = nest + 1;
											}
										}; 
									char.notNil  && (nest != 0);
								});
							if (nest == 0) {
								view.setStringColor(Document.theme[\commentColor], startPos, curPos.value - startPos + 1);
							};
							nest = 0;
							startPos = curPos.value;
						};
				 }
				{ char == $' } 
				 {
					startPos = curPos.value;
					while ({
						char = stream.next ;
						char.notNil && { char != $' };
					});
					view.setStringColor(Document.theme[\symbolColor], startPos, curPos.value - startPos);
					startPos = curPos.value;
				 }

				{ char == $" } 
				 {
					startPos = curPos.value;
					while ({
						char = stream.next ;
						char.notNil && { char != $"};
					});
					view.setStringColor(Document.theme[\stringColor], startPos, curPos.value - startPos);
					startPos = curPos.value;
				 }

				{ prevChar.isAlpha.not && char.isUpper && char.isAlpha } 
				 {	
					startPos = curPos.value;
					while ({
						char = stream.next;
						char.notNil && { char.isAlphaNum}
					});
					view.setStringColor(Document.theme[\classColor], startPos, curPos.value - startPos);
					startPos = curPos.value;
				
				 }
				{ char == $\\ } 
				 {
					startPos = curPos.value;
					while ({
						char = stream.next ;
						char.notNil && { char.isAlphaNum};
					});
					view.setStringColor(Document.theme[\symbolColor], startPos, curPos.value - startPos);
					startPos = curPos.value;
				 };
			char.notNil;				
		};	
	}

}

//+String {
//	
//	prPost { _PostString }
//	prPostln {  _PostLine }
//
//	postln {
//		if (DocTop.redirectPost) {
//			Document.listener.string_(this ++ "\n",Document.listener.string.size,0)
//		} {
//			this.prPostln 
//		}
//	}
//	post {
//		if (DocTop.redirectPost) {
//			Document.listener.string_(this,Document.listener.string.size,0)
//		} {
//			this.prPost 
//		}
//	}
//
//}
+Document {
	*allDocuments_ { | docs | allDocuments = docs }
}



/*
	SwingOSC.default.boot;
	SwingDocument.go
	b = SwingDocument.open("/Applications/SuperCollider3/build/SCClassLibrary/GSched.sc")
	DocumentBrowser.default;
SwingDocument.stop
*/

/*
To do:

	file open, save, save as, close
	enclosure selection
	split entry/post window
	minimum text view width

*/
