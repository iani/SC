/* iz 080902
Attempt to model a cluster of contours detected by Contour.dll in vvvv, as evolving entities in time.

c = ContourCluster.new;
c addDependant: { | ... args | args.postln };
d = { | i | Array.rand(6, 0.0, 1.0).add(i) } ! 5
c checkNewInputVectors: d;
c.contours;
c.contours.size;
e = { | i | Array.rand(6, 0.0, 1.0).add(i + 3) } ! 7
c checkNewInputVectors: e;
c.contours;
c.contours.size;

f = { | i | Array.rand(6, 0.0, 1.0).add(i + 2000) } ! 7
c checkNewInputVectors: f;
c.contours;
c.contours.size;

f = { | i | Array.rand(6, 0.0, 1.0).add(i + 2000) } ! 2
c checkNewInputVectors: f;
c.contours;
c.contours.size;
*/

ContourCluster : ModelWithController {
	var <contours;
	*model { ^ContourInput.getInstance }
	*actions {
		^(
			contours: { | input, cluster |
//				thisMethod.report(input, cluster);
				cluster.contours = input.contourVectors;
			}, 
			zero_contours: { | input, cluster | cluster.die }
		)
	}
	contours_ { | newContourVectors |
		contours.copy do: _.moveOrDie(newContourVectors);
		newContourVectors do: Contour(this, *_);
		this.changed(\contours);
	}
	addContour { | contour |
		contours = contours.add(contour);
		this.changed(\contour_born, contour);
	}
	removeContour { | contour |
		contours.remove(contour);
	}
	die {
		// all clusters have ended and must be removed
		// triggered by osc message '/zero_contours' from vvvv
		contours.copy do: _.die;
		this.changed(\zero_contours);
	}
}

Contour : Model {
	var <cluster; // the cluster to which this contour belongs;
	var <x, <y, <width, <height, <orientation, <area, <id;
	var <>blobID;	// id of blog that this contour belongs to. For tracking IDs of blobs in time
	*new { | cluster, x, y, width, height, orientation, area, id |
		^super.newCopyArgs(nil, cluster, x, y, width, height, orientation, area, id.asInteger).init;
	}
	init {
		cluster.addContour(this);
	}
	moveOrDie { | contourVectors |
		var newPos;
		newPos = contourVectors detect: { | v | v.last == id };
		if (newPos.isNil) {
			this.die;
		}{
			this.moveTo(newPos);
			contourVectors.remove(newPos);
		}
	}
	die {
		cluster.removeContour(this);
		this.changed(\died);
	}
	moveTo { | newPlaceSpecs |
		#x, y, width, height, orientation, area = newPlaceSpecs;
		this.changed(\moved);
	}
	printOn { arg stream;
		stream << this.class.name << "(" <<* [x, y, width, height, orientation, area, id].round(0.001) << ")";
	}
	// support for neighbor grouping: 
	- { | contour |
		// return the distance from the other contour. See Point:dist
		^hypot(x - contour.x, y - contour.y)
	}
	// support for sending: 
	vector { ^[x, y, width, height, orientation, area, id] }
	
	vectorWithBlobID { ^[x, y, width, height, orientation, area, blobID] }
}

