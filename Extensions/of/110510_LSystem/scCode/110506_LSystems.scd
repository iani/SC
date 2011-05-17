/*
LSystem and Event - enviroment study
Quarks.gui
Aris Bezas Tue, 05 April 2011, 10:15
*/

//:Compile the Enviroment
e = (
	initString: "F-F", 		// Initial string for substitution
	subString: "F[F]", 		// Substitution string
	doos: 4, 					// Recurcions	
	thetaStep: 1, 			// step
	lengthStep: 0.01, 
	rate: 1, 
	length: 10, 
	theta: 0, 
	addr: NetAddr.localAddr);

e use: { 
	k = Task({
		loop  { 
			l= RedLSystem(~initString, ($F: ~subString));		
			t = RedLTurtle(l, ~length, ~theta, 1, 0);
			~doos do: {
				l.next;
			};
			~addr.sendMsg("lsystem", l.asString, t.length, t.theta, t.scale, t.noise, l.generation);
			~theta = ~theta + ~thetaStep;
			~length = ~length + ~lengthStep;
			~rate.wait;
		}
	})
};
k.start;
OF.lsystem("translate",0,400);
OF.lsystem("translate", "center");
OF.int("rBack", 55);
OF.int("gBack", 55);
OF.int("bBack", 55);
OF.int("aBack", 20);
OF.lsystem("rgb", 255,255,255,255);

e[\rate] = 0.02;	//	define the rate
e[\addr] = NetAddr("127.0.0.1", 12345);  //localhost, oF port
e[\initString] = "F";
e[\thetaStep]= 0.51;
e[\subString] = "X-F-X-F-X";
e[\theta] = 0;
e[\length] = 20;
e[\doos] = 5;

//:Testing
e[\addr] = NetAddr("127.0.0.1", 12345);  //localhost, oF port
e[\initString] = "F";
e[\subString] = "F[-F]F[+F]F";
e[\theta] = 25;
e[\length] = 10;
e[\doos] = 2;

//:Koch curve
e[\addr] = NetAddr("127.0.0.1", 12345);  //localhost, oF port
e[\initString] = "F";
e[\thetaStep]= 0.1;
e[\subString] = "F-F++F-F";
e[\theta] = 60;
e[\length] = 20;
e[\doos] = 4;

//:Twig
e[\addr] = NetAddr("127.0.0.1", 12345);  //localhost, oF port
e[\initString] = "F";
e[\subString] = "|[-F][+F]";
e[\theta] = 20;
e[\length] = 70;
e[\doos] = 6;

//:Weed 
e[\addr] = NetAddr("127.0.0.1", 12345);  //localhost, oF port
e[\initString] = "F-";
e[\subString] = "-F-F[-F]F[+F]F";
e[\theta] = 25;
e[\length] = 40;
e[\doos] = 3;

//:GUI
(
w = Window.new("Control theta step",Rect(300,Window.screenBounds.height-300,400,100)).front;
a = Slider(w, Rect(50, 20, 300, 40))
	.value_(0.5)
	.step_(0.005)
	.focus
	.action_({
		e[\thetaStep]=(a.value-0.5)*2;
		//e[\thetaStep].postln;
	})
)

/*
(

w = Window.new.front;
a = Slider(w, Rect(20, 10, 150, 20));
a.action = { e[\thetaStep]=(a.value-0.5)*2 };
)
*/