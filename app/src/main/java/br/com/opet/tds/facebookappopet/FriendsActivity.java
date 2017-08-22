package br.com.opet.tds.facebookappopet;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsActivity extends Activity {

    private String facebookID;
    private TextView userName;
    private TextView swApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        facebookID = getIntent().getStringExtra("FB_ID");
        userName = (TextView) findViewById(R.id.userName);
        swApi = (TextView) findViewById(R.id.swApi);

        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        profilePictureView.setProfileId(facebookID);

        loadUserName();
        getFilms();
    }

    public void loadUserName(){
       new GraphRequest(AccessToken.getCurrentAccessToken(), "me", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {

                JSONObject user = response.getJSONObject();

                try{
                    String name = user.getString("name");
                    userName.setText("Olá " + name);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();
    }

    String url = "https://swapi.co/api/films";
    String aux = "";

    public void getFilms() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray films = response.getJSONArray("results");
                    for(int i = 0; i < response.getInt("count"); i++) {
                        JSONObject film = films.getJSONObject(i);
                        getName(film.getString("title"), film.getJSONArray("characters"));
                    }
                } catch (JSONException e ) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(request);
    };

    public void getName(final String filmName, JSONArray peopleNames) {

        for(int i = 0; i < peopleNames.length(); i++) {
            try {
                String people = peopleNames.get(i).toString();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, people, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            getPlanet(filmName, response.getString("homeworld"), response.getString("name"));
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                MySingleton.getInstance(this).addToRequestQueue(request);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getPlanet(final String filmName, String planetUrl, final String peopleName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, planetUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    aux += "Filme: " + filmName + "\n" +
                           "Personagem: " + peopleName + "\n" +
                           "Planeta: " + response.getString("name") + "\n\n";

                    swApi.setText(aux);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
