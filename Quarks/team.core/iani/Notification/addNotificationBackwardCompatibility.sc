+ Object {
	
	//temporary backwards compatibility to iz.core
	
	objectClosed { 
		Notification.removeNotifiersOf(this);
		Notification.removeListenersOf(this); 
	} 
}