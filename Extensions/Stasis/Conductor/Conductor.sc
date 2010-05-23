/* 
Broadcasting the whole fibonacci structure to everyone over the network. 
*/



Stasis {
	classvar <>receivers;
	classvar <sender;
	classvar <conductStream;	// the stream that plays the process that 
	classvar <tempo = 1.3308;
	classvar <ascendingFib;
	classvar <descendingFib;

	*addReceivers { | ... argReceivers |
		if (receivers.isNil) { receivers = [NetAddr.localAddr]; };
		receivers = receivers ++ argReceivers;
		if (sender.notNil) { 
			sender.clients = receivers;
		}
	}

	*start { | phrase |	// phrase to start from
		if (sender.notNil) { sender.stop };
		sender = SyncSender(clients: receivers);
		this.makeFibs;
		sender.pattern = Ppar([ascendingFib, descendingFib]);
	}

	*stop {
		sender.stop;
	}
	
	*tempo_ { | tempo |
		sender.clock.tempo = tempo;
	}

	*makeFibs {
		ascendingFib = Fib.ascending(15);
		descendingFib = Fib.descending(15);
	}

}