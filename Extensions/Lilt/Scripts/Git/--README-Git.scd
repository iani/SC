/* iz Friday; January 15, 2010: 6:52 AM

This is a collection of mini-scripts that drive the git code managment tool from the Unix shell / terminal using String:runInTerminal.

Once everything has been set up, only one script is needed to update all changes and send them to the server. It is the script named "SendAllChangesToGitHub".

The other scripts are single steps that I tried out and keep here for reference. These are: 

// add changed files interactively
git add -i

// Commit verbosely
git commit -v

// send to server. This version is only for the first push in a newly created repository. 
git push origin master

// send to server all commited changes. 
// This is the version used for subsequent commits after the first commit
git push

*/
