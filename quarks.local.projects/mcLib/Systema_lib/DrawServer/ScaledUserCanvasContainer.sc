// wslib 2006
// scaled SCUserView

// wslib 2009
// scaled SCUserViewContainer
// replacement for ScaledUserViewWindow
// can both be a separate window and a compositeview in an existing window

// adopted by mc 2011

ScaledUserCanvasContainer {
	
	classvar <>defaultBounds, <>defaultFromBounds;
	
	var <composite, <userView, <scaleSliders, <moveSliders;
	var <viewOffset;
	var <>maxZoom = 8, <>minZoom = 0.25; // var <>maxZoom = 8, <>minZoom = 1;
	var resize = 1;
	var window;
	var currentBounds;
	var <sliderKnobColor, <sliderBackColor;

	// var <sliderWidth = 12, <sliderSpacing = 2, <scaleSliderLength = 52; // defaults
	var <sliderWidth = 6, <sliderSpacing = 2, <scaleSliderLength = 100; 
	
	// these defaults should not be changed until after creation
	var <scaleHEnabled = true, <scaleVEnabled = true;
	var <moveHEnabled = true, <moveVEnabled = true;
	
	*initClass { 
		defaultBounds = Rect( 0, 0, 400, 400 );
		defaultFromBounds = Rect(0,0,1,1); // different from ScaledUserView
		}
		
	doesNotUnderstand { arg selector ... args;
		var res;
		res = userView.perform(selector, *args);
		if (res.class == ScaledUserCanvas )
			{ ^this }
			{ ^res }
		}
	
	parent { ^window ? composite }
	
	*new { |parent, bounds, fromBounds, viewOffset, canvasBounds|
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
		^super.new.init( parent, bounds, fromBounds, resize, canvasBounds);
		}
		
	front { composite.front }
	
	init { | inParent, inBounds, inFromBounds, inResize, inCanvasBounds|
		resize = inResize ? resize;
		
		sliderKnobColor = Color.blue; //sliderKnobColor ?? { Color.gray(0.2); };
		sliderBackColor = Color.grey; //sliderBackColor ?? { Color.black.alpha_(0.33); };
		composite = CompositeView( inParent, inBounds );
		composite.resize_( resize );
		// composite.background = Color.gray(0.8);
		// composite.onClose = { onClose.value( this, composite ) };
		
		userView = ScaledUserCanvas( composite, Rect(0,0, 
			composite.bounds.width - (sliderWidth + sliderSpacing),
			composite.bounds.height - (sliderWidth + sliderSpacing)), inFromBounds, inCanvasBounds);
			
		userView.view.resize_(5);
		userView.view.focusColor = Color.clear;
		userView.background = Color.white.alpha_(0.5);
		
		scaleSliders = [ 
			SmoothSlider( composite, Rect( 
					composite.bounds.width - 
						(scaleSliderLength + sliderWidth + sliderSpacing),  
		 			composite.bounds.height - sliderWidth, 
		 			scaleSliderLength, sliderWidth )  )
				.value_(0).action_({ |v| 
					userView.scaleH = 
						v.value.linlin(0,1,minZoom.asPoint.x, maxZoom.asPoint.x);
						//1 + (v.value * maxZoom.asPoint.x);
					this.setMoveSliderWidths( composite.bounds );
					})
				.knobColor_( sliderKnobColor )
		 		.hilightColor_( nil )
		 		.background_(sliderBackColor)
		 		.knobSize_( 1 )
		 		.canFocus_( false )
				.resize_(9)
				.mouseDownAction_{|slider, x, y, modifiers, buttonNumber, clickCount| 
					// this.logln("clickCount" + [this, slider, clickCount]); 
			 		if (clickCount > 1) { this.scaleH_(1.0) } }
			,
			SmoothSlider( composite,  Rect( 
		 			composite.bounds.width - sliderWidth,  
		 			composite.bounds.height - 
		 				(scaleSliderLength + sliderWidth + sliderSpacing), 
		 				sliderWidth, scaleSliderLength ) )
				.value_(1).action_({ |v| 
					userView.scaleV = 
						(1-v.value).linlin(0,1,minZoom.asPoint.y, maxZoom.asPoint.y);
						//1 + ((1 - v.value) * maxZoom.asPoint.y);
					this.setMoveSliderWidths( composite.bounds );
					})
				.knobColor_( sliderKnobColor )
				.hilightColor_( nil )
				.background_(sliderBackColor)
				.knobSize_( 1 )
		 		.canFocus_( false )
				.resize_(9)
				.mouseDownAction_{|slider, x, y, modifiers, buttonNumber, clickCount| 
					if (clickCount > 1) { this.scaleV_(1.0) } }
		]; 
		moveSliders = [ 
			SmoothSlider( composite,  Rect( 0, 
					composite.bounds.height - sliderWidth, 
					composite.bounds.width - 
						(scaleSliderLength + sliderWidth + (2 * sliderSpacing)), 
					sliderWidth ) )
				 .value_(0.5).action_({ |v| userView.moveH = v.value; })
				 .knobColor_( sliderKnobColor)
				 .hilightColor_( nil )
				 .background_(sliderBackColor)
				 .resize_(8)
		 		.canFocus_( false )
		 		.mouseDownAction_{|slider, x, y, modifiers, buttonNumber, clickCount|
			 		if (clickCount == 2) { this.move2OriginX(0) }; 
					if (clickCount == 3) { this.moveH_(1) };
					if (clickCount > 3) { this.moveH_(0) } }
		 	,
			SmoothSlider( composite,  Rect( 
					composite.bounds.width - sliderWidth,  
					0, sliderWidth, 
					(composite.bounds.height - 
						(scaleSliderLength + sliderWidth + (2 * sliderSpacing))) ) )
				 .value_(0.5).action_({ |v| userView.moveV = 1 - (v.value); })
				 .knobColor_( sliderKnobColor )
				 .hilightColor_( nil )
				 .background_(sliderBackColor)
				 .resize_(6)
		 		.canFocus_( false )
		 		.mouseDownAction_{|slider, x, y, modifiers, buttonNumber, clickCount| 
			 		if (clickCount == 2) { this.move2OriginY(0) }; 
			 		if (clickCount == 3) { this.moveV_(1) };
			 		if (clickCount > 3) { this.moveV_(0) } } 
		];
		this.setMoveSliderWidths;
		
		currentBounds = userView.bounds;
		
		userView.refreshAction = { |vw|
			if( currentBounds != vw.bounds )
				{ this.setMoveSliderWidths; currentBounds = vw.bounds; }
			};
		}
		
	updateSliders { |scaleFlag = true, moveFlag = true|
		if( scaleFlag )
			{ scaleSliders[0].value = 
				userView.scaleH.linlin(minZoom.asPoint.x, maxZoom.asPoint.x, 0, 1 );
				//(userView.scaleH - 1) / maxZoom.asPoint.x;
			scaleSliders[1].value = 1 - 
				userView.scaleV.linlin(minZoom.asPoint.y, maxZoom.asPoint.y, 0, 1 );
				//((userView.scaleV - 1) / maxZoom.asPoint.y );
			this.setMoveSliderWidths;
			};
		if( moveFlag )
			{
			moveSliders[0].value = userView.moveH;
			moveSliders[1].value = 1 - userView.moveV;
			};
	}
		
	updateSliderBounds {
	
		var hasH, hasV;
		
		// show/hide sliders
		[scaleHEnabled, scaleVEnabled, moveHEnabled, moveVEnabled].do({ |enabled, i|
			[ scaleSliders[0], scaleSliders[1], moveSliders[0], moveSliders[1] ][i]
				.visible = enabled;
			});
			
		if( scaleVEnabled ) { scaleSliders[1].visible = userView.keepRatio.not; };	
		
		hasH =  (moveSliders[0].visible or: { scaleSliders[0].visible }).binaryValue;
		hasV =  (moveSliders[1].visible or: { scaleSliders[1].visible }).binaryValue;
		
		#hasH, hasV = [ hasH, hasV ] * (sliderWidth + sliderSpacing);
				
		// set bounds		
		if( moveSliders[0].visible )
			{ moveSliders[0].bounds = Rect( 
				0, 
				composite.bounds.height - sliderWidth, 
				composite.bounds.width - ( hasV +
					((scaleSliderLength + sliderSpacing) * scaleSliders[0].visible.binaryValue )
					), 
				sliderWidth );
			};
				
		if( moveSliders[1].visible )
			{ moveSliders[1].bounds = Rect( 
				composite.bounds.width - sliderWidth,  
				0, 
				sliderWidth, 
				composite.bounds.height - (hasH +
					((scaleSliderLength + sliderSpacing) * scaleSliders[1].visible.binaryValue ) 
					)
					
				);
			};
			
		if( scaleSliders[0].visible )
			{ scaleSliders[0].bounds = Rect(
				composite.bounds.width - (scaleSliderLength + hasV),  
		 		composite.bounds.height - sliderWidth, 
		 		scaleSliderLength, 
		 		sliderWidth );
			};
			
		if( scaleSliders[1].visible )
			{ scaleSliders[1].bounds = Rect( 
				composite.bounds.width - sliderWidth,  
	 			composite.bounds.height - (scaleSliderLength + hasH), 
	 			sliderWidth, 
	 			scaleSliderLength );
			};
			
		userView.bounds = Rect(0,0,
			composite.bounds.width - hasV,
			composite.bounds.height - hasH
			);
		}
		
	bounds { ^composite.bounds }
	bounds_ { |newBounds| composite.bounds = newBounds; }
		
	scaleHEnabled_ { |bool| scaleHEnabled = bool; this.updateSliderBounds; }
	scaleVEnabled_ { |bool| scaleVEnabled = bool; this.updateSliderBounds; }
	
	moveHEnabled_ { |bool| moveHEnabled = bool; this.updateSliderBounds; }
	moveVEnabled_ { |bool| moveVEnabled = bool; this.updateSliderBounds; }
	
	sliderWidth_ { |width = 12| sliderWidth = width; this.updateSliderBounds; }
	sliderSpacing_ { |spacing = 2| sliderSpacing = spacing; this.updateSliderBounds; }
	scaleSliderLength_ { |length = 52| scaleSliderLength = length;  this.updateSliderBounds; }
		
	sliderKnobColor_ { |color|
		(scaleSliders ++ moveSliders).do(_.knobColor_(color));
		}
		
	sliderBackColor_ { |color|
		(scaleSliders ++ moveSliders).do(_.background_(color));
		}
// 	original		
//	setMoveSliderWidths { // |rect| // not used anymore
//		moveSliders[0].relThumbSize = (1 / userView.scaleH).min( userView.scaleH );
//		moveSliders[1].relThumbSize = (1 / userView.scaleV).min( userView.scaleV );
//			}

//mc
	setMoveSliderWidths { 
		var totalScaleH, totalScaleV;
		# totalScaleH, totalScaleV = (this.drawBounds.extent / this.totalBounds.extent).asArray;

		moveSliders[0].relThumbSize = (1 / totalScaleH).min( totalScaleH );
		moveSliders[1].relThumbSize = (1 / totalScaleV).min( totalScaleV );
		
//		moveSliders[0].relThumbSize = (1 / userView.scaleH).min( userView.scaleH )
//			* ((1 / userView.canvasScale[0]).min( userView.canvasScale[0] ) );
//		moveSliders[1].relThumbSize = (1 / userView.scaleV).min( userView.scaleV )
//			* ((1 / userView.canvasScale[1]).min( userView.canvasScale[1] ) );
	}

	scaleH_ { |newScaleH, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.scaleH_( newScaleH, refreshFlag );
		this.updateSliders( updateFlag, false );
		 }
		 
	scaleV_ { |newScaleV, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.scaleV_( newScaleV, refreshFlag );
		this.updateSliders( updateFlag, false );
		 }
		 
	scale_ { |newScaleArray, refreshFlag, updateFlag| // can be single value, array or point
		updateFlag = updateFlag ? true;
		userView.scale_( newScaleArray, refreshFlag );
		this.updateSliders( updateFlag, false );
		}
		
		
	moveH_ { |newMoveH, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.moveH_( newMoveH, refreshFlag );
		this.updateSliders( false, updateFlag );
		 }
		 
	moveV_ { |newMoveV, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.moveV_( newMoveV, refreshFlag );
		this.updateSliders( false, updateFlag );
		 }
		 
	move_ { |newMoveArray, refreshFlag, updateFlag| // can be single value, array or point
		updateFlag = updateFlag ? true;
		userView.move_( newMoveArray, refreshFlag );
		this.updateSliders( false, updateFlag );
		}
//mc		
	move2Origin{|origin=0, refreshFlag, updateFlag|
		updateFlag = updateFlag ? true;
		userView.move2Origin(0, refreshFlag);
		this.updateSliders( false, updateFlag );
	}
		
	movePixels_ {  |newPixelsArray, limit, refreshFlag, updateFlag| 
		updateFlag = updateFlag ? true;
		userView.movePixels_( newPixelsArray, limit, refreshFlag );
		this.updateSliders( false, updateFlag );
		}
		
	viewRect_ { |rect, refreshFlag, updateFlag| // can be single value, array or point
		updateFlag = updateFlag ? true;
		userView.viewRect_( rect, refreshFlag );
		this.updateSliders( updateFlag, updateFlag );
		}
//mc
	canvasBounds_{|rect, refreshFlag, updateFlag| //mc
		updateFlag = updateFlag ? true;
		userView.canvasBounds_(rect, refreshFlag);
		this.updateSliders( updateFlag, updateFlag );
	}
		
	keepRatio_ { |bool = false|
		userView.keepRatio = bool;
		this.updateSliderBounds;
		/*
		scaleSliders[1].visible = bool.not;
		
		moveSliders[1].bounds = moveSliders[1].bounds
			.height_( composite.bounds.height -
				[ scaleSliderLength + sliderWidth + (2 * sliderSpacing),
				  sliderWidth + sliderSpacing][ bool.binaryValue ] );
		*/
		this.scaleH = this.scaleH; 
		 }
		
	refresh { //this.setMoveSliderWidths;
		 ^composite.refresh }
		 
	resize { ^resize }
	resize_ { |val| resize = val; composite.resize_( val ) }
	
	window { ^window ?? { window = composite.getParents.last.findWindow; } }
	
	drawHook { ^this.window.drawHook }
	drawHook_ { |func| this.window.drawHook = func; }
	
	onClose { ^this.window.onClose }
	onClose_ { |func| this.window.onClose = func; }
	
	remove { composite.remove; }
		
} 