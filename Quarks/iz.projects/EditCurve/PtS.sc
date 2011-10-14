PtS : Point {
	
	var 	<>p1, 
		<>p2, 
		<>p3, 
		<>p4, 
		<>p5, 
		<>p6, 
		<>p7;
	
	*new { arg x = 0, y = 0, p1 = 0, p2 = 4, p3 = 0, p4 = 0, p5 = 0, p6 = 0, p7 = 0;
		^super.newCopyArgs(x, y, p1, p2, p3, p4, p5, p6, p7);
	}
	
	changePt {
		arg xLock, pt;
		if (xLock == 0) {this.x = pt.x;};
		this.y = pt.y;
		^this;
	}
	
	collect {
		var result = [];
		result = result.add(this.x);
		result = result.add(this.y);
		result = result.add(this.p1);
		result = result.add(this.p2);
		result = result.add(this.p3);
		result = result.add(this.p4);
		result = result.add(this.p5);
		result = result.add(this.p6);
		result = result.add(this.p7);
		^result;
	}
	
	asPtS { ^this }
}

+ Point {
	asPtS { 
		^PtS(x, y) 
	}
}

+ Array {
	asPtS { 
		^PtS(
			this.at(0),
			this.at(1),
			this.at(2),			
			this.at(3),
			this.at(4),
			this.at(5),
			this.at(6),
			this.at(7),
			this.at(8)) 
	}
}
