//This file is part of The PCSet Library. Copyright (C) 2004  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file PCSetLibrary.help

//SetClass 2/1/04

//equivalence classes under action of groups, standard in modulo music theory mathematics 
//being Zn and Dn or T, TnI

//make as SetClass with global object for which group to apply

SetClass
{
	classvar <>group,<>mult;
	var <>rep;
	
	*initClass
	{
	group= \expandTnI;	//could be \expandT or \expandTnImult perhaps
	mult=5;
	}
	
	*new { arg pcsetrep;
		var make;
		
		make=super.new;
		make.rep_(make.findRep(pcsetrep)); 
		^make;
	}
	
	findRep {arg pcset;
		var options,max, result;
		
		//n possible transpositions
		options= this.perform(group, pcset);
	
		//find maximal code;
		max=pcset.code; result=pcset;
		options.do({arg pcs; if(pcs.code>max, {
		result= pcs; max=result.code; 
		});});
		
		^result
	}
	
	//could find max quicker by looking for biggest contiguous run of 1's but
	//makes coding more complex and possibly no real advantage
	
	//override in derived classes for different group structures
	expandT { arg rep;	//doesn't have to be maxcode representative, any rep will do!  
	
		//n possible transpositions
		^Array.fill(rep.array.size,{ arg i; 
		
		PCSet.newFromArray(rep.array.rotate(i));
		});
		
		//should remove transpositional duplicates? not for now but asSet.asArray would do it
	}

	expandTnI { arg rep;
		var array, siz;
	
		siz= rep.array.size;
		
		//2*n possible transpositions and inversions
		^Array.fill(2*siz,{ arg i; 
		var arr;
		
		arr= rep.array.rotate(i);
		if(i<siz,{arr=arr.reverse;});	//invert the first half
		
		PCSet.newFromArray(arr);
		});
	}
	
	expandTnImult { arg rep;
		var array, siz,mulorbitsiz,mul;
	
		siz= rep.array.size;
		
		mul=SetClass.mult;
		mulorbitsiz= (siz/(mul.gcd(siz))).asInteger;
		
		//2*n possible transpositions and inversions
		array=Array.fill(2*siz,{ arg i; 
		var arr,pca;
		
		arr= rep.array.rotate(i);
		if(i<siz,{arr=arr.reverse;});	//invert the first half
			
		pca=PCSet.newFromArray(arr).pcarray;
		
		Array.fill(mulorbitsiz,{arg i; 
		var pca2;
		
		pca2=((pca.copy)*((mul**i).asInteger))%siz;
		
		PCSet.new(pca2,siz)
		});
		});
		
		^array.flatten;
	}


}
