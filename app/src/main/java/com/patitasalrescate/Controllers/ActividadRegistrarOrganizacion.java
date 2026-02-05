package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAORefugio;
import com.patitasalrescate.model.Refugio;
import com.patitasalrescate.utils.SeguridadUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.patitasalrescate.accesoADatos.SupabaseService; // Asegúrate de tener este import
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ActividadRegistrarOrganizacion extends AppCompatActivity {

    // Vistas
    private EditText txtNombre, txtDireccion, txtTelefono, txtCorreo, txtPassword;
    private ImageView imgPreview;
    private Button btnGuardar, btnSeleccionarFoto;
    private ImageButton btnAbrirMapa;

    // Lógica
    private DAORefugio daoRefugio;
    private SupabaseService supabaseService; // Instancia del servicio
    private Uri uriImagenSeleccionada = null; // Guardamos la selección temporalmente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_registrar_organizacion);

        Toolbar toolbar = findViewById(R.id.toolbarRegistrarOrganizacion);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        daoRefugio = new DAORefugio(this);
        supabaseService = new SupabaseService();

        txtNombre = findViewById(R.id.txt_reg_nombre);
        txtDireccion = findViewById(R.id.txt_reg_direccion);
        txtTelefono = findViewById(R.id.txt_reg_telefono);
        txtCorreo = findViewById(R.id.txt_reg_correo);
        txtPassword = findViewById(R.id.txt_reg_password);
        imgPreview = findViewById(R.id.img_preview_refugio);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto);
        btnAbrirMapa = findViewById(R.id.btn_abrir_mapa);
        btnGuardar = findViewById(R.id.btn_guardar_registro);

        ActivityResultLauncher<Intent> launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uriImagenSeleccionada = result.getData().getData();
                        if (uriImagenSeleccionada != null) {
                            // Previsualización rápida con Glide
                            Glide.with(this)
                                    .load(uriImagenSeleccionada)
                                    .circleCrop()
                                    .into(imgPreview);
                        }
                    }
                }
        );

        btnSeleccionarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcherGaleria.launch(intent);
        });

        // --- 5. CONFIGURAR MAPA ---
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
                Toast.makeText(this, "Ingrese una dirección primero", Toast.LENGTH_SHORT).show();
            }
        });

        btnGuardar.setOnClickListener(v -> procesarRegistro());
    }

    private void procesarRegistro() {
        String nombre = txtNombre.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Subiendo foto y registrando...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            String urlFinalFoto = "https://picsum.photos/200"; // URL por defecto si no sube nada

            // 1. Si el usuario seleccionó foto, intentamos subirla
            if (uriImagenSeleccionada != null) {
                try {
                    // Convertimos la URI a Bytes
                    byte[] imagenBytes = getBytesFromUri(uriImagenSeleccionada);

                    if (imagenBytes != null) {
                        // Nombre único para el archivo: "refugio_12345.jpg"
                        String nombreArchivo = "refugio_" + System.currentTimeMillis() + ".jpg";

                        // LLAMADA A SUPABASE (Toma tiempo)
                        String urlSubida = supabaseService.subirFoto(imagenBytes, nombreArchivo);

                        if (urlSubida != null) {
                            urlFinalFoto = urlSubida; // ¡Éxito! Tenemos URL de internet
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Registro", "Error leyendo imagen: " + e.getMessage());
                }
            }

            // 2. Preparamos los datos para SQLite
            String finalUrl = urlFinalFoto; // Variable final para usar en lambda

            // Volvemos al Hilo Principal (UI Thread) para guardar y cerrar
            runOnUiThread(() -> {
                guardarEnBaseDeDatos(finalUrl);
                progressDialog.dismiss();
            });

        }).start();
    }

    private void guardarEnBaseDeDatos(String urlFoto) {
        String nombre = txtNombre.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        String idUnico = UUID.randomUUID().toString();
        String passwordEncriptada = SeguridadUtils.encriptar(password);

        Refugio nuevoRefugio = new Refugio(0,
                nombre, direccion, 0.0, 0.0,
                correo, passwordEncriptada, telefono,
                urlFoto, // Aquí va la URL que vino de Supabase (o la default)
                System.currentTimeMillis()
        );

        long resultado = daoRefugio.insertar(nuevoRefugio);

        if (resultado != -1) {
            Toast.makeText(this, "¡Registro completado!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar en BD local", Toast.LENGTH_SHORT).show();
        }
    }

    // --- MÉTODO AYUDA PARA CONVERTIR URI A BYTES ---
    private byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}