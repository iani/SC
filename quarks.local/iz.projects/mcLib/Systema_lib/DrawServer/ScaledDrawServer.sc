ScaledDrawServer : DrawServer { //extended from quark: SCDrawServer //still quick hack
	
	classvar count=0;
	
	var <scaledView, <viewFollows, <>hideCursor=false;
	var <>rate, active, <background, <refresh;
	var <>frameN=0, oldDrawBounds;
	
	//still to clean up:
	//var queue, orch, responder, resp2, func, ndict, perc, clear;
	//var <id, <drawCmd, <trigCmd; //mc
	
/*
ScaledDrawServer.new
*/
	*new { arg parent, bounds, fromBounds, rate=25.0, viewFollows=false, refresh=true,
		background=Color.black, orch, addr=nil,demand=false, name;
		^this.basicNew(name ? \scaledDrawServer)
			.init(orch, addr, rate, bounds, fromBounds, background, demand, refresh, parent, viewFollows);
	}
	init { arg dict, addr, fRate, bounds, fromBounds, color, demand, fresh, parent, isFollow; //sill a mess
		rate = fRate;
		active = false;
		background = color;
		refresh = fresh;
		
//count = count + 1;
//drawCmd = ("draw" ++"/"++ count).asSymbol;
//trigCmd = ("drawTrig" ++"/"++ count).asSymbol;
//		queue = Array.new;
//		orch = dict;
//		clear = 0;
		
		viewFollows = isFollow; // boolean needed by initView
		# parent, scaledView, view = this.initView(parent, bounds, fromBounds);
		view.onClose_({ active = false;
			//responder.remove; resp2.remove;
		});
		this.viewFollows_(isFollow); // set resize mode
		
//		if(fullscreen == true, {
//			window = Window.new(name, Rect(200, 200, width, height), resizable: false, 
//				border: false).front.fullScreen;
//			view = UserView(window, Window.screenBounds()).background_(bgcolor)
//				.clearOnRefresh_(refresh);
//			if(hideCursor == true, { SCNSObject("NSCursor", "hide"); });
//		},{
//			parent ?? { window = Window.new(name, Rect(200, 200, width, height), 
//					resizable: true).front;
//				parent = window.view };
//			//scaledView = ScaledUserView(parent, parent.bounds, parent.bounds) //choose either one
//			scaledView = ScaledUserViewContainer(parent, parent.bounds, parent.bounds)
//				.autoRefresh_(true); // .background_(bgcolor);
//			view = scaledView.view;
//			this.viewFollows_(isFollow);
//			drawEnvir.parent.putAll( (scaledView: scaledView) );
//			view.clearOnRefresh_(refresh);
//		});
//		// bounds = view.bounds;
////view.background_(bgcolor);
//		//window.view.keyDownAction_({ arg view, char, mod, uni, key; key.postln; char.postln; if(key == 3, { "it was an f!".postln; }); });
//		view.onClose_({
//			active = false;
//			window !? { window.endFullScreen };
//			SCNSObject("NSCursor", "unhide");
//			responder.remove;
//			resp2.remove;
////"finished!".postln;
//			});


			
//		if(demand == false, {
//			responder = OSCresponderNode(addr, drawCmd, { arg time, resp, msg; 
//				this.addToQueue(msg); }).add;
//			resp2 = OSCresponderNode(addr, trigCmd, { arg time, resp, msg; 
//				this.addToQueue2(msg); }).add;
//		},{
//			responder = OSCresponderNode(addr, drawCmd, { arg time, resp, msg; 
//				this.drawOneFrame(msg); }).add;
//			resp2 = OSCresponderNode(addr, trigCmd, { arg time, resp, msg; 
//				this.drawOneFrame2(msg); }).add;
//		});
		
	}
	initView {|par, bounds, fromBounds, canvasBounds|
		var win, scaledV, v;
		bounds = bounds ?? { Rect(200, 200, 500, 500) };
		par = par ?? { Window.new(name, bounds).front.view };
		//scaledV = ScaledUserView(parent, parent.bounds, parent.bounds) //choose either one
		//scaledV = ScaledUserViewContainer(par, par.bounds) // fromBounds always default at init
		scaledV = ScaledUserCanvasContainer(par, par.bounds, canvasBounds: canvasBounds)
			.autoRefresh_(true).autoRefreshActions_(true) // true is the default anyway
			.background_(background, false)	// false => do not refresh
			.beforeDrawFunc_({|view| this.doBeforeDraw(view) }, false)
			//.drawFunc_(this.getDrawFunc, false) 
			//.unscaledDrawFunc_(this.getUnscaledDrawFunc, false)
			.refreshAction_({|view| this.getDrawFunc.value; this.getDebugFunc(view).value })
		; 
		if (scaledV.isKindOf(ScaledUserCanvasContainer)) { scaledV.updateSliders }; //start correctly
		scrollersOn = true; 
		v = scaledV.view; v.focus;
		if (viewFollows) { scaledV.fromBounds = oldDrawBounds = v.bounds //compansate for scrollers!
		}{ fromBounds !? { scaledV.fromBounds = fromBounds } };
		drawEnvir.parent.putAll((scaledView: scaledV, view: v)); 
		v.clearOnRefresh_(refresh);
		this.postViewInit(scaledV);
		^[par, scaledV, v]
	}
	background_ {|color|
		background = color;
		scaledView.background_(background)
	}
	refresh_ {|boolean|
		refresh = boolean;
		view.clearOnRefresh_(refresh)
	}
	viewFollows_ {|boolean|
		if (boolean) { scaledView.resize = 5 } { scaledView.resize = 1 };
		viewFollows = boolean
	}

	scrollersOn_ {|boolean|
		super.scrollersOn_(boolean);
		if (actionView.isKindOf(ScaledUserCanvasContainer)) {
			actionView.scaleHEnabled_(scrollersOn).scaleVEnabled_(scrollersOn); 
			actionView.moveHEnabled_(scrollersOn).moveVEnabled_(scrollersOn);
			// actionView.view.refresh; // this does not change little delay in FullScreen mode ?! 
		}
	}
	rationalZoomBySmaller {
		if (actionView.scaleH < actionView.scaleV) { actionView.scaleV_(actionView.scaleH, false)
		}{ actionView.scaleH_(actionView.scaleV, false) }
	}
	rationalZoomByBigger {
		if (actionView.scaleH > actionView.scaleV) { actionView.scaleV_(actionView.scaleH, false)
		}{ actionView.scaleH_(actionView.scaleV, false) }
	}
//---------------------------------------		
//	addToQueue {|msg|
//		var dict; //msg.postln;
//		if(msg[1] == \clear) { clear = 1 }{
//			if(msg.size > 3) { dict = Dictionary.new();
//				((msg.size-3)/2).do{ arg j; dict.put(msg[j*2+3], msg[j*2+4]) } };
//			queue = queue.add([ 0, (frameRate*msg[2]).asInteger, msg[1], dict]);
////mc: this should add to the drawTree with all addAction flexibility !!
//		}
//	}
//			
//	drawOneFrame { arg msg;
//		var dict = Dictionary.new();
////msg.postln;
//		if(msg.size > 3, { ((msg.size-3)/2).do({ arg j; dict.put(msg[j*2+3], msg[j*2+4]); }); });
//		func = orch.at(msg[1]);
//		ndict = dict;
//		perc = msg[2];
//		scaledView.drawFunc = {
//			if(msg[1] == \clear, { Pen.fillColor = bgcolor; Pen.fillRect(bounds); });
//			func.value(perc, ndict);
//			};
//		{ scaledView.refresh; }.defer;
//	}
//
//	addToQueue2 { arg msg;
//		var dict;
////msg.postln;
//		if(msg[2] == 0, { clear = 1; },
//			{
//			if(msg.size > 4, { dict = Array.new();
//							(msg.size-4).do({ arg j;
//							dict = dict.add(msg[j+4]);
//							});
//						});
//			queue = queue.add([ 0, (frameRate*msg[3]).asInteger, msg[2], dict]);
//			});
//	}
//		
//	drawOneFrame2 { arg msg;
//		var dict = Array.new();		
////msg.postln;
//		if(msg.size > 4, { dict = Array.new();
//						(msg.size-4).do({ arg j;
//							dict = dict.add(msg[j+4]);
//							});
//						});
//		func = orch.at(msg[2]);
//		ndict = dict;
//		perc = msg[3];
//		scaledView.drawFunc = {
//			if(msg[2] == 0, { Pen.fillColor = bgcolor; Pen.fillRect(bounds); });
//			func.value(perc, ndict);
//			};
//		{ scaledView.refresh; }.defer;
//	}
//---------------------------------------	
	
	run {
		frameN = 0;
		active = true;
		SystemClock.sched(0.0, {  //SystemClock ?? -> ok because time is not frames !!
			if(active == true) { 
				frameN = frameN +1; 
				{ if (active && isPause.not) { actionView.refresh } }.defer; 
		
//				scaledView.drawFunc = {
//					var removeThese = Array.new();
//					
//					if(clear == 1, { Pen.fillColor = bgcolor; Pen.fillRect(bounds); clear = 0; });
//					queue.do({ arg it, i;
//						if(it[0] == (it[1]-1), { removeThese = removeThese.add(i.asInteger); });
//						(orch.at(it[2])).value(it[0]/(it[1]-1), it[3]);
//						it[0] = it[0] + 1;
//					});
//					removeThese.reverse.do({ arg it; queue[it][3] = nil; queue.removeAt(it); });
//				};
////				{ if (active) { scaledView.refresh } }.defer;
			
			
			rate.reciprocal }{ nil }
		});
	}
	active { ^active }
	active_{|boolean|
		if (boolean && active.not) { active = boolean; this.run } { active = boolean }
	}
	
	doBeforeDraw { // |v| // this.logln("doOnRefresh" + v);
		drawEnvir.clear.putAll((
			frameN: frameN, post: List[ [\postln, "post on:"] ]
			,scale: actionView.scale, move: actionView.move // scaleDelta & trans to compensate:
			,scaleDelta: actionView.scaleDelta * -1, trans: actionView.totalTranslation * -1
		));
		if (viewFollows) { if (oldDrawBounds != actionView.drawBounds) {
				actionView.fromBounds = oldDrawBounds = actionView.drawBounds }}
	}
	getDrawFunc { 
		^{ drawTree.envirRealmDoTree(drawEnvir, actionView) } 
	}
	getDebugFunc {|v| 
		^{ if (isDebug) { Pen.color = Color.grey(0.2); Pen.fillRect( v.drawBounds ) };
			debugTree.envirRealmDoTree(drawEnvir, actionView) }
	}	
// even temporal nodes are possible with the right doTree func and args like frameN ...
// then self removal from tree should be easy ...
	
	fullScreen_ {|boolean|
		var scrollersOnState, pauseState;
		if (isFullScreen && boolean.not) { fullWin.close };
		if (isFullScreen.not && boolean) { //this.logln("start:");
			// pauseState = isPause; isPause = true; 
			isFullScreen = true; scrollersOnState = scrollersOn;
			fullWin = Window.new(border: false).front.fullScreen
				.onClose_({ // "onClose".postln;
					fullWin = nil; SCNSObject("NSCursor", "unhide");
					// backcopying of scale and move could be done here
					actionView = scaledView; scrollersOn = scrollersOnState; isFullScreen = false;
					drawEnvir.parent.putAll((scaledView: scaledView, view: view))
				});
			fullWin.view.background_(background);
			if(hideCursor == true) { SCNSObject("NSCursor", "hide") };
			{ 
				this.initView(fullWin, canvasBounds: scaledView.canvasBounds);

				actionView.scale_(scaledView.scale, false);
				actionView.oneShotRefreshAction = { //this.logln("oneShot:");
					actionView.move_(scaledView.move, false) };

				// support customised sliders:
				actionView.scaleSliders.do{|sl, i| 
					sl.knobColor_(scaledView.scaleSliders[i].knobColor);
					sl.background_(scaledView.scaleSliders[i].background) };
				actionView.moveSliders.do{|sl, i| 
					sl.knobColor_(scaledView.moveSliders[i].knobColor);
					sl.background_(scaledView.moveSliders[i].background) };
		//"done".postln;		
				// isPause = pauseState; 
			}.defer(0.4);
		}
 	}
}

