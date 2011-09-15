/* Utility function.
Useful for preserving in History the creation and playing of different ProxySpaces
IZ, 2011 08 17

TODO: Add option for switching to the new proxyspace right afer play. 
*/

+ ProxySpace {
	*createIfNeededAndPlay { | name = \default, server, clock |
		var proxySpace;
		proxySpace = all[name];
		if (proxySpace.isNil) {
			proxySpace = ProxySpace(server ?? { Server.default }, name, clock ?? { TempoClock.new });
		};
		server.waitForBoot({ 
			proxySpace.play;
		});
	}
}
