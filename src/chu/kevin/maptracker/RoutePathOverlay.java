package chu.kevin.maptracker;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

public class RoutePathOverlay extends Overlay{
	static int si;
	List<GeoPoint> g;
	boolean b;
			
	public RoutePathOverlay(List<GeoPoint> gp) {
		this(gp,si,true);
	}
			
	@SuppressWarnings("static-access")
	public RoutePathOverlay(List<GeoPoint> gp, int j, boolean b) {
		g = gp;
		this.si = j;
		this.b = b;
	}
			
	private void drawOval(Canvas c, Paint p, Point pt) {
		Paint p1 = new Paint(p);
		p1.setStyle(Paint.Style.FILL_AND_STROKE);
		p1.setStrokeWidth(2);
		int a = 6;
		RectF r = new RectF(pt.x - a, pt.y - a, pt.x + a, pt.y + a);
		c.drawOval(r, p1);               
	}

    @Override
	public synchronized boolean draw(Canvas c, MapView mv, boolean b1, long t) {
            Projection p = mv.getProjection();
            if (b1 == false && g != null) {
                    Point pt1 = null, pt2 = null;
                    Path path = new Path();
                    synchronized (g) { 
                	   for (int i = 0; i < g.size(); i++) {
                            GeoPoint gp = g.get(i);
                            Point pt3 = new Point();
                            p.toPixels(gp, pt3);
                            if (i == 0) { //This is the start point
                                    pt1 = pt3;
                                    si = Color.RED;
                                    path.moveTo(pt3.x, pt3.y);                                    
                            }                          
                            else {
                                    if (i == g.size() - 1){//This is the end point
                                            pt2 = pt3;
                                    		si = Color.BLUE;
                                    }                             
                                    path.lineTo(pt3.x, pt3.y);
                            }                           
                	   }
                    }
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setColor(si);
                    paint.setStyle(Paint.Style.STROKE);	
                    paint.setStrokeWidth(5);
                    paint.setAlpha(90);
                    if (getDrawStartEnd()) {
                            if (pt1 != null) {
                                    drawOval(c, paint, pt1);
                            }
                            if (pt2 != null) {
                                    drawOval(c, paint, pt2);
                            }
                    }
                    if (!path.isEmpty())
                            c.drawPath(path, paint);
            }
            return super.draw(c, mv, b1, t);
    }

    public synchronized boolean getDrawStartEnd() {
            return b;
    }

    public synchronized void setDrawStartEnd(boolean b) {
            this.b = b;
    }
}
