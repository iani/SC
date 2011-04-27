UniqueWindow : UniqueObject {
	*mainKey { ^[\windows] }
	*removedMessage { ^\closed }

	init { | windowFunc |
		super.init(windowFunc ?? { Window(key.last.asString).front });
		object.onClose = {
			this.remove;
			object.releaseDependants;
			object = nil;	
		}
	}
	
	onClose { | func | this.onRemove(func) }	// synonym
	window { ^object } 					// synonym
	name { ^object.name }
	bounds { ^object.bounds }
	front { object.front }
	
	// Utilities: types of commonly used windows
	*listWindow { | key = 'list', bounds, getItemsAction, getIndexAction, notifier, messages, title, delay = 0.0 |
		var ulistwindow;
		ulistwindow = this.new(key, {
			var w, view, listview, searchview, items;
			w = Window(title ? key, bounds ?? { Rect(0, 0, 200, Window.screenBounds.height) });
			view = w.view;
			searchview = TextView(w, view.bounds.height = 20).font = Font("Helvetica", 12);
			searchview.keyDownAction = { | view, char, mod, unicode, key | 
				if (unicode == 13) {
					listview.doAction; // = listview.value;
					{ view.string = ""; }.defer(0.01);
				}{
					{	
						var string, item;
						string = view.string;
						item = items.select({ | i | ("^" ++ string).matchRegexp(i.key.asString) }).first;
						listview.value = items.indexOf(item) ? 0;
					}.defer(0.001); // must defer to get the latest string !!!
				}
			};
			listview = EZListView(w, view.bounds.insetBy(0, 12).top = 24);
			listview.widget.resize = 5;
			listview.widget.parent.resize = 5;
			listview.items = items = getItemsAction.value;
			w.addDependant({ | me |
				{ 	items;
					listview.items = items = getItemsAction.value;
					listview.value = getIndexAction.(items) ? 0;
				}.defer(delay);
			});
			w.toFrontAction = { searchview.focus };
			w;
		});
		messages.asArray do: { | m | 
			ulistwindow.addNotifier(notifier, m, { | me | me.window.changed; });
		};
		ulistwindow.front;
	}
}
