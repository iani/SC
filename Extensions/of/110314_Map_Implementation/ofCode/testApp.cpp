/*
http://www.cplusplus.com/reference/stl/map/
http://www.openframeworks.cc/forum/viewtopic.php?f=8&t=5751
http://www.sgi.com/tech/stl/stl_introduction.html
*/

#include "testApp.h"
#include "stdio.h"

//--------------------------------------------------------------
testApp::testApp(){
}

//--------------------------------------------------------------
void testApp::setup(){

	cout << "windscape recieving OSC at port:" << PORTlisten << " and send to 57120 \n";
	receiver.setup( PORTlisten );
	current_msg_string = 0;
//	
	sender.setup( HOST, PORTsender );	
	
	ofSetFrameRate(24); // if vertical sync is off, we can go a bit fast... this caps the framerate at 60fps.
	ofSetWindowTitle(ofToString(ofGetFrameRate(), 2.0));		
	ofBackground(0,0,0);
	ofEnableSmoothing();
}

//--------------------------------------------------------------
void testApp::update(){

	for ( int i=0; i<NUM_MSG_STRINGS; i++ )	{
		if ( timers[i] < ofGetElapsedTimef() )
			msg_strings[i] = "";
	}	
	while( receiver.hasWaitingMessages() )	
	{
		ofxOscMessage m;		
		receiver.getNextMessage( &m ); 
		if ( m.getAddress() == "int" )	{
			iv[m.getArgAsString(0)] = m.getArgAsInt32(1);			
		}
		if ( m.getAddress() == "point" )	{
			point[m.getArgAsInt32(0)] = ofPoint(m.getArgAsInt32(1), m.getArgAsInt32(2));
		}
	}

}


//--------------------------------------------------------------
void testApp::draw(){
	
	cout << "Size of int Variables (iv): " << iv.size() << endl;
	//cout << "The value of the first key is:" << iv.first << endl;

   for( map<string, int>::iterator ii=iv.begin(); ii!=iv.end(); ++ii)   {
       cout << (*ii).first << ": " << (*ii).second << endl;
   }
   
   ofEllipse(100,100, iv["radius"], iv["radius"]);
   ofEllipse(iv["circle1.x"],iv["circle1.y"],iv["circle1.rx"],iv["circle1.ry"]);
   ofLine(iv["line1.x1"], iv["line1.y1"], iv["line1.x2"], iv["line1.y2"]);
//   for( int i = 0; i < 50000; i++)   {
//		ofEllipse(point[i].x, point[i].y, 2, 2);
//   }	

	ofLine(iv["posX1"], iv["posY1"], iv["posX2"], iv["posY2"]);



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

/*
// SuperCollider code
// Aris Bezas Mon, 14 March 2011, 13:23

OF.int("radius",rrand(100,400));
OF.int("red",255)
OF.int("blue",0)
OF.int("white",10)
OF.int("alphe",20)
(
t = Task({ 
		5000.do({ arg i;
			OF.int("radius",rrand(100,400));
			0.01.wait 
		}); 
	});
)
t.start;
t.pause;


OF.int("circle1.x.ale",220)
OF.int("circle1.y",120)
OF.int("circle1.rx",20)
OF.int("circle1.ry",10)
*/

