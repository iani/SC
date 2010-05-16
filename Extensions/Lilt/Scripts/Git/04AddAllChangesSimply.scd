/* iz Friday; January 15, 2010: 8:36 AM

*/


~script.routine {
	// list all files in the User Application Support Directory of SuperCollider
	("cd " ++ Platform.userAppSupportDir.replace(" ", "\\ ") ++ "/Extensions/Lilt
ls -a
git add .").runInTerminal;
	1.0.wait;
};
