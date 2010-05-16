GUIEvent : Environment {
	classvar <>osx;
	
	*initClass {
		osx = 
		(
			h:		20,				// default height
			w: 		800,				// default width
			labelW:	80,				// width of a label and numberbox

			vh:		200,				// height of a vertical slider
			vw: 		20, 				// width of a vertical slider
			
			// functions that specify default dimensions of various views
			
			numericalRect: 		{ Rect(0,0, ~labelW, ~h) },
			smallNumericalRect: 	{ Rect(0,0, ~labelW/2, ~h) },
			sliderRect:			{ Rect(0,0, ~w, ~h) },
			vsliderRect:			{ Rect(0,0, ~vw, ~vh) },
			vnsliderRect:			{ Rect(0,0, ~labelW/2, ~vh) },
			msliderRect:			{ Rect(0,0, ~w, ~vh) },
			tdsliderRect:			{ Rect(0,0, ~vh, ~vh) },
			movieRect:			{ Rect(0,0, 240, 320) },
			labelRect:			{ Rect(0,0, ~labelW, ~h) },
			popupRect:			{ Rect(0,0, ~labelW, ~h) },
			listRect:				{ Rect(0,0, ~labelW, ~h) },
			radioButtonsRect: 		{ Rect(0,0,10,~h) },
			simpleButtonRect:		{ Rect(0,0,~h,~vw) },

// GRAPHIC PRIMITIVES
			window: { |  name, rect |
				var win;
				win = SCWindow(name, rect);
				win.view.decorator_(FlowLayout(win.view.bounds));
				win;
			},
			
			nextLine: { | win |
				win.view.decorator.nextLine
			},
			
			resizeWindowToContents: { | win |
				var bounds;
				win.view.decorator.nextLine;
				bounds = win.bounds;
				bounds.height = win.view.decorator.top + 16;
				win.bounds_(bounds);
				win.front;
			}, 
			
			label: { |win, name, cv, rect |
				rect = rect ?? ~labelRect; 			
				SCStaticText(win, rect).align_(\right).string_(name)
			},
					
			numberBox: { |win, name, cv, rect |
				rect = rect ?? ~numericalRect; 
				cv.asArray.do( SCNumberBox(win,rect).connect(_) );
			},

			smallNumberBox: { |win, name, cv, rect|
				rect = rect ?? ~smallNumericalRect;
				cv.asArray.do( SCNumberBox(win,rect).connect(_) );
			},

			slider: { |win, name, cv, rect|
				rect = rect ?? ~sliderRect;
				cv.asArray.do( SCSlider(win,rect).connect(_) );
			},

			rangeSlider: { |win, name, cv, rect|
				rect = rect ?? ~sliderRect;
				cv.asArray.do(SCRangeSlider(win,rect).connect(_) );
			},

			twodSlider: { |win, name, cv, rect|
				rect = rect ?? ~tdsliderRect;
				cv.asArray.do(SC2DSlider(win,rect).connect(_) );
			},

			popup: { |win, name, cv, rect |
				rect = rect ?? ~popupRect; 
				cv.asArray.do( SCPopUpMenu(win, rect).items_(cv.items.collect(_.asSymbol)).connect(_) );
			},
			
			list: { |win, name,  cv, rect |
				rect = rect ?? ~listRect; 
				cv.asArray.do( SCListView(win, rect).items_(cv.items.collect(_.asSymbol)).connect(_) );
			},
			
			text: { |win, name, cv, rect |
				rect = rect ?? ~sliderRect; 
				SCTextView(win, rect).string_(name.asString)
			},
		
			movie: { |win, name, cv, rect |
				SCMovieView(win, rect ?? ~movieRect)
			},

			simpleButton: { |win, rect|	
				SCButton(win, rect  ?? ~simpleButtonRect)
			},
			
			scMultislider: {  |win, name, cv, rect |
				rect = rect ?? ~msliderRect;
				cv.asArray.do ( SCMultiSliderView(win, rect).connect(_) );
			},	

			knob: { |win, name, cv, rect| var v, origin;
				cv.asArray.do { |cv|
					v = SCCompositeView(win, Rect(0,0,50,80));
					origin = v.bounds.origin;
						Knob(v, Rect(5,0,40,40).moveTo(origin.x, origin.y)).connect(cv);
						SCNumberBox(v,Rect(0,0,50,20).moveTo(origin.x, origin.y + 40))
							.connect(cv);
						SCStaticText(v,Rect(0,0,50,20).moveTo(origin.x, origin.y + 60))
							.string_(name).align_(\center).font_(Font("Futura", 10));
				};		
			},

			simpknob: { |win, name, cv, rect| var v, origin;
				~label.value(win, name);
				cv.asArray.do { |cv|
					v = SCCompositeView(win, Rect(0,0,50,60));
					origin = v.bounds.origin;
						Knob(v, Rect(5,0,40,40).moveTo(origin.x, origin.y)).connect(cv);
						SCNumberBox(v,Rect(0,0,50,20).moveTo(origin.x, origin.y + 40))
							.connect(cv);
				};		
			},
			
			
			vnumerical: { |win, name, cv, rect| var v, origin;
				cv.asArray.do { |cv|
					v = SCCompositeView(win, Rect(0,0,40,40));
					origin = v.bounds.origin;
						SCNumberBox(v,Rect(0,0,40,20).moveTo(origin.x, origin.y )).connect(cv);
						SCStaticText(v,Rect(0,0,40,20).moveTo(origin.x, origin.y + 20))
							.string_(name).align_(\center).font_(Font("Futura", 10));
				};		
			},
			
// END OF GRAPHIC PRIMITIVES


			numerical: { |win, name, cv, rect |
				~label.value(win, name);
				~numberBox.value(win, name, cv, rect);
			},
			
			smallNumerical: { |win, name, cv, rect|
				~label.value(win, name);
				~smallNumberBox.value(win, name, cv, rect);
			},
			
			hslider: { |win, name, cv, rect|
				rect = rect ?? ~sliderRect;
				rect = rect.resizeBy(~labelW * -2,0);
				~label.value(win, name);
				~slider.value(win, name, cv, rect);
			},

			vslider: { |win, name, cv, rect|
				rect = rect ?? ~vsliderRect;
				~label.value(win, name);
				~slider.value(win, name, cv, rect);
			},

			rslider: { |win, name, cv, rect|
				rect = rect ?? ~sliderRect;
				rect = rect.resizeBy(~labelW * -1,0);
				~label.value(win, name);
				~rangeSlider.value(win, name, cv, rect);
			},
			
			vrslider: { |win, name, cv, rect|
				rect = rect ?? ~vsliderRect;
				~label.value(win, name);
				~rangeSlider.value(win, name, cv, rect);
			},

			nslider: { |win, name, cv, rect|
				~hslider.value(win, name, cv, rect);
				~numberBox.value(win, name, cv);
			},

			vnslider: { |win, name, cv, rect| 
				var v, origin, sliderRect, numRect, labelRect;
				sliderRect = rect ? ~vnsliderRect.value;
				numRect = ~smallNumericalRect.value;
				labelRect = numRect;
				cv.asArray.do { |cv|
					v = SCCompositeView(win, numRect.resizeBy(0,sliderRect.height + labelRect.height) );
					origin = v.bounds.origin;
						SCSlider(v, sliderRect.moveTo(origin.x, origin.y))
							.connect(cv);
						SCNumberBox(v,numRect.moveTo(origin.x, origin.y + sliderRect.height))
							.connect(cv);
						SCStaticText(v,labelRect.moveTo(origin.x, origin.y + sliderRect.height + numRect.height))
							.string_(name).align_(\center).font_(Font("Futura", 10));
				};		
			},

			tdslider: { |win, name, cv, rect|
				~label.value(win, name);
				~twodSlider.value(win, name, cv, rect);
			},
			
			multislider: { |win, name, cv, rect|
				var size, slider;
				rect = rect ??  ~msliderRect;
				rect = rect.resizeBy(~labelW * -1,0);
				~label.value(win, name); 
				~scMultislider.value(win, name, cv, rect);
			},

			radiobuttons:	{ |win, name, cv, rect|
				var buttons, link,  size, preVal;
				rect = rect ?? ~radioButtonsRect;
				~label.value(win, name); 
				cv.asArray.do { |cv|
					size = cv.spec.maxval;
					buttons = Array.fill(size).collect { 
						~simpleButton.value(win, rect)
							.states_([
								["", Color.red, Color.grey(0.4)],
								["", Color.red, Color.blue(0.3)]
							])

					};
					buttons.do { |bt, i|
						bt.action_({ | ...x|
							if (preVal > 0) { buttons[preVal - 1].value_(0) }; 
							if (bt.value == 0) 
								{ preVal = 0; cv.value = 0 } 
								{ cv.value = preVal = i + 1};  
						
						})
					};
					
					link = cv.action_( {
						if (preVal > 0) { buttons[preVal - 1].value = 0 };
						preVal = cv.value;
						if (preVal > 0) { buttons[preVal - 1].value = 1 }
					});
					preVal = 0;				
					cv.value = cv.value;		// sync GUI to CV
					buttons[0].onClose_({link.remove});
				}				
			}
			
		);
		
	}
}

