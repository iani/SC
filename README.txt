                Motto: "Organize 10000 code snippets"
                =====================================

Author: IZ
Date: Fri 09 November 2012 12:46 PM EET


1. Application programming framework for SuperCollider based on notifications between objects. Uses the Object:changed method together with own class Notification, which filters changed messages according to message symbol and permits easy attaching and removal of notified objects. 

2. Window for editing and organizing code in virtual files and folders, archiving collections of code snippets in files, and running them either as proxies or as scripts in their own environment, with start and stop. 

3. Proxy Mixer with auto-generated gui for setting proxies parameters

4. Sound file list organizer, permitting viewing, listening, and loading of samples. 


------------


_Folders:_

Table of Contents
=================
1 AppModel 
2 Εxperimental 
3 Guis 
4 Help and Help Source 
5 MenusAndPrefs 
6 ProxyStuff 
7 ScriptLib 
8 SoundFileGui 
9 SystemExtensions 
10 SystemOverwrites 


1 AppModel 
~~~~~~~~~~~

A framework for creating applications. 

- Main classes: =AppModel=, =Value=, =Widget=

2 Εxperimental 
~~~~~~~~~~~~~~~

Various utility classes and ideas, under development.

3 Guis 
~~~~~~~

Guis for various purposes. Currently under development: 

- =MapperGui=: Edit mapping objects such as ControlSpecs. 

4 Help and Help Source 
~~~~~~~~~~~~~~~~~~~~~~~

Self-explanatory. 

5 MenusAndPrefs 
~~~~~~~~~~~~~~~~

- =ActionStrip=: Create a narrow window strip at bottom of screen with buttons and menus as a dock for accessing useful actions. 

- =Lilt2DefaultMenu=: Creates and ActionStrip for using the present library, with buttons for opening ScriptLibs, ScriptMixer, 

6 ProxyStuff 
~~~~~~~~~~~~~

Classes for working with ProxySpace and NodeProxies in the AppModel/Value/Widget framework.

7 ScriptLib 
~~~~~~~~~~~~

Window for organizing code snippets in virtual folders and files. ScritpLib saves in sctxar (SuperCollider text archive) format, but can also export and import in normal folders and files. 

Each snippet can be run as proxy or evaluated in its own environment. Menu for selecting the proxy that is used is provided. Proxies can be controlled during runtime using ScriptMixer. 

Scripts placed in folder =---Config---= are run when the ScriptLib is loaded.  ScriptLib cooperates with SoundFileGui to permit easy addition and deletion of sound files to be loaded with a ScriptLib. 

8 SoundFileGui 
~~~~~~~~~~~~~~~

A window for viewing and listening to sound files from disc, and for organizing them in sound file lists. File lists are automatically saved on archive for the next session. Works together with ScriptLib to add or remove files as code snippets in a ScriptLib's auto-config folder. 

- Main classes: =SoundFileGui=, =BufferItem=

9 SystemExtensions 
~~~~~~~~~~~~~~~~~~~

- =Notification=: Helper class for attaching "changed/update" notifications to actions by symbol and receiver, and for adding and removing notifiers of a receiver. 

- =MultiLevelIdentityDictionary:makeUniqueName=: Create a new unique name for adding a new branch to a MultiLevelIdentityDictionary, when a branch of that name already exists. 

10 SystemOverwrites 
~~~~~~~~~~~~~~~~~~~~

- =Main:run=: Open Lilt2DefaultMenu action strip on run. 
