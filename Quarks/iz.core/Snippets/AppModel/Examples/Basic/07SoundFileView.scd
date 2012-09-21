/* iz Fri 21 September 2012 10:34 PM EEST
Preparing soundFileView method for AppModel. 
*/

//:
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.widget(\soundFile, SoundFileView())
	)	
})