

UniqueWindow.listWindow('test', nil, { { | i | i.asString->{ i.postln; } } ! 10 }).onClose({ "closed".postln; })