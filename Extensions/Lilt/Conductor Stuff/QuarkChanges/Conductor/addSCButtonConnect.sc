/* IZ Sunday, May 11, 2008 
adding connect to SCButton to connecting to ConductorGUI
*/

+ SCButton {
	connect { arg ctl;
		var link;
		this.value = ctl.input;
		this.states = ctl.states;
		this.action_(
			{ | me |
				ctl.input_(me.value)
			}
		);
		link = SimpleController(ctl).put(\synch, { | ... args |
				defer({ this.value = ctl.input; nil });
			})
			.put(\update, {
				{
//					thisMethod.report(ctl.value, this.value);
					this.value = ctl.value
				}.defer;
			});
		this.onClose = {  link.remove };
		this.value = ctl.value;
	}
}	
