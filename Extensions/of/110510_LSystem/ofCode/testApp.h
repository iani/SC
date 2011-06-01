#ifndef _TEST_APP
#define _TEST_APP

#define PORTlisten			12345
#define NUM_MSG_STRINGS		20


#include "ofMain.h"
#include "ofxOsc.h"
#include "ofLsystem.h"

class testApp : public ofBaseApp{

	public:
		void setup();
		void update();
		void draw();

		void keyPressed  (int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		
		int		lsystemGeneration;
		int		rBack,gBack,bBack,aBack;
		ofLsystem				LSystem;
		ofxOscSender			osc_sender;	
		
		bool mouseDrag;
		
		// STL <map> implementation
		map<string, int> iv;
		map<string, float> fv;		
		map<string, string> sv;				
		
		
	private:
		
		ofxOscReceiver	receiver;
		int				current_msg_string;
		string			msg_strings[NUM_MSG_STRINGS];
		float			timers[NUM_MSG_STRINGS];
		
};

#endif
