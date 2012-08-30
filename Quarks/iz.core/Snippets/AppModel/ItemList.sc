/* IZ Wed 29 August 2012  3:07 PM EEST

Enable ListAdapter to handle lists of items that contain other things than just strings.

See application in BufferList, BufferListGui.

*/
ItemList : List { 
	var <>name, <>itemClass;

	*new { | name, items |
		^super.new.name_(name).array_(items ?? { [] }).init;
	}

	init { itemClass = this.defaultItemClass; }
	defaultItemClass { ^this.class }		// subclasses change this
	asString { ^name }	
	add { | item | super.add(itemClass.new(item)); }
	put { | index, item | super.put(index, itemClass.new(item)); }
	insert { | index, item | super.insert(index, itemClass.new(item)); }
	== { | itemList |
		if (itemList.isNil) {
			^true
		}{
			^name == itemList.name
		}
	}
	
	save {
		postf("Saving % to %\n", this, Platform.userAppSupportDir +/+ name);
		this.makeArchiveVersion.writeArchive(Platform.userAppSupportDir +/+ name);
	}
	
	makeArchiveVersion { ^this } // Subclasses can specialize what they archive. See BufferListList
}

NamedItem { // Not used. Draft. 
	var <>name, <>item;

	*new { | name | ^this.newCopyArgs(name) }

	asString { ^name }
	== { | item |
		if (item.isNil) {
			^true
		}{
			^name == item.name
		}
	}
}

