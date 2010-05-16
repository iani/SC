/* iz Thursday; September 11, 2008: 8:14 AM

Template-Classes for controllers that process updates from ContourCluster and Contour.
The present approach uses MessagePerformer which performs the messages received via 'update' as methods. 

There are 5 methods corresponding to the messages received from ContourCluster and from the Contours it creates. Subclasses can add code to these methods to perform actions in response to the updates.  

A: Messages received from ContourCluster -- and the methods that correspond to them. 
contours		 		(no arguments) new contour vectors were received from osc input and processed
contour_born(contour)	contour is a newly created instance of Contour
zero_contours			(no arguments) there are no contours present in the contour cluster

B: Messages received from an individual Contour
'moved': 			the contour moved to a new position
'died':			the contour has ceased to exist

*/

ContourClusterController : MessagePerformer {
	var <>contourControllerClass;
	*defaultArgs { ^[this.model, this.contourControllerClass] }
	*model { ^ContourCluster.getInstance }
	*contourControllerClass { ^ContourController }
	*new { | model, contourControllerClass |
		^super.new.init(
			model ?? { this.model },
			contourControllerClass ?? { this.contourControllerClass }
		)
	}
	init { | argModel, argContourControllerClass |
		model = argModel;
		contourControllerClass = argContourControllerClass;
	}
	contours { }
	contour_born { | self, contour |
		contourControllerClass.new(contour).add;
	}
	zero_contours { }
}

ContourController : MessagePerformer {
	moved {}
	died {
		model.removeDependant(this);
	}
	*addScript { | script, controllerClass |
		controllerClass = controllerClass ? ContourClusterController;
		controllerClass.new(contourControllerClass: this) addScript: script;
	}

}
