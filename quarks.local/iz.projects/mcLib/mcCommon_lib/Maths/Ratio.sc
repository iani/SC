// a Simple Number ???
// --> yes, we want easy response to ALL that a SimpleNumber can do, so that no obj may realise...
// --> thus, anything may contain a Ratio instead of Int or Float, even a Complex or a Trigon.

Ratio : SimpleNumber {
	classvar <>denominator=1000, <>fasterBetter=false; // affects .asRatio conversion from Floats  

	var <>num, <>dev;
	
	*new {|num=1, dev=1| ^super.newCopyArgs(num, dev).init }
	*fromCents {|cents| ^cents.centratio.asRatio}
	init{ // kürzen 
		var divider;
		if ((num.isInteger && dev.isInteger), {
		divider = num.gcd(dev);
		num = num div: divider;
		dev = dev div: divider;
		if (dev.isNegative) {num = num.neg; dev = dev.neg}
		}, {	Error("arguments to Ratio must be Intergers").throw;});
	}
	isRatio { ^true}
	asRatio { ^this}
	value { ^num/dev}
	
	asString { 
		var nSize = num.asString.size max: dev.asString.size;
		^num.mcFormat(nSize, \r) ++ "/|" ++ dev.mcFormat(nSize, \l) 
	}
	asCompileString { ^"(" ++ this.asString ++ ")" }
	storeOn { |stream| stream << this.asCompileString } 
		
	//printOn { |stream| stream << this.asCompileString } 
	printOn { arg stream;
		var val, cent;
		val = this.value.round(0.001);
		cent = this.ratiocent.round(0.1);
		stream << "(Ratio(" << num << "," << dev << ") =~ " << val <<  "; " << cent <<"c)"
	}
	
	info { //RedExcel would be overkill here
		^"Ratio(" ++ num.mcFormat(4,\r) ++ "," ++ dev.mcFormat(4,\r) ++ ") /* v:~ "
			++ this.value.round(0.0001).mcFormat(6,\l) 
			++  " | c:~ " ++ this.ratiocent.round(0.1).mcFormat(6) ++ " */"
	}
			
	//proper ratio math
	+ { arg that, adverb;
		^case
		{ that.isRatio } 	{ Ratio.new(((num*that.dev) + (dev*that.num)), dev*that.dev) }
		{ that.isInteger }	{ Ratio.new(num + (that*dev), dev) }
		{ that.isFloat }	{ this + that.asRatio } // conversion !!!
		{ that.performBinaryOpOnSimpleNumber('+', this, adverb) }
	}
	- { arg that, adverb; 
		^case
		{ that.isRatio } 	{ Ratio.new(((num*that.dev) - (dev*that.num)), dev*that.dev) }
		{ that.isInteger }	{ Ratio.new(num - (that*dev), dev) }
		{ that.isFoat} 	{ this - that.asRatio } // conversion !!!
		{ that.performBinaryOpOnSimpleNumber('-', this, adverb) }
	}
	* { arg that, adverb; 
		^case
		{ that.isRatio } 	{ Ratio.new(num*that.num, dev*that.dev) }
		{ that.isInteger }	{ Ratio.new(num * that, dev) }
		{ that.isFloat}	{ this * that.asRatio } // conversion !!!
		{ that.performBinaryOpOnSimpleNumber('*', this, adverb) }
	}
	/ { arg that, adverb; // this.logln("Division:" + [this, that]);
		^case
		{ that.isRatio } 	{ Ratio.new(num*that.dev, dev*that.num) }
		{ that.isInteger }	{ Ratio.new(num, dev * that) }
		{ that.isFloat }	{ this / that.asRatio } // conversion !!!
		{ that.performBinaryOpOnSimpleNumber('/', this, adverb) }
	}
	/| { arg that, adverb; this.logln("Ratio of Ratio:" + [this, that]);
		^this.perform('/', that, adverb)
	}
	pow { arg that, adverb;
		 ^case
		{ that.isInteger } { (num**that).asRatio / (dev**that).asRatio }
		{ that.isKindOf(SimpleNumber) } { this.value.pow(that) }
		{ that.performBinaryOpOnSimpleNumber('pow', this, adverb) }
	}	

	// if a Ratio is the second operand of a BinaryOpOnSimpleNumber -> non-commutative behaviour
	performBinaryOpOnSimpleNumber { arg aSelector, aNumber, adverb;
			//this.logln("performBinaryOpOnSimpleNumber:" + [aSelector, aNumber, adverb]);
		if (aNumber.isInteger, {
			^aNumber.asRatio.perform(aSelector, this, adverb) //keeps being a Ratio
		},{
			^aNumber.perform(aSelector, this.asFloat, adverb) //converts into a Float
		})
	}
	abs { ^Ratio(num.abs, dev) }
	neg { ^Ratio(num.neg, dev) }
	reciprocal { ^Ratio(dev, num) }
	squared { ^this * this }
	cubed { ^this * this * this }
	
	
	// still work left to finish: eg. neg, abs, squared, cubed etc...
	

/* test if all methods work
(
	SimpleNumber.methods.do{|i| ("" ++ i.name ++ " {|...args| ^this.value.performList('" ++ i.name 		++ "', args)}").postln}
)
*/
isValidUGenInput {|...args| ^this.value.performList(\isValidUGenInput, args)}
numChannels {|...args| ^this.value.performList(\numChannels, args)}

magnitude {|...args| ^this.value.performList(\magnitude, args)}
angle {|...args| ^this.value.performList(\angle, args)}

// neg {|...args| ^this.value.performList(\neg, args)}	 
bitNot {|...args| ^this.value.performList(\bitNot, args)}
// abs {|...args| ^this.value.performList(\abs, args)}
ceil {|...args| ^this.value.performList(\ceil, args)}
floor {|...args| ^this.value.performList(\floor, args)}
frac {|...args| ^this.value.performList(\frac, args)}
sign {|...args| ^this.value.performList(\sign, args)}
// squared {|...args| ^this.value.performList(\squared, args)}
// cubed {|...args| ^this.value.performList(\cubed, args)}
sqrt {|...args| ^this.value.performList(\sqrt, args)}
exp {|...args| ^this.value.performList(\exp, args)}
// reciprocal {|...args| ^this.value.performList(\reciprocal, args)}
midicps {|...args| ^this.value.performList(\midicps, args)}
cpsmidi {|...args| ^this.value.performList(\cpsmidi, args)}
midiratio {|...args| ^this.value.performList(\midiratio, args)}
ratiomidi {|...args| ^this.value.performList(\ratiomidi, args)}
ampdb {|...args| ^this.value.performList(\ampdb, args)}
dbamp {|...args| ^this.value.performList(\dbamp, args)}
octcps {|...args| ^this.value.performList(\octcps, args)}
cpsoct {|...args| ^this.value.performList(\cpsoct, args)}
log {|...args| ^this.value.performList(\log, args)}
log2 {|...args| ^this.value.performList(\log2, args)}
log10 {|...args| ^this.value.performList(\log10, args)}
sin {|...args| ^this.value.performList(\sin, args)}
cos {|...args| ^this.value.performList(\cos, args)}
tan {|...args| ^this.value.performList(\tan, args)}
asin {|...args| ^this.value.performList(\asin, args)}
acos {|...args| ^this.value.performList(\acos, args)}
atan {|...args| ^this.value.performList(\atan, args)}
sinh {|...args| ^this.value.performList(\sinh, args)}
cosh {|...args| ^this.value.performList(\cosh, args)}
tanh {|...args| ^this.value.performList(\tanh, args)}
rand {|...args| ^this.value.performList(\rand, args)}
rand2 {|...args| ^this.value.performList(\rand2, args)}
linrand {|...args| ^this.value.performList(\linrand, args)}
bilinrand {|...args| ^this.value.performList(\bilinrand, args)}
sum3rand {|...args| ^this.value.performList(\sum3rand, args)}

distort {|...args| ^this.value.performList(\distort, args)}
softclip {|...args| ^this.value.performList(\softclip, args)}
coin {|...args| ^this.value.performList(\coin, args)}
isPositive {|...args| ^this.value.performList(\isPositive, args)}
isNegative {|...args| ^this.value.performList(\isNegative, args)}
isStrictlyPositive {|...args| ^this.value.performList(\isStrictlyPositive, args)}
isNaN {|...args| ^this.value.performList(\isNaN, args)}


booleanValue {|...args| ^this.value.performList(\booleanValue, args)}
binaryValue {|...args| ^this.value.performList(\binaryValue, args)}


rectWindow {|...args| ^this.value.performList(\rectWindow, args)}
hanWindow {|...args| ^this.value.performList(\hanWindow, args)}
welWindow {|...args| ^this.value.performList(\welWindow, args)}
triWindow {|...args| ^this.value.performList(\triWindow, args)}

scurve {|...args| ^this.value.performList(\scurve, args)}
ramp {|...args| ^this.value.performList(\ramp, args)}

//+ {|...args| ^this.value.performList(\+, args)}
//- {|...args| ^this.value.performList(\-, args)}
//* {|...args| ^this.value.performList(\*, args)}
/// {|...args| ^this.value.performList(/|, args)}
mod {|...args| ^this.value.performList(\mod, args)}
div {|...args| ^this.value.performList(\div, args)}
//pow {|...args| ^this.value.performList(\pow, args)}
min {|...args| ^this.value.performList(\min, args)}
max {|...args| ^this.value.performList(\max, args)}
bitAnd {|...args| ^this.value.performList(\bitAnd, args)}
bitOr {|...args| ^this.value.performList(\bitOr, args)}
bitXor {|...args| ^this.value.performList(\bitXor, args)}
bitHammingDistance {|...args| ^this.value.performList(\bitHammingDistance, args)}
bitTest {|...args| ^this.value.performList(\bitTest, args)}
lcm {|...args| ^this.value.performList(\lcm, args)}
gcd {|...args| ^this.value.performList(\gcd, args)}
round {|...args| ^this.value.performList(\round, args)}
roundUp {|...args| ^this.value.performList(\roundUp, args)}
trunc {|...args| ^this.value.performList(\trunc, args)}
atan2 {|...args| ^this.value.performList(\atan2, args)}
hypot {|...args| ^this.value.performList(\hypot, args)}
hypotApx {|...args| ^this.value.performList(\hypotApx, args)}
leftShift {|...args| ^this.value.performList(\leftShift, args)}
rightShift {|...args| ^this.value.performList(\rightShift, args)}
unsignedRightShift {|...args| ^this.value.performList(\unsignedRightShift, args)}
ring1 {|...args| ^this.value.performList(\ring1, args)}
ring2 {|...args| ^this.value.performList(\ring2, args)}
ring3 {|...args| ^this.value.performList(\ring3, args)}
ring4 {|...args| ^this.value.performList(\ring4, args)}
difsqr {|...args| ^this.value.performList(\difsqr, args)}
sumsqr {|...args| ^this.value.performList(\sumsqr, args)}
sqrsum {|...args| ^this.value.performList(\sqrsum, args)}
sqrdif {|...args| ^this.value.performList(\sqrdif, args)}
absdif {|...args| ^this.value.performList(\absdif, args)}
thresh {|...args| ^this.value.performList(\thresh, args)}
amclip {|...args| ^this.value.performList(\amclip, args)}
scaleneg {|...args| ^this.value.performList(\scaleneg, args)}
clip2 {|...args| ^this.value.performList(\clip2, args)}
fold2 {|...args| ^this.value.performList(\fold2, args)}
wrap2 {|...args| ^this.value.performList(\wrap2, args)}

excess {|...args| ^this.value.performList(\excess, args)}
firstArg {|...args| ^this.value.performList(\firstArg, args)}
rrand {|...args| ^this.value.performList(\rrand, args)}
exprand {|...args| ^this.value.performList(\exprand, args)}

== {|...args| ^this.value.performList('==', args)}
!= {|...args| ^this.value.performList('!=', args)}
< {|...args| ^this.value.performList('<', args)}
> {|...args| ^this.value.performList('>', args)}
<= {|...args| ^this.value.performList('<=', args)}
>= {|...args| ^this.value.performList('>=', args)}

equalWithPrecision {|...args| ^this.value.performList(\equalWithPrecision, args)}

// hash { _ObjectHash; ^this.primitiveFailed }
hash { ^num.hash bitXor: dev.hash } // used here like in Complex.sc
// hash {|...args| ^this.value.performList(\hash, args)}

asInteger {|...args| ^this.value.performList(\asInteger, args)}

asFloat {|...args| ^this.value.performList(\asFloat, args)}

asComplex {|...args| ^this.value.performList(\asComplex, args)}
asRect {|...args| ^this.value.performList(\asRect, args)}


degrad {|...args| ^this.value.performList(\degrad, args)}
raddeg {|...args| ^this.value.performList(\raddeg, args)}

fontID {|...args| ^this.value.performList(\fontID, args)}

//performBinaryOpOnSimpleNumber { arg aSelector, aNumber; ^error("Math operation failed.\n") }
performBinaryOpOnComplex {|...args| ^this.value.performList(\performBinaryOpOnComplex, args)}
performBinaryOpOnSignal {|...args| ^this.value.performList(\performBinaryOpOnSignal, args)}

nextPowerOfTwo {|...args| ^this.value.performList(\nextPowerOfTwo, args)}
nextPowerOf {|...args| ^this.value.performList(\nextPowerOf, args)}
nextPowerOfThree {|...args| ^this.value.performList(\nextPowerOfThree, args)}
previousPowerOf {|...args| ^this.value.performList(\previousPowerOf, args)}

quantize {|...args| ^this.value.performList(\quantize, args)}

linlin {|...args| ^this.value.performList(\linlin, args)}
linexp {|...args| ^this.value.performList(\linexp, args)}
explin {|...args| ^this.value.performList(\explin, args)}
expexp {|...args| ^this.value.performList(\expexp, args)}
lincurve {|...args| ^this.value.performList(\lincurve, args)}
curvelin {|...args| ^this.value.performList(\curvelin, args)}
bilin {|...args| ^this.value.performList(\bilin, args)}
biexp {|...args| ^this.value.performList(\biexp, args)}
lcurve {|...args| ^this.value.performList(\lcurve, args)}
gauss {|...args| ^this.value.performList(\gauss, args)}
gaussCurve {|...args| ^this.value.performList(\gaussCurve, args)}

asPoint {|...args| ^this.value.performList(\asPoint, args)}
asWarp {|...args| ^this.value.performList(\asWarp, args)}

wait {|...args| ^this.value.performList(\wait, args)}
waitUntil {|...args| ^this.value.performList(\waitUntil, args)}
sleep {|...args| ^this.value.performList(\sleep, args)}

//printOn {|...args| ^this.value.performList(\printOn, args)}
//storeOn {|...args| ^this.value.performList(\storeOn, args)}

rate {|...args| ^this.value.performList(\rate, args)}
asAudioRateInput {|...args| ^this.value.performList(\asAudioRateInput, args)}

writeInputSpec {|...args| ^this.value.performList(\writeInputSpec, args)}

series {|...args| ^this.value.performList(\series, args)}
seriesIter {|...args| ^this.value.performList(\seriesIter, args)}
degreeToKey {|...args| ^this.value.performList(\degreeToKey, args)}
keyToDegree {|...args| ^this.value.performList(\keyToDegree, args)}
nearestInList {|...args| ^this.value.performList(\nearestInList, args)}
nearestInScale {|...args| ^this.value.performList(\nearestInScale, args)}
partition {|...args| ^this.value.performList(\partition, args)}
nextTimeOnGrid {|...args| ^this.value.performList(\nextTimeOnGrid, args)}

playAndDelta {|...args| ^this.value.performList(\playAndDelta, args)}
asQuant {|...args| ^this.value.performList(\asQuant, args)}

asTimeString {|...args| ^this.value.performList(\asTimeString, args)}
asFraction {|...args| ^this.value.performList(\asFraction, args)}
prSimpleNumberSeries {|...args| ^this.value.performList(\prSimpleNumberSeries, args)}

asBufWithValues {|...args| ^this.value.performList(\asBufWithValues, args)}
schedBundleArrayOnClock {|...args| ^this.value.performList(\schedBundleArrayOnClock, args)}

// other libs extensions

asMIDIInPortUID {|...args| ^this.value.performList('asMIDIInPortUID', args)}
prepareForProxySynthDef {|...args| ^this.value.performList('prepareForProxySynthDef', args)}
rgb {|...args| ^this.value.performList('rgb', args)}
guiClass {|...args| ^this.value.performList('guiClass', args)}
addToDefName {|...args| ^this.value.performList('addToDefName', args)}
time2secs {|...args| ^this.value.performList('time2secs', args)}
asTimeString1 {|...args| ^this.value.performList('asTimeString1', args)}
secs2time {|...args| ^this.value.performList('secs2time', args)}
encodeForOSC {|...args| ^this.value.performList('encodeForOSC', args)}
binomial {|...args| ^this.value.performList('binomial', args)}
framesecs {|...args| ^this.value.performList('framesecs', args)}
bindClassName {|...args| ^this.value.performList('bindClassName', args)}
isValidIDictKey {|...args| ^this.value.performList('isValidIDictKey', args)}
mapMode {|...args| ^this.value.performList('mapMode', args)}
unmapMode {|...args| ^this.value.performList('unmapMode', args)}
nz {|...args| ^this.value.performList('nz', args)}
storeEditOn {|...args| ^this.value.performList('storeEditOn', args)}
isValidVoicerArg {|...args| ^this.value.performList('isValidVoicerArg', args)}
isValidSynthArg {|...args| ^this.value.performList('isValidSynthArg', args)}
asTimeSpec {|...args| ^this.value.performList('asTimeSpec', args)}
asRational {|...args| ^this.value.performList('asRational', args)}
/% {|...args| ^this.value.performList('/%', args)}
fuzzygcd {|...args| ^this.value.performList('fuzzygcd', args)}
breakUp {|...args| ^this.value.performList('breakUp', args)}
xlistrand {|...args| ^this.value.performList('xlistrand', args)}
range {|...args| ^this.value.performList('range', args)}
forceRange {|...args| ^this.value.performList('forceRange', args)}
rangeExp {|...args| ^this.value.performList('rangeExp', args)}
madd {|...args| ^this.value.performList('madd', args)}
bi2uni {|...args| ^this.value.performList('bi2uni', args)}
uni2bi {|...args| ^this.value.performList('uni2bi', args)}
asNote {|...args| ^this.value.performList('asNote', args)}
midiname {|...args| ^this.value.performList('midiname', args)}
midivoicetype {|...args| ^this.value.performList('midivoicetype', args)}
makeScale {|...args| ^this.value.performList('makeScale', args)}
makeScaleMidi {|...args| ^this.value.performList('makeScaleMidi', args)}
makeScaleCps {|...args| ^this.value.performList('makeScaleCps', args)}
makeScaleName {|...args| ^this.value.performList('makeScaleName', args)}
cpsname {|...args| ^this.value.performList('cpsname', args)}
cpsvoicetype {|...args| ^this.value.performList('cpsvoicetype', args)}
cpstransp {|...args| ^this.value.performList('cpstransp', args)}
asSMPTEString {|...args| ^this.value.performList('asSMPTEString', args)}
asColor {|...args| ^this.value.performList('asColor', args)}
asWebColorString {|...args| ^this.value.performList('asWebColorString', args)}
interpretVal {|...args| ^this.value.performList('interpretVal', args)}
interpret {|...args| ^this.value.performList('interpret', args)}

//asRatio {|...args| ^this.value.performList('asRatio', args)}

cent2ratio {|...args| ^this.value.performList('cent2ratio', args)}
centratio {|...args| ^this.value.performList('centratio', args)}
cr { ^this.centratio }
ratiocent {|...args| ^this.value.performList('ratiocent', args)}
rc { ^this.ratiocent }


calcPVRecSize {|...args| ^this.value.performList('calcPVRecSize', args)}
midinote {|...args| ^this.value.performList('midinote', args)}
betarand {|...args| ^this.value.performList('betarand', args)}
cauchy {|...args| ^this.value.performList('cauchy', args)}
gaussian {|...args| ^this.value.performList('gaussian', args)}
linrrand {|...args| ^this.value.performList('linrrand', args)}
logistic {|...args| ^this.value.performList('logistic', args)}
pareto {|...args| ^this.value.performList('pareto', args)}
poisson {|...args| ^this.value.performList('poisson', args)}
weibull {|...args| ^this.value.performList('weibull', args)}

-= {|...args| ^this.value.performList('-=', args)}

// asTrigon {|...args| ^this.value.performList(\asTrigon, args)} see in Trigon.sc

}


