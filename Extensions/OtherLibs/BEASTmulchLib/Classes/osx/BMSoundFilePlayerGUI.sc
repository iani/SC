BMSoundFilePlayerGUI : BMAbstractGUI {
	
	var player, responder, clockView, loadButton, info, dur, playButton, stopButton, clust, clust2; 	var clearButton, forwButton, backButton, clockButton, bigClock, bigText;
	
	*new { |player, name|
		^super.new.init(player).makeWindow;
	}
	
	init { |argplayer, argname|
		player = argplayer;
		name = argname ? player.name;
		player.addDependant(this);
	}
	
	makeWindow {
		window = SCWindow.new(name, Rect(220, 700, 640, 100), false);
		window.view.background_(Color.white.alpha_(0.2));
		window.view.decorator = FlowLayout(window.view.bounds, Point(10, 10), Point(10, 10));
		
		window.view.keyDownAction = { arg view,char,modifiers,unicode,keycode;
			if(unicode == 32, {player.togglePlay});
			if(unicode == 13, {player.stop});
		};
		
		clockView = SCStaticText.new(window, Rect(0,0,200,45));
		clockView.string = "00:00:00.0";
		clockView.background = HiliteGradient(Color.black.alpha_(0.1), Color.black, \v, 256, 0.5);
		clockView.font = Font("Helvetica-Bold", 18);
		clockView.stringColor = Color.new255(106, 90, 205);
		clockView.align = \center;
		
		window.view.decorator.shift(-40, 10);
		clockButton = RoundButton.new(window, Rect(0,0,25,25)).extrude_( false )
			.canFocus_(false).radius_( 3 );
		clockButton.states = [[\clock, Color.white, Color.white.alpha_(0.2)]];
		clockButton.action = {this.makeBigClock};
		
		window.view.decorator.shift(5, -10);
		
		clust = SCVLayoutView(window,Rect(10,10,200,40));
		clust2 = SCVLayoutView(window,Rect(10,10,200,40));
	     info = SCStaticText.new(clust, Rect(10,10,150,20));
	     info.font = Font("Helvetica-Bold", 12);
		dur = SCStaticText.new(clust2, Rect(10,10,150,20));
		dur.font = Font("Helvetica-Bold", 12);
		player.loading.not.if({
			player.path.notNil.if({{
				info.string = player.path.basename; 
				dur.string =  "Length:" + 
					(player.buffer.numFrames / player.buffer.sampleRate).asTimeString
				}.defer 
			});
		}, {info.string = "Loading...";});
		
		loadButton = RoundButton.new(clust, Rect(10,10,200,20)).extrude_(false).canFocus_(false);
		loadButton.states = [[\folder, Color.black,Color.clear]];
		loadButton.action = {
			var oldString;
			oldString = info.string;
			CocoaDialog.getPaths({ arg paths; 
				player.read(paths[0]);
			}, {oldString.notNil.if({{info.string = oldString}.defer})});
		};
		clearButton = RoundButton.new(clust2, Rect(10,10,200,20)).extrude_(false).canFocus_(false);
		clearButton.states = [[\x, Color.black,Color.clear]];
		clearButton.action = { player.stop; player.freeBuffer; };
		
		window.view.decorator.nextLine;
		
		stopButton = RoundButton.new(window, Rect(10,10,200,20)).extrude_(false).canFocus_(false);
		stopButton.states = [[\stop]];
		stopButton.action = { player.stop; };
		
		backButton = RoundButton.new(window, Rect(10,10,95,20)).extrude_(false).canFocus_(false);
		backButton.states = [[\rewind]];
		backButton.action = { player.rate = -6; playButton.value = 0 };
		
		playButton = RoundButton.new(window, Rect(10,10,200,20)).extrude_(false).canFocus_(false);
		playButton.states = [[\play], [\pause]];
		playButton.action = { |butt|
			switch (butt.value,
				1, {player.play;},
				0, {player.pause}
			)
			
		};
		
		forwButton = RoundButton.new(window, Rect(10,10,95,20)).extrude_( false ).canFocus_(false);
		forwButton.states = [[\forward]];
		forwButton.action = { player.rate = 6; playButton.value = 0 };
		
		window.onClose_({
			player.removeDependant(this); 
			bigClock.notNil.if({bigClock.close});
			onClose.value(this);
		});
		window.front;
	}
	
	makeBigClock {
		bigClock.isNil.if({
			bigClock = SCWindow.new("Big Clock", Rect(600, 800, 800, 180)).alwaysOnTop_(true);
			bigClock.alpha = 0.95;
			bigClock.onClose = { bigClock = nil; };
			bigText = SCStaticText.new(bigClock, Rect(0, 0, 800, 180)).resize_(5);
			bigText.string = "00:00:00.0";
			bigText.background = HiliteGradient(Color.black.alpha_(0.3), Color.black, \v, 1024, 0.5);
			bigText.font = Font("Helvetica-Bold", 120);
			bigText.stringColor = Color.new255(106, 90, 205);
			bigText.align = \center;
			bigClock.front;
		});
	}
    
    	updateTimeDisplay {| string |
		{ clockView.string = string; bigClock.notNil.if({bigText.string = string});}.defer;
	}
	
	// always updated from player
	update {arg changed, what ...args; 
		window.isClosed.not.if({
		{
		switch(what,
			\n_end, {this.updateTimeDisplay(0.getTimeString);
				{playButton.value = 0;}.defer;
			},
			\play, {
				playButton.value = 1;
			},
			\pause, {
				playButton.value = 0;
			},
			\playFailed, {
				//"Playing failed".postln; 
				this.updateTimeDisplay(0.getTimeString);
				playButton.value = 0;
				},
			\bufferFreed, {info.string = ""; dur.string = "";},
			\stop, { 
				this.updateTimeDisplay(0.getTimeString);
				playButton.value = 0;
				},
			\loading, {info.string = "Loading...";},
			\loaded, {info.string = player.path.basename; dur.string =  "Length:" + (player.buffer.numFrames / player.buffer.sampleRate).asTimeString },
			\time, { this.updateTimeDisplay(args.first.getTimeString) }
		)
		}.defer
		});
	}
	
	changed { arg what ... moreArgs;
		dependantsDictionary.at(this).do({ arg item;
			item.update(this, what, *moreArgs);
		});
	}
}

