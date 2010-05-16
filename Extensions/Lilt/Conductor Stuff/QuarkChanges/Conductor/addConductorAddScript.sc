
+ Conductor { 
	addScript { | script, argName, x, y, w = 200, h = 160, stopOnClose = true, noHeader = false |
		var starter, stopper;
		x = x ?? {{ | argScript | argScript.guiBounds.left + argScript.guiBounds.width }};
		y = y ?? {{ | argScript | argScript.guiBounds.top }};
		// On start, open a gui based on Conductor. On stop, close that gui. 
		argName = argName ?? {{ | script | script.name ++ ":conductor" }}; 
		if (stopOnClose) {
			this.gui.addDependant { | who, how | 
				if (how === \closed) { script.stopped }
			};
		};
		if (noHeader) { this.gui.header = #[] };
		starter = { | argScript, message |
			if (message === \started) {
				argScript.removeDependant(starter);
				argScript.addDependant(stopper);
				this.show1(argName.(script), x.(script), y.(script), w.(script), h.(script))			}
		};
		stopper = { | argScript, message |
			if (message === \stopped) {
				argScript.removeDependant(stopper);
				argScript.addDependant(starter);
				this.hide;
			}
		};
		if (script.isRunning) {
			script.addDependant(stopper)
		}{
			script.addDependant(starter);
		};
		if (script.envir[\start].isNil) {
			script.envir[\start] = { postf("script starting: %\n", script.name) }
		};
	}
}