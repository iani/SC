
b = Buffer(Server.default, 1024, 1);
b.alloc


Buffer.alloc(Server.default, 1024, 1, { | ... args | args.postln; })