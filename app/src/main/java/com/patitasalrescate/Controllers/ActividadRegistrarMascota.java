package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOMascota;
import com.patitasalrescate.accesoADatos.SupabaseService;
import com.patitasalrescate.model.Mascota;
import com.patitasalrescate.ui.AdaptadorFotosPreview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActividadRegistrarMascota extends AppCompatActivity {

    private EditText txtNombre, txtEspecie, txtRaza, txtEdad, txtTemperamento, txtHistoria;
    private Button btnSeleccionarFotos, btnGuardar;
    private RecyclerView recyclerFotosPreview;

    private DAOMascota daoMascota;
    private SupabaseService supabaseService;

    private List<Uri> urisFotosSeleccionadas = new ArrayList<>();
    private List<String> linksFotosSubidas = new ArrayList<>();

    private ActivityResultLauncher<Intent> launcherGaleria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_registrar_mascota);

        daoMascota = new DAOMascota(this);
        supabaseService = new SupabaseService();

        txtNombre = findViewById(R.id.txt_reg_nombre_mascota);
        txtEspecie = findViewById(R.id.txt_reg_especie);
        txtRaza = findViewById(R.id.txt_reg_raza);
        txtEdad = findViewById(R.id.txt_reg_edad);
        txtTemperamento = findViewById(R.id.txt_reg_temperamento);
        txtHistoria = findViewById(R.id.txt_reg_historia);
        btnSeleccionarFotos = findViewById(R.id.btn_seleccionar_fotos);
        btnGuardar = findViewById(R.id.btn_guardar_mascota);
        recyclerFotosPreview = findViewById(R.id.recycler_fotos_preview);

        recyclerFotosPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerFotosPreview.setAdapter(new AdaptadorFotosPreview(urisFotosSeleccionadas));

        launcherGaleria = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                if (result.getData().getClipData() != null) {
                    int count = result.getData().getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                        urisFotosSeleccionadas.add(uri);
                    }
                } else if (result.getData().getData() != null) {
                    urisFotosSeleccionadas.add(result.getData().getData());
                }
                recyclerFotosPreview.getAdapter().notifyDataSetChanged();
            }
        });

        btnSeleccionarFotos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            launcherGaleria.launch(intent);
        });

        btnGuardar.setOnClickListener(v -> registrarMascota());
    }

    private void registrarMascota() {
        String nombre = txtNombre.getText().toString().trim();
        String especie = txtEspecie.getText().toString().trim();
        String raza = txtRaza.getText().toString().trim();
        String edadStr = txtEdad.getText().toString().trim();
        String temperamento = txtTemperamento.getText().toString().trim();
        String historia = txtHistoria.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || especie.isEmpty()) {
            Toast.makeText(this, "Nombre y especie son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        final int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Edad debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (urisFotosSeleccionadas.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener id_refugio de la sesión
        SharedPreferences prefs = getSharedPreferences("sesion_refugio", MODE_PRIVATE);
        final String idRefugio = prefs.getString("id_refugio", "");

        if (idRefugio.isEmpty()) {
            Toast.makeText(this, "Debes iniciar sesión como refugio para registrar mascotas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final String idMascota = UUID.randomUUID().toString();

        Toast.makeText(this, "Subiendo fotos...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            // Subir fotos
            linksFotosSubidas.clear(); // Limpiar lista por si acaso
            for (Uri uri : urisFotosSeleccionadas) {
                try {
                    byte[] bytes = getBytesFromUri(uri);
                    String nombreArchivo = "mascota_" + UUID.randomUUID().toString() + ".jpg";
                    String link = supabaseService.subirFoto(bytes, nombreArchivo);
                    if (link != null) {
                        linksFotosSubidas.add(link);
                    } else {
                        Log.e("SubidaFoto", "Fallo al subir foto");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Si no subió ninguna foto → error
            if (linksFotosSubidas.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "No se pudieron subir las fotos", Toast.LENGTH_LONG).show());
                return;
            }

            // Crear Mascota
            Mascota nuevaMascota = new Mascota(
                    idMascota,
                    idRefugio,
                    nombre,
                    especie,
                    raza,
                    edad,
                    temperamento,
                    historia,
                    new ArrayList<>(linksFotosSubidas),
                    false,
                    System.currentTimeMillis()
            );

            // Guardar local
            long localResult = daoMascota.insertar(nuevaMascota);

            // Guardar en Supabase
            boolean nubeResult = false;
            try {
                nubeResult = supabaseService.insertarMascota(nuevaMascota);
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean finalNubeResult = nubeResult;
            runOnUiThread(() -> {
                if (localResult != -1 && finalNubeResult) {
                    Toast.makeText(this, "¡Mascota registrada! (local + nube)", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al registrar mascota (revisa Logcat)", Toast.LENGTH_LONG).show();
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