ScaledActionField : ActionField {
	convert {|sv, point| ^sv.convertBwd( *(point - sv.drawBounds.origin).asArray ).asPoint }
}
ScaledDragField : DragField {
	convert {|sv, point| ^sv.convertBwd( *(point - sv.drawBounds.origin).asArray ).asPoint }
}

MovedActionField : ActionField {
	convert {|sv, point| ^point - sv.drawBounds.origin - sv.totalTranslation }
}
MovedDragField : DragField {
	convert {|sv, point| ^point - sv.drawBounds.origin - sv.totalTranslation }
}

MovedXActionField : ActionField {
	convert {|sv, point| ^point - sv.drawBounds.origin - [sv.totalTranslation[0], 0] }
}
MovedXDragField : DragField {
	convert {|sv, point| ^point - sv.drawBounds.origin - [sv.totalTranslation[0], 0] }
}

MovedYActionField : ActionField {
	convert {|sv, point| ^point - sv.drawBounds.origin - [0, sv.totalTranslation[1]] }
}
MovedYDragField : DragField {
	convert {|sv, point| ^point - sv.drawBounds.origin - [0, sv.totalTranslation[1]] }
}

MovedDeltaActionField : ActionField {
	convert {|sv, point| ^point - sv.drawBounds.origin - sv.totalTranslation + sv.scaleDelta }
}
MovedDeltaDragField : DragField {
	convert {|sv, point| ^point - sv.drawBounds.origin - sv.totalTranslation + sv.scaleDelta }
}

