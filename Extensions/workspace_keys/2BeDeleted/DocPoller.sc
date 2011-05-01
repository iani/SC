/*
DocPoller.start;
DocPoller.stop;
DocPoller.toggle;

DocPoller.poller;
*/

DocPoller {
	classvar dependants;
	classvar <poller;
	
	*makeUserMenuItems {
		CocoaMenuItem.addToMenu("User Menu", "Toggle DocPoller", ["0", false, false], { 
			this.toggle;
		});

	}
	*toggle {
		if (this.isRunning) { this.stop } { this.start }
	}
	
	*isRunning { ^poller.notNil }
	
	*start {
		this.makeUserMenuItems;
		if (this.isRunning.not) { this.startPoller };
		if (dependants.size == 0) { this.makeWindow };
		CmdPeriod remove: this; // prevent adding self twice
		CmdPeriod add: this;
	}
	
	*startPoller {
		poller = {
			loop {
				0.003.wait;
				dependants do: { | d | d.update(Document.allDocuments) };
			};
		}.fork(AppClock);
	}
	
	*cmdPeriod {
		this.pollerStopped;
		this.startPoller;
	}


	*dependants { ^dependants.asArray }

	*makeWindow { DocListWindow.new.add }

	*stop {
		if (this.isRunning) { 
			poller.stop;
			this.pollerStopped;
		};
		dependants.copy do: _.close;	
		CmdPeriod remove: this;
	}
	
	*pollerStopped { poller = nil }
	
	*add { | dependant |
		dependants = dependants add: dependant;	
	}
	
	*remove { | dependant |
		dependants remove: dependant;	
	}
}