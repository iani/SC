//algorithm from p.212 of Barto and Sutton RL 1998
//tile coding with binary features, epsilon-greedy policy
//accumulating traces; but p.215 suggests replacing traces might lead to faster learning?  

//linear gradient descent sarsa(lambda)

LGDSarsa {
	var <numfeatures, <actions, <stateactiontofeature, <reward; 
	var <>epsilon, <>alpha, <>gamma, <>lambda, <>accum; 
	var <theta, <e;  //theta are linear model weights, e are eligibility traces
	
	var <>s, <>a, <r, <a2; //last state, action, reward


	*new {arg numfeatures=10, actions, stateactiontofeature, reward, epsilon=0.1, alpha=0.05, gamma=0.1, lambda=0.9, accum=false; 
	 	^super.newCopyArgs(numfeatures, actions, stateactiontofeature, reward, epsilon, alpha, gamma, lambda, accum).initLGDSarsa;
	 }
	 
	 //initialise theta and e
	 initLGDSarsa {
	 	
		theta = 	Array.rand(numfeatures,0.0,1.0);
		e = Array.fill(numfeatures,{0.0});	 
	 }

	//assess current quality of state and val=possible action
	getqa {|state, val|
		var indices;
	
		indices= stateactiontofeature.value(state,val);

		^theta.at(indices).sum; 	
	}

	//epsilon-greedy policy
	chooseaction {|state|
		var qa, maxa, maxqa; 
		
		maxqa= 999999.9.neg;
		maxa= actions[0];
		
		actions.do {|val|
			//indices= stateactiontofeature.value(state,val);

			qa = this.getqa(state,val); //theta.at(indices).sum; 
			
			if(qa>maxqa,{maxqa=qa; maxa=val;}); 
			
			};
			
		//epsilon greedy step
		if(epsilon.coin,{maxa= actions.choose;});	
	
		^maxa
	}


	//online training as data received
	//s2 is state just observed, can now backup to previous state s
	train {|s2|
		var qa, qa2, delta;
		
		//accumulating traces
		e = gamma*lambda*e;
		
		//accumulate or replace?
		if(accum,{
		stateactiontofeature.value(s,a).do {|i| e[i]=e[i]+1};
		},
		{
		//if replacing traces could set all e to zero at this point effectively! 
		
		//replace
		stateactiontofeature.value(s,a).do {|i| e[i]=1};
		});
		
		
		//could zero those below a certain threshold

		//match of a to new state
		r=reward.value(a,s2);
		
		qa= this.getqa(s,a);
		 
		delta = r - qa;
		
		a2 = this.chooseaction(s2);

		qa2 = this.getqa(s2,a2);
		
		delta = delta + (gamma*qa2);
		
		theta = theta + (alpha*delta*e); //array update
		
		s=s2;
		a=a2;
		
		^a2;  //return action just chosen
	}
	
	
	newepisode {|state|
	
		//rather than staying within chain of decisions following train
		
		s=state; 
		a= this.chooseaction(s);
		
		//but will miss one update cycle
	}
	

	//given a current state, what is the probability distribution over actions? 
	distribution {|state|
		var dist;
		
		dist= actions.collect({|val| this.getqa(state, val)});
		
		^dist.normalizeSum;
		
	}


}