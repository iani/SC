SwingDocument : Document {
	classvar <>defaultFont, <>syntaxColorTheme, <>keyDownFunction;
	classvar <>minimumTextViewWidth;
	classvar <>findWindow, <>findString, <>replaceString, <>ignoreCase;
	classvar <>listener;
	classvar <>verbose = false;
//	classvar <>redirectPost = false;			

	var <>window, <>view, <>path;
	var <>promptToSave, <>isEdited, bounds;

	*initClass{
//		this.startup;
	}

	*startup {
		Document.implementationClass = SwingDocument;
		if (Document.allDocuments.isNil) { super.startup };
		defaultFont = JFont("Monaco", 10);
		ignoreCase = true;
		findString = "";
		replaceString = "";
		minimumTextViewWidth = 600;
		listener = SwingDocument("listener", "");
	}
	
	*current { 		
		^current 
	}	

	*go {  GUI.swing; this.startup}
	*stop { if (CocoadDocument.notNil) { GUI.cocoa; Document.implementationClass = CocoaDocument; } }
	*new { arg title="Untitled", string="", makeListener=false;
		^super.prBasicNew.initByString(title, string.asString, makeListener);
	}

	*prSetSyntaxColorTheme {|textC, classC, stringC, symbolC, commentC, numberC|
		syntaxColorTheme = [textC, classC, stringC, symbolC, commentC, numberC];
	}
		
	stripUnicode  {
		var char, ascii, size, string, i, newString;
		size = view.string.size;
		i = 0;
		while ({ 
		 	char = view.string[i];
		 	ascii = char.ascii;
		 	 case
		 	 { ascii < 128 } 
		 	 	{ }
			 { ascii & 0xe0 == 0xc0 }
			 	{ view.setString("\n", i, 2); }
			 { ascii & 0xf0 == 0xe0 }
			 	{ view.setString("\n", i, 3)  }
			 { ascii & 0xf8 == 0xf0 }
			 	{view.setString("\n", i, 4) };
			 i = i + 1;
			 i < view.string.size;
		})
	}

	save { | path |
		var file;
		file = File(path, "w");
		file.putString(view.string);
		file.close
	}
	
	propen { arg argPath, selectionStart=0, selectionLength=0;
		var ext, file, docstr;
		path = argPath;
		this.makeWindow(path.basename);
		dataptr = window;
		ext = path.splitext[1];
		if ( true && ( ( ext == "htm" ) || ( ext == "html") || ( ext == "rtf")  )  ){
	 		view.open(path);
	 		// hack to take care of unicode characters in html files
	 		Routine({ 
		 		100.do { 
		 			if (view.string.size > 0) { this.stripUnicode; nil.yield };
		 			0.01.wait;
		 		}
	 		}).play(AppClock);
		} {
			file = File(path, "r");
			if(file.isOpen){
				docstr = ext.switch(
					"htm",		{file.readAllStringHTML },
					"html",		{file.readAllStringHTML },
					"rtf",		{file.readAllStringRTF},
					"sc",		{file.readAllString},
					"scd",		{file.readAllString},
					"txt",		{file.readAllString},
					"ily",		{file.readAllString},
					"ly",		{file.readAllString},
					"doc",		{file.readAllString},
					
					nil,		{"reading".postln; file.readAllString}							
					);
				file.close;
				if(docstr.notNil) { view.string = docstr } { view.string = "" };
				view.select(selectionStart, selectionLength);
				if ( (ext == "sc") ||  (ext == "scd") ) {
					defer({this.syntaxColorize}, 0.1);
				};
			}
		};
		
	}
	
	prinitByString { arg title, str, makeListener;
		this.makeWindow(title);
		view.string = str;
	}

	front {
		window.front
	}

	unfocusedFront {
		window.front
	}

	alwaysOnTop_{|boolean=true|
		window.alwaysOnTop_(boolean)
	}

	alwaysOnTop{
		^window.alwaysOnTop
	}
	
	selectRange {arg start=0, length=0;
		view.select(start, length)
	}

	removeUndo{
	}

	underlineSelection{
	}

	balanceParens { arg levels = 1;
		var prev = this.selectionStart, next;
		levels.do {
			this.prBalanceParens;
			prev = next;
			next = this.selectionStart;
			if(prev == next) { ^this };
		}
	}

	*postColor_{ arg color;
//		_PostWindow_SetTextColor
	}



//private-----------------------------------
	prIsEditable_{arg editable=true;
		view.editable_(editable)
	}
	prSetTitle { arg argName;
		window.name_(argName)
	}
	prGetTitle {
		^window.name
	}
	prGetFileName {
		^path
	}
	prSetFileName {|apath|
		path = apath;
	}
	prGetBounds { 	// we need a copy for DocumentBrowser to stay synch'ed
		^bounds;
	}

// window sizing does not include titlebar
// include it for compatability with CocoaDocument
	prSetBounds { arg argBounds;
		bounds = argBounds;
		window.bounds = argBounds.resizeBy(0, -20);
	}
	
	prBalanceParens {
//		_TextWindow_BalanceParens
	}

	//if range is -1 apply to whole doc
	setFont {arg font, rangeStart= -1, rangeSize=100;
		view.setFont(font, rangeStart, rangeSize)
	}

	setTextColor { arg color,  rangeStart = -1, rangeSize = 0;
		view.setStringColor(color,rangeStart, rangeSize);
	}

	text {
		^view.string
	}
	selectedText {
		^view.selectedString
	}
	selectUnderlinedText { arg clickPos;
//		_TextWindow_SelectUnderlinedText
		^false
	}

	linkAtClickPos { arg clickPos;
//		_TextWindow_LinkAtClickPos
		^false
	}

	rangeText { arg rangestart=0, rangesize=1;
		^view.string[rangestart..rangestart + rangesize - 1]
	}


	prinsertText { arg dataPtr, txt;
		view.setString(txt, dataPtr);
	}
	insertTextRange { arg string, rangestart, rangesize;
		view.setString(string, rangestart, rangesize)
	}

	*prnumberOfOpen {
		^Document.allDocuments.size
	}
	prinitByIndex { arg idx;
		^Document.allDocuments[idx]
	}
	prGetLastIndex {
		^Document.allDocuments.last
	}


	prclose {
//		_TextWindow_Close
		window.close
	}
	
	//other private
	//if -1 whole doc
	prSetBackgroundColor { arg color;
		view.background_(color)
	}
	prGetBackgroundColor { arg color;
		^view.background
	}
	prSetSelectedBackgroundColor { arg color;
//		view.setBackground(color, view.selectStart, view.selectRange)
	}
	prGetSelectedBackgroundColor{ arg color;
//		_TextWindow_GetSelectedBackgroundColor
	}
	selectedRangeLocation {
		^view.selectionStart
	}
	selectedRangeSize {
		^view.selectionSize
	}

	prSelectLine { arg line;
		var lines, start, end;
		lines = view.string.findAll("\n");
		start = lines[line];
		if (start.notNil) {
			end = lines [line + 1] ? {view.string.size};
			view.select(start, end-start);
		}
	}

	*prGetIndexOfListener {
//		_TextWindow_GetIndexOfListener
		^Document.allDocuments.indexOf(listener)
	}

	initLast {
		this.prGetLastIndex;
		if(dataptr.isNil,{^nil});
		this.prAdd;
	}

	isClosed { ^window.isClosed } 
}

