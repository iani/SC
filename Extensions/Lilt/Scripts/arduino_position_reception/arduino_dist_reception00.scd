
var dist_receiver;
var scale = 1;

~controlSpecs = [
[\scale, 0, 10, 'linear', 0, 1, { | val | scale = ~scale = val }]
];



dist_receiver = OSCresponder(nil, "/distance", { | time, resp, msg |
	(scale * msg[1]).postln; 
});

~start = {
	dist_receiver.add;
};

~stop = {
	dist_receiver.remove;
};