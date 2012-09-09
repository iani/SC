/* IZ Sat 01 September 2012  1:05 AM EEST
FIrst tests with AppModel2.
*/

//:Numeric values: NumberBox, Knob, Slider, with a frequency-type control spec.
// All three views share the same value. The spec is given in either knob or slider widget  
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.numberBox(\number).view,
		app.knob(\number, \freq).view,
		app.slider(\number).view.orientation_(\horizontal)
	)
})

//:Button accessing its related value
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.numberBox(\number).view,
		app.knob(\number, \freq).view,
		app.button(\number).action_({ | widget |
			widget.value.adapter.value_(widget, 20 rrand: 20000) 
		}).view.states_([["random number between 20 and 20000"]]),
		app.button(\number).action_({ | widget |
			// standardizedValue refers to values in the "standard" range between 0 and 1.
			widget.value.adapter.standardizedValue_(widget, [0, 0.25, 0.5, 0.75, 1].choose)
		}).view.states_([["choose randomly between 5 values"]])
	)
})

//:StaticText, TextField
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.staticText(\text).view,
		app.textField(\text).do({ | widget | 
			widget.value.adapter.string_(widget, "Edit my TextField + press return to update");
		}).view,
	)
})

//:TextView: Needs button to update
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.staticText(\text).view,
		app.textView(\text).do({ | widget | 
			widget.value.adapter.string_(widget, "Edit my TextView + press button to update");
		}).view,
		app.button(\text).getText.view.states_([["press me to update edited text"]]),
	)
})

//:Customizing update action of a widget: Make number display size of edited string
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.staticText(\text).view,
		app.textView(\text).do({ | widget | 
			widget.value.adapter.string_(widget, "Edit my TextView + press button to update");
		}).view,
		app.button(\text).getText.view.states_([["press me to update edited text"]]),
		HLayout(
			[StaticText().string_("Size of string in TextView:"), s: 4],
			app.numberBox(\text).updateAction(\text, { | what, me | 
				me.view.value = me.value.adapter.string.size; 
			}).view
		)
	)
})

//:Lists: PopUpMenu, ListView, and listItem accessing the chosen item in a list
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.popUpMenu(\list).items_(["alpha", "beta", "gamma"]).view,
		app.listView(\list).view,
		app.textField(\list).listItem.view
	)
})

//:Sublists: Lists accessing the lists inside a nested list
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.popUpMenu(\list).items_([["alpha", 1], ["beta", 2], ["gamma", 3, 4, 5]]).view,
		app.listView(\list).view,
		app.listView(\sublist).sublistOf(\list).view,
		app.textField(\sublist).listItem.view
	)
})

//:NamedLists: Lists of Lists with names. 
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.popUpMenu(\list, { | me | me.value.adapter.items collect: _.name}).items_([
			NamedList("alpha", [1, "more", "items"]),
			NamedList("beta", ["even", "more", "items", "added"]), 
			NamedList("gamma", ["to", "choose", "from", "et", "cetera"])
		]).view,
		app.listView(\list, { | me | 
			me.value.adapter.items collect: { | i | format("% : % elements", i.name, i.size) };
		}).view,
		app.listView(\sublist).sublistOf(\list).view,
		app.textField(\sublist).listItem.view
	)
})

//:General tests for numeric views, text views and lists.
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.numberBox(\number).view,
		app.button(\number, { | me | me.value.adapter.value_(me, 20 rrand: 20000) })
			.view.states_([["randomize"]]),
		app.knob(\number, \freq).view,
		app.slider(\number).view.orientation_(\horizontal),
		app.listView(\list).do({ | me | me.value.adapter.items_(me, ["a", "b", "c"]) })
			.view,
		app.popUpMenu(\list).view,
		app.listSize(\list).view,
		app.listIndex(\list).view,
		app.listItem(\list).replace.view,
		app.textField(\list).append.view,
		app.textField(\list).insert.view,
		app.textView(\list).appendOn.view,
		app.button(\list).notifyAction(\append).view.states_([["append"]]),
		app.textField(\text).view,
		app.textView(\text).view,
		app.staticText(\text).view,
		app.button(\text).notifyAction(\getText).view.states_([["get text from TextView"]]),
	)
});

//:Examples for text and button views with lists
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).do({ | me | me.value.adapter.items_(me, ["a", "b", "c"]) })
			.view,
		StaticText().string_("Press return to append my string to the list:"),
		app.listItem(\list)
			.append			// when return key is pushed, append new item to list
			.replaceOn		// replace item if sent message \replace
			.insertOn			// insert item if send message \insert
			.view,
		app.button(\list).notifyAction(\replace).view.states_([["replace"]]),
		app.button(\list).notifyAction(\insert).view.states_([["insert"]]),
	)
});

//:Indexing the same list independently from different views
var list;
list = List.newUsing(["a", "b", "c"]);
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).do({ | me | me.value.adapter.items_(me, list) })
			.view,
		app.listItem(\list)
			.append			// when return key is pushed, append new item to list
			.replaceOn		// replace item if sent message \replace
			.insertOn			// insert item if send message \insert
			.view,
		app.button(\list).notifyAction(\replace).view.states_([["replace"]]),
		app.button(\list).notifyAction(\insert).view.states_([["insert"]]),
		StaticText().string_("Same list, but independent index into it:"),
		app.listView(\list2).do({ | me | me.value.adapter.items_(me, list) })
			.view,
		app.listItem(\list2)
			.append			// when return key is pushed, append new item to list
			.replaceOn		// replace item if sent message \replace
			.insertOn			// insert item if send message \insert
			.view,
		app.button(\list2).notifyAction(\replace).view.states_([["replace"]]),
		app.button(\list2).notifyAction(\insert).view.states_([["insert"]]),
	)
});

//:One list view obtains its items from sublists contained in another item
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list, { | me | me.value.adapter collect: { | l | 
				format("a list of size: %", l.size)
			}
		}).items_([["alpha", "beta", "gamma"], ["delta", "epsilon"]])
			.view,
		app.listView(\sublist).sublistOf(\list).view,
		app.textField(\sublist).sublistOf(\list).listItem.view,
		app.button(\sublist).sublistOf(\list).nextItem.view.states_([["next"]]),
	)
});

//:Creating list items by interpreting code from a text field
// Note: The created elements only replace the elements on the list. 
// Pre-existing Synths are not replaced. NodeProxies can be used for that. 
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).do({ | me | me.value.adapter.items_(me, ["a", "b", "c"]) })
			.view,
		app.listItem(\list, TextView())
			.appendOn({ | me | me.view.string.interpret })
			.replaceOn({ | me | me.view.string.interpret })
			.insertOn({ | me | me.view.string.interpret })
			.view,
		app.button(\list).notifyAction(\append).view.states_([["append"]]),
		app.button(\list).notifyAction(\replace).view.states_([["replace"]]),
		app.button(\list).notifyAction(\insert).view.states_([["insert"]]),
	)
});

//:===================== Special cases

//:Getting the string from a view without updating the Value
// Note: Advanced. Used by ProxyCodeEditor.
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.textView(\text).makeStringGetter.view,
		app.button(\text).action_({ | me |
			"the string of the other view is: ".post; me.getString.postln;
			"the string in the Value container is: ".post; me.value.adapter.string.postln;
		}).view.states_([["get string without updating"]])
	)
})


