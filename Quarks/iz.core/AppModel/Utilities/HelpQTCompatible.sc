
HelpQtCompatible : Help {
/* Mofifying Help:gui to make it work with 3.5 qt and the new help scheme.
The modifications are: 

- Check if the platform is osx. If yes, use modified scheme, otherwise use standard gui. 
- Modified scheme for osx: 
	- Save the user's current GUI scheme
	- Switch to CocoaGUI because it is the one that this old Help gui really works well with
	- Modify the action of button "Open Help File" to open the help file using unixCmd "open <path>",
	  because the openHelpFile message would open in the new Help scheme, which does not 
	  work for old .scd or .rtf files. 
	- Restore the user's GUI scheme 1 second after opening this GUI. 
*/

	gui { | sysext = true, userext = true, allowCached = true |
		Platform.case(
			\osx, { this.guiOSX(sysext, userext, allowCached) },
			{ super.gui(sysext, userext, allowCached) }
		);
	}

	guiOSX { | sysext=true, userext=true, allowCached=true|
	var classes, win, lists, listviews, numcols=7, selecteditem, node, newlist, curkey;
	var selectednodes, scrollView, compView, textView, keys;
	var classButt, browseButt, bwdButt, fwdButt;
	var isClass, history = [], historyIdx = 0, fBwdFwd, fHistoryDo, fHistoryMove;
	var screenBounds, bounds, textViewBounds, results, resultsview, statictextloc;
	var searchField, helpguikeyacts, fSelectTreePath, inPathSelect = false, fUpdateWinTitle, fLoadError;
	var usersGUIscheme;

	usersGUIscheme = GUI.current; // usersGUIscheme.postln; usersGUIscheme.class.postln;
	
	GUI.set(CocoaGUI);	//  the old help scheme works best with CocoaGUI on MacOS X ...

	// Call to ensure the tree has been built
	this.tree( sysext, userext, allowCached );

	// Now for a GUI
	screenBounds = Window.screenBounds;
	bounds = Rect(128, 264, 1040, 564);
	bounds = bounds.center_(screenBounds.center);
	bounds = bounds.sect(screenBounds.insetBy(15));
	win = Window.new("Help browser", bounds); // SCWindow
	// scrollView and compView hold the category-browsing list widgets
	scrollView = ScrollView.new(win, Rect(5, 0, 425, 529)).hasBorder_(true).resize_(4);
	compView = CompositeView.new(scrollView, Rect(0, 0, numcols * 200, /*504*/ bounds.height-60));
	// textView displays a help file "inline"
	textViewBounds = Rect(435, 0, /*620*/bounds.width-435, /*554*/ bounds.height-35);
	textView = TextView.new(win, textViewBounds)
		.hasVerticalScroller_(true)
		.hasHorizontalScroller_(false)
		.autohidesScrollers_(false)
		.resize_(5)
		.canFocus_(true);

	if(GUI.current.id == \swing, { textView.editable_( false ).canFocus_( true ) });

	textView.bounds = textView.bounds; // hack to fix origin on first load

	// hidden at first, this will receive search results when the search field is activated
	resultsview = ScrollView(win, textViewBounds)
				.resize_(5)
				.visible_(false);

	// updates the history arrow buttons
	fBwdFwd = {
		bwdButt.enabled = historyIdx > 0;
		fwdButt.enabled = historyIdx < (history.size -	1);
	};

	fLoadError = { |error|
		error.reportError;
		"\n\nA discrepancy was found in the help tree.".postln;
		if(allowCached) {
			"rm \"%\"".format(cachePath).unixCmd;
			"The help tree cache may be out of sync with the file system. Rebuilding cache. Please reopen the Help GUI when this is finished.".postln;
			this.rebuildTree;
			win.close;
		} {
			"Please report the above error dump on the sc-users mailing list.".postln;
		};
	};

	// cuts the redo history, adds and performs a new text open action
	fHistoryDo = { arg selector, argum;
		history		= history.copyFromStart( historyIdx ).add([ selector, argum ]);
		historyIdx	= history.size - 1;
		try({ textView.perform( selector, argum ) }, fLoadError);
		fBwdFwd.value;
	};

	// moves relatively in the history, and performs text open action
	fHistoryMove = { arg incr; var entry;
		historyIdx	= historyIdx + incr;
		entry		= history[ historyIdx ];
		try({ textView.perform( entry[ 0 ], entry[ 1 ]) }, fLoadError);
		fBwdFwd.value;
	};

	// keep this check for compatibility with old versions of swingOSC
	if( textView.respondsTo( \linkAction ), {
		textView
			.linkAction_({ arg view, url, descr;
				var path;
				if( url.notEmpty, {
					//fHistoryDo.value( \open, url );
					keys = this.findKeysForValue(url);
					if(keys.size == 0, {
						("Invalid hyperlink:" + url + "Please repair this.\nSearching help directories for alternative.").warn;
						url = Help.findHelpFile(url.basename.splitext.first);
						url.notNil.if({keys = this.findKeysForValue(url)});
					});
					if(keys.size > 0, {
						fSelectTreePath.value(keys.drop(-1), keys.last.asString);
					});
				}, {
					if( descr.beginsWith( "SC://" ), {
						fHistoryDo.value( \open, descr );
					});
				});
			});
	});

	lists = Array.newClear(numcols);
	lists[0] = tree.keys(Array).collect(_.asString).sort;
	selectednodes = Array.newClear(numcols);

	// SCListView
	listviews = (0..numcols-1).collect({ arg index; var view;
		view = ListView( compView, Rect( 5 + (index * 200), 4, 190, /* 504 */ bounds.height - 60 ));
		//view.items = []; // trick me into drawing correctly in scrollview
		if( view.respondsTo( \allowsDeselection ), {
			view.allowsDeselection_( true ).value_( nil );
		});
		view
		.resize_(4)
		.action_({ arg lv; var lv2;
			if( lv.value.notNil, {
				// We've clicked on a category or on a class

				if((lv.items.size != 0), {
					lv2 = if( index < (listviews.size - 1), { listviews[ index + 1 ]});

					selecteditem = lists[index][lv.value];
					if( lv2.notNil, {
						// Clear the GUI for the subsequent panels
						listviews[index+1..].do({ arg lv; lv.items=#[];
							if( lv.respondsTo( \allowsDeselection ), { lv.value = nil })});
					});

					// Get the current node, from the parent node
					node = try { if(index==0, tree, {selectednodes[index-1]})[selecteditem] };
					curkey = selecteditem;
					selectednodes[index] = node;

					if(node.isNil, {
						// We have a "leaf" (class or helpdoc), since no keys found

						if( (index + 1 < lists.size), { lists[index+1] = #[] });

						if(inPathSelect.not, {
						{
							// Note: the "isClosed" check is to prevent errors caused by event triggering while user closing window
							if(textView.isClosed.not){textView.visible = true};
							if(resultsview.isClosed.not){resultsview.visible = false};
							fHistoryDo.value( \open, fileslist.at( selecteditem.asSymbol ) ? fileslist.at( \Help ));
						}.defer( 0.001 );
						});
						isClass = selecteditem.asSymbol.asClass.notNil;
						// Note: "Help" class is not the class that matches "Help.html", so avoid potential confusion via special case
                            if(classButt.notNil){
                            	classButt.enabled_((selecteditem!="Help") and: {isClass});
                            };
						browseButt.enabled_((selecteditem!="Help") and: {isClass});
						// The "selectednodes" entry for the leaf, is the path to the helpfile (or "")
						selectednodes[index] = try { if(index==0, {tree}, {selectednodes[index-1]})
									[curkey.asSymbol.asClass ? curkey.asSymbol]};

						fUpdateWinTitle.value;
					}, {
						// We have a category on our hands
						if( lv2.notNil, {
							lists[ index + 1 ] = node.keys(Array).collect(_.asString).sort({|a,b|
									// the outcomes:
									// a and b both start with open-bracket:
									//	test result should be a < b
									// or one starts with open-bracket and the other doesn't (xor)
									//	test result should be whether it's a that has the bracket
								if(a[0] == $[ /*]*/ xor: (b[0] == $[ /*]*/)) {
									a[0] == $[ /*]*/
								} {
									a < b
								}
							});
							lv2.items = lists[index+1];
						});

					});

					if( (index + 1) < listviews.size, {
						listviews[index+1].value = if( listviews[index+1].respondsTo( \allowsDeselection ).not, 1 );
						listviews[index+1].valueAction_( 0 );
					});
					selectednodes[index+2 ..] = nil; // Clear out the now-unselected
				});
			});
		});
	});

	listviews[0].items = lists[0];

	// Add keyboard navigation between columns
	listviews.do({ |lv, index| // SCView
		lv.keyDownAction_({|view,char,modifiers,unicode,keycode|
			var nowFocused, lv2;
			nowFocused = lv;
			switch(unicode,
			// cursor left
			63234, { if(index > 0, { lv2 = listviews[ index - 1 ]; lv2.focus; nowFocused = lv2 })
			},
			// cursor right
			63235, { if( index < (listviews.size - 1) and: { listviews[ index + 1 ].items.notNil }, {
						lv2 = listviews[ index + 1 ];
						try {
							lv2.value_( if( lv2.respondsTo( \allowsDeselection ).not, - 1 )).valueAction_( 0 ).focus;
							nowFocused = lv2;
						}
				   })
			},
			13, { // Hit RETURN to open source or helpfile
				// The class name, or helpfile name we're after

				if(lv.value.notNil and: {if(index==0, tree, {selectednodes[index-1]})[lists[index][lv.value]].isNil}, {
					{ selecteditem.openHelpFile }.defer;
				});
			},
			//default:
			{
				// Returning nil is supposed to be sufficient to trigger the default action,
				// but on my SC this doesn't happen.
				view.defaultKeyDownAction(char,modifiers,unicode);
			});
			if(scrollView.visibleOrigin.x > nowFocused.bounds.left or: {scrollView.visibleOrigin.x + scrollView.bounds.width > nowFocused.bounds.left}, {
				scrollView.visibleOrigin_(Point(nowFocused.bounds.left - 5, 0));
			});
		})
		.mouseDownAction_({|view, x, y, modifiers, buttonNumber, clickCount|
			{
			if(lists[index][lv.value][0]==$[, {
				if(scrollView.visibleOrigin.x != (lv.bounds.left - 5), {
					{
					10.do({|i| { scrollView.visibleOrigin_(
									Point(((lv.bounds.left - lv.bounds.width)+((10+i)*10)-5), 0))
								}.defer;
						0.02.wait;
					});
					}.fork;
				});
			});
			}.defer(0.01); // defer because .action above needs to register the new index

			if(clickCount == 2, {
				if(lv.value.notNil and: { try { if(index==0, tree, {selectednodes[index-1]})[lists[index][lv.value]] }.isNil}, {
					{ selecteditem.openHelpFile }.defer;
				});
			});
		});
	});

	// Add ability to programmatically select an item in a tree
	fSelectTreePath = { | catpath, leaf |
		var foundIndex;
		Task{
			0.001.wait;
			inPathSelect = true;
			catpath.do{ |item, index|
				foundIndex = listviews[index].items.indexOfEqual(item);
				if(foundIndex.notNil){
					listviews[index].value_(foundIndex).doAction;
				}{
					"Could not select menu list item % in %".format(item, listviews[index].items).postln;
				};
				0.02.wait;
			};
			inPathSelect = false;
			foundIndex = listviews[catpath.size].items.indexOfEqual(leaf);
			if(foundIndex.notNil){
				listviews[catpath.size].value_(foundIndex).doAction;
//				history = history.drop(-1);
//				historyIdx = history.size - 1;
			}{
				"Could not select menu list item %".format(leaf).postln;
			};
			textView.visible = true;
			resultsview.visible = false;
			fUpdateWinTitle.value;
			win.front;
		}.play(AppClock);
	};

	fUpdateWinTitle = {
		win.name_(
			(["Help browser"] ++ listviews.collect{|lv| lv.value !? {lv.items[lv.value]} }.reject(_.isNil)).join(" > ") );
	};

	Platform.case(\windows, {
            // TEMPORARY WORKAROUND:
            // At present, opening text windows from GUI code can cause crashes on Psycollider
            // (thread safety issue?). To work around this we just remove those buttons.
	}, {
		Button.new( win, Rect( 5, /* 534 */ bounds.height - 30, 110, 20 ))
			.states_([["Open Help File", Color.black, Color.clear]])
			.resize_(7)
			.action_({
				{
				format("open %", fileslist[selecteditem.asSymbol].asCompileString).unixCmd;
				}.defer;
			});
		classButt = Button.new( win, Rect( 119, /* 534 */ bounds.height - 30, 110, 20 ))
			.states_([["Open Class File", Color.black, Color.clear]])
			.resize_(7)
			.action_({
				if(selecteditem.asSymbol.asClass.notNil, {
					{selecteditem.asSymbol.asClass.openCodeFile }.defer;
				});
			});
	});
	browseButt = Button.new( win, Rect( 233, /* 534 */ bounds.height - 30, 110, 20 ))
		.states_([["Browse Class", Color.black, Color.clear]])
		.resize_(7)
		.action_({
			if(selecteditem.asSymbol.asClass.notNil, {
				{selecteditem.asSymbol.asClass.browse }.defer;
			});
		});
	bwdButt = Button.new( win, Rect( 347, /* 534 */ bounds.height - 30, 30, 20 ))
		.states_([[ "<" ]])
		.resize_(7)
		.action_({
			if( historyIdx > 0, {
				fHistoryMove.value( -1 );
			});
		});
	fwdButt = Button.new( win, Rect( 380, /* 534 */ bounds.height - 30, 30, 20 ))
		.states_([[ ">" ]])
		.resize_(7)
		.action_({
			if( historyIdx < (history.size - 1), {
				fHistoryMove.value( 1 );
			});
		});
	fBwdFwd.value;

	// textfield for searching:
	statictextloc = Rect(10, 10, textViewBounds.width-20, 200);
	StaticText.new(win, Rect(435, bounds.height-35, 100 /* bounds.width-435 */, 35))
		.align_(\right).resize_(7).string_("Search help files:");
	searchField = TextField.new(win, Rect(535, bounds.height-35, bounds.width-535-35, 35).insetBy(8))
		.resize_(8).action_({|widget|

			if(widget.value != ""){
				// Let's search!
				// hide the textView, show the resultsview, do a query
				textView.visible = false;
				resultsview.visible = true;
				resultsview.removeAll;
				results = this.search(widget.value);
				// Now add the results!
				StaticText(resultsview, Rect(0, 0, textViewBounds.width / 2, 30))
					.resize_(1)
					.align_(\right)
					.string_("% results found for query '%'.".format(results.size, widget.value));
				Button(resultsview, Rect(textViewBounds.width / 2, 0, 100, 30).insetBy(5))
					.resize_(1)
					.states_([["Clear"]])
					.action_({ searchField.valueAction_("") })
					.focus();
				results.do{|res, index|
					res.drawRow(resultsview, Rect(0, index*30 + 30, textViewBounds.width, 30),
						// Add an action that uses the gui itself:
						{ fSelectTreePath.(res.catpath, res.docname) }
						);
				};

			}{
				// Empty query string, go back to textView
				textView.visible = true;
				resultsview.visible = false;
			};

		});

	// Handle some "global" (for the Help gui) key actions
	helpguikeyacts = {|view, char, modifiers, unicode, keycode|
		if((modifiers & (262144 | 1048576)) != 0){ // cmd or control key is pressed
			unicode.switch(
				6, { // f for find
					searchField.focus;
				},
				8, // h for home
				{
					{
						listviews[0].valueAction_(listviews[0].items.find(["Help"]));
						scrollView.visibleOrigin_(0@0);
					}.defer(0.001)
				}
			);
		};
	};
	win.view.addAction(helpguikeyacts, \keyUpAction);

	win.onClose_{
		// This is done to prevent Cmd+W winclose from trying to do things in vanishing textviews!
		fHistoryDo = {};
	};

	win.front;
	listviews[0].focus;
	if(listviews[0].items.detect({ |item| item == "Help" }).notNil) {
		fSelectTreePath.([], "Help"); // Select the "Help" entry in the root
		selecteditem = "Help";
	}{
		selecteditem = listviews[0].items.first;
		fSelectTreePath.([], selecteditem);
	};
	
	// restore user's GUI scheme: 
	{ GUI.set(usersGUIscheme); }.defer(1);

	}

}
