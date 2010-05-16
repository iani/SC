/* IZ Sunday; September 21, 2008: 8:30 AM

Global settings for the B2 application.

*/

B2 {
	classvar <vvvv_address;
	classvar <videos;
	classvar <photos;
	classvar <audio_samples;
	*initClass {
		vvvv_address = NetAddr("192.168.5.66", 9001);
	}
}

