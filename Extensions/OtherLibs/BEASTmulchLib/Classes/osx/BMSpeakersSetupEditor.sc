BMSpeakerArrayGUI : BMAbstractGUI {

	var outputArray, okayFunc;
	var speakerListCompView, speakerList, instanceVarsBoxes, specText, subarraysWindow;
	var speakerButtonsView, deleteButton, upButton, downButton, importPopUpMenu, okButton;
	
	
	*new {| startArray, okayFunc, name, origin |
		  ^super.new.init(startArray.deepCopy ?? { BMInOutArray[]}, okayFunc, name ? "Define Speaker Array")
		  	.makeWindow(origin ? (40@200));
	}
	
	init {|startArray, argokayFunc, argname|
		outputArray = startArray;
		okayFunc = argokayFunc;
		name = argname;
	}
	
	makeWindow {| origin |
	
	   var x, y, numTypes, specsList, speakerVarsView;
	   
	   x = origin.x;
	   y = origin.y;
		
	   window		= SCWindow(name, Rect.new(x, y, 496, 554+31+10), false);
	   window.alwaysOnTop_(true);

	   window.view.decorator = FlowLayout(window.view.bounds);
	   specsList	= SCScrollView(window, Rect(0, 0, 160, 508))
				   .hasHorizontalScroller_(false)
				   .hasBorder_(true);
	   numTypes	= BMSpeakerSpec.specs.size;
	   specsList	= SCVLayoutView(specsList, Rect(4,4,150, numTypes * 24 + 4));
	   
	   BMSpeakerSpec.specs.keys.asArray.sort.do({|spName|
			SCDragSource(specsList, Rect(0, 0, 150, 20)).string_("   " ++ spName.asString)
				.background_(Color.grey.alpha_(0.2))
				.font_(Font("Helvetica-Bold", 12))
				.beginDragAction_({ BMSpeaker(spec: spName) })
		});
	   
	   speakerListCompView	= SCCompositeView(window, 160 @ 508)
						   .background_(Color.grey.alpha_(0.3));
	   speakerListCompView.decorator = FlowLayout(speakerListCompView.bounds);
	   speakerList	= SCListView(speakerListCompView, 152 @ (508-35))
	 					.items_(outputArray.keys.asArray)
	 					.action_({| view | 
	 						var speaker	= outputArray[view.item];
		 					if ((outputArray.size > 0))
				    	  	    	   { if (speaker.isBMSpeaker)
			 					   { instanceVarsBoxes
			 						 .keysValuesDo{| key | 
			 						 	var value;
			 						 	value = speaker.perform(key);
			 						 	if (key == \index) { value = value + 1  };
			 						 	instanceVarsBoxes[key].value = value };
			 						 specText.string_(speaker.spec.name.asString);
			 					   }
			 					   {  
			 					     instanceVarsBoxes
			 						 .keysValuesDo{| key | 
			 						 	if (key == \name) 
			 						 		{ instanceVarsBoxes[key].value = view.item }
			 						 		{ instanceVarsBoxes[key].value = "" }
			 						 };
			 						 specText.string_("");
			 					   }
			 				   }
			 					 						       				   { instanceVarsBoxes
			 						 .keysValuesDo{| key | 
			 						 	instanceVarsBoxes[key].value = "" 
			 						 };
			 				   }
	 					});
	   
		// redirect to button actions
	 	speakerList.keyDownAction = { arg view,char,modifiers,unicode,keycode;
	 		block { |break|
				if((modifiers == 11534600) && (unicode == 63233), {
					downButton.doAction;
					break.value;
				});
				if((modifiers == 11534600) && (unicode == 63232), {
					upButton.doAction;
					break.value;
				});
				if(unicode == 127, { deleteButton.doAction });
				speakerList.defaultKeyDownAction(char,modifiers,unicode);
			}
		};

		speakerList.canReceiveDragHandler = { SCView.currentDrag.isKindOf(BMSpeaker) };
		speakerList.receiveDragHandler = { var newSpeaker = SCView.currentDrag; 
			this.makeNewSpeakerWindow(newSpeaker)
		};



// List's Buttons ---------------------

		deleteButton = RoundButton(speakerListCompView, 20 @ 20).extrude_(false).canFocus_(false);		deleteButton.states		= [[ '-', Color.black,  Color.white.alpha_(0.8) ]];
		deleteButton.action		= { var viewIndex;
			if (speakerList.item.notNil)
				{ viewIndex = speakerList.value;
	    	  	    outputArray.removeAt(speakerList.item);
	    	  	    speakerList.items_(outputArray.keys.asArray);
	    	  	    if ((viewIndex == (outputArray.size)) and: { outputArray.size > 0 })
	    	  	    	   { speakerList.valueAction = viewIndex - 1 }
	    			{ speakerList.value_(viewIndex).doAction }
	    		}
		};
	
		speakerListCompView.decorator.shift(6, 0);
		upButton	= RoundButton(speakerListCompView, 20 @ 20).extrude_(false).canFocus_(false);		upButton.states = [[ \up, Color.black,  Color.white.alpha_(0.8) ]];
		upButton.action = { var index;
			index = speakerList.value;
			if (index.notNil && (index > 0), { 
				outputArray.swap(index - 1, index);
				speakerList.items_(outputArray.keys.asArray);
				speakerList.value = index - 1;
			});
	 	};

		speakerListCompView.decorator.shift(-3, 0);
		
		downButton = RoundButton(speakerListCompView, 20 @ 20).extrude_(false).canFocus_(false);		downButton.states = [[ \down, Color.black,  Color.white.alpha_(0.8) ]];
		downButton.action = { var index;
			index = speakerList.value;
			if (index.notNil && (index < (outputArray.size - 1)), { 
				outputArray.swap(index, index + 1);
				speakerList.items_(outputArray.keys.asArray);
				speakerList.value = index + 1;
			});		
		};


		// instance variables
		speakerVarsView = SCCompositeView(window, 160 @ 508).background_(Color.grey.alpha_(0.3));
		speakerVarsView.decorator = FlowLayout(speakerVarsView.bounds, Point(10, 10), Point(4, 10));
		
		instanceVarsBoxes	= [ \name -> "Name", \index -> "Output", \x -> "x", \y -> "y", 
			\z -> "z", \azi -> "azimuth", \ele -> "elevation", \rad -> "radius" ]
			.collectAs({| instVar |
				var speaker, index;
			  	SCStaticText(speakerVarsView, 54 @ 20)
			  		.string_(instVar.value ++ ":").font_(Font("Helvetica", 12));
			  	if (instVar.key == \name)
			  	   {	instVar.value = SCTextField(speakerVarsView, 82 @ 20);
			  	   	instVar.value.action_{| view |
		  	   			var name = view.value;
		  	   			if (outputArray.keys.any{| nameInList | nameInList == name.asSymbol }
		  	   			    and: {outputArray.keys.indexOfEqual(name.asSymbol) != speakerList.value })
   			        		   { BMAlert( "The name \"" ++ name ++ "\" is already taken. Please choose a different name.", 
   			        			 [[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]],
   			        			 background: Color.white,
   			        			 color: Color.red,
   			        			 border: false
   			        	 		);
   			        	 		//view.value = speaker.name;
   			        	 	    }
   			        	 	    { 
   			        	 	    		index = speakerList.value;
   			        	 	    		speaker = outputArray.removeAt(speakerList.item).value;
   			        	 	    		speaker.name = name;
   			        	 	    		outputArray.insert(index, speaker);
   			        	 	    		speakerList.items_(outputArray.keys.asArray);
								speakerList.value = index;
								speakerList.doAction; // show any changed params
   			        	 	    	}
	   			           }
			  	   }
			  	   { instVar.value = SCNumberBox(speakerVarsView, 82 @ 20);
			  	     instVar.value.action_({| view | 
			  				 		var speaker = outputArray[speakerList.item], value = view.value;
			  				 		if (instVar.key == \index) { value = value - 1 };
			  				 		speaker.perform(instVar.key.asSetter, value);
			  				 		speakerList.doAction; // show any changed params
			  					 })
			  	   };
			  	   instVar.value.background_(Color.white.alpha_(0.3)).font_(Font("Helvetica", 12));
			  	 	
			  	
			  	speakerVarsView.decorator.shift(0, 10);
			  	instVar
						   },
						   Event
						   );
		
		SCStaticText(speakerVarsView, 54 @ 20)
			.string_("Spec:")
			.font_(Font("Helvetica", 12));
		specText = SCStaticText(speakerVarsView, 82 @ 20)
			.font_(Font("Helvetica", 12));
			
		speakerList.doAction;
		speakerVarsView.decorator.shift(0, 62); 
		RoundButton(speakerVarsView, 140 @ 20)
			   .extrude_(false).canFocus_(false) 
			   .states_([[ "Subarrays", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({ 
			   	if (subarraysWindow.isNil) 
			   		{ subarraysWindow = BMSubarrayMenuGUI(window, outputArray);
			   		  subarraysWindow.onClose_({ subarraysWindow = nil })
			   		}
			   });
		speakerVarsView.decorator.shift(0, 5); 
		SCStaticText.new(speakerVarsView, 140 @ 20).string = "Import / Export:";
		
		importPopUpMenu = SCPopUpMenu(speakerVarsView, 140 @ 20)
					   .items_([ " ", "Import Speaker Array", "Export Speaker Array" ])
					   .background_(Color.white)
					   .action_({| view |
					   	switch(view.value,
					   		// import speaker array
					   		1, { CocoaDialog.getPaths({| path | 
								var recalledstartArray;
								
								recalledstartArray = Object.readTextArchive(path[0]);
								if (subarraysWindow.notNil) { subarraysWindow.window.close };
								outputArray = recalledstartArray;
								speakerList.items_(outputArray.keys.asArray);
								speakerList.value_(0).doAction;
								}, maxSize: 1);

							 },
					   		
					   		// export speaker array
					   		2, { CocoaDialog.savePanel({| path | 
							 	outputArray.writeTextArchive(path);
							    })
					  		 }
					      );
					       	
					  	 view.value = 0
					   		
					   	});
		
		window.view.decorator.nextLine;
		window.view.decorator.shift(0, 8);
		
		SCStaticText(window, 488 @ 16)
		 .string_("Drag speaker model from left to create a new speaker")
		 .font_(Font("Helvetica-Bold", 12));
		window.view.decorator.nextLine;
		window.view.decorator.shift(250, 10);
		RoundButton(window, 115 @ 20)
			   .extrude_(false).canFocus_(false) 
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	window.close });

		okButton = RoundButton(window, 115 @ 20)
				   .extrude_(false).canFocus_(false)
				   .states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
				   .action_({ 
				   			okayFunc.value(outputArray);
				   			window.close;
				   });
				   
		window.onClose = { 
			if (subarraysWindow.notNil) { subarraysWindow.window.close };
			//this.removeoutputArrayDependants;
			onClose.value(this)
		};
		window.front;
		}
			
	// popup window for a new speaker	 
	makeNewSpeakerWindow {| newSpeaker, origin |

		var newWindow, name, speakerNameField, okButton, speakerIndexField;
		 
		origin		= origin ?? { 490 @ 500 };
		newWindow 	= SCModalSheet(window, Rect(origin.x, origin.y, 260, 110 + 30), false);
		newWindow.view.decorator = FlowLayout(newWindow.view.bounds, Point(10, 10), Point(10, 10));
		
		SCStaticText(newWindow, 50 @ 20).string = "Name:";

		speakerNameField	= SCTextField(newWindow, 180 @ 20);
		
		SCStaticText(newWindow, 50 @ 20).string = "HW Out:";
		speakerIndexField	= SCTextField(newWindow, 180 @ 20);
					
		newWindow.view.decorator.shift(0, 30);
		
		RoundButton(newWindow, 115 @ 20)
			   .extrude_(false).canFocus_(false) 
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	newWindow.close });
			   
		okButton = RoundButton(newWindow, 115 @ 20)
				   .extrude_(false).canFocus_(false)
				   .states_([[ "Create", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
				   .action_({ var name;
				   			
				   			name = speakerNameField.string;
				   			if (name.size > 0) 
				   				{ name = name.asSymbol;
				   				  if (outputArray.keys.any{| nameInList | nameInList == name })
				   			        	{ BMAlert("The name \"" ++ name ++ "\" is already taken. Please choose a different name.", 
				   			        			 [[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]],
				   			        			 background: Color.white,
				   			        			 color: Color.red,
				   			        			 border: false
				   			        	 ) 
				   			          }
				   			          {newWindow.close;
					   				  newSpeaker.name = name;
					   				  newSpeaker.index = speakerIndexField.string.asInteger - 1;
									  outputArray.add(newSpeaker);
									  speakerList.items_(outputArray.keys.asArray);
									  speakerList.value_(outputArray.size - 1).doAction;
				   				 	 }
				   				 }
				   		   });
		speakerNameField.focus;
	}		 
}


BMSubarrayMenuGUI : BMAbstractGUI {

	var parent, outputArray;
	var assigns, assignSection, assignButton, assignView, newButton;
	var subarrays, subarraySection, subarrayView, speakerSection, speakerView;
	var newButton, deleteButton, addButton, upButton, downButton;
	var labelPlusButton, matrixButton, clearButton, buttonSection;
	
	*new {|parent, outputArray|
		^super.new.init(parent, outputArray).makeWindow;
	}
	
	init { |argparent, argoutputArray |
		parent = argparent;
		outputArray = argoutputArray;
		assigns = List.new;
	}
	
	makeWindow {
		
		window = SCModalSheet(parent, (800-20)@300, false);
		window.view.decorator = FlowLayout(window.view.bounds);
		subarraySection = SCCompositeView(window, 200 @ 281)
			.background_(Color.grey.alpha_(0.3));
		
		subarraySection.decorator = FlowLayout(subarraySection.bounds);
		SCStaticText.new(subarraySection, Rect(0,0,180,20)).font_(Font("Helvetica-Bold", 14))
			.string = "Subarrays";
		subarrayView = SCListView(subarraySection, (200-8) @ (250 - 26))
	 		.items_(outputArray.subArrays.asArray)
	 		.action_({ speakerView.value_(0); assignView.value_(0); this.updateLists });

		subarraySection.decorator.nextLine;
	
		newButton	 = RoundButton(subarraySection, 20 @ 20).extrude_(false).canFocus_(false)
			.font_(Font("Arial", 11)).states_([['+', Color.black,  Color.white.alpha_(0.8) ]])
			.action_({ this.makeNewSubarrayWindow(490 @ 500) });
		deleteButton = RoundButton(subarraySection, 20 @ 20).extrude_(false).canFocus_(false);		deleteButton.states = [[ '-', Color.black,  Color.white.alpha_(0.8) ]];
		deleteButton.action = { var viewIndex, name;
			if (subarrayView.item.notNil)
		  	  { viewIndex = subarrayView.value;
		  	    name = subarrayView.item;
		  	    if ((viewIndex == (outputArray.subArrays.lastIndex)) and: { outputArray.subArrays.size > 1 })
		  	    	   { subarrayView.value_(viewIndex - 1) }
		  	    	   { subarrayView.value_(viewIndex) };
		  	    outputArray.removeSubArray(name.asSymbol);
		  	    this.updateLists;
		  	 }
		   };

		
		assignSection = SCCompositeView(window, Rect(0, 0, 200, 281))
			.background_(Color.grey.alpha_(0.3));
		assignSection.decorator = FlowLayout(assignSection.bounds);
		SCStaticText.new(assignSection, Rect(0,0,180,20)).font_(Font("Helvetica-Bold", 14))
			.string = "Assignments";
		assignView = SCListView(assignSection, Rect(0, 0, 200-8, 250-1))
			.canReceiveDragHandler = true;
		assignView.receiveDragHandler = { 
			outputArray.addToSubArray(subarrayView.item.asSymbol, SCView.currentDrag.asSymbol);
			this.updateLists;
		};
		assignView.keyDownAction = { arg view,char,modifiers,unicode,keycode;
			var viewIndex, name, assigns;
			if(unicode == 127 and: { subarrayView.item.notNil }) 
			   { viewIndex = assignView.value;
			     name = assignView.item;
			     assigns = outputArray.getSubArrayKeys(subarrayView.item.asSymbol);
			     if ((viewIndex == assigns.lastIndex) and: { assigns.size > 1 })
							  	    	   { assignView.value_(viewIndex - 1) }
							  	    	   { assignView.value_(viewIndex) };
			   	outputArray.removeFromSubArray(subarrayView.item.asSymbol, name.asSymbol) };
			this.updateLists;
			
		};
	
			
		speakerSection = SCCompositeView(window, Rect(0, 0, 200, 281))
			.background_(Color.grey.alpha_(0.3));
		speakerSection.decorator = FlowLayout(speakerSection.bounds);
		SCStaticText.new(speakerSection, Rect(0,0,75,20)).font_(Font("Helvetica-Bold", 14))
			.string = "Speakers";
		assignButton = RoundButton(speakerSection, Rect(0,0,110,20))
			.extrude_(false)
			.canFocus_(false)
			.canReceiveDragHandler = false;		
		assignButton.states = [["<", Color.black, Color.white.alpha_(0.8)]];
		assignButton.action = { 
			if (subarrayView.item.notNil and: { speakerView.item.notNil },{
				outputArray.addToSubArray(subarrayView.item.asSymbol, 
					speakerView.item.asSymbol);
				this.updateLists;
			})
		};	
		speakerView = SCListView(speakerSection, Rect(0, 0, 200-8, 250-1))
			.canReceiveDragHandler = false;
		this.updateLists;
		speakerView.beginDragAction = {|view| view.item };
		buttonSection = SCCompositeView(window, Rect(0, 0, 155, 281));
		clearButton = RoundButton(buttonSection, Rect(0,30,155,20))
			.extrude_(false)
			.canFocus_(false)
			.canReceiveDragHandler = false;		
		clearButton.states = [["Clear Assignments", Color.black, Color.white.alpha_(0.8)]];
		clearButton.action = { 
			if (subarrayView.item.notNil) { 
				outputArray.defineSubArray(subarrayView.item.asSymbol, []);
				this.updateLists;
			} 
		};
		
		SCStaticText.new(buttonSection, Rect(0,30,155,115))
		 .string_("Assign speakers to selected subarray. Cmd-drag or use button to add, select and press delete to remove.");
		this.updateLists;
		
		RoundButton(buttonSection, Rect(40, 260, 115, 20))
			.extrude_(false).canFocus_(false)
			.states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
			.action_({ window.close; });
		
		window.onClose = { 
			onClose.value(this)
		};
	}
	
	updateLists {
		subarrayView.items = outputArray.subArrays;
		if (outputArray.subArrays.size > 0) 
			{ assignView.items  = outputArray.getSubArrayKeys(subarrayView.item.asSymbol);
			  speakerView.items = outputArray.keys
			  			   		.difference(outputArray.getSubArrayKeys(subarrayView.item.asSymbol));
		     }
			{ assignView.items = [];
			  speakerView.items = outputArray.keys
			}
	}
	
	makeNewSubarrayWindow {| origin |

		var newSAWindow, subarrayNameField, okButton;
		 
		origin		= origin ?? { 490 @ 500 };
		newSAWindow 		= SCModalSheet(window, 260@110, false);
		newSAWindow.view.decorator = FlowLayout(newSAWindow.view.bounds, Point(10, 10), Point(10, 10));
		
		SCStaticText(newSAWindow, 50 @ 20).string = "Name:";

		subarrayNameField	= SCTextField(newSAWindow, 180 @ 20);
					
		newSAWindow.view.decorator.shift(0, 30);
		
		RoundButton(newSAWindow, 115 @ 20)
			   .extrude_(false).canFocus_(false) 
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	newSAWindow.close });
			   
		okButton = RoundButton(newSAWindow, 115 @ 20)
			.extrude_(false).canFocus_(false)
			.states_([[ "Create", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
			.action_({ var name;
		   			
	   			name = subarrayNameField.string;
	   			if (name.size > 0) 
	   				{ name = name.asSymbol;
	   				  if (outputArray.subArrays.any{| nameInList | nameInList == name })
	   			        	{ BMAlert("The name \"" ++ name ++ "\" is already taken. Please choose a different name.", 
	   			        			 [[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]],
	   			        			 background: Color.white,
	   			        			 color: Color.red,
	   			        			 border: false
	   			        	 ) 
	   			          }
	   			          { newSAWindow.close;
	   			            outputArray.defineSubArray(name, []);
	   			            subarrayView.items_(outputArray.subArrays.asArray);
	   			            subarrayView.value_(outputArray.subArrays.lastIndex)
	   			            	.doAction;
	   				 	}
	   				 }
			});
		subarrayNameField.focus;
	}
}
