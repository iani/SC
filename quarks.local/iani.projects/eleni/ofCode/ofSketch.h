/*
 *  Sketch.h
 *  sketch091221
 *
 *  Created by fou fou on 12/23/09.
 * 
 *  Copyright 2009 Aris Bezas. All rights reserved.
 *
 */

#pragma once

#include "ofxOsc.h"
#include "ofxVectorMath.h"

#define stoixeia			40
#define stoixeiaMouse		40		// power to the people: 400 loop working fine
#define MAX_N_PTS			150

#define HOST				"localhost"
#define PORT_SC				57120

class ofSketch {
public:	
	int				sketchID;
	
	float			xi[stoixeia];
	float			yi[stoixeia];
	float			epitaxinsiX[stoixeia];
	float			epitaxinsiY[stoixeia];
	float			elastikotita[stoixeia];
	float			aposbesi[stoixeia];
	float			deltaX[stoixeia];
	float			deltaY[stoixeia];

	float			elast, aposv;
	float			xL, yL, zL, value, xC, yC;		
	int				redL, greenL, blueL, alphaL;
	float			delta;
	float			xp,yp;
	
	float			on_off, lines, slines;
	
	ofxVec3f		my3d;

    void			init(float elast, float aposv);
	void			draw(float xL, float yL, float zL, int redL, int greenL, int blueL, int alphaL, float slines);	

private:
	
	ofxOscSender	sender;
	
};








































