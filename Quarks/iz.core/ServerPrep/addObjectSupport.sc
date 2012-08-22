

+ Object {
	// ===== ServerPrep stuff =====

	addToServerTree { | function, server |
		ServerPrep(server).addToServerTree(this, function);
	}
	removeFromServerTree { | function, server |
		ServerPrep(server).removeFromServerTree(this);
	}
}