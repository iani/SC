<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Style-Type" content="text/css">
<title></title>
<meta name="Generator" content="Cocoa HTML Writer">
<meta name="CocoaVersion" content="1038.35">
<style type="text/css">
p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 9.0px Monaco}
p.p2 {margin: 0.0px 0.0px 0.0px 0.0px; font: 9.0px Monaco; min-height: 12.0px}
span.Apple-tab-span {white-space:pre}
</style>
</head>
<body>
<p class="p1">//: Test of CodeOSC with iPhone / TouchOSC</p>
<p class="p2"><br></p>
<p class="p1">//:1/b6</p>
<p class="p2"><br></p>
<p class="p1">\default.playFunc({ | freq = 400 | SinOsc.ar(freq, 0, 0.1) }).set(\freq, ~msg[1].midicps);</p>
<p class="p2"><br></p>
<p class="p1">//:2/soundLines</p>
<p class="p2"><br></p>
<p class="p1">if (~msg[1] &gt; 0) {</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>\default.play;</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>3.wait;</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>\default.set(\freq, 1000);</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>3.wait;</p>
<p class="p2"><span class="Apple-tab-span">	</span></p>
<p class="p1"><span class="Apple-tab-span">	</span>\default.release(5);</p>
<p class="p1">}{</p>
<p class="p1"><span class="Apple-tab-span">	</span>\default.release(2);<span class="Apple-tab-span">	</span></p>
<p class="p1">};</p>
</body>
</html>
