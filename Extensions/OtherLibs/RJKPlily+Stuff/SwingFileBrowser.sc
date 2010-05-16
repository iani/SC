
	SwingFileBrowser {
	
		classvar win, lists, <fileLists;
		classvar <scrollView, <compView, <listSizerView;
		classvar screenBounds, bounds;
		classvar makeView, setViewCurrent, setViewValueCurrent, setPathCurrent;
	
		classvar <>lViewWidth = 150;
		
		*new { | path |
			if (win.isNil) { 
				this.makeBrowser("open file");		
				fileLists = [ this.makeView(0, this.getPathContents) ];
			};
			win.front;
			this.setPathCurrent(path);
		}
		
		*getCurrentPath {
			var path = "";
			fileLists.do({ | l | path = path ++ l.item})
			^path;
		}
		
		*getPathContents{ | path|
			^(path ++ Platform.pathSeparator ++ "*").pathMatch.sort
		}
		
		*makeBrowser { | title|
			screenBounds = Window.screenBounds;
			bounds = Rect(128, 264, lViewWidth * 2 + 5, 564);
			bounds = bounds.center_(screenBounds.center);
			bounds = bounds.sect(screenBounds.insetBy(15));
			win = Window(title, bounds)
				.onClose_({win = nil; fileLists = nil; scrollView = nil; compView = nil; listSizerView = nil })
				.front; 
			
			listSizerView = UserView(win, Rect(5, 0, lViewWidth * 2, 529)).resize_(5);
			scrollView = ScrollView(win, Rect(5, 0, lViewWidth * 2, 529)).hasBorder_(true).resize_(5);
			compView = CompositeView(scrollView, Rect(0, 0, lViewWidth * 10,  scrollView.bounds.height - 25));
		
			listSizerView.drawFunc = { var target;
				target = scrollView.bounds.height - 25;
				if ( compView.bounds.height !=target ){
					compView.bounds = compView.bounds.height_(scrollView.bounds.height - 25)
				};
			};
			fileLists = [];
		}	
		
		*setViewCurrent { | view |
			var index, origin;
			index = fileLists.indexOf(view);
			fileLists[index+1..].do { | v |  v.remove;  };
			fileLists = fileLists[..index];
			origin = view.bounds.origin;
			origin = Point(origin.x -lViewWidth max: 0, origin.y);
			scrollView.visibleOrigin_(origin);
			scrollView.refresh;
			view.focus;
		}
			
		*setViewValueCurrent { | view | 
			var newView, newItems;
			var path, index;
			index = fileLists.indexOf(view);
			path  = view.items[view.value].copy;
			(index).reverseDo { | i |
				path = fileLists[i].items[ fileLists[i].value] ++ path
			};
			if ( (path.splitext[1] == nil) && (path.last == Platform.pathSeparator) ) {
				newItems = this.getPathContents(path).collect(_.asRelativePath(path) ); 
				if (newItems.size > 0) {
					newView = this.makeView(index + 1, newItems); 
					fileLists = fileLists[..index].add(newView);
					compView.bounds = compView.bounds.width_(max(425, fileLists.size * lViewWidth) );
					newView.focus;
					defer({scrollView.visibleOrigin_(view.bounds.origin)},0.01);
				};
				scrollView.refresh;
		 	} {
				win.close;
				Document.open(path);
		 	};
		 }
		 
		*setPathCurrent { | path |
			var dirs, last, lView;
			path = path ? "";
			path = path.asAbsolutePath.pathMatch[0];  // need this to guarantee trailing separator for folders
			if (path.size > 0) {
				this.setViewCurrent(fileLists[0]);
				dirs = path.split(Platform.pathSeparator);
				if (dirs.size > 2) {
					last = dirs.pop;
					dirs[1] = Platform.pathSeparator ++ dirs[1];
					dirs = dirs[1..].collect { | d | (d ++ "/").asSymbol };
					dirs.do { | name, i |
						lView = fileLists[i];
						lView.value = lView.items.collect(_.asSymbol).indexOf(name);
						this.setViewValueCurrent(lView);
						
					};
					if (last.size > 0) {  
						lView = fileLists[dirs.size];
						lView.value = lView.items.collect(_.asSymbol).indexOf(last.asSymbol);
					} {
						 this.setViewCurrent( fileLists[fileLists.size - 2 max: 0] );
//						 defer({scrollView.visibleOrigin_(fileLists[fileLists.size - 3 max: 0].bounds.origin)}, 0.1)
					}
				}		
			}
		}
		
		*makeView {| index, items  |
			var view, oldvalue, docstr, ext, file;
			view = ListView( compView, Rect((index * lViewWidth), 0, lViewWidth - 10, scrollView.bounds.height - 25 ))
				.items_(items ? [])
				.resize_(4)
				.action_({});
			view.keyDownAction_({ var search = "";
				{ | view, char, modifiers, unicode|
					var retval, i;
					retval = view;
					if (char.isAlpha.not) {	search = "" };
					case
						{ char.isAlpha }
							{	
								search = search ++ char.toUpper;
								i = items.detectIndex({|item| item.asString.toUpper >= search });
								if (i.notNil) { view.value = i};
							}
						{ unicode == 13 }
							{ this.setViewValueCurrent(view) }
						{unicode == 16rF700} 
							{ if (GUI.scheme.name == \CocoaGUI) { view.valueAction = view.value - 1} }
						{unicode == 16rF701} 
							{ if (GUI.scheme.name == \CocoaGUI) { view.valueAction = view.value + 1} }
						{unicode == 16rF702} // left arrow
							{  this.setViewCurrent( fileLists[index - 1 max: 0] ) }   
						{unicode == 16rF703 } // right arrow 
							{ this.setViewValueCurrent(view) }
						{ true }
							{ /*retval = nil*/ };
				};
			}.value);
				
			view.mouseDownAction_({ |lv| 
				if (lv.value == oldvalue) { 
					this.setViewValueCurrent(lv) 
				} {
					this.setViewCurrent(lv)
				};
				oldvalue = lv.value;
			
			});
			^view
		}
	}

	SwingSaveAs : SwingFileBrowser {
		classvar <>textFieldView, <>fileContents;
		*new { | path, string |
			fileContents = string;
			if (win.isNil) { 
				this.makeBrowser("Save as:");		
				fileLists = [ this.makeView(0, this.getPathContents) ];
			};
			win.front;
			path = (path ? "").asAbsolutePath;
			if (path.last != Platform.pathSeparator) {
				textFieldView.string = path.basename;
				this.setPathCurrent(path.dirname);
			} {
				this.setPathCurrent(path);
			}
		}
		
		*getPathContents{ | path|
			^(path ++ Platform.pathSeparator ++ "*")
				.pathMatch
				.select({ |p | p.splitext[1].isNil && (p.last == Platform.pathSeparator) })
		}
		
		*makeBrowser { | title |
			screenBounds = Window.screenBounds;
			bounds = Rect(128, 264, lViewWidth * 2 + 5, 564);
			bounds = bounds.center_(screenBounds.center);
			bounds = bounds.sect(screenBounds.insetBy(15));
			win = Window(title, bounds)
				.onClose_({win = nil; fileLists = nil; scrollView = nil; compView = nil; listSizerView = nil })
				.front; 
			textFieldView = TextField(win, Rect(25, 0, bounds.width - 50, 30)
							.insetBy(0, 5))
							.resize_(2)
							.string_("Untitled")
							.align_(\center)
							.action_({
								File((SwingSaveAs.getCurrentPath ++ textFieldView.string),"w")
									.putString(fileContents)
									.close
							});
			listSizerView = UserView(win, Rect(5, 35, lViewWidth * 2, 529)).resize_(5);
			scrollView = ScrollView.new(win, Rect(5, 35, lViewWidth * 2, 529)).hasBorder_(true).resize_(5);
			compView = CompositeView.new(scrollView, Rect(0, 0, lViewWidth * 10,  scrollView.bounds.height - 25));
	
			listSizerView.drawFunc = { var target;
				target = scrollView.bounds.height - 25;
				if ( compView.bounds.height !=target ){
					compView.bounds = compView.bounds.height_(scrollView.bounds.height - 25)
				};
			};
			fileLists = [];
		}	
		
//		*setViewValueCurrent { | view | 
//			var newView, newItems;
//			var path, index;
//			index = fileLists.indexOf(view);
//			path  = view.items[view.value].copy;
//			(index).reverseDo { | i |
//				path = fileLists[i].items[ fileLists[i].value] ++ path
//			};
//			if (path.last == Platform.pathSeparator) {
//				if (path.splitext[1] != ("app" ++ Platform.pathSeparator)) {
//					newItems = (path ++ Platform.pathSeparator ++ "*" ++ Platform.pathSeparator).pathMatch
//									.sort.collect(_.asRelativePath(path) ); 
//					if (newItems.size > 0) {
//						newView = this.makeView(index + 1, newItems); 
//						fileLists = fileLists[..index].add(newView);
//						compView.bounds = compView.bounds.width_(max(425, fileLists.size * lViewWidth) );
//						newView.focus;
//						defer({scrollView.visibleOrigin_(view.bounds.origin)},0.01);
//					}
//				};
//				scrollView.refresh;
//		 	};
//		 }
	}
