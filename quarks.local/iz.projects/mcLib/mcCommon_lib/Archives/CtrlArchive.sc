ArchiveBase {
	
	var <name, <>dir, <mDict;
	
	*unixPst { ^false }
	*new{|name, dir| 
		^super.newCopyArgs(name, dir).init
	}
	init {
		this.clear;
		dir !? { this.checkDir };
	}
	clear {
		mDict = MultiLevelIdentityDictionary.new;
	}
	// the rest goes like this, mimicking a MultiLevelIdentityDictionary
	doesNotUnderstand{|selector ...args|
		^mDict.performList(selector, args)
	}
	read {|filename|
		var expandedFileName = filename ?? { dir +/+ (name  ++ ".sctxar") } ;
		if (File.exists(expandedFileName)) {
			if (expandedFileName.endsWith(".scar")) {
				mDict = this.class.readBinaryArchive(expandedFileName)
			}{
				mDict = this.class.readArchive(expandedFileName)
			};
			if (mDict.isNil) { mDict = MultiLevelIdentityDictionary.new }
		}{ 
			this.logln("¥file:" + expandedFileName + "does not exist", 1);
		}
	}
	write { // maybe writing asCompileString more efficient here...
		var expandedFileName = dir +/+ (name  ++ ".sctxar");
		mDict.writeArchive(expandedFileName);
	}
	checkDir {
		var callback = {|ok, dir| if (ok) { this.registerForShutdown 
			}{ this.logln("¥cannot create dir:" + dir, 1) } };
		if (File.exists(dir).not) {this.createFileDir(dir, callback)} {this.registerForShutdown }
	}
	createFileDir {|dir, cb|
		if (thisProcess.platform.isKindOf(UnixPlatform).not) {
			("*¥do not know how to create a new folder on this platform").warn;
			this.callback(cb, false, dir);
		}{
			("mkdir -p -v" + dir.escapeChar($ )).unixCmd({|res|
				if (res != 0) { ("*¥ could not create directory:" + dir).warn; 
					this.callback(cb, false, dir); } { this.callback(cb, true, dir) }
			}, this.class.unixPst.postln )
		}
	}
	callback {|callback ...args| //callback must be either a Symbol or Function
		if (callback.isKindOf(Symbol)) {this.perform(callback, *args)} {callback.value(*args)} 
	}
	registerForShutdown {
		UI.registerForShutdown( {this.write} )
	}
}

CtrlArchive : ArchiveBase {

	// just provide the full method interface with arg names
	softPutAt {|path, param, val, within = 0.025, mapped = true, lastVal, spec|
		^mDict.softPutAt(path, param, val, within, mapped, lastVal, spec);
	}	
	softSetAt {|path, param, val, within = 0.025, mapped = true, lastVal, spec| 
		mDict.softPutAt(path, param, val, within, mapped, lastVal, spec);
	}
}

/*
a = CtrlArchive.new(\test, FourierScratching.fs_initPath +/+ "CtrlArchives")
a.mDict.isEmpty
a.read
a.isEmpty
a.softPutAt([\a, \b], \amp, 0.2002)
a.softSetAt([\a, \b], \amp, 0.2001)

a.softSetAt([\a, nil], \amp, 0.2001)


a.write
FourierScratching.fs_initPath +/+ "CtrlArchives"

*/

+ MultiLevelIdentityDictionary {
	
	softPutAt {|path, param, val, within = 0.025, mapped = true, lastVal, spec| 
		var curVal, curValNorm, newValNorm, lastValNorm, maxDiff;
		
		path = path.add(param);

		curVal = this.atPath(path);
		spec = (spec ? param).asSpec;
				
		if (curVal.isNil or: spec.isNil) { 
			this.putAtPath(path, val);
			^true
		};

		curValNorm = spec.unmap( curVal );
		maxDiff = max(within, spec.step);

		if (mapped) {
			newValNorm = spec.unmap(val);
			if (lastVal.notNil) { lastValNorm = spec.unmap(lastVal) };
		} {
			newValNorm = val;
			lastValNorm = lastVal;
			val = spec.map(val);
		};
				
		if (
			(newValNorm.absdif(curValNorm) <= maxDiff)   // new val is close enough
									// or controller remembers last value it sent.
			or: { lastValNorm.notNil and: { curValNorm.absdif(lastValNorm) <= maxDiff } })
		{
			this.putAtPath(path, val);
			^true
		} {
			^false
		}
	}
	
	softSetAt {|path, param, val, within = 0.025, mapped = true, lastVal, spec| 
		this.softPutAt(path, param, val, within, mapped, lastVal, spec);
	}
}