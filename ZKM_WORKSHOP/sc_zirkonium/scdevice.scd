
// General Setup -- Do this first!
(
~numberOfChannels = 8;
Server.local.options.numInputBusChannels = ~numberOfChannels;
Server.local.options.numOutputBusChannels = ~numberOfChannels;
Server.local.options.device = "Zirkonium";
)

// Start Zirkonium, then launch Audio MIDI Setup and select Zirkonium as the 
// Standard Input and Output. Then boot a server and run the following.


//
//	Audio
//
//	Generate the audio that can be panned by Zirkonium. This example generates 8 channels of audio.
// 	The Zirkonium Device can accept up to 16 audio channels.
//
(
{ 
	var noise, numChannels, numChannelsOver2;
	numChannels = ~numberOfChannels;
	numChannelsOver2 = (numChannels/2).asInteger;
	noise = PinkNoise.ar(Array.fill(numChannels, 0.005));
	Ringz.ar(noise, Harmonics(numChannelsOver2).rand(50, 400) ++ Harmonics(numChannels - numChannelsOver2).rand(400, 800), Harmonics(numChannels).rand(1, 2));
}.play
)




//
// 	Control Example 1 -- Spherical Coordinates
//
//	Control the positions of the 8 channels from SuperCollider. This example uses the
//	"/pan/az" command which takes coordinates in spherical form. 
//
// 	Compare to the other control examples. See the Zirkonium documentation 
// 	for more information about the OSC interface.
//  	
(
var slider;
var text;
var panCtl;
var azSpan = 0.0, znSpan = 0.0;
//azSpan = 0.1;
//znSpan = 0.1;

	// Connection to Zirkonium
z = NetAddr("localhost", 50808);

w = SCWindow("Dome", Rect(128, 128, 400, 256));
w.front;
slider = SC2DSlider(w, Rect(10, 10, 200, 200));
slider.x = 0.5;
slider.y = 0.0;
slider.action = { arg widget; panCtl.value(widget.x, widget.y) };

panCtl = { arg x, y;
	var panx, pany;
	var polar;
	var zenith, azimuth;
	var dazimuth;
	var numChannels = 8;
	var panCommands;
	
		// X and Y are swapped in Zirkonium (X is height)
	panx = y; pany = x;
	
		// [0, 0] needs to be in the middle
	panx = (panx - 0.5) * 2.0;
	pany = 1.0 - pany;
	pany = (pany - 0.5) * 2.0;
	polar = Point(panx, pany).asPolar;
	
		// Project onto the surface of a sphere
	zenith = (0.5 - (polar.rho * 0.5)).max(0.0);
	azimuth = polar.theta / 3.141593;

	// DEBUG	
//	[azimuth, zenith].postln;
	panCommands = List.new;
	dazimuth = 2.0 / numChannels;
	numChannels.do { arg i; panCommands.add(["/pan/az", i, azimuth + (i * dazimuth), zenith, azSpan, znSpan, 1.0]) };
	z.sendBundle(0.0, *panCommands);
};
)




//
// 	Control Example 2 -- Speakers (Azimuth / Zenith)
//
//	Control the positions of the 2 of the 8 channels from SuperCollider. This example uses the
//	"/pan/speaker/az" command which takes a coordinates in spherical formant moves the ID to the
// 	speaker nearest the specified position.
//
// 	Compare to the other control examples. See the Zirkonium documentation 
// 	for more information about the OSC interface.
//  	
(
z = NetAddr("localhost", 50808);
z.sendMsg("/pan/speaker/az", 0, 0.1, 0.1, 1.0);
z.sendMsg("/pan/speaker/az", 1, 0.7, 0.0, 1.0);
)




//
// 	Control Example 3 -- Speakers (X/ Y)
//
//	Control the positions of the 2 of the 8 channels from SuperCollider. This example uses the
//	"/pan/speaker/xy" command which takes a coordinates in cartesian formant moves the ID to the
// 	speaker nearest the specified position.
//
// 	Compare to the other control examples. See the Zirkonium documentation 
// 	for more information about the OSC interface.
//  	
(
z = NetAddr("localhost", 50808);
z.sendMsg("/pan/speaker/xy", 0, 0.3, 0.3, 1.0);
z.sendMsg("/pan/speaker/xy", 1, -0.4, -0.4, 1.0);
)




//
// 	Control Example 4 -- Cartesian
//
//	Control the positions of the channel 1 from SuperCollider. This example uses the
//	"/pan/xy" command which takes a coordinates in cartesian form.
//
// 	Compare to the other control examples. See the Zirkonium documentation 
// 	for more information about the OSC interface.
//  	
(
var slider;
var text;
var panCtl;

	// Connection to Zirkonium
z = NetAddr("localhost", 50808);

w = SCWindow("Dome", Rect(128, 128, 400, 256));
w.front;
slider = SC2DSlider(w, Rect(10, 10, 200, 200));
slider.x = 0.5;
slider.y = 0.0;
slider.action = { arg widget; panCtl.value(widget.x, widget.y) };

panCtl = { arg x, y;
	var panx, pany;
	
		// X and Y are swapped in Zirkonium (X is height)
	panx = y; pany = x;
	
		// [0, 0] needs to be in the middle
	panx = (panx - 0.5) * 2.0;
	pany = 1.0 - pany;
	pany = (pany - 0.5) * 2.0;

	z.sendMsg("/pan/xy", 0, panx, pany, 0.3, 0.3, 1.0);
};
)

//
// 	Control Example 5 -- Device
//
//   Normally, OSC commands to Zirkonium are routed to the frontmost window, but it is
//   possible to address a specific window or the device by specifying that in the OSC command.
//
//   Compare to Example 1.
//  	
(
var slider;
var text;
var panCtl;
var azSpan = 0.0, znSpan = 0.0;
//azSpan = 0.1;
//znSpan = 0.1;

	// Connection to Zirkonium
z = NetAddr("localhost", 50808);

w = SCWindow("Dome", Rect(128, 128, 400, 256));
w.front;
slider = SC2DSlider(w, Rect(10, 10, 200, 200));
slider.x = 0.5;
slider.y = 0.0;
slider.action = { arg widget; panCtl.value(widget.x, widget.y) };

panCtl = { arg x, y;
	var panx, pany;
	var polar;
	var zenith, azimuth;
	var dazimuth;
	var numChannels = 8;
	var panCommands;
	
		// X and Y are swapped in Zirkonium (X is height)
	panx = y; pany = x;
	
		// [0, 0] needs to be in the middle
	panx = (panx - 0.5) * 2.0;
	pany = 1.0 - pany;
	pany = (pany - 0.5) * 2.0;
	polar = Point(panx, pany).asPolar;
	
		// Project onto the surface of a sphere
	zenith = (0.5 - (polar.rho * 0.5)).max(0.0);
	azimuth = polar.theta / 3.141593;

	// DEBUG	
//	[azimuth, zenith].postln;
	panCommands = List.new;
	dazimuth = 2.0 / numChannels;
	numChannels.do { arg i; panCommands.add(["/pan/az", i, azimuth + (i * dazimuth), zenith, azSpan, znSpan, 1.0, "__device__"]) };
	z.sendBundle(0.0, *panCommands);
};
)

