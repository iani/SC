
+ Document {
	*prBasicNew { 
		^super.new.addNotifications;
	}


	addNotifications {
		NotificationCenter.notify(Document, \opened, this);		this.toFrontAction = {
			NotificationCenter.notify(Document, \toFront, this); 
		};
		this.endFrontAction = { 
			NotificationCenter.notify(Document, \endFront, this); };		this.onClose = { NotificationCenter.notify(Document, \closed, this); };
	}
}