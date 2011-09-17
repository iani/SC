SYSTabGui : ObjGui {
/*
SYSTabGui.new
*/	
	var <drawServer, <scaledView, drawAddr, <drawDict;
	var <xTotal, <xS0, <yS0, <xS=1, <yS=1, scrlFroom = 11; // scrollFootRoom for ScrollView's scolly
var <yTotal;
	var labelHRects;
	
	var fontsize=14, drawFont, annoSize=10, annoFont;
			
	*observedClasses { ^[SYSTab] }
	*rowWidth { ^900 }
	*rowHeight { ^650 }
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 1050@800
		} { 
			defPos = skin.margin;
		};
		minSize = if (bounds.notNil) { minSize = bounds.extent
			}{ (this.class.rowWidth) @ (numItems * this.class.rowHeight) };
			
		drawDict = IdentityDictionary.new;
		drawAddr = NetAddr.localAddr;
		
		drawFont = Font.new("Helvetica", fontsize);
		annoFont = Font.new("Helvetica", annoSize);
		
		options[0] !? { # xS, yS = options[0] };
		
		labelHRects = List.new;
		
		allGuiDefs = (
			allGuiClass: 		SYSTabAllGui
			,objGuiClasses:	[this.class]
			,numItems:		30
			// optional ClassAllGui defaults:
			,initPos:			400@200
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\vertical
			,makeFilter:		true
			,name:			"SYSTabGui"
		)
	}
	getOptions { ^[[xS, yS]] }
	updateFast {
		var newState = this.getState;
		if (newState == prevState) { ^this };
		
		if (newState[\object] != prevState[\object]) { 
			this.name_(this.getName);
			prevState = newState //mc
		}		
	}
	customiseScaledView {|v|
		v.scaleSliders.do{|sl| sl.knobColor_( Color.blue ); 
			sl.background_( Color.grey ) };
		v.moveSliders.do{|sl| sl.knobColor_( Color.new255(196, 196, 196) );
			sl.background_( Color.grey ) };
	}
	makeViews {|obj| 
		// zone.bounds_(zone.bounds.height_((zone.bounds.height - scrlFroom) * max(yS, 1)));
		// zone.parent.background_(skin.foreground);
		zone.background_(Color.black);
xS0 = zone.bounds.width;
yS0 = zone.bounds.height;
		drawServer = ScaledDrawServer(zone, zone.bounds, nil, obj.frameRate ? 1, true);
		drawServer.view // .resize_(5) //resized already to 5 by viewFollows: true;
//			.canReceiveDragHandler_( {View.currentDrag.isKindOf(Systema) ||				View.currentDrag.isKindOf(SYS) } )
//			.receiveDragHandler_( {|view| obj.addAll([View.currentDrag.name]) } )
		;		
		scaledView = drawServer.scaledView;
//scaledView.autoRefreshMouseActions = false; // not needed anymore!
		this.customiseScaledView(scaledView);

		obj.buildDrawGraph(this, drawServer);
		// drawServer.active_(true); -> do not switch on -> overkill with many tabs !!!
		obj.addDependant(this);
	}
	activate {|tabIndex|
		drawServer.active_(true);
		object.class.selectName = object.name;
	}
	deactivate { 
		drawServer.active_(false);
	}
	stopSkip { //called on destroy 
		skipjacks.do{|skippy| skippy.stop };
		object.saveBackParams(drawServer.drawEnvir);
		object.clearDrawGraph(drawServer);
		object.removeDependant(this);
	}
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		what.switch(
			 \sysParams, { this.saveBackParamsRebuildDrawGraph } 
			,{ this.logln("unmaped update:" + [who, what, args]) })
	}
	rebuildDrawGraph{|clearPE=false|
		object.clearDrawGraph(drawServer, clearPE);
		object.buildDrawGraph(this, drawServer);
	}
	saveBackParamsRebuildDrawGraph{|clearPE=false|
		object.saveBackParams(drawServer.drawEnvir);
		this.rebuildDrawGraph(clearPE);
	}
	saveBackParamsSave{
		object.saveBackParams(drawServer.drawEnvir);
		object.save;
	}
	copySYSs {
		object.saveBackParams(drawServer.drawEnvir);
		object.copySYSs;
		this.rebuildDrawGraph(true);
	}
	copySYSsSave {
		object.saveBackParams(drawServer.drawEnvir);
		object.copySYSs;
		this.rebuildDrawGraph(true);
		object.save
	}
	frameRate {
		^drawServer.rate
	}
	frameRate_{|val|
		drawServer.rate = val;
		object.frameRate_(val, drawServer);
	}
