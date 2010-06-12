Posix search exercises


"**** abc23xx4".findRegexp("\\*{2}\\D+");

"00".findRegexp("[:digit:]+");
"00:00:123".findRegexp("[:digit:]{2}\:[:digit:]{2}\:[:digit:]{2}");


"2010-06-08 16:40:19.213287+00:00 abcedellae".findRegexp("[:digit:]{2}-[:digit:]{2}-[:digit:]{2} [:digit:]{2}:[:digit:]{2}:([:digit:]|\\.)*");

"2010-06-08 16:40:19.213287+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:(\\d{2}|\\.)+");

"2010-06-08 16:40:19.213287+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*");

"2010-06-08 16:40:19.213287+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*");

"2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*");

"2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*")
	.first[1].findRegexp(" \(\\d{2}\)");
"2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*")
	.first[1].findRegexp(" \\d{2}").first[1].interpret + 3;


a = "[%]".format("2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*")
	.first[1].tr($-, $,).tr($:, $,).replace(" ", ", ")).interpret;
a[3] = a[3] + 3;

// <2010-06-11 Fri 00:12>
format("<%

format("Date.new(%)", "2010-06-09 13:03:39".tr($-, $,).tr($:, $,).replace(" ", ", ")).interpret

Date.new(2010, 06, 10, 10, 10, 10, 10, 10, 10)

(
a = "2010-06-08 16:40:19+00:00 abcedellae".findRegexp("\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^+]*").first[1];
b = (a[11..12].interpret + 103).asString[1..];
a[11] = b[0];
a[12] = b[1];
)

(0..26) + 100

b = (1..10);

b[*(1..4)] = (101..104)

====

"**** <2010-06-08 16:40:19> abcedellae".findRegexp("\\*{4} <\\d+-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[^>]*>")
"**** <2010-06-08 16:40:19> abcedellae".findRegexp("\\*{4} <\\d+-\\d{2}-\\d{2}[^>]*>")


====


