package com.patitasalrescate.accesoADatos;

import android.content.Context;

import com.patitasalrescate.model.Mascota;

import java.util.List;

public class SyncManager {

    private final DAOMascota daoMascota;
    private final SupabaseService supabase;

    public SyncManager(Context context) {
        this.daoMascota = new DAOMascota(context);
        this.supabase = new SupabaseService();
    }





    // Sincronización completa (pull + push)
    public void sincronizarTodo() {
        new Thread(() -> {
            try {
                // 1. PUSH: Enviar locales que no estén sincronizados
                List<Mascota> locales = daoMascota.listarTodos();
                for (Mascota m : locales) {
                    // Simple: intenta insertar siempre (Supabase ignora si ya existe por PK)
                    boolean subido = supabase.insertarMascota(m);
                    if (subido) {
                        // Marca como sincronizado (opcional)
                        m.setLastSync(System.currentTimeMillis());
                        daoMascota.actualizar(m);
                    }
                }

                // 2. PULL: Traer todo de Supabase
                List<Mascota> remotas = supabase.getMascotas();
                if (remotas != null) {
                    for (Mascota m : remotas) {
                        // Insertar o actualizar local (upsert simple)
                        daoMascota.insertar(m);  // Si ya existe, puedes hacer update en vez de insert
                    }
                }
                android.util.Log.d("SyncPush", "Se intentaron subir " + locales.size() + " mascotas locales");
                android.util.Log.d("Sync", "Sincronización completada: " + (remotas != null ? remotas.size() : 0) + " remotas");
            } catch (Exception e) {
                android.util.Log.e("Sync", "Error en sync: " + e.getMessage());

                e.printStackTrace();

            }
        }).start();
    }
}