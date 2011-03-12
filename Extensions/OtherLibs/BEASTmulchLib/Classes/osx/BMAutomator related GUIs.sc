

// probably this should be made into a subclass with alternate makeWindows for timerefs
// which are not soundfiles

BMControllerAutomatorGUI : BMAbstractGUI {
	var ca, envView;
	var path, sf, durInv, sfView, scrollView, selectView, backView, timesView;
	var activeSequence, activeSnapshot;
	var sequenceLevels;
	var dependees;
	var seqs, snapshots, names, times, connections;
	var addSS, remSS;
	var showOnlySelected = false;
	var time, curSSTime, refTime;
	var zoomSlider;
	
	*new {|ca, name, origin|
		^super.new.init(ca, name ? "Soundfile / Controller Snapshots").makeWindow(origin ? (200@200));
	}
	
	init {|argCa, argName|
		ca = argCa;
		name = argName;
		dependees = [ca.addDependant(this), ca.timeReference.addDependant(this)];
		sequenceLevels = IdentityDictionary.new;
		ca.sequences.do({|sq, i| sequenceLevels[sq] = (0.1 * (i + 1))%1.0});
	}
	
	makeWindow { |origin|
		var width = 1008;
		path = ca.timeReference.path; // How best to do this?
		sf = SoundFile.new;
		path.notNil.if({sf.openRead(path);});
		
		durInv = sf.duration.reciprocal;
		
		window = SCWindow.new(name, Rect(origin.x, origin.y, width, 400), false);
		window.view.decorator = FlowLayout(window.view.bounds);
		
		window.view.keyDownAction = { arg view,char,modifiers,unicode,keycode;
			if(unicode == 32, {ca.timeReference.togglePlay});
			if(unicode == 13, {ca.timeReference.stop});
		};
		
		scrollView = SCScrollView(window, Rect(0, 0, width - 8, 334));
		scrollView.hasBorder = true;
		scrollView.resize = 2;
		scrollView.background = Color.black;
		
		sfView = SCSoundFileView.new(scrollView, Rect(0,20, width - 10, 300));
		sfView.background = HiliteGradient(Color.blue, Color.cyan, steps: 256);
		this.setWaveColors;
		sfView.timeCursorOn = true;
		sfView.timeCursorColor = Color.red;
		sfView.canFocus_(false);
		
		sfView.mouseDownAction = {|view, x|
			var newTime;
			newTime = (x / view.bounds.width) * sf.duration;
			ca.timeReference.setTime(newTime);
		};
		
		sfView.mouseMoveAction = sfView.mouseDownAction;
		sfView.gridOn = false;
		
		scrollView.canFocus_(false);
		
		backView = SCCompositeView(scrollView, Rect(0, 20,  width - 10, sfView.bounds.height)).background_(Color.clear);
		
		path.notNil.if({sfView.soundfile = sf;});
		
		sfView.elasticMode = 1;
		
		this.makeTimesView;
		
		window.onClose = {
			sf.close; 
			dependees.do({|dee| dee.removeDependant(this)});
			onClose.value(this);	
		};
		
		window.view.decorator.shift(0, 5);
		SCStaticText(window, Rect(0, 0, 5, 10)).string_("-").font_(Font("Helvetica-Bold", 12));
		zoomSlider = SmoothSlider(window, Rect(0, 5, 100, 10)).action_({|view| 
			var width;
			//width = scrollView.bounds.width - 2 + (sf.duration * 160 * ([0.001, 1.001, \exp].asSpec.map(view.value) - 0.001));
			// temp fix for userview with large width bug
			width = scrollView.bounds.width - 2 + ((32768 - scrollView.bounds.width) * ([0.001, 1.001, \exp].asSpec.map(view.value) - 0.001));
			width = width.round;
			sfView.bounds = Rect(0,20, width, 300);
			envView.bounds = Rect(0,0, width, sfView.bounds.height);
			backView.bounds = Rect(0, 20, width, sfView.bounds.height); 
			timesView.bounds = Rect(0, 0, width, 20);
			sfView.selections.size.do({|i| 
				sfView.setSelectionSize(i, sf.numFrames / sfView.bounds.width);
			});
			this.drawSelections;
			this.resetPoints;
			scrollView.refresh;
		}).knobSize_(1).canFocus_(false).hilightColor_(Color.blue).enabled_(false);
		SCStaticText(window, Rect(0, 0, 10, 10)).string_("+").font_(Font("Helvetica-Bold", 10));
		window.view.decorator.shift(0, -5);

		window.front;
		
		if(path.notNil, {sfView.read(block: 256); zoomSlider.enabled = true;});
		this.makeEnvView;

		RoundButton(window, 120@20).extrude_(false)
			.canFocus_(false)
			.font_(Font("Helvetica-Bold", 10))
			.states_([["Show Only Selected"], ["Show Only Selected", Color.black, Color.grey]])
			.action_({|view|
				showOnlySelected = view.value.booleanValue;
				this.makeEnvView;
			});
		RoundButton(window, 120@20)
			.extrude_(false)
			.canFocus_(false)
			.font_(Font("Helvetica-Bold", 10))
			.states_([["Add Sequence"]])
			.action_({
				BMSnapShotSeqConfigGUI(window).onClose = {|results|
					var newname;
					newname = UniqueID.next.asSymbol;
					ca.addSequence(newname, time, results);
					sequenceLevels[ca.sequences[newname]] = 0.1;
					this.makeEnvView;
				}
			});		
		addSS = RoundButton(window, 120@20)
			.extrude_(false)
			.canFocus_(false)
			.font_(Font("Helvetica-Bold", 10))
			.states_([["Add Snapshot"]])
			.action_({
				var newname;
				if(activeSequence.notNil, {
					newname = UniqueID.next.asSymbol;
					ca.addSnapShot(activeSequence.name, time, newname);
					activeSnapshot = activeSequence.snapshotsDict[newname];
					this.makeEnvView;
				});
			});
		remSS = RoundButton(window, 120@20)
			.extrude_(false)
			.canFocus_(false)
			.font_(Font("Helvetica-Bold", 10))
			.states_([["Remove Snapshot"]])
			.action_({
				if(activeSnapshot.notNil && activeSnapshot.isKnown, {
					if(activeSequence.snapshots.size > 2, {
						activeSequence.removeSnapShot(activeSnapshot.name);
						activeSnapshot = activeSequence.snapshots.last;
						this.makeEnvView;
					}, {
						ca.removeSequence(activeSequence);
						activeSequence = nil;
						this.makeEnvView;
						this.drawSelections;
					});
				});
			});
		
		window.view.decorator.nextLine.nextLine;
		window.view.decorator.shift(10, 0);
		refTime = SCStaticText(window, Rect(0, 0, 200, 25))
			.string_("Source Time:") // initialise
			.font_(Font("Helvetica-Bold", 16));
		time = BMTimeReferences.currentTime(ca.timeReference);
		sfView.timeCursorPosition = time * sf.sampleRate;
		refTime.string_("Source Time:" + time.getTimeString);
					
		window.view.decorator.shift(0, 4);
		curSSTime = SCStaticText(window, Rect(0, 0, 300, 20))
			.string_("Selected Snapshot Time:") // initialise
			.font_(Font("Helvetica-Bold", 12));
		
		if(activeSnapshot.notNil, {
			curSSTime.string_("Selected Snapshot Time:" + activeSnapshot.time.asTimeString);
		});

		zoomSlider.doAction; // hack to make envView take mouseDown initially
	}
	
	setWaveColors {
		sfView.waveColors_(
			Array.fill(sf.numChannels, {|i| 
				Color.grey(0.2, 0.6).blend(Color.grey(0.3, 0.6), 1 / (sf.numChannels - 1) * i)
			})
		); 
	}
	
	makeTimesView {
		
		timesView.notNil.if({timesView.remove});
		timesView = SCUserView(scrollView, Rect(0, 0, sfView.bounds.width, 20));
		timesView.background = Color.clear;
		timesView.canFocus_(false);
		
		timesView.drawFunc = {
			var tenSecs, thirtySecs, bounds;
			bounds = timesView.bounds;
			Pen.addRect(Rect(0, 0, bounds.width, 20));
			Pen.fillColor = Color.new255(0, 0, 238);
			Pen.fill;

			tenSecs = timesView.bounds.width * durInv * 10;
			Pen.beginPath;
			Pen.strokeColor = Color.new255(125, 125, 255).alpha_(0.8);
			(sf.duration / 10).floor.do({|i|
				var x;
				if((i + 1)%3 == 0, {
					Pen.width = 2;
					Pen.lineDash_(FloatArray[]);
				}, {
					Pen.width = 1;
					Pen.lineDash_(FloatArray[3,3]);
				});
				x = (i + 1) * tenSecs;
				Pen.line(x@20, x@0);
				Pen.stroke;
			});
			Pen.width = 1;
			Pen.lineDash_(FloatArray[]);
			thirtySecs = bounds.width * durInv * 30;
			(sf.duration / 30).floor.do({|i|
				((i + 1) * 30).asTimeString.drawLeftJustIn(
					Rect((i+1) * thirtySecs + 1, 0, 50, 20),
					Font("Helvetica-Bold", 11), 
					Color.black
				); 
			});
			Pen.strokeColor = Color.black;
			Pen.lineDash_(FloatArray[3,3]);
			Pen.line(0@20, bounds.width@20);
			Pen.stroke;
			Pen.lineDash_(FloatArray[]);
		};
		
		timesView.mouseDownAction = sfView.mouseDownAction;
		timesView.mouseMoveAction = sfView.mouseDownAction;
	}
	
	makeEnvView {
		envView.notNil.if({envView.remove});
		
		
		envView = SCEnvelopeView(backView, Rect(0, 0, sfView.bounds.width, sfView.bounds.height))
			.thumbWidth_(19)
			.thumbHeight_(19)
			.drawLines_(true)
			.drawRects_(true)
			.selectionColor_(Color.grey)
			.strokeColor_(Color.white)
			.canFocus_(false)
			.background_(Color.clear);
			
		currentEnvironment[\envV] = envView;
			
		(ca.sequences.size > 0).if({
		if(activeSequence.isNil, {activeSequence = ca.sequences.values[0]});
		if(activeSnapshot.isNil, {activeSnapshot = activeSequence.snapshots[0]});
		
		this.resetPoints;
		this.setFillColors;
		
		this.drawSelections;
		envView.canFocus_(false);
	
		envView.mouseMoveAction = {|view|
			var time, ss, index;
			index = view.index;
			time = view.value[0][index];
			
			time.notNil.if({
				curSSTime.string_("Selected Snapshot Time:" + (sf.duration * time).asTimeString); 
				ss = snapshots[index];
				sfView.setEditableSelectionStart(view.index, true);
				sfView.setEditableSelectionSize(view.index, true);
				sfView.setSelection(index, [sf.numFrames * time, sf.numFrames / sfView.bounds.width]); 
				sfView.setSelectionColor(index, Color.white);

				sfView.setEditableSelectionStart(index, false);
				sfView.setEditableSelectionSize(index, false);
				
				// match levels
				sequenceLevels[seqs[index]] = envView.value[1][index];
				envView.value_([view.value[0], seqs.collect({|seq| sequenceLevels[seq] })]); 
				
				this.drawSelections;
			});
		};

		envView.mouseUpAction = {|view|
			var ss, seq, index, next, prev, selected;
			index = view.index;
			//postf("index: %\n", index);
			(index >= 0).if({
				//postf("ss(mouseUp): %\n", snapshots);
				selected = snapshots[index];
				snapshots[index].time = view.value[0][index] * sf.duration;
				
//				// correct for crossovers
//				next = snapshots[index + 1];
//				if(next.notNil && {snapshots[index].time > next.time}, {
//					envView.selectIndex(index + 1);
//					envView.refresh;
//				});
//				prev = snapshots[index - 1];
//				if(prev.notNil && {snapshots[index].time < prev.time}, {
//					envView.selectIndex(index - 1);
//					envView.refresh;
//				});
				
				// match levels
				sequenceLevels[seqs[index]] = envView.value[1][index];

				this.resetPoints;
				this.drawSelections;
				
				this.setFillColors;
			});
		};
		envView.mouseDownAction = {|view, x, y, modifiers, buttonNumber, clickCount|
			var newTime, clickedOnNode;
			// deselects on click in midst
			if(clickCount < 2, {
				if(view.index >=0, {
					activeSequence = seqs[view.index];
					activeSnapshot = snapshots[view.index];
					clickedOnNode = true;
				}, {
					clickedOnNode = false;
				});
				this.setFillColors;
				this.drawConnections;
				this.drawSelections;
				
				if(clickedOnNode, {
					curSSTime.string_("Selected Snapshot Time:" + activeSnapshot.time.asTimeString);
				}, {
					curSSTime.string_("Selected Snapshot Time:");
					// update time cursor
					newTime = (x / view.bounds.width) * sf.duration;
					ca.timeReference.setTime(newTime);
				});
			}, {
				if(activeSnapshot.notNil and: { activeSnapshot.isKnown}, {
					BMSnapShotSliders(activeSnapshot, window);
				})
			})

		};
	
		}, {
			envView.mouseDownAction = {|view, x, y, modifiers, buttonNumber, clickCount|
				var newTime;
				curSSTime.string_("Selected Snapshot Time:");
				// update time cursor
				newTime = (x / view.bounds.width) * sf.duration;
				ca.timeReference.setTime(newTime);
			};
			
			envView.mouseMoveAction = envView.mouseDownAction;
		});
	}

	
	setFillColors {
		var color;
		snapshots.do({|ss, i|
			color =  if(ss === activeSnapshot, {Color.grey.alpha_(0.9)}, {
				if(seqs[i] === activeSequence, {Color.blue.alpha_(0.9)}, {Color.black.alpha_(0.6)})
			});
			envView.setFillColor(i, color);
		});
	}
	
	resetPoints {
		seqs = List.new;
		snapshots = List.new;
		names = List.new;
		times = Array.new;
		showOnlySelected.not.if({
			ca.sequences.do({|seq|
				seq.snapshots.do({|ss|
					var sstime;
					sstime = ss.time;
					times = times.add(sstime / sf.duration);
					names.add(ss.name.asString + sstime.asTimeString(0.01));
					seqs.add(seq); // for ordered lookup
					snapshots.add(ss);
				});
			});
		}, {
		
			activeSequence.snapshots.do({|ss|
				var time;
				time = ss.time;
				times = times.add(time / sf.duration);
				names.add(ss.name.asString + ss.time.asTimeString(0.01));
				seqs.add(activeSequence); // for ordered lookup
				snapshots.add(ss);
			});
		});
		
		// values
		envView.value_([times, seqs.collect({|seq| sequenceLevels[seq] })]); 
		
		this.drawConnections;
		
		snapshots.do({arg ss, i;
			envView.setString(i, ss.isKnown.if({""}, {"?"}));
		});
	}
	
	drawConnections {

		seqs.doAdjacentPairs({|a,b, i| if(a === b, {
			envView.connect(i, [i +1])
		}, {envView.connect(i, [])})});
	}
	
	// we use SCSoundFileView Selections for the snapshot time cursors
	drawSelections {
	
		var seltime;
		
		if(sfView.numFrames.notNil, {this.clearSelections;});
		snapshots.do({|ss, index|
			if(seqs[index] === activeSequence, {
				seltime = envView.value[0][index];
				sfView.setEditableSelectionStart(index, true);
				sfView.setEditableSelectionSize(index, true);
				sfView.setSelection(index, [sf.numFrames * seltime, 
					sf.numFrames / sfView.bounds.width * 2]); 
				sfView.setSelectionColor(index, Color.white);
				sfView.setEditableSelectionStart(index, false);
				sfView.setEditableSelectionSize(index, false);
			});
		});
		sfView.refresh;
	
	}
	
	clearSelections {
		64.do({|i| sfView.selectNone(i)});
	}
	
	update { arg changed, what ...args;
		var cursorLoc;
		switch(what,
			
			\time, {
				{
					time = BMTimeReferences.currentTime(ca.timeReference);
					sfView.timeCursorPosition = time * sf.sampleRate;
					refTime.string_("Source Time:" + time.asTimeString);
					cursorLoc = time * durInv * sfView.bounds.width;
					// scroll to see cursor
					if(args[1] != 0, { // not paused or stopped
						if(cursorLoc > (scrollView.visibleOrigin.x + 
								scrollView.bounds.width - 2), {
							scrollView.visibleOrigin = cursorLoc@0;
						}, {
							if(cursorLoc < scrollView.visibleOrigin.x, {
								scrollView.visibleOrigin = 
									(cursorLoc - scrollView.bounds.width - 2)@0;
							});
						});
					});
				}.defer;
			},
			\stop, {
				{sfView.timeCursorPosition = 0;}.defer;
			},
			\base, {
				path = ca.timeReference.path; // How best to do this?
				path.notNil.if({
					{
					zoomSlider.value = 0;
					sf.close;
					sf.openRead(path);
					durInv = sf.duration.reciprocal;
					sfView.soundfile = sf;
					//sfView.read(block: 256);
					this.setWaveColors;
					sfView.refresh;
					this.makeTimesView;
					sfView.read(block: 256);
					ca.sequences.do({|sq, i| sequenceLevels[sq] = (0.1 * (i + 1))%1.0});
					this.makeEnvView;
					zoomSlider.enabled = true;
					//zoomSlider.valueAction = 0; 
					zoomSlider.doAction;
					}.defer;
				}, {zoomSlider.value = 0; zoomSlider.enabled = false;});
				
			}

		)
	
	}
}

