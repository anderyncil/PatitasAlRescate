package com.patitasalrescate.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

        Button button_soypersona = findViewById(R.id.rj_button_soypersona);
        Button button_soyasociacion = findViewById(R.id.rj_button_soyasociacion);

        TextView text_yatengocuenta = findViewById(R.id.rj_click_inicia_sesion);

        text_yatengocuenta.setPaintFlags(text_yatengocuenta.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);

        /*String text_completo = "¿Ya tienes cuenta? Inicia sesión aquí";
        SpannableString spannable = new SpannableString(text_completo);
        int inicio = text_completo.indexOf("Inicia sesión aquí");
        int fin = inicio + "Inicia sesión aquí".length();

        if(inicio != -1){
            spannable.setSpan(new UnderlineSpan(), inicio, fin, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.naranja_asociacion)),inicio, fin, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        text_yatengocuenta.setText(spannable);*/


        button_soypersona.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadRegistrarAdoptante.class);
            startActivity(intent);
        });

        button_soyasociacion.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadRegistrarOrganizacion.class);
            startActivity(intent);
        });

        text_yatengocuenta.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadIniciarSesion.class);
            startActivity(intent);
        });







        /*
        LOGICA ANTERIOR DESCARTADA
        Button b_iniciarsesion = findViewById(R.id.rj_button_iniciosesion);
        Button b_registrarse = findViewById(R.id.rj_button_registrarse);

        b_iniciarsesion.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadIniciarSesion.class);
            startActivity(intent);
        });
        b_registrarse.setOnClickListener(v->{
            Intent intent = new Intent(ActividadIngresar.this, ActividadRegistro.class);
            startActivity(intent);
        });*/


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ingresar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}