#pragma once
#include "ofMain.h"
#include "ofxOsc.h"
#include "Atom.h"
#include <list>
using std::list;

// listen on port 12345
#define PORT 12345
#define NUM_MSG_STRINGS 20
#define MAX_ELEMENTS			20000

class testApp : public ofBaseApp {
	public:

		void setup();
		void update();
		void draw();

		void keyPressed(int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y);
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		void dragEvent(ofDragInfo dragInfo);
		void gotMessage(ofMessage msg);
		void lookAtMedian();
		ofTrueTypeFont batang;
		ofxOscReceiver receiver;

		int current_msg_string;
		string msg_strings[NUM_MSG_STRINGS];
		float timers[NUM_MSG_STRINGS];

		int mouseX, mouseY;
		string mouseButtonState;
	
		ofEasyCam cam; // add mouse controls for camera movement
	
	int atomID,numAtoms, groupID;
	string element[MAX_ELEMENTS];
	float posX, posY, posZ;
	float bIso;
	string type_symbol, acid;
	bool manualAlpha;
	int alpha;
	list<Atom> atoms;
	ofVec3f lastAtomPosition;
	int lastAtomGroup;

};