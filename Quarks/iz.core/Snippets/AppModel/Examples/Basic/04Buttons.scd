/* IZ Sat 01 September 2012  1:05 AM EEST
FIrst tests with AppModel2.
*/

// ============== Buttons and Numeric Values 

//:Button accessing its related value
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.numberBox(\number).view,
		app.knob(\number, \freq).view,
		app.button(\number).action_({ | widget |
			widget.number_(20 rrand: 20000) 
		}).view.states_([["random number between 20 and 20000"]]),
		app.button(\number).action_({ | widget |
			// standardizedValue refers to values in the "standard" range between 0 and 1.
			widget.standardizedNumber_([0, 0.25, 0.5, 0.75, 1].choose)
		}).view.states_([["choose randomly between 5 values"]])
	)
})

// ============== Setting the states of buttons 

//:Setting the states of buttons
/* Note: One can address any widget directly from any object by adding it as a listener to that
object and message. That is the easiest way to set a buttons state from elsewhere. */
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.button(\number).action_({ | widget |
			postf("my view's value is: %\n", widget.view.value); 
		})
		// Make the button widget listen to app updates on message \set_your_value:
		.updater(app, \set_your_value, { | me, newValue | me.view.valueAction = newValue })
		.view.states_([["not zero but one"], ["two"], ["three"], ["four"], ["five!"]]),
	);
	// Just a demo. The app can set the button's value based on any other input:
	{ 10 do: { | n | app.notify(\set_your_value, n % 5); 0.5.wait; } }.fork(AppClock);
})

// ============== Buttons and Text (Strings) in Views 

//:Updating the string value of a TextView via button
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.staticText(\text).view,
		app.textView(\text).string_("Edit my TextView + press button to update").view,
		app.button(\text).getText.view.states_([["press me to update edited text"]]),
	)
})

//:Customizing update action of a widget: Make number display size of edited string
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.staticText(\text).view,
		app.textView(\text).string_("Edit my TextView + press button to update").view,
		app.button(\text).getText.view.states_([["press me to update edited text"]]),
		HLayout(
			[StaticText().string_("Size of string in TextView:"), s: 4],
			app.numberBox(\text).updateAction(\text, { | what, me | 
				me.view.value = me.value.adapter.string.size; 
			}).view
		)
	)
})

// ============== Buttons and Lists

//:Appending, replacing, inserting items in lists. 
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
		app.listView(\list2).items_(list)
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

//:Radio buttons from the items of a list
a = AppModel().window({ | window, app |
	window.layout = VLayout(
		*app.radioButtons(\radio, ["a", "b", "c", "d", "e"], { | me | me.item.postln; })
	)
})

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
