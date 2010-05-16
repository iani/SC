/* IZ Monday, May 12, 2008 

New To Dos: 

Incorporate Conductor-like scheme for customizing GUIs of scripts: 
Use header and body variables in script to store keys of items to be drawn. 
Use defaults and default-override scheme to provide default drawing methods for items such as the start-stop button, the link-view and the parameter views, but be able to override these defaults or provide other GUIs for other types of elements. 

A basic case study to start with might be once more a list view, with updates of other "satellite views" whenever some item from the list is selected. 

Need to implement a resize algorithm. (?)

To store the full dimensions of a script's gui in the session rather than just it's left top corner. 

SynthDefs: make it load only files that end in .scd or .rtf

Possibly: Move out all the init-time loading of files in SynthDefs, Samples, ScriptBrowser from initClass to custom init files inside the scwork folder. 

Debug removeAllWriters to remove tr (action / trigger rate) links from scripts when deleting them. See Script:close. Necessary to correctly save a session whose script has been deleted and that script is a reader linked to the tr-output of another script. s

*/

