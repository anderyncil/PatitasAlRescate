package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    private EditText txtNombre, txtDireccion, txtTelefono, txtCorreo, txtPassword;
    private ImageView imgPreview;
    private Button btnGuardar, btnSeleccionarFoto;
    private DAORefugio daoRefugio;
    private SupabaseService supabaseService;
    private Uri uriImagenSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_registrar_organizacion);

        Toolbar toolbar = findViewById(R.id.toolbarRegistrarOrganizacion);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        daoRefugio = new DAORefugio(this);
        supabaseService = new SupabaseService();

        txtNombre = findViewById(R.id.txt_reg_nombre);
        txtDireccion = findViewById(R.id.txt_reg_direccion);
        txtTelefono = findViewById(R.id.txt_reg_telefono);
        txtCorreo = findViewById(R.id.txt_reg_correo);
        txtPassword = findViewById(R.id.txt_reg_password);
        imgPreview = findViewById(R.id.img_preview_refugio);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto);
        btnGuardar = findViewById(R.id.btn_guardar_registro);

        ActivityResultLauncher<Intent> launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uriImagenSeleccionada = result.getData().getData();
                        if (uriImagenSeleccionada != null) {
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

        btnGuardar.setOnClickListener(v -> procesarRegistro());
    }

    private void procesarRegistro() {
        String nombre = txtNombre.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordEncriptada = SeguridadUtils.encriptar(password);

        // Generamos UUID para el ID (tanto local como remoto)
        String idRefugio = UUID.randomUUID().toString();

        // Subir foto (si hay)
        String urlFinalFoto = "https://picsum.photos/200";
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

        // Crear objeto Refugio con String ID
        Refugio nuevoRefugio = new Refugio(
                idRefugio,           // ← String UUID
                nombre,
                direccion,
                0.0,
                0.0,
                correo,
                passwordEncriptada,
                telefono,
                urlFinalFoto,
                System.currentTimeMillis()
        );

        // Guardar en Supabase (nube)
        new Thread(() -> {
            try {
                boolean exitoNube = supabaseService.insertarRefugio(nuevoRefugio);
                runOnUiThread(() -> {
                    if (exitoNube) {
                        Toast.makeText(this, "¡Refugio registrado en la nube!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al registrar en la nube", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Guardar en local (SQLite)
        long resultadoLocal = daoRefugio.insertar(nuevoRefugio);
        if (resultadoLocal != -1) {
            Toast.makeText(this, "¡Registro completado localmente!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar localmente", Toast.LENGTH_SHORT).show();
        }
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