BMMatrixMenuGUI : BMAbstractGUI {

	var matrix;
	var inputSection, assignSection, outputSection, inputView, assignView, outputView;
	var assignButton, labelPlusButton, matrixButton, clearButton, buttonSection;
	var <matrixGUI;
	
	*new {|matrix, name, origin|
		^super.new.init(matrix, name ? matrix.name).makeWindow(origin ? (40@200));
	}
	
	init { |argmatrix, argname|
		matrix = argmatrix;
		name = argname;
		matrix.addDependant(this);
		matrix.takesControlsForInputs.if({
			matrix.inNames.do({|inName|
				BMAbstractController.allControls[inName].addDependant(this);
			});
		});
	}
	
	makeWindow { |origin|
		var x, y;
		x = origin.x;
		y = origin.y;
		
		window = SCWindow(name, Rect.new(x, y, 800, 300), false);
		window.view.decorator = FlowLayout(window.view.bounds, Point(10, 10), Point(10, 10));
		
		inputSection = SCVLayoutView(window, Rect(0, 0, 200, 300));
		SCStaticText.new(inputSection, Rect(0,0,180,20)).font_(Font("Helvetica-Bold", 14))
			.string = "Inputs";
		inputView = SCListView(inputSection, Rect(0, 0, 200, 250)).canReceiveDragHandler = false;
		inputView.items = matrix.inNames;
		inputView.action = {this.update};
		inputView.background = Color.white.alpha_(0.2);
		
		assignSection = SCVLayoutView(window, Rect(0, 0, 200, 300));
		labelPlusButton = SCHLayoutView(assignSection, Rect(0, 0, 200, 20));
		SCStaticText.new(labelPlusButton, Rect(0,0,180,20)).font_(Font("Helvetica-Bold", 14))
			.string = "Assignments";
		assignView = SCListView(assignSection, Rect(0, 0, 200, 250)).canReceiveDragHandler = true;
		assignView.receiveDragHandler = { 
			matrix.connect(inputView.item, SCView.currentDrag.asSymbol);
		};
		assignView.keyDownAction = { arg view,char,modifiers,unicode,keycode;
			if(unicode == 127, {
				matrix.disconnect(inputView.item, view.item);
			});
		};
		assignView.background = Color.white.alpha_(0.2);
		
		outputSection = SCVLayoutView(window, Rect(0, 0, 200, 300));
		labelPlusButton = SCHLayoutView(outputSection, Rect(0, 0, 200, 20));
		SCStaticText.new(labelPlusButton, Rect(0,0,80,20)).font_(Font("Helvetica-Bold", 14))
			.string = "Outputs";
		assignButton = RoundButton(labelPlusButton, Rect(0,0,110,20))
			.extrude_(false)
			.canFocus_(false)
			.canReceiveDragHandler = false;		
		
		assignButton.states = [["<", Color.black, Color.white.alpha_(0.8)]];
		assignButton.action = { matrix.connect(inputView.item, outputView.item);};
		outputView = SCListView(outputSection, Rect(0, 0, 200, 250)).canReceiveDragHandler = false;
		outputView.beginDragAction = {|view| view.dragLabel = view.item.asString; view.item };
		outputView.background = Color.white.alpha_(0.2);
		
		
		buttonSection = SCVLayoutView(window, Rect(0, 0, 150, 300));
		SCStaticText.new(buttonSection, Rect(0,0,80,20)).string_(" ");// placeholder
		
		clearButton = RoundButton(buttonSection, Rect(0,0,110,20))
			.extrude_(false)
			.canFocus_(false)
			.canReceiveDragHandler = false;		
		clearButton.states = [["Clear Matrix", Color.black, Color.white.alpha_(0.8)]];
		clearButton.action = { matrix.clear};
		
		SCStaticText.new(buttonSection, Rect(0,0,80,0)).string_(" ");// placeholder
		SCStaticText.new(buttonSection, Rect(0,0,80,0)).string_(" ");// placeholder
		
		matrixButton = RoundButton(buttonSection, Rect(0,0,110,20))
			.extrude_(false)
			.canFocus_(false)
			.canReceiveDragHandler = false;		
		matrixButton.states = [["View Matrix", Color.black, Color.white.alpha_(0.8)]];
		matrixButton.action = { if (matrixGUI.isNil) 
			   					{ matrixGUI = BMMatrixGUI(matrix, name);
			   		  			  matrixGUI.onClose_({ matrixGUI = nil })
			   					}
			   					{ matrixGUI.window.front }
			   			    };
		
		SCStaticText.new(buttonSection, Rect(0,0,80,110)).string_("Assign outputs to selected input. Cmd-drag or use button to assign, select and press delete to unassign.");
		this.update;
		window.onClose = { 
			matrix.removeDependant(this); 
			matrix.takesControlsForInputs.if({
				matrix.inNames.do({|inName|
					BMAbstractController.allControls[inName].removeDependant(this);
				});
			});
			onClose.value(this)
		};
		window.front;
	}
	
	update {
		var mappedTo;
		(matrix.takesControlsForInputs && BMOptions.allowMultipleControlMappings.not).if({
			mappedTo = BMAbstractController.allControls[inputView.item.asSymbol].mappedTo;
			if(mappedTo.notNil && (mappedTo !== matrix), {
				assignView.items = ["Mapped to" + matrix.name];
				assignView.enabled_(false);
			}, { assignView.enabled_(true).items = matrix.mappings[inputView.item].asArray; });
		}, { assignView.enabled_(true).items = matrix.mappings[inputView.item].asArray;});
		outputView.items = matrix.outNames.difference(assignView.items);
	}
}

