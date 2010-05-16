/* IZ Sunday, May 11, 2008 

*/

/*

DragSourceV : CV {

	*initClass {
		StartUp.add {
			ConductorGUI.osx.use {
				~drag_source = { |win, av, rect|
					rect = rect ?? ~buttonRect;
					av.asArray.do( SCButton(win,rect).connect(_) );
				};
				~drag_source_rect = { Rect(0,0,~labelW * 2,~h) };
				~drag_source_vGUI = { | win, name, av | ~drag_source.value(win, av) }				
			}
		}
	}
}
*/