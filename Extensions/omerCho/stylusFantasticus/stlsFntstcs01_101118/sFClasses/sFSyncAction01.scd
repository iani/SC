{\rtf1\ansi\ansicpg1253\cocoartf1038
{\fonttbl\f0\fnil\fcharset0 Monaco;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue191;\red0\green115\blue0;}
\deftab560
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardeftab560\ql\qnatural\pardirnatural

\f0\fs18 \cf0 \
\
~beatPhrase1 do: \cf2 _\cf0 .deactivate;\
(\
~beatPhrase1 = \cf2 SyncAction\cf0 (\cf3 \\beats\cf0 , \{ \cf2 | beat ... otherStuff |\cf0 \
		beat.postln;}