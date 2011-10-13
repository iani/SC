#include "testApp.h"
#include "stdio.h"

//--------------------------------------------------------------
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
		screen.allocate(ofGetWidth(), ofGetHeight(),GL_RGB);
		
	}	//Texture
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
		iv["mirrorMode"] = 0; iv["pixelate"] = 0;
		fv["spectroRed"] = fv["spectroGreen"] = fv["spectroBlue"] = 1;
		
		fv["xPosImg"] = ofGetWidth()/2 - imgWidth/2;
		fv["yPosImg"] = ofGetHeight()/2 - imgHeight/2;
		fv["wImg"] = imgWidth; fv["hImg"] = imgHeight;
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
			//cout << m.getNumArgs() << endl;
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // GL_SRC_ALPHA_SATURATE,GL_ONE     GL_SRC_ALPHA, GL_ONE			
			switch (m.getNumArgs())	{
				case 1:
					ofFill();
					ofSetColor(0xFFFFFF);				
					//image[m.getArgAsInt32(0)].draw(m.getArgAsInt32(1), 0, 0, ofGetWidth(), ofGetHeight());
				break;
				case 5:
					ofFill();
					ofSetColor(0xFFFFFF);				
					image[m.getArgAsInt32(0)].draw(m.getArgAsInt32(1), m.getArgAsInt32(2),m.getArgAsInt32(3),m.getArgAsInt32(4));
				break;
				case 8:
					ofNoFill();
					ofSetColor(0xFFFFFF);		
					ofBeginShape();		
					ofRotateX(m.getArgAsInt32(5));
					ofRotateY(m.getArgAsInt32(6));
					ofRotateZ(m.getArgAsInt32(7));										
					image[m.getArgAsInt32(0)].draw(m.getArgAsInt32(1), m.getArgAsInt32(2),m.getArgAsInt32(3),m.getArgAsInt32(4));
					ofEndShape();
				break;
				case 11:
					//cout << m.getNumArgs() << endl;
					ofNoFill();
					ofSetColor(0xFFFFFF);		
					ofBeginShape();		
					ofTranslate(m.getArgAsInt32(5),m.getArgAsInt32(6),m.getArgAsInt32(7));
					ofRotateX(m.getArgAsInt32(8));
					ofRotateY(m.getArgAsInt32(9));
					ofRotateZ(m.getArgAsInt32(10));										
					image[m.getArgAsInt32(0)].draw(m.getArgAsInt32(1), m.getArgAsInt32(2),m.getArgAsInt32(3),m.getArgAsInt32(4));
					ofEndShape();
				break;
				case 14:
					cout << m.getNumArgs() << endl;
					ofNoFill();
					ofSetColor(0xFFFFFF);		
					ofBeginShape();		
					ofTranslate(m.getArgAsInt32(5),m.getArgAsInt32(6),m.getArgAsInt32(7));
					ofScale(m.getArgAsInt32(8),m.getArgAsInt32(9),m.getArgAsInt32(10));										
					ofRotateX(m.getArgAsInt32(11));
					ofRotateY(m.getArgAsInt32(12));
					ofRotateZ(m.getArgAsInt32(13));										
					image[m.getArgAsInt32(0)].draw(m.getArgAsInt32(1), m.getArgAsInt32(2),m.getArgAsInt32(3),m.getArgAsInt32(4));
					ofEndShape();
				break;
				
			}
		}		
		if ( m.getAddress() == "rect")	{
			ofFill();
			ofSetColor(0,0,0);
			ofRect(m.getArgAsInt32(0), m.getArgAsInt32(1), m.getArgAsInt32(2), m.getArgAsInt32(3));
		}
		if ( m.getAddress() == "loadImage" )	{
			image[m.getArgAsInt32(0)].loadImage(m.getArgAsString(1));
			printf("Load Image: %i \n", m.getArgAsInt32(0));
		}		
	}
	}	//OSC
}
//--------------------------------------------------------------
void testApp::draw(){
//	image[0].draw(fv["xPosImg"],fv["yPosImg"], fv["wImg"], fv["hImg"]);
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
		if(mouseX > 0 and mouseX < imgWidth and mouseY > 0 and mouseY < imgHeight)	{
			unsigned char * pixels = image[0].getPixels();
			int index = mouseY*imgWidth*3 + mouseX*3;
			iv["redSketch"] = pixels[index];
			iv["greenSketch"] = pixels[index+1];
			iv["blueSketch"] = pixels[index+2];
			for( int i=0; i<MAX_SKETCHES; i++ ) {

				sketch[i].draw(mouseX, mouseY, 0, iv["redSketch"], iv["greenSketch"], iv["blueSketch"], iv["alphaSketch"], 0);	
			}		
		}
	}
	if (iv["pixelate"] == 1)	{
		
		unsigned char * pixels = image[0].getPixels();
		for (int y=0; y<imgHeight; y += 1){	
			for(int x=0; x<imgWidth; x += 1){
				// the index of the pixel:
				int index = y*imgWidth*3 + x*3;
				int red = pixels[index];
				int green = pixels[index+1];
				int blue = pixels[index+2];
				ofSetColor(red,green,blue);
				ofEllipse(x,y,4,4);
			}
		}
	}
	if (iv["edgeDetection"] == 1)	{	
		float kernel[3][3] = {	{ -1, -1, -1 },
								{ -1, 9,  -1 },
								{ -1, -1,  -1 } };
								
		kernel[1][1] = 8 +sin(ofGetFrameNum());
		
		for (int y = 1; y < screen.height-1; y++) { // Skip top and bottom edges
			for (int x = 1; x < screen.width-1; x++) { // Skip left and right edges
			  float sum = 0; // Kernel sum for this pixel
			  for (int ky = -1; ky <= 1; ky++) {
				for (int kx = -1; kx <= 1; kx++) {
				  // Calculate the adjacent pixel for this kernel point
				  int pos = (y + ky)*screen.width + (x + kx);
				  
				  //+++++++++ SOLVE THIS ++++++++++++++++++//
				  // Image is grayscale, red/green/blue are identical
				  
				  //float val = screen.pixels[pos].rgbRed;
				  
				  // Multiply adjacent pixels based on the kernel values
				  
				  //sum += kernel[ky+1][kx+1] * val;
				  
				}
			  }
			  // For this pixel in the new image, set the gray value
			  // based on the sum from the kernel
		
			  	
				//screen.pixels[y*screen.width + x] = color(sum);
					
					
			}
		  }
		  // State that there are changes to edgeImg.pixels[]
		  
		  //screen.updatePixels();
		  
		  screen.draw(0,0);// Draw the new image
		  //image(edgeImg, 600, 0); // Draw the new image
		

	
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
	{
//	if(key == 'c' or key == 'C')	{
//	{
//		int trans= ofRandom(0,500);
//		unsigned char * pixels = af1.getPixels();
//		for (int y=0; y<600; y++){
//			for(int x=0; x<600; x++){
//				// the index of the pixel:
//				int index = y*600*3 + x*3;
//				int red = pixels[index];
//				int green = pixels[index+1];
//				int blue = pixels[index+2];
//				//printf("%d,%d,%d\n", red, green , blue);	
//				ofSetColor(red,green,blue);
//				ofLine(x,y,300,300);
//				//ofEllipse(trans+x,y,2,2);
//				// ok, so this example does not actually DO anything...
//			}
//		}
//	}	//Working
//	}
//	if(key == 'k' or key == 'K')	{
//	{
//
//		unsigned char * pixels = af0.getPixels();
//		int index = mouseY*af0000.width*3 + mouseX*3;
//		iv["redSketch"] = pixels[index];
//		iv["greenSketch"] = pixels[index+1];
//		iv["blueSketch"] = pixels[index+2];
//		ofSetColor(iv["redSketch"],iv["greenSketch"],iv["blueSketch"]);
//		ofEllipse(mouseX, mouseY,10,10);
//		
//	}	//Working
//	}
	}	// TESTING

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