//	hideSliders {
//		scaledView.scaleHEnabled_(false).scaleVEnabled_(false); 
//		scaledView.moveHEnabled_(false).moveVEnabled_(false);
//	}
//	showSliders {
//		scaledView.scaleHEnabled_(true).scaleVEnabled_(true); 
//		scaledView.moveHEnabled_(true).moveVEnabled_(true);
//	}
	moveH_{|val| 
		//scaledView.moveH_(val)
		drawServer.drawEnvir.parent.transH = val;
	}
	moveV_{|val| 
		//scaledView.moveV_(val)
		drawServer.drawEnvir.parent.transV = val;
	}

	
	xS_{|val|		
//		var newWidth = if (val>1) { xS0 * val } { xS0 };
//		zone.bounds_(zone.bounds.width_(newWidth));
//			//scaledView.fromBounds(scaledView.fromBounds.width_(newWidth)); // no way !!!
//		if (val>1) { scaledView.scaleH = 1 } { 
//			scaledView.scaleH = val; 
////zone.parent.visibleOrigin.postln;
////this.logln("xTotal:" + xTotal);
////this.logln("xTotal scaled:" + (xTotal * val));
////this.logln("zone.bounds.width:" + zone.bounds.width );
////this.logln("zone.parent.bounds.width:" + zone.parent.bounds.width );
////this.logln("origin:" + ((zone.bounds.width - (xTotal * val)) / 2) );
////			//zone.parent.visibleOrigin_((xTotal/8).postln@0) 
//			};
//		xS = val;
//// this.logln("newxS" + xS);		
	}
	yS_{|val|
		this.logln("yS" + val);
		// object.yS = val; // save back immediatly -> no!
		drawServer.drawEnvir.parent.yS = val;
	}
	//yS_{|val|
