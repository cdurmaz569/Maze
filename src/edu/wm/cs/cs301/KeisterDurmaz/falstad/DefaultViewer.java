package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import android.graphics.Canvas;

/**
 * This is a default implementation of the Viewer interface
 * with methods that do nothing but providing debugging output
 * such that subclasses of this class can selectively overwrite
 * those methods that are truly needed.
 * 
 * @author Kemper
 *
 */
public class DefaultViewer implements Viewer {

	@Override
	public void redraw(Canvas canvas, int state, int px, int py, int viewdx,
			int viewdy, int walkStep, int viewOffset, RangeSet rset, int angle) {
		dbg("redraw") ;
	}

	@Override
	public void incrementMapScale() {  
		dbg("incrementMapScale") ;
	}

	@Override
	public void decrementMapScale() {
		dbg("decrementMapScale") ;
	}


	private void dbg(String str) {
		//System.out.println("DefaultViewer" + str);
	}

}

