
BMMasterFaderGUI : BMAbstractGUI {
	var masterFader, spec;
	var slider, numberBox;
	
	*new {| masterFader, name |
		^super.new.init(masterFader, name ? masterFader.name)
			 .makeWindow;
	}
	
	init {| argMasterFader, argName |
		 masterFader 	= argMasterFader;
		 name 		= argName;
		 masterFader.addDependant(this);
		 spec = \db.asSpec;
	}
	
	makeWindow {
		

		window 	= SCWindow(name, 
						  Rect.new( 
						  	SCWindow.screenBounds.width - 120, 0, 120, 
						  	SCWindow.screenBounds.height - 150
						  ), 
						  false
				   ).front;
		
		slider 	= SmoothSlider(window, window.view.bounds.resizeBy(-30, -100).moveBy(15, 15))
					.mode_(\move).canFocus_(false)
					.value_(spec.unmap(masterFader.level))					.action_({| view | 
							 masterFader.level	= spec.map(view.value);
							 numberBox.value 	= masterFader.level.round(0.1)
				     });
				   
		numberBox	= NumberBox(window, window.view.bounds
					.resizeTo(90, 60)
					.moveBy(15, window.view.bounds.height - 80))
					.value_(masterFader.level.round(0.1))
					.font_(Font( "Monaco", 22 ))
					.align_(\center)
					.action_({| view | 
							 masterFader.level = view.value;
							 view.value = masterFader.level.round(0.1);
							 slider.value = spec.unmap(masterFader.level);
					});
		
		window.onClose = { masterFader.removeDependant(this);
						onClose.value(this) 
					    }
		
	}
	
	update { |changed, what, args|
		if(what == \level, {
			slider.value = spec.unmap(masterFader.level);
			numberBox.value 	= masterFader.level.round(0.1);
		});
	
	}
}
