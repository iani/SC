/*
 *  ofLsystem.h
 *
 *  Created by Aris Bezas Fri, 18 June 2010, 03:48PM
 *
 * Create class to control LSystem development for string rewriting class RedLSystem (SuperCollider) 
 * L-Systems studies
 * RedLSystem: rewrite strings, simple string rewrite class.
 * RedLTurtle
 * new(lsystem, length, theta, scale, noise)
		lsystem - an instance of RedLSystem or a string.
		length - line segment length in pixels.  default 40.
		theta - angle in degrees.  default 20.
		scale - the amount to scale all moving commands ($F, $G, $|) in percent.  default 1.
		noise - the amount of uniform noise to add to all angles in radians.  default 0.

 * $F: draw forward
 * $G: go forward
 * $+: turn right by theta
 * $-: turn left by theta
 * $[: push state
 * $]: pop state
 * $|: draw forward scaled by depth
 * an integer before each character repeats that command n times.
 *
 *
 *	TODO
 *	*	Fix scale 
 *	*	Add noise parameter
 *	*	Adding your own commands
 *	*	Adding repetitions of a command
 *	*	Character substitution at openFrameworks
 *  *   adding OSC reciever and responder in ofLsystem class
 *  *   create ofLsystem addons
 *
 *	*	*	Special thanks to Till Bovermann, redFrik
 */

#include "ofMain.h"
#include "testApp.h"
#include "ofLsystem.h"

ofLsystem::ofLsystem()	{
}

void ofLsystem::setup(){

	cout << "LSystem class recieving OSC at port: 46100 " << PORTlisten << "\n";
	receiver.setup( PORTlistenLSystem );
	current_msg_string = 0;
	
	subString = initString;
	depthLengthDefault = 50;
	depthLength = 100;
	length = 100;
	lengthStep = 0.1;
	thetaStep = 1;
	theta = 20;
	scale = 0.65;
	startGeneration = true;
	rLsys =255;
	gLsys = 255;
	bLsys = 255;
	aLsys = 255;
	
	startX = ofGetWidth()/2;
	startY = ofGetHeight()/2;			
	startZ = 0;				
}

void ofLsystem::update(){
}
void ofLsystem::substitution(){
	// here we will make the string substitution
	int stringPos = initString.length();
	for ( int j = 0; j < stringPos; ++j )	{
		if	(initString.at(j) == 'F')	{
			initString.replace(j, 1, subString);
			//cout << initString << endl;
			j = j + subString.length();
		}		
	}
	theta = theta + thetaStep;
	length = length + lengthStep; 
	lsystemString  = initString;
	recieveString();
}

void ofLsystem::colorSystem(){
	ofFill();
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glEnable(GL_LINE_SMOOTH);
	ofSetColor(rLsys,gLsys,bLsys,aLsys);    
}
void ofLsystem::recieveString(){
	int j;
	float oldLength;
	oldLength = length;
	ofPushMatrix();
	if	(startGeneration)	{	
		ofTranslate(startX, startY, startZ);			
	}
	colorSystem();
	for (j=0; j < lsystemString.length(); j++)	{
		if	(lsystemString[j] == 'F')	{
			ofLine(0,0,0, - oldLength);
			oldLength = scale*oldLength;	
			ofCircle(0,0,3);
			ofTranslate(0, - oldLength, 0);				
		}
		if	(lsystemString[j] == 'G')	{
			ofTranslate(0, - oldLength, 0);						
		}
		if	(lsystemString[j] == '+')	{
			ofRotate(theta);
		}
		if	(lsystemString[j] == 'Z')	{
			ofRotateZ(theta);
		}
		if	(lsystemString[j] == 'z')	{
			ofRotateZ(-theta);
		}
		if	(lsystemString[j] == 'X')	{
			ofRotateX(theta);
		}
		if	(lsystemString[j] == 'x')	{
			ofRotateX(-theta);
		}
		if	(lsystemString[j] == '-')	{
			ofRotate(-theta);
		}
		if	(lsystemString[j] == '[')	{
			ofPushMatrix();
		}
		if	(lsystemString[j] == ']')	{
			ofPopMatrix();
		}
		if	(lsystemString[j] == '|')	{
			depthLengthScale = scale*oldLength;	
			ofLine(0,0,0, - depthLengthScale);
			ofTranslate(0, - depthLengthScale, 0);						
			depthLengthScale = oldLength;
		}
	}			
	ofPopMatrix();			
}

void ofLsystem::imageTranslation(){
	int j;
	depthLength = scale*depthLength;	
	ofPushMatrix();
	if	(startGeneration)	{	
		ofTranslate(ofGetWidth()/2, ofGetHeight()/2, 0);			
	}
	for (j=0; j < lsystemString.length(); j++)	{
		if	(lsystemString[j] == 'F')	{
			ofSetColor(0xFFFFFF);
			golondrina.draw(0, 0);						
			ofTranslate(0, - depthLength, 0);				
		}
		if	(lsystemString[j] == 'G')	{
			ofTranslate(0, - depthLength, 0);						
		}
		if	(lsystemString[j] == '+')	{
			ofRotate(theta);
		}
		if	(lsystemString[j] == '-')	{
			ofRotate(-theta);
		}
		if	(lsystemString[j] == '[')	{
			ofPushMatrix();
		}
		if	(lsystemString[j] == ']')	{
			ofPopMatrix();
		}
		if	(lsystemString[j] == '|')	{
			depthLengthScale = scale*depthLength;	
			ofLine(0,0,0, - depthLengthScale);
			ofTranslate(0, - depthLengthScale, 0);						
			depthLengthScale = depthLength;
		}
	}	
	ofPopMatrix();			
}
