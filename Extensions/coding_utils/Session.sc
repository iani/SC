/* 

Save and load positions of documents to / from archive. 

IZ July 20, 2011: Does not always work.
New doc positions are not stored when a doc is moved. 
Also "bring window to front after compiling" feature does not always work.

Implementation written by MC in May-June 2011. 
Minor corrections by IZ together with MC early July 2011. 

*/

Session {
	classvar <session, archivePath;	// for saving / restoring doc positions and doc texts to archive
	var <selectOrder, <prevSelectOrder, <docPositions;

	//mc:
	*new { ^super.new.init }
	
	*prepare {	
		archivePath = PathName(this.class.filenameSymbol.asString).pathOnly +/+ "sessionArchive";
		session = if (File.exists(archivePath)) { this.readArchive(archivePath) } { Session.new };
		session.activate;
		UI.registerForShutdown( {
			session.writeArchive(archivePath) 
		})
	}

	*prevSelectOrder { ^session.prevSelectOrder }

	init {
		selectOrder = [];
		docPositions = IdentityDictionary.new;
		session = this;
	}
	activate {
		var openDocNames;
		openDocNames = Document.allDocuments.reject{|doc| doc.isListener}
			.collect{|doc| doc.name.asSymbol};
		selectOrder = selectOrder.select{|sym| openDocNames.includes(sym) };
		prevSelectOrder = selectOrder.copy;
//this.logln("selectOrder" + prevSelectOrder);
		NotificationCenter.register(Panes, \docToFront, this, {|doc| this.putFirst(doc) });
	}
	putFirst{|doc|
		var docID = doc.name.asSymbol;
		docPositions[doc.name.asSymbol] = doc.bounds;
		selectOrder.remove(docID);
		selectOrder = selectOrder.addFirst(docID);
	}
	
	*restoreWindowPositions { session.restoreWindowPositions }

	restoreWindowPositions {		
		[0,1] do: { | i |
			Document.allDocuments.select {|doc| doc.name.asSymbol == prevSelectOrder[i]}				.do { |doc| doc.front };
		};
		prevSelectOrder do: { | docName |
				Document.allDocuments.detect({ | d | d.name.asSymbol == docName })
					.bounds = docPositions[docName];
		};
	}

}