package kolorstone.kolorstoneaccuratecolordetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import kolorstone.kolorstoneaccuratecolordetection.Custom.BackgroundWorker;

public class LoginActivity extends AppCompatActivity {

    EditText Email,Password;
    Button login,newuser;
    String result;
    String loginstatus;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences=this.getSharedPreferences("Info",MODE_PRIVATE);
        String s= sharedPreferences.getString("status","0");
     //   Toast.makeText(this,s,Toast.LENGTH_LONG).show();
        if(s.equals("1"))
        {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email= (EditText) findViewById(R.id.Etemail);
        Password= (EditText) findViewById(R.id.Etpassword);
        login= (Button) findViewById(R.id.Btlogin);
        newuser= (Button) findViewById(R.id.Btregister);

    }
    public void OnNewuser(View view)
    {
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void OnLogin(View view)
    {
        String type="Login";
        String email=Email.getText().toString();
        String password=Password.getText().toString();
        if(isNetworkAvailable()==true)
        {
            if((!email.isEmpty())&&(!password.isEmpty())) {
                BackgroundWorker worker = new BackgroundWorker(this);
                worker.execute(type, email, password);
                try {
                    result = worker.get();

                    if (!(result.equals(null)) && !(result.equals("0"))) {
                        String id = result;
                        Toast.makeText(this,id+"successful",Toast.LENGTH_LONG).show();
                        loginstatus = "1";
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("status", "1");
                        editor.putString("id",id);
                        editor.commit();
                        finish();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Incorrect username and password")
                                .setTitle("Error")
                                .setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            final android.support.v7.app.AlertDialog.Builder builder1=new android.support.v7.app.AlertDialog.Builder(this);
            builder1.setTitle("Connection Problem");
            builder1.setMessage("Please Connect to Internet");
            builder1.setPositiveButton("Ok",null);
            builder1.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

  
