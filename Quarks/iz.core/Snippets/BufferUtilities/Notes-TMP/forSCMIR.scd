e = SCMIRAudioFile(Platform.resourceDir +/+ "sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);
\test.addNotifier(e, \extractionDone, { | smaf |
	smaf.featureinfo.postln;
	smaf.featuredata.postln; });
//:
{e.extractFeatures()}.fork(AppClock);

//:

{ e.extractOnsets(); }.fork(AppClock);

//:
e.onsetdata;
//:
e.numonsets;

//:




//:----------------------- NEED DEBUGGING: ------------------------
//:NOT!!!

e = SCMIRAudioFile(Platform.resourceDir +/+ "sounds/a11wlk01.wav", [[\Loudness],[\Transient,0.7,0.2],[\Transient,0.1,0.05],[\Transient,0.1,0.0],[\Transient,0.9,0.0],[\Transient,0.2,0.01],[\Onsets,\rcomplex]]);

\test.addNotifier(e, \extractionDone, { | smaf |
	smaf.featureinfo.postln;
	smaf.featuredata.postln; });
//:

{e.extractFeatures()}.fork(AppClock);
