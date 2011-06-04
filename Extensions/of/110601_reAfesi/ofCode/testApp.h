#ifndef _TEST_APP
#define _TEST_APP

#include "ofMain.h"
#include "ofSketch.h"
#include "ofxOsc.h"
#include "ofxOpenCv.h"
// 
// listen on port 12345
#define PORT 12345
#define NUM_MSG_STRINGS 20
#define MAX_SKETCHES 100

class testApp : public ofBaseApp{

	public:

		testApp();
		void setup();
		void update();
		void draw();

		void keyPressed  (int key);
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);		
		void printFoto(int photoID, float xPosImg, float yPosImg, float wImg, float hImg);
		
		ofxOscSender	osc_sender;		
		ofTexture		texScreen;
		ofImage			af0, af1, af2, af3,af4,af5,af6,af7, screen;		
		ofTrueTypeFont	font;
		ofSketch		sketch[MAX_SKETCHES];
		ofxCvGrayscaleImage 	grayImage;
		
		map<string, int> iv;
		map<string, float> fv;		

		float	data[1024];
		float		countX;
		
		bool full;

		
	private:
		ofxOscReceiver	receiver;

		int				current_msg_string;
		string			msg_strings[NUM_MSG_STRINGS];
		float			timers[NUM_MSG_STRINGS];
		string			mouseButtonState;
	

};

#endif
