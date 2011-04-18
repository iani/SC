
/* workfile for further extensions and tries

*/

//:1 browse own classes (defined in UserAppSupportDir/Extensions)

a = EZListView(bounds: Rect(0, 0, 250, 200));
a.items = Class.allClasses.select({ | c |
	"SuperCollider/Extensions/".matchRegexp(c.filenameSymbol.asString)
	and: { "Meta*".matchRegexp(c.name.asString).not }
}).collect({ | c | c.name.asSymbol->{ c.openCodeFile; } });
a.widget.parent.resize = 5;
a.widget.resize = 5;