//		var newHeight = if (val>1) { yS0 * val } { yS0 };
//		zone.bounds_(zone.bounds.height_(newHeight));
//		scaledView.fromBounds(scaledView.fromBounds.height_(newHeight));
//		yS = val;
//	}
	topLabel {
		^{|sys, offPoint, extents|
		//	 Rect(x0, yHroom/2, offset, drawFont.size);
		 extents }	
	}
	drawContents {
		var newBounds, offset = 150;

		object.sysSymbols.do{|sym, i|
			
				var sys = Systema.at(sym);
				var yHroom = 12, yFroom = 8;
				var x0 = offset*i, xMid = x0 + (offset/2), xEnd = x0 + offset;
				var y0 = yHroom + drawFont.size + 15,
					yEnd = zone.bounds.extent.y - (2*scrlFroom) - yFroom - drawFont.size,  
					yMid = (yEnd - y0)/2 + y0;
				var labelFRect = Rect(x0, y0 + yEnd + (yFroom/2), offset, drawFont.size);
				var sysRect = Rect(x0, y0, offset, yEnd);
				var labelHRect = Rect(x0, yHroom/2, offset, drawFont.size);
				
				labelHRects.add(labelHRect);
				xTotal = xEnd;
				
//Pen.use { Color.green.set; Pen.strokeRect( sysRect ) };
//Pen.use { Color.red.set; Pen.strokeRect( labelFRect ) };
//Pen.use { Color.rand.set; Pen.line(x0@yMid, xEnd@yMid); Pen.stroke };

//midiRefs[i] ?? { midiRefs.add(sys.midiRoot.postln) };

				Color.white.set;
				Pen.use{
					var scaleY = 2.2*1200 / yS / (yEnd - y0); // 3.5 octaves
					var shiftY = yMid;
					var degLength = 10, degWidth = 1, deg0Width = 3;
					var minY=inf, maxY=0;
//this.logln([sys.scaleIndices, sys.scaleDegrees]);
					//sys.scaleIndices.collect{|deg| sys.tuning.degToC(deg)} // sys.cents
					//sys.scaleDegrees.collect{|deg, i| sys.at(deg) * 100}
					//.do{|cent, i| }
			
					sys.scaleIndices.do{|deg, index| var cent = sys.tuning.degToC(deg);
						//sys.scaleDegrees.do{|deg, i| }
						// var cent = sys.at(deg) * 100;  
						var y = ( (cent * -1) + ((object.midiRef-sys.midiRoot) * 100) 
							/ scaleY) + shiftY;
//this.logln([i, cent]);
						minY = min(minY, y); maxY = max(maxY, y);
								//Pen.addArc(xMid@y, 2, 0.0, 2pi); Pen.fill;
						Pen.width = if (cent == 0) { deg0Width } { degWidth };
						Pen.line((xMid-degLength)@y, (xMid+degLength)@y); Pen.stroke;
		//				Pen.use{ this.annotate(sys, deg, xMid, y, index) };
					};
			
					Pen.line(xMid@minY, xMid@maxY); Pen.stroke;
				};
				Color.black.set; Pen.addRect(labelHRect); Pen.fill;
			//sys.globDict[\label].asString.drawCenteredIn(labelHRect, drawFont, Color.white);
				object.sysPs[sym].label.drawCenteredIn(labelHRect, drawFont, Color.white);
				
				// drawDict[\spot].value(perc, dict, i, offset)
			};

//this.logln("xTotal, zone.bounds.width" + [xTotal, xTotal *xS, zone.bounds.width]);
//this.logln("boolean" + [(xTotal *xS).asInt > zone.bounds.width]);

			if((xTotal * xS).asInt > zone.bounds.width) { 
			//newBounds = zone.bounds.resizeTo(xTotal * xS, zone.bounds.extent.y - scrlFroom);
				newBounds = zone.bounds.resizeTo(xTotal * xS, zone.bounds.extent.y);

//this.logln("zone b:" + zone.bounds );
				zone.bounds_(newBounds); // newBounds.copy ?
//this.logln("zone a:" + zone.bounds );
//this.logln("fromBounds b:" + scaledView.fromBounds );
				scaledView.fromBounds = newBounds.width_(xTotal);
//this.logln("fromBounds a:" + scaledView.fromBounds );

//this.logln("xS0 b:" + xS0 );
				//xS0 = newBounds.width / xS;
				xS0 = xTotal;
//this.logln("xS0 a:" + xS0 );

			}

	}

	makeDrawDict{|obj| drawDict
		.put(\contents, {|perc, dict| //this.logln("perc:" + perc);
			if (obj.sysSymbols.isEmpty) { "-- empty --".drawAtPoint(30@1, color: Color.green) 
			}{ this.drawContents }
		})
		.put(\spot, {|perc, dict, i=0, offset=0|
			var x, y, r;
			x = dict.at(\x) + (offset*i + (offset/2));
			y = dict.at(\y);
			r = dict.at(\r)*(1.0-perc);
			Pen.fillColor = Color.blue;
			Pen.strokeColor = Color.red;
			Pen.addArc(x@y, r, 0.0, 2pi);
			Pen.fill;
			perc.asString.drawAtPoint(x@y, Font.new("Helvetica", 14), Color.grey) 
		})
	}
	annotate {|sys, deg, xMid, y, index| //Systema.testSystema('testSystema').stepDicts.at(0)[\name]
		var dist = 20 + (3 - index.mod(3) * 10 );
		var name = if (sys.stepDicts.at(index).isNil) { "" 
			}{ sys.stepDicts.at(index)[\name] ?? {""} };
		var count = if (sys.stepDicts.at(index).isNil) { ""
			}{ sys.stepDicts.at(index)[\count] ?? {""} };
//this.logln("deg, count, name" + [deg, count, name]);
		// var agmIns = sys.stepDicts.at(deg)[\agmIns].asString;
		// this.logln([sys, deg, name]);
		//name.asString
		//	.drawRightJustIn((20@20).asRect.moveTo(xMid-dist-20, y-10), annoFont, Color.white);
	//	name.asEncUTF8(\agmIns)
	//	.drawLeftJustIn((20@20).asRect.moveTo(xMid+dist, y-10), Font("AGMuni", 10), Color.white);
	if (name.isKindOf(Symbol)) {
		(256 - name.asEncDigit(\agmIns)).asAscii.asString
			.drawRightJustIn((20@20).asRect.moveTo(xMid-dist-20, y-10), Font("MonacoIns", 14), 
Color.new255(0x00, 0x40, 0x80).lighten(Color.blue, 0.5));
		(256 - name.asEncDigit(\agmVoc)).asAscii.asString
		.drawLeftJustIn((20@20).asRect.moveTo(xMid+dist, y-10), Font("MonacoVoc", 14), Color.new255(0x80,0x00,0x40).lighten(Color.red, 0.2));
	};

	}
}
