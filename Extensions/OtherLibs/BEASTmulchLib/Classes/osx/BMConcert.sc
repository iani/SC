// notes on sergio's implementation:
// manages a concert, which consists of 
// (system: (speakers: arrays[0], controllers: controllers), pieces: pieces)

// a piece is an optional file path, and controller automation
// pieces is also ordered: backupManager.rememberWorkspace([ \concert, \pieces ],  { List[] });
// could possibly take a BMInOutArray

// now duplicates controllers and sysconfig

BMConcert {	
	var <pieces, <configManager, arrays, controllers, controllerAutomator, sysconfig, backup;
	var <concert;
	

	*new {| pieces, configManager, arrays, controllers, controllerAutomator, sysconfig, backup | 
		^super.newCopyArgs(pieces, configManager, arrays, controllers, controllerAutomator, sysconfig, backup).init
	}

	init { 
		concert = (system: (speakers: arrays[0], controllers: controllers, sysconfig:sysconfig), pieces: pieces)
	}
	
	add {| pieceEvent, indexInList |	 
		if (indexInList.notNil)
		   { concert.pieces.insert(indexInList + 1, pieceEvent) }
		   { concert.pieces.add(pieceEvent) };
		this.changed(\add, pieceEvent)
	}
	
	removeAt {| pieceEventIndex |
		concert.pieces.removeAt(pieceEventIndex);
		this.changed
	}
	
	// who gets this message?
	// cause chain elements (i.e. soundfile players) to load any heavy resources
	// asssociated with a piece
	loadAt {| pieceEventIndex |
		var pieceEvent, element;
		//this.changed(\loadPiece, concert.pieces[pieceEventIndex])
		
		pieceEvent = concert.pieces[pieceEventIndex];
		// probably not the way this should work, but for now...
//		pieceEvent.chainResources.keysValuesDo{| key, value |
// 			element = BMAbstractAudioChainElement.allChainElements[\key];
//			element.notNil.if({ element.loadPiece(value) });
// 		}
		//controllerAutomator.mappings = pieceEvent.controllerAutomation;
		
		BMAbstractAudioChainElement.allChainElements.do({| value |
 			value.loadPiece(pieceEvent);
 		});
 		
 		configManager.currentConfig_(pieceEvent.config, \concertEditor);
		controllerAutomator.mappings = pieceEvent.controllerAutomation;
		
	}
	
	// not sure about this dependancy stuff
	// seems for backup
	storeSession {| configManager |
		backup.makeSessionBackup(this, configManager);
		//this.changed(\storeSession, this, configManager)
	}
	
	storePiece {| pieceName, pieceAndConfig |
		backup.makeBackup(\piece, pieceName, pieceAndConfig);
		//this.changed(\store, \piece, pieceName, pieceAndConfig)
	}
	
	// maybe move this later
	setSpeakers {|newSpeakers|
		arrays[0] = newSpeakers;
		concert.system.speakers = newSpeakers;
		//this.changed(\store, \system, "speakers", newSpeakers);
	}
	
	setSysConfig {|newSysConfig|
		concert.system.sysconfig = newSysConfig;
	}
}


