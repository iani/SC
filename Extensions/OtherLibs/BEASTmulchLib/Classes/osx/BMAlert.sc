// from wslib 2006
// changed the class used for the buttons, the positions, fonts, colors, the window cannot be closed, etc. SL 

// made window modal

BMAlert {
	
	var <string, <buttons, <>actions, <color, <iconName, iconView, stringView, <buttonViews;
	var <window, <>onCloseIndex = -1;
	var <>buttonClosesWindow = true;
	
	string_ {  |newString = ""|
		string = newString;
		if( window.notNil && { window.dataptr.notNil } )
			{ stringView.string = string };
		}
		
	iconName_ { |newIconName = \warning|
		iconName = newIconName.asSymbol;
		if( window.notNil && { window.dataptr.notNil } )
			{ iconView.refresh; };
		}
		
	color_ { |newColor|
		color = newColor ? Color.red.alpha_(0.75);
		if( window.notNil && { window.dataptr.notNil } )
			{ window.refresh; };
		}
		
	background { ^window.view.background }
	background_ { |aColor|
		if( window.notNil && { window.dataptr.notNil } )
			{  window.view.background = aColor };
		}
		
	hit { |index| // focussed or last if no index provided
		if( window.notNil && { window.dataptr.notNil } )
			{ index = index ?? { buttonViews.detectIndex({ |bt| bt.hasFocus }) ?
					 ( buttonViews.size - 1 ) }; 
				 buttonViews[ index ] !? 
				 	{ buttonViews[ index ].action.value( buttonViews[ index ], this ) }
				};
		}
		
	enable { |index| if( index.notNil ) // all if no index provided
				{ buttonViews[ index ].enabled_( true ) }
				{ buttonViews.do( _.enabled_( true ) ) };
		}
		
	disable { |index| if( index.notNil )
				{ buttonViews[ index ].enabled_( false ) }
				{ buttonViews.do( _.enabled_( false ) ) };
		}
		
	isEnabled { |index| if( index.notNil )
			{ ^buttonViews[ index ].enabled }
			{ ^buttonViews.collect( _.enabled ) };
		}
		
	focus { |index| if( index.notNil )
				{ buttonViews[ index ].focus( true ); }
				{ buttonViews.last.focus( true ); }
		}
		
	buttonLabel { |index = 0|
		^buttonViews.wrapAt( index ).states[0][0];
		}
	
	buttonLabel_ { |index = 0, newLabel = ""|
		buttonViews.wrapAt( index ).states = [ [ newLabel ] ];
		buttonViews.wrapAt( index ).refresh;
		buttons[ index.wrap( 0, buttons.size - 1 ) ] = newLabel;
		}
	
		
	*new { | string = "Warning!", buttons, actions, color, background, iconName = \warning,
			border = true |
		^super.newCopyArgs( string, buttons, actions, color, iconName ).init( background, border );
		}
		
	openAgain { ^this.init; }
		
	init { |background, border|
		//var buttonViews;
		var charDict;
		
		background = background ? Color.white;
		color = color ? Color.red.alpha_( 0.75 );
		buttons = buttons ?? 
			{buttons = [	["cancel"],
						["ok"]
					 ];
			};
		
		buttons = buttons.collect( { |item|
			case { item.isString }
				{ [ item ] }
				{ item.class == Symbol }
				{ [ item.asString ] }
				{ item.isArray }
				{ item }
				{ true }
				{ [ item.asString ] }
			} );
				
		actions = actions ?? { ( { |i| { |button| buttons[i][0].postln; } } ! buttons.size ); };
						
		window = SCModalWindow( "Alert", 
			Rect.aboutPoint( SCWindow.screenBounds.center, 
				((buttons.size * 42) + 2).max( 160 ), 
					((26 + (string.occurrencesOf( $\n ) * 10) ) + 4 + 20 + 15).max( 52 )
					), false, border ? true );
				//.userCanClose_(false);
					
		//window.front;
		window.view.background_( background );
		window.alwaysOnTop_( true );
		window.alpha_( 0.95 );
		window.drawHook_( { |w|
			Pen.width = 2;
			color.set;
			Pen.strokeRect( w.bounds.left_(0).top_(0).insetBy(1, 1) );
			} );
		
		iconView = SCUserView( window, Rect( 4,4, 72, 72) ).drawFunc_({ |vw|
			color.set;
			DrawIcon.symbolArgs( iconName, vw.bounds );
			}).canFocus_( false );
		
		stringView = SCStaticText(window, Rect(80, 0, window.bounds.width - 84, window.bounds.height - 60 ) )
			.string_( string );
			//.align_( \center );
		
		buttonViews = { |i| 
			var rect;
			rect = Rect( 
					(window.view.bounds.width) - ((buttons.size - i ) * (95 + 10)), 
					window.view.bounds.height - 40, 95, 20 );
			
			RoundButton(window,rect).extrude_(false).canFocus_(false)
					.states_( [
						buttons[i] ] )
					.action_( { |button|
						if( button.enabled )
							{ actions.wrapAt(i).value( button, this );
								if( buttonClosesWindow && { window.dataptr.notNil } )
									{ window.close; };
								};
						} );
					} ! buttons.size;
					
		buttonViews.last.focus;
		
		charDict = ();
		buttonViews.do({ |item, i| // keydownactions for first letters of buttons
			charDict[ item.states[0][0][0].toLower.asSymbol ] = { 
				item.action.value( item, this ) };
			});
		
		buttonViews.do({ |item|
			item.keyDownAction = { |v, char, a,b|
				case { [13,3].includes( b ) } // enter or return
					{ v.action.value( v, this ) }
					{ true }
					{ charDict[ char.asSymbol ].value; };
				};
			});
		
		window.refresh;
		window.onClose_({ buttonViews[onCloseIndex] !? 
			{ buttonViews[onCloseIndex].action.value( buttonViews[onCloseIndex], this ) }; });
		//^super.newCopyArgs( window, string, buttonViews, actions, color, iconName, iconV, strV );
	}
}



