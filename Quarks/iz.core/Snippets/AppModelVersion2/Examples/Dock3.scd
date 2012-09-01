/* IZ Thu 30 August 2012  9:40 PM EEST
Redoing Dock class using AppModel. 

The Document-List window is build with an AppModel sticky window.

The other utilities such as scope windows etc. should go in a separate class. 

Finally, the document list window could be incorporated in the Panes class. 

Document.openDocuments.first.front
*/

AppModel2().window({ | window, app |
	var updateDocList, currentDocumentIndex;
	var currentDocuments;  // keep track of last open document to retrieve when a document closes
	currentDocuments = Document.openDocuments.copy add: Document.current;
	updateDocList = { | me |
		{
		me.value.adapter.items_(nil, Document.openDocuments.sort({ | a, b | a.name < b.name }));
		me.value.adapter.index_(nil, currentDocumentIndex = 
			me.value.adapter.items.indexOf(Document.current);
		);
		if (currentDocumentIndex.notNil) {
			currentDocuments = currentDocuments add: Document.current;

		}{
			while { currentDocuments includes: Document.current }
			{ currentDocuments remove: Document.current; };
			currentDocuments.last.front;
			me.value.adapter.index_(nil,  
				me.value.adapter.items.indexOf(currentDocuments.last);
			);
		};
		me.view.value = me.value.adapter.index;
		}.defer(0.1); // Obligatory. Leave time for Document to set its name variable when opening.
	};

	window.bounds = Rect(Window.screenBounds.width - 160, 200, 160, Window.screenBounds.height - 200);
	window.name = "Documents";
	window.layout = VLayout(
		app.listView(\documents, { | me | me.value.adapter.items collect: _.name })
		.do({ | me | 
			me.value.adapter.items_(me, 
				Document.openDocuments.sort({ | a, b | a.name < b.name });
			);
			me.value.adapter.index_(me, Document.openDocuments.collect({ | d | d.name.asSymbol })
				.sort.indexOf(Document.current.name.asSymbol)
			);
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
				me.value.adapter.index_(nil, me.value.adapter.items.indexOf(doc));
			});
			me.action_({ | me | me.value.adapter.items[me.view.value].front; });
		})
		.view
	).margins_(0, 0, 0, 0);
})

