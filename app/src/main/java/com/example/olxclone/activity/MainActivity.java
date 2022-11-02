package com.example.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class MainActivity extends AppCompatActivity {

    private Button buttonAcessar;
    private EditText campoEmail, campoSenha;
    private SwitchMaterial switchAcesso;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        buttonAcessar.setOnClickListener(view -> {

            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if ( ! email.isEmpty() ){
                if ( ! senha.isEmpty() ){

                    //Verifica o estado do switch
                    if ( switchAcesso.isChecked() ){//Cadastro

                        autenticacao.createUserWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener(task -> {
                            if ( task.isSuccessful() ){
                                Toast.makeText(this, "Sucesso ao cadastrar usuario!", Toast.LENGTH_SHORT).show();

                            }else{

                                String excecao = "";
                                try {
                                    throw task.getException();
                                }catch ( FirebaseAuthWeakPasswordException e){
                                    excecao = "Digite uma senha mais forte!";
                                }catch ( FirebaseAuthInvalidCredentialsException e){
                                    excecao = "Por favor, digite um e-mail válido";
                                }catch ( FirebaseAuthUserCollisionException e){
                                    excecao = "Esta conta já foi cadastrada.";
                                }catch ( Exception e ){
                                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                    e.printStackTrace();
                                }

                                Toast.makeText(this, "Erro" + excecao,
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

                    }else{//Login

                        autenticacao.signInWithEmailAndPassword( email, senha).addOnCompleteListener(task -> {
                            if ( task.isSuccessful() ){

                                Toast.makeText(this, "Logado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(this, "Erro ao fazer logim :" + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else{
                    Toast.makeText(this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        switchAcesso = findViewById(R.id.switchAcesso);
        buttonAcessar = findViewById(R.id.buttonAcessar);
    }
}