//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCut for JITLib, 20/2/04 thanks julian

+BBCut {

	proxyControlClass {
		^BBCutControl
	}

	buildForProxy { arg proxy, channelOffset=0;
	var player, ok, index, server, numChannels, rate, grp, busindex;
	if(proxy.clock.notNil,{tempoclock = proxy.clock});
	
		ok = if(proxy.isNeutral) { 
			rate = 'audio';
			numChannels = 2;
			proxy.initBus(rate, numChannels);
		} {
			rate = proxy.rate; // if proxy is initialized, it is user's responsibility
			numChannels = proxy.numChannels;
			true
		};
		if(ok) { 
				index = proxy.index;
				grp = proxy.group;
						
				busindex=channelOffset % numChannels + index;
				server = proxy.server;
				
				//groups will be killed when bbcut freed for replacement, so don't pass in grp itself!
				bbcgarray.do({arg val; val.reassign(busindex,Group.head(grp))});
				
				true;
				} { false }	
	}
} 