BMConcertGUI  {

	var <concertManager, <configManager, outputArray, <>chain, <chainFunc, <configsGUIFunc, controllerAutomator, name;
	var window, windowView, concertView, concertListView, listSection;
	var addButton, deleteButton, buttonSection, upButton, downButton, storeButton;
	var loadButton, configButton, systemSetup, configText;
	var >onClose;
	var selectable, pieceLoaded = false, loadButtonStates, speakersWindow, importPopUpMenu;
	
	*new {| concertManager, configManager, outputArray, chain, chainFunc,  configsGUIFunc, controllerAutomator, name, origin |
		  ^super.newCopyArgs(concertManager, configManager, outputArray,  chain, chainFunc, configsGUIFunc, controllerAutomator, name)
		  	.init.makeWindow(origin ? (40@200));
	}
	
	init {
		concertManager.addDependant(this);
		configManager.addDependant(this);
		controllerAutomator.addDependant(this);
	}
	
	makeWindow {| origin |
	
		var x, y;
		x = origin.x;
		y = origin.y;
		
		window 					= SCWindow(name, Rect.new(x, y, 410, 456), false).userCanClose = false;
		windowView				= SCCompositeView(window, Rect(5, 5, 400, 435)).background_(Color.white.alpha_(0.2));
		
		concertView 				= SCCompositeView(windowView, Rect(0, 0, 200, 425));
		concertView.decorator 		= FlowLayout(concertView.bounds, Point(10, 10), Point(10, 10));
		SCStaticText.new(concertView, 180 @ 20).string_("Pieces").font_(Font("Helvetica-Bold", 12));


		// Pieces List ---------------------
	
		concertListView				= SCListView(concertView, 180 @ 373)
									   .items_(concertManager.concert.pieces.collect{| x | x.name }.asArray);
									   
		concertListView.keyDownAction 	= { arg view,char,modifiers,unicode,keycode;
								    
									    if(unicode == 127, { deleteButton.action.value(0) });
									    if (unicode == 16rF700, { concertListView.valueAction = concertListView.value - 1 });
									    if (unicode == 16rF703, { concertListView.valueAction = concertListView.value + 1 });
									    if (unicode == 16rF701, { concertListView.valueAction = concertListView.value + 1 });
									    if (unicode == 16rF702, { concertListView.valueAction = concertListView.value - 1 })								    
									  };
		
		concertListView.action			= {| view | 
									   pieceLoaded = false; 
								   	   
								   	   if ((concertManager.concert.pieces.size > 0))
									      { configText.string = concertManager.concert.pieces[concertListView.value].config;
									        loadButton.states = loadButtonStates.loadSelected } 
									      { configText.string = "";
									        loadButton.states = loadButtonStates.noPieces
							    	  	    	  }
								  	  };
								  						concertListView.mouseDownAction  = {| view |
								  	    if (selectable.not) 
								  		  { this.listViewSelection(true);
								  		    if (concertManager.concert.pieces.size > 0)
								  		    	  { configText.string = concertManager.concert.pieces[concertListView.value].config } 
								  		    	  { configText.string = "" }
								  		  }
								  		  
								  	   };
								  	   
		concertListView.background_(Color.white).hiliteColor_(Color.new255(51, 111, 203, 255 * 0.95));
		concertView.decorator.shift(0, -6);
		
		
		// List's Buttons ---------------------
		
		addButton					= RoundButton(concertView, 20 @ 20).extrude_(false).canFocus_(false);		addButton.states 			= [[ '+', Color.black, Color.white.alpha_(0.8) ]];
		addButton.action 			= { this.makeSelectConfigurationWindow(
										{| configsViewItem |
										  this.makeSelectSourceWindow((config: configsViewItem))
										}
									)
								   };
							  	  
		concertView.decorator.shift(-8, 0);
		
		deleteButton				= RoundButton(concertView, 20 @ 20).extrude_(false).canFocus_(false);		deleteButton.states		= [[ '-', Color.black,  Color.white.alpha_(0.8) ]];
		deleteButton.action		= { var viewIndex;
							   	    if (concertListView.item.notNil)
							    	  	  { viewIndex = concertListView.value;
							    	  	    concertManager.removeAt(viewIndex);
							    	  	    if ((viewIndex == (concertManager.concert.pieces.size)) and: { concertManager.concert.pieces.size > 0 })
							    	  	    	   { concertListView.valueAction = viewIndex - 1 }
							    	  	    	   { concertListView.action.value(viewIndex) }
							    	  	  }
							       };

		
		concertView.decorator.shift(4, 0);
		upButton					= RoundButton(concertView, 20 @ 20).extrude_(false).canFocus_(false);		upButton.states			= [[ \up, Color.black,  Color.white.alpha_(0.8) ]];
		upButton.action 			= { var index;
							    
								    index 	= concertManager.concert.pieces.collect{|x| x.name }.indexOf(concertListView.item);
								    if (index.notNil and: {index > 0 })
								    	   { concertManager.concert.pieces = concertManager.concert.pieces.swap(index - 1, index);
								    	     concertManager.changed;
								    	     concertListView.valueAction = index - 1
								    	   }
							 	  };

		concertView.decorator.shift(-8, 0);
		
		downButton				= RoundButton(concertView, 20 @ 20).extrude_(false).canFocus_(false);		downButton.states 			= [[ \down, Color.black,  Color.white.alpha_(0.8) ]];
		downButton.action 			= { var index;
			
								    index 	= concertManager.concert.pieces.collect{|x| x.name }.indexOf(concertListView.item);
								    if (index.notNil and: { index < (concertManager.concert.pieces.size - 1) })
								    	  { concertManager.concert.pieces = concertManager.concert.pieces.swap(index, index + 1);
								    	    concertManager.changed;
								    	    concertListView.valueAction = index + 1
								    	  }
								  };
								  
		concertView.decorator.shift(4, 0); 				
		storeButton				= RoundButton(concertView, 46 @ 20).extrude_(false).canFocus_(false)
					 			  .font_(Font("Arial", 11)).states_([["Store", Color.black,  Color.white.alpha_(0.8) ]])
					 			  .action_{| view | concertManager.storeSession(configManager) };

								  
		// Second Column -------------
						  
		buttonSection 			= SCCompositeView(windowView, Rect(200, 10, 200 + 56, 425));
		buttonSection.decorator 	= FlowLayout(buttonSection.bounds, Point(10, 10), Point(10, 10));
		buttonSection.decorator.shift(0, 20);
				
		loadButton				= RoundButton(buttonSection, 180 @ 20).extrude_(false).canFocus_(false);		loadButtonStates			= (noPieces: 		[[ "No Pieces Available", Color.black, Color.white.alpha_(0.8) ]],
		 						   noSelection:	[[ "No Piece Active", Color.black, Color.white.alpha_(0.8) ]],
		 						   loadSelected: 	[[ "Load Selected Piece", Color.black, Color.white.alpha_(0.8) ]],
		 						   pieceLoaded: 	[[ "Piece Active", Color.black, Color.green.alpha_(0.2) ]]
		 						  );
								   
		loadButton.states 			= if (concertManager.concert.pieces.size > 0)
									{ loadButtonStates.loadSelected }
									{ loadButtonStates.noPieces };
				
		loadButton.action 			= {| view |  
								   if (selectable)
								   	 { if (pieceLoaded.not)
								   	 	  { concertManager.loadAt(concertListView.value);
								   	 	    view.states = loadButtonStates.pieceLoaded;
								   	 	    pieceLoaded = true
								   	 	  }
								   	 	  { ("The Piece \"" ++ concertListView.item ++ "\" has already been loaded").inform }
								   	 }
								  };
		
		buttonSection.decorator.shift(0, 10);
		
		SCStaticText.new(buttonSection, 180 @ 20).string = "Piece Configuration:";
		configText	= SCStaticText.new(buttonSection, 180 @ 20).background_(Color.white)
					  .align_(\center).stringColor_(Color.new255(51, 111, 203, 255 * 0.95))
					  .font_(Font("Helvetica-Bold", 14));
		
		
		buttonSection.decorator.shift(0, 237);	
		
//		RoundButton(buttonSection, 180 @ 20).extrude_(false).canFocus_(false)			   .extrude_(false).canFocus_(false) 
//			   .states_([[ "Speakers", Color.black, Color.white.alpha_(0.8) ]])
//			   .action_({ 
//			   	if (speakersWindow.isNil) 
//			   		{ speakersWindow = BMSpeakerArrayGUI(outputArray, {|newSpeakers| outputArray = newSpeakers; concertManager.setSpeakers(newSpeakers); concertManager.storeSession(configManager); 
//			   			BMAlert("To change the speaker array the system must be restarted.\nDo you want to do this now?", [["cancel", Color.black, Color.new255(51, 111, 203, 255 * 0.95)], ["ok", Color.black, Color.new255(51, 111, 203, 255 * 0.95)]], [nil, {thisProcess.recompile;}], background: Color.white, color: Color.red, border:false);
//			   		}, 
//			   									    "Speaker Array Definition", 129 @ 34);
//			   		  speakersWindow.onClose_({ speakersWindow = nil })
//			   		}
//			   });
//			   
			   
//		buttonSection.decorator.shift(0, 10);	
		SCStaticText.new(buttonSection, 180 @ 20).string = "Import / Export:";
		
		importPopUpMenu = SCPopUpMenu(buttonSection, 180 @ 20)
					   .items_([ " ",
					   		    "Import Concert", "Export Concert", "-",
					   		    "Import Piece", "Export Piece", "-",
					   		    "Import Configuration", "Export Configuration"   
					   		  ])
					   .background_(Color.white)
					   .action_({| view |
					   	  switch(view.value,
					   	   	// Import Concert
					   	   	1,
					   	   	{ CocoaDialog.getPaths({| path | 
								var recalled;
								
								recalled = Object.readTextArchive(path[0]);
								concertManager.concert.pieces = recalled.concert.pieces.deepCopy;
								
								configManager.currentConfig_('all off', \concertEditor);
								//sources
								chain[0].do({|el|
       								el.free; // clean me up
       								el.release; // remove me from BMAbstractAudioChainElement's dict
								});
								chain.copyToEnd(1).do({|el|
       								el.free; // clean me up
       								el.release; // remove me from BMAbstractAudioChainElement's dict
								});
								outputArray.subArrays.copy.do{| key | outputArray.removeSubArray(key) };
								outputArray.keys.copy.do{| key | outputArray.removeAt(key) };
								recalled.concert.system.speakers.subArrays.do{| key | 
									outputArray.defineSubArray(key, recalled.concert.system.speakers.getSubArrayKeys(key).deepCopy) 
								};
   		     					outputArray.addAll(recalled.concert.system.speakers.array.collect{| x | x.value });
								configManager.clear;
								
								if (recalled.configurations.names.indexOf('all off').notNil)
									{ recalled.configurations.dict.removeAt('all off');
									  recalled.configurations.names.removeAt(recalled.configurations.names.indexOf('all off'))
									};
								
								recalled.configurations.names
									.do{| name | 
										configManager.dict.add(name -> recalled.configurations.dict[name])
									   };
								configManager.names = configManager.names.addAll(recalled.configurations.names);
								chain = chainFunc.value;
								configsGUIFunc.value(chain); 
								
								if (selectable.not) { this.listViewSelection(true) };
								concertListView.value_(0).doAction;
								concertManager.storeSession(configManager);
								}, allowsMultiple: false);

							 },
							 
							 // Export Concert
							 2,
							 {  CocoaDialog.savePanel({| path | 
							 	this.prepareForExport.writeTextArchive(path);
							 	concertManager.storeSession(configManager);
							    })
					  		 },
					  		 
					  		 // Import Piece 
					  		 4,
					  		 { CocoaDialog.getPaths({| path | 
								var piece, configuration, pieceAndConfig, completion;
								
								pieceAndConfig = Object.readTextArchive(path[0]);
								piece = pieceAndConfig.piece;
								configuration = pieceAndConfig.config;
								completion = { concertManager.add(piece, concertListView.value);
										      pieceAndConfig = (piece: piece, config: configuration);
										      concertManager.storePiece(piece.name, pieceAndConfig);
										      concertManager.storeSession(configManager);
					   	  				      if (selectable.not) { this.listViewSelection(true) };
					   	  				      concertListView.valueAction = concertListView.value + 1
										    };
										    
								this.makeNewNameWindow(
									piece.name, 
									concertManager.concert.pieces.collect{| e | e.name },
									{| newName | 
					  				piece.name = newName;
					  				if (configManager.names.any{| e | e == piece.config })
					  				 { if (configuration.value != configManager.dict[piece.config])
					  				   	    
					  					{ BMAlert("The name of the Configuration used by this Piece is already in use by a different Configuration.", 
			   			        			    [[ "Rename it", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ],
			   			        			     [ "Use current", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]
			   			        			    ],
			   			        			    [ { this.makeNewNameWindow(
			   			        			          piece.config, 
			   			        			          configManager.names, 
			   			        			          {| newName | 
			   			        			           piece.config = newName;
			   			        			           configuration = newName -> configuration.value;
			   			        			           configManager.add(configuration);
			   			        			           completion.value;
			   			        			           configManager.storeConfiguration(configuration.key)
			   			        			           }
										        )
										      },
										      { configuration.value = configManager.dict[piece.config];
										        completion.value 
										      }
			   			        			    ],
			   			        			    background: Color.white, color: Color.red, border:false
			   			        			  );
			   			        			  
					  					}
					  					{ completion.value };
				   					}
				   					{ configManager.add(configuration);
				   					  completion.value
				   					} 
				   				}
								)
							 }, maxSize: 1)
							 },
					  		 
					  		 // Export Piece
					  		 5, 
					  		 { if (selectable and: { concertListView.items.size > 0 }) 
					  		 	  {  CocoaDialog.savePanel({| path | 
					  		 	  		var piece, configuration, pieceAndConfig;
					  		 	  		
					  		 	  		piece = concertManager.concert.pieces[concertListView.value].deepCopy;
					  		 	  		configuration = piece.config -> configManager.dict[piece.config].deepCopy;
					  		 	  		pieceAndConfig = (piece: piece, config: configuration);
					  		 	  		pieceAndConfig.writeTextArchive(path);
					  		 	  		concertManager.storePiece(piece.name, pieceAndConfig);
					  		 	  		concertManager.storeSession(configManager);
					  		 	  		configManager.storeConfiguration(configuration.key)
									})
								  }
					  		 	  { BMAlert( "Please select a Piece", 
		   			        			 [[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]],
		   			        			 background: Color.white, color: Color.red, border:false
		   			        	 		) 
		   			          	  }
					  		 },
							 
							 // Import Configuration
							 7, 
					  		 { CocoaDialog.getPaths({| path | 
								var configuration;
								
								configuration = Object.readTextArchive(path[0]);
								this.makeNewNameWindow(
									configuration.key, 
									configManager.names, 
									{| newName | 
									  configuration = newName -> configuration.value;
									  configManager.add(configuration);
									  configManager.storeConfiguration(configuration.key);
									  concertManager.storeSession(configManager);
									  
									}
								)
							 }, maxSize: 1)
							 },
							 
							 // Export Configuration
							 8,
							 { this.makeSelectConfigurationWindow(
							   	{| configName | 
							   	 var configuration;
							   	
							   	 configuration = configName -> configManager.dict[configName].deepCopy;
							   	 CocoaDialog.savePanel({| path | 
							   	 	configuration.writeTextArchive(path);
							   	 	configManager.storeConfiguration(configuration.key);
							   	 	concertManager.storeSession(configManager);
							   	 	
							 	 })
					  		     }
					  		   )
					  		 }
					  	);
					  	
					  	view.value = 0
					  });

	    this.listViewSelection(false);
	    
		window.onClose 			= { concertManager.removeDependant(this); 
								    onClose.value(this) 
								  };
		window.front
	}
			
	update {| changed, change, config, from |
		var piece;
		if ((change == \currentConfig) and: { from == \configurationEditor }) 
	    	  { 	if (selectable) { this.listViewSelection(false) } }
		  {  if ((change != \storeSession) and: { change != \store }) 
		  		{ concertListView.items = concertManager.concert.pieces.collect{| x | x.name }.asArray }
		  };
		  
		
		if(change == \sequencesChanged, {
			
			piece = concertManager.concert.pieces[concertListView.value];
			piece.controllerAutomation = controllerAutomator.mappings;
		});
		
	}
		
	prepareForExport {	var names, dict;
									
		names	= List[];
		dict		= ();
		concertManager.concert.pieces
			.do{| piece | 
			    var configName = piece.config;
			    
			    if (names.indexOf(configName).isNil) 
			    	  { dict.add(configName -> configManager.dict[configName]);
			    	    names.add(configName)
			    	  }
			};
		^(concert: concertManager.concert.deepCopy, configurations: (dict: dict.deepCopy, names: names))
	}
	
	
	makeSelectConfigurationWindow {| action, origin |
		
		var window, name, configsView, okButton;
		 
		origin		= origin ?? { 490 @ 500 };
		window 		= SCWindow("Select a Configuration", Rect(origin.x, origin.y, 200 + 20 , 317 + 60), false).userCanClose_(false);
		
		window.view.decorator = FlowLayout(window.view.bounds, Point(10, 10), Point(10, 10));
		
		configsView 				= SCListView(window, 200 @ 317).canReceiveDragHandler = false;
		configsView.background_(Color.white).hiliteColor_(Color.new255(51, 111, 203, 255 * 0.95)); 
		configsView.items 			= configManager.names.asArray;
		
		RoundButton(window, 95 @ 20)
			   .extrude_(false).canFocus_(false)
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	window.close });
			   
		okButton = RoundButton(window, 95 @ 20)
				   .extrude_(false).canFocus_(false)
				   .states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
				   .action_({ window.close;
				   			action.value(configsView.item)
				   			
				   		   });
				   		   
		
		window.front
	}
	
	makeSelectSourceWindow {| event, origin |
		
		var window, name, button, okButton;
		 
		origin		= origin ?? { 490 @ 500 };
		window 		= SCWindow("Sources", Rect(origin.x, origin.y, 260, 110), false).userCanClose_(false);
		
		window.view.decorator = FlowLayout(window.view.bounds, Point(10, 10), Point(10, 10));
		 
		button 		= RoundButton(window, 240 @ 20)
						.extrude_(false).canFocus_(false)
				 		.states_([[ "Do you want to use a soundfile? Yes", Color.black, Color.green(alpha: 0.2) ],
				 				 [ "Do you want to use a soundfile? No", Color.black, Color.clear ]
				 				]);

		window.view.decorator.shift(0, 30);
		
		RoundButton(window, 115 @ 20)
			   .extrude_(false).canFocus_(false)
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	window.close });
			   
		okButton = RoundButton(window, 115 @ 20)
				   .extrude_(false).canFocus_(false)
				   .states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
				   .action_({ var origin =  490 @ 500;
				   
				   			window.close;
				   			if (button.value == 0)
				   			   { CocoaDialog.getPaths({| path | 
				   			   					   event.add(\path -> path[0]);
												   this.makeNewPieceWindow(event, origin);
											       }, 
											       maxSize:1
								)
							   }
							   { this.makeNewPieceWindow(event, origin) }
				   		   });
		window.front
	}
	
	makeNewPieceWindow {| event, origin |

		var suggestedName;
		 
		suggestedName		= if (event[\path].notNil)	{ event[\path].basename.splitext[0] } { "Fileless Piece" };
		this.makeNewNameWindow(suggestedName,
							concertManager.concert.pieces.collect{| e | e.name },
							{| newName | 
							  event.add(\name -> newName);
						   	  concertManager.add(event, concertListView.value);
						   	  concertManager.storeSession(configManager);
						   	  if (selectable.not) { this.listViewSelection(true) };
						   	  concertListView.valueAction = concertListView.value + 1
						   	}
		)							
		
	}
	
	
	makeNewNameWindow {| suggestedName, usedNames, action, origin |
		var window, pieceNameField, okButton;
		 
		origin		= origin ?? { 490 @ 500 };
		window 		= SCWindow("Select a Name", Rect(origin.x, origin.y, 260, 110), false).userCanClose_(false);
		window.view.decorator = FlowLayout(window.view.bounds, Point(10, 10), Point(10, 10));
		
		SCStaticText(window, 50 @ 20).string = "Name:";

		pieceNameField	= SCTextField(window, 180 @ 20).string_(suggestedName.asString);				
		window.view.decorator.shift(0, 30);

		RoundButton(window, 115 @ 20)
			   .extrude_(false).canFocus_(false)
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	window.close });
			   
		okButton = RoundButton(window, 115 @ 20)
				   .extrude_(false).canFocus_(false)
				   .states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
				   .action_({ var name;
				   			
				   			name = pieceNameField.string;
				   			if (name.size > 0) 
				   			   { name = name.asSymbol;
				   			   
				   			     if (usedNames.any{| e | e == name })
				   			        	{ BMAlert( "The name \"" ++ name ++ "\" is already taken. Please choose a different name.", 
				   			        			 [[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]],
				   			        			 background: Color.white,
				   			        			 color: Color.red,
				   			        			 border:false
				   			        	 ) 
				   			          }
					   				{ window.close;
					   				  action.value(name)
					   				}
					   		   }
				   		   });
		pieceNameField.focus;
		window.front
	}


	listViewSelection {| condition |
					
					selectable = condition;
					if (selectable)
					   { concertListView.selectedStringColor = Color.white;
						concertListView.hiliteColor = Color.new255(51, 111, 203, 255 * 0.95);
						loadButton.states 	= loadButtonStates.loadSelected;
						pieceLoaded 		= false
					   }
					   { concertListView.selectedStringColor = Color.black;
					   	concertListView.hiliteColor = Color.clear; 
					   	configText.string = "";
					   	loadButton.states = if (concertManager.concert.pieces.size > 0)
											{ loadButtonStates.noSelection }
											{ loadButtonStates.noPieces };
					   	pieceLoaded 		= false
					   }
	}
}


