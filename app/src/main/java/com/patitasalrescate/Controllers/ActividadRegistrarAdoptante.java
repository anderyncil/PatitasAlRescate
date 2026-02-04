package com.patitasalrescate.Controllers;

import android.net.eap.EapSessionConfig;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOAdoptante;
import com.patitasalrescate.model.Adoptante;
import com.patitasalrescate.utils.SeguridadUtils;

public class ActividadRegistrarAdoptante extends AppCompatActivity {

    private EditText etNombre, etCorreo, etPass, etTelefono, etEdad;
    private Spinner spSexo;
    private DAOAdoptante daoAdoptante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_registrar_adoptante);

        //inicializamos con los elementos del .xml
        daoAdoptante = new DAOAdoptante(this);
        etNombre = findViewById(R.id.rj_text_adopt_nombre);
        etCorreo = findViewById(R.id.rj_text_adopt_correo);
        etPass = findViewById(R.id.rj_text_adopt_password);
        etTelefono = findViewById(R.id.rj_text_adopt_telefono);
        etEdad = findViewById(R.id.rj_text_adopt_edad);
        spSexo = findViewById(R.id.rj_combo_adopt_sexo);

        // Crear Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.opciones_sexo, android.R.layout.simple_spinner_item);
        // Diseño usado
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicamos adaptador a elemento / Spinner "sexo"
        spSexo.setAdapter(adapter);

        findViewById(R.id.rj_button_registrar_adoptante).setOnClickListener(v -> registrarUsuario());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrar_adoptante), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void registrarUsuario(){
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String passTextoPlano = etPass.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

       //validacion para sexo
        int seleccion = spSexo.getSelectedItemPosition();
        if (seleccion == 0) {
            Toast.makeText(this, "Por favor, seleccione un sexo", Toast.LENGTH_SHORT).show();
            return;
        }

        String sexo = spSexo.getSelectedItem().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String passEncriptada = SeguridadUtils.encriptar(passTextoPlano);

        // validaciones de los datos
        if(nombre.isEmpty()) {
            etNombre.setError("Aún no ha ingresado su NOMBRE");
            return;
        }else if(passTextoPlano.length() < 6){
            etPass.setError("La CONTRASEÑA debe tener al menos 6 dígitos");
            return;
        }
        if(correo.isEmpty()){
            etCorreo.setError("Ingrese su CORREO");
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            etCorreo.setError("Ingrese un formato de CORRO válido \n ejemplo: @gmail.com");
            return;
        }
        if(daoAdoptante.existeCorreo(correo)){
            etCorreo.setError("Este CORREO ya está registrado, Intenta con otro");
            return;
        }

        if(telefono.length() != 9){
            etTelefono.setError("El TELEFONO debe contar 9 dígitos");
            return;
        }
        if(edadStr.isEmpty()){
            etEdad.setError("Ingrese su EDAD");
            return;
        }
        int edad = Integer.parseInt(edadStr);
        if(edad <= 8 || edad > 115){
            etEdad.setError("Ingrese una edad válida (8-115)");
            return;
        }

        //agregar a la base de datos
        Adoptante nuevoAdoptante = new Adoptante();

        nuevoAdoptante.setNombre(nombre);
        nuevoAdoptante.setCorreo(correo);
        nuevoAdoptante.setPassword(passEncriptada);
        nuevoAdoptante.setNumCelular(telefono);
        nuevoAdoptante.setEdad(edad);
        nuevoAdoptante.setSexo(sexo);
        nuevoAdoptante.setLastSync(System.currentTimeMillis());

        long resultado = daoAdoptante.insertar(nuevoAdoptante);

        if (resultado != -1) {
            Toast.makeText(this, "¡Registro exitoso! Bienvenido a Patitas", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}