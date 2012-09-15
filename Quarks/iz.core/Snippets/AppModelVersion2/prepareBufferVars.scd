/* iz Wed 12 September 2012  4:10 PM EEST

Testing testing testing: 

Insert a var line after the snippet header for setting buffers chosen from a menu to be used in the chosen node proxies playing function. 

*/

//:Something like this:  (first rough draft)
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.textView(\code).makeStringGetter.view,
		app.button(\code).action_({ | me |
			var source, lines;
			source = me.getString.postln;
			lines = source.split($\n);
			if (lines[0][0] !== $/) {
				lines = ["//:"] ++ lines;
			};
			if (lines[1][..2] != "var") {
				lines = lines.insert(1, "var newbuffer = 'something'.p;");
			};
			source = lines[1..].inject(lines[0], { | code, line | code prCat: "\n" ++ line });
			me.string_(source);
		})
		.view.states_([["add stuff after header"]]),
	)
})

