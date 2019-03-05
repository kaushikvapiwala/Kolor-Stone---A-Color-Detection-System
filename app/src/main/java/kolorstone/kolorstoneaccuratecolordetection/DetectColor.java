package kolorstone.kolorstoneaccuratecolordetection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import kolorstone.kolorstoneaccuratecolordetection.Custom.BackgroundWorker;


/**
 * Created by Kaushik on 07-10-2017.
 */

public class DetectColor  extends AppCompatActivity {

    private static final String TAG = "DetectColor" ;
    Button getData;
    TextView txtArduino;
    Handler h;
    String id;

    TextView red;
    TextView green;
    TextView blue;

    TextView cmyk;
    TextView hsl;
    TextView hex1;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    ImageView rectColor;
    String sbprint;



    float cyan = 0.0f;
    float magenta = 0.0f;
    float yellow = 0.0f;
    float black = 0.0f;



    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "98:D3:31:F6:1D:26";
    int old[]= new int[3];
    int color[]=new int[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detectcolor);

        SharedPreferences sharedPreferences = this.getSharedPreferences("Info", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "0");


        getData = (Button)findViewById(R.id.button2);
        txtArduino = (TextView) findViewById(R.id.textView);      // for display the received data from the Arduino
        rectColor=(ImageView)findViewById(R.id.rectimage);

        cmyk =(TextView)findViewById(R.id.CMYK);
        hsl=(TextView)findViewById(R.id.HSL);
        hex1=(TextView)findViewById(R.id.HEX);





        Drawable background = rectColor.getBackground();



        float[] hsv = new float[3];

        DecimalFormat dec = new DecimalFormat("#0.000");





        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());                                      // and clear
                         //   txtArduino.setText(sbprint);            // update TextView
                            int color[]=new int[3];
                            String colors[]=sbprint.split(",");
                            color[0]=Integer.parseInt(colors[0]);
                            color[1]=Integer.parseInt(colors[1]);
                            color[2]=Integer.parseInt(colors[2]);
                          //  int endcolor= Color.rgb(color[0],color[1],color[2]);
                            /*

                            String redhex=Integer.toHexString(color[0]);
                            String greenhex=Integer.toHexString(color[1]);
                            String bluehex=Integer.toHexString(color[2]);
                            String FinalcolorHex=redhex+greenhex+bluehex;

                            */

                            boolean judge=similar(old,color);


                            // GradientDrawable gd = (GradientDrawable) rectColor.getBackground().getCurrent(); //To shange the solid color
                            // gd.setColor(Color.argb(255,color[0],color[1],color[2]));

                            int colorint;
                            if(judge==false) {
                                rectColor.setBackgroundColor(Color.rgb(color[0], color[1], color[2]));
                              //  convertRGBtoCMYK(color[0],color[1],color[2]);
                                Color.RGBToHSV(color[0],color[1],color[2], hsv);
                                String hex = String.format("#%02X%02X%02X", color[0],color[1],color[2]);
                                colorint=getIntFromColor(color[0],color[1],color[2]);
                                float[] cmyk1 = Colour.colorToCMYK(colorint);
                                txtArduino.setText("R:"+color[0]+", G:"+color[1]+", B:"+color[2]);
                                cmyk.setText("C:"+dec.format(cmyk1[0])+", M:"+dec.format(cmyk1[1])+", Y:"+dec.format(cmyk1[2])+", K:"+dec.format(cmyk1[3]));
                                hsl.setText("H:"+dec.format(hsv[0])+", S:"+dec.format(hsv[1])+", V:"+dec.format(hsv[2]));
                                hex1.setText("HEX: "+hex);
                                old[0]= color[0];
                                old[1]= color[1];
                                old[2]= color[2];
                                String c= old[0]+","+old[1]+","+old[2];
                                BackgroundWorker worker = new BackgroundWorker(DetectColor.this);
                                worker.execute("Set", id, c);
                                try {
                                    String result = worker.get();
                                    if (result.equals("1")) {
                                        Toast.makeText(DetectColor.this,"Successful",Toast.LENGTH_LONG).show();
                                    }
                                    else if(result.equals("2")){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DetectColor.this);
                                        builder.setMessage("Color already exists")
                                                .setTitle("Error")
                                                .setPositiveButton("OK", null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                    else
                                    {
                                        Toast.makeText(DetectColor.this,"Connection fail",Toast.LENGTH_LONG).show();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }

                            else {
                                rectColor.setBackgroundColor(Color.rgb(old[0], old[1], old[2]));
                               // convertRGBtoCMYK(old[0],old[1],old[2]);
                                Color.RGBToHSV(old[0],old[1],old[2], hsv);
                                String hex = String.format("#%02X%02X%02X", old[0],old[1],old[2]);
                                colorint=getIntFromColor(old[0],old[1],old[2]);
                                float[] cmyk1 = Colour.colorToCMYK(colorint);
                                txtArduino.setText("R:"+old[0]+", G:"+old[1]+", B:"+old[2]);
                                cmyk.setText("C:"+dec.format(cmyk1[0])+", M:"+dec.format(cmyk1[1])+", Y:"+dec.format(cmyk1[2])+", K:"+dec.format(cmyk1[3]));
                                hsl.setText("H:"+dec.format(hsv[0])+", S:"+dec.format(hsv[1])+", V:"+dec.format(hsv[2]));
                                hex1.setText("HEX: "+hex);
                            }


                        }



                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }

            };
        };



        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        try {
            checkBTState();
        }
        catch (Exception e)
        {

        }
       // relay(color[0],color[1],color[2]);
        getData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //txtArduino.setText(sbprint);
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });


    }




    public boolean similar(int old[], int newc[])
    {
     //   Toast.makeText(DetectColor.this,old[0]+","+old[1]+","+old[2],Toast.LENGTH_LONG).show();
       // Toast.makeText(DetectColor.this,newc[0]+","+newc[1]+","+newc[2],Toast.LENGTH_LONG).show();
        if ((old[0]+15)>=newc[0] && (old[0]-15)<=newc[0] ) {
            if ((old[1] + 15) >= newc[1] && (old[1] - 15) <= newc[1]) {
                if ((old[2] + 15) >= newc[2] && (old[2] - 15) <= newc[2]) {
                    return true;
                }
            }
        }


        return false;
    }





    public int getIntFromColor(int Red, int Green, int Blue){
    Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
    Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
    Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
}



    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {


        if(Build.VERSION.SDK_INT >= 10){
            try {

                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
      BluetoothDevice device = btAdapter.getRemoteDevice(address);

            //BluetoothDevice device = getIntent().getExtras().getParcelable("btdevice");

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }


    private Context mContext;
    /*

    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }
    */
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (!btAdapter.isEnabled()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Press OK to enable bluetooth");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Enabling bluetooth");
                        btAdapter.enable();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();
            }
        }

    }


    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        Thread workerThread;
        byte[] readBuffer;
        int readBufferPosition;
        int counter;
        volatile boolean stopWorker;

        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character



        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}
