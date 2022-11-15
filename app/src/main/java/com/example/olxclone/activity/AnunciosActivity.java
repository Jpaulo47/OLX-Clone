package com.example.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.olxclone.R;
import com.example.olxclone.adapter.AdapterAnuncios;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.RecyclerItemClickListener;
import com.example.olxclone.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        //inicializando componentes
        buttonCategoria = findViewById(R.id.buttonCategoria);
        buttonRegiao = findViewById(R.id.buttonRegiao);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios");
        //autenticacao.signOut();

        //Configurando recyclerView

        RecyclerView recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);

        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();

        //Aplicar evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerAnunciosPublicos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Anuncio anuncioSelecionado = listaAnuncios.get(position);
                Intent i = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                i.putExtra("anuncioSelecionado", anuncioSelecionado);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    public void filtarPorEstados(View view) {

        AlertDialog.Builder dialogEstados = new AlertDialog.Builder(this);
        dialogEstados.setTitle("Selecione o estado desejado");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        //CONFIGURAÇÃO SPINNER ESTADOS
        Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, estados);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        dialogEstados.setView(viewSpinner);

        dialogEstados.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;

            }
        });

        dialogEstados.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogEstados.create();
        dialog.show();
    }

    public void filtarPorCategoria(View view) {

        if (filtrandoPorEstado == true) {

            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
            dialogCategoria.setTitle("Selecione a categoria desejada");

            //Configurar spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //CONFIGURAÇÃO SPINNER ESTADOS
            Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            String[] estados = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, estados);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapter);

            dialogCategoria.setView(viewSpinner);

            dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();

                }
            });

            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategoria.create();
            dialog.show();

        } else {
            Toast.makeText(this, "  Escolha primeiro uma região", Toast.LENGTH_SHORT).show();
        }
    }

    private void recuperarAnunciosPorCategoria() {

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios")
                .child(filtroEstado)
                .child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaAnuncios.clear();

                for (DataSnapshot anuncios : snapshot.getChildren()) {
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarAnunciosPorEstado() {

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaAnuncios.clear();

                for (DataSnapshot categorias : snapshot.getChildren()) {
                    for (DataSnapshot anuncios : categorias.getChildren()) {
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);

                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarAnunciosPublicos() {

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Anúncios")
                .setCancelable(false).build();
        dialog.show();

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot estados : snapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot anuncios : categorias.getChildren()) {
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);

                        }
                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (autenticacao.getCurrentUser() == null) {//Usuario Deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);

        } else {//Usuario Logado

            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                break;

            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

            case R.id.menu_limpar_filtros:
                //recuperarAnunciosPublicos();
                break;


        }

        return super.onOptionsItemSelected(item);
    }
}