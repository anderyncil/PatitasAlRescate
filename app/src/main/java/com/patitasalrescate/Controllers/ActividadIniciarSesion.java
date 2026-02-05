package com.patitasalrescate.Controllers;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.patitasalrescate.R;
import com.patitasalrescate.accesoADatos.DAOAdoptante;
import com.patitasalrescate.accesoADatos.DAORefugio;

public class ActividadIniciarSesion extends AppCompatActivity {
    private EditText textCorreo, textPass;
    private DAOAdoptante daoAdoptante;
    private DAORefugio daoRefugio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_inicia_sesion);





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
}