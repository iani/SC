                    AppModel Application Framework
                    ==============================

Author: Ioannis Zannos
Date: Tue 28 August 2012  6:37 PM EEST


This library provides a framework for creating GUIs for SuperCollider applications. 

AppModel has methods for creating views named after the view creating methods of GUI. Additionally, it creates and stores Adapter instances which make it easy to interconnect different objects and views. 

Examples of subclasses of AppModel are provided: ProxyCodeMixer, ProxyCodeMixer3 and ProxyCodeEditor, which build guis for working with NodeProxies created from a Document via keyboard commands, executed throught class ProxyCode.  ProxyCode is an extension of Code class, also provided here: 

Code is a class for navigating inside a SuperCollider window by marking code segments as "snippets" with comments starting like this: 

 //: 

The Code class facilitates the selection and evaluation of such code snippets, and provides further utilities such as creation of a list of snippets, a window with snippet buttons, and binding of snippets to OSC commands. 

Finally, this library contains a variant of NotificationCenter, called NotificationCenter2, which enables one to create notifications for objects a little more easily, but most importantly, to remove these notifications when either the sending object or the listening object call the method "objectClosed". 


