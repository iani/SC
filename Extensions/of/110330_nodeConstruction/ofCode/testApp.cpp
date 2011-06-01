#include "testApp.h"
#include "Poco/Delegate.h"

void testApp::setup(){
	ofBackground(50,50,50);
	nInstances = 26; // we have to pre-define the number of the instances (strange put you can create more instances)
	myClassObject = new myClass*[nInstances];	// initialize myClassObject 
	nodeId = 0;
}

void testApp::update(){}

void testApp::draw(){
	ofDrawBitmapString("nodeId: " + ofToString(nodeId), 20, 20);
}

void testApp::keyPressed  (int key){
	if (key == 'o' && nodeId != 0)	
	{	
		nodeId--;
		myClassObject[nodeId]->disable();
		myClassObject[nodeId]->~myClass();
	}
	if (key == 'p')	
	{
		myClassObject[nodeId] = new myClass();
		myClassObject[nodeId]->enable();
		nodeId++;
	}
}

void testApp::keyReleased(int key){}

void testApp::mouseMoved(int x, int y ){}

void testApp::mouseDragged(int x, int y, int button){}

void testApp::mousePressed(int x, int y, int button){}

void testApp::mouseReleased(int x, int y, int button){}

void testApp::windowResized(int w, int h){}

