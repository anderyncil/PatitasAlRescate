package com.patitasalrescate.accesoADatos;

import com.patitasalrescate.model.Mascota;
import com.patitasalrescate.model.Refugio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiSimulada {

    // Simula lista de refugios (sin cambios)
    public List<Refugio> getRefugiosDesdeApi() {
        List<Refugio> refugios = new ArrayList<>();
        refugios.add(new Refugio("ref1", "Refugio Ejemplo", "Dirección 1", -12.0464, -77.0428, "refugio@ejemplo.com", "999999999", System.currentTimeMillis()));
        // Agrega más mock si quieres
        return refugios;
    }

    // Simula lista de mascotas (ahora con List<String> para fotos)
    public List<Mascota> getMascotasDesdeApi() {
        List<Mascota> mascotas = new ArrayList<>();

        // Mascota 1
        List<String> fotos1 = Arrays.asList(
                "https://picsum.photos/200/300",  // URL real para pruebas
                "https://picsum.photos/200/301"
        );
        mascotas.add(new Mascota(
                "masc1",
                "ref1",
                "Perro",
                "Labrador",
                24,
                "Amigable",
                "Historia de rescate",
                fotos1,
                false,
                System.currentTimeMillis()
        ));

        // Mascota 2 (ejemplo extra)
        List<String> fotos2 = Arrays.asList(
                "https://picsum.photos/200/302"
        );
        mascotas.add(new Mascota(
                "masc2",
                "ref1",
                "Gato",
                "Persa",
                36,
                "Tranquilo",
                "Historia rescatada de la calle",
                fotos2,
                false,
                System.currentTimeMillis()
        ));

        return mascotas;
    }
}