MovedDeltaYActionField : ActionField {
	convert {|sv, point| ^point - sv.drawBounds.origin - sv.totalTranslation + [0, sv.scaleDelta[1]] }
}
MovedDeltaYDragField : DragField {
	convert {|sv, point| ^point - sv.drawBounds.origin - sv.totalTranslation + [0, sv.scaleDelta[1]] }
}

+ NetAddr {
	sendAdjMsg { arg ... args;
		args = args.insert(1, 0);
		this.sendMsg(*args);
	}
}


/*
all that ScaledUserView does is this:

	1.	viewRect = this.viewRect; 
		== ^view.drawBounds.transformFromRect( view.drawBounds, fromBounds, keepRatio, 
				scaleH@scaleV, moveH@moveV ).insetBy( inset, inset );

	2.	Pen.transformToRect( v.drawBounds, fromBounds, keepRatio, 
				scaleH@scaleV, moveH@moveV );
						
	==> all core functionality is in file: extPen-transformToRect.sc
	
	3. see also: viewRect_
		-> just adjusting scale and move to cut-out given by arg rect
	
	viewRect_ { |rect, refreshFlag| 
		var scale, move, msc;
		
		rect = rect.asRect;
		
		scale = (fromBounds.extent / rect.extent).asArray;
		move = (rect.leftTop - fromBounds.leftTop);
		msc = (fromBounds.extent - rect.extent).asArray;
		
		move = [ 
			move.x.linlin( 0, msc[0], 0, 1 ), 
			move.y.linlin( 0, msc[1], 0, 1 )
			];
			
		if( keepRatio )
			{ this.scale_( scale.minItem, false ); }
			{ this.scale_( scale, false ); };
		this.move_( move, false );
		
		this.refresh( refreshFlag );
	}
*/
+ ScaledUserView {

	refresh {|flag| //was: |flag = true| -> so has no effect on autoRefresh !!!
		flag = flag ? autoRefresh;
		if( flag == true ) {
			view.refresh;
			// refreshAction.value( this ); // won't work since this isn't the actual view
			 }; 
		}
	
	scaleFromBounds { ^(view.bounds.extent / fromBounds.extent).asArray } //mc
	
	translateScaleX {|item|
		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
			scaleH@1, moveH@1 )
	}
	
	translateScaleY {|item|
		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
			1@scaleV, 1@moveV );
	} 
	
	resize { ^view.resize }
	resize_ { |val| view.resize_( val ) }
	
	keyUpAction { ^view.keyUpAction }
	keyUpAction_ { |newAction| view.keyUpAction_( newAction ); }
}

