
//:f UniqueFunction

u = { | a, b, c | [a, b, c].sum.postln; }.doOnce(10, 20, 30);
u.onRemove({ | uf | ({ uf.value } ! 5).postln; });
u.remove;


