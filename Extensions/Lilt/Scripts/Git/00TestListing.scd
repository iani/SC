/* iz Friday; January 15, 2010: 6:55 AM

Go to user SuperCollider home directory. 

This is just a test of String:runInTerminal. 

*/


~script.routine {
	// list all files in the User Application Support Directory of SuperCollider
	("cd " ++ Platform.userAppSupportDir.replace(" ", "\\ ") ++ "\nls -a").runInTerminal;
	1.0.wait;
};
