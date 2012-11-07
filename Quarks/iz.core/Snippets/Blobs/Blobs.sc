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
	var <blobs, <startFunc, <moveFunc, <endFunc;
	var <myBlobs;

	*new { | startFunc, moveFunc, endFunc, blobs |
		^this.newCopyArgs(blobs ?? { Blobs.default }, startFunc, moveFunc, endFunc).init;
	}

	init {
		startFunc = startFunc ?? {{ | ... args | postf("started: %\n", args) }};
		moveFunc = moveFunc ?? {{ | ... args | postf("moved: %\n", args) }};
		endFunc = endFunc ?? {{ | ... args | postf("ended: %\n", args) }};
		this.addNotifier(blobs, \blobStarted, { | blob | this.addBlob(blob); });
	}

	free {
		myBlobs.copy do: _.changed(\blobEnded);
		this.objectClosed;
	}

	addBlob { | blob |
		var blobLife;
		myBlobs = myBlobs add: blob;
		blobLife = BlobLife(blob, startFunc, moveFunc, endFunc);
		this.addNotifier(blob, \blobMoved, { blobLife.move });
		this.addNotifier(blob, \blobEnded, {
			myBlobs remove: blob;
			this.removeNotifier(blob, \blobMoved);
			this.removeNotifier(blob, \blobEnded);
			blobLife.end;
		});
	}
}

BlobLife {
	var <blob, <>startFunc, <>moveFunc, <>endFunc, <state;

	*new { | blob, startFunc, moveFunc, endFunc |
		^this.newCopyArgs(blob, startFunc, moveFunc, endFunc).init;
	}

	init { state = startFunc.(blob, this) }
	move {  moveFunc.(this) }
	end { endFunc.(this); }

	// access to blob data and to state (synth or other process or data):
	set { | ... args | state.set(*args); }
	free { state.free; }
	x_pos { ^blob.x_pos }
	y_pos { ^blob.y_pos }
	x_vel { ^blob.x_vel }
	y_vel { ^blob.y_vel }
	height { ^blob.height }
	width { ^blob.width }
}