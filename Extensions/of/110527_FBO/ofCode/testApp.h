#ifndef _TEST_APP
#define _TEST_APP

#include "ofMain.h"

#include "ofx3DModelLoader.h"
#include "ofxDirList.h"
#include "ofxNetwork.h"
#include "ofxOpenCv.h"
#include "ofxOsc.h"
#include "ofxThread.h"
#include "ofxVectorGraphics.h"
#include "ofxVectorMath.h"
#include "ofxXmlSettings.h"

#include "renderManager.h"
#include "ofFBOTexture.h"

#define PORTlisten			12345
#define NUM_MSG_STRINGS		20

class testApp : public ofBaseApp{

	public:

		testApp();
		void setup();
		void update();
		void draw();

		void keyPressed  (int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void resized(int w, int h);
		
		ofxCvGrayscaleImage cvGray;
		ofx3DModelLoader modelLoader;
		ofxDirList dirList;
		ofxVec2f p;
		ofxTCPClient client;
		ofxTCPServer server;
		ofxOscSender osc_sender;
		ofxThread thread;
		ofxXmlSettings settings;

		//data
		ofVideoPlayer video0;
		//#############	FBO  ########################
		renderManager	rm0, rm1, rm2, rm3, rm4, rm5, rm6, rm7, rm8, rm9;
		ofRectangle		quad0, quad1, quad2, quad3, quad4, quad5, quad6, quad7, quad8, quad9;		
		
		bool defaultFBO;
		bool toggleImage, doRender;
		bool frameByframe;
		bool fbo0, fbo1, fbo2, fbo3, fbo4, fbo5, fbo6, fbo7, fbo8, fbo9;	
		bool fbo0_A, fbo0_B, fbo0_C;
		
		map<string, int> iv;
		map<string, float> fv;		
		
	private:

		ofxOscReceiver	receiver;
		int				current_msg_string;
		string			msg_strings[NUM_MSG_STRINGS];
		float			timers[NUM_MSG_STRINGS];

};

#endif
