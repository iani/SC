/* IZ Sat 01 September 2012  1:05 AM EEST */

//:PopUpMenu, ListView accessing the same list
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.popUpMenu(\list).items_(["alpha", "beta", "gamma"]).view,
		app.listView(\list).view,
		StaticText().string_("Edit item below, then type 'return' to send changes to list:"),
		app.textField(\list).listItem.view
	)
})

// === List items: Widgets accessing the currently selected item of a list: 

//:1. Text field for editing the item of a list. Default mode is replace contents.
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).items_(["alpha", "beta", "gamma"]).view,
		StaticText().string_("Edit item below, then type 'return' to send changes to list:"),
		app.textField(\list).listItem.view
	)
})

//:2. Appending the contents of Text field to the list
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).items_(["alpha", "beta", "gamma"]).view,
		StaticText().string_("Edit item below, then type 'return' to append to list:"),
		app.textField(\list).listItem.append.view
	)
})

//:3. Inserting the contents of a Text field at current position in list
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).items_(["alpha", "beta", "gamma"]).view,
		StaticText().string_("Edit item below, then type 'return' to insert in list:"),
		app.textField(\list).listItem.insert.view
	)
})

//:4. Replace, append, insert combined in one window: 
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).items_(["alpha", "beta", "gamma"]).view,
		HLayout(
			StaticText().string_("Replace:"),
			app.textField(\list).listItem.view
		),
		HLayout(
			StaticText().string_("Append:"),
			app.textField(\list).listItem.append.view
		),
		HLayout(
			StaticText().string_("Insert:"),
			app.textField(\list).listItem.insert.view
		)
	)
})

//:Indexing the same list independently from different views
var list;
list = List.newUsing(["a", "b", "c"]);
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\list).do({ | me | me.value.adapter.items_(me, list) })
			.view,
		StaticText().string_("Same list, but independent index into it:"),
		app.listView(\list2).items_(list).view,
		HLayout(
			[StaticText().string_("Edit, then press return to append to list:"), s: 3],
			[app.listItem(\list2).append.view, s: 1]
		)
	)
});

//:Sublists: Lists accessing the lists inside a nested list
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.popUpMenu(\list).items_([["alpha", 1], ["beta", 2], ["gamma", 3, 4, 5]]).view,
		app.listView(\list).view,
		app.listView(\sublist).sublistOf(\list).view,
		StaticText().string_("Edit item below, then type 'return' to send changes to list:"),
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
		StaticText().string_("Edit item below, then type 'return' to send changes to list:"),
		app.textField(\sublist).listItem.view
	)
})

//:Multiple choice lists (!!!!!!!!!!!! UNDER DEVELOPMENT !!!!!!!!!!!!!)
/* Note: No documentation or source code hints  are available about accessing the selected items in
a QListView with \multi or \extended selection modes. */ 
AppModel().window({ | window, app |
	window.layout = VLayout(
		v = app.multiListView(\list).view.items_(["alpha", "beta", "gamma", "delta", "epsilon"]),
	)
})

//:
v

