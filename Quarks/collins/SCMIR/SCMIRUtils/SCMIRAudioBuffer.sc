/* IZ 120726 
Utilities for handling SCMIRAudioFile instances via GUI

SCMIRAudioBuffer.load;

SCMIRAudioBuffer.all;

SCMIRAudioBuffer.all.at('weddell1').plotFeatures;

w = SCMIRAudioBuffer.get('weddell1');

w.plotFeatures;
w.inspect;
w.featuredata;
w.buffer;
w.featuredata.size;
w.buffer.numFrames / w.featuredata.size;

BufferResource.loadPaths("/Users/iani2/Music/sounds/osmosis_sounds_wav/seals_normalized/*.wav".pathMatch);

w.buffer.play;

x = w.similarityMatrix;
n = w.novelty(x, 30);
n.normalize.plot;
l = w.findSections;


SCMIRAudioBuffer.load(Onsets);

BufferResource

*/

SCMIRAudioBuffer : SCMIRAudioFile {
	classvar <all;
	classvar <buffers;

	var <buffer; 	// holds the audio buffer for various work stuff;

	*initClass {
		StartUp add: {
			all = IdentityDictionary.new;
		};
	}

	*load { | ... features |
		if (features.size == 0) { features = [[MFCC, 13], [Chromagram, 12]] };
		Dialog.getPaths({ | path |
			var sf;
			path = path.first;
			sf = this.new(path, features);
			{
				sf.extractFeatures;
				"loading buffer".postln;
				sf.loadBuffer;
			}.fork;
			all.put(path.basename.splitext.first.asSymbol, sf);
		});
	}
	
	*get { | name | ^all.at(name.asSymbol) }
	
	loadBuffer { | name |
		buffer = Buffer.read(Server.default, sourcepath);
	}
}