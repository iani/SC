SYSTabGuiSupport {
	
	classvar <popItemsInt, <popItemsStep;
	
	*initClass {
		popItemsInt = [" ", "ratio", "cent", "index"];
		popItemsStep = [" ", "ratio", "cent", "index"];
		CodePage.asClass !? {
			NotificationCenter.registerOneShot(CodePage, \ready, this, {
				popItemsStep = popItemsStep.addAll(["-"].addAll(CodePage.asClass.groups.default))
			})
		}
	}
}