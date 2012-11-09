/* IZ 12 08 01 
Notify system when node state changes to enable gui and other updates
*/

+ BusPlug { // superclass of NodeProxy
	play { | out, numChannels, group, multi=false, vol, fadeTime, addAction |
		var bundle = MixedBundle.new;
		if(this.homeServer.serverRunning.not) {
			("server not running:" + this.homeServer).warn;
			^this
		};
		this.playToBundle(bundle, out.asControlInput, 
			numChannels, group, multi, vol, fadeTime, addAction
		);
		// homeServer: multi client support: monitor only locally
		bundle.schedSend(this.homeServer, this.clock ? TempoClock.default, this.quant);
		this.changed(\play, out, numChannels, group, multi, vol, fadeTime, addAction);
	}

	stop { | fadeTime = 0.1, reset = false |
		monitor.stop(fadeTime);
		if(reset) { monitor = nil };
		this.changed(\stop, fadeTime, reset);
	}
}

+ NodeProxy {

	clear { | fadeTime = 0 |
		this.free(fadeTime, true); 	// free group and objects
		this.removeAll; 			// take out all objects
		this.stop(fadeTime, true);		// stop any monitor
		monitor = nil;
		this.freeBus;	 // free the bus from the server allocator
		this.init;	// reset the environment
		this.changed(\clear, fadeTime);
	}

	end { | fadeTime, reset = false |
		var dt = fadeTime ? this.fadeTime;
		fork {
			this.free(dt, true);
			(dt + (server.latency ? 0)).wait;
			this.stop(0, reset);
		};
		this.changed(\end, fadeTime, reset);
	}

	free { | fadeTime, freeGroup = true |
		var bundle;
		if(this.isPlaying) {
			bundle = MixedBundle.new;
			if(fadeTime.notNil) { bundle.add([15, group.nodeID, "fadeTime", fadeTime]) };
			this.stopAllToBundle(bundle, fadeTime);
			if(freeGroup) {
				bundle.sched((fadeTime ? this.fadeTime) + (server.latency ? 0), { group.free });
			};
			bundle.send(server);
		};
		this.changed(\free, fadeTime, freeGroup);
 	}

	pause {
		if(this.isPlaying) { objects.do { |item| item.pause(clock, quant) } };
		paused = true;
		this.changed(\pause);
	}

	resume {
		paused = false;
		if(this.isPlaying) { objects.do { |item| item.resume(clock, quant) } };
		this.changed(\resume);
	}

	fadeTime_ { | dur |
		if(dur.isNil) { this.unset(\fadeTime) } { this.set(\fadeTime, dur) };
		this.changed(\fadeTime, dur);
	}

	// setting the source

	put { | index, obj, channelOffset = 0, extraArgs, now = true |		var container, bundle, orderIndex;
 		this.changed(\put, index, obj, channelOffset, extraArgs, now);
		if(obj.isNil) { this.removeAt(index); ^this };
		if(index.isSequenceableCollection) { 						^this.putAll(obj.asArray, index, channelOffset)
		};

		orderIndex = index ? 0;
		container = obj.makeProxyControl(channelOffset, this);
		container.build(this, orderIndex); // bus allocation happens here

		if(this.shouldAddObject(container, index)) {
			bundle = MixedBundle.new;
			if(index.isNil)
				{ this.removeAllToBundle(bundle) }
				{ this.removeToBundle(bundle, index) };
			objects = objects.put(orderIndex, container);
			this.changed(\source, obj, index);
		} {
			format("failed to add % to node proxy: %", obj, this).inform;
			^this
		};

		if(server.serverRunning) {
			now = awake && now;
			if(now) {
				this.prepareToBundle(nil, bundle);
			};
			container.loadToBundle(bundle, server);
			loaded = true;
			if(now) {
				container.wakeUpParentsToBundle(bundle);
				this.sendObjectToBundle(bundle, container, extraArgs, index);
			};
			nodeMap.wakeUpParentsToBundle(bundle);
			bundle.schedSend(server, clock ? TempoClock.default, quant);
		} {
			loaded = false;
		}
	}

	removeAt { | index, fadeTime |
		var bundle = MixedBundle.new;
		if(index.isNil)
			{ this.removeAllToBundle(bundle, fadeTime) }
			{ this.removeToBundle(bundle, index, fadeTime) };
		this.changed(\source, nil, index);
		bundle.schedSend(server);
	}
}

