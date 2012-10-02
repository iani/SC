/* iz Sun 30 September 2012 11:51 AM EEST

ScriptList knows how to make a copy of itself that can be archived,
and how to restore its ProxyDoc items from data stored in archive. 

TODO: Create separeate class for archiving - dearchiving, with future classes corresponding to different phases in the development of the archiving format of ScriptList. 


*/

ScriptList : NamedList {

	makeArchiveCopy {
		^this.class.newUsing(array collect: _.archiveData).name_(name);
	}

	restoreFromArchive {
		array = array collect: ProxyDoc.newFromArchive(_);
	}
}
