/**
 * oscP5oscArgument by andreas schlegel
 * example shows how to parse incoming osc messages "by hand".
 * it is recommended to take a look at oscP5plug for an alternative way to parse messages.
 * oscP5 website at http://www.sojamo.de/oscP5
 */
import oscP5.*;
import netP5.*;

int gridSize = 27;

PFont fontA;

float fontSize = 24;

OscP5 oscP5;
NetAddress myRemoteLocation;

void setup() {  
  //background(100,100,5);
  background(0);
  size(screen.width,screen.height);
  frameRate(100);
  
  smooth();
  // Load the font. Fonts must be placed within the data 
  // directory of your sketch. A font must first be created
  // using the 'Create Font...' option in the Tools menu.
  fontA = loadFont("CourierNew36.vlw");
  textFont(fontA, fontSize);
  textAlign(CENTER);
  translate(floor(screen.width/6) , 0);
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,12000);
  myRemoteLocation = new NetAddress("127.0.0.1",12000);  
}
void draw() {
//  background(0);
}

void oscEvent(OscMessage theOscMessage) {
  /* check if theOscMessage has the address pattern we are looking for. */  
  if(theOscMessage.checkAddrPattern("/hello")==true) {
    /* check if the typetag is the right one. */
    if(theOscMessage.checkTypetag("sii")) {
      /* parse theOscMessage and extract the values from the osc message arguments. */
      String first = theOscMessage.get(0).stringValue(); // get the third osc argument
      int x_coor = theOscMessage.get(1).intValue()%27; // get the second osc argument
      int y_coor = theOscMessage.get(2).intValue()%27; // get the third osc argument
 //     char testChar = theOscMessage.get(3).charValue(); // get the third osc argumen
//      int testInt = theOscMessage.get(3).intValue(); // get the third osc argument

//      print(" testInt = "+testInt);


//update element characters[i]
//      int characterPosition = floor(x_coor*sqrt(gridSize)+y_coor);
//  characters[characterPosition] = first;
//        println(characterPosition);

//      print("### received an osc message /test with typetag ifs.");
 //     println(" values: "+first+", "+x_coor+", "+y_coor);
      float ratio = screen.height/gridSize;
      rectMode(CENTER); 
      fill(0);
      rect(round(screen.width/6) + round(ratio/2)+round(x_coor*ratio),round(ratio)+round(y_coor*ratio - 10),30,30);
      fill(255);
      text(first,round(screen.width/6) + round(ratio/2)+round(x_coor*ratio),round(ratio)+round(y_coor*ratio),30,30);
    }
  }
//  println("### received an osc message. with address pattern "+
//          theOscMessage.addrPattern()+" typetag "+ theOscMessage.typetag());        
}
