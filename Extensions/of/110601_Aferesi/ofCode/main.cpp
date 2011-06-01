#include "ofMain.h"
#include "testApp.h"
#include "ofAppGlutWindow.h"

//========================================================================
int main( ){

    ofAppGlutWindow window;

	//ofSetupOpenGL(&window, 1024, 512, OF_WINDOW);			// <-------- setup the GL context
	ofSetupOpenGL(&window, 1280, 1024, OF_WINDOW);	// LG Monitor, hp vp6321	
		
	//ofSetWindowPosition(1440,0);
	// this kicks off the running of my app
	// can be OF_WINDOW or OF_FULLSCREEN
	// pass in width and height too:
	ofRunApp( new testApp());
	


}
