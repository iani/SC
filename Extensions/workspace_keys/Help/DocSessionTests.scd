
//:a save all docs under "defaultDocSession" in global archive

Archive.global.put(\defaultDocSession, Document.allDocuments collect: DocProxy(_));
Archive.write;
"Documents saved in archive: ".post;
Document.allDocuments.collect(_.name).postln;

//:b load all docs from "defaultDocSession" in global archive
Archive.global.at(\defaultDocSession).postln;
Archive.global.at(\defaultDocSession) do: { | p | p.path.postln; };
Archive.global.at(\defaultDocSession) do: { | p | p.open.postln; };

//:c create DocProxy tests
a = DocProxy(Document.allDocuments.last);

//:d close the Doc of the doc proxy
Document.allDocuments.last.close;

//:e reopen Doc from proxy
Document(a.name, a.text);

//:f reopen Doc from path
Document.open(a.path);

//:g save hierarchical: many sessions
Archive.global.put(\test, \one, 1);
Archive.write;

//:h load hierarchical: many sessions
Archive.global.at(\test, \one).postln;

//:i save all docs dialog

DocSession.saveAllDialog;

//:j load session dialog

DocSession.loadAndOpenDialog;