//Runs as a sheet
BMSnapShotSliders : BMAbstractGUI {
	var snapshot, sliders;
	
	*new {|snapshot, parent|
		^super.new.init(snapshot)
			.makeWindow(parent);
	}
	
	init {|argss|
		snapshot = argss;
		snapshot.addDependant(this);
	}
	
	makeWindow {|parent|
		var numSliders, font, labelWidth;
		var maxVisSliders = 10, widthOffset = 0;
		font = Font("Helvetica-Bold", 10);
		numSliders = snapshot.values.size;
		if(numSliders >= maxVisSliders, {widthOffset = 10});
		window = SCModalSheet.new(parent, 
			Rect(0, 0, 652, (24 * min(numSliders, maxVisSliders)) + 28), // 508
			scroll: numSliders >= maxVisSliders); 
		window.view.decorator = FlowLayout(window.view.bounds);
		window.view.background = Color.rand.alpha_(0.3);
		sliders = IdentityDictionary.new;
		labelWidth = snapshot.controlNames.collect({|name| 
			name.asString.bounds(font).width
		}).maxItem;
		snapshot.controlNames.asArray.sort({|a, b| 
			b.asString.naturalCompare(a.asString) >= 0
		}).do({|label, i|
			var initVal, control, displaySpec, unitWidth = 20;
			control = BMAbstractController.allControls[label];
			displaySpec = control.displaySpec;
			initVal = displaySpec.map(snapshot.values[label]);
			//postf("init: %\n", initVal);
			sliders[label] = EZSlider.new(window, (640 - widthOffset)@20, label.asString, displaySpec,
				{|ez| 
					// convert back to 0..1
					snapshot.setValue(label, displaySpec.unmap(ez.value));
				}, initVal, layout: \horz, labelWidth: labelWidth, unitWidth: unitWidth
			);
			sliders[label].font = font;
		});
		window.view.decorator.nextLine;
		SCStaticText(window, Rect(0, 0, window.bounds.width - 132 - widthOffset, 20))
			.font_(font)
			.align_(\right)
			.string_("Adjust snapshot levels");
		RoundButton(window, 120@20)
			.extrude_(false)
			.canFocus_(false)
			.font_(font)
			.states_([["OK"]])
			.action_({
				window.close;
			});	
		window.onClose = { snapshot.removeDependant(this); onClose.value };
	}
	
}

