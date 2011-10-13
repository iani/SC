+ PtS {

//============
seg {
	arg pt1;
		^pt1.x - 0.000001@this.y;
}


//============
carre {
	arg pt1;
	var result = [];
	var interX = Interpoli.new(this.x, pt1.x, this.p3, this.p2);
	interX.do({
		arg x, i;
				var y0, y1;
				if (i.odd) 
					{y0 = pt1.y; y1 = this.y;
					}{
					y0 = this.y; y1 = pt1.y;};
				result = result.add(x - 0.000001@y0);
				result = result.add(x@y1);
			});
			result = result.add(pt1.x - 0.000001@this.y);
			^result;
}


//============
scie {

		arg pt1;
		var result = [];
		var interX = Interpoli.new(this.x, pt1.x, this.p3, this.p2);
			interX.do({
				arg x, i;
				var y;
				if (i.odd)
					{y = this.y;
					}{
					y = pt1.y;};
				result = result.add(x@y);
			});
			^result;
	}


//============
iter {

		arg pt1;
		var result = [];
		var interX = Interpoli.new(this.x, pt1.x, this.p3, this.p2);
			interX.do({
				arg x;
				result = result.add(x@this.y);
			});
			^result;
	}


//============
gliss {

		arg pt1;
		var result = [];
		var interX = Interpoli.new(this.x, pt1.x, this.p3, this.p2);
		var interY = Interpoli.new(this.y, pt1.y, this.p4, this.p2);
			interX.do({
				arg x, i;
				result = result.add(x@interY.at(i));
			});
			^result;
	}


//============
loop {

		arg pt1;
		var result = [];
		var interX = Interpoli.new(this.x, pt1.x, this.p3, this.p2);
		var liY = 
			(this.p6..this.p7).collect({
				arg id; this.p5.at(id);});
		liY = (liY - liY.at(0)).rotate(-1);
		interX.do({
			arg x;
			result = result.add(x@(liY.at(0) + this.y));
			liY = liY.rotate(-1);
		});
		^result;
	}

}
		

