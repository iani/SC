/* under development

*/

+ QView {

	addInterface { | notifier ... messageActionPairs |
		messageActionPairs pairsDo: { | message, action |
			this.addNotifier(message, action);
		};
		this.onClose = { this.objectClosed };
	}

	/*
	addUpdater { | sender, message, updateAction |
		this.addNotifier(sender, message, action);
	}
	*/

}

