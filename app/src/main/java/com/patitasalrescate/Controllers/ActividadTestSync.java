package com.patitasalrescate.Controllers;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOMascota;
import com.patitasalrescate.accesoADatos.SyncManager;
import com.patitasalrescate.model.Mascota;
import com.patitasalrescate.ui.AdaptadorMascotas;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActividadTestSync extends AppCompatActivity {
    private DAOMascota daoMascota;
    private RecyclerView rvMascotas;
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilitar diseño moderno
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_sync);

        // Inicializar DAO y Vista
        daoMascota = new DAOMascota(this);
        syncManager = new SyncManager(this);
        rvMascotas = findViewById(R.id.rv_mascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));

        // BOTÓN: INSERTAR
        // En el botón Insertar
        // En el botón Insertar
        findViewById(R.id.btn_insert_mascota).setOnClickListener(v -> {
            List<String> fotosList = new ArrayList<>();
            fotosList.add("https://picsum.photos/200/300");
            fotosList.add("https://picsum.photos/200/301");

            String idRefugioValido = "13373e9e-27b0-488d-ac76-bd51dd09406a";  // ← TU UUID REAL del refugio

            Mascota masc = new Mascota(
                    UUID.randomUUID().toString(),
                    idRefugioValido,  // ← UUID válido
                    "Gato",
                    "Siames",
                    12,
                    "Juguetón",
                    "Historia de rescate",
                    fotosList,
                    false,
                    System.currentTimeMillis()
            );

            long id = daoMascota.insertar(masc);
            if (id > 0) {
                Toast.makeText(this, "Mascota guardada localmente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar localmente", Toast.LENGTH_SHORT).show();
            }
        });

        // BOTÓN: SYNC (Supabase) - CORREGIDO
        findViewById(R.id.btn_sync_supabase).setOnClickListener(view -> {
            Toast.makeText(this, "Sincronizando con Supabase...", Toast.LENGTH_SHORT).show();

            // Ejecutar la sincronización
            syncManager.sincronizarTodo();

            // Recargar la lista después de un pequeño retraso para dar tiempo a la red
            view.postDelayed(() -> {
                List<Mascota> lista = daoMascota.listarTodos();
                rvMascotas.setAdapter(new AdaptadorMascotas(lista));
                Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show();
            }, 2000);
        });

        // BOTÓN: LISTAR
        findViewById(R.id.btn_listar).setOnClickListener(v -> {
            actualizarLista();
        });
    }

    private void actualizarLista() {
        List<Mascota> lista = daoMascota.listarTodos();
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay mascotas en la base de datos", Toast.LENGTH_SHORT).show();
        } else {
            rvMascotas.setAdapter(new AdaptadorMascotas(lista));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncManager.sincronizarTodo(); // Se ejecuta cada vez que vuelves a la pantalla
    }
}