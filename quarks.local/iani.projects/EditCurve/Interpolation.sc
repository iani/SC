//============
Interpolation {
	*new {
		arg a0, antp, coef, netap, n;
			^(1.0 - (exp((neg(n) * (coef * (log(antp / a0)))) / (1 - netap))))* 
			((antp - a0) / (1 - exp(coef * (log(antp / a0))))) + a0;
	}
}

//============
Interpoli {
	*new {
		arg a0, antp, coef, netap, n = 1; 
		var valtransp = neg(min(a0, antp)) + 0.0001; 				if (coef == 0) {coef = 0.000001};
			^(n..(netap - 2)).collect({
				arg id; 
				Interpolation.new(
					a0 + valtransp, antp + valtransp, coef, netap, id) - valtransp;});
	}
}

//============
ValInterpo {
	*new {
		arg liAx, liBx, liBy;
		var result = [];
		var id, p1x, p1y, p2x, p2y;
			liAx.do({
				arg ptP;
				id = liBx.detectIndex({
					arg ptD;
					ptP < ptD;});
				if (id == nil) {id = liBx.size - 1;}; 
				p1x = liBx.at(id - 1); 
				p1y = liBy.at(id - 1);
				p2x = liBx.at(id); 
				p2y = liBy.at(id);
				result = result.add(p2y - p1y / (p2x - p1x / (ptP - p1x)) + p1y);
			});
			^result;
	}
}

//============ Pas terminŽ, il faut une sŽcuritŽ au cas o deux valeurs interpolŽes soient identiques
InterpoliToLi {
	*new {
		arg li0, liNtp, coef, netap;
		var result = [];
		if (coef == 0) {coef = 0.000001};
		netap.do({
			arg id;
			var trans = [];
			li0.do({
				arg val, i;
				trans = trans.add(Interpolation.new(val, liNtp.at(i), coef, netap, id));
			});
			result = result.add(trans);
		});
		^result;
	}
}
