+ PathName {

	extensionAt {|at=1| 
		var n=0;
		var fileName = this.fileName;
		fileName.reverseDo{|char, i| 
			if(char == $.) { n = n +1;
				if(at==n) { ^fileName.copyRange(fileName.size - i,fileName.size - 1)} 
			}
		};
		^""
	}
	/*
		PathName("ok.test.string.here").extensionAt.postln
		PathName("ok.test.string.here").extensionAt(2).postln
		PathName("ok.test.string.here").extensionAt(3).postln
	*/
}