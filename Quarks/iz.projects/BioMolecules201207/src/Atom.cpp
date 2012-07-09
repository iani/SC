#include "Atom.h"

Atom::Atom(int i, ofVec3f pos, float disp, string ty, int gr, string a)
{
	id = i;
	position = pos;
	displacement = disp;
	type = ty;
	group = gr;
	acid = a;

	transparency = 255/(displacement+1);
	if (type == "N") {
		color = ofColor(255, 0, 0, transparency);
	} else if (type == "C") {
		color = ofColor(125, 125, 125, transparency);
	} else if (type == "O") {
		color = ofColor(0,0,255, transparency);		
	} else if (type == "S") {
		color = ofColor(255,255,0, transparency);		
	} else if (type == "P") {
		color = ofColor(84,147,18, transparency);		
	} else if (type == "CL") {
		color = ofColor(255,128,128, transparency);
	} else if (type == "NA") {
		color = ofColor(255,255,0, transparency);	
	} else {
		color = ofColor(0,0,0, 255);
	}
}

Atom::Atom()
{
}