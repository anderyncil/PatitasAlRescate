package com.patitasalrescate.Controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAORefugio;
import com.patitasalrescate.accesoADatos.SupabaseService;
import com.patitasalrescate.model.Refugio;
import com.patitasalrescate.utils.SeguridadUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ActividadRegistrarOrganizacion extends AppCompatActivity {

    // Vistas (Usamos los nuevos IDs)
    private EditText txtNombre, txtDireccion, txtTelefono, txtCorreo, txtPassword;
    private ImageView imgPreview;
    private Button btnGuardar, btnSeleccionarFoto;
    private ImageButton btnAbrirMapa;

    // Lógica
    private DAORefugio daoRefugio;
    private SupabaseService supabaseService;
    private Uri uriImagenSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_registrar_organizacion);

        // 1. Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarRegistrarOrganizacion);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 2. Inicializar DAOs
        daoRefugio = new DAORefugio(this);
        supabaseService = new SupabaseService();

        // 3. Vincular con los nuevos IDs del XML (Estilo Adoptante)
        txtNombre = findViewById(R.id.rj_text_org_nombre);
        txtDireccion = findViewById(R.id.rj_text_org_direccion);
        txtTelefono = findViewById(R.id.rj_text_org_telefono);
        txtCorreo = findViewById(R.id.rj_text_org_correo);
        txtPassword = findViewById(R.id.rj_text_org_password);

        imgPreview = findViewById(R.id.img_preview_refugio);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto);
        btnAbrirMapa = findViewById(R.id.btn_abrir_mapa);
        btnGuardar = findViewById(R.id.rj_button_registrar_organizacion);

        // 4. Configurar Padding para EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrar_organizacion_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 5. Launcher para Galería
        ActivityResultLauncher<Intent> launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uriImagenSeleccionada = result.getData().getData();
                        if (uriImagenSeleccionada != null) {
                            Glide.with(this).load(uriImagenSeleccionada).circleCrop().into(imgPreview);
                        }
                    }
                }
        );

        // Listeners
        btnSeleccionarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcherGaleria.launch(intent);
        });

        btnAbrirMapa.setOnClickListener(v -> {
            String direccion = txtDireccion.getText().toString();
            if (!direccion.isEmpty()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(mapIntent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
                }
            } else {
                txtDireccion.setError("Ingrese una dirección primero");
            }
        });

        btnGuardar.setOnClickListener(v -> procesarRegistro());
    }

    private void procesarRegistro() {
        String nombre = txtNombre.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // --- VALIDACIONES (Igual que en Adoptante) ---
        if (nombre.isEmpty()) {
            txtNombre.setError("Ingrese el nombre de la organización");
            return;
        }
        if (direccion.isEmpty()) {
            txtDireccion.setError("Ingrese la dirección");
            return;
        }
        if (telefono.length() < 7) { // Asumiendo mínimo fijo o celular
            txtTelefono.setError("Ingrese un teléfono válido");
            return;
        }
        if (correo.isEmpty()) {
            txtCorreo.setError("Ingrese el correo");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Ingrese un formato de correo válido");
            return;
        }
        if (password.length() < 6) {
            txtPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // --- PROCESO DE REGISTRO (CON SUPABASE) ---
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando organización...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> {
            String urlFinalFoto = "https://picsum.photos/200"; // Default

            // 1. Subir Foto (Sin cambios)
            if (uriImagenSeleccionada != null) {
                try {
                    byte[] imagenBytes = getBytesFromUri(uriImagenSeleccionada);
                    if (imagenBytes != null) {
                        String nombreArchivo = "refugio_" + System.currentTimeMillis() + ".jpg";
                        String urlSubida = supabaseService.subirFoto(imagenBytes, nombreArchivo);
                        if (urlSubida != null) {
                            urlFinalFoto = urlSubida;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 2. Preparar Datos (Sin cambios)
            String passwordEncriptada = SeguridadUtils.encriptar(password);
            String idRefugio = UUID.randomUUID().toString();

            Refugio nuevoRefugio = new Refugio(
                    idRefugio,
                    nombre, direccion, 0.0, 0.0,
                    correo, passwordEncriptada, telefono,
                    urlFinalFoto,
                    System.currentTimeMillis()
            );

            // ... dentro del Thread ...

            // 3. Subir a Supabase (AQUÍ ES EL ENVÍO A LA NUBE) ☁️
            boolean subidoANube = false;
            String mensajeError = ""; // <--- NUEVA VARIABLE PARA VER EL ERROR

            try {
                subidoANube = supabaseService.insertarRefugio(nuevoRefugio);
                if (!subidoANube) {
                    mensajeError = "Servidor rechazó los datos (Revisa columnas/tipos)";
                }
            } catch (Exception e) { // Cambiamos a Exception para atrapar TODO
                e.printStackTrace();
                mensajeError = e.getMessage(); // Guardamos el error técnico
                Log.e("SupabaseError", "🔥 CRASH: " + mensajeError);
            }

            // 4. Guardar Localmente y Mostrar Toast
            Refugio finalRefugio = nuevoRefugio;
            boolean finalSubidoANube = subidoANube;
            String finalMensajeError = mensajeError; // Pasamos el error al hilo principal

            runOnUiThread(() -> {
                long resultado = daoRefugio.insertar(finalRefugio);
                progressDialog.dismiss();

                if (resultado != -1) {
                    // --- TOAST DE DIAGNÓSTICO ---
                    if (finalSubidoANube) {
                        Toast.makeText(this, "¡ÉXITO TOTAL! Nube y Local ☁️", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // AQUÍ VERÁS EL ERROR EN TU PANTALLA
                        Toast.makeText(this, "⚠️ Solo Local. Error Nube: " + finalMensajeError, Toast.LENGTH_LONG).show();
                        // No cerramos la actividad (finish) para que puedas leer el error
                    }
                } else {
                    Toast.makeText(this, "Error al guardar localmente", Toast.LENGTH_SHORT).show();
                }
            });


        }).start();

    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputStream.close();
        return byteBuffer.toByteArray();
    }
}