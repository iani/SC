
Rhythmanalysis2 {
	classvar <numChans = 7;
	*edit {
		format("open %", this.path.escapeChar($ )).unixCmd;
	}

	*path { ^this.filenameSymbol.asString.dirname }
	*hpath { ^this.filenameSymbol.asString.dirname +/+ "Help" }
	*setup {
		Server.default.reboot;
		BufferResource.loadPaths(
			"/Users/iani2/Music/sounds/osmosis_sounds_wav/seals_normalized/*.wav".pathMatch
		);
		BufferResource.loadPaths(
			"/Users/iani2/Music/sounds/osmosis_sounds_wav/swallows/*.wav".pathMatch
		);
//		this.hload("Rdefs.scd");
	}

	*hload { | filename | load(this.hpath +/+ filename) }
}

Rout {
	classvar <>numChans = 2;
	*ar { | out = 0, source | ^Out.ar(out % numChans, source); }
}

Rclock {
	classvar startTime, window, timer, timeRout;
	
	*reset {
		if (window.notNil) {
			window.close;
			window = nil;
			if (timeRout.notNil) { timeRout.stop; timeRout = nil };
		}
	}
	
	*start {
	//:---
		if (window.notNil) { ^nil };
		startTime = Date.getDate.bootSeconds;
		window = Window("TIME!", Rect(1240, 50, 19a0, 100)).front;
		timer = NumberBox(window, window.view.bounds);
		timer.font = Font("Helvetica", 72);
		timeRout = {
			var dt;
			loop {
				1.wait;
				dt = Date.getDate.bootSeconds - startTime;
				timer.value = (dt / 60).floor(1) + (dt / 60 mod: 1 * 60 / 100).round(0.01);
			};
		}.fork(AppClock);	
	}	
	
}


/*

Rclock.start;
Rclock.reset;

*/