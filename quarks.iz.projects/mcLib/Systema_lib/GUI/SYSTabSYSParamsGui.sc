SYSTabSYSTabParamSyssGui : ObjGui {
/*
SYSTabSYSTabParamSyssGui.new
*/	
	var tabText, offsetRatioText, centOffsetNum;
	var lStepAnnoPop, lIntAnnoPop, rStepAnnoPop, rIntAnnoPop;
		
	*observedClasses { ^[SYSTabSYSTabParamSyss] }
	*rowWidth { ^100 + (4 * (10 + 45)) + 80 + 30 }
	*rowHeight { ^this.skin.buttonHeight}
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 50@700
		} { 
			defPos = skin.margin;
		};
		minSize = (this.class.rowWidth) @ (numItems * this.class.rowHeight);
				
		allGuiDefs = (
			allGuiClass: 		SYSTabSYSTabParamSyssAllGui
			,objGuiClasses:	[this.class]
			,numItems:		20
			// optional ClassAllGui defaults:
			,initPos:			900@5
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\vertical
			,makeFilter:		false
			,name:			"Systema AllGUI"
		)
	}
	makeViews {|obj|
		var lineheight = zone.bounds.height - (skin.margin.y * 2);
		 
		tabText = TextField(zone, 100@lineheight).font_(font).align_(\center)
			.string_(obj.label)
			.action_({|txt| obj.label = txt.value })
		;
		StaticText(zone, 5@lineheight).font_(font).align_(\center).string_("")
		;
		lStepAnnoPop = PopUpMenu(zone, 45@lineheight).font_(font)
			.items_(SYSTabGuiSupport.popItemsStep)
		;
		StaticText(zone, 10@lineheight).font_(font).align_(\center).string_(">")
		;
		lIntAnnoPop = PopUpMenu(zone, 45@lineheight).font_(font)
			.items_(SYSTabGuiSupport.popItemsInt)
		;
		StaticText(zone, 10@lineheight).font_(font).align_(\center).string_("i")
		;
		rIntAnnoPop = PopUpMenu(zone, 45@lineheight).font_(font)
			.items_(SYSTabGuiSupport.popItemsInt)
		;
		StaticText(zone, 10@lineheight).font_(font).align_(\center).string_("<")
		;
		rStepAnnoPop = PopUpMenu(zone, 45@lineheight).font_(font)
			.items_(SYSTabGuiSupport.popItemsStep)
		;
		StaticText(zone, 5@lineheight).font_(font).align_(\center).string_("")
		;
		offsetRatioText = StaticText(zone, 80@lineheight).font_(font)
			.value_( obj.centOffset.round(0.1) )
			.action_({|num| obj.centOffset = num.value })
//			.receiveDragHandler_({|num| 
//				var offset = View.currentDrag.value - Systema.at(obj.sysSymbol).midiRoot.midicps;
//				obj.centOffset = offset;
//				num.value = obj.midiOffset.round(0.1) })
		;
		centOffsetNum = NumberBox(zone, 30@lineheight).font_(font)
			.value_( obj.centOffset.round(0.1) )
			.action_({|num| obj.centOffset = num.value })
			.receiveDragHandler_({|num| 
				var offset = View.currentDrag.value - Systema.at(obj.sysSymbol).midiRoot.midicps;
				obj.centOffset = offset;
				num.value = obj.midiOffset.round(0.1) })
		;
	}
	updateFast {
		var newState = this.getState;
		if (newState == prevState) { ^this };
		
		if (newState[\object] != prevState[\object]) { 
			this.name_(this.getName);
			prevState = newState //mc
		}		
	}
}

SYSTabSYSTabParamSyssAllGui : ClassAllGui {
	addToHead {
		StaticText(headZone, 100@skin.headHeight).font_(font).align_(\center).string_("Label")
		;
		StaticText(headZone, (4*55)@skin.headHeight).font_(font).align_(\center).string_("Annotation")
		;
		StaticText(headZone, 40@skin.headHeight).font_(font).align_(\center).string_("freqOff")
		;
	}
}
