/* IZ 080803
Ping someone with OSC messages
*/

// 192.168.1.64
var addr, port;

~addr1 = 192;
~addr2 = 168;
~addr3 = 5;
~addr4 = 66;
~port = 9001;

~makeAddr = { | addr1, addr2, addr3, addr4, port |
	format("%.%.%.%", addr1, addr2, addr3, addr4).postln;
	addr = NetAddr(format("%.%.%.%", addr1, addr2, addr3, addr4), port);
};

~controlSpecs = [
[\addr1, 0, 255, \linear, 1, ~addr1, { | val |
	~addr1 = val;
	~makeAddr.(~addr1, ~addr2, ~addr3, ~addr4, ~port);
}],
[\addr2, 0, 255, \linear, 1, ~addr2, { | val |
	~addr2 = val;
	~makeAddr.(~addr1, ~addr2, ~addr3, ~addr4, ~port);
}],
[\addr3, 0, 255, \linear, 1, ~addr3, { | val |
	~addr3 = val;
	~makeAddr.(~addr1, ~addr2, ~addr3, ~addr4, ~port);
}],
[\addr4, 0, 255, \linear, 1, ~addr4, { | val |
	~addr4 = val;
	~makeAddr.(~addr1, ~addr2, ~addr3, ~addr4, ~port);
}],
[\port, 0, 32000, \linear, 1, ~port, { | val |
	~port = val;
	~makeAddr.(~addr1, ~addr2, ~addr3, ~addr4, ~port);
}]
];

~script.routine {
	loop {
		1.wait;
//		addr.postln.sendMsg("/test", Date.getDate.asctime.postln);
		addr.postln.sendBundle(0, ["/contours"] ++ Array.rand(5, 1, 5));	}
};

