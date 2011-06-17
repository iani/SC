#include "testApp.h"
#include "stdio.h"

//--------------------------------------------------------------
testApp::testApp(){

}

//--------------------------------------------------------------
void testApp::setup(){
	ofSetCircleResolution(200);
	ofSetBackgroundAuto(true);
	ofEnableSmoothing();
	ofEnableAlphaBlending(); 
	//glutSetCursor(GLUT_CURSOR_CYCLE);  // change cursor icon (http://pyopengl.sourceforge.net/documentation/manual/glutSetCursor.3GLUT.html)
	cout << "FBO project recieving OSC at port: 12345 " << PORTlisten << "\n";
	receiver.setup( PORTlisten );
	current_msg_string = 0;
	video0.loadMovie("/Users/fou/videos/110527_FBO/video.mov");			
	ofSetWindowTitle("Mapping Projections");
	ofSetFrameRate(30); // if vertical sync is off, we can go a bit fast... this caps the framerate at 60fps.
	
	fbo0 = true;
	fbo0_A = true;
	defaultFBO = false;
	fbo1 = fbo2 = false;
	
	rm0.allocateForNScreens(3, 320, 280); //the first dedicate the screens
	rm0.loadFromXml("/Users/fou/data/110527_FBO/fboSettings0.xml");
	quad0  = ofRectangle(0,0,200,200);		
	rm1.allocateForNScreens(1, 320, 280); //the first dedicate the screens
	rm1.loadFromXml("/Users/fou/data/110527_FBO/fboSettings1.xml");
	quad1  = ofRectangle(220,0,200,200);		
	rm2.allocateForNScreens(1, 320, 280); //the first dedicate the screens
	rm2.loadFromXml("/Users/fou/data/110527_FBO/fboSettings2.xml");
	quad2  = ofRectangle(0,0,200,200);		

}

//--------------------------------------------------------------
void testApp::update(){
	{
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

		// map implementation
		if ( m.getAddress() == "int" )	{
			iv[m.getArgAsString(0)] = m.getArgAsInt32(1);	
		}
		if ( m.getAddress() == "float" )	{
			fv[m.getArgAsString(0)] = m.getArgAsFloat(1);			
		}
	}
	}	//OSC
}


//--------------------------------------------------------------
void testApp::draw(){
	if ( fbo0 )	{
		rm0.startOffscreenDraw();
			if	(	fbo0_A	)	{
				video0.idleMovie();
				ofSetColor(0xFFFFFF);
				video0.play();							
				video0.draw(0,0,320,280);
			}
			if	(	fbo0_B	)	{
			}
			if	(	defaultFBO	)	{							
				ofSetColor(255,255,255);
				ofRect(0, 0, rm0.width, rm0.height);							
				ofSetColor(0xFF0000);
				ofLine(0, 0, rm0.width, rm0.height);							
				ofSetColor(0xFF0000);
				ofLine(rm0.width, 0, 0, rm0.height);							
			}					
		rm0.endOffscreenDraw();
		rm0.drawOutputDiagnostically(quad0.x, quad0.y, quad0.width, quad0.height);	
	}
	if ( fbo1 )	{
		rm1.startOffscreenDraw();
			ofSetColor(255,255,0,150);
			ofRect(0, 0, rm1.width, rm1.height);							
		rm1.endOffscreenDraw();
		rm1.drawOutputDiagnostically(quad1.x, quad1.y, quad1.width, quad1.height);	
	}
	if ( fbo2 )	{
		rm2.startOffscreenDraw();
			ofSetColor(255,0,0,150);
			ofRect(0, 0, rm2.width, rm2.height);							
		rm2.endOffscreenDraw();
		rm2.drawOutputDiagnostically(quad2.x, quad2.y, quad2.width, quad2.height);	
	}
}

//--------------------------------------------------------------
void testApp::keyPressed  (int key){
	if ( key == '0' ) {		
		fbo0 = !fbo0;	
		if(fbo0){
			video0.play();
		} else {
			video0.stop();			
		}		
	}		
	if ( key == 'm')	{	ofHideCursor();	}
	if ( key == 'M')	{	ofShowCursor();	}
    if( key == 's'){	
		if ( fbo0 ) {    rm0.saveToXml(); }
		if ( fbo1 ) {    rm1.saveToXml(); }		
		if ( fbo2 ) {    rm2.saveToXml(); }
		
	}
    if( key == 'l' ){
		if ( fbo0 ) { rm0.reloadFromXml(); }
		if ( fbo1 ) { rm1.reloadFromXml(); }
		if ( fbo2 ) { rm2.reloadFromXml(); }
	}	
    if(key == 'r'){
		if ( fbo0 ) { rm0.resetCoordinates(); }
		if ( fbo1 ) { rm1.resetCoordinates(); }
		if ( fbo2 ) { rm2.resetCoordinates(); }
	}
    if(key == 'c'){
		if ( fbo0 ) { rm0.viewControlPoints = 1; }
		if ( fbo1 ) { rm1.viewControlPoints = 1; }
		if ( fbo2 ) { rm2.viewControlPoints = 1; }
	}
    if(key == 'C'){
		if ( fbo0 ) { rm0.viewControlPoints = 0; }
		if ( fbo1 ) { rm1.viewControlPoints = 0; }
		if ( fbo2 ) { rm2.viewControlPoints = 0; }
	}		
}

//--------------------------------------------------------------
void testApp::keyReleased(int key){

}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){

}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){
	if ( fbo0 ) { rm0.mouseDragOutputPoint(quad0, ofPoint( x, y)); }
	if ( fbo1 ) { rm1.mouseDragOutputPoint(quad1, ofPoint( x, y)); }
	if ( fbo2 ) { rm2.mouseDragOutputPoint(quad2, ofPoint( x, y)); }

}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){
	if ( fbo0 ) { rm0.mouseSelectOutputPoint(quad0, ofPoint( x,  y)); }
	if ( fbo1 ) { rm1.mouseSelectOutputPoint(quad1, ofPoint( x,  y)); }
	if ( fbo2 ) { rm2.mouseSelectOutputPoint(quad2, ofPoint( x,  y)); }
}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void testApp::resized(int w, int h){

}

