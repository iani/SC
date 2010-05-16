/* iz Friday; January 15, 2010: 7:35 AM
Commit changes that were staged (added), verbose. 

Must be done after a git add command such as: 
git add -i

*/


~script.routine {
	// list all files in the User Application Support Directory of SuperCollider
	("cd " ++ Platform.userAppSupportDir.replace(" ", "\\ ") ++ "/Extensions/Lilt
ls -a
git commit -v").runInTerminal;
	1.0.wait;
};
