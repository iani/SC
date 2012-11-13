/* iz Tue 13 November 2012  5:23 PM EET
Window with list of proxies from ProxyCentral.
See ScriptLibGui:proxyListWindow
*/

// !!!!!!!!!!!!!!!! UNDER DEVELOPMENT!
ProxyList : AppModel {
	var <owner; // a ScriptLibGui (or other app model?). Make only one window per owner

	makeWindow {
		this.stickyWindow(owner, 'proxyListWindow', { | w, app |
			w.bounds = Rect(0, 20, 100, 600);
			w.layout = VLayout(
				owner.listView('Proxy').proxyWatcher(
					startedAction: { | me | this.colorProxyList(me.view, me.items) },
					stoppedAction: { | me | this.colorProxyList(me.view, me.items) }
				)
				.do({ | widget |
					{
						widget.updateAction(\index, { widget.view.value = widget.index });
						widget.view.items = widget.value.items collect: _.name;
						widget.value.items do: { | item |
							widget.addNotifier(item.item, \play, { this.colorProxyList(widget.view, widget.items) });
							widget.addNotifier(item.item, \stop, { this.colorProxyList(widget.view, widget.items) });
						};
						widget.view.action_({ | me |
							widget.value.index_(nil, me.value);
						});
						widget.view.keyDownAction_({ | me, char, mod, ascii, key |
							var proxy;
							proxy = widget.items[widget.view.value];
							switch (ascii,
								32, { proxy.toggle }, // space
								27, { w.close },  // escape
								60, { proxy.item.vol = proxy.item.vol - 0.02 max: 0 }, // <
								62, { proxy.item.vol = proxy.item.vol + 0.02 },  // >
								46, { proxy fadeTo: 1 },  // .
								47, { proxy fadeTo: 0 }  // /
							);
						});
						this.colorProxyList(widget.view, widget.items);
					}.defer(0.1)
				}).view,
			)
		});
	}

	colorProxyList { | view, items |
		view.colors = items collect: { | item |
			if (item.isMonitoring) { Color.red } { Color.white }
		};
	}
}

