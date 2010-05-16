/* iz 080825
Handle input from Contour.dll camera tracking of vvvv.

080903 adding second responder to handle the case when the camera tracks 0 contours. 
080908 simplifying osc activation/deactivation scheme with OSCresponderManager
*/

ContourInput : OSCcontroller {
	var <contours;
	*actions {
		^[['/contours', 'setContoursFromOSC'], ['/zero_contours', 'zero_contours']]
	}
	setContoursFromOSC { | model, self, message, time, responder, data |
		#time ... data = data;
//		thisMethod.report(data);
		this.contours = data;
	}
	contours_ { | argContours |
		contours = argContours;
		this.changed('contours');
	}
	zero_contours {
		contours = [];
		this.changed('zero_contours')
	}
	contourVectors {
		// return list of vectors in the form of [[x1, y1, w1, h1, o1, a1, i1], [...] ...]
//		thisMethod.report(contours);
		^contours.clumps([contours.size / 7]).flop;
	}
}