+ Object {
	isRatio { ^false }
	mcFormat {|tabwith, argAlign, plus=false|
		var strg, size, half;
		var align = argAlign ?? {
			case 
			{this.isKindOf(Integer)} {\r}
			{this.isKindOf(Float)} {\r}
			{this.isKindOf(Ratio)} {\c}
			{\l} 
		};
		
		if (plus) { if (this.isKindOf(Float) || this.isKindOf(Integer)) {
				if (this > 0) { strg=this.asString.addFirst($+) } } };
		
		strg = strg ?? {this.asString.replace("\t", "").replace("\r","").replace("\f","")};
		size = strg.size;
		
		if (size>tabwith) {strg = strg.copyRange(0, tabwith-2) ++ $.; size=tabwith};
		^strg = switch (align)
			{\l} {^strg.catList(Array.fill(tabwith-size,$ )) }
			{\r} {^"".catList(Array.fill(tabwith-size,$ )) ++ strg}
			{\c} {half = (tabwith-size) div: 2; ^"".catList(Array.fill(tabwith-size-half,$ )) 
				 ++ strg.catList(Array.fill(half,$ ))}
	}

}
+ AbstractFunction {
	asBestRatio { ^this.composeUnaryOp('asBestRatio') }
	asRatio { ^this.composeUnaryOp('asRatio') }
	cent2ratio { ^this.composeUnaryOp('cent2ratio') }
	centratio { ^this.composeUnaryOp('centratio') }
	ratiocent { ^this.composeUnaryOp('ratiocent') }
}

