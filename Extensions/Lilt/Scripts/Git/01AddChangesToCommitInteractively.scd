/* iz Friday; January 15, 2010: 7:30 AM
Add the changes that you want to commit next, interactively

git add -i
*/


~script.routine {
	// list all files in the User Application Support Directory of SuperCollider
	("cd " ++ Platform.userAppSupportDir.replace(" ", "\\ ") ++ "/Extensions/Lilt
ls -a
git add -i").runInTerminal;
	1.0.wait;
};
