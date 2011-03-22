
//:a Name of code part

a = nil;
t = Document.current.string;
b = t.findRegexp("^//:").slice(nil, 0);

b collect: { | pos, i | t[pos..b[i + 1] ? t.size - 1] };


//:b Name of next code part
/*:
some special configuration for this code, in SC code

*/

