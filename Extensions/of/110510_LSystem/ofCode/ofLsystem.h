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
 * $[: push stateofLsystem
 * $]: pop state
 * $|: draw forward scaled by depth
 * an integer before each character repeats that command n times.
 */

#pragma once

#include "ofMain.h"
#include "ofxOsc.h"

#define PORTlistenLSystem			46100
#define NUM_MSG_STRINGSLSystem		20

class ofLsystem {

	public: 

		ofLsystem(); 
		ofImage				golondrina;
        ofxOscSender			osc_sender;

		void	setup();
		void	colorSystem();		
		void	update();
		void	substitution();		
		void	recieveString();		
		void	imageTranslation();	
		void	stringVisualization();

		float	length, lengthStep, depthLength, depthLengthDefault, depthLengthScale, scale, theta, thetaStep, noise;
		string	initString, subString, lsystemString;	
		int		doos;
		
		bool	startGeneration;
		int		rLsys,gLsys,bLsys,aLsys;
		
		int		startX, startY, startZ;
		


private:
		ofxOscReceiver	receiver;
		int				current_msg_string;
		string			msg_strings[NUM_MSG_STRINGSLSystem];
		float			timers[NUM_MSG_STRINGSLSystem];


}; 

