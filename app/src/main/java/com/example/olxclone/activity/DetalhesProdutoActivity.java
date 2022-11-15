package com.example.olxclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.olxclone.R;
import com.example.olxclone.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.Objects;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo, descricao, estado, preco;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        inicializarComponentes();

        //Configura Toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Recupera anuncio para exibição
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
        if ( anuncioSelecionado!= null ){

            titulo.setText( anuncioSelecionado.getTitulo() );
            descricao.setText( anuncioSelecionado.getDescricao() );
            estado.setText( anuncioSelecionado.getEstado() );
            preco.setText( anuncioSelecionado.getValor() );

            ImageListener imageListener = (position, imageView) -> {

                String urlString = anuncioSelecionado.getFotos().get( position );
                Picasso.get().load( urlString ).into(imageView);
            };

            carouselView.setPageCount( anuncioSelecionado.getFotos().size() );
            carouselView.setImageListener( imageListener );
        }
    }

    public void visualizarTelefone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado
                .getTelefone(), null));
        startActivity( i );

    }

    private void inicializarComponentes(){

        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        preco = findViewById(R.id.textPrecoDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        estado = findViewById(R.id.textEstadoDetalhe);
    }

    @Override
    public boolean onSupportNavigateUp() {//Método para finalizar activity atual
        finish();
        return false;
    }
}