package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOMascota;
import com.patitasalrescate.model.Mascota;

import java.util.List;

public class ActividadPerfilMascota extends AppCompatActivity {

    // Vistas
    private EditText txtNombre, txtEspecie, txtRaza, txtEdad, txtTemperamento, txtHistoria;
    private ImageView imgFoto;
    private Button btnAccion;

    // Datos
    private DAOMascota daoMascota;
    private Mascota mascotaActual;
    private int idMascota;
    private boolean esModoEdicion = false; // TRUE = Refugio, FALSE = Adoptante

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_perfil_mascota);

        // 1. Inicializar DAO y Vistas
        daoMascota = new DAOMascota(this);
        initViews();

        // 2. Recibir Datos del Intent
        if (getIntent().hasExtra("id_mascota_key")) {
            idMascota = getIntent().getIntExtra("id_mascota_key", -1);
        }

        // Aquí decidimos el rol: ¿Viene del Refugio (botón editar)?
        if (getIntent().hasExtra("es_modo_edicion")) {
            esModoEdicion = getIntent().getBooleanExtra("es_modo_edicion", false);
        }

        // 3. Cargar Datos de la BD
        cargarDatosMascota();

        // 4. Configurar la Interfaz según el Rol
        configurarModoVisual();
    }

    private void initViews() {
        txtNombre = findViewById(R.id.txt_edit_nombre);
        txtEspecie = findViewById(R.id.txt_edit_especie);
        txtRaza = findViewById(R.id.txt_edit_raza);
        txtEdad = findViewById(R.id.txt_edit_edad);
        txtTemperamento = findViewById(R.id.txt_edit_temperamento);
        txtHistoria = findViewById(R.id.txt_edit_historia);
        imgFoto = findViewById(R.id.img_detalle_mascota);
        btnAccion = findViewById(R.id.btn_accion_principal);
    }

    private void cargarDatosMascota() {
        if (idMascota != -1) {
            // Usamos el método obtenerPorId que agregaste al DAO
            mascotaActual = daoMascota.obtenerPorId(idMascota);

            if (mascotaActual != null) {
                // Llenamos los campos
                txtNombre.setText(mascotaActual.getNombre());
                txtEspecie.setText(mascotaActual.getEspecie());
                txtRaza.setText(mascotaActual.getRaza());
                txtEdad.setText(String.valueOf(mascotaActual.getEdad()));
                txtTemperamento.setText(mascotaActual.getTemperamento());
                txtHistoria.setText(mascotaActual.getHistoria());

                // Cargar Foto
                List<String> fotos = mascotaActual.getFotos();
                if (fotos != null && !fotos.isEmpty()) {
                    Glide.with(this).load(fotos.get(0)).centerCrop().into(imgFoto);
                }
            } else {
                Toast.makeText(this, "Error: Mascota no encontrada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void configurarModoVisual() {
        if (esModoEdicion) {
            // --- MODO REFUGIO (EDITAR) ---
            habilitarCampos(true);
            btnAccion.setText("GUARDAR CAMBIOS");
            btnAccion.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark)); // Color naranja para editar

            btnAccion.setOnClickListener(v -> guardarCambios());

        } else {
            // --- MODO ADOPTANTE (VER Y ADOPTAR) ---
            habilitarCampos(false); // Bloqueamos escritura
            btnAccion.setText("¡QUIERO ADOPTARLO! 🐾");
            // Usamos el color primario o verde para acción positiva

            btnAccion.setOnClickListener(v -> irAAdoptar());
        }
    }

    // Método mágico para bloquear/desbloquear escritura
    private void habilitarCampos(boolean habilitar) {
        txtNombre.setEnabled(habilitar);
        txtEspecie.setEnabled(habilitar);
        txtRaza.setEnabled(habilitar);
        txtEdad.setEnabled(habilitar);
        txtTemperamento.setEnabled(habilitar);
        txtHistoria.setEnabled(habilitar);

        // Opcional: Cambiar el estilo visual si están deshabilitados
        if (!habilitar) {
            txtNombre.setTextColor(getResources().getColor(android.R.color.black));
            // ... repetir para otros si quieres que se vea bien negro y no gris
        }
    }

    private void guardarCambios() {
        // 1. Recoger datos modificados
        mascotaActual.setNombre(txtNombre.getText().toString());
        mascotaActual.setEspecie(txtEspecie.getText().toString());
        mascotaActual.setRaza(txtRaza.getText().toString());
        mascotaActual.setTemperamento(txtTemperamento.getText().toString());
        mascotaActual.setHistoria(txtHistoria.getText().toString());

        try {
            mascotaActual.setEdad(Integer.parseInt(txtEdad.getText().toString()));
        } catch (NumberFormatException e) {
            mascotaActual.setEdad(0);
        }

        // 2. Actualizar en BD
        int filas = daoMascota.actualizar(mascotaActual);

        if (filas > 0) {
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
            finish(); // Volvemos a la lista
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void irAAdoptar() {
        // Redirigir a la pantalla de proceso de adopción
        Intent intent = new Intent(this, ActividadAdopcion.class);
        intent.putExtra("id_mascota_key", mascotaActual.getIdMascota());
        intent.putExtra("nombre_mascota_key", mascotaActual.getNombre());
        startActivity(intent);
    }
}