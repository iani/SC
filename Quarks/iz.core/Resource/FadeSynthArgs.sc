
FadeSynthArgs : Event {
	*new { | argArgs |
		^super.new.init(argArgs);
	}
	
	init { | argArgs |
		argArgs pairsDo: { | key, value | this.[key] = value; }
	}
	
	addArgsFromDesc { | desc |
		var controls, name, hasGate = false;
		controls = desc.controls;
		controls do: { | c, i |
			name = c.name.asSymbol;
			if (name == \gate) { hasGate = true };
			if (this[name].isNil) { this[name] = c.defaultValue };
		};
		^hasGate;
	}
	
	fadeSynthArgs {
		// return array of args used to start synth
		var synthArgs;
		synthArgs = Array.newClear(this.size * 2);
		this keysValuesDo: { | key, value, i |
			i = i * 2;
			synthArgs[i] = key;
			synthArgs[i + 1] = value;
		};
		^synthArgs;
	}
}

