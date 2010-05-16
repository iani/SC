/* iz Friday; January 15, 2010: 8:19 AM

Once git is installed in the Lilt folder and the first commit has been sent,
this script will add all changes and send them to the server. 

*/



~script.routine {
	("cd " ++ Platform.userAppSupportDir.replace(" ", "\\ ") ++ "/Extensions/Lilt
git add .
git commit -a
git push").runInTerminal;
	1.0.wait;
};
