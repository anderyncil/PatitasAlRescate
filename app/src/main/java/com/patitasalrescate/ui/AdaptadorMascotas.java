package com.patitasalrescate.ui;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.patitasalrescate.Controllers.ActividadAdopcion;
import com.patitasalrescate.Controllers.ActividadAdopcion;
import com.patitasalrescate.R;
import com.patitasalrescate.model.Mascota;

import java.util.List;

public class AdaptadorMascotas extends RecyclerView.Adapter<AdaptadorMascotas.MascotaViewHolder> {

    private List<Mascota> listaMascotas;
    private boolean esModoRefugio; // TRUE = Ver Editar, FALSE = Ver Adoptar

    // Constructor: Recibe la lista y el rol (Refugio o Adoptante)
    public AdaptadorMascotas(List<Mascota> lista, boolean esModoRefugio) {
        this.listaMascotas = lista;
        this.esModoRefugio = esModoRefugio;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asegúrate de que tu archivo XML se llame 'ly_item_cardview_mascota'
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ly_item_cardview_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = listaMascotas.get(position);
        Context context = holder.itemView.getContext();

        // --- CORRECCIÓN IMPORTANTE ---
        // NO uses getIdMascota() directo en setText sin convertirlo a String.
        // Lo mejor es usar el nombre real que agregamos al modelo:
        if (mascota.getNombre() != null && !mascota.getNombre().isEmpty()) {
            holder.txtNombre.setText(mascota.getNombre());
        } else {
            // Si no tiene nombre, mostramos la especie o el ID convertido a texto
            holder.txtNombre.setText(mascota.getEspecie() + " #" + mascota.getIdMascota());
        }
        // -----------------------------

        // txt_raza_mascota
        holder.txtRaza.setText(mascota.getRaza() + " - " + mascota.getEdad() + " meses");

        // txt_estado_mascota
        holder.txtEstado.setText(mascota.getTemperamento());

        // CARGA DE IMAGEN (Esto estaba bien)
        List<String> fotos = mascota.getFotos();
        if (fotos != null && !fotos.isEmpty()) {
            String primeraFoto = fotos.get(0).trim();
            if (!primeraFoto.isEmpty()) {
                Glide.with(context)
                        .load(primeraFoto)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .centerCrop()
                        .into(holder.imgFoto);
            }
        } else {
            holder.imgFoto.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // LÓGICA DE BOTONES (Esto estaba bien)
        if (esModoRefugio) {
            holder.btnAdoptar.setVisibility(View.GONE);
            holder.btnEditar.setVisibility(View.VISIBLE);

            holder.btnEditar.setOnClickListener(v -> {
                Toast.makeText(context, "Editar: " + mascota.getNombre(), Toast.LENGTH_SHORT).show();
            });

        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnAdoptar.setVisibility(View.VISIBLE);

            holder.btnAdoptar.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActividadAdopcion.class);
                intent.putExtra("id_mascota_key", mascota.getIdMascota()); // Pasa el int, está bien
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaMascotas != null ? listaMascotas.size() : 0;
    }

    // --- CLASE VIEWHOLDER CON TUS IDs EXACTOS ---
    static class MascotaViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtRaza, txtEstado;
        ImageView imgFoto;
        Button btnAdoptar, btnEditar;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Buscamos los controles usando los IDs de TU XML:
            imgFoto    = itemView.findViewById(R.id.img_foto_mascota);
            txtNombre  = itemView.findViewById(R.id.txt_nombre_mascota);
            txtRaza    = itemView.findViewById(R.id.txt_raza_mascota);
            txtEstado  = itemView.findViewById(R.id.txt_estado_mascota);

            btnAdoptar = itemView.findViewById(R.id.btn_adoptar);
            btnEditar  = itemView.findViewById(R.id.btn_editar_estado);
        }
    }
}