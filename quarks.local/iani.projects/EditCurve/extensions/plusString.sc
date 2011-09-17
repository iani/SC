+ String {

	asMidiPitch {
		var note = this.split($_).at(0);
		var oct = this.split($_).at(1).asInteger;
		^oct + 2 * 12 + case 	{note == "Do"}	{0}
						 	{note == "Do#"}	{1}
							{note == "Re"}	{2}
							{note == "Re#"}	{3}
							{note == "Mi"}	{4}
							{note == "Fa"}	{5}
							{note == "Fa#"}	{6}
							{note == "Sol"}	{7}
							{note == "Sol#"}	{8}
							{note == "La"}	{9}
							{note == "La#"}	{10}
							{note == "Si"}	{11};
	}

}

+ Collection {
	
	asMidiPitch {
		^this.collect({
			arg string;
			if (string.isString)
				{string.asMidiPitch;}
				{string;};
		});
	}

}
			

