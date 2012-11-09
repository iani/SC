/*

Blobs.enable;
Blobs.disable;

b = BlobWatcher();
b.free;
\test.addNotifier(Blobs.default, \blobStarted, { | theBlob | theBlob.postln; });
\test.removeNotifier(Blobs.default, \blobStarted)
Env.perc(

(0..10) difference: (4..7);

*/

Blobs {

	classvar <>default;

	var <oscFunc, <blobs, <>blobClass;

	*initClass {
		StartUp add: { default = this.new; };
	}

	*new {
		^super.new.init; // .enable;
	}

	init {
		oscFunc = OSCFunc({ | msg | this.performList(msg[1], msg[2..]); }, '/tuio/2Dcur').disable;
		blobs = IdentityDictionary();
		blobClass = Blob;
	}

	*disable { default.disable }
	*enable { | blobClass | default enable: blobClass }

	enable { | argBlobClass |
		argBlobClass !? { blobClass = argBlobClass };
		oscFunc.enable;
	}

	disable { oscFunc.disable; }

	set { | id ... args |
		var blob;
		blob = blobs[id];
		if (blob.isNil) {
			blobClass.new(this, id, args)
		}{
			blob.moved(args);
		}
	}

	alive { | ... blobIdArray |
		// Iterate over array. Do not iterate over dict while modifying it: It would skip entries
		blobs.values.asArray do: _.checkIfAlive(blobIdArray);
//		[blobs.values.collect(_.id).sort, blobIdArray.sort].postln;
	}

	fseq { } // catch this message but do nothing

}

Blob {
	var <blobs, <id, <data;
	var <x_pos, <y_pos, <x_vel, <y_vel, <m_accel, <height, <width;


	*new { | blobs, id, data |
		^this.newCopyArgs(blobs, id, data).init;
	}

	init {
		blobs.blobs[id] = this;
		this.setData;
		blobs.changed(\blobStarted, this);
	}

	setData {
		#x_pos, y_pos, x_vel, y_vel, m_accel, height, width = data;
	}

	moved { | argData |
		data = argData;
		this.setData;
		this.changed(\blobMoved);
	}

	checkIfAlive { | blobIdArray |
		if (blobIdArray.includes(id).not) { this.died; }
	}

	died {
		blobs.blobs[id] = nil;
		this.changed(\blobEnded);
	}
}

BlobWatcher {
	var <blobs, <>startFunc, <>moveFunc, <>endFunc, <>mapper;
//	var <myBlobs;

	*new { | startFunc, moveFunc, endFunc, mapper, blobs |
		^this.newCopyArgs(blobs ?? { Blobs.default },
			startFunc ?? {{ | ... args | postf("started: %\n", args) }},
			moveFunc ?? {{ | ... args | postf("moved: %\n", args) }},
			endFunc ?? {{ | ... args | postf("ended: %\n", args) }},
			mapper ?? { BlobMapper() },
		).init;
	}

	init {
		this.addNotifier(blobs, \blobStarted, { | blob | this.addBlob(blob); });
	}

	free {
		this.changed(\watcherEnded);
		this.objectClosed;
	}

	addBlob { | blob |
		var blobLife;
//		myBlobs = myBlobs add: blob;
		blobLife = BlobLife(blob, this, startFunc, moveFunc, endFunc);
		this.addNotifier(blob, \blobMoved, { blobLife.move });
		this.addNotifier(blob, \blobEnded, { blobLife.end });
		blobLife.addNotifier(this, \watcherEnded, {
//			myBlobs remove: blob;
			blobLife.end;
		});
	}

	x_pos { | blob | ^mapper.x_pos(blob) }
	y_pos { | blob | ^mapper.y_pos(blob) }
	x_vel { | blob | ^mapper.x_vel(blob) }
	y_vel { | blob | ^mapper.y_vel(blob) }
	m_accel { | blob | ^mapper.m_accel(blob) }
	height { | blob |  ^mapper.height(blob) }
	width { | blob | ^mapper.width(blob) }
}

BlobLife {
	var <blob, <watcher, <>startFunc, <>moveFunc, <>endFunc, <state;

	*new { | blob, watcher, startFunc, moveFunc, endFunc |
		^this.newCopyArgs(blob, watcher, startFunc, moveFunc, endFunc).init;
	}

	init { state = startFunc.(blob, this) }
	move {  moveFunc.(this) }
	end {
		endFunc.(this);
		this.objectClosed;
	}

	// access to blob data and to state (synth or other process or data):
	set { | ... args | state.set(*args); }
	free { state.free; }
	x_pos { ^watcher.x_pos(blob) }
	y_pos { ^watcher.y_pos(blob) }
	x_vel { ^watcher.x_vel(blob) }
	y_vel { ^watcher.y_vel(blob) }
	m_accel { ^watcher.m_accel(blob) }
	height { ^watcher.height(blob) }
	width { ^watcher.width(blob) }
}

BlobMapper {
	var <>x_pos_map, <y_pos_map, <x_vel_map, <y_vel_map, <m_accel_map, <height_map, <width_map;

	x_pos { | blob | ^x_pos_map.map(blob.x_pos) }
	y_pos { | blob | ^y_pos_map.map(blob.y_pos) }
	x_vel { | blob | ^x_vel_map.map(blob.x_vel) }
	y_vel { | blob | ^y_vel_map.map(blob.y_vel) }
	m_accel { | blob | ^m_accel_map.map(blob.m_accel) }
	height { | blob | ^height_map.map(blob.height) }
	width { | blob | ^width_map.map(blob.width) }
}