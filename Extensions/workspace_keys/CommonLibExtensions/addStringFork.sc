/*
Add routine support for intepreting strings from a Document using DocListWindow.
*/
+ String {
	fork { | clock |
		this.compile.fork(clock ? AppClock);	
	}
}