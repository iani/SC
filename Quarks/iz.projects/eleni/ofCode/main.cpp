#include "ofMain.h"
#include "testApp.h"
#include "ofAppGlutWindow.h"

//========================================================================
int main( ){

    ofAppGlutWindow window;

	//ofSetupOpenGL(&window, 1024, 512, OF_WINDOW);			// <-------- setup the GL context 2
	ofSetupOpenGL(&window, 1280, 1024, OF_WINDOW);	// LG Monitor, hp vp6321	
	//ofSetupOpenGL(&window, 600,600, OF_WINDOW);	// LG Monitor, hp vp6321		
	//ofSetupOpenGL(&window, 1240, 720, OF_WINDOW);	// LG Monitor, hp vp6321			
	//ofSetupOpenGL(&window, 1024, 768, OF_WINDOW);	// LG Monitor, hp vp6321				
		
	//ofSetWindowPosition(1440,0);
	ofSetWindowPosition(-1280,0);	
	// this kicks off the running of my app
	// can be OF_WINDOW or OF_FULLSCREEN
	// pass in width and height too:
	ofRunApp( new testApp());
	


}
