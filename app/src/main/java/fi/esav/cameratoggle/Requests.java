package fi.esav.cameratoggle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


public class Requests {
    String base_url, cookie;

    Context context;
    MainActivity main;

    RequestQueue queue;

    String interval;
    String sensitivity;
    String duration;
    String weekHourCfg;
    String mask0, mask1, mask2;

    public Requests(Context c, MainActivity main) {
        this.context = c;
        this.main = main;

        getPreferences();
        queue = Volley.newRequestQueue(c);
    }

    private void getPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.base_url = prefs.getString("camera_address_key", "");
        String user = prefs.getString("camera_username_key", "");
        String pass = prefs.getString("camera_password_key", "");

        this.cookie = "user=" + user + "; password=" + pass + "; usr=" + user + "; pwd=" + pass;
    }

    public void getStatus() {
        String status_url = this.base_url + "/md.xml";
        getPreferences();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Reader reader = new StringReader(response);
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(reader);
                    while(parser.next() != XmlPullParser.END_DOCUMENT) {
                        if(parser.getEventType() != XmlPullParser.START_TAG) continue;

                        String name = parser.getName();
                        if(name.equalsIgnoreCase("success")) {
                            parser.next();
                            String success = parser.getText();
                            if(!success.equalsIgnoreCase("1")) {
                                main.status.setText("Virhe: " + response);
                                return;
                            }
                        }
                        if(name.equalsIgnoreCase("EmailAlarm")) {
                            parser.next();
                            String enabled = parser.getText();
                            if(enabled.equalsIgnoreCase("1")) {
                                main.status.setText("Liiketunnistus käytössä");
                            } else if(enabled.equalsIgnoreCase("0")) {
                                main.status.setText("Liiketunnistus pois käytöstä");
                            } else {
                                main.status.setText("Virhe tulkittaessa tilaa: " + enabled);
                            }
                        }
                        if(name.equalsIgnoreCase("EmailAlarmInterval")) {
                            parser.next();
                            interval = parser.getText();
                        }
                        if(name.equalsIgnoreCase("Sensitivity")) {
                            parser.next();
                            sensitivity = parser.getText();
                        }
                        if(name.equalsIgnoreCase("Duration")) {
                            parser.next();
                            duration = parser.getText();
                        }
                        if(name.equalsIgnoreCase("WeekHourCfg")) {
                            parser.next();
                            weekHourCfg = parser.getText();
                        }
                        if(name.equalsIgnoreCase("Mask0")) {
                            parser.next();
                            mask0 = parser.getText();
                        }
                        if(name.equalsIgnoreCase("Mask1")) {
                            parser.next();
                            mask1 = parser.getText();
                        }
                        if(name.equalsIgnoreCase("Mask2")) {
                            parser.next();
                            mask2 = parser.getText();
                        }
                    }
                } catch (Exception e) {
                    main.status.setText("virhe: " + e.toString());
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                main.status.setText("Virhe: " + error.toString());
            }
        };

        StringRequest request = new StringRequest(Request.Method.GET, status_url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", cookie);
                return headers;
            }
        };
        queue.add(request);
    }

    public void setMD(final boolean enabled) {
        String set_url = this.base_url + "/setmd.xml?" +
                "enable=1&" +
                "FtpAlarm=0&" +
                "EmailAlarm="+ (enabled ? "1" : "0") + "&" +
                "EMailAlarmInterval=" + interval + "&" +
                "HttpAlarm=0&" +
                "Sensitivity=" + sensitivity + "&" +
                "Duration=" + duration + "&" +
                "WeekHourCfg=" + weekHourCfg + "&" +
                "Mask0=" + mask0 + "&" +
                "Mask1=" + mask1 + "&" +
                "Mask2=" + mask2;


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Reader reader = new StringReader(response);
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(reader);
                    while(parser.next() != XmlPullParser.END_DOCUMENT) {
                        if(parser.getEventType() != XmlPullParser.START_TAG) continue;

                        String name = parser.getName();
                        if(name.equalsIgnoreCase("success")) {
                            parser.next();
                            String success = parser.getText();
                            if(!success.equalsIgnoreCase("1")) {
                                main.status.setText("Virhe: " + response);
                                return;
                            } else {
                                String msg = enabled ? "Liiketunnistus otettu käyttöön" : "Liiketunnistus poistettu käytöstä";
                                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                                toast.show();
                                getStatus();
                            }
                        }
                    }
                } catch (Exception e) {
                    main.status.setText("virhe: " + e.toString());
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        };

        StringRequest request = new StringRequest(Request.Method.GET, set_url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", cookie);
                return headers;
            }
        };
        queue.add(request);
    }
}
