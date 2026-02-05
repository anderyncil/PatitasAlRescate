package com.patitasalrescate.Controllers;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOAdoptante;
import com.patitasalrescate.accesoADatos.DAORefugio;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import com.patitasalrescate.model.Adoptante;
import com.patitasalrescate.model.Refugio;
import com.patitasalrescate.utils.SeguridadUtils;

public class ActividadIniciarSesion extends AppCompatActivity {
    private EditText textCorreo, textPassword;
    private DAOAdoptante daoAdoptante;
    private Button button_Ingresar;
    private DAORefugio daoRefugio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_inicia_sesion);
        // Iniciar los DAOs
        daoAdoptante = new DAOAdoptante(this);
        daoRefugio = new DAORefugio(this);

        // Referenciar los UI
        textCorreo = findViewById(R.id.rj_text_correr_inisesion);
        textPassword = findViewById(R.id.rj_text_pass_inisesion);
        button_Ingresar = findViewById(R.id.rj_button_ingresar_inisesion);

        button_Ingresar.setOnClickListener(v->ejecutarLogin());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.iniciarsesion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //CONSIDERA ENVIAR EL NOMBRE DEL REFUGIO CUANDO SEA DE TIPO REFUGIO CON PUTEXTRAS
        // Y PONLE DE NOMBRE: nombre_refugio_key
        //PORQUE LO LLAMARÉ EN INICIO REFUGIO, LO MISMO HACER PARA INCIOADOPTANTE
        // Y COORDINA CON LA PERSONA DE INICIO ADOPTANTE PARA VER COMO LO TRABAJAN
    }
    private void ejecutarLogin(){
        String correo = textCorreo.getText().toString().trim();
        String passPlana = textPassword.getText().toString().trim();
        if(correo.isEmpty() || passPlana.isEmpty()){
            Toast.makeText(this, "Completa todos los Campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // Encriptacion
        String passEncriptada = SeguridadUtils.encriptar(passPlana);
        // buscamos en la Tabla ADOPTANTE
        Adoptante adoptante = daoAdoptante.login(correo, passEncriptada);
        if(adoptante != null){
            // Es Adoptante
            irAPantallaPrincipal(adoptante.getNombre(), "Adoptante");
            return;
        }
        //Si no fue adoptante vamos a refugio
        Refugio refugio = daoRefugio.login(correo, passEncriptada);
        if(refugio != null){
            // Es un refugio
            irAPantallaPrincipal(refugio.getNombre(), "Refugio");
            return;
        }
        // No existente, falta registrarse
        Toast.makeText(this, "Contraseña o Correo Electronico incorrectos. \nIntentalo de nuevo", Toast.LENGTH_LONG).show();
    }
    public void irAPantallaPrincipal(String nombre, String tipo){

        Toast.makeText(this, "Bienvenido" + nombre + "("+tipo+")", Toast.LENGTH_SHORT).show();
        Intent intent;
        if(tipo.equals("Adoptante")){
            intent = new Intent(this, ActividadInicioAdoptante.class);
            intent.putExtra("nombre_adoptante_key", nombre);
        }else{
            intent = new Intent(this, ActividadInicioRefugio.class);
            intent.putExtra("nombre_refugio_key", nombre);
        }
        startActivity(intent);
        finish(); //cerramos login para que no regrese pantalla
    }
}