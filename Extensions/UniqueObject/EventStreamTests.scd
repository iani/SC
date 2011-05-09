
//:Experimental: Trying with EnvirStream


{
	\default.mplay([\freq, (70, 72..80).choose.midicps]).dur(0.3, exprand(0.1, 1.0));
	EventStream(\hocketus, Pser([0.25, 0.5, 0.5, 1.0] / 3, 100)).next;
}.schedEnvir;


{
	\default.mplay([\freq, [50, 55, 57].choose.midicps]).dur(0.1, exprand(0.1, 0.5));
	EventStream(\hocketus, Pser([1.25, 1] / 3.0, 50)).next;
}.schedEnvir;

