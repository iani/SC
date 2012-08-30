/* IZ Thu 30 August 2012  9:40 PM EEST
Redoing Dock class using AppModel. 

The Document-List window is build with an AppModel sticky window.

The other utilities such as scope windows etc. should go in a separate class. 

Finally, the document list window could be incorporated in the Panes class. 

Document.openDocuments.first.front
*/

AppModel().window({ | window, app |
	var updateDocList, currentDocumentIndex;
	var currentDocuments;  // keep track of last open document to retrieve when a document closes
	currentDocuments = Document.openDocuments.copy add: Document.current;
	updateDocList = { | me |
		{
		me.adapter.adapter.setItems(Document.openDocuments.sort({ | a, b | a.name < b.name }));
		me.adapter.value = currentDocumentIndex = me.adapter.adapter.items.indexOf(Document.current);
		if (currentDocumentIndex.notNil) {
			currentDocuments = currentDocuments add: Document.current;

		}{
			while { currentDocuments includes: Document.current }
			{ currentDocuments remove: Document.current; };
			currentDocuments.last.front;
			me.adapter.value =  
				me.adapter.adapter.items.indexOf(currentDocuments.last);
		};
		me.view.value = me.adapter.value;
		}.defer(0.1);
	};
	window.bounds = Rect(Window.screenBounds.width - 160, 200, 160, Window.screenBounds.height - 200);
	window.name = "Documents";
	window.layout = VLayout(
		app.listView(\documents)
		.initItems(
			Document.openDocuments.sort({ | a, b | a.name < b.name }).collect(_.name),
			Document.openDocuments.collect({ | d | d.name.asSymbol })
				.sort.indexOf(Document.current.name.asSymbol)
		)
		.updateAction_({ | view, sender, adapter |
			view.items = adapter.adapter.items collect: _.name;
		})
		.widgetDo({ | me |
			me.adapter.adapter.setItems(Document.openDocuments.sort({ | a, b | a.name < b.name }));
			me.addNotifier(Document, \docOpened, { | doc | updateDocList.(me); });
			me.addNotifier(Document, \docClosed, { | doc |
				while { currentDocuments includes: Document.current } 
				{ currentDocuments remove: Document.current; };
				updateDocList.(me);
			});
			me.addNotifier(Document, \docClosed, { | doc |
				while { currentDocuments includes: Document.current } 
				{ currentDocuments remove: Document.current; };
				updateDocList.(me);
			});
			me.addNotifier(Document, \docToFront, { | doc |
				currentDocuments = currentDocuments add: doc;
				me.view.value = me.adapter.adapter.items.indexOf(doc);
			});
			me.view.action_({ | view | me.adapter.adapter.items[view.value].front; });
		})
		.view
	).margins_(0, 0, 0, 0);
})

//:

\test.addNotifier(\notifier, \message, { { "hello".postln; } ! 10 });
\test.addNotifier(\notifier, \message2, { { "hello 2".postln; } ! 10 });
\test.addNotifier(\notifier2, \message, { { "hello 3".postln; } ! 10 });

NotificationCenter2.register(\notifier, \message, \test, { "test received from nofifier".postln; });
NotificationCenter2.unregister(\notifier, \message, \test);

NotificationCenter2.registerOneShot(\notifier, \message, \test, { "test received from nofifier".postln; });


NotificationCenter2.registrations.;

\notifier.notify(\message);

\test.removeNotifier(\notifier, \message);

\test.objectClosed;


\notifier.notify(\message);
\notifier.notify(\message2);
\notifier2.notify(\message);


\test.objectClosed;

\test.registrations;



NotificationCenter2.registrations.at(\notifier);


OnObjectCloseRegistrations.all[\test]
OnObjectCloseRegistrations.all[\notifier2]


