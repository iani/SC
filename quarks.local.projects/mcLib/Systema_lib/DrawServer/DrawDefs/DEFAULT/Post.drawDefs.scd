if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

// |name, protoCalcFunc, protoDrawFunc|
/*
DrawDef.all

d = ScaledDrawServer.new(rate: 1).active_(true); //click in win to refresh
DrawFunc(\postFrameRate, target: d); 

d = ScaledDrawServer.new(rate: 1).active_(true); d.scaledView.resize = 5; d; //click in win to refresh
		d.scaledView.scaleHEnabled_(false).scaleVEnabled_(false); // ScaledUserViewContainer only
		d.scaledView.moveHEnabled_(false).moveVEnabled_(false); // ScaledUserViewContainer only
DrawFunc(\postFrameRate, target: d); 
DrawScaled(\postFrameRate, target: d); 
DrawFunc(\postFrameRate, (dump: false), target: d); 
DrawScaled(\postFrameRate, (dump: false), target: d);

d.drawEnvir.isPost
d.drawEnvir.parent.isPost = true

d = ScaledDrawServer.new(rate: 1, viewFollows: true).active_(true); //click in win to refresh
DrawFunc(\postFrameRate, target: d); 
DrawScaled(\postFrameRate, target: d); 

d.scaledView.autoRefreshMouseActions
d.scaledView.autoRefreshMouseActions = false

// check jump behaviour and double click reset --->
*/

(
DrawDef(\postFrameRate, \draw, nil
	,{|node, rect, font, color, offset, dump=true|
		font = font ?? { Font.new("Helvetica", 10) };
		color = color ?? { Color.green };
		offset = offset ?? { 0@0 };
		{|e, drawServer, view, scaledView, isPost=true, post|
			var pos, str = ("frameRate:" + drawServer.rate);
			var bounds = str.bounds(font);
			var cRect = rect ?? { view.drawBounds };Ê//var !! may change due to user interaction
			//var cRect = rect = rect ?? { view.bounds };Ê//check-hack: here initial bounds stay
			
			//node.tree.postTree;
			//if (scaledView.isKindOf(ScaledUserCanvasContainer)) { }
			
if (dump) {
	//Color.yellow.set; Pen.width = 2; Pen.strokeRect( cRect.insetBy(6) );
//post.add([\postln, "----------------------------------------------"]);
//post.add([\postln, ("view, hasFocus:" + [view, view.hasFocus]) ]);
//post.add([\postln, ("view.parent.bounds:" + view.parent.bounds) ]);
//post.add([\postln, ("view.bounds:" + view.bounds) ]);
//post.add([\postln, ("view.drawBounds:" + view.drawBounds) ]);

if (scaledView.notNil) {
//	post.add([\postln, "+++:"]);
//	post.add([\postln, ("scaledView:" + scaledView) ]);
//	if (scaledView.isKindOf(ScaledUserViewContainer) || 
//		scaledView.isKindOf(ScaledUserCanvasContainer) ) {
//			post.add([\postln, ("scaledView.composite, hasFocus:" + 
//				[scaledView.composite, scaledView.composite.hasFocus])]);
//			post.add([\postln, ("scaledView.composite.bounds:" + scaledView.composite.bounds) ]);
//	};
//	post.add([\postln, ("scaledView.parent.bounds:" + scaledView.parent.bounds) ]);
//	post.add([\postln, ("scaledView.bounds:" + scaledView.bounds) ]);
//	post.add([\postln, ("scaledView.drawBounds:" + scaledView.drawBounds) ]);
	
	post.add([\postln, "scaledView.viewRect (rounded):" 
		+ scaledView.viewRect.asArray.round(0.001) , 1]);
	post.add([\postln, "scaledView.fromBounds:" + scaledView.fromBounds ]);
	post.add([\postln, "all .asArray.round(0.001):", 1]);
	post.add([\postln, "scaledView.scale:" + scaledView.scale.round(0.001) ]);
	post.add([\postln, "scaledView.move:" + scaledView.move.round(0.001) ]);
	post.add([\postln, "scaledView.scaleFromBounds:" 
		+ scaledView.scaleFromBounds.round(0.001) ]); // mc
	if (scaledView.isKindOf(ScaledUserCanvasContainer)) {
		post.add([\postln, "scaledView.canvasBounds:" 
			+ scaledView.canvasBounds.asArray.round(0.001) , 1]); // mc
		post.add([\postln, "scaledView.totalBounds:" 
			+ scaledView.totalBounds.asArray.round(0.001) , 0]); // mc
		post.add([\postln, "scaledView.totalTranslation:" 
			+ scaledView.totalTranslation.asArray.round(0.001) , 0]); // mc
	};
	post.add([\postln, [node, "cRect:" + cRect.asArray.round(0.001)], 1]);
	post.add([\postln, "scaledView.transScale(cRect):" 
		+ scaledView.transScale(cRect).asArray.round(0.001) ]);
	post.add([\postln, "scaledView.convertFwd(*cRect.extent.asArray):" 
		+ scaledView.convertFwd(*cRect.extent.asArray).round(0.001)  ]);
		
	post.add([\postln, "scaledView.convertBwd(*cRect.extent.asArray):" 
		+ scaledView.convertBwd(*cRect.extent.asArray).round(0.001) ]);
	post.add([\postln, "----------------------------------------------", 0, 1]);  
} };
	if (isPost) {
			pos = (cRect.left + 2)@(cRect.bottom - bounds.height);
			str.drawAtPoint(pos + offset, font, color);
			if (scaledView.notNil) {
				// pos.x = pos.x + (bounds.width * scaledView.scaleFromBounds[0]) + 10;
				pos.x = pos.x + bounds.width + 10;
				str = "scale:" + scaledView.scale.round(0.001) 
					+ " move:" + scaledView.move.round(0.001)
					+ " trans:" + scaledView.totalTranslation.round(0.001)
					+ " scaleDelta:" + scaledView.scaleDelta.round(0.001);
				bounds = str.bounds(font);
				str.drawAtPoint(pos + offset, font, color) };
			if (scaledView.isKindOf(ScaledUserView)) {
				//pos.x = pos.x + (bounds.width * scaledView.scaleFromBounds[0]) + 10;
				pos.x = pos.x + bounds.width + 10;
				str = "scaleFromBounds:" + scaledView.scaleFromBounds; bounds = str.bounds(font);
				str.drawAtPoint(pos + offset, font, color) };
	}}});
);

(
DrawDef(\post, \draw, nil
	,{ arg node, font = Font.new("Monaco", 10), color = Color.green, offset = 0@0;
		{arg e, isPost, post, postOffset = 0@0;
			var str, bounds, pos, xOff=2, yOff=0;
			if (isPost) {
				post.do{|pArray| 
					var lfB = pArray[2] ? 0;
					var lfE = pArray[3] ? 0;
					str = pArray[1].asString;
					bounds = str.bounds(font);
					yOff = yOff + (bounds.height * lfB);
					pos = xOff@yOff;
					pArray.first.switch( // to be extended \enc ... + post methods in Systema!!!
						\post, { xOff = xOff + bounds.width },
						\postln, { xOff=2; yOff = yOff + bounds.height });
					str.drawAtPoint(pos + offset + postOffset, font, color);
					yOff = yOff + (bounds.height * lfE);
				}
			}
	}});
);

