/* IZ 080813
Group (partition) a set of elements into groups, where each element a in a group has at least one neighbor b in the same group so that the distance between a and b is smaller than a given max_distance.

Interface for dependants:
A. Messages sent by Blobs:  
\blobs	(arg: blobs) blobs have been made
\blobBorn (arg: blob) a new blob was created
B. Messages sent by Blob: 
\moved	blob moved to a new position
\died	blob died
S
*/

Blobs : MessagePerformer {
	var <>max_dist, <blobs, previous_blobs;
	*model { ^ContourCluster.getInstance }
	init { | argModel |
		super.init(argModel);
	}
	contours { | sender | this.makeBlobs(sender.contours); }
	contour_born { /* this.changed(\contour_born) */ }
	zero_contours { /* this.changed(\zero_contours) */ }
	makeBlobs { | contours |
		// calculates blobs - this version collects the blobs as array then makes a set
		var bloblets, size;
		previous_blobs = blobs;
		bloblets = contours collect: Bloblet(_);
		size = bloblets.size - 1;
		bloblets do: { | b, index |
			if (index < size) {
				for(index + 1, size, { | j | b.joinNeighbor(bloblets[j], max_dist) })
			}
		};
		blobs = this.matchBlobs(blobs, Set.new.addAll(bloblets collect: _.blob).asArray);
		this.changed(\blobs, blobs);
	}
	matchBlobs { | oldBlobs, newBlobs |
		var distancePairs, movedBlobs;
		var old_blob, new_blob;
		distancePairs = [newBlobs, oldBlobs ? []].allTuples.collect({ | t |
			[t[0], t[1], t[0] dist: t[1]]
		}) sort: { | t1, t2 | t1[2] < t2[2] };
		while { distancePairs.size > 0 } {
			#new_blob, old_blob = distancePairs[0];
			newBlobs remove: new_blob;
			oldBlobs remove: old_blob;
			old_blob.moveTo(new_blob);
			movedBlobs = movedBlobs add: old_blob;
			distancePairs.select({ | d |
				(d[0] === new_blob) or: { d[1] === old_blob }
			}) do: distancePairs.remove(_);
		};
		newBlobs do: _.born(this);
		oldBlobs do: _.died;
		^newBlobs ++ movedBlobs;
	}
	blobBorn { | blob | this.changed(\blobBorn, blob) }
}

