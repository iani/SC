/* iz Thu 25 October 2012 11:53 AM EEST

Default action strip for Lilt2 = mini menu + button strip substitute for menu, for 3.6ide.


*/

Lilt2DefaultMenu {
	classvar <menu;
	*initClass { StartUp add: { this.makeMenu } }

	*makeMenu {
		if (menu.isNil) {
			menu = ActionStrip().addItems(
				"Scripts", { ScriptLib.open },
				"Mixer", { ScriptMixer() },
				"Scripts", { ScriptLib.open },
				"Quarks", { "not yet implemented".postln }
			);
			menu.window.onClose = { menu = nil };
		}{
			menu.window.front;
		}
	}
}
