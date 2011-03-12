// notes on Sergio's implementation below
// manages sets of chain configs
// stores in a dict, but also seems to have to do with order?

// configs seems to be an Event  (dict:  IdentityDictionary[], names: List[ 'all off' ])
// configs could perhaps be something ordered? 

BMConfigurations {
	var <configurations, backup;
	var <dict, <names, <currentConfig;

	*new {| configurations, backup |
		^super.newCopyArgs(configurations, backup).init
	}

	init {
		dict = configurations.dict;
		names = configurations.names; // names is a List
		this.addAllOff
	}
	
	// adds the allOff configuration
	// maybe this could just be a clear over the chain array
	addAllOff{
		dict.add('all off' -> 
			 IdentityDictionary[
				\mackies -> BMAbstractController
							.allControllers
							.collect({|interface, name|
								      IdentityDictionary[
								         \ctrlVals -> interface.getAllValues
								      ]
						      })			      
			 ].deepCopy
		);
		BMAbstractAudioChainElement
		 .allChainElements.keysValuesDo{| key, value | dict['all off'].add(key -> value.mappings.deepCopy) }
	}	
	  
	clear { 
		dict 	= IdentityDictionary[ 'all off' -> dict['all off'] ];
		names	= List[ 'all off' ]
	}
	
	// this seems never to be called
	// needed?
	dict_{| x |
		dict = x;
		this.changed(\dict)
	}
	
	// this is used
	// why?
	names_{| x |
		names = x;
		this.changed(\names) 
	}
	
	// sets the current config and notifies dependants
	currentConfig_{| configName, from |
		currentConfig = configName;
		this.loadConfig(configName);
		this.changed(\currentConfig, configName, from)
	}
	
	// seems to add and deals with order
	add {| configuration, indexInNamesList |
	     if (indexInNamesList.notNil)
	   	   { names.insert(indexInNamesList + 1, configuration.key) }
	   	   { names = names.add(configuration.key) };
	   	dict.add(configuration);
	   	this.changed(\add, configuration.key);	   
	}
	
	removeAt {| configurationIndex |
		dict.removeAt(names[configurationIndex]);
		names.removeAt(configurationIndex);
		this.changed(\removeAt)
	}
	
	store {| name |
	// store the current mappings in the selected configuration
	       if (name != 'all off')
	       	 { dict[name] = IdentityDictionary[];
		        BMAbstractAudioChainElement.allChainElements
				 .keysValuesDo{| key, value |
		 			 dict[name].add(key -> value.mappings.deepCopy)
		 		  };
			   // and make a backup
		 	   this.storeConfiguration(name, name -> dict[name]);
		 	 }
 		  	 { "The Configuration \"all off\" cannot be modified".error }
	}
	
	// why does this happen through dependancy?
	storeConfiguration {| configName |
		backup.makeBackup(\configuration, configName, configName -> dict[configName]);
		//this.changed(\store, \configuration, configName, configName -> dict[configName])
	}
	
	loadConfig {| configName |
   		   BMAbstractAudioChainElement
			 .allChainElements
			 .keysValuesDo{| key, value |
			 			 BMAbstractAudioChainElement.allChainElements[key].mappings = dict[configName][key]
			 }
	}
	
}