BMMatrixGUI : BMAbstractGUI {

	var h = 700, v = 700, numIns = 10, numOuts = 10, dotSize = 10;
	var hinterval, vinterval, tabletView;
	var cellsize = 25, screenBounds; // maximum cellsize
	var hoffset = 80, voffset = 100;
	var color, ringColor;
	var lastx, lasty, on = false;
	var ins, outs;
	var matrix;
	var xpos = 1, ypos = 1; // draw lines initially
	var linex, liney, xdist, ydist;
	var font;
	
	*new {|matrix, name|
		^super.new.init(matrix, name ? matrix.name).makeWindow;
	}
	
	init {|argmatrix, argname|
		matrix = argmatrix;
		name = argname;
		matrix.addDependant(this);
		matrix.takesControlsForInputs.if({
			matrix.inNames.do({|inName|
				BMAbstractController.allControls[inName].addDependant(this);
			});
		});
		font = Font("Andale Mono", 12);
	}
	
	makeWindow {	
		
		ins = matrix.inNames;
		
		numIns = ins.size;
		
		outs = matrix.outNames;
		numOuts = outs.size;
		
		hoffset = max(hoffset, ins.collect({|lbl| lbl.asString.bounds(font).width}).maxItem + 15);
		voffset = max(voffset, outs.collect({|lbl| lbl.asString.bounds(font).width}).maxItem + 15);
		
		// scale size to available monitor size
		screenBounds = SCWindow.screenBounds;
		cellsize = cellsize min: (screenBounds.width - 40 - hoffset / numOuts);
		cellsize = cellsize min: (screenBounds.height - 40 - voffset / numIns);
		dotSize = cellsize * 0.33 min: 15; // maximum dot size
		h = numOuts * cellsize + hoffset;
		v = numIns * cellsize + voffset;
		
		color = Color.blue.alpha_(0.5).set;
		ringColor = Color.black;
		
		window = SCWindow(name, Rect(40, 40, h, v), false);
		window.alpha = 0.98;

		window.view.background = Color.new255(140, 38, 255);
		hinterval = window.bounds.width - hoffset / (numOuts + 1);
		vinterval = window.bounds.height - voffset / (numIns + 1);
		tabletView = SCTabletView(window, window.view.bounds);
		tabletView.background = Color.clear;
		tabletView.mouseDownAction = { arg view,inx,iny;
			var x, y;
			x = outs[(inx - hoffset/ hinterval).round.clip(1, numOuts) - 1];
			y = ins[(iny - voffset/ vinterval).round.clip(1, numIns) - 1];
			if(matrix.mappings[y].indexOf(x).isNil, {matrix.connect(y, x); on = true;},
				{matrix.disconnect(y, x); on = false});
			lastx = x; lasty = y;
		};
		// dragging
		tabletView.action = { arg  view,inx,iny;
			var x, y;
			x = outs[(inx - hoffset/ hinterval).round.clip(1, numOuts) - 1];
			y = ins[(iny - voffset/ vinterval).round.clip(1, numIns) - 1];
			if((x != lastx) || (y != lasty), {
				linex = outs[(inx - hoffset/ hinterval).round.clip(1, numOuts) - 1];
				liney = ins[(iny - voffset/ vinterval).round.clip(1, numIns) - 1];
				if(on, {
						if(matrix.mappings[y].indexOf(x).isNil, {
							matrix.connect(y, x);
						});},
					{
						if(matrix.mappings[y].indexOf(x).notNil, {
							matrix.disconnect(y, x);
						});
				});
				window.refresh;
				
			});
			lastx = x; lasty = y;
		};
		
		// draw line for easy view
		tabletView.mouseOverAction = { arg view,inx,iny;
			xpos = (inx - hoffset/ hinterval);
			ypos = (iny - voffset/ vinterval);
			linex = outs[xpos.round.clip(1, numOuts) - 1];
			liney = ins[ypos.round.clip(1, numIns) - 1];
			xdist = abs(xpos - xpos.round.clip(1, numOuts));
			ydist = abs(ypos - ypos.round.clip(1, numIns));
			window.refresh;
		};
		
		window.acceptsMouseOver = true;
		window.front;
		window.drawHook = {
		
			Pen.width = 2;
			
			Pen.use {
				// border lines
		
				Pen.line(hoffset@voffset, window.bounds.width@voffset);
				Pen.line(hoffset@voffset, hoffset@window.bounds.height);
				
				color.set;
				numIns.do { |i|
					if(ins[i] == liney && (ypos > 0), {
						Pen.stroke; 
						Color.white.alpha_(1-ydist).set;
					});
					Pen.line((1 + hoffset)@(vinterval + voffset + (i * vinterval)), 
						(window.bounds.width + hoffset)@(vinterval + voffset + 
						(i * vinterval)));
					if(ins[i] == liney, {Pen.stroke; color.set;});
				};
				numOuts.do { |i|	 
					if(outs[i] == linex && (xpos > 0), {
						Pen.stroke; 
						Color.white.alpha_(1-xdist).set;
					});
					Pen.line((hinterval + hoffset + (i * hinterval))@(1 + voffset), (hinterval + 
						hoffset + (i * hinterval))@(window.bounds.height + voffset)); 
					if(outs[i] == linex, {Pen.stroke; color.set;});
				};
				Pen.stroke;
				matrix.matrixArray.do({ arg row, y;
					row.do({ arg item, x;
						var crosspoint, rect;
						item.notNil.if({
							rect = Rect.aboutPoint((hinterval  + hoffset + (x * hinterval))
								@(vinterval + voffset + (y * vinterval)), dotSize, dotSize);
							color.set;
							Pen.fillOval(rect);
		
							ringColor.set;
							Pen.strokeOval(rect);
						});
					})
				});
				
			};
			outs.do({|item, i|
				
				Pen.use({
					Pen.translate((hoffset + hinterval + (hinterval * i)), (voffset / 2));
					Pen.rotate(0.5pi);
					item.asString.drawCenteredIn(Rect.aboutPoint(0@0, 40, 10), 
						font,
						Color.black
					);
				});
			});
			
			ins.do({|item, i|
				var inColor, mappedTo;

				(matrix.takesControlsForInputs && BMOptions.allowMultipleControlMappings.not).if({
					mappedTo = BMAbstractController.allControls[item].mappedTo;
					if(mappedTo.notNil && (mappedTo !== matrix), {
						inColor = Color.grey;
					}, { inColor = Color.black;});
				}, { inColor = Color.black;});

			
				Pen.use({
					Pen.translate((hoffset / 2), (voffset + vinterval + (vinterval * i)));
					item.asString.drawCenteredIn(Rect.aboutPoint(0@0, 40, 10), 
						font,
						inColor
					);
				});
			});

		};

		window.onClose = {  
			matrix.removeDependant(this); 
			matrix.takesControlsForInputs.if({
				matrix.inNames.do({|inName|
					BMAbstractController.allControls[inName].removeDependant(this);
				});
			});
			onClose.value(this)
		};
		window.refresh;
	}
	
	update { |changed, what| window.refresh; }
}
