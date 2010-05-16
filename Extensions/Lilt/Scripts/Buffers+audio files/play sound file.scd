/* IZ Sunday, May 11, 2008 
Play a sound file from disc, selected by the user via a file dialog. 
Keeps a list of paths of opened files, accessible via a menu.
Saves all files opened in the current session for reloading as a menu next time. 

Requires Conductor quark by Ronald Kuivila.


*/

var script, conductor;
var path, file, duration, buffer, routine, synth, out, level;
var set_parameter, load, reselect_path, default_path = 'sounds/a11wlk01.wav';

script = ~script;

set_parameter = { | param, val |
	if (synth.notNil) { synth.set(param, val) }
};

~controlSpecs = [
[\out, 0, 128, \linear, 1, 0, { | val | out = ~out = val; set_parameter.(\out, val) }],
[\level, 0, 2, \linear, 0, 1, { | val | level = ~level = val; set_parameter.(\level, val) }]
/*, [\monitor_level, 0, 2, \linear, 0, 1, { | val | level = ~level = val; set_parameter.(\monitor_level, val) }] */
];

load = { | argPath |
	path = argPath ? default_path;
	conductor[\selected_file].items = [path];
	if (script.isRunning) {
		script.stop;
		{ script.start; }.defer(0.25);
	}
};

reselect_path = { | old_path, func |
	// func may arbitrarily change path, so restore it!
	var path_index;
	old_path = path;
	func.value;
	path_index = conductor[\file_list].items.indexOf(old_path);
	if (path_index.notNil) {
		conductor[\file_list].value = path_index;
	}{
		conductor[\file_list].value = 0;
//		conductor[\selected_file].items = [path = default_path];
//		script.stop;
	};
};

path = path ? default_path;

conductor = Conductor make: { | conductor, file_button, selected_file, file_list |
	~file_button = AV(["load files", {
		"loading file".postln;
		GUI.dialog.getPaths({ | paths |
			reselect_path.(path, { 
				conductor[\file_list].items = Set.with(*(conductor[\file_list].items ++ paths collect: _.asSymbol))
					.asArray.sort;
			});
		});
	}]);
	~file_list = SV([]);
	~selected_file = SV([]);
	~file_list addDependant: { | list, how |
		if (list.items.size > 0) {
			switch(how, 
			\deleteKey, {
				reselect_path.(path, {
					list.items.remove(list.items[list.value]);
					list.changed(\items);
				})
			},
			\returnKey, {
				load.(list.items[list.value]);
			})
		}
	};
	~file_list.action = { | list |
		path = list.items[list.value] ? 'sounds/a11wlk01.wav';
	};
	conductor.gui.use { 
		~popupRect = Rect(0,0, 200, ~h);
		~listRect = { Rect(0, 0, 594, 200); };
	};
	conductor.gui.guis use: {
		~file_list = \list;
		~selected_file = { | win, name, v | ~popup.(win, name, conductor[\selected_file], Rect(0, 0, 594, 20)) };
		~instructions = { | win | GUI.staticText.new(win, Rect(0, 0, 350, 20))
			.string_("type 'return' on an item from the list below to select its file")
		};
//		~selected_file = { | win, name, v | ~popup.(win, name, conductor[\file_list], Rect(0, 0, 594, 20)) };
	};
	conductor.gui.header = [[\file_button, \instructions]];
	conductor.gui.keys = [\selected_file, \file_list];
	conductor[\selected_file].items = path.asArray;
};

script.onStart { conductor.show1(format("Sound File Selector for: %", script.name), w: 600) };

script.onStop {
//	conductor.hide;
	// all freeing of resources and processes is done in ~stop.
	// Is that the most reliable way?
	if (synth.notNil) { synth.free };
	buffer.close;
	buffer.free;
};

script.onClose { conductor.hide };

~start = {
	var play_func, target, addAction, numChannels = 1, numFrames;
	target = ~target;
	addAction = ~addAction;
	file = SoundFile.new;
	path = path ? default_path;
//	conductor[\selected_file].items = path.asArray;
	file.openRead(path.asString);
	duration = (numFrames = file.numFrames) / script.session.server.sampleRate; // get the duration
	numChannels = file.numChannels;
	file.close; // don't forget
	play_func = { | argChannels = 1 |
		synth = Synth(format("diskin_%", argChannels), 
			[\out, out, \bufnum, buffer.bufnum, 'loop', 1, \level, level], 
				target, #[\h, \t, \addBefore, \addAfter, \addReplace][addAction]
		);
		synth.onEnd({ synth = nil; script.stopped; });
	};
	buffer = Buffer.cueSoundFile(script.session.server, path.asString, 0, numChannels, 
		1024, // works good for very small files, as well as for larger ones! // numFrames min: 32768,
		completionMessage: { | buf |
			buffer = buf;
			play_func.(numChannels);
			buffer.close;
			nil;
		}
	);
};

~saveData = { | envir, file |
	file.putAll(conductor[\file_list].items.asCompileString);
};

~loadData = { | envir, data |
//	[envir, data].postln;
//	data.postln;
	conductor[\file_list].items = data;
};
