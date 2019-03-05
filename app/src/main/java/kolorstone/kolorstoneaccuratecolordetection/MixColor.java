package kolorstone.kolorstoneaccuratecolordetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kolorstone.kolorstoneaccuratecolordetection.Custom.BackgroundWorker;
import kolorstone.kolorstoneaccuratecolordetection.Custom.ColorDetails;

public class MixColor extends AppCompatActivity {

    Spinner spinner1, spinner2;
    SharedPreferences sharedPreferences;
    String type = "Getdata";
    String result;
    Button mix;
    TextView mixtext;
    ArrayList<ColorDetails> list;

    Button button4;
    Spinner first;
    Spinner second;

    int finalcolor1[];

    ImageView rectColor;

    ColorDetails currentItem1;

    int R1,R2,G1,G2,B1,B2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_color);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        mix = findViewById(R.id.mixbutton);
        mixtext = findViewById(R.id.mixedcolor);


        rectColor=findViewById(R.id.rectimage1);

        first=findViewById(R.id.spinner1);
        second=findViewById(R.id.spinner2);

        finalcolor1=new int[6];




        sharedPreferences = this.getSharedPreferences("Info", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "0");

        BackgroundWorker worker = new BackgroundWorker(this);
        worker.execute(type, id);
        try {
            result = worker.get();
           // Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        list = new ArrayList<>();
        while (result.length() > 1) {
            int index = result.indexOf("|");
            int length = result.length();
            String s = result.substring(0, index);
            result = result.substring(index + 1, length);
            list.add(new ColorDetails(s));
        }

        ColorAdapter1 mAdapter = new ColorAdapter1(this, list);
        spinner1.setAdapter(mAdapter);
        spinner2.setAdapter(mAdapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ColorDetails details=list.get(i);
                String color1=details.getColorvalue();
                int index=color1.indexOf(",");
                int length=color1.length();
                 R1= Integer.parseInt(color1.substring(0,index));
                color1=color1.substring(index+1,length);

                index=color1.indexOf(",");
                length=color1.length();
               G1= Integer.parseInt(color1.substring(0,index));
                color1=color1.substring(index+1,length);

                 B1= Integer.parseInt(color1);

                String s1=R1+","+G1+","+B1;
                Toast.makeText(MixColor.this,s1,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ColorDetails details=list.get(i);
                String color2=details.getColorvalue();
                int index=color2.indexOf(",");
                int length=color2.length();
                 R2= Integer.parseInt(color2.substring(0,index));
                color2=color2.substring(index+1,length);

                index=color2.indexOf(",");
                length=color2.length();
                 G2= Integer.parseInt(color2.substring(0,index));
                color2=color2.substring(index+1,length);

                 B2= Integer.parseInt(color2);

                String s2=R2+","+G2+","+B2;
                Toast.makeText(MixColor.this,s2,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Drawable background = rectColor.getBackground();

        int color1s;
        int color2s;

        mix.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                rectColor.setBackgroundColor(Color.rgb((R1+R2)/2,(G1+G2)/2,(B1+B2)/2));

            }
        });



    }






    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public int[] MixColor(String firstcolor, String secondcolor) {



        int color1[] = new int[3];
        int color2[] = new int[3];


         firstcolor = first.getSelectedItem().toString();
         secondcolor = second.getSelectedItem().toString();
        int finalcolor[]=new int[6];

        String colors1[]=firstcolor.split(",");
        color1[0]=Integer.parseInt(colors1[0]);
        color1[1]=Integer.parseInt(colors1[1]);
        color1[2]=Integer.parseInt(colors1[2]);

        String colors2[]=secondcolor.split(",");
        color2[0]=Integer.parseInt(colors2[0]);
        color2[1]=Integer.parseInt(colors2[1]);
        color2[2]=Integer.parseInt(colors2[2]);

        for(int i =0;i<3;i++)
            finalcolor[i]=color1[i];

        for(int i=0;i<3;i++)
            finalcolor[i+3]=color1[i];

        return finalcolor;




    }

        public class ColorAdapter1 extends ArrayAdapter<ColorDetails> {
            public ColorAdapter1(Context context, List<ColorDetails> objects) {
                super(context, 0, objects);
            }


            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return initView(position, convertView, parent);

            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return initView(position, convertView, parent);
            }

            private View initView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.customlist, parent, false);
                }

                ImageView imageViewColor = convertView.findViewById(R.id.ivcolor);
                TextView textViewColor = convertView.findViewById(R.id.tvcolor);

                ColorDetails currentItem = getItem(position);

                if (currentItem != null) {
                    textViewColor.setText(currentItem.getColorvalue());

                    String value = currentItem.getColorvalue();
                    int index = value.indexOf(",");
                    int length = value.length();
                    int R = Integer.parseInt(value.substring(0, index));
                    value = value.substring(index + 1, length);

                    index = value.indexOf(",");
                    length = value.length();
                    int G = Integer.parseInt(value.substring(0, index));
                    value = value.substring(index + 1, length);

                    int B = Integer.parseInt(value);

                    imageViewColor.setBackgroundColor(Color.rgb(R, G, B));
                }

                return convertView;
            }
        }
    }

