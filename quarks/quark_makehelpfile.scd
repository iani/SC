// =====================================================================
// Generate Quark overview helpfile
// nescivi, jan 2009
// =====================================================================

(
q = Quarks.local.findQuark("Dimple").info;

(
~header = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> \n 
<html> \n 
  <head> \n 
    <title>"++ q.name ++ "</title> \n
  </head>\n 
  <body>\n 
  <h1>" ++ q.name ++ "</h1>\n
  <h2>" ++ q.summary ++ "</h2>\n
  <h3> by" + q.author ++ "</h3>\n
";
);


~footer = "</body></html>";

(
~string = "";
(Quarks.local.path +/+ q.path +/+ "Help/*.*").pathMatch.collect{ |it|
PathName( it ).asRelativePath( Quarks.local.path +/+ q.path +/+ "Help" );
}.do{ |it| 
	~string = ~string + "<A href=\""++it++"\">"++it++"</A><br>\n";
};
);


f = File.new( Quarks.local.path +/+ q.path +/+ "Help/" ++ q.name ++".html", "w" );
f.write( ~header );
f.write( ~string );
f.write( ~footer );

f.close;

);

// edit the quark file here to point to the right helpdoc
(Quarks.local.path +/+ "DIRECTORY" +/+ q.name ++".quark").openDocument;
