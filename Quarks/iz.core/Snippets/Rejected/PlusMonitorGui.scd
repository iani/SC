/* modify monitor gui to provide amp slider with range 0.001 - 4 */
// IZ 2012 0709


//Rejected. Use modifier keys to raise volume > 1
+ MonitorGui {
	makeVol { |width, height|
		var showLev = config.level.binaryValue;

		ampSl = EZSlider(zone, (width @ height), \vol, ControlSpec(0.0001, 10, \exp, default: 1),
			{ arg sl; if(object.notNil) { object.vol_(sl.value) } },
			0, false,
			labelWidth: showLev * 20,
			numberWidth: showLev * 40);

		ampSl.labelView.font_(font).align_(0);
		ampSl.view.resize_(2);
	}
}