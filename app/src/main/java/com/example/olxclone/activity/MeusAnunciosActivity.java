package com.example.olxclone.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxclone.R;
import com.example.olxclone.adapter.AdapterAnuncios;
import com.example.olxclone.databinding.ActivityMeusAnunciosBinding;
import com.example.olxclone.helper.ConfiguracaoFirebase;
import com.example.olxclone.helper.RecyclerItemClickListener;
import com.example.olxclone.model.Anuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMeusAnunciosBinding binding;

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeusAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //configuraçoes iniciais
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios")
                        .child( ConfiguracaoFirebase.getIdUsuario() );


        setSupportActionBar(binding.toolbar);
        setTitle("Meus Anúncios");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.fab.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

        });

        //Configurando recyclerView

        RecyclerView recyclerAnuncios = findViewById(R.id.recyclerAnuncios);

        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter( adapterAnuncios );

        // Recupera anuncios para o usuario
        recuperaAnuncios();

        //adicionar eventos de click no recyclerView
        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onLongItemClick(View view, int position) {

                Anuncio anuncioSelecionado = anuncios.get(position);
                anuncioSelecionado.removerAnuncio();

                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void recuperaAnuncios(){

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Anúncios")
                .setCancelable(false).build();
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anuncios.clear();
                for ( DataSnapshot ds : snapshot.getChildren()){
                    anuncios.add( ds.getValue( Anuncio.class ) );
                }

                Collections.reverse( anuncios );
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}