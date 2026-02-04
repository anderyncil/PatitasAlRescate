package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.patitasalrescate.R;

public class ActividadRegistro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_tipo_registro);

        Button b_adoptante = findViewById(R.id.rj_button_imperson);
        Button b_organizacion = findViewById(R.id.rj_button_imorganizacion);
        b_adoptante.setOnClickListener(v->{
            Intent i = new Intent(this, ActividadRegistrarAdoptante.class);
            startActivity(i);
        });
        b_organizacion.setOnClickListener(v->{
            Intent i = new Intent(this, ActividadRegistrarOrganizacion.class);
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tipo_registro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}