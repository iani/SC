// wslib 2006
// scaled SCUserView

// 2007: altered for SC3.2 compatibility
// 2009: altered for SwingOsc compatibility

// TODO: correct half pixel problem for mouse feedback

//adopted by mc 2011
// requires exPenCanvas-transformToRect.sc
// 2do: make an optimasised version without fromBounds option! !!!!!!!!!!!!!!!!!!!!!!!!!!!!

//still work to do...
	// unscaledDrawFunc:  0@0 == leftTop of view (not window)

ScaledUserCanvas { 

	classvar <>defaultGridColor;
	var <gridColor;
	
	var <view, <>fromBounds;
	var <scaleH = 1, <scaleV = 1, <moveH = 0.5, <moveV = 0.5;
	var <>gridSpacingH = 0, <>gridSpacingV = 0; // no grid when spacing == 0
	var <>gridMode = \blocks;
//	var <>mouseDownAction, <>mouseMoveAction, <>mouseUpAction;
//	var <>mouseOutOfBoundsAction;
	var <drawFunc, <unscaledDrawFunc, <beforeDrawFunc;
	var <>autoRefresh = true;
//	var <>autoRefreshMouseActions = true;
	
	var <>refreshAction;
	
	var <background;
	var <>keepRatio = false; 
//mc	
	var <>oneShotRefreshAction;
	var <canvasBounds, <>savedOrigin; //savedOrigin necessary when win is risized by hand
	var <>autoRefreshActions = true, autoRefreshFunc;
	var realmFuncs, realmArgs;

		
	canvasBounds_{|rect, refreshFlag| //mc
		canvasBounds = rect;
		this.move2Origin(savedOrigin, refreshFlag); // set correct move
	}
	getOriginFromMoves { // identical with this.viewRect.origin; ? -> NO
		^view.drawBounds.originFromMove(this.totalBounds, moveH@moveV);
	}
	saveOrigin { savedOrigin = this.getOriginFromMoves }
	
	move2Origin{|origin=0, refreshFlag|
		// this.logln("move2Origin:" + [origin, view.drawBounds, this.totalBounds]);
		this.move_( ((moveH@moveV).transformMove4Origin(view.drawBounds, this.totalBounds, origin)),
			refreshFlag);
	}
	move2OriginX {|x, refreshFlag|
		var origY = this.getOriginFromMoves.y;
		this.move2Origin(x@origY, refreshFlag);
	}
	move2OriginY {|y, refreshFlag|
		var origX = this.getOriginFromMoves.x;
		this.move2Origin(origX@y, refreshFlag);
	}
	
	totalBounds {		
		/* these are now all identical: -> just scale viewBounds if isFollow!!!!
		this.logln("scaleByRatio:" +  view.drawBounds.scaleByRatio(this.scale, keepRatio), lfB:2 ); 
		Rect.new.scaleCenteredIn(view.drawBounds, keepRatio, this.scale, 0@0).postln
		this.transScale(view.drawBounds).postln;
			!but! there is a scale delta (as things get bigger ;-) to compansate: s.scaleDelta
		*/
		^canvasBounds.union(view.drawBounds.scaleByRatio(this.scale, keepRatio));
	}
	totalTranslation {
		^view.drawBounds.originFromMove(this.totalBounds, this.move).asArray.neg
	}
	
	doInRealm{|realm, func| // this.logln("realm, func:" + [realm, func]);
		if (realmFuncs[realm].notNil) { realmFuncs[realm].value(func) } { func.value }
	}

	makeRealmFuncs { 
		// possible optimizations if drawServer isFollow: 
			// viewScale = scale; Pen.width =1; cut unsued realms
		
		// var viewTrans = [view.drawBounds.left, view.drawBounds.top];
		// var initFunc = {|func| Pen.use{ Pen.translate(*viewTrans); func.value } };
		
		var moveFunc = {|func| Pen.use{ Pen.translate(*realmArgs.trans); func.value } };
		var moveXFunc = {|func| Pen.use{ Pen.translate(*realmArgs.trans*[1, 0]); func.value } };
		var moveYFunc = {|func| Pen.use{ Pen.translate(*realmArgs.trans*[0, 1]); func.value } };
		
		var scaleFunc = {|func| Pen.use{ Pen.translate(*realmArgs.scaledTrans);
			Pen.scale(*realmArgs.scale); Pen.width = realmArgs.scaledPenWidth; func.value } };
		var scaleXFunc = {|func| Pen.use{ Pen.translate(*realmArgs.scaledTrans); 
			Pen.scale(realmArgs.scale[0], 1); Pen.width = realmArgs.scaledPenWidth; func.value } };
		var scaleYFunc = {|func| Pen.use{ Pen.translate(*realmArgs.scaledTrans); 
			Pen.scale(1, realmArgs.scale[1]); Pen.width = realmArgs.scaledPenWidth; func.value } };
		
		realmFuncs.putAll(( 
			// calc: 	{|func| func.value } // redundant
			draw: 	{|func| func.value }
			,moved: 	{|func| moveFunc.value(func) }
			,movedX: 	{|func| moveXFunc.value(func) }
			,movedY: 	{|func| moveYFunc.value(func) }
			,scaled: 	{|func| scaleFunc.value(func) }
			,scaledX: {|func| scaleXFunc.value(func) }
			,scaledY: {|func| scaleYFunc.value(func) }
		))	
	}
	storeRealmArgs {
		realmArgs.trans = this.totalTranslation;
		realmArgs.scale = view.drawBounds.getScaleToRect(fromBounds, keepRatio, scaleH@scaleV);
		realmArgs.scaledTrans = realmArgs.trans + this.scaledTransError;
		realmArgs.scaledPenWidth = [ (fromBounds.width / view.drawBounds.width).abs,
				(fromBounds.height / view.drawBounds.height).abs ].mean
	}
	
	addAction { arg func, selector=\action; // mimic SCView addAction
		view.removeAction(autoRefreshFunc, selector);
		view.perform(selector.asSetter, view.perform(selector).addFunc(func));
		view.perform(selector.asSetter, view.perform(selector).addFunc(autoRefreshFunc))//no bubbling
	} // actually think of subclassing UserView... but what about FullScreen functionality then?
	doesNotUnderstand {|selector ... args| ^view.perform(selector, *args) } // forward to true view
	

	*initClass { 
		defaultGridColor = Color.gray.alpha_(0.25); 
		}
	
	*new { |window, bounds, fromBounds, canvasBounds|
		bounds = (bounds ? Rect(0,0,360, 360)).asRect;
		fromBounds = (fromBounds ? bounds).asRect;
		canvasBounds = (canvasBounds ? bounds).asRect;
		^super.new.fromBounds_( fromBounds ).init( window, bounds, canvasBounds);
		}
		
	*window { |name, bounds, fromBounds, viewOffset| 
			// creates a window with sliders for scale/move
		^ScaledUserCanvasContainer( name, bounds, fromBounds, viewOffset );
		}
		
	*withSliders { |window, bounds, fromBounds|
		^ScaledUserCanvasContainer( window, bounds, fromBounds );
		}
	
	init { |window, bounds, inCanvasBounds|
		view = UserView( window, bounds );
		//view.relativeOrigin_( false );
		view.background = background;

		//mc
		canvasBounds = inCanvasBounds;
		savedOrigin = 0@0;
		autoRefreshFunc = { this.refresh( autoRefreshActions ) };
		realmFuncs = (); realmArgs = ();
		this.makeRealmFuncs;

		gridColor = defaultGridColor;
		
		if( view.respondsTo( \drawBounds ).not ) // dirty - but it does the trick..
			{ view.addUniqueMethod( \drawBounds, { |vw| 
				if ( vw.relativeOrigin ) // thanks JostM !
					{ vw.bounds.moveTo(0,0) }
					{ vw.absoluteBounds; };
				})
			};
	
		view.drawFunc = { |v| //mc grid functionality still to be reenabled...
			
			var scaledViewBounds;
			var viewRect;
			var scaleAmt;
			
			//this.makeRealmFuncs;
			this.storeRealmArgs;
			
			Pen.use({	
				viewRect = this.viewRect;
				
				beforeDrawFunc.value( this );
				
				if( background.class == Color )
					{ Pen.use({ 
						Pen.color = background;
						Pen.fillRect( v.drawBounds );
						}); 
					}; 
					
				// move to views leftTop corner (only when relativeOrigin==false) :
				Pen.translate( v.drawBounds.left, v.drawBounds.top );
								
////				if( clip ) { // swing will need clip
////						Pen.moveTo(0@0);
////						Pen.lineTo(v.drawBounds.width@0);
////						Pen.lineTo(v.drawBounds.width@v.drawBounds.height);
////						Pen.lineTo(0@v.drawBounds.height);
////						Pen.lineTo(0@0);
////						Pen.clip;
////						};
//				Pen.use({
//				Pen.translate(*this.totalTranslation);
//
//				Pen.use({
//				
//					//scaleAmt = this.scaleAmt;
//					//Pen.scale( *scaleAmt );
//					
//					Pen.transformToRectCanvas(v.drawBounds, fromBounds, keepRatio, scaleH@scaleV);
//
//				
//					if( GUI.scheme.id == 'swing' && {(scaleAmt[0] != scaleAmt[1])} )
//						{ Pen.translate( 0.5, 0.5 ); }; // temp correction for swingosc half-pixel bug
//						
//					// Pen.translate( *this.moveAmt );
//					
//					/*
//					// clip:
//					if( clip && clipScaled ) { 
//						
//						// clip doesn't work with negative scaling
//						scaledViewBounds =
//							Rect.fromPoints( *([[0,0], 
//									[v.drawBounds.width,v.drawBounds.height]]
//								.collect({ |point| this.convertBwd( *point ).asPoint; }) ) );
//								
//						Pen.moveTo(scaledViewBounds.leftTop);
//						Pen.lineTo(scaledViewBounds.rightTop);
//						Pen.lineTo(scaledViewBounds.rightBottom);
//						Pen.lineTo(scaledViewBounds.leftBottom);
//						Pen.lineTo(scaledViewBounds.leftTop);
//						Pen.clip;
//						
//						};
//					*/
//					
//					// grid:
//					
//					Pen.use({
//					
//					Pen.translate( fromBounds.left, fromBounds.top );
//					
//					if( (gridSpacingV != 0) && // kill grid if spacing < 2px
//							{ (viewRect.height / v.drawBounds.height) < ( gridSpacingV / 2 ) } )
//						{	if( gridMode.asCollection.wrapAt( 0 ) === 'blocks' )
//						
//								{ 	Pen.color = gridColor.asCollection[0];
//									Pen.width = gridSpacingV;
//									
//									((0, (gridSpacingV * 2) .. fromBounds.height + gridSpacingV) 
//											+ (gridSpacingV / 2))
//										.abs
//										.do({ |item| Pen.line( 0@item, (fromBounds.width)@item ); });
//								} 
//								{  	Pen.color = gridColor.asCollection[0]; //Color.black.blend( gridColor, 0.5 );
//									Pen.width = (fromBounds.width / v.drawBounds.width).abs / scaleV; 
//									
//									(0, gridSpacingV .. (fromBounds.height + gridSpacingV))
//										.abs
//										.do({ |item| Pen.line( 0@item, (fromBounds.width)@item ); });
//								 };
//								
//							Pen.stroke;
//						};
//					
//					
//					if( ( gridSpacingH != 0 ) &&
//						 	{ (viewRect.width / v.drawBounds.width) < (gridSpacingH / 2 ) } )
//						{	if( gridMode.asCollection.wrapAt( 1 ) === 'blocks' )
//								{	Pen.color = gridColor.asCollection.wrapAt(1);
//									Pen.width = gridSpacingH;
//									
//									((0, (gridSpacingH * 2) .. fromBounds.width + gridSpacingH) 
//											+ (gridSpacingH / 2))
//										.abs
//										.do({ |item| Pen.line( item@0, item@(fromBounds.height) ); });
//								} 
//								{  	Pen.color =  gridColor.asCollection.wrapAt(1);
//									Pen.width = (fromBounds.width / v.drawBounds.width).abs / scaleH; 
//									
//									(0, gridSpacingH .. (fromBounds.width + gridSpacingH))
//										.abs
//										.do({ |item| Pen.line( item@0, item@(fromBounds.height) ); });
//								};	
//							
//							Pen.stroke;
//						};
//					});
//						
//					// drawFunc:
//					
//					// line will be 1px at current view width and scale == [1,1] 
//					Pen.width = 
//						[ (fromBounds.width / v.drawBounds.width).abs,
//						  (fromBounds.height / v.drawBounds.height).abs ].mean; 
//						 
//					Pen.color = Color.black; // back to default
//					
//					drawFunc.value( this );
//					});
//					
//				
//				
//					//Pen.use({ 
//						unscaledDrawFunc.value( this ) 
//					//});//Pen.use: shield inside settings ??
//				});
				
				//Pen.use({	
				refreshAction.value( this );
				oneShotRefreshAction.value( this );
				oneShotRefreshAction = nil; 	
				// });
				});
			};
		}
		
	refresh {|flag| //mc was: |flag = true| -> so has no effect on autoRefresh !!!
		flag = flag ? autoRefresh;
		if( flag == true ) { view.refresh } 
	}
	
	gridColor_ { |color, refreshFlag| 
		gridColor = color; 
		this.refresh( refreshFlag );
	}

	
	scaleH_ { |newScaleH, refreshFlag|
		scaleH = newScaleH ? scaleH; 
		if( keepRatio ) { scaleV = newScaleH ? scaleH; };
		this.refresh( refreshFlag );
		}
		
	scaleV_ { |newScaleV, refreshFlag|
		if( keepRatio.not )
			{ scaleV = newScaleV ? scaleV; this.refresh( refreshFlag ); };
		}
	
	scale { ^[ scaleH, scaleV ] }
	
	scale_ { |newScaleArray, refreshFlag| // can be single value, array or point
		newScaleArray = (newScaleArray ? this.scale).asPoint;
		this.scaleH_( newScaleArray.x, false );
		this.scaleV_( newScaleArray.y, false );
		this.refresh( refreshFlag );
	}
	
	moveH_ { |newMoveH, refreshFlag|
		moveH = newMoveH ? moveH; 
		this.saveOrigin; // this.logln("moveH:" + savedOrigin);
		this.refresh( refreshFlag );
		}
	
	moveV_ { |newMoveV, refreshFlag|
		moveV = newMoveV ? moveV; 
		this.saveOrigin; // this.logln("moveV:" + savedOrigin);
		this.refresh( refreshFlag );
		}
	
	move { ^[ moveH, moveV ] }
	
	move_ { |newMoveArray, refreshFlag| 
		newMoveArray = (newMoveArray ? this.move).asPoint;
		moveH = newMoveArray.x;
		moveV = newMoveArray.y;
		this.saveOrigin; //this.logln("move:" + savedOrigin);
		this.refresh( refreshFlag );
		}
		
	movePixels { // works - pixel offset from center
		var bnds;
		bnds = this.drawBounds.extent.asArray.neg;
		^this.move.collect({ |item,i|
			item.linlin( 0.5,1.5,0, bnds[i] * (this.scale[i] - 1), \none);
			});
		}
		
	movePixels_ { |newPixelsArray, limit, refreshFlag| 
		var bnds;
		limit = limit ? true;
		newPixelsArray = (newPixelsArray ? [0,0]).asPoint.asArray;
		bnds = this.drawBounds.extent.asArray.neg;
		#moveH, moveV = newPixelsArray.asPoint.asArray.collect({ |item,i|
			if( this.scale[i] != 1 ) // no change if scale == 1 (prevent nan error)
				{ item.linlin( 0, bnds[i] * (this.scale[i] - 1), 0.5, 1.5, \none); }
				{ [moveH,moveV][i] };
			});
		if( limit ) { #moveH, moveV = [ moveH, moveV ].clip(0,1) };
		this.refresh( refreshFlag );	
		}
		
	reset { |refreshFlag| scaleH = scaleV = 1; moveH = moveV = 0.5; this.refresh( refreshFlag ); }
		
	// number of grid lines:
	gridLines { ^[ fromBounds.width / gridSpacingH, fromBounds.height / gridSpacingV ]; }
	
	gridLines_ { |newGridLines, refreshFlag|
		newGridLines = (newGridLines ? this.gridLines).asPoint;
		gridSpacingH = fromBounds.width / newGridLines.x;
		gridSpacingV = fromBounds.height / newGridLines.y;
		if( gridSpacingH == inf ) { gridSpacingH = 0 };
		if( gridSpacingV == inf ) { gridSpacingV = 0 };
		this.refresh( refreshFlag );
		}
	
	drawFunc_ { |newDrawFunc, refreshFlag|
		drawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	unscaledDrawFunc_ { |newDrawFunc, refreshFlag|
		unscaledDrawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	beforeDrawFunc_ { |newDrawFunc, refreshFlag|
		beforeDrawFunc = newDrawFunc;  this.refresh( refreshFlag );
		}
		
	clearDrawFuncs { |refreshFlag|
		drawFunc = unscaledDrawFunc = beforeDrawFunc = nil;
		this.refresh( refreshFlag );
		}
	
	background_ { |color, refreshFlag|
		background = color; 
		this.refresh( refreshFlag ); 
	}
		
	keyDownAction { ^view.keyDownAction }
	keyDownAction_ { |newAction| view.keyDownAction_( newAction ); }
	
	keyUpAction { ^view.keyUpAction }
	keyUpAction_ { |newAction| view.keyUpAction_( newAction ); }
	
	resize { ^view.resize }
	resize_ { |val| view.resize_( val ) }
		
	bounds { ^view.bounds }
	
	drawBounds { ^view.drawBounds }
	
	bounds_ { |newBounds, refreshFlag|
		newBounds = (newBounds ? view.bounds).asRect;
		view.bounds = newBounds;
		this.move2Origin(savedOrigin, refreshFlag); // set correct move
	}
		
	viewRect { |inset = 0| //mc must still be adopted..
		// the currently viewed part of fromBounds
		/*
		var points;
		points = [ inset@inset,  this.drawBounds.extent - (inset@inset) ];
		points = points.collect({ |point| this.convertBwd( point.x, point.y ).asPoint; });
		^Rect( points[0].x, points[0].y, points[1].x - points[0].x, points[1].y - points[0].y );
		*/
		^view.drawBounds.transformFromRect( view.drawBounds, fromBounds, keepRatio, 
				scaleH@scaleV, moveH@moveV ).insetBy( inset, inset );
		}
	
	viewRect_ { |rect, refreshFlag| //mc must still be adopted..
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
	
	pixelScale { 
		
		// extent of a rect that shows up as 
		// one pixel regardless of the scale settings
		// example: 
		//  Pen.width = vw.pixelScale.asArray.mean;

		^(((1@1)/view.bounds.extent) * fromBounds.extent) / (scaleH@scaleV);

	}
	
	scaledPenWidthXY { // optimise if isFollow: ^this.scale
		^this.scale * [ (fromBounds.width / view.drawBounds.width).abs,
			(fromBounds.height / view.drawBounds.height).abs ].mean
	}
	scaledOutline {|rect, penWidth=1|
		^this.transScale(rect).resizeCenteredBy(*this.scaledPenWidthXY - [0, 0] * penWidth);
	}
	scaledInline {|rect, penWidth=1|
		^this.transScale(rect).resizeCenteredBy(*this.scaledPenWidthXY - [0, 0] * penWidth * -1);
	}
	scaledLineRect{|p1, p2, penWidth=1| //only sensible for strictly horizontal or verticals lines
		var scaledPenWidth, rect = Rect.fromPoints(this.transScale(p1), this.transScale(p2));
		if (rect.width == 0)  { 
			scaledPenWidth = this.scaledPenWidthXY[0] * penWidth;
			rect = rect.width_(scaledPenWidth).moveBy(scaledPenWidth / -2, 0);
		}{ if( rect.height == 0 ) { 
			scaledPenWidth = this.scaledPenWidthXY[1] * penWidth;
			rect = rect.height_(scaledPenWidth).moveBy(0, scaledPenWidth / -2)
		}};
		^rect;
	}
	
	
	scaleDelta { // move is 0.5@0.5 !!
		^Rect.new.scaleCenteredIn(view.drawBounds, false, this.scale).origin.asArray
			- this.scaledTransError;
	}
	
	scaledTransError { 
		^[0, (scaleV * -3) + 3] // might be mac specific
		//scale error may depend on total view scale:
		//	view.drawBounds.getScaleToRect(fromBounds, keepRatio, scaleH@scaleV );
		//needs to be checked stil...	
	}
		
	transScale { |item| 
//		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
//			scaleH@scaleV, moveH@moveV );
		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
			scaleH@scaleV, 0@0 ) + this.scaledTransError;
	}
	
	transScaleX {|item|
//		item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
//			scaleH@1, moveH@1 )
		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
			scaleH@1, 0@0) + (this.scaledTransError * [1, 0]);
	}
	
	transScaleY {|item|
		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
			1@scaleV, 0@0 ) + (this.scaledTransError * [0, 1]);
	}
	
	trans {|item|
		^item.transformToRect( view.drawBounds, fromBounds, keepRatio, 
			1@1, 0@0);
	}
	
	scaleFromBounds { ^(view.bounds.extent / fromBounds.extent).asArray } //mc
		  
	// scaling methods /// OLD (not compatible with keepRatio == true)
	convertScale { |inRect, outRect, sh = 1, sv = 1|
		if ( keepRatio )
			{	^( (1 / inRect.width.min(  inRect.height ) ) * 
				       outRect.width.min( outRect.height ) * [sh,sv] );  }
			{ 	^[ (1 / inRect.width ) * outRect.width  * sh, 
				   (1 / inRect.height) * outRect.height * sv ]; };
		}
			 
	convertMove { |inRect, mh = 0, mv = 0| 
		if ( keepRatio )
			{  ^( (( [mh.neg, mv.neg] * inRect.width.min( inRect.height )) + 0) 
				* (1 - (1/scaleH)) ); }
			{ ^[
				((mh.neg * inRect.width) + 0) * (1 - (1/scaleH)), 
				((mv.neg * inRect.height) + 0) * (1 - (1/scaleV))
			   ]; 
			 };
		}
		
	// these return input values for .translate and .scale
     /// OLD (not compatible with keepRatio == true)
	scaleAmt { ^this.convertScale( fromBounds, view.bounds, scaleH, scaleV ); }
	moveAmt { ^this.convertMove( fromBounds, moveH, moveV ); }
	
	// you can use these within drawFuncs and mouseFuncs to convert x/y values:
	
	convertFwd { |x = 0, y = 0| // move and scale input
//		^(x@y).transformToRect( view.drawBounds, fromBounds, keepRatio,
//				scaleH@scaleV, moveH@moveV ).asArray;
		^(x@y).transformToRect( view.drawBounds, fromBounds, keepRatio,
				scaleH@scaleV, 0@0 ).asArray + this.totalTranslation + this.scaledTransError;
		}
	
	convertBwd { |x = 0, y = 0| // scale and move input backwards
//		^(x@y).transformFromRect( view.drawBounds, fromBounds, keepRatio, 
//				scaleH@scaleV, moveH@moveV ).asArray;
//		}
		^(([x, y] - (this.totalTranslation + this.scaledTransError)).asPoint)
			.transformFromRect( view.drawBounds, fromBounds, keepRatio, 
				scaleH@scaleV, 0@0 ).asArray;
		}
	
	}