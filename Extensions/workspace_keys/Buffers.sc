Buffers : IdentityDictionary {
	classvar default;
	
	*new { | x |
		^super.new;	
	}
	*default {
		if (default.isNil) { default = this.new };
		^default;
	}

	*loadDialog {
		Dialog.getPaths({ | paths |
			this.load(*paths);
		});
	}
	
	*load { | ... paths | paths do: { | path | this.default.load(path) } }
	
	load { | path |
		var buffer;
		if (path.pathMatch.size > 0) { 
			buffer = Buffer.read(Server.default, path.asString);
			this[buffer.path.basename.splitext.first.asSymbol] = buffer;
		}{
			
		}
	}

	*at { | key | ^this.default.at(key) }
	
	*names { ^this.default.keys }
	
}

/*

Buffers.loadDialog;
Buffers.names.asArray.asCompileString;
Buffers.at(\tcp_d1_01_the_swedish_rhapsody_irdial);
*/