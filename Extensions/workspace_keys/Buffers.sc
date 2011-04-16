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

	*loadOnce { | ... paths | paths do: { | path | this.default.loadOnce(path) } }
	
	loadOnce { | path | this.load(path, doNotReload: true) }
	
	load { | path, doNotReload = true |
		var buffer;
		if (this[this getNameFromPath: path].notNil) { ^postf("% already loaded\n", path) };
		if (path.pathMatch.size > 0) { 
			buffer = Buffer.read(Server.default, path.asString);
			this[this getNameFromPath: path] = buffer;
		}{
			postf("file not found: %\n", path);
		}
	}
	
	getNameFromPath { | path | ^path.basename.splitext.first.asSymbol }

	*at { | key | ^this.default.at(key) }
	
	*names { ^this.default.keys }
	
}

/*

Buffers.loadDialog;
Buffers.names.asArray.asCompileString;
Buffers.at(\tcp_d1_01_the_swedish_rhapsody_irdial);
*/