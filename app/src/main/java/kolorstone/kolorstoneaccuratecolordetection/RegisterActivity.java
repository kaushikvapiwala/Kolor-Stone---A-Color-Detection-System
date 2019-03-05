package kolorstone.kolorstoneaccuratecolordetection;

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

public class RegisterActivity extends AppCompatActivity {

    EditText Et_email,Et_password,Et_password1;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Et_email=findViewById(R.id.et_email);
        Et_password=findViewById(R.id.et_password);
        Et_password1=findViewById(R.id.et_password1);
        register=findViewById(R.id.bt_register);
    }

    public void OnRegister(View view)
    {
        String type="Register";
        String email=Et_email.getText().toString().trim();
        String password=Et_password.getText().toString().trim();
        String repassword=Et_password1.getText().toString().trim();
        if(isNetworkAvailable()==true) {
            if ((!email.isEmpty()) && (!password.isEmpty()) && (!repassword.isEmpty())) {
                if ((password.length() > 6) && (repassword.length() > 6)) {
                    if (password.equals(repassword)) {
                        Toast.makeText(this,email+"\n"+password,Toast.LENGTH_LONG).show();
                        BackgroundWorker worker = new BackgroundWorker(this);
                        worker.execute(type, email, password);
                        try {
                            String result = worker.get();
                            if (result.equals("1")) {
                                super.onBackPressed();
                                Et_email.setText("");
                                Et_password.setText("");
                                Et_password1.setText("");
                            }
                            else if(result.equals("2")){
                                Et_email.setText("");
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Username already exists"+"\n"+"please use different username for registration")
                                        .setTitle("Error")
                                        .setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else
                            {
                                Toast.makeText(this,"Connection fail",Toast.LENGTH_LONG).show();
                                Toast.makeText(this,result,Toast.LENGTH_LONG).show();

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Password doesn't match ")
                                .setTitle("Error")
                                .setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Password is too short")
                            .setTitle("Error")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please fill all the fields")
                        .setTitle("Error")
                        .setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else
        {
            final android.support.v7.app.AlertDialog.Builder builder1=new android.support.v7.app.AlertDialog.Builder(this);
            builder1.setTitle("Connection Problem");
            builder1.setMessage("Please Connect to Internet");
            builder1.setPositiveButton("OK", null);
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
