package com.patitasalrescate.accesoADatos;

import com.patitasalrescate.model.Mascota;
import com.patitasalrescate.model.Refugio;
import com.patitasalrescate.utils.SeguridadUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiSimulada {

    public List<Refugio> getRefugiosDesdeApi() {
        List<Refugio> refugios = new ArrayList<>();

        refugios.add(new Refugio(
                1,
                "Refugio Ejemplo",
                "Dirección 1",
                -12.0464,
                -77.0428,
                "refugio@ejemplo.com",
                SeguridadUtils.encriptar("123456"),
                "999999999",
                "https://picsum.photos/200",
                System.currentTimeMillis()
        ));

        return refugios;
    }

    public List<Mascota> getMascotasDesdeApi() {
        List<Mascota> mascotas = new ArrayList<>();

        List<String> fotos1 = Arrays.asList(
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/301"
        );

        mascotas.add(new Mascota(
                1,          // idMascota (int)
                1,          // idRefugio (int)
                "Perro",    // especie
                "Labrador", // raza
                24,         // edad
                "Amigable", // temperamento
                "Historia de rescate", // historia
                fotos1,     // fotos
                false,      // esAdoptado
                "Bobby",    // NOMBRE (Posición correcta según tu modelo)
                System.currentTimeMillis() // lastSync
        ));

        List<String> fotos2 = Arrays.asList("https://picsum.photos/200/302");

        mascotas.add(new Mascota(
                2,
                1,
                "Gato",
                "Persa",
                36,
                "Tranquilo",
                "Rescatado de la calle",
                fotos2,
                false,
                "Michi",    // NOMBRE
                System.currentTimeMillis()
        ));

        return mascotas;
    }
}