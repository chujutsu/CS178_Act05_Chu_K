package chu.kevin.maptracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import chu.kevin.maptracker.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyMapOverlay extends Overlay{

	int indicator;
	GeoPoint p; 
	Context cont;
	
	public MyMapOverlay(GeoPoint point,Context context, int indicator){
		this.indicator = indicator;
		this.p=point;
		this.cont=context;
		
	}
    @Override
    public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {  
        super.draw(canvas, mv, shadow);  
        Point screenPts = new Point();  
        Bitmap bmp;
        mv.getProjection().toPixels(p, screenPts);  
       
        if(indicator==1)
        	 bmp= BitmapFactory.decodeResource(cont.getResources(),R.drawable.start);  
        else 
        	bmp = BitmapFactory.decodeResource(cont.getResources(),R.drawable.stop); 
        canvas.drawBitmap(bmp, screenPts.x-bmp.getWidth()/2, screenPts.y-bmp.getHeight()/2, null);        
        super.draw(canvas,mv,shadow);  

        return true;  
    }
    
    public GeoPoint getGeoPoint(){
    	return p;
    }

}
