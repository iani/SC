#include "testApp.h"
//#define vara	iv["a"]

//--------------------------------------------------------------
void testApp::setup(){
		ofSetBackgroundAuto(false);
		ofEnableSmoothing();
		ofEnableAlphaBlending(); 
		//glutSetCursor(GLUT_CURSOR_CYCLE);  // change cursor icon (http://pyopengl.sourceforge.net/documentation/manual/glutSetCursor.3GLUT.html)
		cout << "LSystem app recieving OSC at port: 12345 " << PORTlisten << "\n";
		receiver.setup( PORTlisten );
		current_msg_string = 0;
				
		ofSetWindowTitle("LSystem study");
		ofSetFrameRate(60); // if vertical sync is off, we can go a bit fast... this caps the framerate at 60fps.

		LSystem.setup();
}

//--------------------------------------------------------------
void testApp::update(){
	//lets tumble the world with the mouse   
	glPushMatrix();
	//draw in middle of the screen
	glTranslatef(ofGetWidth()/2,ofGetHeight()/2,0);
	//tumble according to mouse
	glRotatef(-mouseY,1,0,0);
	glRotatef(mouseX,0,1,0);
	glTranslatef(-ofGetWidth()/2,-ofGetHeight()/2,0);
	while( receiver.hasWaitingMessages() )
	{
		ofxOscMessage m;
		receiver.getNextMessage( &m ); 
		if ( m.getAddress() == "int" )	{
			iv[m.getArgAsString(0)] = m.getArgAsInt32(1);			
			printf("%d", m.getArgAsInt32(1));
		}
		if ( m.getAddress() == "lsystem" )	{
			LSystem.lsystemString = m.getArgAsString( 0 );
			LSystem.length = m.getArgAsFloat( 1 );
			LSystem.theta = m.getArgAsFloat( 2 );
			LSystem.scale = m.getArgAsInt32( 3 );				
			LSystem.noise = m.getArgAsInt32( 4 );	
			lsystemGeneration = m.getArgAsInt32( 5 );
			if (m.getArgAsString( 0 ) == "translate") {
				LSystem.startX = m.getArgAsInt32( 1 );
				LSystem.startY = m.getArgAsInt32( 2 );
				if (m.getArgAsString( 1 ) == "center"){
					LSystem.startX = ofGetWidth()/2;
					LSystem.startY = ofGetHeight()/2;
				}
			}
			if (m.getArgAsString( 0 ) == "rgb") {
				LSystem.rLsys = m.getArgAsInt32( 1 );
				LSystem.gLsys = m.getArgAsInt32( 2 );
				LSystem.bLsys = m.getArgAsInt32( 3 );
				LSystem.aLsys = m.getArgAsInt32( 4 );				
			}

			if	(lsystemGeneration == 0)	{
				LSystem.startGeneration = true;
			}				
			LSystem.recieveString();							
			}
		}
		glPopMatrix();
}

//--------------------------------------------------------------
void testApp::draw(){
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // GL_SRC_ALPHA_SATURATE,GL_ONE     GL_SRC_ALPHA, GL_ONE
	ofFill();	
	ofSetColor(iv["rBack"],iv["gBack"],iv["bBack"],iv["aBack"]);
	ofRect(0,0,ofGetWidth(),ofGetHeight());			
}

//--------------------------------------------------------------
void testApp::keyPressed(int key){

}

//--------------------------------------------------------------
void testApp::keyReleased(int key){

}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){

}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){
	mouseDrag = true;

}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){
	mouseDrag = false;
}

//--------------------------------------------------------------
void testApp::windowResized(int w, int h){

}

