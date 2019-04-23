package com.organizze.jmdevelopers.blogapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.organizze.jmdevelopers.blogapp.R;

public class LoginActivity extends AppCompatActivity {
    private EditText email, senha;
    private Button botao;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.logemail);
        senha = findViewById(R.id.logsenha);
        botao = findViewById(R.id.logbotao);
        progressBar=findViewById(R.id.progressBarlog);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String campoemail = email.getText().toString();
                final String camposenha = senha.getText().toString();
                botao.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (campoemail.isEmpty() || camposenha.isEmpty()) {
                    showmenssagem("preencha os campos !");

                } else {
                    logar(campoemail, camposenha);

                }


            }
        });


    }

    private void logar(String campoemail, String camposenha) {
        firebaseAuth.signInWithEmailAndPassword(campoemail, camposenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    botao.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    updateUI();


                }else{

                    showmenssagem("erro"+task.getException().getMessage());
                }
            }
        });
    }

    private void showmenssagem(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(), Home.class);
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
