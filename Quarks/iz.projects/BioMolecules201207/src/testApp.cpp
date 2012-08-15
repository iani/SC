#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup(){
	ofBackground(0, 0, 0, 255);
	ofSetBackgroundAuto(true);
	//ofEnableAlphaBlending(); 
	ofEnableBlendMode(OF_BLENDMODE_ADD);
	ofSetWindowTitle("biochemical molecule");
	batang.loadFont("/Users/ari/Media/fonts/favorites/Batang.ttf", 9, true, true);
	ofSetFrameRate(60); // if vertical sync is off, we can go a bit fast... this caps the framerate at 60fps.
	ofSetVerticalSync(false);
	manualAlpha = false;
	
	// listen on the given port
	cout << "listening for osc messages on port " << PORT << "\n";
	receiver.setup(PORT);
	current_msg_string = 0;
	mouseX = 0;
	mouseY = 0;
	mouseButtonState = "";
	ofSetSphereResolution(4);
	//ofEnableSmoothing();
	ofEnablePointSprites();
	cam.setTarget(ofVec3f(173.082, 184.656, 177.797));
}

//--------------------------------------------------------------
void testApp::update(){
	alpha = (((float)ofGetMouseX()/(float)ofGetWidth())*128) -64;

	// hide old messages
	for(int i = 0; i < NUM_MSG_STRINGS; i++){
		if(timers[i] < ofGetElapsedTimef()){
			msg_strings[i] = "";
		}
	}

	// check for waiting messages
	while(receiver.hasWaitingMessages()){
		// get the next message
		ofxOscMessage m;
		receiver.getNextMessage(&m);
		if(m.getAddress() == "atomID"){
			// both the arguments are int32's
			atomID		=	m.getArgAsInt32 ( 0 );
			posX		=	ofMap(m.getArgAsFloat( 1 ), 100,200, 0 ,ofGetWidth());
			posY		=	ofMap(m.getArgAsFloat( 2 ), -50, 50, 0 ,ofGetHeight());
			posZ		=	ofMap(m.getArgAsFloat( 3 ), -30, 30, 0 ,400);
			bIso		=	ofMap(m.getArgAsFloat( 4 ), 0, 50, 0 , 10);
			type_symbol	=	m.getArgAsString(5);
			groupID		= m.getArgAsInt32(6);
			acid		= m.getArgAsString(7);
			atoms.push_back(Atom(atomID,ofVec3f(posX,posY,posZ),bIso,type_symbol,groupID,acid));
		}
	}
}


//--------------------------------------------------------------
void testApp::draw(){
	//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // GL_SRC_ALPHA_SATURATE,GL_ONE     GL_SRC_ALPHA, GL_ONE
	cam.begin();
	ofFill();	
	ofSetColor(0,0,0,10);
	lastAtomPosition = ofVec3f(0,0,0);
	lastAtomGroup = 0;
	for (list<Atom>::iterator atom = atoms.begin(); atom != atoms.end(); atom++){
		if (!manualAlpha){
			ofSetColor(atom->color);
		} else {
			int tempAlpha = alpha + atom->color.a;
			if (tempAlpha > 255) tempAlpha = 255;
			if (tempAlpha < 0) tempAlpha = 0;
			ofSetColor(atom->color.r,atom->color.g,atom->color.b,tempAlpha);
		}
		ofSphere(atom->position, atom->displacement);

		if (atom != atoms.begin() && atom->group != lastAtomGroup) {
			ofSetColor(255,128,0,128+alpha);
			ofLine(lastAtomPosition,atom->position);
		}
		lastAtomPosition = atom->position;
		lastAtomGroup = atom->group;
	}
	cam.end();
	ofSetWindowTitle("biochemical molecule " + ofToString(ofGetFrameRate()));
}
//--------------------------------------------------------------
void testApp::keyPressed(int key){
	switch(key) {
		case 'M':
		case 'm':
			if(cam.getMouseInputEnabled()) cam.disableMouseInput();
			else cam.enableMouseInput();
			break;
			
		case 'F':
		case 'f':
			ofToggleFullscreen();
			break;

		case 'L':
		case 'l':
			lookAtMedian();
			break;

		case 'A':
		case 'a':
			manualAlpha = !manualAlpha;
			break;
	}

}


void testApp::lookAtMedian() {
	float maxX, minX,maxY,minY,maxZ,minZ;
	ofVec3f tempVector;
	tempVector.zero();
	for (list<Atom>::iterator atom = atoms.begin(); atom != atoms.end(); atom++) {
		/*if (atom->position.x > maxX) maxX = atom->position.x;
			if (atom->position.x > maxX) maxX = atom->position.x;
			if (atom->position.x < minX) minX = atom->position.x;
			if (atom->position.x > maxY) maxY = atom->position.y;
			if (atom->position.x < minY) minY = atom->position.y;
			if (atom->position.x > maxZ) maxZ = atom->position.z;
			if (atom->position.x < minZ) minZ = atom->position.z;
			*/
		tempVector += atom->position;
	}
	//ofVec3f median = ofVec3f((maxX+minX)/2,(maxY+minY)/2,(maxZ+minZ)/2);
	//median /= atoms.size();
	//cout << median;
	tempVector /= atoms.size();

	cam.setTarget(tempVector);
	//cam.lookAt(median);
}


//--------------------------------------------------------------
void testApp::keyReleased(int key){

}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y){

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
void testApp::windowResized(int w, int h){

}

//--------------------------------------------------------------
void testApp::gotMessage(ofMessage msg){

}

//--------------------------------------------------------------
void testApp::dragEvent(ofDragInfo dragInfo){

}

