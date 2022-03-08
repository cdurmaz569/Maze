package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import edu.wm.cs.cs301.KeisterDurmaz.falstad.ActivitySingleton;

public class MazePanel extends View {
    final int view_w = 400;
    final int view_h = 400;
    Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    int col;
    Context context = this.getContext();
	ActivitySingleton activitySingleton = ActivitySingleton.getInstance();
	public Maze maze;

    public MazePanel(Context context) {
        super(context);     
    }

    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MazePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	
    @Override
    public void onDraw(Canvas canvas) { 
    	activitySingleton.setPanel(this);
    	maze = activitySingleton.getMaze();
	    this.canvas = canvas;
        
        maze.notifyViewerRedraw(this, canvas);
    }
    
    public void drawQuad(Canvas canvas, int[] x, int[] y) {	
    	//Log.v("MazePanel: drawQuad", "drawing quadrangle as maze wall");
	    Path path = new Path();
	    path.reset();
	    path.moveTo(x[0], y[0]); 
	    path.lineTo(x[1], y[1]);
	    path.lineTo(x[2], y[2]);
	    path.lineTo(x[3], y[3]);
	    path.lineTo(x[0], y[0]);
	
	    canvas.drawPath(path, paint);
    }
    
    public void drawRect(Canvas canvas, int x, int y, int w, int h) {
    	//Log.v("MazePanel: drawRect", "drawing rectangle");
    	Rect r = new Rect(x, y+h-1, x+w-1, y);
    	canvas.drawRect(r, paint);
    }
    
    public void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
//    	Log.v("MazePanel: drawLine", "drawing line");
    	canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
    
    public void drawOval(Canvas canvas, int x1, int y1, int w1, int h1) {
    	//Log.v("MazePanel: drawOval", "drawing oval");
    	float x = (float) x1;
    	float y = (float) y1;
    	float w = (float) w1;
    	float h = (float) h1;
    	
    	RectF oval = new RectF(x, y, x+w-1, y+h-1);
    	canvas.drawOval(oval, paint);
    }
    	
    public void setColor (String c, int y){
        switch (c) {
        case "0":
                paint.setColor(Color.rgb(y, 20, 20));
        case "1":
                paint.setColor(Color.rgb(20, y, 20));
        case "2":
                paint.setColor(Color.rgb(20, 20, y));
        case "3":
                paint.setColor(Color.rgb(y, y, 20));
        case "4":
                paint.setColor(Color.rgb(20, y, y));
        case "5":
                paint.setColor(Color.rgb(y, 20, y));
        default:
                paint.setColor(Color.rgb(20, 20, 20));
        }
    }
    
    public void setColor(int[] color) {
    	paint.setARGB(255,  color[0], color[1], color[2]);
    }
    
    public void setColor(String c) {
    	//Log.v("MazePanel: setColor", "");
    	switch (c) {
        case "black":
                paint.setColor(Color.BLACK); break;
        case "darkGray":
                paint.setColor(Color.DKGRAY); break;
        case "gray":
                paint.setColor(Color.GRAY); break;
        case "white":
                paint.setColor(Color.WHITE); break;
        case "red":
                paint.setColor(Color.RED); break;
        case "blue":
                paint.setColor(Color.BLUE); break;
        case "yellow":
                paint.setColor(Color.YELLOW); break;
        }
    }
    
    public int getRGB(String c) {
		return Integer.parseInt(c);
	}
    
    public int getRGB(int col) {
    	int red = Color.red(col);
    	int green = Color.green(col);
    	int blue = Color.blue(col);
    	
    	return android.graphics.Color.rgb(red, green, blue);    	
    }
    

}
