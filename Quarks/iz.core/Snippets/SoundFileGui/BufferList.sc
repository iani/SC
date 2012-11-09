/* IZ Tue 06 November 2012  8:41 PM EET

INCOMPLETE

Extracting Buffer list loading process from SoundFileGui to make it independently available to other classes.

Returns a List of NamedLists of BufferItems.


*/


BufferList {

	var <>archivePath, <list;

	*new { | archivePath |
		^this.newCopyArgs(archivePath ?? { Platform.userAppSupportDir +/+ "BufferLists.sctxar" }).load;
	}

	load {
		list = Object.readArchive(archivePath);
		if (list.notNil) {
			list do: { | bl | bl do: _.rebuild; }
		}{
			list = List() add: NamedList().name_(Date.getDate.format("Buffer List %c"));
		}
	}


	save {

	}
}