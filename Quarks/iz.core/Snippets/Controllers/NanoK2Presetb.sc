/* IZ Mon 13 August 2012  1:31 AM EEST

Store and restore the states of a NanoKontrol2b mini-mixer GUI. 
A saved state that can be restored is called here a snapshot. 

Each NanoK2Presetb stores multiple snapshots as follows: 

- When the user types return on the button of the current preset instance,
  the preset stores a snapshot in snapshotHistory. 

- When the user types space on the button of the current preset, the preset
  toggles between the most recently selected item in snapshotHistory and autoSnapshot.
  If the switch is to the most recently selected item in history, the current state of the GUI
  will be saved in autoSnapshot before switching. 
  
- When the user selects a different preset button, the autoSnapshot of that button is restored.
  A snapshot of the current state of the GUI is saved in autoSnapshot of this preset 
  before restoring the newly selected preset. 
  
- When the user types '<' the previous snapshotHistory item is restored

- When the user types '>' the next snapshotHistory item is restored

- When the user types 'a' the GUI starts using single-node-per-strip mode ("auto-proxy-mode").
  In this mode, each new node is automatically allocated to the next free strip

================ Mouse and keyboard actions: =================

=== Mouse click: ===
Select this preset group, and restore its more recent state to the GUI
The previously selected preset stores its last state before leaving. 

=== Keyboard commands ===

- Return or s(ave): Save the current state the history of snapshots of this preset group.
- Space or t(oggle): Toggle between the most recent snapshotHistory item and autoSnapshot.
- < : restore the previous snapshot from snapshotHistory to the GUI
- > : restore the next snapshot from snapshotHistory to the GUI
- , : restore first item in history
- . : restore last item in history
- a(utoproxy) : Switch to automatic one-proxy-per-strip configuration ("auto-proxy-mode").
  In this mode, each new proxy is added to a subsequent strip in the GUI.
  If more than 8 proxies are created, then they are saved in subsequent presets. 

=============== Colors ============

=== Background color: ===
- Grey: Snapshot group inactive
- Blue: Snapshot group just stored an item in history
- Yellow: Snapshot group just recalled an autoSnapshot
- Green: Snapshot group just recalled a snapshot from history
- White: Snapshot group is in one-proxy-per-strip settings configuration
=== Text color: ===
- Black: no snapshots stored
- Red: autoSnapshot stored only - history is empty
- Blue: Snapshots stored in history exist

*/

NanoK2Presetb {
	classvar <empty;	// stores empty preset data, used when switching to yet unsaved "page"
	var <index = 0, <nanoK2;
	var <autoProxyMode = false;
	var <autoSnapshot;      // snapshot of most recent free-mode config 
	var <autoProxySnapshot; // snapshot of most recent auto-proxy-mode config 
	var <mostRecentlyRestored, <history, <historyPos = 0;

	var <button;	// store conveniently for changing color according to history / autoSnapshot
	var <textColor;	// Black: nothing stored. Red: snapshot stored. Blue: history stored

	*new { | index = 0, nanoK2 | ^this.newCopyArgs(index, nanoK2).init; }
	init { textColor = Color.black; }
	empty { ^empty }
	// called by nanoK2 when it inits: 
	initEmpty { empty = nanoK2.takeSnapshot; }
	
	gui {
		^button = Button().font_(nanoK2.font)
			.states_([[index.asString]])
			.action_({ this.switchToMyself })
			.keyDownAction_({ | b, char, key, ascii |
				switch ( ascii, 
					13, { this.saveToHistory }, 	   // return
					115, { this.saveToHistory }, 	   // s
					32, { this.toggle }, 			   // space
					116, { this.toggle }, 			   // t
					60, { this.previousInHistory },     // <
					62, { this.nextInHistory },         // >
					44, { this.restoreFromHistory(history.size - 1) },  // ,
					46, { this.restoreFromHistory(0) },   // .
					97, { this.switchToAutoProxies }     // a
//					{ ascii.postln; }
				)
			})
	}

	switchToMyself {
		if (nanoK2.currentPreset === this) {
			// do not do anything if this is already the loaded preset
		}{
			nanoK2.currentPreset.saveToAutoSnapshotAndLeave;
			this.restoreFromAutoSnapshot;
		}
	}

	toggle {
		if (history.size == 0) { 
			^"There are no currently stored items in history. Type s to store one".postcln;
		};
		if (mostRecentlyRestored === autoSnapshot) {
			// leaving autoSnapshot, therefore renew autoSnapshot: 
			this.saveToAutoSnapshot;
			this.restoreFromHistory(historyPos);	
		}{
			this.restoreFromAutoSnapshot;
		}
	}

	saveToAutoSnapshot {
		if (this.isReallyAutoProxyMode) {
			autoProxySnapshot = this.takeSnapshot;
		}{
			autoSnapshot = this.takeSnapshot;
		};
		if (history.size == 0) { textColor = Color.red };
	}

	saveToAutoSnapshotAndLeave {
		this.saveToAutoSnapshot;
		button.states = [[index.asString, textColor]];
	}

	restoreFromHistory { | pos = 0 |
		if (history.size == 0) { 
			^"There are no currently stored items in history. Type s to store one".postcln;
		};
		if (pos < 0) { 
			^"You cannot go before the first item in history".postcln;
		};
		if (pos >= history.size) { 
			^"You cannot go beyond the last item in history".postcln;
		};
		historyPos = pos;
		mostRecentlyRestored = history[pos];
		this.restoreSnapshot(mostRecentlyRestored);	
	}
	
	restoreFromAutoSnapshot {
		if (this.isReallyAutoProxyMode) {
			this.restoreSnapshot(autoProxySnapshot);
		}{			
			mostRecentlyRestored = autoSnapshot;
			this.restoreSnapshot(autoSnapshot);
		}
	}

	isReallyAutoProxyMode {
		^autoProxyMode and: { nanoK2.proxyPresetCache.size / 8 < (index + 1); }
	}

	previousInHistory { this.restoreFromHistory(historyPos - 1); }
	nextInHistory { this.restoreFromHistory(historyPos + 1); }
	saveToHistory {
		textColor = Color.blue;
		history = history add: this.takeSnapshot;
		button.states = [[index.asString, textColor, Color(0.5, 0.7, 1.0)]];
	}

	takeSnapshot { ^nanoK2.takeSnapshot }

	restoreSnapshot { | argSnapshot |
		var bcolor;
		if (this.isReallyAutoProxyMode) {
			bcolor = Color.white
		}{
			if (argSnapshot === autoSnapshot) { bcolor = Color.yellow; } { bcolor = Color.green }
		};
		button.states = [[index.asString, textColor, bcolor]];
		nanoK2.restoreSnapshot(argSnapshot, this, this.isReallyAutoProxyMode);
	}
	
	switchToAutoProxies {
		autoProxyMode = true;
		this.restoreFromAutoSnapshot;	
	}
}
