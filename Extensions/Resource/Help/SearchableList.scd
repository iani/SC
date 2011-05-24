
//:prepare

Buffer.default;

//:go
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
