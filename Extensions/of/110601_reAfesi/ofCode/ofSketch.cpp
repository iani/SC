/*
 *  Sketch.cpp
 *  sketch091221
 *
 *  Created by Aris Bezas on 091223
*
 *  Copyright 2009 igoumeninja. All rights reserved.
 *
 */
 
#include "ofSketch.h"

void ofSketch::init(float elast, float aposv) {	
	sender.setup( HOST, PORT_SC );	
	for (int i=0; i<stoixeia; i++){
		elastikotita[i] = (elast)*(.07*(i+1));// 0.05  kai 0.005
		aposbesi[i] = aposv-(0.02 *i);
	}
}
void ofSketch::draw(float xL, float yL, float zL, int redL, int greenL, int blueL, int alphaL, float slines) {
	ofFill();
	ofSetColor(redL, greenL, blueL, alphaL);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glEnable(GL_LINE_SMOOTH);   
	if	(slines)	{
		glBegin(GL_LINE_LOOP); //GL_LINE_LOOP,GL_POINTS, GL_LINE_STRIP  ( http://pyopengl.sourceforge.net/documentation/manual/glBegin.3G.xml )
	}	else	{
		glBegin(GL_POINTS); //GL_LINE_LOOP
	}
	for (int i=0; i<stoixeia; i++){
		if (i==0){
			deltaX[i] = (xL - xi[i]);
			deltaY[i] = (yL - yi[i]);
		}
		else {
			deltaX[i] = (xi[i-1]-xi[i]);
			deltaY[i] = (yi[i-1]-yi[i]);
		}		
		deltaX[i] *= elastikotita[i];    // create elastikotita effect
		deltaY[i] *= elastikotita[i];
		epitaxinsiX[i] += deltaX[i];
		epitaxinsiY[i] += deltaY[i];
		xi[i] += epitaxinsiX[i];// move it
		yi[i] += epitaxinsiY[i];
		my3d.x = xi[i];
		my3d.y = yi[i];
		my3d.z = zL;
		glVertex3f(my3d.x, my3d.y, my3d.z);	
		epitaxinsiX[i] *= aposbesi[i];    // slow down elastikotita
		epitaxinsiY[i] *= aposbesi[i];
	}
	glEnd();	
}
