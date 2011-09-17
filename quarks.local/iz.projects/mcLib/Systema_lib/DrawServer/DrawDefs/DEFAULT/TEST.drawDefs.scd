if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };
/*
DrawDef.all.keys
*/

(
DrawDef(\scaledTest, \scaled, nil
	, {{|e, isPost, outline| if(isPost && outline) { 
		Pen.use{ 
			Color.yellow.set; Pen.width = 2; Pen.strokeRect( e.scaledView.drawBounds.insetBy(4) ); 
			
			e.testPenW = 3; // change this to see results
			e.testRect = Rect(450, 250, 200, 200);
			e.testLP1 = 460@260; e.testLP2 = 460@280;
			e.testPoint = 470@260; 
			e.pointRect = Rect.fromPoints(e.testPoint, e.testPoint).extent_((e.testPenW-1).asPoint);
			Pen.width = e.testPenW; 
			Pen.strokeRect(e.testRect);
			Pen.line(e.testLP1, e.testLP2); Pen.stroke;
			Pen.strokeRect(e.pointRect);
			
	}}}})
);

(
DrawDef(\movedTest, \moved, nil
	, {{|e, isPost, outline, testPenW, testRect, testLP1, testLP2, testPoint, pointRect, scaledView,
		trans| 
		if(isPost && outline) { Pen.use{ 
			var rectTS, pointTS, pointRectTS, testLP1TS, testLP2TS, testLP1fwd, testLP2fwd;
			
			Color.white.set; Pen.width = 2; Pen.strokeRect( e.scaledView.canvasBounds.insetBy(2) );
			Color.red.set; Pen.width = 2; Pen.strokeRect( e.scaledView.totalBounds.insetBy(0) );
			
			rectTS = scaledView.transScale(testRect);
			testLP1TS = scaledView.transScale(testLP1);
			testLP2TS = scaledView.transScale(testLP2);
			testLP1fwd = scaledView.convertFwd(*testLP1.asArray).asPoint;
			testLP2fwd = scaledView.convertFwd(*testLP2.asArray).asPoint;
			pointTS = scaledView.transScale(testPoint);
			//pointRectTS = Rect.fromPoints(pointTS, pointTS).extent_((testPenW - 1).asPoint);
			pointRectTS = scaledView.transScale(pointRect);
			Pen.width = testPenW; 
			Color.red.setStroke; Pen.strokeRect(rectTS);
				Pen.line(testLP1TS, testLP2TS); Pen.stroke;
			// either above or below:
				// Pen.use{ Pen.translate(*trans); Pen.line(testLP1fwd, testLP2fwd); Pen.stroke };
			Pen.strokeRect(pointRectTS);
			
	}}}})
);

(
DrawDef(\drawTest, \draw, nil
	, {{|e, isPost, outline, testPenW, testRect, testLP1, testLP2, testPoint, pointRect, scaledView,
		trans| 
		if(isPost && outline) { Pen.use{ 
			var rectTS, pointTS, pointRectTS, testLP1TS, testLP2TS;
			rectTS = scaledView.transScale(testRect);
			testLP1TS = scaledView.transScale(testLP1);
			testLP2TS = scaledView.transScale(testLP2);
			pointTS = scaledView.transScale(testPoint);
			pointRectTS = scaledView.transScale(pointRect);
			Pen.translate( *trans * -1 );
			Pen.width = 1;
			Color.blue.setStroke; Pen.strokeRect(scaledView.scaledOutline(testRect, testPenW));			Color.green.setStroke; Pen.strokeRect(scaledView.scaledInline(testRect, testPenW));
			Color.white.setStroke; Pen.strokeRect(rectTS);
			
			Color.blue.setStroke; Pen.strokeRect(scaledView.scaledLineRect(
				testLP1, testLP2, testPenW));
			Color.white.setStroke; Pen.line(testLP1TS, testLP2TS); Pen.stroke;
						
			Color.blue.setStroke; Pen.strokeRect(scaledView.scaledOutline(pointRect, testPenW));			Color.green.setStroke; Pen.strokeRect(scaledView.scaledInline(pointRect, testPenW));
			Color.white.setStroke; Pen.strokeRect(pointRectTS);
	}}}})
);

(
DrawDef(\freeTest, \draw, nil
	, {|node|
		{|e, isPost| 			
	}}
	, {|node| // this.logln("proto free:" + node); 
		{|e, isPost| // this.logln("free:" + e); 			
	}})
);