+ ScaledUserViewContainer {
	
	parent { ^window ? composite }
	
	*new { |parent, bounds, fromBounds, viewOffset|
		var resize;
		
		viewOffset = viewOffset ? [1,1];
		case { viewOffset.class == Point }
			{ viewOffset = [ viewOffset.x, viewOffset.y ]; }
			{ viewOffset.size == 0 }
			{ viewOffset = [ viewOffset, viewOffset ]; }
			{ viewOffset.size == 1 }
			{ viewOffset = viewOffset ++ viewOffset; };
		
		if( parent.isNil or: {parent.isString} )
		 	{ 
			parent = Window(parent ? "ScaledUserView", bounds).front;
			resize = 5;
			bounds = (bounds ? defaultBounds).asRect;
			bounds !? { bounds = bounds
					.moveTo(*viewOffset)
					.resizeBy(*viewOffset.neg)
					.resizeBy(-2,-2) };
			};
			
		bounds = (bounds ? defaultBounds).asRect;
		fromBounds = (fromBounds ? defaultFromBounds).asRect;
		^super.new.myDefaults.init( parent, bounds, fromBounds, resize).mySliders; //mc
	}
	myDefaults {
		maxZoom = 8; minZoom = 1/4;
		sliderKnobColor = Color.blue; sliderBackColor = Color.grey;
		sliderWidth = 6; sliderSpacing = 2; scaleSliderLength = 100; 
	}
	mySliders {
		scaleSliders[0].mouseDownAction_({|slider, x, y, modifiers, buttonNumber, clickCount| 
			// this.logln("clickCount" + [this, slider, clickCount]); 
			 if (clickCount > 1) { this.scaleH_(1.0) };
		});
		scaleSliders[1].mouseDownAction_({|slider, x, y, modifiers, buttonNumber, clickCount| 
			 if (clickCount > 1) { this.scaleV_(1.0) };
		});
		
		moveSliders[0].mouseDownAction_({|slider, x, y, modifiers, buttonNumber, clickCount|
			 if (clickCount == 2) { this.moveH_(0.5) }; 
			 if (clickCount == 3) { this.moveH_(1) };
			 if (clickCount > 3) { this.moveH_(0) };
		});
		moveSliders[1].mouseDownAction_({|slider, x, y, modifiers, buttonNumber, clickCount| 
			 if (clickCount == 2) { this.moveV_(0.5) }; 
			 if (clickCount == 3) { this.moveV_(1) };
			 if (clickCount > 3) { this.moveV_(0) };
		});
	}
}