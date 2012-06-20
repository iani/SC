//:---
w = Window.new("Live Hardware Coding GUI", Rect(0, 0, 400, 400)).layout_(
Ê Ê VLayout(
Ê Ê Ê Ê HLayout( Button(), TextField(), Button() ),
Ê Ê Ê Ê TextView()
Ê Ê )
).front;


//:---
w = Window.new("Live Hardware Coding GUI", Rect(0, 0, 400, 400)).layout_(
Ê Ê VLayout(
Ê Ê Ê Ê HLayout( Button(), TextField(), Button() ),
Ê Ê Ê Ê TextView()
Ê Ê )
).front;


//:---

var window = SCWindow.new("",Rect(318, 309, 506, 447)).front;
SCButton.new(window,Rect(61, 305, 100, 20))
.states_([ [ "Push", Color(), Color(1.0) ], [ "Pop", Color(1.0, 1.0, 1.0), Color(0.0, 0.0, 1.0) ] ])
.action_{|v| };

SCButton.new(window,Rect(164, 305, 100, 20))
.states_([ [ "Push", Color(), Color(1.0) ], [ "Pop", Color(1.0, 1.0, 1.0), Color(0.0, 0.0, 1.0) ] ])
.action_{|v| };

SCButton.new(window,Rect(268, 304, 100, 20))
.states_([ [ "Push", Color(), Color(1.0) ], [ "Pop", Color(1.0, 1.0, 1.0), Color(0.0, 0.0, 1.0) ] ])
.action_{|v| };

SCNumberBox.new(window,Rect(59, 330, 100, 20))
.action_{|v| };

SCNumberBox.new(window,Rect(164, 329, 99, 23))
.action_{|v| };

SCNumberBox.new(window,Rect(270, 331, 100, 20))
.action_{|v| };

SCNumberBox.new(window,Rect(109, 97, 94, 101))
.action_{|v| };

SCStaticText.new(window,Rect(106, 209, 100, 20))
.string_("The lazy brown fox")
.action_{|v| };

SCStaticText.new(window,Rect(227, 208, 100, 20))
.string_("The lazy brown fox")
.action_{|v| };

SCButton.new(window,Rect(423, 212, 52, 48))
.states_([ [ "Push", Color(), Color(1.0) ], [ "Pop", Color(1.0, 1.0, 1.0), Color(0.0, 0.0, 1.0) ] ])
.action_{|v| };
SCTextField.new(window,Rect(225, 100, 89, 95))
.action_{|v| };

)