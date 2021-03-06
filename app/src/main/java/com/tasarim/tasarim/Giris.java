package com.tasarim.tasarim;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Giris extends AppCompatActivity {

    TextView kayitOl;
    FloatingActionButton giris;
    EditText kullaniciAdi,sifre;
    FirebaseAuth mAuth;

    ProgressBar progressBarlogin;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        
        getWindow().setBackgroundDrawableResource(R.drawable.arkaplangiris) ;


        this.setTitle("Giriş Yap");
        kayitOl=(TextView) findViewById(R.id.kayitOlSayfasi);
        giris=(FloatingActionButton) findViewById(R.id.giris);
        kullaniciAdi=(EditText)findViewById(R.id.kullaniciAdi);
        sifre=(EditText)findViewById(R.id.sifre);

        progressBarlogin=(ProgressBar)findViewById(R.id.progress_bar_login) ;

        progressBarlogin.setVisibility(View.INVISIBLE);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//preferences objesi
       editor = preferences.edit();

     

        if(preferences.getBoolean("login", false)){

            String rol=preferences.getString("rol","");
            if(rol.equals("müşteri")){
                Intent i = new Intent(getApplicationContext(),MusteriSayfasi.class);

                startActivity(i);
                finish();
            }
            else if(rol.equals("admin")){
                Intent i = new Intent(getApplicationContext(),AdminSayfasi.class);
                startActivity(i);
                finish();
            }

        }

        mAuth=FirebaseAuth.getInstance();

        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(Giris.this,KayitOl.class);
                startActivity(intent);
            }
        });

        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(kullaniciAdi.getText().toString())||TextUtils.isEmpty(sifre.getText().toString()))
                {
                    Toast.makeText(Giris.this, "Boş alan bırakmayınız", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    login(kullaniciAdi.getText().toString(),sifre.getText().toString());

                }
            }
        });


    }


    public void klavyekapat(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(kullaniciAdi.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(sifre.getWindowToken(), 0);
    }

    public void login(final String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Giris.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                    progressBarlogin.setVisibility(View.VISIBLE);

                   editor.putBoolean("login",true);
                    editor.putString("eposta",email);
                    editor.commit();

                    DatabaseReference dbIsimler= FirebaseDatabase.getInstance().getReference().child("Müşteri").child(mAuth.getCurrentUser().getUid());

                    dbIsimler.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String admin=dataSnapshot.child("admin").getValue().toString();

                            if(admin.equals("1"))
                            {
                                editor.putBoolean("login", true);
                                    Intent intent = new Intent(Giris.this, AdminSayfasi.class);
                                    startActivity(intent);
                                    finish();


                            }
                            else {

                                editor.putBoolean("login", true);
                                    Intent i = new Intent(getApplicationContext(),MusteriSayfasi.class);
                                    startActivity(i);
                                    finish();


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
                else{
                    Toast.makeText(Giris.this, "Giriş Başarısız", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
