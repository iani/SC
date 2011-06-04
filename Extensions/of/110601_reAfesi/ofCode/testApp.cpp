#include "testApp.h"
#include "stdio.h"

//------------------------------------*--------------------------
testApp::testApp(){

}

//--------------------------------------------------------------
void testApp::setup(){
	{
		//ofSetFullscreen(true);
		
		ofSetBackgroundAuto(false);
		ofEnableSmoothing();
		ofEnableAlphaBlending();
		ofBackground(0,0,0);
		ofSetFrameRate(60);
		ofSetWindowTitle("reAfesi");
	}	//Screen
	{
		texScreen.allocate(ofGetWidth(), ofGetHeight(),GL_RGB);// GL_RGBA); 
		//screen.allocate(50,50);
		
	}	//Texture
	{
		af0.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af0.png");
		af1.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af1.png");
		af2.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af2.png");
		af3.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af3.png");
		af4.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af4.png");
		af5.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af5.png");
		af6.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af6.png");
		af7.loadImage("/Users/fou/Dropbox/AB-/ArisOmer/AferesiDB/aferesi/af7.png");
		
		af1.allocate(600,600,OF_IMAGE_COLOR);												
	}	//load DATA
	{
		cout << "listening for osc messages on port " << PORT << "\n";
		receiver.setup( PORT );
		current_msg_string = 0;
	}	//OSC
	{		
		for (int i = 0; i < MAX_SKETCHES; i++){
			sketch[i].init(ofRandom(0.01, 0.29), ofRandom(0.01, 0.29));
		}
	}	//sketch
	{
	iv["textureRed"] = iv["textureGreen"] = iv["textureBlue"] = iv["textureAlpha"] = 255;
	iv["reverseEllipse"] = ofGetWidth();	iv["reverseTexture"] = -1;
	iv["mirrorMode"] = 0;
	fv["spectroRed"] = fv["spectroGreen"] = fv["spectroBlue"] = 1;
	
	fv["xPosImg"] = ofGetWidth()/2 - af1.width/2;
	fv["yPosImg"] = ofGetHeight()/2 - af1.height/2;
	fv["wImg"] = af1.width; fv["hImg"] = af1.height;
	iv["sketch"] = 0;
	iv["alphaSketch"] = 10;
	}	//Initial value
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
			printf("value = %d\n", m.getArgAsInt32(1));		
		}
		if ( m.getAddress() == "float" )	{
			fv[m.getArgAsString(0)] = m.getArgAsFloat(1);			
		}
		if ( m.getAddress() == "img" )	{
			printFoto(int(m.getArgAsFloat(0)), m.getArgAsFloat(1), m.getArgAsFloat(2),m.getArgAsFloat(3),m.getArgAsFloat(4));			
		}				
	}
	}	//OSC
}
//--------------------------------------------------------------
void testApp::draw(){
	switch ( iv["mirrorMode"] )	{
		case 0:
		break;
		 case 1:
			texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
			ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);
			texScreen.draw(iv["reverseTexture"],0,ofGetWidth(), ofGetHeight());
			
			break;
		 case 2:
			texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
			ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);
			texScreen.draw(iv["reverseTexture"],0,ofGetWidth(), ofGetHeight());
			break;
		 case 3:
			texScreen.loadScreenData(0,0,ofGetWidth(), ofGetHeight());
			ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);
			texScreen.draw(iv["reverseTexture"],0,ofGetWidth(), ofGetHeight());
			break;

		 case 4:
			texScreen.loadScreenData(0,0,ofGetWidth()/2, ofGetHeight());
			ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);					
			texScreen.draw(-1,0,ofGetWidth()/2, ofGetHeight());					
			texScreen.loadScreenData(ofGetWidth()/2, 0,ofGetWidth(), ofGetHeight());
			ofSetColor(iv["textureRed"],iv["textureGreen"],iv["textureBlue"],iv["textureAlpha"]);					
			texScreen.draw(ofGetWidth()/2 +1,0,ofGetWidth(), ofGetHeight());					
			break;
		 case 5:
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
		}	// mirrowMode
		
	if (iv["sketch"] == 1)	{	
//		unsigned char * pixels = af1.getPixels();
//		int index = mouseY*600*3 + mouseX*3;
//		iv["redSketch"] = pixels[index];
//		iv["greenSketch"] = pixels[index+1];
//		iv["blueSketch"] = pixels[index+2];

//		ofSetColor(iv["redSketch"], iv["greenSketch"], iv["blueSketch"], iv["alphaSketch"]);
//		ofLine(0,0,mouseX, mouseY);
//		ofLine(0,ofGetHeight(),mouseX, mouseY);
//		ofLine(ofGetWidth(),0,mouseX, mouseY);
//		ofLine(ofGetWidth(), ofGetHeight(),mouseX, mouseY);				
//		ofEllipse(mouseX, mouseY, 10,10);
		for( int i=0; i<MAX_SKETCHES; i++ ) {

			sketch[i].draw(mouseX, mouseY, 0, iv["redSketch"], iv["greenSketch"], iv["blueSketch"], iv["alphaSketch"], 0);	
		}		


	}
	
}

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
	
	if(key == 'c' or key == 'C')	{
	{
		int trans= ofRandom(0,500);
		unsigned char * pixels = af1.getPixels();
		for (int y=0; y<600; y++){
			for(int x=0; x<600; x++){
				// the index of the pixel:
				int index = y*600*3 + x*3;
				int red = pixels[index];
				int green = pixels[index+1];
				int blue = pixels[index+2];
				//printf("%d,%d,%d\n", red, green , blue);	
				ofSetColor(red,green,blue);
				ofLine(x,y,300,300);
				//ofEllipse(trans+x,y,2,2);
				// ok, so this example does not actually DO anything...
			}
		}
	}	//Working
	}
	if(key == 'k' or key == 'K')	{
	{

		unsigned char * pixels = af0.getPixels();
		int index = mouseY*af0.width*3 + mouseX*3;
		iv["redSketch"] = pixels[index];
		iv["greenSketch"] = pixels[index+1];
		iv["blueSketch"] = pixels[index+2];
		ofSetColor(iv["redSketch"],iv["greenSketch"],iv["blueSketch"]);
		ofEllipse(mouseX, mouseY,10,10);
		
	}	//Working
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
void testApp::mouseDragged(int x, int y, int button){
//pixel (100,20):
}
void testApp::mouseMoved(int x, int y ){
		/*
		unsigned char * pixels = grayImage.getPixels();
		int widthOfLine = grayImage.width * 1024;  // how long is a line of pixels
		iv["redSketch"] 	= pixels[(mouseY * widthOfLine) + mouseX * 3    ];
		iv["greenSketch"] 	= pixels[(mouseY * widthOfLine) + mouseX * 3 + 1];
		iv["blueSketch"] 	= pixels[(mouseY * widthOfLine) + mouseX * 3 + 2];
		iv["alphaSketch"] 	= pixels[(mouseY * widthOfLine) + mouseX * 3 + 3];
		printf("%d,%d,%d,%d\n", iv["redSketch"], iv["greenSketch"], iv["blueSketch"], iv["alphaSketch"]);	
		ofSetColor(iv["redSketch"], iv["greenSketch"], iv["blueSketch"], iv["alphaSketch"]);
		ofLine(0,0,mouseX, mouseY);
		ofLine(0,ofGetHeight(),mouseX, mouseY);
		ofLine(ofGetWidth(),0,mouseX, mouseY);
		ofLine(ofGetWidth(), ofGetHeight(),mouseX, mouseY);				
		ofEllipse(mouseX, mouseY, 10,10);
		*/

}
void testApp::mousePressed(int x, int y, int button)	{

}
void testApp::mouseReleased(int x, int y, int button){}

void testApp::printFoto(int photoID, float xPosImg, float yPosImg, float wImg, float hImg)	{
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // GL_SRC_ALPHA_SATURATE,GL_ONE     GL_SRC_ALPHA, GL_ONE
	ofFill();
	ofSetColor(0xFFFFFF);				
	fv["xPosImg"] = xPosImg;
	fv["yPosImg"] = yPosImg;
	fv["wImg"] = wImg;
	fv["hImg"] = hImg;			
	switch ( photoID )	{
		case 0:	af0.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		case 1:	af1.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		case 2:	af2.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		case 3:	af3.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		case 4:	af4.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		case 5:	af5.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		case 6:	af6.draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);	break;
		//default: //printf("No foto found\n");
	}
}

