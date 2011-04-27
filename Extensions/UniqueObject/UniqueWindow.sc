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
			var w, listview;
			w = Window(title ? key, bounds ?? { Rect(0, 0, 250, 400) });
			listview = EZListView(w, w.view.bounds);
			listview.widget.resize = 5;
			listview.widget.parent.resize = 5;
			listview.items = getItemsAction.value;
			w.addDependant({ | me |
				{ 
					listview.items = getItemsAction.value;
//					listview.value = getIndexAction.value;
				}.defer(delay);
			});
			w;
		});
		messages.asArray do: { | m | 
			ulistwindow.addNotifier(notifier, m, { | me |
				me.window.changed;
			});
		};
		ulistwindow.front;
	}
}
