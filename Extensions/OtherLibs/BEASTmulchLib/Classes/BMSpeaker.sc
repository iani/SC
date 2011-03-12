
BMSpeaker {
	classvar rad2deg, deg2rad;
	var <name; // matches speaker taxonomy we've hashed out
	
	var <>index; // SC output
	
	// cartesian
	var <x, <y, <z; // in meters; for 2D arrays z = 0;
	var <>spec; // instance of SpeakerSpec, contains shared info like freq range, etc.
	
	var <>description; // human readable text
	var <>directivity = 'direct'; // symbol, either 'direct' or 'reflected'
	
	// spherical coords, angles (in degrees) from a central point
	var <azi; // from median plane +/- 180 deg 
	var <ele; // above azimuthal plane
	var <rad; // in meters from (0, 0, 0), which should be from head height at audience centre
	
	// dBFS cut populated by auto balncing function. This may be arbitrarily low, 
	// so it should only be used for comparison purposes unless normalised across an array
	var <>autoTrim = 0;
	
	*new {|name, index, x = 1, y = 1, z = 1, spec = 'Generic'|
		^super.newCopyArgs(name.asSymbol, index, x, y, z, BMSpeakerSpec.specs[spec.asSymbol]).init;
	}
	
	*newFromSpherical {|name, index, azi = 0, ele = 0, rad = 1, spec = 'Generic'|
		^super.new.initFromSpherical(name.asSymbol, index, azi, ele, rad, BMSpeakerSpec.specs[spec.asSymbol]);
	}
	
	initFromSpherical{|argName, argInd, azimuth, elevation, radius, argSpec|
		name = argName;
		index = argInd;
		azi = azimuth;
		ele = elevation;
		rad = radius;
		spec = argSpec;
		this.calcCartesian;
	}
	
	calcCartesian {
		var azrad, elrad;
		azrad = azi * deg2rad;
		elrad = ele * deg2rad;
		x = rad * cos(elrad) * sin(azrad);
		y = rad * cos(elrad) * cos(azrad);
		z = rad * sin(elrad);
	}
	
	*initClass { 
		rad2deg = 360.0 / ( 2 * pi );
		deg2rad = (2 * pi / 360);	
	}
	
	init {
		azi = atan2(x, y) * rad2deg;
		rad = (x.squared + y.squared + z.squared).sqrt;
		ele = atan2(z, hypot(x, y)) * rad2deg;
	}
	
	name_ {| newName | 
		name = newName.asSymbol;
	}
		   
	//index_ {| new | index = new }
	x_ {| new | x = new; this.init; this.changed(\newCoordinate) }
	y_ {| new | y = new; this.init; this.changed(\newCoordinate) }
	z_ {| new | z = new; this.init; this.changed(\newCoordinate) }
	
	azi_{| new | azi = new; this.calcCartesian; this.changed(\newCoordinate) }
	ele_{| new | ele = new; this.calcCartesian; this.changed(\newCoordinate) }
	rad_{| new | rad = new; this.calcCartesian; this.changed(\newCoordinate) }
	
	asUGenInput { ^index }
	asControlInput { ^index }
	
	isBMSpeaker { ^true }
	
	key {^name }
	
	// post pretty
	printOn { arg stream; stream << this.class.name << "(" <<* [name, index, spec.name] << ")" }
	
	// update any changes in spec
	initFromArchive { spec = BMSpeakerSpec.specs[spec.name] ?? BMSpeakerSpec.specs['Generic'] }
}

// Wrapper class for managing specs for different speaker models
// speakerspecs are singletons: There can only be one of each name
// creating a new one of an existing name overwrites

