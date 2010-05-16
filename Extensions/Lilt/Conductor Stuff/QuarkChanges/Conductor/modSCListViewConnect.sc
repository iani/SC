/* IZ Monday, May 12, 2008 
Adding keyboard commands to SCListView in connect.
These work via update messages to the SV. 

*/

+ SCListView {
	connect { arg ctl; 
		var link;
		this.value_(ctl.value);
		this.action_({ctl.value_(this.value); });
		link = SimpleController(ctl)
			.put(\synch, 
			 { arg changer, what;
			 	defer { this.value = ctl.value };
			})
			.put(\items,
			 { arg changer, what;
			 	defer { this.items = ctl.items };
			}
		);
		this.onClose = { link.remove };
		this.keyDownAction = { | view, char, mod, unicode, key |
			switch (unicode,
			127, {    // backspace: "delete" current selection
				ctl.changed(\deleteKey)
			},
			13, {	// return key: "send" or "enter" current seletion
				ctl.changed(\returnKey)
			},
			{ view.defaultKeyDownAction(char, mod, unicode, key) }
			);
		}
	}
}