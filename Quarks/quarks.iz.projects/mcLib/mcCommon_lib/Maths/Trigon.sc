Trigon : Polar { 
	asTrigon { ^this}
	asList { ^List[rho, theta] }
	
	printOn {|stream| stream << rho << " *@ " << theta }
	storeArgs { ^[rho, theta] }
	
	hash { ^rho.hash bitXor: theta.hash } // used here like in Complex.sc
		
	// overwerite math that is not done as Complex
  	* {|obj| ^switch (obj.class,
			Trigon, { ^Trigon(this.rho * obj.rho, (this.theta + obj.theta).mod(2*pi) ) },
			Number, { ^this.asComplex * obj },
			{^error("Math operation failed.\n")}); 
  	}  
}

+ Polar {
	asPoint { ^this.class.new(this.real, this.imag) }
	scale { arg scale; ^this.class.new(rho * scale, theta) }
	rotate { arg angle; ^this.class.new(rho, theta + angle) }
	neg { ^this.class.new(rho, theta + pi) }
	
	asTrigon { ^Trigon(rho, theta) }	
}

+ SimpleNumber {
	*@ {|that| ^Trigon.new(this, that) }	
}

+ Complex {
	asTrigon { ^Trigon.new(this.magnitude, this.angle)}
	asComplex { ^this }
	storeArgs { ^[real, imag] }
//Bugfix:	
	// mc: just WRONG still in SC 3.4: // exp { ^exp(real) * Complex.new(cos(imag), sin(real)) }
	exp { ^exp(real) * Complex.new(cos(imag), sin(imag)) }
	
}

+ Ratio {
	asTrigon {^Trigon.new(this.value, 0)}	
	*@ {|that| ^Trigon.new(this, that) }
}

+ Float {
	asTrigon {^Trigon.new(this, 0.0)}	
}

+ Integer {
	asTrigon {^Trigon.new(this, 0)}	
}