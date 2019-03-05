package kolorstone.kolorstoneaccuratecolordetection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kolorstone.kolorstoneaccuratecolordetection.Custom.BackgroundWorker;
import kolorstone.kolorstoneaccuratecolordetection.Custom.ColorDetails;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Bundle bundle = null;
    String type="Getdata";
    SharedPreferences sharedPreferences;
    String result;
    ListView listView;
    ArrayList<ColorDetails> details;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String id;
    int index,length;
    String s;
    ColorAdapter colorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("KolorStone:Color Detection");



        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        //mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.addDrawerListener(toggle);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        sharedPreferences=this.getSharedPreferences("Info",MODE_PRIVATE);
        id= sharedPreferences.getString("id","0");

        BackgroundWorker worker = new BackgroundWorker(this);
        worker.execute(type, id);
        try {
            result=worker.get();
            // Toast.makeText(this,result,Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        listView=findViewById(R.id.lvcolors);
        details=new ArrayList<>();

        colorAdapter = new ColorAdapter(this, details);

        try {
            while (result.length() > 1) {
                index = result.indexOf("|");
                length = result.length();
                s = result.substring(0, index);
                result = result.substring(index + 1, length);
                details.add(new ColorDetails(s));
            }

            listView.setAdapter(colorAdapter);
            registerForContextMenu(listView);
            registerForContextMenu(listView);
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Connection fail to Database Failed",Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BackgroundWorker worker = new BackgroundWorker(MainActivity.this);
                worker.execute(type, id);
                try {
                    result=worker.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                listView=findViewById(R.id.lvcolors);
                details=new ArrayList<>();

                colorAdapter = new ColorAdapter(MainActivity.this, details);

                try {
                    while (result.length() > 1) {
                        index = result.indexOf("|");
                        length = result.length();
                        s = result.substring(0, index);
                        result = result.substring(index + 1, length);
                        details.add(new ColorDetails(s));
                    }

                    listView.setAdapter(colorAdapter);
                    registerForContextMenu(listView);
                    registerForContextMenu(listView);
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Connection fail to Database Failed",Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                listView.setAdapter(colorAdapter);
                registerForContextMenu(listView);

            }


        });





    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        /*
        if (id == R.id.action_logout) {
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            String loginstatus="0";
            SharedPreferences.Editor editor = getSharedPreferences("Info", MODE_PRIVATE).edit();
            editor.putString("status",loginstatus);
            editor.commit();
            this.finish();
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_connectbluetooth) {
            Intent intent = new Intent(this, ConnecBluetooth.class);
            startActivity(intent);
            // Handle the camera action
        }
        else if (id == R.id.nav_detectcolor) {
            Intent intent=new Intent(this,DetectColor.class);
            startActivity(intent);

        }  else if (id == R.id.nav_mixcolor) {
            Intent intent=new Intent(this,MixColor.class);
            startActivity(intent);
        }
/*
        else if (id == R.id.nav_calculator) {
            Intent intent=new Intent(this,Calculator.class);
            startActivity(intent);
        }

        */

        else if (id == R.id.nav_color_picker) {
            Intent intent=new Intent(this,ColorPicker.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class ColorAdapter extends ArrayAdapter<ColorDetails> {
        public ColorAdapter(Context context, List<ColorDetails> objects) {
            super(context, -1, objects);
        }

        class ColorNameHolder {
            TextView name;
            ImageView imageView;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ColorNameHolder holder = null;
            if (convertView == null) {

                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.customlist, null);
                TextView colorname = convertView.findViewById(R.id.tvcolor);
                ImageView colorimage=convertView.findViewById(R.id.ivcolor);
                holder = new ColorNameHolder();
                holder.name = colorname;
                holder.imageView = colorimage;
                convertView.setTag(holder);

            } else {
                holder = (ColorNameHolder) convertView.getTag();
            }
            ColorDetails d = getItem(position);


            holder.name.setText(d.getColorvalue());

            String value=d.getColorvalue();
            int index=value.indexOf(",");
            int length=value.length();
            int R= Integer.parseInt(value.substring(0,index));
            value=value.substring(index+1,length);

            index=value.indexOf(",");
            length=value.length();
            int G= Integer.parseInt(value.substring(0,index));
            value=value.substring(index+1,length);

            int B= Integer.parseInt(value);
            holder.name.setText("R: "+R+", G: "+G+", B: "+B);
            holder.imageView.setBackgroundColor(Color.rgb(R,G,B));

            return convertView;
        }
    }

}
