
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

// Point manipulation
(
t = Task({ 
		50000.do({ arg i;
			OF.point(i, rrand(0,700), rrand(0,800));
			0.001.wait 
		}); 
	});
)
t.start;
t.pause;

//line control
OF.int("posX1",120)
OF.int("posY1",120)
OF.int("posX2",220)
OF.int("posY2",220)
OF.int("circle1.y",120)
OF.int("circle1.rx",20)
OF.int("circle1.ry",10)

// Point manipulation
(
t = Task({ 
		50000.do({ arg i;
			OF.point(i, rrand(0,700), rrand(0,800));
			0.001.wait 
		}); 
	});
)
t.start;
t.pause;

