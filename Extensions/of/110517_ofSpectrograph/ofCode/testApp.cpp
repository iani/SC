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
	ofSetFrameRate(30);
	ofSetWindowTitle("ofSpectrogram");
	texScreen.allocate(ofGetWidth(), ofGetHeight(),GL_RGB);// GL_RGBA); 

	// listen on the given port
	cout << "listening for osc messages on port " << PORT << "\n";
	receiver.setup( PORT );

	current_msg_string = 0;
	
	iv["textureRed"] = 255;
	iv["textureAlpha"] = 255;
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
		ofxOscMessage m;
		receiver.getNextMessage( &m );

		if ( m.getAddress() == "/fft" )		{
			for (int i=0; i<512; i++)	{
				data[i] = m.getArgAsFloat( i );
			}
		} 
		// map implementation
		if ( m.getAddress() == "int" )	{
			iv[m.getArgAsString(0)] = m.getArgAsInt32(1);			
		}
	}
}
//--------------------------------------------------------------
void testApp::draw(){
	for (int i=0; i<512; i++)	{
		glColor3f(data[i],data[i],data[i]);
		ofEllipse(ofGetWidth(),512-i,2,2);
	}
	texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
	ofSetColor(iv["textureRed"],255,255,iv["textureAlpha"]);
	texScreen.draw(-1,0,ofGetWidth(), ofGetHeight());
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

