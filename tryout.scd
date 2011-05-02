FFTsynthPoller(\test).start.rate = 0.210;

NotificationCenter.register(\test, \fft, \testing, { | data | data.postln });
NotificationCenter.unregister(\test, \fft, \testing);

FFTsynthPoller(\test).stop;



UniqueBuffer.onServer.do: 

Buffer(Server.default, bufnum: 0).play;

FFTpollSynth(\test).buffer;
FFTpollSynth(\test).bufSize;


UniqueBuffer.onServer collect: { | b | [b.key[2], b.object.numFrames] };
UniqueObject.objects.leaves;
rsynca
.stop;

b = UniqueBuffer(\test);
b.inspect;
b.bufnum;
c = b.object;

c.getn(0, 1024);
ServerReady

UniqueBuffer.onServer do: _.postln;

Buffer.alloc(Server.default, 1024, 1);

Synth(\fft, [\in, 0, \buf, 6]);

booting 57110
Number of Devices: 4
   0 : "Built-in Microphone"
   1 : "Built-in Input"
   2 : "Built-in Output"
   3 : "Aggregate Device"

"Built-in Microphone" Input Device
   Streams: 1
      0  channels 2

"Built-in Output" Output Device
   Streams: 1
      0  channels 2

SC_AudioDriver: sample rate = 44100.000000, driver's block size = 512
SuperCollider 3 server ready..
notification is on
initLoadChain for ServerReady waiting 3 seconds before starting synths
uniquesynth makeobject: target Group(1), defname a Function, args [ bufnum, nil ], addaction addToHead, otherArgs[ 0, 0.02 ]

init FFTpollSynth bufSize: 1024
init FFTpollSynth buffer created: UniqueBuffer(test, nil)
an Udef preparing to load
FFTpollSynth init, posting buf args sent to fft synth(def): key, buffer
deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- deferred creation of synth by 1 second! --- --------------
FFTsynthPoller(test, nil)
buf
UniqueBuffer(test, nil)
an Udef  SENT TO SERVER
UniqueBuffer(test, nil) makeObject, allocating: numFrames : 1024, numCHannels: 1
server, numframes, numchannels follow
localhost
1024
1
UBuf allocation succeeded : Buffer(6, 1024, 1, nil, nil)
initLoadChain for ServerReady waiting 3 seconds before starting synths
uniquesynth makeobject: target localhost, defname fft, args [ in, 0, buf, UniqueBuffer(test, Buffer(6, 1024, 1, 44100, nil)) ], addaction addToHead, otherArgs[  ]
uniquesynth prMakeObject: target localhost, defname fft, args [ in, 0, buf, UniqueBuffer(test, Buffer(6, 1024, 1, 44100, nil)) ], addaction addToHead
FFTBase_Ctor error: buffer size (1521788) not a power of two.
fftbuf: Buffer(6, 1024, 1, 44100, nil), bufSize: 1024, bufnum, 6
fftbuf: Buffer(6, 1024, 1, 44100, nil), bufSize: 1024, bufnum, 6
fftbuf: Buffer(6, 1024, 1, 44100, nil), bufSize: 1024, bufnum, 6
fftbuf: Buffer(6, 1024, 1, 44100, nil), bufSize: 1024, bufnum, 6

FFTsynthPoller(test, [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, ...etc...

[ UniqueBuffer(a11wlk01, Buffer(5, 188893, 1, 44100, sounds/a11wlk01.wav)), UniqueBuffer(Belugawhales, Buffer(4, 2547708, 1, 44100, /Users/iani/sounds/osmosis_sounds/seals_normalized/Belugawhales.aiff)), UniqueBuffer(Humpbacks, Buffer(3, 2660862, 2, 44100, /Users/iani/sounds/osmosis_sounds/seals_normalized/Humpbacks.aiff)), UniqueBuffer(elephant_seal, Buffer(2, 1284476, 1, 32000, /Users/iani/sounds/osmosis_sounds/seals_normalized/elephant_seal.aiff)), UniqueBuffer(leo_wed, Buffer(1, 1829374, 2, 44100, /User...etc...

ERROR: Message 'name' not understood.
RECEIVER:
Instance of UniqueBuffer {    (18BF8650, gc=EC, fmt=00, flg=00, set=03)
  instance variables [7]
    key : instance of Array (0x1ba3cfb0, size=3, set=2)
    object : instance of Buffer (0x1ba2c550, size=7, set=3)
    server : instance of Server (0x1621c4c0, size=45, set=6)
    path : "sounds/a11wlk01.wav"
    startFrame : Integer 0
    numFrames : Integer 188893
    numChannels : Integer 1
}
ARGS:
CALL STACK:
	DoesNotUnderstandError:reportError   1BA20A30
		arg this = <instance of DoesNotUnderstandError>
	Nil:handleError   1BA1F410
		arg this = nil
		arg error = <instance of DoesNotUnderstandError>
	Thread:handleError   1BA20850
		arg this = <instance of Thread>
		arg error = <instance of DoesNotUnderstandError>
	Object:throw   1BA20610
		arg this = <instance of DoesNotUnderstandError>
	Object:doesNotUnderstand   1BA20310
		arg this = <instance of UniqueBuffer>
		arg selector = 'name'
		arg args = [*0]
	< closed FunctionDef >   1BA205B0
		arg b = <instance of UniqueBuffer>
	< FunctionDef in Method Collection:collectAs >   1BA202B0
		arg elem = <instance of UniqueBuffer>
		arg i = 0
	ArrayedCollection:do   1BA204F0
		arg this = [*6]
		arg function = <instance of Function>
		var i = 0
	Collection:collectAs   1BA2F310
		arg this = [*6]
		arg function = <instance of Function>
		arg class = class Array
		var res = [*0]
	Interpreter:interpretPrintCmdLine   15AD1880
		arg this = <instance of Interpreter>
		var res = nil
		var func = <instance of Function>
		var code = "UniqueBuffer.onServer collec..."
		var doc = <instance of CocoaDocument>
	Process:interpretPrintCmdLine   18A0A570
		arg this = <instance of Main>

[ [ a11wlk01, 188893 ], [ Belugawhales, 2547708 ], [ Humpbacks, 2660862 ], [ elephant_seal, 1284476 ], [ leo_wed, 1829374 ], [ elephant_seal2, 1521788 ] ]

1521788
