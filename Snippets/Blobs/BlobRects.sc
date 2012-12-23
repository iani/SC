/* iz Mon 19 November 2012 10:33 AM EET

Start a single process when one of the blobs is within a rectangle, and stop that process when no blobs are within a rectangle.

*/

BlobRects {
	classvar <all;
	var <>blobs;   // Blobs instance that notifies me
	var <>rects;

	*initClass { all = IdentityDictionary() }

	*new { | port = 57120 |
		var instance;
		instance = all[port];
		instance ?? {
			instance = this.newCopyArgs(Blobs(port), Set()).init;
			all[port] = instance;
		};
		^instance;
	}

	init {
		this.addNotifier(blobs, \alive, { | blobArray |
			rects do: _.check(blobArray)
		})
	}

	add { | rect |
		rects add: rect;
	}

	remove { | rect |
		rects remove: rect;
	}
}

BlobRect {
	var <>rects;  // the rects instance that gives me the blobs
	var <>rect; // when a first blob appears within this rectangle, state is set
	var <>startAction, <>moveAction, <>stopAction;
	var <>state; // synth or other process. Stopped when no blobs are within rect
	var <blobsInRect, <blobCenter;

	*new { | port, rect, startAction, moveAction, stopAction |
		^this.newCopyArgs(BlobRects(port), rect, startAction, moveAction, stopAction)
	}

	check { | blobArray |
		blobsInRect = blobArray select: { | b | rect containsPoint: b.pos };
		if (blobsInRect.size > 0) {
			blobCenter = blobArray.collect(_.pos).sum / blobArray.size;
			if (state.isNil) {
				state = startAction.(blobCenter, this, blobArray);
			}{
				moveAction.(blobCenter, this, blobArray);
			}
		}{
			this.stopIfActive;
		}
	}

	stopIfActive {
		state !? { stopAction.(state, this); };
		state = nil;
	}

	enable {
		rects add: this;
		rects.blobs.enable;
	}
	disable {
		this.stopIfActive;
		rects remove: this;
	}

	free { this.disable; }
}

BlobRectArray {
	var <port, <blobRects;
	*new { | port = 57120 ... rectSpecs |
		^this.newCopyArgs(port).init(rectSpecs);
	}

	init { | rectSpecs |
		blobRects = rectSpecs collect: this.makeRect(_);
	}

	makeRect { | spec |
		^BlobRect(port, *spec);
	}

	enable { blobRects do: _.enable }

	disable { blobRects do: _.disable }
	free { this.disable; }

	fork { | func |
		{ func.(this) }.fork;
	}
}