BlobsController : MessagePerformer {
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

Blob : Model {
	classvar <>idCount = 0; // idAllocator -> MRUNumberAllocator ...
	// needed to track ids of blobs and for global control of synthesis from blobs:
	var <>id, mean_center, <height, <width, <area, <>contours;
	*new { | ... args |
		^super.new.init(args);
	}
	init { | args |
		contours = Set.new.addAll(args);
	}
	add { | contour |
		contours add: contour;
	}
	addAll { | contour |
		contours addAll: contour.contours;
	}
	dist { | otherBlob |
		^this.mean_center dist: otherBlob.mean_center
	}
	mean_center {
		var array;
		if (mean_center.isNil) {
			array = contours.asArray;
			mean_center = Point(array.collect(_.x).mean, array.collect(_.y).mean)
		};
		^mean_center;
	}
	printOn { arg stream;
		stream << this.class.name << "(" << id << ": " << mean_center << " "
			<<* contours.asArray.collect({ | c | [c.contour.id, c.x.round(0.001), c.y.round(0.001)] })
			<< ")";
	}
	born { | blobs |
		// get id and notify blobs
		id = idCount = idCount + 1;
		blobs.blobBorn(this);
	}
	moveTo { | newBlob |
		// substitute my contours + center by the contours of the new blob. notify dependants
		contours = newBlob.contours;
		mean_center = newBlob.mean_center;
		this.changed(\moved);
	}
	died { this.changed(\died) } // notify dependants
}

Bloblet {
	var <contour, <>blob;
	*new { | contour |
		^this.newCopyArgs(contour).init;
	}
	init {
		blob = Blob(this) // blob = Set[this]
	}
	joinNeighbor { | neighbor, max_dist = 0.1 |
		var origin_blob, target_blob;
		if (abs(contour - neighbor.contour) > max_dist or: { blob === neighbor.blob}) { ^nil };
		if (blob.size > neighbor.blob.size) {
			origin_blob = neighbor.blob; target_blob = blob;
		}{
			origin_blob = blob; target_blob = neighbor.blob;
		};
		target_blob.addAll(origin_blob);
		blob = target_blob;
		neighbor.blob = target_blob;
	}
	x { ^contour.x }
	y { ^contour.y }
}

		
/*
	getIDs {
		// old version of identification: transfers ID numbers
		var distances, distance;
		var old_blob, new_blob;
		distances = [blobs, previous_blobs ? []].allTuples.collect({ | t |
			[t[0], t[1], t[0] dist: t[1]]
		}) sort: { | t1, t2 | t1[2] < t2[2] };
		while { distances.size > 0 } {
			distance = distances[0];
			#new_blob, old_blob = distance;
			new_blob.id = old_blob.id;
			previous_blobs remove: old_blob;
			distances.select({ | d |
				(d[0] === new_blob) or: { d[1] === old_blob }
			}) do: distances.remove(_);
		};
	}
*/
/*
Blob(1).joinNeighbor(Blob(0.99), 0.1);

// Testing original algorithm and new algorithm with blobs2:
a = Array.rand(20, 0, 2.5).round(0.01);
BlobList(0.5, a).blobs;
BlobList(0.5, a).blobs2;

b = BlobList(0.5, a);
{ (b.blobs collect: _.asSet).asSet == (b.blobs2 collect: _.asSet).asSet } ! 50

080907 algorithm for allocating ids to blobs so that each blob inherits the id from that blob of the previous frame whose center is closest to the new blob: 
	Given new_blobs and old_blobs: 
1.	Measure the distances of all blob pairs between new_blobs and new blobs.
2.	Sort these in order of ascending distance, obtaining array: sorted_distances 
3.	Start with the first (smallest distance pair old_blob-new_blob)
4.	give the new_blob in the smallest distance pair the id of the old_blob
5.	remove the new_blob from the list of new_blobs and the old_blob from the list of old_blobs.
6.	remove all pairs containing new_blob and old_blob from the sorted_distances array
7.	repeat steps 3 to 6 above until there are no more distance pairs in sorted_distances


~data = [ 0.24330000579357, 0.40959998965263, 0.26510000228882, 0.36710000038147, 0.33700001239777, 0.29379999637604, 0.043299999088049, 0.025100000202656, 0.00760000012815, 0.09910000115633, 0.11479999870062, 0.048900000751019, -0.41460001468658, -0.049899999052286, -0.028000000864267, 0.001300000003539, 0.0020999999251217, 0.00030000001424924, 1.0, 2.0, 3.0 ];
~contour_input = ContourInput.new;
~contour_input.contours = ~data;
// ~contour_input.rawInput;
// ~contour_input.contourVectors;
~contours = ContourCluster.new.contours = ~contour_input.contourVectors;
// ~contours.contours;
~max_dist = 0.15;
~blobs = Blobs.new;
~blobs.makeBlobs(~contours.contours);
~blobs.blobs;
Bloblet(~contours.contours.first);

///  ======================================================================= 

~contour_input.raw_input = [ 0.32929998636246, 0.36910000443459, 0.41350001096725, 0.26879999041557, 0.37439998984337, 0.4505999982357, 0.43680000305176, 0.34580001235008, 0.3517000079155, 0.23360000550747, 0.0086000002920628, 0.014499999582767, 0.028799999505281, 0.1022000014782, 0.021299999207258, 0.019099999219179, 0.030600000172853, 0.15199999511242, 0.18490000069141, 0.037300001829863, -0.0080000003799796, -0.33899998664856, -0.039200000464916, -0.46579998731613, 0.041200000792742, 9.9999997473788e-05, 0.00030000001424924, 0.0032999999821186, 0.011199999600649, 0.00060000002849847, 4.0, 5.0, 2.0, 1.0, 3.0 ];
// ~contour_input.rawInput;
// ~contour_input.contourVectors;
~contours.raw_input(~contour_input);
~blobs.makeBlobs(~contours.contours);
~blobs.blobs;
*/
