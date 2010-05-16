/* iz Friday; February 12, 2010: 7:08 PM
to enable Terminal run commands such as 'ls' on paths that have spaces 

"Application Support".asShellPath;

Platform.userAppSupportDir.asShellPath;

format("ls %/", Platform.userAppSupportDir.asShellPath).runInTerminal;


*/
+ String {
	asShellPath {
		^this.replace(" ", "\\ ");
	}
}