/* IZ 080619
For sending and receiving MIDI across an IP network via OSC. 

Usage: 
Suppose there is a MIDI application m1 on a computer c1 that wants to send MIDI messages to another application m2 on a computer c2 (where c1 and c2 may be the same computer). 

Then m1 can send these MIDI messages locally on c1 to a SuperCollider client that runs the present MIDI2OSC utility. The SuperCollider client that receives this MIDI and translates it to OSC will be called here the midi2osc sender. MIDI2OSC running on midi2osc sender will translate each MIDI message received into OSC and will send it to any other designated SuperCollider client computer as OSC messages. The SuperCollider that receives the OSC messages and translates them to MIDI will be called osc2midi receiver. 

The midi2osc sender must know the IP addresses of the osc2midi receivers, to which it will send the OSC messages that result from translating the received MIDI. 

The osc2midi sender must know the MIDI ports to which it will send the MIDI messages that result from translating the received OSC.

*/


MIDI2OSC : NetAddr {
	classvar {}
}


OSC2MIDI {
	var addr;
	var osc_responder;
	var midi_outs;
	
	*new { | addr |
		^super.newCopyArgs(addr).init;
	}
	
	init {
		responder = [\note_on, \note_off, \cc, \pitch_bend, \after_touch] collect: { | cmd_name |
			OSCresponder(addr, cmd_name, { | time, responder, msg |
				midi_outs do: { | out | out.performList(msg) }
			})
		}
	}
		
	activate {
		responder.add;
		this.initMIDI
	}

	initMIDI {
		var destinations;
		MIDIClient.init(0, 128);
		destinations = MIDIClient.destinations;
		midi_outs = destinations collect: { | port, index |
			MIDIOut(index, destinations[index]);
		};
	}

	deactivate {
		responder.remove;
		midi_ports = nil;
	}
}
