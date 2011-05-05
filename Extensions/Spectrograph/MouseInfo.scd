

MouseInfo {
	var info;
	*new { | userview, parent |
		userview.mouseOverAction = { | v, x, y |
			info = format("X: %, Y: %",
				((x / parent.bounds.width) * userview.width).floor.min(width-1),
				((y / parent.bounds.height) * userview.height).floor.min(height-1) );
			userview.refreshInRect(Rect(5.5,5.5,100,20));
		}
	}
}