



Makam{
	*cargah{}
	*buselik{}
	*kurdi{}
	*rast{}
	*ussak{}
}

Cargah{
	*value{
		
	
	}	
}

Buselik{
	*load{
		
	
	}	
}

Kurdi{
	*load{
		
	
	}	
}

Rast{
	*load{
		
	
	}	
}

Ussak{
	*load{
		
	
	}	
}

/*
~f=1; 		//Koma 
~e=3; 		//Koma3 
~b=4; 		//Bakiye 
~s=5; 		//KucukMucenneb 
~k=8; 		//BuyukMucenneb 
~t=9; 		//Tanini 
~a12=12; 		//ArtikIkili
~a13=13; 		//ArtikIkili


f = { | kabaCargah |	// arguments are a and b here 
	i = 2 ** ([~t, ~t, ~b, ~t, ~t, ~t, ~b] / 53);
	[~yegah = ~kabaCargah * i[0];
	~huseyniAsiran = ~yegah * i[1];
	~acemAsiran = ~huseyniAsiran * i[2];
	~rast = ~acemAsiran * i[3];
	~dugah = ~rast * i[4];
	~buselik = ~dugah * i[5];
	~cargah = ~buselik * i[6];
	~neva = ~cargah * i[0];
	~huseyni = ~neva *i[1];
	~acem = ~huseyni * i[2];
	~gerdaniye = ~acem * i[3];
	~muhayyer = ~gerdaniye * i[4];
	~tizBuselik = ~muhayyer * i[5];
	~tizCargah = ~tizBuselik * i[6];
	~huseyni].next.value;
};


f.value(1);

f.(440);


(
a = Pfunc { |val| val = 2 ** ([~t, ~t, ~b, ~t, ~t, ~t, ~b] / 53) };
b = Pfunc { |x| x * 3 };
c = Pseries(1, 2, inf);
)



(
car = {|val = 5, times = 7 |
	var stp;
	stp = 2 ** ([~t, ~t, ~b, ~t, ~t, ~t, ~b] / 53);
	times.do{|i|
		val* stp[0+i].value.postln;
	};

}.value.postln;
)
// post some values from the composite streams:

t = (a <> b).asStream;
8.do {|i| A * val[i] };

t = (a <> b <> c).asStream;
10.do { t.value(10).postln };

t = (b <> c <> a).asStream;
10.do { t.value(10).postln };


*/