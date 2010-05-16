/* IZ 080807
Class for working with data received from Contour.dll video motion object detection object in vvvv
*/

ContourRect : Rect {
	// extends Rect to include Orientation and Area data as received from vvvv
	var <orientation, <area, <id;

	// old protocol: | left, top, width, height, orientation, area, id |
	// new protocol as of 080818:
	*new { | left, top, id, area |
//		[area, area - 100].postln;
		^this.newCopyArgs(left, top, nil, nil, nil, area - 100, id);
	}
	
	x { ^left }
	y { ^top }
	
	// return distance of top-left points. 
	// So ContourRects can be used in NeighborGroup or other algorithms
	- { | rect |
		^(left@top) dist: (rect.left@rect.top)
	}
	vvvv_data { ^[left, top, id, area] }
}

ContourList {
	var <>time;
	var <>rects;
	var <raw_data;
	*fromData { | dataArray, vector_size = 4 |
		// create an instance from data received raw via OSC in form 
		// ['/contour', x1, y1, width1, height1, orientation1, area1, x2, y2 ...]
		// also generate and store a timestamp with the moment of creation of this instance;
		var msg;
//		[dataArray.size, vector_size, dataArray.size / vector_size].postln;
//		dataArray.clumps([(dataArray.size / vector_size).asInteger]).flop.size.postln;
//		#msg ... dataArray = dataArray; // dropped on Monday, August 18, 2008
		^this.newCopyArgs(Date.getDate, dataArray.clumps([(dataArray.size / vector_size).asInteger])
			.flop collect: ContourRect(*_), dataArray);
	}

	*sendList { | rect_list, addr, msg = '/contour' |
		thisMethod.report(addr, msg);
		addr.sendMsg(msg, rect_list.size, *(rect_list collect: _.vvvv_data).flop.flat);
	}
	sendList { | addr, msg = '/contour' |
		thisMethod.report(addr, msg);
		this.class.sendList(rects, addr, msg);
	}

	size { ^rects.size }
	areaSum {
		^sum(rects collect: _.area);
	}
	largest_area {
		^largest(rects collect: _.area);
	}
	largest_spot {
		var largest;
		largest = this.largest_area;
		^rects detect: { | rect | rect.area == largest }
	}
	matchPreviousList { | list |
		// rearrange the points in this list so that each point is closest in distance to the point that has the 
		// same index in the previous list received.
		list = list.rects;
		
	}
}

