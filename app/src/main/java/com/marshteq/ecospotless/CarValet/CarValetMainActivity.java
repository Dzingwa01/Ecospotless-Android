package com.marshteq.ecospotless.CarValet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.marshteq.ecospotless.Adapters.ValetWashRequestAdapter;
import com.marshteq.ecospotless.Adapters.WashRequestAdapter;
import com.marshteq.ecospotless.Client.AboutEcospotlessActivity;
import com.marshteq.ecospotless.Client.ClientMainActivity;
import com.marshteq.ecospotless.Client.HelpActivity;
import com.marshteq.ecospotless.Client.SettingsActivity;
import com.marshteq.ecospotless.Helpers.Credentials;
import com.marshteq.ecospotless.LoginActivity;
import com.marshteq.ecospotless.Models.Price;
import com.marshteq.ecospotless.Models.UserPref;
import com.marshteq.ecospotless.Models.WashRequest;
import com.marshteq.ecospotless.R;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarValetMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int back_pressed_count;
    private String user_id, picture_url;
    private String user_email, first_name, id_number, gender, phone_number, last_name;
    TextView user_details;
    TextView user_email_textview;
    CircleImageView img;
    View hView;
    String Base_URL;
    Merlin merlin;
    MerlinsBeard merlinsBeard;
    SharedPreferences sp;

    private List<WashRequest> washRequests = new ArrayList<>();
    private RecyclerView recyclerView;
    private ValetWashRequestAdapter mAdapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar progressBar;
    private static boolean refreshing = false;
    String CHANNEL_ID = "MS1235";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_valet_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_valet);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_valet);
        navigationView.setNavigationItemSelectedListener(this);

        hView = navigationView.getHeaderView(0);
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
        Base_URL = credentials.server_url;
        back_pressed_count = 0;
        UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        String name = pref.name;
        String surname = pref.surname;
        last_name = surname;
        String email = pref.email;
        phone_number = pref.contact_number;
        gender = pref.gender;
        user_id = pref.id;
        picture_url = pref.profile_picture_url;
        Log.d("full name", name +" "+surname);
        user_email = email;
        user_details = (TextView) hView.findViewById(R.id.user_name);
        user_email_textview = (TextView) hView.findViewById(R.id.user_email);
        img = hView.findViewById(R.id.user_image);
        Picasso.get().load(Base_URL+"storage/"+picture_url).placeholder(R.drawable.placeholder).into(img);
        user_details.setText(name + "  " + surname);
        user_email_textview.setText(email);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        //Log.d("Payments", "Payments");
        if (!sp.getBoolean("logged", true)) {
            Intent intent = new Intent(CarValetMainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        merlin = new Merlin.Builder().withConnectableCallbacks().build(this);
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                // Do something you haz internet!
            }
        });
        merlinsBeard = MerlinsBeard.from(this);
        if (merlinsBeard.isConnected()) {
            // Connected, do something!

        } else {
            // Disconnected, do something!
            Toast.makeText(this,"Yo are not connected to the internet",Toast.LENGTH_LONG);
        }

        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing = true;
//                getServices();
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.valet_wash_requests_list);
        mLayoutManager = new LinearLayoutManager(CarValetMainActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        getServices();
    }

    public void setAdapter(){
//        Log.d("Setting","Adapter");
//        Log.d("Setting9",washRequests.get(0).description);
        mAdapter = new ValetWashRequestAdapter(washRequests,CarValetMainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(CarValetMainActivity.this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getServices(){
//        progressBar.setVisibility(View.VISIBLE);
        washRequests = new ArrayList<WashRequest>();
        RequestQueue requestQueue = Volley.newRequestQueue(CarValetMainActivity.this);
        Credentials credentials = EasyPreference.with(CarValetMainActivity.this).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(CarValetMainActivity.this).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get-valet-requests";
        if(!refreshing){
            progressBar.setVisibility(View.VISIBLE);
        }
        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("requests");
                    Log.d("Response requests",response_obj.toString());
                    if (response_obj.length() > 0) {
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            WashRequest request = gson.fromJson(element, WashRequest.class);
                            washRequests.add(request);
                        }
//                        setAdapter();
                    }else{
                        Price price = new Price("empty","No Open requests","None","none",0,"No open requests.",null);
                        WashRequest request = new WashRequest("empty",null,price,"No Open Requests.",null,null,"test date","","","","","");
                        washRequests.add(request);

                    }
                    setAdapter();
                    progressBar.setVisibility(View.INVISIBLE);
                    pullToRefresh.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error2",e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("error", error.toString());

            }
        });
        requestQueue.add(provinceRequest);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_client);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            back_pressed_count++;
            if (back_pressed_count == 1) {
                Toast.makeText(CarValetMainActivity.this, "Press Back again to Logout", Toast.LENGTH_SHORT).show();
            } else {
                sp = getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putBoolean("logged", false).apply();
//                AppDatabase.destroyInstance();
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.car_valet_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout_valet) {
            // Handle the camera action
            showLogoutConfirmDialog();
        }else if(id == R.id.nav_settings_valet){
            Intent intent = new Intent(CarValetMainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_about_valet){
            Intent intent = new Intent(CarValetMainActivity.this, AboutEcospotlessActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_help_valet) {
            Intent intent = new Intent(CarValetMainActivity.this, HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_valet);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showLogoutConfirmDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CarValetMainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(CarValetMainActivity.this);
        final View view = inflater.inflate(R.layout.logout_dialog, null);
        alertDialogBuilder.setView(view);
//        alertDialogBuilder.setTitle("Image Caption");
//        final EditText captionText = view.findViewById(R.id.caption_text);
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });
            }
        });
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().putBoolean("logged", false).apply();
//                        AppDatabase.destroyInstance();
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
