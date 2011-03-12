+BMAbstractMatrix {

	gui { ^BMMatrixMenuGUI(this); }

}

+BMControllerAutomator {

	gui { ^BMControllerAutomatorGUI(this) }
	
}

+BMMasterFader {

	gui { ^BMMasterFaderGUI(this) } 

}

+BMMultichannelPluginsRack {

	gui { ^BMMultichannelPluginsRackGUI(this) }
	
}

+BMSoundFilePlayer {

	gui { ^BMSoundFilePlayerGUI(this, this.name) }
	
}

+BMTrimPluginsRack {

	gui { ^BMTrimPluginsRackGUI(this) } 

}