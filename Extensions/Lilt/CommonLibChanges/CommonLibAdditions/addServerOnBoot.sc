/* iz 080821
make Server:onBoot have arguments as expected intuitively: 

aServer.onBoot(aFunction, doNowIfRunning)

*/

+ Server {
	onBoot { | action, doNowIfRunning = true |
		action.onBoot(this, doNowIfRunning);
	}
}

