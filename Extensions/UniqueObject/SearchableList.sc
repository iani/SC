/*
Experimental: based on ListWondow, but can be built in any window. 

NOT COMPLETE YET
*/
SearchableList {
	var listview, getItemsAction, getIndexAction, items, item, string, notifiers, delay;
	*new { | view, getItemsAction, notifier, messages, getIndexAction, bounds, delay = 0.01 |
		^super.new.init(view, bounds, getItemsAction, getIndexAction, notifier, messages, delay)
	}
		
	init { | view, bounds, argGetItemsAction, argGetIndexAction, notifier, messages, argDelay |
		var screenBounds, centerX, centerY, itemsHeight;
		var searchview, items;
		bounds = bounds ?? { view.bounds.moveTo(0, 0); };
		getItemsAction = argGetItemsAction ?? { { ["---"->{ }] } };
		getIndexAction = argGetIndexAction;
		delay = argDelay;
		searchview = TextView(view, bounds.copy.height = 20).font = Font("Helvetica", 12);
		searchview.focusColor = Color.red;
		searchview.keyDownAction = { | view, char, mod, unicode, key |
			switch (unicode, 
			13, {
				listview.doAction;
				{ view.string = ""; }.defer(0.01);
			},
			16rF700, { listview.value = listview.value - 1; },
			16rF701, { listview.value = listview.value + 1; },
			16rF702, { listview.value = 0; },
			16rF703, { listview.value = listview.items.size - 1; },
			{
				{	
					string = view.string;
					item = items.select({ | i | ("^" ++ string).matchRegexp(i.key.asString) }).first;
					listview.value = items.indexOf(item) ? 0;
				}.defer(0.001); // must defer to get the latest string !!!
			})
		};
		listview = EZListView(view, bounds.insetBy(0, 12).top = 24);
		listview.widget.resize = 5;
		searchview.onClose = { this.closed };
		messages.asArray do: { | m | 
				this.addNotifier(notifier, m, { view.changed; });
		};
		this.getItems;
	}
	
	getItems {
		{
		listview.items = items = getItemsAction.value;	
		listview.value = getIndexAction.(items) ? 0;
		}.defer(delay);
	}
	
	addNotifier { | notifier, message |
		notifiers = notifiers add: [notifier, message];
		NotificationCenter.register(notifier, message, this, { this.getItems });
	}
	closed {
		notifiers do: { | n | 
			NotificationCenter.unregister(n[0], n[1], this)
		}
	}
}
