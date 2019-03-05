package kolorstone.kolorstoneaccuratecolordetection;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import kolorstone.kolorstoneaccuratecolordetection.Custom.BackgroundWorker;


public class ColorPicker extends AppCompatActivity  {

    Button adddb;

    TextView rgbcp,cmykcp,hslcp,hexcp;
    int r,g,b;
    String id;
    int colorint;
    float cmyk1[]=new float[4];
    float hsl[]=new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);



        rgbcp=(TextView)findViewById(R.id.RGBcp);
        cmykcp=(TextView)findViewById(R.id.CMYKcp);
        hslcp=(TextView)findViewById(R.id.HSLcp);
        hexcp=(TextView)findViewById(R.id.HEXcp);
        DecimalFormat dec = new DecimalFormat("#0.000");




        adddb=(Button)findViewById(R.id.addtodb);

        SharedPreferences sharedPreferences = this.getSharedPreferences("Info", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "0");

        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);


        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {
                // Handle on color change
                Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
            }
        });
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                Toast.makeText(
                        ColorPicker.this,
                        "selectedColor: " + Integer.toHexString(selectedColor).toUpperCase(),
                        Toast.LENGTH_SHORT).show();


                 r = (selectedColor >> 16) & 0xFF;
                g = (selectedColor >> 8) & 0xFF;
                 b = (selectedColor >> 0) & 0xFF;

                rgbcp.setText("R,G,B: "+r+","+g+","+b);
                Color.RGBToHSV(r,g,b, hsl);
                hslcp.setText("H,S,L: "+dec.format(hsl[0])+","+dec.format(hsl[1])+","+dec.format(hsl[2]));
                 colorint=getIntFromColor(r,g,b);
                 cmyk1 = Colour.colorToCMYK(colorint);
                cmykcp.setText("C,M,Y,K: "+ dec.format(cmyk1[0])+","+dec.format(cmyk1[1])+","+dec.format(cmyk1[2])+","+dec.format(cmyk1[3]));
                hexcp.setText("HEX: "+Integer.toHexString(selectedColor).toUpperCase());




            }
        });

        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {

                r = (selectedColor >> 16) & 0xFF;
                g = (selectedColor >> 8) & 0xFF;
                b = (selectedColor >> 0) & 0xFF;

                rgbcp.setText("R,G,B: "+r+","+g+","+b);
                Color.RGBToHSV(r,g,b, hsl);
                hslcp.setText("H,S,L: "+hsl[0]+","+hsl[1]+","+hsl[2]);
                colorint=getIntFromColor(r,g,b);
                cmyk1 = Colour.colorToCMYK(colorint);
                cmykcp.setText("C,M,Y,K: "+cmyk1[0]+","+cmyk1[1]+","+cmyk1[2]+","+cmyk1[3]);
                hexcp.setText("HEX: #"+Integer.toHexString(selectedColor));

            }
        });


        adddb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String c=r+","+g+","+b;
                BackgroundWorker worker = new BackgroundWorker(ColorPicker.this);
                worker.execute("Set", id, c);
                try {
                    String result = worker.get();
                    if (result.equals("1")) {
                        Toast.makeText(ColorPicker.this,"Successful",Toast.LENGTH_LONG).show();
                    }
                    else if(result.equals("2")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ColorPicker.this);
                        builder.setMessage("Color already exists")
                                .setTitle("Error")
                                .setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else
                    {
                        Toast.makeText(ColorPicker.this,"Connection fail",Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    }

