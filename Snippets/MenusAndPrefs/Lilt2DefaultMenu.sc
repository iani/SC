/* iz Thu 25 October 2012 11:53 AM EEST
Default action strip for Lilt2 = mini menu + button strip substitute for menu, for 3.6ide.
*/

Lilt2DefaultMenu {
	classvar <menu;
	*initClass { StartUp add: {
			{	// compatibility with 3.5
				GUI.qt;
				QtGUI.style = \CDE;
				this.makeMenu;
			}.defer(0.5);

	} }

	*makeMenu {
		if (menu.isNil) {
			menu = ActionStrip().addItems(
				"Open default lib", { ScriptLib.openDefault },
				"Open lib ...", { ScriptLib.open },
				"Mixer", { ScriptMixer() },
				"Sound Files", { SoundFileGui().makeWindow },
				"Quarks", { Quarks.gui },
				[["Post OSC"], ["Stop posting OSC"]], { | view | OSCFunc.trace([false, true][view.value]) },
				"Quit Server", { Server.default.quit },
				"Reboot Server", { Server.default.reboot },
				"Stop sounds+routines", { thisProcess.stop },
				"Scope", {
					Server.default.scope.window.bounds_(Rect(0, 400, 258, 250));
//					FreqScope();
				},
			);
			menu.window.onClose = { menu = nil };
		}{
			menu.window.front;
		}
	}
}
