/*

RastMakam.load;

*/

RastMakam { 

	
	*load {
//////////////////////	
var i;

~f=1; 		//Koma 
~e=3; 		//Koma3 
~b=4; 		//Bakiye 
~s=5; 		//KucukMucenneb 
~k=8; 		//BuyukMucenneb 
~t=9; 		//Tanini 
~a12=12; 		//ArtikIkili
~a13=13; 		//ArtikIkili

i = 2 ** ([~t, ~k, ~s, ~t, ~t, ~k, ~s] / 53); //G Rast

//	Rast Rate

~rastRateA1 = 1/4;

~rastRateA2 = ~rastRateA1 * i[0];
~rastRateA3 = ~rastRateA2 * i[1];
~rastRateA4 = ~rastRateA3 * i[2];
~rastRateA5 = ~rastRateA4 * i[3];
~rastRateA6 = ~rastRateA5 *i[4];
~rastRateA7 = ~rastRateA6 * i[5];

~rastRateB1 = ~rastRateA7 * i[6];

~rastRateB2 = ~rastRateB1 * i[0];
~rastRateB3 = ~rastRateB2 * i[1];
~rastRateB4 = ~rastRateB3 * i[2];
~rastRateB5 = ~rastRateB4 * i[3];
~rastRateB6 = ~rastRateB5 *i[4];
~rastRateB7 = ~rastRateB6 * i[5];

~rastRateC1 = ~rastRateB7 * i[6];

~rastRateC2 = ~rastRateC1 * i[0];
~rastRateC3 = ~rastRateC2 * i[1];
~rastRateC4 = ~rastRateC3 * i[2];
~rastRateC5 = ~rastRateC4 * i[3];
~rastRateC6 = ~rastRateC5 *i[4];
~rastRateC7 = ~rastRateC6 * i[5];

~rastRateD1 = ~rastRateC7 * i[6];

~rastRateD2 = ~rastRateD1 * i[0];
~rastRateD3 = ~rastRateD2 * i[1];
~rastRateD4 = ~rastRateD3 * i[2];
~rastRateD5 = ~rastRateD4 * i[3];
~rastRateD6 = ~rastRateD5 *i[4];
~rastRateD7 = ~rastRateD6 * i[5];

~rastRateE1 = ~rastRateD7 * i[6];

//	Rast Freq

~rastA1 = 392.98 / 8;

~rastA2 = ~rastA1 * i[0];
~rastA3 = ~rastA2 * i[1];
~rastA4 = ~rastA3 * i[2];
~rastA5 = ~rastA4 * i[3];
~rastA6 = ~rastA5 *i[4];
~rastA7 = ~rastA6 * i[5];

~rastB1 = ~rastA7 * i[6];

~rastB2 = ~rastB1 * i[0];
~rastB3 = ~rastB2 * i[1];
~rastB4 = ~rastB3 * i[2];
~rastB5 = ~rastB4 * i[3];
~rastB6 = ~rastB5 *i[4];
~rastB7 = ~rastB6 * i[5];

~rastC1 = ~rastB7 * i[6];

~rastC2 = ~rastC1 * i[0];
~rastC3 = ~rastC2 * i[1];
~rastC4 = ~rastC3 * i[2];
~rastC5 = ~rastC4 * i[3];
~rastC6 = ~rastC5 *i[4];
~rastC7 = ~rastC6 * i[5];

~rastD1 = ~rastC7 * i[6];

~rastD2 = ~rastD1 * i[0];
~rastD3 = ~rastD2 * i[1];
~rastD4 = ~rastD3 * i[2];
~rastD5 = ~rastD4 * i[3];
~rastD6 = ~rastD5 *i[4];
~rastD7 = ~rastD6 * i[5];

~rastE1 = ~rastD7 * i[6];



////////////////////////////	
	}
}