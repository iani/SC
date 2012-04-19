//This file is part of The PCSet Library. Copyright (C) 2004  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file PCSetLibrary.help

//Orderly Algorithm by R.C.Read implemented by NMC 2/1/04, added stack algorithm 8/1/04

//see R.C.Read (1978) Every One a Winner or How to Avoid Isomorphism Search When Cataloguing Combinatorial Configurations. Annals of Discrete Mathematics 2 pp 107-120

//create all setclasses via augmentation on m-sets up to floor(n/2)
//placing all m-sets in the mth output file
//stores codes only so to save space

//also a hack by myself from my observation that can directly create all m-sets and still avoid isomorphism search!
//takes advantage of the unique coding and maximal code value for the best set class representative
//and now I've worked out how to use a stack to avoid running through all 2**n possible binary .numbers- we're cookin'

//SPECULATIVE- note that for my algorithm you can relax the condition that there be a group transformation
//ie the classes don't need to be equivalence classes (but will then overlap with one another)
//could do TnI*x set classes where multiply by x (ie for n=12, let x=4 not 5!). 
//all that is required is that classes have a unique code for the rep
//and this will always be true. from the nature of the simple binary coding!   
//Should also work for orderly algorithm!


OrderlyAlgorithm {
	var <>n, <>maxm;

	*new { arg n;
		
		^super.new.n_(n).maxm_(n.div(2));
	}

	generate {
		var files;
		var considered, maxfilesize;
		
		considered=1;
		
		files= Array.fill(maxm,{arg i; File("mset"++((i+1).asString)++"n"++(n.asString),"w") });
		
		//note that there is always one 1-set, namely [1,0,..,0], code 2**(n-1)
		//so make this file straight away
		
		files[0].write(Int32Array[(2**(n-1)).asInteger]);
		files[0].close;
				
		//now iterate using the previous file of (m-1)-sets to make the m-sets via the augmenting 		//operation
		
		(maxm-1).do({ arg i;
			var prev, now, cand, len, pcset, rep, poss, keep;
			
			Post << "creating file " << (i+2) <<nl; 
			
			files[i]=File("mset"++((i+1).asString)++"n"++(n.asString),"r");
			prev= files[i];
			now=files[i+1];
			
			len= prev.length;
			
			//iterate through previous file here
			len.div(4).do({ arg j; 
			
				cand= prev.getInt32;
				
				//put this code in another function so can override for generating TSetClasses
				pcset= PCSet.newFromCode(cand,n);
				
				//should be here in this function rather than in PCSet class
				poss= pcset.augment;
				
				keep=List.new;
				
				poss.do({ arg array;
				var test;
				
				test= PCSet.newFromArray(array); 
				rep=SetClass(test).rep;
				if(rep.code==test.code,
				{
				//if canonical, ie maximal code representative, write out
				keep.add(test.code);
				});
				});
				
				considered=considered+(poss.size);
				
				keep=Int32Array.newFrom(keep);
			
				now.write(keep);
			});
			
			now.close;
			prev.close;
		
		});
		
		Post << "considered " << considered<< nl;
	
	}


	pcSpace {
		this.generate;
		^this.getPCSpace;
	}

	//do all createMSets
	getPCSpace {
		^Array.fill(n+1,{arg i; this.getMSets(i)})
	}
	
	//could do two funcs, one for case of complement to avoid slowdown due to if
	getMSets {
		arg m;
		var file, list;
		
		if(m==0,{^[[]]});
		if(m==n,{^[Array.series(n,0,1)]});
		
		//if(m>maxm,{m=n-m;});
		
		file= File("mset"++((if(m>maxm,n-m,m)).asString)++"n"++(n.asString),"r");
		
		if(file.isOpen,1,{"mset file not found"; ^nil});
		
		list=List.new;
		
		file.length.div(4).do({ arg j; 
			var next,set;
			next=file.getInt32;
			set=PCSet.newFromCode(next,n);
			if(m>maxm,{set.complement.primeform;});
			list.add(set.pcarray.asArray);
		});	
		
		^list.asArray;
	}
	
	
		//helper function for the above
	generateCandidates
	{
	arg array, target;
	var siz, list, last1, lev, tofit, left;
	
	lev=array[1];
	last1=array[2];
	array=array[0];
	//list=List.new;
	siz=array.size;
	
	left= siz-1-last1;
	tofit= target-lev;
	
	if(left<tofit,{^List.new});		//could make nil for efficiency and do notNil test above
	
	^List.fill(left-tofit+1,{arg i; var arr,ind;
	ind=last1+1+i;
	arr=array.copy;
	arr[ind]=1;
	
	[arr,lev+1,ind]
	});

	}
	
	
	
	//noted by myself- just create all possible positions of the m 1's and test each for 
	//whether its a representative or not- keep if code maximal 
	//direct generation, doesn't require isomorphism search or the (m-1)-sets!
	//one drawback- doesn't necessarily create them in code order, would have to sort the file in increasing codes
	//easy since file is just a list of numbers!
	//actually seems to generate in reverse code order, untested hypothesis 
	findMSets { arg m;
			var test, array, num, base, file, rep, prog, numbits;
			var stack, ones, digits, pivot, level, penultimate, candidates, survivors;
			//var candconsid, stackmax;
			
			//candconsid=0;
			//stackmax=0;
			
			//stupid cases
			if(m==0,{^[]});
			if(m==n,{^Array.series(n,0,1)});
			if(m==1,{^Array.fill(n,{arg i; if(i==0,1,0)}); });
			if(m==(n-1),{^Array.fill(n,{arg i; if(i<(n-1),1,0)}); });
			
			if(m>maxm,{m=n-m;});
					
			file=File("mset"++(m.asString)++"n"++(n.asString),"w"); 	
			//always put a 1 in leading position	then m-1 1's into other n-1 slots
			//choose m-1 from n-1 			
			
			//see alternative slower algorithms below 
			
			//better algorithm again, using a stack and a few twists to generate the binary numbers with m-1 1's in n-1
			//without enumerating all of them
			
			//not possible that (m-1)>(n-1)/2 due to earlier code
			//DEAL WITH CASE m=1- dealt with initially above
		
			digits=n-1;
			ones=m-1;
			penultimate=ones-1;
			
			//make stack using list and add and pop
			stack= List.new.add([Array.fill(digits,0),0,-1]);
			
			prog=0;
			
			while({not(stack.isEmpty)},
			{
				pivot= stack.pop;
				
				level= pivot[1];
				
				/*
				//update doesn't work anyway...
				if(level==1,{
					//estimate progess
					prog=prog+1;
					Post <<(100*prog/(n-1))<<"%"<<nl; 
					//Post.flush;
				});
				*/
				
				candidates= this.generateCandidates(pivot,ones);
				
				//test all now for canonicity, works because of Read's Theorem page 112-3
				
				survivors=List.new;
				
				candidates.do({arg array;
						test= PCSet.newFromArray([1]++(array[0])); 
							rep=SetClass(test).rep;
							if(rep.code==test.code, {//if canonical
							
							if(level==(penultimate),{
							
								//if canonical, ie maximal code representative, write out
								file.write(Int32Array[test.code]);
								},{survivors.add(array)});
					});
					});
				
				//candconsid= candconsid+(candidates.size);

									
				//generate further nodes for stack tree 
				
					//add to stack in reverse order so biggest code next for popping
					if(level!=(penultimate), {stack= stack++(survivors.reverse)});
					
					//if(stack.size>stackmax,{stackmax=stack.size;});
				
				
			});
		
			file.close;
			
			//Post << "done " << "considered "<< candconsid << " stackmax "<< stackmax<< nl;
	}

	
	
}



