//============
Seg {
	*new {
		arg pt0, pt1;
		^pt1.x - 0.001@pt0.y;
	}
}

//============
Carre {
	*new {
		arg pt0, pt1;
		var result = [];
		var interX = Interpoli.new(pt0.x, pt1.x, pt0.p3, pt0.p2);
			interX.do({
				arg x, i;
				var y0, y1;
				if (i.odd) 
					{y0 = pt1.y; y1 = pt0.y;
					}{
					y0 = pt0.y; y1 = pt1.y;};
				result = result.add(x - 0.001@y0);
				result = result.add(x@y1);
			});
			result = result.add(pt1.x - 0.001@pt0.y);
			^result;
	}
}

//============
Scie {
	*new {
		arg pt0, pt1;
		var result = [];
		var interX = Interpoli.new(pt0.x, pt1.x, pt0.p3, pt0.p2);
			interX.do({
				arg x, i;
				var y;
				if (i.odd)
					{y = pt0.y;
					}{
					y = pt1.y;};
				result = result.add(x@y);
			});
			^result;
	}
}

//============
Iter {
	*new {
		arg pt0, pt1;
		var result = [];
		var interX = Interpoli.new(pt0.x, pt1.x, pt0.p3, pt0.p2);
			interX.do({
				arg x;
				result = result.add(x@pt0.y);
			});
			^result;
	}
}

//============
Gliss {
	*new {
		arg pt0, pt1;
		var result = [];
		var interX = Interpoli.new(pt0.x, pt1.x, pt0.p3, pt0.p2);
		var interY = Interpoli.new(pt0.y, pt1.y, pt0.p4, pt0.p2);
			interX.do({
				arg x, i;
				result = result.add(x@interY.at(i));
			});
			^result;
	}
}

//============
Loop {
	*new {
		arg pt0, pt1;
		var result = [];
		var interX = Interpoli.new(pt0.x, pt1.x, pt0.p3, pt0.p2);
		var liY = 
			(pt0.p6..pt0.p7).collect({
				arg id; pt0.p5.at(id);});
		liY = (liY - liY.at(0)).rotate(-1);
		interX.do({
			arg x;
			result = result.add(x@(liY.at(0) + pt0.y));
			liY = liY.rotate(-1);
		});
		^result;
	}
}

// Fonctions ébauchées à insérer une fois les modifications générales du code (fonctions speciales dans la Class
// PtS) effectuées. A poursuivre...

Phas {
	*new {
		arg pt0, pt1;
		var result = [];
		var interX = Interpoli.new(pt0.x, pt1.x, pt0.p3, pt0.p2);
		interX.do({
			arg x, i;
			result = result.add(x - 0.001@pt1.y);
			result = result.add(interX.at(i + 1)@pt0.y);
			result.postln;
		});
		^result;
	}
}
		

