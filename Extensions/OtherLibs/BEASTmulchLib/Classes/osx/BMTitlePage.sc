BMTitlePage {

	*initClass {
		String.scDir.contains("BEASTmulch System.app").if({ ApplicationStart.add(this) });
	}
	
	*doOnApplicationStart {
		var screenBounds, titleWindow;
		screenBounds = SCWindow.screenBounds;

		{
		
		titleWindow = SCWindow("Welcome", Rect(screenBounds.width / 2 - 350, screenBounds.height / 2 - 262,  700, 524), false, false)
			.alwaysOnTop_(true).front;
		
		SCQuartzComposerView(titleWindow, 700@524)
			.path_(this.filenameSymbol.asString.dirname ++ "/QC/Title.qtz";)
			.start;
		
		"\n\n//////////////////////////////////////////////////////////////////////////\n\n
 BEASTmulch System version 1.0.0
 Copyright (C) 2009 Scott Wilson and Sergio Luque
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 2 as published by
 the Free Software Foundation.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 
 http://www.beast.bham.ac.uk/research/mulch.shtml
 beastmulch-info@contacts.bham.ac.uk
 
 The BEASTmulch project was supported by a grant from the Arts and Humanities
 Research Council of the UK: http://www.ahrc.ac.uk
\n\n//////////////////////////////////////////////////////////////////////////\n\n".postln;
		5.wait;
		titleWindow.close;
		}.fork(AppClock)

	}
}