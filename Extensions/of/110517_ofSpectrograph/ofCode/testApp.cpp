#include "testApp.h"
#include "stdio.h"

//--------------------------------------------------------------
testApp::testApp(){

}

//--------------------------------------------------------------
void testApp::setup(){
	ofSetBackgroundAuto(false);
	ofEnableSmoothing();
	ofEnableAlphaBlending();
	ofBackground(0,0,0);
	
	texScreen.allocate(ofGetWidth(), ofGetHeight(),GL_RGB);// GL_RGBA); 

	// listen on the given port
	cout << "listening for osc messages on port " << PORT << "\n";
	receiver.setup( PORT );

	current_msg_string = 0;

}

//--------------------------------------------------------------
void testApp::update(){

	// hide old messages
	for ( int i=0; i<NUM_MSG_STRINGS; i++ )
	{
		if ( timers[i] < ofGetElapsedTimef() )
			msg_strings[i] = "";
	}

	// check for waiting messages
	while( receiver.hasWaitingMessages() )
	{
		// get the next message
		ofxOscMessage m;
		receiver.getNextMessage( &m );

		// check for mouse moved message
		if ( m.getAddress() == "/fft" )
		{
//			float giveme;
//			giveme = m.getArgAsFloat( 10 );
//			cout << giveme << endl;
			// both the arguments are int32's
			for (int i=0; i<512; i++)	{
				data[i] = m.getArgAsFloat( i );
				//cout << data[i] << endl;
			}
		}
	}
}



//--------------------------------------------------------------
void testApp::draw(){
	
	for (int i=0; i<512; i++)	{
		//printf("%f,", data[i]);
		glColor3f(data[i],data[i],data[i]);
		ofEllipse(ofGetWidth(),512-i,2,2);
		//ofPoint(i,i,0);
	}
	

	texScreen.loadScreenData(ofGetWidth(),0,2, ofGetHeight());	//1280, 1024   ofGetWidth(), ofGetHeight()
	glPushMatrix();
	//glTranslatef(feedbackSpeedX,feedbackSpeedY,0);
	glTranslatef(-0.3,0,0);
		//glColor3f(1.0,1.0,1.0);
		ofSetColor(0xffffff);
		//ofSetColor(255,255,255,253);		
		//ofSetColor(2505,255,255,100);		
		
	texScreen.draw(0,0,ofGetWidth()-1, ofGetHeight());  //  ofGetWidth(), ofGetHeight()
	glPopMatrix();
	
/*
	texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());							//1280, 1024   ofGetWidth(), ofGetHeight()
	glPushMatrix();
	//glTranslatef(feedbackSpeedX,feedbackSpeedY,0);
	glTranslatef(-0.7,0,0);
		ofSetColor(0xffffff);
		//ofSetColor(0,0,0,1);		
		//ofSetColor(255,255,255,100);		
		
	texScreen.draw(0,0,ofGetWidth(), ofGetHeight());  //  ofGetWidth(), ofGetHeight()
	glPopMatrix();

*/

}

//--------------------------------------------------------------
void testApp::keyPressed  (int key){

}

//--------------------------------------------------------------
void testApp::keyReleased(int key){

}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){

}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::resized(int w, int h){

}

