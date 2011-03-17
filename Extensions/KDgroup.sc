/* IZ 2011 0317

managing a group of synths which outputs audio on the 43 channels of the Klangdom 

Things to provide: 

- Global fade-in, fade-out, volume control. 
- Volume control for individual channels
- ... other utils such as: 
  - Creating the array of 43 synths
  - Accessing the array of 43 synths
  - Volume control with x-y PanAz rings on the 43 channels of the dome
  - Adding effects
  - Adding control bus arrays that algorithmically control an array of parameters

Implementation notes: 

It is impractical to do the amplitude scaling on a separate synth, because that would
require 43 audio channels for each KD43 instance, in order to send the outputs of the
sources to the amplitude scaling synth. Therefore we do the volume scaling exclusively by
setting the vol or amp parameter of the synths. (The name of the parameter could be
dynamically customizable, but there must be one). 


*/

KDgroup {
	var synths;		// the 43 synths
	var controllers; 	// dictionary of processes driving parameters of the
	    			// synths. One of these can be a KDPan43 volume panner
	
	*new { | synthfunc ... controllers |
	     // synthfunc { | i, kdgroup | ... } : create the ith synth. 
	     // controllers: instances of KD43ctl or its subclasses
	}

}