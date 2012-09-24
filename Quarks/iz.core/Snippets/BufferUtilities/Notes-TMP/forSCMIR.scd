e = SCMIRAudioFile(Platform.resourceDir +/+ "sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);
\test.addNotifier(e, \extractionDone, { | smaf |
	smaf.featureinfo.postln;
	smaf.featuredata.postln; });
//:
{e.extractFeatures()}.fork(AppClock);
