/*
UserPath("hello.txt");
*/

UserPath {
	*new { | path |
		^Platform.userAppSupportDir ++ "/" ++ path;	
	}	
}