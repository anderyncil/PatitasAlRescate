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

public class ActividadIngresar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_ingresar);

        Button b_iniciarsesion = findViewById(R.id.rj_button_iniciosesion);
        Button b_registrarse = findViewById(R.id.rj_button_registrarse);

        b_iniciarsesion.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadIniciarSesion.class);
            startActivity(intent);
        });
        b_registrarse.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadRegistro.class);
            startActivity(intent);
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ingresar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}