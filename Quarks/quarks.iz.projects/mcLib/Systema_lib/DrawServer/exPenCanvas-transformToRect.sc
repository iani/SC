+ Pen {
	*transformToRectCanvas {|rect, fromRect, keepRatio = false, scale = 1, move = 0.5|
		var scaleAmt;
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? rect;
move = 0.0; // ignore move;
		rect = fromRect.scaleCenteredIn( rect, keepRatio, scale, move );
		scaleAmt = (rect.extent/fromRect.extent).asArray;
//this.logln("Pen.translate:" + (fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray, lfE:0);
//this.logln("Pen.translate res:" + ((fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray));
		//Pen.translate( *(fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray );
		^Pen.scale( *scaleAmt );
	}
	
	*transformToRectCanvOld {|rect, fromRect, keepRatio = false, scale = 1, move = 0.5, canvasRect|
		var scaleAmt;
		var scaleCanvas;
		rect = rect ?? {Rect(0,0,400,400)}; // the actual drawBounds to project To
		fromRect = fromRect ? rect;
		canvasRect = canvasRect ? rect;
//this.logln("canvasRect:" + canvasRect);		
//		scaleCanvas = (rect.extent/canvasRect.extent).asArray; // ok
//		//scaleCanvas = (canvasRect.extent/rect.extent).asArray; // wrong
		
this.logln("rect before:" + rect );
		rect = fromRect.scaleCenteredIn( rect, keepRatio, scale, move );
////		rect = canvasRect.scaleCenteredIn( rect, keepRatio, scale, move ); // no effect since canvasRect does not even take part in calc
////		//rect = fromRect.scaleCenteredIn( canvasRect, keepRatio, scale, move ); // no effect, too
//		//rect = fromRect.scaleCenteredIn( canvasRect, keepRatio, scaleCanvas, move );
//		rect = canvasRect.scaleCenteredIn( canvasRect, keepRatio, scaleCanvas, move ); //works

this.logln("rect after:" + rect );
//this.logln("canvasRect scaleCenteredIn:" + rect);
		scaleAmt = (rect.extent/fromRect.extent).asArray;
//		scaleCanvas = (rect.extent/canvasRect.extent).asArray;
//this.logln("scaleAmt, scaleCanvas" + [scaleAmt, scaleCanvas]);

//this.logln("Pen.translate:" + (fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray, lfE:1);
//		Pen.translate( *(fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray );

//this.logln("Pen.translate Canvas:" + (fromRect.origin.asArray.neg * scaleAmt) + rect.origin.asArray, lfE:0);
//this.logln("Pen.translate Canvas res:" + ((fromRect.origin.asArray.neg * scaleAmt) 
//	- rect.origin.asArray
//), lfE:1);
//		Pen.translate( *(fromRect.origin.asArray.neg * scaleAmt) 
//			- rect.origin.asArray
//		);
this.logln("translate easy:" + rect.originFromMove(canvasRect, move));
		// Pen.translate(*rect.originFromMove(canvasRect, move).asArray.neg);
		^Pen.scale( *scaleAmt );
		//^Pen.scale(1,1) //testing
		}
	}
	
+ Point {
	transformToRect {|rect, fromRect, keepRatio = false, scale = 1, move = 0.5|
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? rect;
		rect = fromRect.scaleCenteredIn( rect, keepRatio, scale, move );
		^((this - fromRect.origin) * ( rect.extent/fromRect.extent )) + rect.origin;
		}
		
	transformFromRect {|rect, fromRect, keepRatio = false, scale = 1, move = 0.5|
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? rect;
		rect = fromRect.scaleCenteredIn( rect, keepRatio, scale, move );
		^((this - rect.origin) * ( fromRect.extent/rect.extent )) + fromRect.origin;
		}
	
	transformMove4Origin{|rect, fromRect origin=0| //mc  Point is a move value
		var thisMoveArray = [x, y];
			//this.logln("prevMovePoint, origin:" + [this, origin]);
			//this.logln("draw, total:" + [rect, fromRect]);
		^((origin.asPoint + rect.origin - fromRect.origin) // spacing
			/ (fromRect.extent - rect.extent)) // convert to move
			.asArray.collect{|item, i| 
				if(item.isNaN || (item == inf) || (item == -inf)) {
					thisMoveArray[i]} {item} } //filter nan
			.asPoint;			
	}
}
	
+ Rect {
	originFromMove{|fromRect, move=0| //mc Rect is cut-out of fromRect // pen translate is .neg!
		^(fromRect.extent - this.extent) * move.asPoint + fromRect.origin - this.origin
	}
	scaleByRatio {|ratio = 1, keepRatio = false|
		var xyr, rect;
		ratio = ratio.asPoint;
		^if(keepRatio) {	xyr = width/height;
			if( (this.width / this.height).abs < xyr.abs ) { 
				Rect(0,0, this.width * ratio.x , (this.width / xyr) * ratio.y )
			}{	Rect(0,0, this.height * xyr * ratio.x, this.height * ratio.y ) }
		}{ 		Rect(0,0, this.width * ratio.x,  this.height * ratio.y) };
	}
	getScaleToRect {|fromRect, keepRatio = false, scale = 1|
		var rect;
		fromRect = fromRect ? this;
rect = fromRect.scaleCenteredIn(this, keepRatio, scale, 0@0 ); // make it easier..
		^(rect.extent/fromRect.extent).asArray;
	}
	resizeCenteredBy{|h, v|
		^this.class.new(left - (h/2), top - (v/2), width + h, height + v)
	}
	
	scaleCenteredIn { |toRect, keepRatio = false, ratio = 1, move = 0.5|
		var xyr, rect, spacing;
		
		move = move.asPoint;
		ratio = ratio.asPoint;
		
		toRect = (toRect ? this).asRect;
		
		if( keepRatio )
			{	
			xyr = width/height;
			
			if( (toRect.width / toRect.height).abs < xyr.abs )
				{ rect = Rect(0,0, toRect.width * ratio.x , (toRect.width / xyr) * ratio.y ); }
				{ rect = Rect(0,0, toRect.height * xyr * ratio.x, toRect.height * ratio.y ); };
			}
			{ rect = Rect(0,0, toRect.width * ratio.x,  toRect.height * ratio.y) };
			
		spacing  = (toRect.extent - rect.extent) * move;
//this.logln("move, spacing:" + [move,  spacing]);
		rect.origin = toRect.origin - rect.origin + spacing;
		
		//rect.origin = rect.centerIn( toRect );
		^rect;
		}
		
	transformToRect {  |rect, fromRect, keepRatio = false, ratio = 1, move = 0.5|
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? this;
		^this.class.fromPoints( *[ this.leftTop, this.rightBottom ]
			.collect( _.transformToRect( rect, fromRect, keepRatio, ratio, move ) ) );
		}
	
	transformFromRect {  |rect, fromRect, keepRatio = false, ratio = 1, move = 0.5|
		rect = rect ?? {Rect(0,0,400,400)};
		fromRect = fromRect ? this;
		^this.class.fromPoints( *[ this.leftTop, this.rightBottom ]
			.collect( _.transformFromRect( rect, fromRect, keepRatio, ratio, move ) ) );
		}
	}
	
+ Collection {
		transformToRect {  |rect, fromRect, keepRatio = false, ratio = 1, move = 0.5|
				^this.collect( _.transformToRect( rect, fromRect, keepRatio, ratio, move ) );		}
				
		transformFromRect {  |rect, fromRect, keepRatio = false, ratio = 1, move = 0.5|
				^this.collect( _.transformFromRect( rect, fromRect, keepRatio, ratio, move ) );		}
	
}