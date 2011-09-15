/* (IZ 2006-02-27)  Brief doc of the scripts in the Sand folder

==========  Installation, Startup, Playing ========== 

- Put the Lilt library in the SCClassLibrary folder and re-compile SC. 
- A small window named "Browsers" will appear to the right of the server windows. Click on the Scripts button to open the Scripts Browser.
- The leftmost list view on the Scripts browser is the folders view. Select the folder named "Sand".
- The right list view now lists the scripts of the Sand folder. Select the script named "Sand-Main Script" by clicking on its name.
- Type "t" while Sand-Main Script is selected to open an instance of this script. This will make a ***SMALL*** gui window for starting  and stopping the script. The gui window is placed at the rightmost border of the screen, right below the window for script groups. 
- To start operating the Sand-Main Script, click on the button of its gui. This will create a session containing all the scripts needed for the Sand performance. It will also start data reception from Python or other Sand software. 
- To stop, click again on the button of the Sand-Main Script gui. This will stop data reception and close the session together with all its scripts. 
- To chain different scores for different pieces one after the other, by OSC control:
	- Stop the Sand-Main Script by sending it the following message and 2 arguments:
		Script rStop main
	  This will close the current Sand session
	- Rename the config file you want to use to config, and place the file in the
	  Scripts/Sand/ fodler.
	- Restart the Sand-Main script by sending it the following message and 2 arguments:
		Script rStart main
   NOTE: It is important to create the Sand-Main Script instance manually first. 
   	After that one can start it by OSC. 

========== Configuration ==========

1. The config script

To load sound samples, create your own scores of scripts and rules, use custom scripts for listening to data from external programs, or create any other data structures, edit the script: 
	/Scripts/Sand/config
This file tells the Sand-Main script which files to use for listener (data input) and for score (sound generation). The score script may load any number of instances of other scripts, both for sound generation and for creation of additional data structures that support the logic of the score. The config script is very short; look at the config script itself to see how it is used in detail. The changes you may want to make depending on circumstances are: 

(a) Specify which sample files or folders to load.
(b) Specify which listener script to use (see below).
(b) Specify which score script to use (see below). 

2. The listener script (DataReceptionNoTags / DataReceptionWithTags etc.)

The listener script creates the OSCresponder(s) that are used to obtain data from the sand-driving program. When an OSC message is received, it notifies all its dependants about the contents of this message. As different versions of the program may vary the structure and number of messages used during communication from the sand program to sc, one may write different variants of this script to adapt to the different message structures used. The listener script notifies each rule of the score script when a vertex is received, and passes its environment to the rule as argument. That way, the rule has access to all the data that were received by OSC and stored in the environment of the listener. 

3. The score script (Score etc.)

This script does two things: 

1. Create an "orchestra" consisting of any number of other "instrument" scripts that produce sound in response to data received from the listener. 

2. Define any number of rules for playing the instruments of the orchestra based on the messages receive from the listener. These rules are functions that are evaluated using the enfironment of listener script as argument. This environment contains also the data of the current vertex stored under the names of each data item (see script DataReceptionNoTags).

See example score scripts zTestScore00 etc. on how to create instruments and to code the rule functions. 

Important: 

(a) The instrument scripts should be added to the Session contained in ~session so that their guis are arranged automatically and so that they close when the Sand-Main Script stops. 

(b) The rule functions should be stored in the variable ~conductor. If more than one rule function is used, then the functions should be stored in an Array, for example: 
~conductor = [ { (rule 1) }, { (rule 2) }, ... ];

========= Acessing Input Data in the Score =========
All data input by the listener script are stored in the environment of that script (envir). That environment is passed as argument to each rule function in the score, so each rule can obtain it and the variables it contains thus:  
~conductor = [ { |envir| envir.ai  ... },  ... ];

Streams generated from patterns such as Paction cannot easily access the envir when passed as argument. Another technique can be used in this case: The environment of listener is stored as global environment variable ~envir in the default parent environment of Script. In the score script, assign ~envir to a local variable for fast access, then use it within the definition of the Paction functions or any other pattern functions that need it: 

var envir, rule;
.... ...
envir = ~envir;

rule = Pactions([
	{ sin.set(\freq, envir.aa * 2000; sin.start }}, 
	...
]).asStream;

========= Starting new pieces remotely via OSC command =========
To load a new piece and start playing it via OSC, send to SC the message
'do' with additional argument the following string: 
	"Script['main'].envir['startScore'].('<scorePath>')"
Where <scorePath> is either a symbol with the name of a score script inside the Sand subdirectory of Scripts, or a string giving the full path of a score script to be loaded.
This will stop the current piece, load the new score, and start the piece with the new score. 
To test this from SuperCollider evaluate the following: 

(
NetAddr("127.0.0.1", 57120).sendMsg(\do, 
	"Script['main'].envir['startScore'].('zTestScore00')"
);
)

Note: In SuperCollider, to write the full path of a file instead just the name of the script in the Sand subfolder, you must precede the quote mark (") inside the string with a backslash: (\").
The string sent itself will not include the backslash. When sending from another language, do not include the backslash - encode it in the way that your own programming language requires. 
Example: 

(
NetAddr("127.0.0.1", 57120).sendMsg(\do, 
	"Script['main'].envir['startScore'].(
		\"/Applications/SuperCollider_f/SCClassLibrary/Lilt/Scripts/Sand/zTestScore06VertDat\"
	)"
);
)

========== More Background Info on the Scripts Involved: ==========

*** Sand-Main Script

Sand-Main Script sets up a session that to manage all the scripts involved in a sand piece. The session and its scripts are created when the script starts (not when the main script is created!). So, starting the main script creates the session and the scripts that it contains, and stopping the main script removes the session and closes all the scripts it contains. This script uses a subscript called 'config' in order to customize the session. 

*** config Script

This script is evaluated by the Sand-Main Script when this starts (that is, when one clicks on the start button of the Sand-Main Script - not when one creates the Sand-Main Script). The config script is evaluated "as is", which means that no start/stop functions are involved and no Gui is used. The currentEnvironment is set to Script.globalParentEnvir so that any environment variables it uses or creates refer to that environment. The config script should set the environment variables ~listenerPath and ~scorePath to be the paths of the listener and score scripts respectively.

*** listener Script:  

This script implements the data reception from the sand driving program. It should create any OSCresponders needed at start time and remove them at stop time. To use the standard update mechanism for score rules that is set up by Sand-Script Main, the OSCresponders should either set controlSpec parameters of the listener script or explicitly perform ~script.changed(message, ... additioal data). 

*** score Script: 

This is the main script for implementing an orchestra and the rules that play this orchestra based on Sand input. It does these two things as follows: 

1. The instrument playing scripts are created here, and placed inside the Session created by Sand-Main Script. To access these scripts inside the function or functions that make up the score playing rules, one should assign each script to local or environment variable of the score script. 

2. This score script creates one or more functions which define the rules for making sounds on the instruments by reacting to the data received by the DataReception script. This function or array of functions must be stored in the environment variable called ~conductor. The Sand-Main Script attaches all functions that are found in ~conductor to the DataReception script as dependant, so that these get called whenever data are received. 


*/

