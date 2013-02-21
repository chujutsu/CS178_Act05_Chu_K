package chu.kevin.maptracker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import chu.kevin.maptracker.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MainActivity extends MapActivity {
	MapView mapView;
	MapController mapController;
	GeoPoint point, tempPoint;
	LocationListener locationListener;
	LocationManager locationManager;
	Projection projection;
	List<Overlay> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	public void init() {
		point = null;
		tempPoint = null;
		mapView = (MapView) findViewById(R.id.map);
		list = new ArrayList<Overlay>();
		projection = mapView.getProjection();

		mapView.setTraffic(true);
		((Button) findViewById(R.id.startButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setLocationListener();

					}
				});
		((Button) findViewById(R.id.stopButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						disable();

					}
				});
		((Button) findViewById(R.id.resetButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						reset();
					}
				});

		mapController = mapView.getController();
		mapController.setZoom(17);

	}

	public void setLocationListener() {
		Toast.makeText(getBaseContext(), "Start", Toast.LENGTH_SHORT)
				.show();
		((Button) findViewById(R.id.stopButton)).setClickable(true);
		reset();
		locationListener = new MyLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5,
				5, locationListener);
		((Button) findViewById(R.id.startButton)).setClickable(false);
	}

	public void reset() {
		list.clear();
		mapView.invalidate();
	}
	public synchronized void displayRoute() {
		OverlayTask ot = new OverlayTask();
		ot.execute();
	}
	public void disable() {
		Toast.makeText(getBaseContext(), "Ended "+Double.toString(point.getLatitudeE6())+" "+Double.toString(point.getLongitudeE6()), Toast.LENGTH_SHORT)
				.show();
		((Button) findViewById(R.id.stopButton)).setClickable(false);
		locationManager.removeUpdates(locationListener);
		((Button) findViewById(R.id.startButton)).setClickable(true);
	}
	public void cameraFocus(MyMapOverlay mapOverlay) {
		list = mapView.getOverlays();
		list.add(mapOverlay);
		mapController.animateTo(mapOverlay.getGeoPoint());
		mapView.invalidate();
	}

	public void happyOverlay() {
		MyMapOverlay mapOverlay = new MyMapOverlay(point, this, 1);
		cameraFocus(mapOverlay);
	}

	public void sadOverlay() {
		MyMapOverlay mapOverlay = new MyMapOverlay(tempPoint, this, 2);
		cameraFocus(mapOverlay);
		displayRoute();
	}

	

	class OverlayTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			mapView.invalidate();
			pd = new ProgressDialog(MainActivity.this);
			pd.setMessage("Loading Path...");
			pd.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = false;
			JsonParsing json = new JsonParsing();
			try {
				synchronized (list) {
					json.parsing(point, tempPoint, mapView);

				}
				result = true;
			} catch (ClientProtocolException e) {

				e.printStackTrace();
				result = false;
			} catch (IOException e) {

				e.printStackTrace();
				result = false;
			} catch (JSONException e) {

				e.printStackTrace();
				result = false;
			} catch (URISyntaxException e) {

				e.printStackTrace();
				result = false;
			}
			point = tempPoint;
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pd.dismiss();
			if (result == false)
				Toast.makeText(
						getBaseContext(),
						"Faild to get Path, please secure a better internet connection",
						Toast.LENGTH_SHORT).show();

			mapView.invalidate();
		}
	}

	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (point == null) {
				point = new GeoPoint((int) (location.getLatitude() * 1E6),(int) (location.getLongitude() * 1E6));
				happyOverlay();
			} else {
				tempPoint = new GeoPoint((int) (location.getLatitude() * 1E6),(int) (location.getLongitude() * 1E6));
				sadOverlay();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

}
