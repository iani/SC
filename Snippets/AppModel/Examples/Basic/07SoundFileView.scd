/* iz Fri 21 September 2012 10:34 PM EEST
Preparing soundFileView method for AppModel. 
*/

//:
a = AppModel().window({ | window, app |
	window.layout = VLayout(
		v = app.soundFileView(\soundFile).view
	)
});

//:

a.getValue(\soundFile).adapter.soundFile_('weddell3R'.b.path);
