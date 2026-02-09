package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOAdoptante;
import com.patitasalrescate.accesoADatos.DAORefugio;
import com.patitasalrescate.accesoADatos.SupabaseService;
import com.patitasalrescate.model.Adoptante;
import com.patitasalrescate.model.Refugio;
import com.patitasalrescate.utils.SeguridadUtils;

import java.io.IOException;

public class ActividadIniciarSesion extends AppCompatActivity {

    private EditText textCorreo, textPassword;
    private Button button_Ingresar;

    private DAOAdoptante daoAdoptante;
    private DAORefugio daoRefugio;
    private SupabaseService supabaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_inicia_sesion);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.tollbariniciarsesion);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        daoAdoptante = new DAOAdoptante(this);
        daoRefugio = new DAORefugio(this);
        supabaseService = new SupabaseService();

        textCorreo = findViewById(R.id.rj_text_correr_inisesion);
        textPassword = findViewById(R.id.rj_text_pass_inisesion);
        button_Ingresar = findViewById(R.id.rj_button_ingresar_inisesion);

        button_Ingresar.setOnClickListener(v -> ejecutarLogin());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.iniciarsesion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ejecutarLogin() {
        String correo = textCorreo.getText().toString().trim();
        String passPlana = textPassword.getText().toString().trim();

        if (correo.isEmpty() || passPlana.isEmpty()) {
            Toast.makeText(this, "Completa todos los Campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String passEncriptada = SeguridadUtils.encriptar(passPlana);

        // Deshabilitar botón para evitar múltiples clics
        button_Ingresar.setEnabled(false);

        new Thread(() -> {
            try {
                // 1. Adoptante - local
                Adoptante adoptanteLocal = daoAdoptante.login(correo, passEncriptada);
                if (adoptanteLocal != null) {
                    runOnUiThread(() -> {
                        irAPantallaPrincipal(adoptanteLocal.getNombre(), "Adoptante");
                        button_Ingresar.setEnabled(true);
                    });
                    return;
                }

                // 2. Adoptante - remoto
                Adoptante adoptanteRemoto = supabaseService.loginAdoptanteRemoto(correo, passEncriptada);
                if (adoptanteRemoto != null) {
                    daoAdoptante.insertar(adoptanteRemoto); // guardar local
                    runOnUiThread(() -> {
                        irAPantallaPrincipal(adoptanteRemoto.getNombre(), "Adoptante");
                        button_Ingresar.setEnabled(true);
                    });
                    return;
                }

                // 3. Refugio - local
                Refugio refugioLocal = daoRefugio.login(correo, passEncriptada);
                if (refugioLocal != null) {
                    guardarSesionRefugio(refugioLocal);
                    runOnUiThread(() -> {
                        irAPantallaPrincipal(refugioLocal.getNombre(), "Refugio");
                        button_Ingresar.setEnabled(true);
                    });
                    return;
                }

                // 4. Refugio - remoto
                Refugio refugioRemoto = supabaseService.loginRefugioRemoto(correo, passEncriptada);
                if (refugioRemoto != null) {
                    daoRefugio.insertar(refugioRemoto); // guardar local
                    guardarSesionRefugio(refugioRemoto);
                    runOnUiThread(() -> {
                        irAPantallaPrincipal(refugioRemoto.getNombre(), "Refugio");
                        button_Ingresar.setEnabled(true);
                    });
                    return;
                }

                // Si nada funcionó
                runOnUiThread(() -> {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    button_Ingresar.setEnabled(true);
                });

            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error de conexión. Intenta más tarde", Toast.LENGTH_LONG).show();
                    button_Ingresar.setEnabled(true);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void guardarSesionRefugio(Refugio refugio) {
        SharedPreferences prefs = getSharedPreferences("sesion_refugio", MODE_PRIVATE);
        prefs.edit()
                .putString("id_refugio", refugio.getIdRefugio())
                .putString("nombre_refugio", refugio.getNombre())
                .apply();
    }

    public void irAPantallaPrincipal(String nombre, String tipo) {
        Intent intent = new Intent(this, tipo.equals("Adoptante") ? ActividadInicioAdoptante.class : ActividadInicioRefugio.class);
        intent.putExtra(tipo.equals("Adoptante") ? "nombre_adoptante_key" : "nombre_refugio_key", nombre);
        startActivity(intent);
        finish();
    }
}