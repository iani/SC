
+SCNumberBox {
	connect { arg ctl; 
	var link;
		this.value_(ctl.value);
		this.action_({ctl.value_(this.value); });
		link = SimpleController(ctl)
			.put(\synch, 
			 { arg changer, what;
			 	defer({
			 	this.value = ctl.value									.round(pow(10, floor(max(min(0, ctl.value.abs.log10 - 3).asInteger,-12))));
					nil
				});
			}
		);
		this.onClose = { link.remove };
	}
}
+SCSlider {
	connect { arg ctl;
		var link;
		this.value = ctl.input;
		this.action_({ctl.input_(this.value); });
		link = SimpleController(ctl).put(\synch, {
				defer({ this.value = ctl.input; nil });
			});
		this.onClose = {  link.remove };
	}
}	
+Knob {
	connect { arg ctl;
		var link;
		this.value = ctl.input;
		this.action_({ctl.input_(this.value); });
		link = SimpleController(ctl).put(\synch, {
				defer({ this.value = ctl.input; nil });
			});
		this.onClose = {  link.remove };
	}
}
+SCMultiSliderView {
	connect { arg ctl;
		var link, size;
		this.value = ctl.input;
		this.mouseUpAction_({ctl.input_(this.value); });
		size = this.value.size;
		this.thumbSize = (this.bounds.width - 16 /size);
		this.xOffset = 0;
		this.valueThumbSize = 1;
		link = SimpleController(ctl).put(\synch, {
				defer({ this.value = ctl.input; nil });
			});
		this.onClose = {  link.remove };
	}
}	

+SCRangeSlider {
	connect { arg ctls; var lo, hi,link;
		#lo, hi = ctls;
		this.setProperty(\lo,lo.input);
		this.setProperty(\hi,hi.input);
		this.action_({lo.input_(this.lo); hi.input_(this.hi); });
		link = 
			[SimpleController(lo).put(\synch, {
				defer({ this.setProperty(\lo,lo.input);  nil });
			}),
			SimpleController(hi).put(\synch, {
				defer({ this.setProperty(\hi,hi.input);  nil });
			})];
		this.onClose = { link.do(_.remove) };
	}
}
+SC2DSlider {
	connect { arg ctls; var x, y, link;
		#x, y = ctls;
		this.setProperty(\x,x.input);
		this.setProperty(\y,y.input);
		this.action_({x.input_(this.x); y.input_(this.y); });
		link =	
			[SimpleController(x).put(\synch, {
				defer({ this.setProperty(\x,x.input); nil });
			}),
			SimpleController(y).put(\synch, {
				defer({ this.setProperty(\y,y.input); nil });
			})
			];
		this.onClose = {link.do(_.remove) };
	}

}

+EZSlider {
	connect { arg ctl; var link;
		this.value_(ctl.value);
		this.action_({ctl.value_(this.value); });
		link = 
			SimpleController(ctl)
			.put(\synch, 
			 { arg changer, what;
			 	defer({ numberView.value = ctl.value });
			});
		this.sliderView.onClose = {link.remove};
		this.numberView.onClose = { link.remove};
	}
}



+SCPopUpMenu {
	connect { arg ctl; 
	var link;
		this.value_(ctl.value);
		this.action_({ctl.value_(this.value); });
		link = SimpleController(ctl)
			.put(\synch, 
			 { arg changer, what;
			 	defer { this.value = ctl.value };
			})
			.put(\items,
			 { arg changer, what;
			 	defer { this.items = ctl.items };
			}
		);
		this.onClose = { link.remove };
	}
}

+SCListView {
	connect { arg ctl; 
	var link;
		this.value_(ctl.value);
		this.action_({ctl.value_(this.value); });
		link = SimpleController(ctl)
			.put(\synch, 
			 { arg changer, what;
			 	defer { this.value = ctl.value };
			})
			.put(\items,
			 { arg changer, what;
			 	defer { this.items = ctl.items };
			}
		);
		this.onClose = { link.remove };
	}
}