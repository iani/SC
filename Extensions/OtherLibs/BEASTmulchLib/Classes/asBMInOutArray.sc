+ Bus {

	asBMInOutArray {|name|
		var offset;
		name.isNil.if({
			name = "ctrlbus";
			offset = index; // actual numbers for simple case
		}, { offset = 1; }); // otherwise start at 1
		^numChannels.collectAs({|channum| 
			(name.asString ++ "-" ++ (channum + offset)).asSymbol->(index + channum)
		 }, BMInOutArray)
	}
} 

+ Server {

	insAsBMInOutArray {
		^options.numInputBusChannels.collectAs({|i|
			(name ++ "In-" ++ (i + 1)) -> (i + options.numOutputBusChannels)
		}, BMHardwareInputsProxy);
	
	}
	
	outsAsBMInOutArray {
		^options.numOutputBusChannels.collectAs({|i|
			(name ++ "Out-" ++ (i + 1)) -> i
		}, BMInOutArray);
	
	}

}