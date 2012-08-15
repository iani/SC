/* IZ Wed 08 August 2012  1:01 PM EEST

Draft for an algorithm that prevents jumping too fast to a new midi value when a parameter is mapped to a MIDI controller input whose value is previously set to a value very different from that of the current value of the parameter. 

Trying two variants here. I think the classic "catch" algorithm is better after all. 

a = PickUp.new;

{ a approximate: 1 } ! 10 ++ ({ a approximate: 0 } ! 10)

b = PickUp.new;

{ | i | [i + 4 / 10, b.approximate(i + 4 / 10)] } ! 10;


*/

PickUp {
	var <>value = 0;

	approximate { | newValue = 1 |
//		[value, newValue, (newValue - value)].postln;
		if ((value - newValue).abs > 0.14) {
			^value = value + (newValue - value / 5);
		}{
			^value = newValue;
		}
	}
	
	catch { | newValue = 1 |
		if ((value - newValue).abs > 0.1) {
			^value;
		}{
			^value = newValue;
		}
	}	
}
