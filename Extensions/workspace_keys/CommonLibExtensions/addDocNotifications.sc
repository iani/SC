
+ Document {
	*prBasicNew {
		^super.new.notify;
	}

	notify { 	NotificationCenter.notify(Document, \opened, this); }

	addNotifications {
		this.toFrontAction = {
			NotificationCenter.notify(Document, \toFront, this); 
		};
		this.endFrontAction = { 
			NotificationCenter.notify(Document, \endFront, this); };
		this.onClose = { NotificationCenter.notify(Document, \closed, this); };
	}

	removeNotifications { // not used by DocListWindow. May be useful for other purposes
		this.toFrontAction = nil;
		this.endFrontAction = nil;
		this.onClose = nil;
	}
}