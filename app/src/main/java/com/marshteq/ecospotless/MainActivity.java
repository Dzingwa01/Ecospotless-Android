package com.marshteq.ecospotless;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.iamhabib.easy_preference.EasyPreference;
import com.marshteq.ecospotless.Client.ClientMainActivity;
import com.marshteq.ecospotless.Helpers.Credentials;
import com.marshteq.ecospotless.Models.UserPref;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_admin);
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_logout_admin) {
            // Handle the camera action
            showLogoutConfirmDialog();
        }
// else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            back_pressed_count++;
            if (back_pressed_count == 1) {
                Toast.makeText(MainActivity.this, "Press Back again to Logout", Toast.LENGTH_SHORT).show();
            } else {
                sp = getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putBoolean("logged", false).apply();
//                AppDatabase.destroyInstance();
                finish();
            }

        }
    }

    public void showLogoutConfirmDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
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
