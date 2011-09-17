GuiSkin {
	classvar <guiSkins;
	/*
	GUI.skins[\fsSkinBig]
	*/	
	*initClass {
		this.buildSkin;
		Class.initClassTree(GUI);
		GUI.skins.putAll(guiSkins);
	}
	
	*buildSkin {
		guiSkins = (
			big:	(
						fontSpecs: 		["Helvetica", 18],
						fontColor: 		Color.black,
						background: 		Color(0.8, 0.85, 0.7, 0.5),
						foreground:		Color.grey(0.95),
						onColor:			Color(0.5, 1, 0.5),
						offColor:			Color.clear,
						gap:				0 @ 0,
						margin: 			2@2,
						buttonHeight:		22,
												
						focusColor:		Color(0.5, 1.0, 0.5).darken(0.8),
						warnColor:		Color.new255(255, 62, 150).darken(0.7),
						warnFontColor: 	Color.new255(255, 62, 150),
						alterFontColor: 	Color.blue
			),
			medium:	(
						fontSpecs: 		["Helvetica", 12],
						fontColor: 		Color.black,
						background: 		Color(0.8, 0.85, 0.7, 0.5),
						foreground:		Color.grey(0.95),
						onColor:			Color(0.5, 1, 0.5),
						offColor:			Color.clear,
						gap:				0 @ 0,
						margin: 			2@2,
						buttonHeight:		22,
												
						focusColor:		Color(0.5, 1.0, 0.5).darken(0.8),
						warnColor:		Color.new255(255, 62, 150).darken(0.7),
						warnFontColor: 	Color.new255(255, 62, 150),
						alterFontColor: 	Color.blue
			),
			small:	(
						fontSpecs: 		["Helvetica", 10],
						fontColor: 		Color.black,
						background: 		Color(0.8, 0.85, 0.7, 0.5),
						foreground:		Color.grey(0.95),
						onColor:			Color(0.5, 1, 0.5),
						offColor:			Color.clear,
						gap:				0 @ 0,
						margin: 			0@0,
						buttonHeight:		18,
												
						focusColor:		Color(0.5, 1.0, 0.5).darken(0.8),
						warnColor:		Color.new255(255, 62, 150).darken(0.7),
						warnFontColor: 	Color.new255(255, 62, 150),
						alterFontColor: 	Color.blue,
						flashColor:		Color.new255(255, 140, 0)
			),
			
			AllGuiSkin: (
						fontSpecs: 		["Helvetica", 12],
						fontColor: 		Color.black,
						background: 		Color(0.8, 0.85, 0.7, 0.5),
						foreground:		Color.grey(0.95),
						onColor:			Color(0.5, 1, 0.5),
						offColor:			Color.clear,
						gap:				0 @ 0,
						margin: 			0@0,
						buttonHeight:		16,
						
						headHeight:		18,
						headFontSpecs: 	["Helvetica", 12],
						filtTextWidth:	100,
						
						focusColor:		Color(0.5, 1.0, 0.5).darken(0.8),
						warnColor:		Color.new255(255, 62, 150).darken(0.7),
						warnFontColor: 	Color.new255(255, 62, 150),
						alterFontColor: 	Color.blue
			)
		);
	}
}
