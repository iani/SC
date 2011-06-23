

/*
~int1 = Bummer.read(s, "sounds/_Evfer/int01.aif");

~int1.brate_(-1).play0(0.001, 0.5, 0.1);

*/

	Bummer : Buffer {
	
	var <>bout = 0, <>batt = 0.1, <>bsus = 2.0, <>brls = 2.5, <>bmul = 1.0, <>bloop = true;
	var <>btrig = 0, <>brate = 1.0, <>bstart = 0, <>bend = 1, <>breset = 0, <>bpan = 0;

	play { arg loop = false;
		^{ var player;
			player = PlayBuf.ar(
				numChannels,
				bufnum,
				BufRateScale.kr(bufnum),
				loop: loop.binaryValue
			);
			loop.not.if(FreeSelfWhenDone.kr(player));
			player * bmul;
		}.play(Server.default);
	}

	play0 { arg  att, sus, rls, mul, trig, rate, start, end, reset, loop, pan, out;

/*		if (att.notNil) { batt = att };
		if (sus.notNil) { bsus = sus };
		if (rls.notNil) { brls = rls };
		if (mul.notNil) { bmul = mul };
		if (trig.notNil) { btrig = trig };
		if (rate.notNil) { brate = rate };
		if (end.notNil) { bend = end };
		if (reset.notNil) { breset = reset };
		if (pan.notNil) { bpan = pan };
		if (out.notNil) { bout = out };
		if (loop.notNil) { bloop = loop };
*/		
		//SHORTCUTS

		batt = att ? batt;
		bsus = sus ? bsus;
		brls = rls ? brls;
		bmul = mul ? bmul;
		btrig = trig ? btrig;
		brate = rate ? brate;
		bstart = start ? bstart;
		bend = end ? bend;
		breset = reset ? breset;
		bpan = pan ? bpan;
		bout = out ? bout;
		bloop = loop ? bloop;
		
		^{ var player, panlayer, env;
			
			env =  EnvGen.ar(
				Env.new([0, 1, 0.8,  0], [batt, bsus, brls], 'linear', releaseNode: nil), 
				1, 
				doneAction: 2
			);
			player = BufRd.ar(
						numChannels,
						bufnum, 
						Phasor.ar(
							btrig, 
							BufRateScale.kr(bufnum) * brate, 
							BufFrames.kr(bufnum) * bstart, 
							BufFrames.kr(bufnum) * bend, 
							BufFrames.kr(bufnum) * breset
						),
						loop: bloop.binaryValue
					);
			panlayer = Pan2.ar(player, bpan);
			Out.ar(bout, panlayer * bmul *env);
		}.play(Server.default);
	}




/*
	play0 { arg  att, sus, rls, mul, trig, rate, start, end, reset, loop, pan, out;
		
		//if (att.notNil) { batt = att }; ->
		// SHORTCUT: 
		
		batt = att ? batt;
		bsus = sus ? bsus;
		brls = rls ? brls;
		bmul = mul ? bmul;
		btrig = trig ? btrig;
		brate = rate ? brate;
		bstart = start ? bstart;
		bend = end ? bend;
		breset = reset ? breset;
		bpan = pan ? bpan;
		bout = out ? bout;
		bloop = loop ? bloop;
		
		^{ var player, panlayer, env;
			
			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [batt, bsus, brls], 'linear', releaseNode: nil), 1, doneAction: 2);
			player = BufRd.ar(
						numChannels,
						bufnum, 
						Phasor.ar(
							btrig, 
							BufRateScale.kr(bufnum) * brate, 
							bstart * BufFrames.kr(bufnum), 
							BufFrames.kr(bufnum) * bend, 
							BufFrames.kr(bufnum) * breset
						),
						loop: bloop
					);
			loop.not.if(FreeSelfWhenDone.kr(player));
			panlayer = Pan4.ar(player, FSinOsc.kr(bpan), FSinOsc.kr(pan), 0.3);
			Out.ar(bout, panlayer * bmul *env);
		}.play(Server.default);
	}
*/

/*	play0 { arg  att = 0.1, sus = 2.0, rls = 3.9, mul = 1, trig = 0, rate = 1, start = 0, end = 1, reset = 0, loop = true, pan = 0, out = 0 ;
		
		batt = att;
		bsus = sus;
		brls = rls;
		bmul = mul;
		btrig = trig;
		brate = rate;
		bstart = start;
		bend = end;
		breset = reset;
		bpan = pan;
		bout = out;
		
		^{ var player, panlayer, env;
			
			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [batt, bsus, brls], 'linear', releaseNode: nil), 1, doneAction: 2);
			player = BufRd.ar(
						numChannels,
						bufnum, 
						Phasor.ar(
							trig, 
							BufRateScale.kr(bufnum) * rate, 
							start * BufFrames.kr(bufnum), 
							BufFrames.kr(bufnum) * end, 
							BufFrames.kr(bufnum) * reset
						),
						loop: loop.binaryValue
					);
			loop.not.if(FreeSelfWhenDone.kr(player));
			panlayer =Pan4.ar(player, FSinOsc.kr(pan), FSinOsc.kr(pan), 0.3);
			Out.ar(out, panlayer * mul *env);
		}.play(Server.default);
	}*/
/*
	playUp { arg loop = true;
		^{ var player, panlayer, env;
			
			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: nil), 1, doneAction: 2);
			player = BufRd.ar(
						numChannels,
						bufnum, 
						Phasor.ar(
							trig, 
							BufRateScale.kr(bufnum) * rate*XLine.kr(0.1, 1.9, 4), 
							start * BufFrames.kr(bufnum), 
							BufFrames.kr(bufnum) * end, 
							BufFrames.kr(bufnum) * reset
						),
						loop: loop.binaryValue
					);
			loop.not.if(FreeSelfWhenDone.kr(player));
			panlayer = Pan4.ar(player, FSinOsc.kr(pan), FSinOsc.kr(pan), 0.3);
			Out.ar(out, panlayer * mul *env);
		}.play(Server.default);
	}

	playDown { arg loop = true;
		^{ var player, panlayer, env;
			
			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: nil), 1, doneAction: 2);
			player = BufRd.ar(
						numChannels,
						bufnum, 
						Phasor.ar(
							trig, 
							BufRateScale.kr(bufnum) * rate*XLine.kr(1.9, 0.1, 4), 
							start * BufFrames.kr(bufnum), 
							BufFrames.kr(bufnum) * end, 
							BufFrames.kr(bufnum) * reset
						),
						loop: loop.binaryValue
					);
			loop.not.if(FreeSelfWhenDone.kr(player));
			panlayer = Pan4.ar(player, FSinOsc.kr(pan), FSinOsc.kr(pan), 0.3);
			Out.ar(out, panlayer * mul *env);
		}.play(Server.default);
	}

	playVib { arg loop = true;
		^{ var player, panlayer, env;
			
			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: nil), 1, doneAction: 2);
			player = BufRd.ar(
						numChannels,
						bufnum, 
						Phasor.ar(
							trig, 
							BufRateScale.kr(bufnum) * rate*SinOsc.ar(freq: 3, mul: Line.kr(0, 15, 3), add: 1.0), 
							start * BufFrames.kr(bufnum), 
							BufFrames.kr(bufnum) * end, 
							BufFrames.kr(bufnum) * reset
						),
						loop: loop.binaryValue
					);
			loop.not.if(FreeSelfWhenDone.kr(player));
			panlayer = Pan4.ar(player, FSinOsc.kr(pan), FSinOsc.kr(pan), 0.3);
			Out.ar(out, panlayer * mul *env);
		}.play(Server.default);
	}


*/
/*
~ats1 = Bummer.read(s, "sounds/_Evfer/ates01.aif");
~ats2 = Bummer.read(s, "sounds/_Evfer/ates02.aif");
~ats3 = Bummer.read(s, "sounds/_Evfer/ates03.aif");
~ats4 = Bummer.read(s, "sounds/_Evfer/ates04.aif");

~kick1 = Bummer.read(s, "sounds/_Evfer/kick1.aif");
~bass1 = Bummer.read(s, "sounds/_Evfer/bass01.aif");
~bass2 = Bummer.read(s, "sounds/_Evfer/bassGen01.aif");
~bass3 = Bummer.read(s, "sounds/_Evfer/bassStr01.aif");

~citMin = Bummer.read(s, "sounds/_Evfer/citMin.aif");
~cirMin = Bummer.read(s, "sounds/_Evfer/cirMin.aif");
~circir1 = Bummer.read(s, "sounds/_Evfer/circir1.aif");

~dlStr = Bummer.read(s, "sounds/_Evfer/dlStr.aif");
~dlyStr1 = Bummer.read(s, "sounds/_Evfer/dlyStrA1.aif");
~dlyStr2 = Bummer.read(s, "sounds/_Evfer/dlyStrA2.aif");
~dlyStr3 = Bummer.read(s, "sounds/_Evfer/dlyStrA3.aif");

~fub1 = Bummer.read(s, "sounds/_Evfer/fub1.aif");

~dran1 = Bummer.read(s, "sounds/_Evfer/dran01.aif");
~dran2 = Bummer.read(s, "sounds/_Evfer/dran02.aif");

~brdk1 = Bummer.read(s, "sounds/_Evfer/bardak1.aif");

~gtr1 = Bummer.read(s, "sounds/_Evfer/gtr1.aif");
~git1 = Bummer.read(s, "sounds/_Evfer/git1.aif");

~gir1 = Bummer.read(s, "sounds/_Evfer/gir01.aif");
~gir2 = Bummer.read(s, "sounds/_Evfer/gir02.aif");
~gir3 = Bummer.read(s, "sounds/_Evfer/gir03.aif");

~int1 = Bummer.read(s, "sounds/_Evfer/int01.aif");
~int2 = Bummer.read(s, "sounds/_Evfer/int02.aif");
~int3 = Bummer.read(s, "sounds/_Evfer/int03.aif");
~int4 = Bummer.read(s, "sounds/_Evfer/int04.aif");
~int5 = Bummer.read(s, "sounds/_Evfer/int05.aif");
~int6 = Bummer.read(s, "sounds/_Evfer/int06.aif");

~zil01 = Bummer.read(s, "sounds/~zkm1/zilA01.aif");
~zil02 = Bummer.read(s, "sounds/~zkm1/zilA02.aif");
~zil03 = Bummer.read(s, "sounds/~zkm1/zilA03.aif");
~zil04 = Bummer.read(s, "sounds/~zkm1/zilA04.aif");


~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;

~ats2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;

~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playDown;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;

~ats4.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(1.2).pan_(0.0).out_(0).play0;
~ats4.att_(1.1).sus_(2.5).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(1.2).pan_(0.0).out_(0).playUp;


~kick1.att_(0.1).sus_(2).rls_(1.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playDown;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;

~bass1.att_(0.1).sus_(1).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~bass1.att_(1.1).sus_(1).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;

~bass2.att_(0.1).sus_(2).rls_(5.0).rate_(1.8 rrand: 1.5).mul_(1.9).pan_(0.1).out_(0).play0; 
~bass3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(-0.2).out_(0).play0;

~citMin.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~cirMin.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~circir1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;

~dlStr.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~dlyStr1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~dlyStr2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~dlyStr3.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;

~fub1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;

~dran1.att_(0.1).sus_(2).rls_(5.0).rate_(0.2).mul_(0.9).pan_(0.0).out_(0).play0;
~dran2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;

~brdk1.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~brdk1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).playUp;
~brdk1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).playDown;

~gtr1.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~gtr1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).playUp;
~gtr1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).playDown;

~git1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;

~gir1.att_(1.1).sus_(2).rls_(7.0).rate_(-0.8 rrand: -1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~gir1.rate_(0.5 rrand: 2).play0;
~gir2.rate_(0.5).play0;
~gir2.rate_(0.5 rrand: 2).play0;
~gir3.rate_(0.5 rrand: 2).play0;
~gir3.rate_(0.5 rrand: 2).play0;

~int1.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(0).playVib;
~int2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~int2.att_(0.1).sus_(2).rls_(5.0).rate_(1.08).mul_(0.9).pan_(0.0).out_(0).playVib;
~int3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~int4.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~int5.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~int6.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;

~zil01.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~zil02.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~zil03.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;
~zil04.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(0).play0;


*/

//{ | rate = 1 | BufRd.ar(1, b, LFDNoise1.ar(rate, BufFrames.ir(b))) }
/*	
	| trig = 0, rate = 1, start = 0, end = 1, reset = 0 | 
	BufRd.ar(
		1, 
		b, 
		Phasor.ar(
			trig, 
			BufRateScale.kr(b) * rate, 
			start * BufFrames.kr(b), 
			BufFrames.kr(b) * end, 
			BufFrames.kr(b) * reset
		)
	) 
*/



/*	play0 { arg loop = true, out = 0, mul = 1;
		^{ var player, brate, env;
			
			env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', releaseNode: nil), 1, doneAction: 2);
			brate = BufRateScale.kr(bufnum);
			player = PlayBuf.ar(
						numChannels,
						bufnum, 
						brate*rate,
						loop: loop.binaryValue
					)!2;
			loop.not.if(FreeSelfWhenDone.kr(player));
			
			Out.ar(outSet, player * mul *env);
		}.play(Server.default);
	}

	play2 { arg loop = false, mul = 1;
		^{ var player, rate;
			rate = BufRateScale.kr(bufnum);
			player = PlayBuf.ar(
						numChannels,
						bufnum, 
						rate*rateSet,
						loop: loop.binaryValue
					)!2;
			loop.not.if(FreeSelfWhenDone.kr(player));
			player * mul;
		}.play(Server.default);
	}

	play1 { arg loop = false, out = 0, mul = 1;
		^{ var player, rate;
			rate = BufRateScale.kr(bufnum);
			player = PlayBuf.ar(
						numChannels,
						bufnum, 
						rate*rateSet,
						loop: loop.binaryValue
					)!2;
			loop.not.if(FreeSelfWhenDone.kr(player));
			
			Out.ar(outSet, player * mul);
		}.play(Server.default);
	}



	play_mousex { arg loop = false, mul = 1, rate = 1;
		^{ var player, rate;
			rate = BufRateScale.kr(bufnum)*MouseX.kr(0.1, 1.9, 2);
			player = PlayBuf.ar(
						numChannels,
						bufnum, 
						rate*rateSet,
						loop: loop.binaryValue
					)!2;
			loop.not.if(FreeSelfWhenDone.kr(player));
			player * mul;
		}.play(Server.default);
	}

*/


}