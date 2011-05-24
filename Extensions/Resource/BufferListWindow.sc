/*
	*new { | key = 'list', bounds, getItemsAction, 
		getIndexAction, notifier, messages, title, delay = 0.0, window |
	}
*/

BufferListWindow : ListWindow {	
	*new { | server |
		server = server ? Server.default;
		^super.new(format("Buffers on %", server.name).asSymbol, nil, 
			{ 
				BufferResource.onServer(server).collect({ | b |
					format("%(%) file: % num frames: %", b.key[2], b.bufnum, 
						PathName(b.path ? "-").fileName, b.numFrames)->{ b.play }
				}).sort({ | a, b | a.key < b.key });
			},
			{ | items |
				var c, cs;
				c = BufferResource.current;
				if (c.notNil) {
					cs = format("%:%:%", c.key[2], PathName(c.path ? "-").fileName, c.numFrames);
					items.indexOf(items detect: { | d | d.key == cs });
				};
			}, 
			BufferResource, [\loaded, \free, \created], delay: 0.01
		);	
	}
}

// A better version, with load button and text field for trying algorithms
// Under development:

BufferListWindow2 : WindowResource {
	*new { | server |
		var key;
		server = server ? Server.default;
		key = format("Buffers on %", server.name).asSymbol;
		^super.new(key, {
			var window;
			window = Window(key);
			window;				
		}).front;
	}
}





/*
w = Window(format("Buffers on %", Server.default.name));
w.addFlowLayout(5@5, 3@3);
v = CompositeView(w, w.view.decorator.indentedRemaining.height_(w.view.bounds.height - 100));
l = SearchableList(v, 
	{
		BufferResource.onServer.collect({ | b | b.key[2].asString->{ b.play } })
	},
	BufferResource, [\created, \loaded, \free]
);

w.front;
*/