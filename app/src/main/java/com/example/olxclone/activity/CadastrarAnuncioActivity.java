package com.example.olxclone.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxclone.R;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.Permissoes;
import com.example.olxclone.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoEstado, campoCategoria;
    private Anuncio anuncio;
    private StorageReference storage;
    private android.app.AlertDialog dialog;

    private final String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final List<String> listaFotosRecuperadas = new ArrayList<>();
    private final List<String> listaUrlFotos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        inicializarComponentes();
        carregarDadosSpinner();

        //Configuraçoes iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();


        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

    }

    public void validarDadosAnuncios(View view) {

        anuncio = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());

        if (listaFotosRecuperadas.size() != 0) {
            if (!anuncio.getEstado().isEmpty()) {
                if (!anuncio.getCategoria().isEmpty()) {
                    if (!anuncio.getTitulo().isEmpty()) {
                        if (!anuncio.getValor().isEmpty() && !valor.equals("0")) {
                            if (!anuncio.getTelefone().isEmpty()) {
                                if (!anuncio.getDescricao().isEmpty()) {

                                    salvarAnuncio();

                                } else {
                                    exibirMensagemErro("Adicione uma descrição ao anúncio!");
                                }
                            } else {
                                exibirMensagemErro("Preencha o campo telefone!");
                            }
                        } else {
                            exibirMensagemErro("Preencha o campo valor!");
                        }
                    } else {
                        exibirMensagemErro("Escolha um título para seu anúncio");
                    }
                } else {
                    exibirMensagemErro("Selecione uma categoria!");
                }
            } else {
                exibirMensagemErro("Selecione um estado!");
            }
        } else {
            exibirMensagemErro("Adicione pelo menos uma foto!");
        }

    }

    private void exibirMensagemErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void salvarAnuncio() {

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Salvando Anúncio")
                .setCancelable(false).build();
        dialog.show();

        // Salvar imagens no storage
        for (int i = 0; i < listaFotosRecuperadas.size(); i++) {
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);

        }
    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int contador) {

        //Criar nó no Storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                //.child("id_anuncio")
                .child("imagem" + contador);

        //Fazer upload do arquivo
        final UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Uri firebaseUrl = imagemAnuncio.getDownloadUrl();

                //===================================================
                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida = uri.toString();      //Esta url funciona!!!
                        listaUrlFotos.add(urlConvertida);

                        //Testa finalização de upload das imagens
                        if (listaUrlFotos.size() == totalFotos) { //todas as fotos salvas
                            anuncio.setFotos(listaUrlFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }
                    }
                });
                //===================================================
            }
        }).addOnFailureListener(e -> {

        });

    }

    private Anuncio configurarAnuncio() {

        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = Objects.requireNonNull(campoTelefone.getText()).toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissoesResultado : grantResults) {
            if (permissoesResultado == PackageManager.PERMISSION_DENIED) ;
            alertaValidacaoPermicao();

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            //Recuperar imagem
            assert data != null;
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no imageView
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                imagem2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    private void alertaValidacaoPermicao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void carregarDadosSpinner() {

        //CONFIGURAÇÃO SPINNER ESTADOS
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, estados);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

        //CONFIGURAÇÃO SPINNER ESTADOS
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);

    }

    private void inicializarComponentes() {

        campoTitulo = findViewById(R.id.editTitulo);
        campoValor = findViewById(R.id.editValor);
        campoDescricao = findViewById(R.id.editDescricao);
        campoTelefone = findViewById(R.id.editTelefone);
        campoEstado = findViewById(R.id.spinnerEstados);
        campoCategoria = findViewById(R.id.spinnerCategoria);

        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);

        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        //Configuração de localidade para pt -> portugues BR -> Brasil
        Locale local = new Locale("pt", "BR");
        campoValor.setLocale(local);
    }
}
