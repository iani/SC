//This file is part of The PCSet Library. Copyright (C) 2004  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file PCSetLibrary.help

//PCSet 2/1/04

//multiple representations for the data as a n-bit code 
//as a binary array of the bits of the code
//as an array of pitch classes
//as a difference set

PCSet {
	var <>code, <>array; 
	
	*newFromCode { arg code,n=12;
		var make;
		
		make=super.new.code_(code);
		make.array_(make.arrayFromCode(n));
		^make
	}
	
	//new from pcarray most likely
	*new { arg pca,n=12;
	    var make,code,array;
	
		make=super.new;
		array= Array.fill(n,0);
		pca.do({arg val; array[val]=1});
		
		make.array_(array);
		make.code_(make.codeFromArray);
	
	    ^make
	}
	
	*newFromArray { arg array;
		var make;
		
		make=super.new.array_(array);
		make.code_(make.codeFromArray);
	
		^make
	}
	
	
	//assumes only have array
	codeFromArray {
		var n, c;
		
		n=array.size; //make an n-bit number
		
		c= 0;
		array.do({arg val,j; if(val>0.5,{c=c+(2**(n-1-j))}) });
	
		^c
	}
	
	//assumes only have code
	arrayFromCode { arg n;
		var test;
		
		test= (2**n).asInteger;
		
		^Array.fill(n, { arg i;  
			test=test.rightShift;
			if(code.bitAnd(test)>0.5,1,0);
		});
	}
	
	//return the standard pcset 
	pcarray {
		var list;
		
		list=List.new;
		
		array.do({
		arg val,j;
		if(val>0.5,{list.add(j)});
		});
	
		^list.asArray;
	}
	
	//successive differences representation
	differenceSet {
		var pca,temp,size;
		
		pca=this.pcarray;
		
		if(pca.isEmpty,{^[]});
		
		temp= pca.rotate(-1);
		//would always be zero? not necessarily
		size=pca.size-1;
		temp[size]= temp[size]+(array.size);
	
		^(temp-pca);
	}
	
	
	//could be moved to separate class
	//augmentation operation support- produces the set of augmented arrays of size +1
	//see RCRead Every One a Winner (1978) 
	augment
	{
		var index;
			
		//find first 1 from end of array
		index=0;
		array.do({arg val, j;  if(val>0.5,{index=j;});});
		
		^Array.fill(array.size-1-index,{arg i; 
			var temp; 
			
			temp= (array.deepCopy);
			temp[index+i+1]=1;
			
			temp
		 });
		
	}
	
	//transpose, inverse
	//complement  1-array 
	
	complement {
		array=1-array;
		code=((2**(array.size)-1)-code); //binary complement
		//code=this.codeFromArray; 
	}
	
	
	//NOTE this is prime form according to the RC Read coding not necessarily the Forte prime form
	primeform {
		var temp;
		temp=SetClass(this).rep;
		array=temp.array;
		code=temp.code;
	}
	
}


