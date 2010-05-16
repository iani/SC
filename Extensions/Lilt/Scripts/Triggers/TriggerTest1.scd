var script;
script = ~script;
~controlSpecs = [
	['t_out', 20, 20000, 'exp', 0, 400] // 't_out' as name creates a TrOutput parameter
];

~script.routine {
	var outputs;
	outputs = script.output.output;
	loop {
//		outputs do: { | o | o.set(400 rrand: 4000.0); };
		script.output.set(400 rrand: 4000.0);
		0.5.wait;
	};
};

~saveData = { | envir, file |
//	{ "saving data".postln } ! 10;
	file.putAll("hello".asCompileString);
};

~loadData = { | data ... args |
	format("proudly presenting my data: % and %", data, args).postln;
};

/*
~start = {
      "hello".postln;
      ~script.output.postln;
};
*/