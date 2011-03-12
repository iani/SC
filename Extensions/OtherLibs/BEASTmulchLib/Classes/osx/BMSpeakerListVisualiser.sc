BMSpeakerListVisualiser : BMAbstractGUI {

	var speakerList, radius = 12, lowerel = 0;
	var maxXinv, lowX, hiX, hiY, lowY, hiZ, lowZ, zoom, labels, qcView;
	var colours, floorZ, viewSpeakers;
	
	*new {|speakerList| ^super.new.init(speakerList).makeWindow }
	
	init {|argspeakerList|
		speakerList = argspeakerList;
	}
	
	//speakerList_ {|list| }
	
	showBoids_{|bool| qcView.showBoids = bool }
	
	boidPositions_{|boidsArray| qcView.positions = boidsArray }
	
	makeWindow {
		var rect, subArraysCols, speakerColours;
		
		#lowX, lowY, lowZ, hiX, hiY, hiZ = speakerList.boundaries.flat;
		
		maxXinv = [lowX, hiX].abs.maxItem.reciprocal;
		#hiY, lowY, hiZ, lowZ = [hiY, lowY, hiZ, lowZ] * maxXinv;
		
		floorZ = -0.8  * maxXinv - 0.09;
		
		window = SCWindow("Speakers", rect = Rect(400,200, 800, 800 * [hiY, lowY].abs.maxItem)).front;

		zoom = SCSlider(window, Rect(0,0,rect.width, 20));

		qcView = SCQuartzComposerView(window, rect.moveTo(0,20));
		qcView.path = this.class.filenameSymbol.asString.dirname ++ "/QC/SpeakerVis.qtz";
		qcView.showBoids = true;
		qcView.resize_(5);
		
		//qcView.maxFPS_(20);
		zoom.action = {qcView.zoom = zoom.value * (hiZ - floorZ) - (hiZ - floorZ);};
		
		zoom.doAction;
		qcView.sphereScale = 0.02;
		
		colours = Pseq([Color.green.alpha_(1), Color.red.alpha_(1), Color.blue.alpha_(1), Color.yellow.alpha_(1), Color.white.alpha_(1), Color.magenta.alpha_(1), Color.cyan.alpha_(1)], inf).asStream;

		subArraysCols = speakerList.subArrays.collectAs({|key| key->colours.next }, IdentityDictionary) ?? { () };
		speakerColours = ();
		subArraysCols.keysValuesDo({|key, value| 
			speakerList.getSubArrayKeys(key).do({|speakKey| speakerColours[speakKey] = value;});
		});
		
		qcView.startY = hiY;
		qcView.endY = lowY;

		qcView.floorZ = floorZ;
		
		viewSpeakers = speakerList.associationsCollectAs({|assoc| 
			var x, y, z, colour, tilt;
			x = assoc.value.x  * maxXinv;
			y = assoc.value.y  * maxXinv;
			z = assoc.value.z  * maxXinv;
			colour = speakerColours[assoc.key] ?? {colours.next};
			tilt = atan2(z, hypot(x, y)) * 57.295779513082;
			[x, y, z, colour, Point(x, y).theta * 57.295779513082, assoc.key.asString, cos(assoc.value.azi * 0.017453292519943) * tilt, sin(assoc.value.azi * 0.017453292519943).neg * tilt] 
			
		}, Array);

		qcView.speakers = viewSpeakers;
		qcView.start;

	}
}