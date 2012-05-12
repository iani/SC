/* 
Needed for proper notification when opening Class definition files with Cmd-j
*/


+ String {
	openTextFileWithReturn { arg selectionStart=0, selectionLength=0;
		^Document.open(PathName(this).asAbsolutePath , selectionStart, selectionLength);
	}
}