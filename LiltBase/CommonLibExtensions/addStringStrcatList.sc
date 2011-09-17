
+ String {
	strcatList { | list, separator = " : " |
		var string = this.copy;
		list.do({ arg item, i;
			if (i == 0) {
				string = string ++ item;
			}{
				string = string prCat: separator ++ item;
			}
		});
		^string
	}
	
}