// runs as a sheet
BMSnapShotSeqConfigGUI : BMAbstractGUI {
	
	*new {|parent|
		^super.new.makeWindow(parent);
	}
	
	makeWindow {|parent|
		var allControls, numControls, font, toggleWidth, numColumns, numRows;
		var results;
		results = IdentitySet.new;
		allControls = BMAbstractController.allControls;
		numControls = allControls.size;
		if(numControls == 0, {
			"No Controls to select".warn;
			^nil;
		});
		font = Font("Helvetica-Bold", 10);
		toggleWidth = allControls.keys.collect({|name| 
			name.asString.bounds(font).width
		}).maxItem + 8;
		numColumns = (parent.bounds.width - 4 / (toggleWidth + 4)).floor;
		numColumns = min(numColumns, numControls);
		numRows = (numControls / numColumns).ceil;
		window = SCModalSheet.new(parent, 
			Rect(300, 300, (numColumns * (toggleWidth + 4)) + 4, 24 * numRows + 28)); // 508
		window.view.decorator = FlowLayout(window.view.bounds);
		window.view.background = Color.rand.alpha_(0.3);
		allControls.keys.asArray.sort({|a, b| 
			b.asString.naturalCompare(a.asString) >= 0
		}).do({|label, i|
			var control;
			control = allControls[label];
			RoundButton(window, Rect(0, 0, toggleWidth, 20))
				.states_([[label.asString, Color.black, Color.black.alpha_(0.1)], [label.asString, Color.black, Color.white.alpha_(0.5)]])
				.font_(font)
				.radius_(2)
				.canFocus_(false)
				.extrude_(false)
				.action_({|v| v.value.booleanValue.if({results.add(control.name)}, {results.remove(control.name)})});
		});
		window.view.decorator.nextLine;
		SCStaticText(window, Rect(0, 0, window.bounds.width - 132, 20))
			.font_(font)
			.align_(\right)
			.string_("Select Controls for Sequence");
		RoundButton(window, 120@20)
			.extrude_(false)
			.canFocus_(false)
			.font_(font)
			.states_([["OK"]])
			.action_({
				window.close;
			});		
		window.onClose = { onClose.value(results.asArray.sort) };
	}
	

}