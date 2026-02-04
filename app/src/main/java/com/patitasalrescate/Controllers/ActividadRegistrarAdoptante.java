package com.patitasalrescate.Controllers;

import android.net.eap.EapSessionConfig;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOAdoptante;

public class ActividadRegistrarAdoptante extends AppCompatActivity {

    private EditText etNombre, etCorreo, etPass, etTelefono, etEdad;
    private Spinner spSexo;
    private DAOAdoptante daoAdoptante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_registrar_adoptante);

        //inicializamos
        daoAdoptante = new DAOAdoptante(this);
        etNombre = findViewById(R.id.rj_text_adopt_nombre);
        etCorreo = findViewById(R.id.rj_text_adopt_correo);
        etPass = findViewById(R.id.rj_text_adopt_password);
        etTelefono = findViewById(R.id.rj_text_adopt_telefono);
        etEdad = findViewById(R.id.rj_text_adopt_edad);
        spSexo = findViewById(R.id.rj_combo_adopt_sexo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.regitrar_adoptante), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void registrarUsuario(){
        String nombre = etNombre.getText().toString();
        String correo = etCorreo.getText().toString();
        String passTextoPlano = etPass.getText().toString();
        String telefono = etTelefono.getText().toString();
        String sexo = spSexo.getSelectedItem().toString();
        int edad = Integer.parseInt(etEdad.getText().toString());


    }
}