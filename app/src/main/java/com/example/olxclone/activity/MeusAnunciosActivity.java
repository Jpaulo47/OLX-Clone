package com.example.olxclone.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.olxclone.databinding.ActivityMeusAnunciosBinding;
import com.google.android.material.snackbar.Snackbar;

public class MeusAnunciosActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMeusAnunciosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMeusAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(view -> {


        });
    }
}