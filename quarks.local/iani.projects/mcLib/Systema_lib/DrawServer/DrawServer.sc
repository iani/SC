DrawServer {
	classvar <all, uniqueSuffix="_V";
	classvar <archiveDir, <archiveFolder="DrawDefs", fileEnding="drawDefs.scd";
	classvar defaultFolder="DEFAULT", isLdPost=true;
	
	var <name, <drawTree, <debugTree;
	var <parent, <view, <isFullScreen=false, <fullWin, <scaleMode=0;
	var <drawEnvir; //stays always the same -> so nice things can be done by use of proto and parent
	var <isPost = false, <isDebug = false, <>isPause=false, <scrollersOn=true;
	var <actionView, <actionChars, <>dragFields, <>actionFields;
	var <>beginDragAction; //passed x, y that we don't get from SCView
	 
	
	*initClass { 
		all = IdentityDictionary.new;
		archiveDir = this.class.filenameSymbol.asString.dirname +/+ archiveFolder;
		StartUp.add({ this.loadDir(archiveDir +/+ defaultFolder) });
	}
//load support quickly hacked for defaults -> should get more sophisticated later...
	*loadDir {|dir, post|
		post = post ? isLdPost;
		if (dir.notNil) { ^this.prLoadFolder(PathName(dir.asString), fileEnding, post)
		}{ File.openDialog(nil, {|dir| ^this.prLoadFolder(PathName(dir), fileEnding, post)}) }
	}
	*prLoadFolder{|pathN, ext, post|
		var return = List.new;
		pathN.filesDo{|pathName| 
			if (pathName.extensionAt(fileEnding.findAll(".").size+1) == ext) {
				return.add(this.prLoadFile(pathName, ext, post))
			}
		}
		^return.flat
	}
	*prLoadFile {|pathN, ext, post| 
		var file, res; 
		if (ext == fileEnding) { 
			res = pathN.fullPath.load;
			if (post) {this.prPstL(pathN, ext)};
			^res
		}
	}
	*prPstL {|pathN, ext| ("*" + ext +"* loaded from:" + pathN.fullPath).postln }

	
	*basicNew {|name| ^super.newCopyArgs(this.uniqueCopyNameSuffix(name ? \drawServer)).basicInit }
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
	basicInit { 
		all.put(name, this); //this.logln("all:" + all);
		
		drawTree = DrawTree((name++\DrawTree).asSymbol, this);
		debugTree = DrawTree((name++\DebugTree).asSymbol, this);
		
		drawEnvir = Event.new(parent: (
			drawServer: this, scaleMode: scaleMode, isDebug: isDebug, isPost: isPost) );
		DrawDef.at(\post) !? { DrawFunc(\post, drawEnvir, debugTree.tail) };
		
		actionChars = IdentityDictionary.new;
		dragFields = List.new;
		actionFields = List.new;
	}
	postViewInit{|v|
		actionView = v ? view;
		
		this.initKeyActions;
		this.initMouseActions;
		
//		beginDragAction = {|v, x, y, m| this.logln("[v, x, y, m]:" + [v, x, y, m]);
//			v.dragLabel = "play".postln; 
//			nil 
//		};
	}
	isPost_{|boolean|
		isPost = boolean;
		drawEnvir.parent.put(\isPost, isPost)
	}
	isDebug_{|boolean|
		isDebug = boolean; this.isPost_(boolean);
		drawEnvir.parent.put(\isDebug, isDebug) 	
	}
	scrollersOn_ {|boolean| scrollersOn = boolean }
		
	asFuncTarget {|funcHolder| 
		funcHolder.class.name.switch(
			//,\Meta_DrawFunc, 		{ ^drawTree.defaultFuncGroup }
			\Meta_DrawFuncGroup, 	{ ^drawTree.defaultFuncGroup }
			,\Meta_DrawDebugGroup, 	{ ^debugTree.defaultFuncGroup }
			, { ("could not find funcHolder:" + funcHolder + "asFuncTarget").warn } )
	}
	initKeyActions {
		var keysStateDict = IdentityDictionary.new, cmdDel = false;
		actionView.addAction( 
			{|v, char, mod, uniC, keyC| var ascii = char.asInt;
				//this.logln("v, char, mod, uniC, keyC:" + [v, char, mod, uniC, keyC]);
				case 
				{ mod == 524576 } { keyC.switch( // alt
					KC(\f), { this.fullScreen_(isFullScreen.not) } // f
					,KC(\z), { this.rationalZoomBySmaller } // z
					,KC(\h), { this.scrollersOn_(scrollersOn.not) } // h
					,KC(\p), { isPause = isPause.not } //p
					,KC(\d), { this.isPost_(isPost.not) } //d
					,KC(\0), { this.scaleMode_(0) }
					,KC(\1), { this.scaleMode_(1) }
					,KC(\2), { this.scaleMode_(2) }
					,KC(\3), { this.scaleMode_(3) }
					,KC(\4), { this.scaleMode_(4) }
					,KC(\5), { this.scaleMode_(5) }
					,KC(\6), { this.scaleMode_(6) }
					,KC(\7), { this.scaleMode_(7) }
					,KC(\8), { this.scaleMode_(8) }
					,KC(\9), { this.scaleMode_(9) } ) }
				{ mod == 655650 } { keyC.switch( // shift-alt
					KC(\z), { this.rationalZoomByBigger } // Z
					,KC(\d), { this.isDebug_(isDebug.not) }) } //D
				// special cases no keyUpAction -> always dawn and repeat
				{ mod == 1048840 && keyC == 51 && cmdDel.not} { // !!! always doubleTrigger !!!!
					cmdDel = true; {cmdDel = false}.defer(0.1);
					actionChars[\cmdDel] !? { actionChars[\cmdDel].first.value(true, true) } }
				{ mod == 11534600 && keyC == 123 } { actionChars[\cmdArrowLeft] !? {
						actionChars[\cmdArrowLeft].first.value(true, true) } }
				{ mod == 11534600 && keyC == 124 } { actionChars[\cmdArrowRight] !? {
					actionChars[\cmdArrowRight].first.value(true, true) } 
				}
				// action chars
				{ (mod == 256) || (mod == 131330) } { //none, shift only!
					this.checkChars(char, true, keysStateDict[char] ? false); 
					keysStateDict.put(char, true) }
			}, \keyDownAction);
		actionView.addAction( 
			{|v, char, mod, uniC, keyC| 
				//this.logln("v, char, mod, uniC, keyC:" + [v, char, mod, uniC, keyC]);
				if ((mod == 256) || (mod == 131330)) { //none, shift only!
					this.checkChars(char, false, false); keysStateDict.put(char, false) }
			}, \keyUpAction)
	}
	initMouseActions {
		var field, prevX, prevY, directionX=true, directionY=true, delta0, deltaXY; // mouse service
		var currMod, currX, currY, dragX=0, dragY=0, prevDragField, dragNode; // drag related
		
		actionView.getParents.last.findWindow.acceptsMouseOver_(true);
		actionView.addAction(
			{|v, x, y| //this.logln("mouseOver:" + [x,y]);
				dragX = x; dragY = y
			},\mouseOverAction);
		actionView.addAction( 
			{|v, x, y, mod, bN, cC| //this.logln("v, x, y, mod, bN, cC:" + [v, x, y, mod, bN, cC]); 
				v.dragLabel = ""; beginDragAction = nil;
				currX = x; currY = y; currMod = mod; // beginDragAction support
				if (mod.isCmd) { this.checkActionFields(actionView, x@y, mod, bN, cC)
				}{ field = this.checkActionFields(actionView, x@y, mod, bN, cC) }
			},\mouseDownAction);
		actionView.addAction( //not called when cmd was pressed!
			{|v, x, y, mod| //this.logln("move: v, x, y, mod" + [v, x, y, mod]);
				// deltaXY service
				delta0 ?? { # prevX, prevY = delta0 = [x, y] };
				if (directionX) {  
					if(x >= prevX) { prevX = x
					}{ directionX = false; delta0[0] = prevX = x }
				}{
					if(x <= prevX) { prevX = x
					}{ directionX = true; delta0[0] = prevX = x }
				};
				if (directionY) {  
					if(y >= prevY) { prevY = y
					}{ directionY = false; delta0[1] = prevY = y }
				}{
					if(y <= prevY) { prevY = y
					}{ directionY = true; delta0[1] = prevY = y }
				};
				deltaXY = [x, y] - delta0;
								
				field !? { field.moveAct(actionView, x@y, mod, deltaXY) };
			}, \mouseMoveAction);
		actionView.addAction( //not called when cmd was pressed!
			{|v, x, y, mod| //this.logln("up: v, x, y, mod:" + [v, x, y, mod]);
				field !? { field.upAct(actionView, x@y, mod); field = nil };
				dragNode.remove; dragNode = prevDragField = nil //killer: if drag released out of win
			}, \mouseUpAction);
		// drag	
		actionView.addAction(
			{|v, m| // this.logln("v, m:" + [v, m]);
				currX = currY = nil // currMod = m does not work here  -> put into mouseDownAction
			}, \keyModifiersChangedAction);
		actionView.beginDragAction = { this.beginDragAction.value(actionView, currX, currY, currMod) };
//		actionView.addAction( // does not work !! -> same with beginDragAction
//			{|v, x, y| this.logln("v, x, y:" + [v, x, y]);
//				this.canReceiveDragHandler.value(v, x, y) ; true
//			}, \canReceiveDragHandler);
		actionView.canReceiveDragHandler_({// |v, x, y| this.logln("v, x, y:" + [v, x, y]); // no x,y !
			var field = this.checkDragFields(actionView, dragX@dragY);
			if (field.notNil) { if (field != prevDragField)  { 
				dragNode.remove; prevDragField = field;
// switch DrawFunc by class of DragField or convert !!!
				dragNode = DrawFunc(\dragRect_draw, (rect: field.rect), drawTree.tail, 1) };
			true }{ dragNode.remove; dragNode = prevDragField = nil; false }
		});
		actionView.addAction(
			{|v, x, y| //this.logln("check drag:" + [x,y, dragX, dragY]); 
				prevDragField.receive(actionView, dragX@dragY); 
				dragNode.remove; dragNode = prevDragField = nil
			}, \receiveDragHandler);
	}
	scaleMode_{|n|
		scaleMode = n;
		drawEnvir.parent.put(\scaleMode, n)
	}
	// action chars
	checkChars {|char, type, repeat| actionChars[char].detect{|field| field.value(type, repeat) } }
	addActionChar {|char, action|
		var actionChar = ActionChar(char, action);
		actionChars[char] ?? { actionChars[char] = List.new };
		actionChars[char].addFirst(actionChar);
		^actionChar
	}
	removeActionChar {|actionChar| ^actionChars[actionChar.char].remove(actionChar) }
	
	// drag field
	checkDragFields{|v, point|
		var obj = View.currentDrag;
		^dragFields.reverse.select{|field| field.contains(point, v) }
		.detect{|field| field.canDrag(v, obj, point) }//if field return true -> stop search
	}
	addDragField {|class, rect, canDragFunc, action|
		var field = class.new(rect, canDragFunc, action);
		dragFields.add(field);
		^field
	}
	removeDragField {|field| ^dragFields.remove(field) }
	
	// action fields
	checkActionFields{|v, point, mod, bN, cC|
		^actionFields.reverse.select{|field| field.contains(point, v) }
		.detect{|field| field.downAct(v, point, mod, bN, cC) }//if field return true -> stop search
	}
	addField {|class, rect, down, move, up|
		var actionField = class.new(rect, down, move, up);
		actionFields.add(actionField);
		^actionField
	}
	removeField {|field| ^actionFields.remove(field) }
	
	clearActions {
		actionChars = IdentityDictionary.new;
		dragFields = List.new;
		actionFields = List.new;
	}
}
ActionChar {
	var <char, <action;
	*new{|char, action| ^super.newCopyArgs(char, action) }
	value {|type, repeat| ^action.value(type, repeat) ?? false }
}

DragField {
	var <rect, <>canDragFunc, <>action;
	*new{|rect, canDragFunc, action| ^super.newCopyArgs(rect, canDragFunc, action) }
	contains {|point, aV| ^rect.contains(this.convert(aV, point)) }
	canDrag {|aV, obj, point| ^canDragFunc.value(aV, obj, point) ?? false }
	receive {|aV, point| action.value(aV, View.currentDrag, point) }
	convert {|aV, point| ^point } // overwrite this in subclasses 
}

ActionField {
	var <>rect, <>down, <>move, <>up, fwdArgs;
	*new{|rect, down, move, up| ^super.newCopyArgs(rect, down, move, up) }
	contains {|point, aV| ^rect.contains(this.convert(aV, point)) }
	downAct {|aV, point, mod, bN, cC|
		fwdArgs = down.value(aV, cC, mod, bN, this.convert(aV, point), point);
		^if (fwdArgs == false || fwdArgs.isNil) {false} {true }
	}
	moveAct {|aV, point, mod, deltaXY|
		fwdArgs = move.value(aV, fwdArgs, mod, this.convert(aV, point), point, deltaXY)
	}
	upAct {|aV, point, mod|
		up.value(aV, fwdArgs, mod, this.convert(aV, point), point)
	}
	convert {|aV, point| ^point } // overwrite this in subclasses 
}



