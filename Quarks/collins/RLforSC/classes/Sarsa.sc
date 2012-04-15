//basic sarsa algorithm

//sarsa(lambda) p181 in ML book, using eligibility traces- drawback is requiring additional data point for each state, plus iteration over all states each time; pragmatic solution to latter: just keep record of last 20 touched and update them

//alpha= learning rate
//gamma= discount factor (for following reward)
//lambda controls eligibility traces, how far reward changes can propagate back  
//eta is chance of exploration over following policy of taking action with highest current reward

Sarsa {
	var <>numstates, <>numactions;
	var <>eta, <>alpha,  <>gamma, <>lambda;
	var <>accum;

	var <>q;
	var <>s,<>a, <>r, <>s2, <>a2;  //sarsa variables, a2 = a dash
	
	var update;
	var <>e, <>lasttraces; //eligibility traces
	var <>statetouched; 
	
	*new {arg numstates, numactions, eta=0.1, alpha=0.1, gamma=0.9, lambda=0.9, accum=false; 
	 	^super.newCopyArgs(numstates, numactions, eta, alpha, gamma, lambda, accum).initSarsa;
	 }
	 
	 //initialise theta and e
	 initSarsa {
			 	
		q= Array.fill(numstates*numactions,{0.01.rand}); //random start for slight alterations
		//will soon be disappointed, starting with too much reward to promote exploration
		e= Array.fill(numstates*numactions,{0}); //eligibility traces

		statetouched = 0.dup(numstates*numactions); 

		lasttraces = List[];
		
		s=0;
		s2=0;
		a=0;
		a2=0;
		r=0;

	}

	
	
	etagreedyfunc {|state|
		//var tmp; //, probdistr;
		var choice, maxval, maxind; 
		
		//tmp= q[state..(state+23)];
		
		choice= if(eta.coin,{numactions.rand},{
		
		//this is eta soft 
		//tmp=tmp.normalizeSum;
		//twelve.wchoose(tmp);
		
		//find max index
		
		maxval=(10000.neg);
		maxind=0;
		
		numactions.do{|j| var now; now= q[state+j];  if(now>maxval,{maxval=now; maxind= j}); };
		
		maxind;
		});
		
		//action returned as index
		^choice
	}
	

	
	//assumes states in index form, ie statenow*numactions
	train {|action, statenow, reward=0, nextaction=nil|
		var tmp, tmp2;
		
		//s held from last time! 
		s2=statenow*numactions; //so that everything works out indexwise
		a=action;
		r=reward;
		a2= nextaction ?? {this.etagreedyfunc(s2)};
		
		tmp = gamma*(q[s2+a2]);
		
		update= q[s+a];
		update= alpha*(r + tmp - update);
	
		if(lasttraces.size>20,{tmp2= lasttraces.pop; e[tmp2]=0.0;});
		
		e[s+a] = if(accum,{e[s+a]+1},1.0); //increment winner
	
		lasttraces.addFirst(s+a);
		
		statetouched[s+a] = statetouched[s+a] + 1; 
		
		//store indices of e states so far; only need to iterate over those? 
		lasttraces.do {|index|
			tmp= q[index];
			q[index] = tmp + (update*(e[index]));
			e[index] =  gamma * lambda * (e[index]);
		};

		s=s2; 
		a=a2;
		
		^a2;
		
	}
	
	policy {|action, statenow|
	
		a2= this.etagreedyfunc(s2);
		
		s=s2; 
		a=a2;
		
		^a2;
	}
	
	
	reset {
	
		lasttraces.do{|index|
			e[index] = 0.0;
		}; 
		
		lasttraces = List[];
			
		s=0;
		s2=0;
		a=0;
		a2=0;
		r=0;

	}
	
	
	//pass in stored states, actions, rewards
	offlinetrain {|list|
		
		//initialise
		this.reset;
		
		//iterate
		list.do{|val| 
				
			//a2 not stored?	
			#s, a, r, s2, a2= val;
			
			s= s*numactions; //else not at correct index for update! 
			
			this.train(a,s2,r,a2);
		};
	
		//done
		this.reset;
	
	}
	

}