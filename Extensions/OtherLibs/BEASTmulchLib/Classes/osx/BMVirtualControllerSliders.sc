
// simple onscreen slider GUI for a BMVirtualController
BMVirtualControllerSliders : BMAbstractGUI {
	var virtualCont, sliders;
	var needsRefresh = false;
	var <>refreshInterval = 0.05;
	var refreshLoopOn = false;
	var specs;
	
	*new {|virtualCont, name, origin|
		^super.new.init(virtualCont, name ? virtualCont.name)
			.makeWindow(origin ? (40@200));
	}
	
	init {|argvirtualCont, argname|
		virtualCont = argvirtualCont;
		virtualCont.addDependant(this);
		name = argname;
	}
	
	makeWindow {|origin|
		var numSliders, font, presetMenu, labelWidth;
		font = Font("Helvetica-Bold", 10);
		numSliders = virtualCont.numControls;
		window = SCWindow.new(name, 
			Rect(300, 300, 652, (numSliders + 1) * 24), false); // 508
		window.view.decorator = FlowLayout(window.view.bounds);
		window.view.background = Color.rand.alpha_(0.3);
		sliders = Array.newClear(numSliders);
		specs = Array.newClear(numSliders);
		labelWidth = virtualCont.controlNames.collect({|name| 
			name.asString.bounds(font).width
		}).maxItem;
		virtualCont.controlNames.do({|controlName, i|
			var initVal, control, label, displaySpec;
			label = virtualCont.getLabel(i + 1);
			if(label.size == 0, {label =  controlName.asString }); 
			control = BMAbstractController.allControls[controlName.asSymbol];
			displaySpec = control.displaySpec;
			initVal = displaySpec.map(virtualCont.getVal(i + 1));
			sliders[i] = EZSlider.new(window, 
				640@20, 
				label, 
				displaySpec,
				{|ez| var setVal;
					setVal = displaySpec.unmap(ez.value);
					virtualCont.setVal(i + 1, setVal);
				}, initVal, labelWidth: labelWidth
			);
			sliders[i].numberView.background = Color.white.alpha_(0.4);
			sliders[i].font = font;
			specs[i] = displaySpec;
		
		});
		window.onClose = { virtualCont.removeDependant(this); onClose.value };
		window.front;
	}
	
	// could be some jitter, but safer
	startRefreshLoop {
		refreshLoopOn.not.if({
			refreshLoopOn = true;
			AppClock.sched(refreshInterval, {
				var resched;
				needsRefresh.if({resched = refreshInterval}, {refreshLoopOn = false});
				virtualCont.getAllValues.do({|val, i| 
					sliders[i].value_(specs[i].map(val));
				});
				needsRefresh = false;
				resched;
			});
		});
	}
	
	update {|changed, what, index, val|
		switch(what,
			\faderVal, {
				needsRefresh = true;
				this.startRefreshLoop;
			},
			\label, {sliders[index].labelView.string_(val.asString)}
		)
	}
	

}