// the only required field is 'name' so any use of these should deal appropriately with nil values
BMSpeakerSpec {
	
	classvar <specs, protoVals;
	var <name, vals;

	*new { | name, vals | // vals is an event or other Dictionary subclass
		specs[name].notNil.if({ ("Overwriting Speaker Spec " ++ name).warn; });
		^super.newCopyArgs(name.asSymbol, (vals ? ()).as(Event)).init;
	}
	
	init {
		vals.proto = protoVals;
		this.class.specs[this.name] = this;
	}
		
	*initClass {
		protoVals = (brand: 'Unknown', fullRange: true); // be careful with this
		StartUp.add{ 
			 specs = IdentityDictionary.new;
			 
			 // spl is continuous at 1m
			 // plugins is [[specname, presetname], ...]
			 BMSpeakerSpec('Generic', (brand: 'Unknown', minFreq: 20, maxFreq: 20000, spl: nil, powered: false, fullRange: true));
			 BMSpeakerSpec('Generic Sub', (brand: 'Unknown', minFreq: 20, maxFreq: 85, spl: nil, powered: false, fullRange: false));
			 BMSpeakerSpec('SCM50', (brand: 'ATC', minFreq: 38, maxFreq: 20000, spl: 112, powered: false, plugins: [[\Highpass, \atcs]], fullRange: true));
			 BMSpeakerSpec('8030A', (brand: 'Genelec', minFreq: 58, maxFreq: 20000, spl: 97, powered: true, fullRange: true));
			 BMSpeakerSpec('8040A', (brand: 'Genelec', minFreq: 48, maxFreq: 20000, spl: 99, powered: true, fullRange: true));
			 BMSpeakerSpec('8050A', (brand: 'Genelec', minFreq: 38, maxFreq: 20000, spl: 101, powered: true, fullRange: true));
			 BMSpeakerSpec('1037C', (brand: 'Genelec', minFreq: 37, maxFreq: 21000, spl: 107, powered: true, fullRange: true));
			 BMSpeakerSpec('1038B', (brand: 'Genelec', minFreq: 35, maxFreq: 20000, spl: 120, powered: true, fullRange: true));
			 BMSpeakerSpec('1032A', (brand: 'Genelec', minFreq: 42, maxFreq: 21000, spl: 113, powered: true, fullRange: true));
			 BMSpeakerSpec('1037A', (brand: 'Genelec', minFreq: 39, maxFreq: 21000, spl: 106, powered: true, fullRange: true));
			 BMSpeakerSpec('1029A', (brand: 'Genelec', minFreq: 70, maxFreq: 18000, spl: 98, powered: true, fullRange: true));
			 BMSpeakerSpec('7070A', (brand: 'Genelec', minFreq: 19, maxFreq: 85, spl: nil, powered: true, fullRange: false));
			 BMSpeakerSpec('1094A', (brand: 'Genelec', minFreq: 29, maxFreq: 80, spl: nil, powered: true, fullRange: false));
			 BMSpeakerSpec('Circle5', (brand: 'HHb', minFreq: 48, maxFreq: 20000, spl: 87, powered: false, fullRange: true));
			 BMSpeakerSpec('Circle3', (brand: 'HHb', minFreq: 70, maxFreq: 20000, spl: 83, powered: false, fullRange: true));
			 BMSpeakerSpec('Volt', (brand: 'Wilmslow Audio', minFreq: 35, maxFreq: 30000, spl: 88, powered: false, fullRange: true));
			 BMSpeakerSpec('Lynx', (brand: 'Tannoy', minFreq: 50, maxFreq: 20000, spl: 95, powered: false, fullRange: true)); // spl assumes two coupled... thanks Tannoy
			 BMSpeakerSpec('MC24', (brand: 'APG', minFreq: 60, maxFreq: 20000, spl: 99, powered: false, fullRange: true)); // spl @ 1W / 1 meter
			 // KSN1005 nominal spl 95
			 BMSpeakerSpec('Tweeters', (brand: 'Motorola', minFreq: 10000, maxFreq: 27000, spl: nil, powered: false, plugins: [[\Highpass, \tweeters]], fullRange: false));
			 BMSpeakerSpec('Tannoy', (brand: 'Tannoy', minFreq: 40, maxFreq: 20000, spl: 96, powered: false, fullRange: true));
			 BMSpeakerSpec('UREI-809', (brand: 'Urei', minFreq: 50, maxFreq: 17500, spl: 93, powered: false, fullRange: true));
			 BMSpeakerSpec('Kef-C20', (brand: 'Kef', minFreq: 72, maxFreq: 20000, spl: 90, powered: false, fullRange: true));
		 }	
	 }
	
	doesNotUnderstand { arg selector ... args;
		^vals.perform(selector, *args); // so nil if not there, vals if setter
	}
	
	// post pretty
	printOn { arg stream; stream << this.class.name << "(" <<* [name, vals] << ")" }
	
}

