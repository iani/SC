/* (IZ 2008-04-23)

To create a buffer used exclusively by a script, use the message allocBuffer.

~script.allocBuffer(bufName, numFrames, numChannels, server)

The buffer is stored in the environment variable given by bufName.  The default bufname is 'buffer'.
The server defaults to ~script.session.server


*/


~script.allocBuffer;

~start {
	~buffer.postln;
};

