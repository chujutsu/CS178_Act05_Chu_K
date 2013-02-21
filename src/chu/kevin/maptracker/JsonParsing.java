package chu.kevin.maptracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class JsonParsing {

	GeoPoint g1,g2;
	URI uri1;
	List<GeoPoint> l1,firstPoint;
	MapView map;
	
	
		public synchronized List<GeoPoint> parsing(GeoPoint gp1, GeoPoint gp2, MapView map1) throws ClientProtocolException, IOException, JSONException, URISyntaxException{
			HttpClient h1 = new DefaultHttpClient();
			StringBuilder sb1 = new StringBuilder();
			sb1.append("https://maps.googleapis.com/maps/api/directions/json?origin=")
			.append(Double.toString(gp1.getLatitudeE6()/1E6)).append(",").append(Double.toString(gp1.getLongitudeE6()/1E6)).append("&destination=")
			.append(Double.toString(gp2.getLatitudeE6()/1E6)).append(",").append(Double.toString(gp2.getLongitudeE6()/1E6))
			.append("&sensor=false&alternatives=true");
			
			uri1 = new URI(sb1.toString());
			
			HttpPost hp = new HttpPost(uri1);
			
			HttpResponse hr = h1.execute(hp);
			HttpEntity he = hr.getEntity();
			InputStream i = null;
			i = he.getContent();
			BufferedReader b = new BufferedReader(new InputStreamReader(i, "iso-8859-1"), 8);
			StringBuilder sb2 = new StringBuilder();
			sb2.append(b.readLine() + "\n");
			String s1 = "0";
			while ((s1 = b.readLine()) != null) {
			    sb2.append(s1 + "\n");
			}
			i.close();
			b.close();
			String s2 = sb2.toString();
			JSONObject j1 = new JSONObject(s2);
			JSONArray ja = j1.getJSONArray("routes");
			JSONObject j2 = ja.getJSONObject(0);
			JSONObject j3 = j2.getJSONObject("overview_polyline");
			String s3 = j3.getString("points");
		    l1 = decodePoly(s3);

			map1.getOverlays().add(new RoutePathOverlay(l1));
			return l1;
		}
		
		
		
		//conver points into GeoPoints
		private synchronized List<GeoPoint> decodePoly(String s) {

		    List<GeoPoint> l = new ArrayList<GeoPoint>();
		    int i, j, k, length = s.length();
		    i = j = k = 0;
		    while (i < length) {
		        int a, b = 0, c = 0;
		        do {
		            a = s.charAt(i++) - 63;
		            c |= (a & 0x1f) << b;
		            b += 5;
		        } while (a >= 0x20);
		        int d = ((c & 1) != 0 ? ~(c >> 1) : (c >> 1));
		        j += d;

		        b = 0;
		        c = 0;
		        do {
		            a = s.charAt(i++) - 63;
		            c |= (a & 0x1f) << b;
		            b += 5;
		        } while (a >= 0x20);
		        int e = ((c & 1) != 0 ? ~(c >> 1) : (c >> 1));
		        k += e;
		        GeoPoint g = new GeoPoint((int) ((j / 1E5) * 1E6), (int) ((k / 1E5) * 1E6));
		        l.add(g);
		    }
		    return l;
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public synchronized static ArrayList getDirections(GeoPoint gp1, GeoPoint gp2) {
			
			        String s1 = "http://maps.googleapis.com/maps/api/directions/xml?origin="+gp1.getLatitudeE6()/1E6 + "," + gp1.getLongitudeE6()/1E6  + "&destination=" + gp2.getLatitudeE6()/1E6+ "," + gp2.getLongitudeE6()/1E6 + "&sensor=false&units=metric";
			        String s[] = { "lat", "lng" };
			        ArrayList  a = new ArrayList();
			        HttpResponse  hp = null;
			        try {
			            HttpClient  hc = new DefaultHttpClient();
			            HttpContext localContext = new BasicHttpContext();
			            HttpPost httpPost = new HttpPost(s1);
			             hp =  hc.execute(httpPost, localContext);
			            InputStream  is =  hp.getEntity().getContent();
			            DocumentBuilder  db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			            Document doc =  db.parse( is);
			            if (doc != null) {
			                NodeList nl1, nl2;
			                nl1 = doc.getElementsByTagName( s[0]);
			                nl2 = doc.getElementsByTagName( s[1]);
			                if (nl1.getLength() > 0) {
			                     a = new ArrayList();
			                    for (int i = 0; i < nl1.getLength(); i++) {
			                        Node node1 = nl1.item(i);
			                        Node node2 = nl2.item(i);
			                        double lat = Double.parseDouble(node1.getTextContent());
			                        double lng = Double.parseDouble(node2.getTextContent());
			                         a.add(new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)));
			                    }
			                } else {
			                    // No points found
			                }
			            }
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			        return  a;
			    }
	
}
