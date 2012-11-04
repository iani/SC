/* IZ Thu 16 August 2012  6:28 PM EEST

Uses default ProxySpace from ProxyCentral.

12345678
qwertyui
asdfghjk
zxcvbnm,

ScriptMixer.activeMixer;

ScriptMixer();

*/

ScriptMixer : AppModel {
	classvar activeMixer;		// instance of mixer whose window is foremost
	var <>numStrips = 8, <numPresets = 8, <proxySpace, <strips, <proxyList;
	var <stripWidth = 82, mWidth = 75, nWidth = 50;
	var <>font;
	var <valueCache;	// fast access to values for storing and restoring presets

	*initClass {
		Class.initClassTree(MIDISpecs);
		MIDISpecs.put(this, this.uc33eSpecs);
	}

	*activeMixer {
		activeMixer ?? { activeMixer = this.new };
		^activeMixer;
	}

	*new { | numStrips = 8, numPresets = 8 | ^super.new(numStrips, numPresets).init; }

	init {
		font = Font.default.size_(9);
		proxySpace = ProxyCentral.default.proxySpace;
		this.makeWindow;
		this.initMIDI;
	}

	makeWindow {
		this.window({ | w, a |
			var tabbedView, winWidth;
			winWidth = stripWidth * numStrips;
			font = Font.default.size_(10);
			w.name_("Proxy Script Mixer")
			.bounds_(Rect(Window.screenBounds.width - winWidth, 0, winWidth, 250));
			tabbedView = TabbedView(w, labels: ["1-8", "q-i", "a-k", "z-,"]);
			tabbedView.views do: { | view, viewNum |
				viewNum = viewNum * 8;
				view.layout = HLayout(
					*({ | i | VLayout(
						i = i + viewNum;
						a.popUpMenu(format("proxy%", i).asSymbol).proxyList(ProxyCentral.default.proxySpace, i)
						.view.fixedWidth_(mWidth).font_(font).background_(Color(0.9, 1, 0.9)),
						a.popUpMenu(format("knob%", i).asSymbol).proxyControlList(format("proxy%", i).asSymbol, 3)
						.view.fixedWidth_(mWidth).font_(font),
						a.knob(format("knob%", i).asSymbol).proxyControl.view,
						HLayout(
							a.slider(format("slider%", i).asSymbol).proxyControl.view.orientation_(\vertical),
							VLayout(
								a.numberBox(format("knob%", i).asSymbol).proxyControl.view.font_(font).fixedWidth_(nWidth),
								a.numberBox(format("slider%", i).asSymbol).proxyControl.view.font_(font).fixedWidth_(nWidth),
								a.button(format("proxy%", i).asSymbol)
								.action_({ "TOODO".postln; })
								.view.states_([["ed"]]).font_(font).fixedWidth_(nWidth),
								a.button(format("proxy%", i).asSymbol).proxyWatcher
								.view.states_([[">"], ["||", nil, Color.red]]).font_(font).fixedWidth_(nWidth),
							),
						),
						a.popUpMenu(format("slider%", i).asSymbol).proxyControlList(format("proxy%", i).asSymbol, 1)
						.view.fixedWidth_(mWidth).font_(font),
					) } ! 8)
				)
			};
			this.addWindowActions(w);
		});
	}

	addWindowActions { | window |
		this.windowClosed(window, {
			this.makeInactive;
			this.disable(window);
			this.objectClosed;
		});
		this.windowToFront(window, { this.enable; this.makeActive; });
		this.windowEndFront(window, { this.disable; });
		window.addNotifier(this, \colorEnabled, {
			if (window.isClosed.not) { window.view.background = Color(*[0.7, 0.8, 0.9]) }
		});
		window.addNotifier(this, \colorDisabled, {
			if (window.isClosed.not) { window.view.background = Color(0.8, 0.8, 0.8, 0.05); };
		});
	}

	enable {
		super.enable(true);
//		strips do: _.enable;
		this.changed(\colorEnabled);
//		activeMixer = this;
	}

	disable {
		super.disable;
//		strips do: _.disable;
		this.changed(\colorDisabled);
//		if (activeMixer === this) { activeMixer = nil };
	}

	makeActive { activeMixer = this }
	makeInactive { if (activeMixer === this) { activeMixer = nil }; }
	// MIDI

	initMIDI {
		var specs, knob, slider, strip;
		specs = this.midiSpecs;
		knob = specs[\knob];
		slider = specs[\slider];
		// _mean_ shortcut creating midi specs by setting the channel number for each strip:
//		8.min(numStrips) do: { | i |
	//		strips[i].addMIDI([slider: slider.put(3, i), knob: knob.put(3, i)]);
		// }
	}

	*uc33eSpecs { // these specs are for M-Audio U-Control UC-33e, 1st program setting
		^(
			knob: [\cc, nil, 10, 0],
			slider: [\cc, nil, 7, 0]
/*			startStopButton: [\cc, { | me | me.toggle }, 18, 0],
			prevSnippet: [\cc, { | me | me.action.value }, 19, 0],
			eval: [\cc, { | me | me.action.value }, 20, 0],
			nextSnippet: [\cc, { | me | me.action.value }, 21, 0],
			firstSnippet: [\cc, { | me | me.action.value }, 22, 0],
			add: [\cc, { | me | me.action.value }, 23, 0],
			lastSnippet: [\cc, { | me | me.action.value }, 24, 0],
			resetSpecs: [\cc,  { | me | me.action.value }, 25, 0],
			toggleWindowSize: [\cc,  { | me | me.toggle }, 26, 0],
			delete: [\cc,  { | me | me.action.value }, 27, 0],
*/		)
	}

}

