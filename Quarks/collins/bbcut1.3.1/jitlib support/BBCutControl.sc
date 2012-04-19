//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCut for JITLib, 20/2/04 thanks julian

BBCutControl : AbstractPlayControl {

	play { source.play(1.0); } //quantise is 1.0 for jitlib compatability
	
	stop { source.stop; }
	pause { source.run(false) }
	resume { source.run(true) }
	
	free { source.kill; }
	
	build { arg proxy;
		^source.buildForProxy(proxy, channelOffset);
	}
	
}