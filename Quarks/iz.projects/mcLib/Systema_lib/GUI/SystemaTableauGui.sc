SystemaTableauGui : ObjGui {
/*
SystemaTableauGui.new
b = SYSTab("Tropoi", [ 'hyperLydianTropos', 'hyperAeolianTropos', 'hyperPhrygianTropos', 'hyperIonianTropos', 'hyperDorianTropos', 'lydianTropos', 'aeolianTropos', 'phrygianTropos', 'ionianTropos', 'dorianTropos', 'hypoLydianTropos', 'hypoAeolianTropos', 'hypoPhrygianTropos', 'hypoIonianTropos', 'hypoDorianTropos' ]);
SystemaTableauGui(b)
*/	
	var sysSink;
		
	*observedClasses { ^[SYSTab] }
	*rowWidth { ^260 }
	*rowHeight { ^this.skin.buttonHeight}
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 50@700
		} { 
			defPos = skin.margin;
		};
		minSize = (this.class.rowWidth) @ (numItems * this.class.rowHeight);
		
		// this.logln("options[0]" + options[0]);
		
		allGuiDefs = (
			allGuiClass: 		SystemaTableauAllGui
			,objGuiClasses:	[this.class]
			,numItems:		4
			// optional ClassAllGui defaults:
			,initPos:			850@5
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\vertical
			,makeFilter:		true
			,name:			"Systema AllGUI"
		)
	}
	getName {	^try { object.name.asString + "|" + object.sysSymbols[0] } ? "_anon_" }
}

SystemaTableauAllGui : ClassAllGui {
	var winBut;
	addToHead { 
		winBut = Button(headZone, 30@skin.headHeight)
			.states_([["win", skin.fontColor, skin.offColor]])
			.action_({|but| SYSTabGui.new })
		;
	}
}