//alternative enumeration algorithms, slower but still functional

			//easiest to code is to do the 2**(n-1) possibles rejecting any that have the wrong number of 1's!
			//should be able to do (n-1)! permutations of [1...10...0] no- massive number involved! 2**(n-1) much smaller
			
			//num=(2**(n-1)).asInteger;
			
			//num=1;
			//get n-1!
			//(n-2).do({arg i; num=num*(i+2)});
			//base array to permute
			//base=(Array.fill(m-1,1))++(Array.fill(n-m,0));
			//array=[1]++(base.permute(i));
			//
//			prog=(num.div(100)).max(1000);
//				
//			num.do({ arg i;
//				
//				if(i%prog==0,{
//				Post << "progress " << ((i/num)*100) <<"%" << nl;
//				//Post.flush; //flush fails!
//				});
//				
//				//same as PCSet internal code
//				test= num;
//		
//				numbits=1;
//				array= Array.fill(n-1, {  
//				test=test.rightShift;
//				if(i.bitAnd(test)>0.5,{numbits=numbits+1; 1},0);
//				});
//				
//				if(numbits==m,
//				{
//					array= [1]++array;
//					
//					//Post <<[numbits, m, array]<<nl; 
//							
//					test= PCSet.newFromArray(array); 
//					rep=SetClass(test).rep;
//					if(rep.code==test.code, {
//						//if canonical, ie maximal code representative, write out
//						file.write(Int32Array[test.code]);
//						});
//					
//				});
//			});























