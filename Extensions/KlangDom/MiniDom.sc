
/* testing the mini dom 

m = MiniDom.new;

m.safetest;
MiniDom.new.safetest2;
MiniDom.new.safetest2b;
MiniDom.new.safetest3;

MiniDom.new.safetest4;


[[1 ,    0 ],
[  0.75 ,    0 ],
[  0.62 ,    0 ],
[   0.5 ,    0 ],
[  0.38 ,    0 ],
[  0.25 ,    0 ],
[     0 ,    0 ],
[ -0.25 ,    0 ],
[ -0.38 ,    0 ],
[  -0.5 ,    0 ],
[ -0.62 ,    0 ],
[ -0.75 ,    0 ],
[     1 ,  0.2 ],
[  0.76 , 0.16 ],
[  0.64 ,  0.2 ],
[  0.36 ,  0.2 ],
[  0.23 , 0.16 ],
[     0 ,  0.2 ],
[ -0.24 , 0.16 ],
[ -0.36 ,  0.2 ],
[ -0.64 ,  0.2 ],
[ -0.76 , 0.16 ],
[     1 , 0.39 ],
[     0, 0.39]] * 180


*/ 

MiniDom {
	var <server;
	var <speakers;
	var <buffer;
	var <synth;
	
	*new {
		^super.new.init;	
	}
	
	init {
		Server.default = Server.internal;
		server = Server.default;	
		server.boot;
//		speakers = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); // 8 channel ring
		// 3d partial dome
		speakers = VBAPSpeakerArray.new(3, 
/*			[[-22.5, 14.97], [22.5, 14.97], [-67.5, 14.97], [67.5, 14.97], 
			[-112.5, 14.97], [112.5, 14.97], [-157.5, 14.97], [157.5, 14.97], [-45, 0], [45, 0], 
				[-90, 0], [90, 0], [-135, 0], [135, 0], [0, 0], [180, 0]]
*/		
		/*
		[[1 ,    0 ],
[  0.75 ,    0 ],
[  0.62 ,    0 ],
[   0.5 ,    0 ],
[  0.38 ,    0 ],
[  0.25 ,    0 ],
[     0 ,    0 ],
[ -0.25 ,    0 ],
[ -0.38 ,    0 ],
[  -0.5 ,    0 ],
[ -0.62 ,    0 ],
[ -0.75 ,    0 ],
[     1 ,  0.2 ],
[  0.76 , 0.16 ],
[  0.64 ,  0.2 ],
[  0.36 ,  0.2 ],
[  0.23 , 0.16 ],
[     0 ,  0.2 ],
[ -0.24 , 0.16 ],
[ -0.36 ,  0.2 ],
[ -0.64 ,  0.2 ],
[ -0.76 , 0.16 ],
[     1 , 0.39 ],
[     0, 0.39]]	

*/	

		[[1 ,    0 ],
[  0.75 ,    0 ],
[  0.62 ,    0 ],
[   0.5 ,    0 ],
[  0.38 ,    0 ],
[  0.25 ,    0 ],
[     0 ,    0 ],
[ -0.25 ,    0 ],
[ -0.38 ,    0 ],
[  -0.5 ,    0 ],
[ -0.62 ,    0 ],
[ -0.75 ,    0 ],
[     1 ,  0.2 ],
[  0.76 , 0.16 ],
[  0.64 ,  0.2 ],
[  0.36 ,  0.2 ],
[  0.23 , 0.16 ],
[     0 ,  0.2 ],
[ -0.24 , 0.16 ],
[ -0.36 ,  0.2 ],
[ -0.64 ,  0.2 ],
[ -0.76 , 0.16 ],
[     1 , 0.39 ],
[     0, 0.39]] * 180

		);

	}
	
	prepare {
		buffer = Buffer.loadCollection(server, speakers.getSetsAndMatrices);	}
	
	play {
		synth = { |azi = 0, ele = 0, spr = 0|
			VBAP.ar(8, PinkNoise.ar(0.2), buffer.bufnum, azi, ele, spr);
		}.scope;
	}
	
	test {
		{ [45, 90, 135, 180, -135, -90, -45, 0].do({|ang| synth.set(\azi, ang); 1.wait; }) }.fork;	
	}

	safetest5 {
		{
				
			Server.default = Server.internal;
			Server.internal.boot;
			server = Server.default;
			
			2.wait;
				
			speakers = VBAPSpeakerArray.new(3, 
						
//	[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ], [ 41.4, 28.8 ], [ 0, 36 ], [ -43.2, 28.8 ], [ -64.8, 36 ], [ -115.2, 36 ], [ -136.8, 28.8 ], [ 180, 70.2 ], [ 0, 70.2 ] ]

[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ], [ 41.4, 28.8 ] ]
			); // zig zag partial dome
			
			buffer = Buffer.loadCollection(server, speakers.getSetsAndMatrices);
			
			2.wait;
	
			// pan around the circle up and down
			synth = { |azi = 0, ele = 0, spr = 0|
			var source;
			source = PinkNoise.ar(0.2);
			VBAP.ar(16, source, buffer.bufnum, MouseX.kr(-180, 180), MouseY.kr(0, 90), spr);
			}.play(server);		
		}.fork(AppClock)
	}


	safetest4 {
		{
				
			Server.default = Server.internal;
			Server.internal.boot;
			server = Server.default;
			
			2.wait;
				
			speakers = VBAPSpeakerArray.new(3, 
			
/*			[[-22.5, 14.97], [22.5, 14.97], [-67.5, 14.97], 
				[67.5, 14.97], [-112.5, 14.97], [112.5, 14.97], [-157.5, 14.97], 
					[157.5, 14.97], [-45, 0], [45, 0], [-90, 0], [90, 0], [-135, 0], 
						[135, 0], [0, 0], [180, 0]]
*/
						
//			[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ] ]
			
//	[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ], [ 41.4, 28.8 ], [ 0, 36 ], [ -43.2, 28.8 ], [ -64.8, 36 ], [ -115.2, 36 ], [ -136.8, 28.8 ], [ 180, 70.2 ], [ 0, 70.2 ] ]

[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ] ]
			); // zig zag partial dome
			
			buffer = Buffer.loadCollection(server, speakers.getSetsAndMatrices);
			
			2.wait;
	
			// pan around the circle up and down
			synth = { |azi = 0, ele = 0, spr = 0|
			var source;
			source = PinkNoise.ar(0.2);
			VBAP.ar(16, source, buffer.bufnum, MouseX.kr(-180, 180), MouseY.kr(0, 90), spr);
			}.play(server);		
		}.fork(AppClock)
	}


	safetest3 {
		{
				
			Server.default = Server.internal;
			Server.internal.boot;
			server = Server.default;
			
			2.wait;
				
			speakers = VBAPSpeakerArray.new(3, 
			
/*			[[-22.5, 14.97], [22.5, 14.97], [-67.5, 14.97], 
				[67.5, 14.97], [-112.5, 14.97], [112.5, 14.97], [-157.5, 14.97], 
					[157.5, 14.97], [-45, 0], [45, 0], [-90, 0], [90, 0], [-135, 0], 
						[135, 0], [0, 0], [180, 0]]
*/
						
//			[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ] ]
			
//	[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ], [ 41.4, 28.8 ], [ 0, 36 ], [ -43.2, 28.8 ], [ -64.8, 36 ], [ -115.2, 36 ], [ -136.8, 28.8 ], [ 180, 70.2 ], [ 0, 70.2 ] ]

[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ] ]
			); // zig zag partial dome
			
			buffer = Buffer.loadCollection(server, speakers.getSetsAndMatrices);
			
			2.wait;
	
			// pan around the circle up and down
			synth = { |azi = 0, ele = 0, spr = 0|
			var source;
			source = PinkNoise.ar(0.2);
			VBAP.ar(16, source, buffer.bufnum, LFSaw.kr(0.5, 0).range(-180, 180) * -1, SinOsc.kr(3, 0).range(0, 14.97), spr);
			}.play(server);		
		}.fork(AppClock)
	}



	safetest2b {
		{
				
			Server.default = Server.internal;
			Server.internal.boot;
			server = Server.default;
			
			2.wait;
				
			speakers = VBAPSpeakerArray.new(3, 
		[[1 ,    0 ],
[  0.75 ,    0 ],
[  0.62 ,    0 ],
[   0.5 ,    0 ],
[  0.38 ,    0 ],
[  0.25 ,    0 ],
[     0 ,    0 ],
[ -0.25 ,    0 ],
[ -0.38 ,    0 ],
[  -0.5 ,    0 ],
[ -0.62 ,    0 ],
[ -0.75 ,    0 ],
[     1 ,  0.2 ],
[  0.76 , 0.16 ],
[  0.64 ,  0.2 ],
[  0.36 ,  0.2 ],
[  0.23 , 0.16 ],
[     0 ,  0.2 ],
[ -0.24 , 0.16 ],
[ -0.36 ,  0.2 ],
[ -0.64 ,  0.2 ],
[ -0.76 , 0.16 ],
[     1 , 0.39 ],
[     0, 0.39]] * 180); // zig zag partial dome
			
			buffer = Buffer.loadCollection(server, speakers.getSetsAndMatrices);
			
			2.wait;
	
			// pan around the circle up and down
			synth = { |azi = 0, ele = 0, spr = 0|
			var source;
			source = PinkNoise.ar(0.2);
			VBAP.ar(43, source, buffer.bufnum, LFSaw.kr(0.5, 0).range(-180, 180) * -1, SinOsc.kr(3, 0).range(0, 14.97), spr);
			}.play(server);		
		}.fork(AppClock)
	}


	safetest2 {
		{
				
			Server.default = Server.internal;
			Server.internal.boot;
			server = Server.default;
			
			2.wait;
				
			speakers = VBAPSpeakerArray.new(3, 
			
/*			[[-22.5, 14.97], [22.5, 14.97], [-67.5, 14.97], 
				[67.5, 14.97], [-112.5, 14.97], [112.5, 14.97], [-157.5, 14.97], 
					[157.5, 14.97], [-45, 0], [45, 0], [-90, 0], [90, 0], [-135, 0], 
						[135, 0], [0, 0], [180, 0]]
*/
						
//			[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ] ]
			
	[ [ 180, 0 ], [ 135, 0 ], [ 111.6, 0 ], [ 90, 0 ], [ 68.4, 0 ], [ 45, 0 ], [ 0, 0 ], [ -45, 0 ], [ -68.4, 0 ], [ -90, 0 ], [ -111.6, 0 ], [ -135, 0 ], [ 180, 36 ], [ 136.8, 28.8 ], [ 115.2, 36 ], [ 64.8, 36 ], [ 41.4, 28.8 ], [ 0, 36 ], [ -43.2, 28.8 ], [ -64.8, 36 ], [ -115.2, 36 ], [ -136.8, 28.8 ], [ 180, 70.2 ], [ 0, 70.2 ] ]
			); // zig zag partial dome
			
			buffer = Buffer.loadCollection(server, speakers.getSetsAndMatrices);
			
			2.wait;
	
			// pan around the circle up and down
			synth = { |azi = 0, ele = 0, spr = 0|
			var source;
			source = PinkNoise.ar(0.2);
			VBAP.ar(16, source, buffer.bufnum, MouseX.kr(-1, 1), MouseY.kr(-1, 1), spr);
			}.play(server);		
		}.fork(AppClock)
	}


	safetest	{
		{
			var a, b, x, s;
				
			Server.default = Server.internal;
			Server.internal.boot;
			s = Server.default;
			
			2.wait;
				
			a = VBAPSpeakerArray.new(3, [[-22.5, 14.97], [22.5, 14.97], [-67.5, 14.97], 
				[67.5, 14.97], [-112.5, 14.97], [112.5, 14.97], [-157.5, 14.97], 
					[157.5, 14.97], [-45, 0], [45, 0], [-90, 0], [90, 0], [-135, 0], 
						[135, 0], [0, 0], [180, 0]]); // zig zag partial dome
			
			b = Buffer.loadCollection(s, a.getSetsAndMatrices);
			
			2.wait;
	
			// pan around the circle up and down
			x = { |azi = 0, ele = 0, spr = 0|
			var source;
			source = PinkNoise.ar(0.2);
			VBAP.ar(16, source, b.bufnum, LFSaw.kr(0.5, 0).range(-180, 180) * -1, SinOsc.kr(3, 0).range(0, 14.97), spr);
			}.play;		
		}.fork(AppClock)
	}
}

/*

m = MiniDom.new;

m.prepare;
m.play;
m.test;
Server.default = s = Server.internal;
// 2D

s.boot;

f = {
	var a, b, x;
	a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); // 8 channel ring

	b = a.loadToBuffer;
	
	
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(8, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

// test them out
{[45, 90, 135, 180, -135, -90, -45, 0].do({|ang| x.set(\azi, ang); 1.wait; }) }.fork;

}
*/