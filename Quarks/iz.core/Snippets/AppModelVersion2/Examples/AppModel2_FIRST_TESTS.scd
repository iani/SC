/* IZ Sat 01 September 2012  1:05 AM EEST
FIrst tests with AppModel2.
*/

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
		app.button(\list).notify(\append).view.states_([["append"]]),
		app.textField(\text).view,
		app.textView(\text).view,
		app.staticText(\text).view,
		app.button(\text).notify(\getText).view.states_([["get text from TextView"]]),
	)
});

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
		app.button(\list).notify(\replace).view.states_([["replace"]]),
		app.button(\list).notify(\insert).view.states_([["insert"]]),
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
		app.button(\list).notify(\replace).view.states_([["replace"]]),
		app.button(\list).notify(\insert).view.states_([["insert"]]),
		StaticText().string_("Same list, but independent index into it:"),
		app.listView(\list2).do({ | me | me.value.adapter.items_(me, list) })
			.view,
		app.listItem(\list2)
			.append			// when return key is pushed, append new item to list
			.replaceOn		// replace item if sent message \replace
			.insertOn			// insert item if send message \insert
			.view,
		app.button(\list2).notify(\replace).view.states_([["replace"]]),
		app.button(\list2).notify(\insert).view.states_([["insert"]]),
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
		app.button(\list).notify(\append).view.states_([["append"]]),
		app.button(\list).notify(\replace).view.states_([["replace"]]),
		app.button(\list).notify(\insert).view.states_([["insert"]]),
	)
});
