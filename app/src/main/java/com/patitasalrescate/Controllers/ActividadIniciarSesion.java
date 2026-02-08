package com.patitasalrescate.Controllers;

import android.content.Intent;
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
import com.patitasalrescate.model.Adoptante;
import com.patitasalrescate.model.Refugio;
import com.patitasalrescate.utils.SeguridadUtils;

public class ActividadIniciarSesion extends AppCompatActivity {

    private EditText textCorreo, textPassword;
    private Button button_Ingresar;

    // DAOs
    private DAOAdoptante daoAdoptante;
    private DAORefugio daoRefugio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_inicia_sesion);

        // 1. Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.tollbariniciarsesion);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 2. Inicializar DAOs
        daoAdoptante = new DAOAdoptante(this);
        daoRefugio = new DAORefugio(this);

        // 3. Referenciar UI
        textCorreo = findViewById(R.id.rj_text_correr_inisesion);
        textPassword = findViewById(R.id.rj_text_pass_inisesion);
        button_Ingresar = findViewById(R.id.rj_button_ingresar_inisesion);

        // 4. Listener del Botón
        button_Ingresar.setOnClickListener(v -> ejecutarLogin());

        // Ajuste EdgeToEdge
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

        // Encriptamos la contraseña ingresada
        String passEncriptada = SeguridadUtils.encriptar(passPlana);

        // 1. Buscamos primero en ADOPTANTES
        Adoptante adoptante = daoAdoptante.login(correo, passEncriptada);
        if (adoptante != null) {
            irAPantallaPrincipal(adoptante.getNombre(), "Adoptante");
            return;
        }

        // 2. Si no es adoptante, buscamos en REFUGIOS
        Refugio refugio = daoRefugio.login(correo, passEncriptada);
        if (refugio != null) {
            irAPantallaPrincipal(refugio.getNombre(), "Refugio");
            return;
        }

        // 3. Si no existe en ninguno
        Toast.makeText(this, "Contraseña o Correo incorrectos", Toast.LENGTH_LONG).show();
    }

    public void irAPantallaPrincipal(String nombre, String tipo) {
        Intent intent;

        Toast.makeText(this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();

        if (tipo.equals("Adoptante")) {
            intent = new Intent(this, ActividadInicioAdoptante.class);
            // Enviamos el nombre con la clave para Adoptante
            intent.putExtra("nombre_adoptante_key", nombre);
        } else {
            intent = new Intent(this, ActividadInicioRefugio.class);
            // Enviamos el nombre con la clave para Refugio
            intent.putExtra("nombre_refugio_key", nombre);
        }

        startActivity(intent);
        finish(); // Cerramos Login
    }
}