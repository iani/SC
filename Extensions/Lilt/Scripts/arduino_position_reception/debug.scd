r = OSCresponder(nil, "/distance", { | ... args | args.postln; });

r.add;
r.remove;
q = OSCresponder(nil, "/test", { | ... args | args.postln; });

q.add;

NetAddr("127.0.0.1", 57120).sendMsg("/test");