+CocoaDocument {
	isClosed { ^dataptr.isNil }
}

/*
SwingDocument.stop
GUI.cocoa;
Document.implementationClass = CocoaDocument;


SwingOSC.default.boot;
JSCTextView.verbose = false
GUI.swing;
Document.implementationClass = SwingDocument;

SwingDocument.go
b = SwingDocument.open("/Applications/SuperCollider3/build/SCClassLibrary/GSched.sc")

c = SwingDocument.open("/Applications/SuperCollider3/build/SCClassLibrary/syntax colorize.rtf")

a = SwingDocument.open("/Applications/SuperCollider3/build/Help/BinaryOps/addition.html")
a = SwingDocument.open("/Applications/SuperCollider3/build/Help/UnaryOps/dbamp.html")
b.syntaxColorize
b.view.font = JFont("Monaco", 12)


a.openCodeFile("SinOsc")
a.methodReferences(\ar)
a.methodTemplates(\ar)
a.cleanGremlin


g.boot;

(
w = JSCWindow.new;
t = JSCTextView( w, w.view.bounds.insetBy( 4, 4 ))
	.resize_(5 )
	.hasVerticalScroller_( true )
	.autohidesScrollers_( true )
	.focus( true );
w.front;
)

(
fork { var fc, tmpFile, fos, dos;
	fc = JavaObject.getClass( "java.io.File", g );
	tmpFile = fc.createTempFile__( "scl", ".html" );
	fos = JavaObject( "java.io.FileOutputStream", g, tmpFile );
	dos = JavaObject( "java.io.DataOutputStream", g, fos );
	dos.writeBytes( "<HTML><BODY><IMG SRC=\"http://www.sciss.de/swingOSC/application.png\"></BODY></HTML>" );
	fos.close;
	t.open( tmpFile.getAbsolutePath_.asString );
	t.onClose = { tmpFile.delete; tmpFile.destroy };
	dos.destroy; fos.destroy; fc.destroy;
}
)


*/