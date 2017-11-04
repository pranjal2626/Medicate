package com.medical.medicate;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class Navigation_SOS extends AppCompatActivity
{
    private Button save_edit,activate_deactivate;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private EditText enumber1,enumber2,enumber3;
    private String enumber1_value,enumber2_value,enumber3_value;
    public final static int REQUEST_CODE = 10101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_sos);

        mDatabase = FirebaseDatabase.getInstance();

        save_edit = (Button) findViewById(R.id.save_edit);
        activate_deactivate = (Button) findViewById(R.id.activate_deactivate);
        enumber1 = (EditText) findViewById(R.id.navigation_sos_number1);
        enumber2 = (EditText) findViewById(R.id.navigation_sos_number2);
        enumber3 = (EditText) findViewById(R.id.navigation_sos_number3);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.getReference().child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                SOS sos = dataSnapshot.child("SOS_Service").getValue(SOS.class);
                if (sos != null) {
                    enumber1.setText(sos.number1);
                    enumber2.setText(sos.number2);
                    enumber3.setText(sos.number3);
                    enumber1.setEnabled(false);
                    enumber2.setEnabled(false);
                    enumber3.setEnabled(false);
                    save_edit.setText("Edit");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Read", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        activate_deactivate.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       String check_activate=activate_deactivate.getText().toString();
                                                       Log.d("Success",""+check_activate);
                                                       if(check_activate.equals("Start"))
                                                       {
                                                           Log.d("Here","Inside Activate");
                                                           if (checkDrawOverlayPermission())
                                                           {

                                                               startService(new Intent(getApplicationContext(), Navigation_SOS_Service.class));
                                                               activate_deactivate.setText("Stop");
                                                           }
                                                       }
                                                       if(check_activate.equals("Stop"))
                                                       {
                                                           stopService(new Intent(getApplicationContext(), Navigation_SOS_Service.class));
                                                           activate_deactivate.setText("Start");
                                                       }

                                                   }
                                               });
            save_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String check=save_edit.getText().toString();
                    Log.d("Success",check+"");
                    if(check=="Save")
                    {
                    enumber1_value = enumber1.getText().toString();
                    enumber2_value = enumber2.getText().toString();
                    enumber3_value = enumber3.getText().toString();
                    if (validateNumber())
                    {
                        mReference = mDatabase.getReference();
                        mReference.push().getKey();
                        SOS sos = new SOS(enumber1_value, enumber2_value, enumber3_value);
                        mReference.child("users").child(userId).child("SOS_Service").setValue(sos);
                    }
                        enumber1.setText(enumber1_value);
                        enumber2.setText(enumber2_value);
                        enumber3.setText(enumber3_value);
                        enumber1.setEnabled(false);
                        enumber2.setEnabled(false);
                        enumber3.setEnabled(false);
                        save_edit.setText("Edit");
                    }
                    if(check=="Edit")
                    {
                        enumber1.setEnabled(true);
                        enumber2.setEnabled(true);
                        enumber3.setEnabled(true);
                        save_edit.setText("Save");
                    }
                }
            });

    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, Navigation_SOS_Service.class));
            }
        }
    }

    public boolean validateNumber()
    {
        if (enumber1_value.length()!=10)
        {
            enumber1.setError("Invalid Phone Number");
            enumber1.requestFocus();
            return false;
        }
        if (enumber2_value.length()!=10)
        {
            enumber2.setError("Invalid Phone Number");
            enumber2.requestFocus();
            return false;
        }
        if (enumber3_value.length()!=10)
        {
            enumber3.setError("Invalid Phone Number");
            enumber3.requestFocus();
            return false;
        }
        return true;
    }

    @IgnoreExtraProperties
    public static class SOS
    {
        public String number1;
        public String number2;
        public String number3;

        public SOS() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public SOS(String number1, String number2,String number3)
        {
            this.number1 = number1;
            this.number2 = number2;
            this.number3 = number3;
        }

    }

    }

