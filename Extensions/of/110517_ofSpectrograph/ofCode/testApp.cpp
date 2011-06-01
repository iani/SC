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
	ofSetFrameRate(60);
	ofSetWindowTitle("ofSpectrogram");
	texScreen.allocate(ofGetWidth(), ofGetHeight(),GL_RGB);// GL_RGBA); 
	ofSetFullscreen(true);

	// listen on the given port
	cout << "listening for osc messages on port " << PORT << "\n";
	receiver.setup( PORT );

	current_msg_string = 0;
	
	iv["textureRed"] = iv["textureGreen"] = iv["textureBlue"] = iv["textureAlpha"] = 255;
	iv["reverseEllipse"] = ofGetWidth();	iv["reverseTexture"] = -1;
	iv["mirrorMode"] = 0;
	fv["spectroRed"] = fv["spectroGreen"] = fv["spectroBlue"] = 1;
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

		if ( m.getAddress() == "/fftpixels" )		{
			switch ( iv["mirrorMode"] )
			  {
				 case 0:
					for (int i=0; i<512; i++)	{
						data[i] = m.getArgAsFloat( i );
						glColor3f(fv["spectroRed"]*data[i],fv["spectroGreen"]*data[i],fv["spectroBlue"]*data[i]);
						ofEllipse(iv["reverseEllipse"],512-i,2,2);
						glColor3f(0,0,0);
						ofEllipse(iv["reverseEllipse"],512+i,2,2);				
					}
					texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
					ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);
					texScreen.draw(iv["reverseTexture"],0,ofGetWidth(), ofGetHeight());
					
					break;
				 case 1:
					for (int i=0; i<512; i++)	{
						data[i] = m.getArgAsFloat( i );
						glColor3f(fv["spectroRed"]*data[i],fv["spectroGreen"]*data[i],fv["spectroBlue"]*data[i]);
						ofEllipse(iv["reverseEllipse"],512-i,2,2);
						ofEllipse(iv["reverseEllipse"],512+i,2,2);				
					}
					texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
					ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);
					texScreen.draw(iv["reverseTexture"],0,ofGetWidth(), ofGetHeight());
					break;
				 case 2:
					for (int i=0; i<512; i++)	{
						data[i] = m.getArgAsFloat( i );
						glColor3f(fv["spectroRed"]*data[i],fv["spectroGreen"]*data[i],fv["spectroBlue"]*data[i]);
						ofEllipse(0,512-i,2,2);
						ofEllipse(0,512+i,2,2);				
					}
					texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
					ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);
					texScreen.draw(iv["reverseTexture"],0,ofGetWidth(), ofGetHeight());
					break;

				 case 3:
					for (int i=0; i<512; i++)	{
						data[i] = m.getArgAsFloat( i );
						glColor3f(fv["spectroRed"]*data[i],fv["spectroGreen"]*data[i],fv["spectroBlue"]*data[i]);
						ofEllipse(ofGetWidth()/2,512-i,2,2);
						ofEllipse(ofGetWidth()/2,512+i,2,2);						
					}
					texScreen.loadScreenData(0,0,ofGetWidth()/2, ofGetHeight());
					ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);					
					texScreen.draw(-1,0,ofGetWidth()/2, ofGetHeight());					
					texScreen.loadScreenData(ofGetWidth()/2, 0,ofGetWidth(), ofGetHeight());
					ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);					
					texScreen.draw(ofGetWidth()/2 +1,0,ofGetWidth(), ofGetHeight());					
					break;
				 case 4:
					for (int i=0; i<512; i++)	{
						data[i] = m.getArgAsFloat( i );
						glColor3f(fv["spectroRed"]*data[i],fv["spectroGreen"]*data[i],fv["spectroBlue"]*data[i]);

						ofEllipse(ofGetWidth()/4,256-i/2,2,2);
						ofEllipse(ofGetWidth()/4,256+i/2,2,2);						

						ofEllipse(ofGetWidth()/4,776-i/2,2,2);
						ofEllipse(ofGetWidth()/4,776+i/2,2,2);						

						ofEllipse(3*ofGetWidth()/4,256-i/2,2,2);
						ofEllipse(3*ofGetWidth()/4,256+i/2,2,2);						

						ofEllipse(3*ofGetWidth()/4,776-i/2,2,2);
						ofEllipse(3*ofGetWidth()/4,776+i/2,2,2);						

					}
					ofSetColor(255,255,255,255);
					texScreen.loadScreenData(0,0,ofGetWidth()/4, ofGetHeight());
					texScreen.draw(-1,0);					
					texScreen.loadScreenData(ofGetWidth()/4, 0,ofGetWidth()/4, ofGetHeight());
					texScreen.draw(ofGetWidth()/4 + 1,0);					
										
					texScreen.loadScreenData(ofGetWidth()/4, 0,ofGetWidth()/4, ofGetHeight());
					texScreen.draw(3*ofGetWidth()/4 + 1,0);					
//
					texScreen.loadScreenData(ofGetWidth()/2, 0, ofGetWidth()/4, ofGetHeight());
					texScreen.draw(ofGetWidth()/2 - 1,0);
					
					break;
				 default:
					printf("%d", fv["mirrorMode"]);
			  }

		} 
		// map implementation
		if ( m.getAddress() == "int" )	{
			iv[m.getArgAsString(0)] = m.getArgAsInt32(1);			
		}
		if ( m.getAddress() == "float" )	{
			fv[m.getArgAsString(0)] = m.getArgAsFloat(1);			
		}
		if ( m.getAddress() == "reverse" )	{
			if (iv["reverseEllipse"] == 0) {
				iv["reverseEllipse"] = ofGetWidth();
			}	else	{
				iv["reverseEllipse"] = 0;
			}
			if (iv["reverseTexture"] == 1) {
				iv["reverseTexture"] = -1;
			}	else	{
				iv["reverseTexture"] = 1;
			}
		}

	}
}
//--------------------------------------------------------------
void testApp::draw(){
}

//--------------------------------------------------------------
void testApp::keyPressed  (int key){
	if(key == 'v' or key == 'V'){
		full = !full;
		if(full){
			ofSetFullscreen(true);
		} else {
			ofSetFullscreen(false);
		}
	}	
	if(key == 'b' or key == 'B'){
		ofBackground(0,0,0);
	}	
	if(key == 't' or key == 'T'){
		//ofEllipse(100,100,100,100);
		for (int i = 0; i < 10; i++)	{
			ofSetColor(255,255,255,255);
			texScreen.loadScreenData(int(ofRandom(0,1200)),int(ofRandom(0,1200)),int(ofRandom(100,500)),int(ofRandom(100,500)));
			texScreen.draw(int(ofRandom(0,1400)),int(ofRandom(0,1400)),100,100);			
		}		
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