+ Collection {
	asBestRatio { ^this.collect({ |item| item.asBestRatio }) }
	asRatio { ^this.collect({ |item| item.asRatio }) }
	/| { |that| ^this.collect(_ /| that) } // Set[1, 2, 3] /| 4  ok
	-= { |that| ^this.collect(_ -= that) }
}

+ SequenceableCollection {
	asRatio { ^this.performUnaryOp('asRatio') }
	asBestRatio { ^this.performUnaryOp('asBestRatio') }
	cent2ratio{ ^this.performUnaryOp('cent2ratio') }
	centratio { ^this.performUnaryOp('centratio') }
	cr { ^this.centratio }
	ratiocent { ^this.performUnaryOp('ratiocent') }
	rc { ^this.ratiocent }
	/| { |that, adverb| ^this.performBinaryOp('/|', that, adverb) } // List[1, 2, 3] /| 4  ok
	-= { |that, adverb| ^this.performBinaryOp('-=', that, adverb) }
}

+ SimpleNumber {
	cent2ratio { ^Ratio.fromCents(this)}
	centratio { ^2**(this/1200)}
	cr { ^this.centratio }
	ratiocent { ^(1200 * log2(this.abs)) }
	rc { ^this.ratiocent }
	-= {|that| ^this.equalWithPrecision(that, 0.0005) }	
}

+ Integer {
	asBestRatio {^this.asRatio}
	asRatio { ^Ratio.new(this, 1)}
	/| { |that| ^this.asRatio / that  } //   2 / List[1/|1, 2, 3]  ok
}

+ Float {
	asBestRatio {^Ratio.new(*this.asFraction(1000000, true))}
	asRatio {	 ^Ratio.new(*this.asFraction(Ratio.denominator, Ratio.fasterBetter))}
	/| { |that| ^this.asRatio / that } 
}