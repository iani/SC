/* iz 080820
Handle sensor input from gluion. 
TODO: rewrite this as subclass of AbstractInputHandler
*/

AnalogSensors : Model {
	var <raw_input; // holds the data as input from gluion, but reordered according to sensor_order
	// sensor order holds the indices for reordering the input data if required.
//	var <>sensor_order = #[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];

	raw_input_ { | input_data |
		raw_input = input_data;
///		raw_input = input_data[sensor_order];
		this.changed(\analog_input, raw_input);
	}
	
	sensor_order_gui {
//		Conductor
	}
}