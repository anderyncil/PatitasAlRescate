package com.patitasalrescate.Controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOMascota;
import com.patitasalrescate.model.Mascota;
import com.patitasalrescate.ui.AdaptadorMascotas;

import java.util.ArrayList;
import java.util.List;

public class ActividadListarMascotas extends AppCompatActivity {

    private RecyclerView recyclerMascotas;
    private TextView txtListaVacia;
    private DAOMascota daoMascota;
    private AdaptadorMascotas adaptador;
    private List<Mascota> listaMascotas;

    // Variable clave para saber el rol
    private boolean esModoRefugio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_listar_mascotas);

        // 1. Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarListarMascotas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // Flecha atrás cierra la actividad


        recyclerMascotas = findViewById(R.id.recycler_mascotas);
        txtListaVacia = findViewById(R.id.txt_lista_vacia);
        recyclerMascotas.setLayoutManager(new LinearLayoutManager(this));

        daoMascota = new DAOMascota(this);
        listaMascotas = new ArrayList<>();


        if (getIntent().hasExtra("es_refugio_key")) {
            esModoRefugio = getIntent().getBooleanExtra("es_refugio_key", false);
        }

        if (esModoRefugio) {
            getSupportActionBar().setTitle("Gestionar Mascotas");
        } else {
            getSupportActionBar().setTitle("Adopta un Amigo");
        }

        cargarListaMascotas();
    }


    private void cargarListaMascotas() {
        listaMascotas = daoMascota.listarTodos();

        if (listaMascotas.isEmpty()) {
            recyclerMascotas.setVisibility(View.GONE);
            txtListaVacia.setVisibility(View.VISIBLE);
        } else {
            recyclerMascotas.setVisibility(View.VISIBLE);
            txtListaVacia.setVisibility(View.GONE);

            adaptador = new AdaptadorMascotas(listaMascotas, esModoRefugio);
            recyclerMascotas.setAdapter(adaptador);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        cargarListaMascotas();
    }
}