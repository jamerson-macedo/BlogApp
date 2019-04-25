package com.organizze.jmdevelopers.blogapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.organizze.jmdevelopers.blogapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistreActivity extends AppCompatActivity {
    CircleImageView imageView;
    static int code = 1;
    static int REQUESTCODE = 1;
    Uri pickimage;
    private EditText email, nome, senha, senha2;
    private Button registrar;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private TextView textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);
        imageView = findViewById(R.id.regimagem);
        email = findViewById(R.id.regemail);
        nome = findViewById(R.id.regnome);
        senha = findViewById(R.id.regsenha);
        senha2 = findViewById(R.id.regsenha2);
        registrar = findViewById(R.id.regbotao);
        progressBar = findViewById(R.id.progressBarlog);
        firebaseAuth = FirebaseAuth.getInstance();
        textView2=findViewById(R.id.abrirlogin);
        progressBar.setVisibility(View.INVISIBLE);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirlogin();
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registrar.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                final String campoemail = email.getText().toString();
                final String camponome = nome.getText().toString();
                final String camposenha = senha.getText().toString();
                final String camposenha2 = senha2.getText().toString();
                if (campoemail.isEmpty() || camponome.isEmpty() || camposenha.isEmpty() || camposenha2.isEmpty() || !camposenha.equals(camposenha2) || pickimage==null) {

                    mensagemdeErro("Por favor preencha os campos");
                    registrar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    criarconta(camponome, campoemail, camposenha);


                }

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // A versão do SDK do software atualmente em execução neste dispositivo de hardware.
                if (Build.VERSION.SDK_INT >= 22) {
                    verificarpermissao();

                } else {

                    abrirgaleria();

                }
            }
        });

    }

    private void criarconta(final String camponome, String campoemail, String camposenha) {
        firebaseAuth.createUserWithEmailAndPassword(campoemail, camposenha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mensagemdeErro("Cadastro realizado");
                    updateuserinfo(camponome, pickimage, firebaseAuth.getCurrentUser());
                    updateUI();
                    finish();


                } else {

                    mensagemdeErro("cadastro falhou" + task.getException().getMessage());
                    registrar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }
        });


    }

    private void updateuserinfo(final String camponome, final Uri pickimage, final FirebaseUser currentUser) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_photos");
        // obtem o ultimo caminho
        final StorageReference imagem = storageReference.child("user_photos/"+pickimage.getLastPathSegment());
        imagem.putFile(pickimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(camponome).setPhotoUri(pickimage).build();

                        currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mensagemdeErro("Registrado");

                                    updateUI();

                                }
                            }
                        });

                    }
                });

            }
        });


    }

    private void mensagemdeErro(String erro) {
        Toast.makeText(RegistreActivity.this, erro, Toast.LENGTH_LONG).show();


    }

    private void abrirgaleria() {
        // esse tipo voce coloca o tipo e ele abre
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUESTCODE);
    }
    public void abrirlogin(){
        Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(loginActivity);


    }

    private void verificarpermissao() {
        //e a permissão foi concedida ao pacote fornecido.
        if (ContextCompat.checkSelfPermission(RegistreActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (RegistreActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "Aceite a Permissão", Toast.LENGTH_LONG).show();

            } else {
                // Solicita que permissões sejam concedidas a esse aplicativo.
                ActivityCompat.requestPermissions(RegistreActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, code);

            }


        } else {

            abrirgaleria();


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            pickimage = data.getData();
            imageView.setImageURI(pickimage);


        }
    }
    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(),Home.class);
        startActivity(homeActivity);
        finish();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            updateUI();

        }
    }
}
