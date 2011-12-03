
Rhythmanalysis {
	classvar <numChans = 24;
	*edit {
		format("open %", this.path.escapeChar($ )).unixCmd;
	}

	*path { ^this.filenameSymbol.asString.dirname }
	*hpath { ^this.filenameSymbol.asString.dirname +/+ "Help" }
	*setup {
		Server.default.options.numOutputBusChannels = numChans;
		Server.default.options.sampleRate = 48000;
		Server.default.reboot;
		BufferResource.loadList(this.hpath +/+ "RAbuffers.scd");
		this.hload("Rdefs.scd");
	}

	*hload { | filename | load(this.hpath +/+ filename) }
	
	*test01 { 'a11wlk01'.buffer.play; }	
	*test02 { 'swallows10minL'.buffer.play; }	

	*test03 {
		'swallows10minL'.buffer.play;		
	}	

	*play {{
		'weddell1'.buffer.play({ | buf |
			Rout.ar(0, PlayBuf.ar(1, buf)); 	
		});
		23.wait;
			
	}.fork}
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
		window = Window("TIME!", Rect(1240, 50, 190, 100)).front;
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