// display an element order and generates and tracks element GUIs
// if an item in chain is an array it goes at the same level
BMConfigurationsGUI : BMAbstractGUI {
	var <chain, configurations, concertManager, name, guis, objects;
	var chainView;
	var configView, configViewWidth, chainView, configListView;
	var newButton, copyButton, deleteButton, storeButton, upButton, downButton;
	
	*new {|chain, configurations, concertManager, name, origin|
		^super.new.init(chain, configurations, concertManager, name ? "Signal Chain").makeWindow(origin ? (250@550));
	}
	
	init {|argChain, argConfigurations, argConcertManager, argName|
		chain = argChain;
		configurations = argConfigurations;
		concertManager = argConcertManager;
		name = argName;
		guis = IdentityDictionary.new; // use Objects as keys
		configurations.addDependant(this);
		//chainManager.addDependant(this);
	}

	makeWindow {|origin|
		var x, y, rows, columns, width, pseudoLevels, pseudoTimes, count = 0, selected;
		var points, rects, selectedIndex;

		x 			= origin.x;
		y			= origin.y;
		objects 		= chain[0] ++ chain.copyToEnd(1);
		selected 		= false ! objects.size;
		rows 		= chain.size;
		columns		= chain[0].size;
		width 		= max(450, columns * 100);
		
		pseudoLevels 	= (1..rows).normalize * 0.8 + 0.075;
		pseudoLevels 	= pseudoLevels.collect({|item, i| if(i == 0, {item ! columns}, {item})}).flat;
		pseudoTimes 	= [(1..columns).normalize - 0.5 * 0.68 + 0.5, 0.5 ! (rows - 1)].flat;
		
		configViewWidth = 210;
		
		window = SCWindow(name, Rect.new(x, y, 450 + configViewWidth + 27, 450 + 6), false);
		window.view.decorator = FlowLayout(window.view.bounds);
		window.userCanClose_(false);
		
		
		configView			= SCCompositeView(window, configViewWidth @ 435);
		configView.decorator 	= FlowLayout(configView.bounds, Point(5, 5), Point(5, 5));
		configView.background	= Color.white.alpha_(0.2);
		SCStaticText(configView, 180 @ 20).string_("Configurations").font_(Font("Helvetica-Bold", 12));
		configView.decorator.shift(0, 5);
		configListView		= SCListView(configView, 200 @ 373).canReceiveDragHandler = false;
		configListView.background_(Color.white).hiliteColor_(Color.new255(51, 111, 203, 255 * 0.95));

		configListView.action	= {| view |
							   configurations.currentConfig_(view.item, \configurationEditor); 
							  };

		configView.decorator.nextLine;
	
		newButton				= RoundButton(configView, 20 @ 20).extrude_(false).canFocus_(false)
					 		  .font_(Font("Arial", 11)).states_([['+', Color.black,  Color.white.alpha_(0.8) ]])
					 		  .action_({ this.makeNewConfigWindow("New", 490 @ 500) });
					 		  
		configView.decorator.shift(-3, 0);
		
		copyButton			= RoundButton(configView, 46 @ 20).extrude_(false).canFocus_(false)
					 		  .font_(Font("Arial", 11)).states_([["Copy", Color.black,  Color.white.alpha_(0.8) ]])
					 		  .action_({ this.makeNewConfigWindow("Copy", 490 @ 500) });
		
		configView.decorator.shift(-3, 0);			 		  
		deleteButton			= RoundButton(configView, 20 @ 20).extrude_(false).canFocus_(false)
					 		  .font_(Font("Arial", 11)).states_([['-', Color.black,  Color.white.alpha_(0.8) ]]);
			 		  
		deleteButton.action	= { var viewIndex, name;
						   	    
						   	    viewIndex 	= configListView.value;
						    	    name			= configListView.item;
						    	    
							    if (name != 'all off')
							    	   { configurations.removeAt(configurations.names.indexOf(name));
							    	     if ((viewIndex == (configurations.names.size)) and: { configurations.names.size > 0 })
							    	     	{ configListView.value = viewIndex - 1 }
					    	  	    	   		{ if (viewIndex > 0) { configListView.value(viewIndex) }};
					    	  	    	     configurations.currentConfig_(configListView.item, \configurationEditor);
					    	  	    	   }
					    	  	    	   { "The Configuration \"all off\" cannot be deleted".error }
						    	  	   
						       };
						       
		configView.decorator.shift(6, 0);

		upButton				= RoundButton(configView, 20 @ 20).extrude_(false).canFocus_(false);		upButton.states		= [[ \up, Color.black,  Color.white.alpha_(0.8) ]];
		upButton.action 		= { var index;
						    
							    index 	= configurations.names.indexOf(configListView.item);
							    if (index.notNil and: {index > 0 })
							    	   { configurations.names = configurations.names.swap(index - 1, index);
							   
						    	          configListView.value = index - 1
							    	   }
						 	  };

		configView.decorator.shift(-3, 0);
	
		downButton			= RoundButton(configView, 20 @ 20).extrude_(false).canFocus_(false);		downButton.states 		= [[ \down, Color.black,  Color.white.alpha_(0.8) ]];
		downButton.action 		= { var index;
		
							    index 	= configurations.names.indexOf(configListView.item);
							    if (index.notNil and: { index < (configurations.names.size - 1) })
							    	  { configurations.names = configurations.names.swap(index, index + 1);
							    	    configListView.value = index + 1
							    	  }
							  };
				 		  
		configView.decorator.shift(6, 0); 				
		storeButton			= RoundButton(configView, 46 @ 20).extrude_(false).canFocus_(false)
				 		 	 .font_(Font("Arial", 11)).states_([["Store", Color.black,  Color.white.alpha_(0.8) ]])
				 		 	 .action_({ 
				 		 	 	configurations.store(configListView.item);
				 		 	 	concertManager.storeSession(configurations);
				 		 	 });

		chainView				= SCScrollView(window, 465 @ 435).hasBorder_(false);
		if(width <= 465, { chainView.hasHorizontalScroller = false });
		chainView 			= SCCompositeView(chainView, Rect(0, 0, width, max(450, rows * 20)));
		chainView.background 	= Color.white.alpha_(0.2);
		chainView 			= SCUserView(chainView, Rect(0, 0, width, max(450, rows * 20)));
		
		
		pseudoLevels = pseudoLevels * chainView.bounds.height;
		pseudoTimes = pseudoTimes * chainView.bounds.width;
		
		points = Array.fill(objects.size, {|i|  Point(pseudoTimes[i], pseudoLevels[i])});
		rects = points.collect({|point| Rect.aboutPoint(point, 50, 15)});

		chainView.drawFunc_({
			// draw lines
			columns.do({|i| Pen.line(points[i], points[columns])});
			(rows - 2).do({|i| Pen.line(points[i + columns], points[i + columns + 1])});
			Pen.stroke;
			
			// draw backgrounds, boxes and strings
			rects.do({|rect, i|
				selected[i].if({Color.grey(0.75)}, {Color.grey}).set;
				Pen.fillRect(rect);
				Color.black.set;
				Pen.strokeRect(rect);
				objects[i].name.asString.drawCenteredIn(rect, Font("Arial", 10), Color.black);
			});
		});		
		chainView.mouseDownAction = {|view, x, y|
			var hitpoint, element;
			hitpoint = x@y;
			selectedIndex = rects.detectIndex({|rect| rect.containsPoint(hitpoint)});
			if(selectedIndex.notNil, { 
				selected[selectedIndex] = true; 
				element = objects[selectedIndex];
				guis[element].notNil.if({ 
					guis[element].window.front;
				},{	guis[element] = element.gui;
					guis[element].notNil.if({guis[element].onClose_({guis[element] = nil }); });
				});
				view.refresh;
			});
			
		};
		
		chainView.mouseUpAction = {|view|
			if(selectedIndex.notNil, { selected[selectedIndex] = false; });
			view.refresh;
		};
		
		this.update;
		configListView.value = configurations.names.indexOf('all off');
		window.onClose = { configurations.removeDependant(this);
						//chainManager.removeDependant(this);
						guis.do{| element | 
							if (element.isKindOf(BMMatrixMenuGUI) and: { element.matrixGUI.notNil }) 
							   { element.matrixGUI.window.close };
							element.window.close;
						};
						onClose.value(this);
					   };
		window.front;
	}
	
	update {| changed, change, argument, from |
			if (change == \freed)
			   { window.close }
			   { configListView.items = configurations.names.asArray;
			     if ((change == \currentConfig) and: { from == \concertEditor }) 
	         	   	   { configListView.value = configurations.names.indexOf(argument) }
	         	   }
	}
	
	makeNewConfigWindow {| method, origin |

		var window, name, pieceNameField, okButton;
		 
		window 			= SCWindow(method + "Configuration", Rect(origin.x, origin.y, 260, 110), false).userCanClose_(false);
		window.view.decorator = FlowLayout(window.view.bounds, Point(10, 10), Point(10, 10));
		
		SCStaticText(window, 50 @ 20).string = "Name:";

		pieceNameField	= SCTextField(window, 180 @ 20);
					
		window.view.decorator.shift(0, 30);
		
		RoundButton(window, 115 @ 20)
			   .extrude_(false).canFocus_(false) 
			   .states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			   .action_({	window.close });
			   
		okButton = RoundButton(window, 115 @ 20)
				   .extrude_(false).canFocus_(false)
				   .states_([[ "Create", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
				   .action_({ var name;
				   			
				   			name = pieceNameField.string;
				   			if (name.size > 0) 
				   				{ name = name.asSymbol;
				   				  if (configurations.names.any{| nameInList | nameInList == name })
				   			        	{ BMAlert( "The name \"" ++ name ++ "\" is already taken. Please choose a different name.", 
				   			        			 [[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]],
				   			        			 background: Color.white,
				   			        			 color: Color.red,
				   			        			 border: false
				   			        	 ) 
				   			          }
				   			          { 
				   				
					   				  window.close;
					   				  if (method == "New")
					   				  	{ configurations.add(name -> configurations.dict['all off'].deepCopy, configListView.value) }
					   				  	{ configurations.add(name -> configurations.dict[configListView.item].deepCopy, configListView.value) };
					   				  configurations.currentConfig_(name, \configurationEditor);
					   				  configurations.storeConfiguration(name);
					   				  concertManager.storeSession(configurations);
					   				  configListView.value = configListView.value + 1;
				   				  
				   				 	 }
				   				 
				   				 
				   				 }
				   				 

				   		   });
		pieceNameField.focus;
		window.front
	}
}