ObjTabGui : ObjGui {

	*observedClasses { ^[ObjTabGuiExampleObj] }
	*rowWidth { ^400 }
	*rowHeight { ^700 }
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 10@260
		} { 
			defPos = skin.margin;
		};
		minSize = if (bounds.notNil) { minSize = bounds.extent
			}{ (this.class.rowWidth) @ (numItems * this.class.rowHeight) };
		
		allGuiDefs = (
			allGuiClass: 		ClassTabAllGui	
			,objGuiClasses:	[this.class]
			,numItems:		3
			// optional ClassAllGui defaults:
			,initPos:			675@130
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\vertical
			,makeFilter:		true
			,name:			"Sample TabAllGUI"
			,paths:			[[]]
		)
	}	
	
}