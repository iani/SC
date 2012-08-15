#include "ofMain.h"

class Atom {
	public:
		ofVec3f position;
		float displacement;
		string type, acid;
		int id, group;
		Atom(int, ofVec3f, float, string, int, string);
		Atom();
		ofColor color;
	private:
		int transparency;
};