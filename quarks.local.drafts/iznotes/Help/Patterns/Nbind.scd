
//:1 ---
Nbind(\default.play, Prand([0.1, 0.2], inf), 
	(freq: Prand((100, 200..3000), 30), amp: Prand([0.01, 0.02, 0.2], inf)));


//:2 ---
Nbind(\default.play, Prand([0.1, 0.2], inf), 
	(freq: Prand((100, 200..3000), inf), amp: Prand([0.01, 0.02, 0.2], inf)));
