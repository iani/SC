/* iz Mon 24 September 2012  9:16 AM EEST

Access the view of a widget from another widget. Needed for actions that depend on aspects of the view for which no interface is available via an adapter. 

*/

AppModel().window({ | window, app |
	window.layout = VLayout(
		app.numberBox(\number).viewGetter.view,
		app.button(\number)
		.action_({ | me | me.getView.background_(Color(*({ 1.0.rand } ! 4))); })
		.view.states_([["change color"]])
	
	)	
})