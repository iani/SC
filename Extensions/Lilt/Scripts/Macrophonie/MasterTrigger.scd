/* IZ Saturday, May 17, 2008 
Collecting all scripts to connect ... and trigger ... in the session ...
*/

var script, scripts;

~controlSpecs = [[\t_out]];

script = ~script;
~scripts = scripts = Event.new;
Script.putGlobal(\scripts, scripts);
Script.putGlobal(\channels, #[0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16, 17, 18, 19]);
Script.putGlobal(\lower_channels, #[0, 1, 2, 3, 4, 5, 6, 7]);
Script.putGlobal(\higher_channels, #[11, 12, 13, 14, 15, 16, 17, 18, 19]);
Script.putGlobal(\fx_names, ['GraphicFilterInput', 'PV_BinScramble', 'PV_BinShift', 
	'PV_LocalMax', 'PV_MagAbove', 'PV_MagSmear', 'PV_RectComb', 'formlet'
]);
Script.putGlobal(\source_names, ['playbuf (looping)', 'playbuf (looping)[2]', 'playbuf (looping)[3]',
	'playbuf (looping)[4]', 'playbuf (looping)[5]', 'playbuf (looping)[6]', 'playbuf (looping)[7]', 'playbuf (looping)[8]'
]);

~script.session.addDependant { | session, what |
	if (what === \loaded) { 
		script.output.output.do { | p |
			scripts[p.script.name.asSymbol] = p.script;
			Script.putGlobal(p.script.name.asSymbol, p.script);
		};
		Script.putGlobal(\fx, ~fx_names collect: { | f | script.envir[f]} );
		Script.putGlobal(\sources, ~source_names collect: { | f | script.envir[f]} );
		{ scripts do: { | s | 
			s.doIfHasMIDI { s.activateMIDI };
		} }.defer(3.0);		
	};	
};

~start = {
//	~script.output.output.postln;
//	~script.output.output do: { | p | format("% - %", p.script.scriptGroup.name,
//		p.script.name).postln;
//	};
	scripts.postln;
};

/*
Script.globalParentEnvir.scripts.keys.asArray.sort.asCompileString;
[ 'GraphicFilterInput',
'PV_BinScramble', 
'PV_BinShift', 
'PV_LocalMax', 
'PV_MagAbove', 
'PV_MagSmear', 
'PV_RectComb', 
'formlet', 
///
'playbuf (looping)', 'playbuf (looping)[2]', 'playbuf (looping)[3]', 'playbuf (looping)[4]', 'playbuf (looping)[5]', 'playbuf (looping)[6]', 'playbuf (looping)[7]', 'playbuf (looping)[8]' 
]




*/