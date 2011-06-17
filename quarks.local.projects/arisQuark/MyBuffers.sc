
/*
//:Create class for load buffers to Server


MyBuffers.seals;
MyBuffers.sketch;
MyBuffers.conet;
MyBuffers.osmoFull;


//: -----
*/

MyBuffers {
	*seals	{
		BufferResource loadPaths: UserPath("Extensions/of/scStuff/BuffferList/seals.txt").load;
	}
	
	*conet	{
		BufferResource loadPaths: UserPath("Extensions/of/scStuff/BuffferList/conet.txt").load;
	}
	
	*sketch	{
		BufferResource loadPaths: UserPath("Extensions/of/scStuff/BuffferList/sketch.txt").load;
	}
	
	*osmoFull {
		BufferResource loadPaths: (UserPath("Extensions/of/scStuff/BuffferList/osmoFull.txt").load);
	}

}

