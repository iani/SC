
//:Experimental: Trying with EnvirStream


{
	\default.mplay([\freq, (70, 72..80).choose.midicps]).dur(0.3, exprand(0.1, 1.0));
	EventStream(\hocketus, Pser([0.25, 0.5, 0.5, 1.0] / 3, 100)).next;
}.schedEnvir;


{
	\default.mplay([\freq, [50, 55, 57].choose.midicps]).dur(0.1, exprand(0.1, 0.5));
	EventStream(\hocketus, Pser([1.25, 1] / 3.0, 50)).next;
}.schedEnvir;


//:using EventStream on symbol the above can also be written as: 

{
	{ SinOsc.ar(exprand(400, 1800) * [1, 1.25], 0, 0.1) }.play.dur(0.01, exprand(0.01, 3));
	\wait.stream(Pfuncn({ exprand(0.02, 1) }, 52));
}.sched;

//:Which makes more sense if you are using patterns for rhythm: 

{
	{ SinOsc.ar(\freq.pseq([400, 500, 600, 800].pyramid(5), inf), 0, 0.1) }.play.dur(0.01, 0.3);
	\wait.pseq([0.1, 0.2, 0.4, 0.8].pyramid(5), 3);
}.sched;
