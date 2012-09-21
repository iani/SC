/* Fri 21 September 2012  8:14 PM EEST

Using buttons to navigate in lists, and NumberBoxes to display position and size

*/

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

//:Changing the item of a list from another value widget.
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.popUpMenu(\list).items_(["alpha", "beta", "gamma"]).view,
		app.listView(\list).view,
		app.textField(\list).listItem.view,
		app.button(\other).action_({ 
			app.getValue(\list).adapter.replace(app, "testing testing testing")
		}).view.states_([["test change item"]]);
	)
})
