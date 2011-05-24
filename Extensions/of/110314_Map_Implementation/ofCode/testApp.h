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

#define PORTlisten			12345
#define NUM_MSG_STRINGS		20

#define HOST "localhost"
#define PORTsender			57120

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
		ofxThread thread;
		ofxXmlSettings settings;

		map<string, int> iv;
		map<string, float> fv;		
		map<string, string> sv;				
		
//		map<string, ofEllipse> ellipse;		
//		map<string, ofLine> line;					
		map<int, ofPoint> point;			

	private:

		ofxOscReceiver	receiver;
		ofxOscSender	sender;
		int				current_msg_string;
		string			msg_strings[NUM_MSG_STRINGS];
		float			timers[NUM_MSG_STRINGS];
		
};

#endif
