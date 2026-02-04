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

public class ActividadTestSync extends AppCompatActivity {
    private DAOMascota daoMascota;
    private RecyclerView rvMascotas;
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_sync);

        // Inicializar DAO y Managers
        daoMascota = new DAOMascota(this);
        syncManager = new SyncManager(this);

        // Configurar RecyclerView
        rvMascotas = findViewById(R.id.rv_mascotas);
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));

        // --- BOTÓN: INSERTAR LOCAL ---
        findViewById(R.id.btn_insert_mascota).setOnClickListener(v -> {
            List<String> fotosList = new ArrayList<>();
            fotosList.add("https://picsum.photos/200/300");

            int idRefugioValido = 1;  // Asumiendo que existe el refugio con ID 1

            // CREAMOS EL OBJETO CON EL CONSTRUCTOR DE TU MODELO
            // Orden: id, idRef, esp, raza, edad, temp, hist, fotos, adoptado, NOMBRE, sync
            Mascota masc = new Mascota(
                    0,               // ID: 0 (SQLite pondrá el autoincrement)
                    idRefugioValido,
                    "Gato",          // Especie
                    "Siames",        // Raza
                    12,              // Edad
                    "Juguetón",      // Temperamento
                    "Historia...",   // Historia
                    fotosList,       // Fotos
                    false,           // Es Adoptado
                    "Garfield",      // NOMBRE (Ubicación correcta)
                    System.currentTimeMillis() // LastSync
            );

            long id = daoMascota.insertar(masc);
            if (id > 0) {
                Toast.makeText(this, "Mascota guardada. ID: " + id, Toast.LENGTH_SHORT).show();
                actualizarLista(); // Refrescar vista
            } else {
                Toast.makeText(this, "Error al guardar localmente", Toast.LENGTH_SHORT).show();
            }
        });

        // --- BOTÓN: SYNC (Supabase) ---
        findViewById(R.id.btn_sync_supabase).setOnClickListener(view -> {
            Toast.makeText(this, "Sincronizando...", Toast.LENGTH_SHORT).show();

            // Llamamos al manager de sincronización
            syncManager.sincronizarTodo();

            // Simulamos un delay para recargar la lista cuando termine (idealmente usar callbacks)
            view.postDelayed(() -> {
                actualizarLista();
                Toast.makeText(this, "Sincronización finalizada", Toast.LENGTH_SHORT).show();
            }, 2000);
        });

        // --- BOTÓN: LISTAR ---
        findViewById(R.id.btn_listar).setOnClickListener(v -> {
            actualizarLista();
        });
    }

    private void actualizarLista() {
        List<Mascota> lista = daoMascota.listarTodos();
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay mascotas registradas", Toast.LENGTH_SHORT).show();
        } else {

            rvMascotas.setAdapter(new AdaptadorMascotas(lista, true));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: actualizar al volver a la pantalla
        // actualizarLista();
    }
}