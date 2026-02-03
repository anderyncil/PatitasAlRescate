package com.patitasalrescate.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.patitasalrescate.R;
import com.patitasalrescate.model.Mascota;

import java.util.List;

public class AdaptadorMascotas extends RecyclerView.Adapter<AdaptadorMascotas.MascotaViewHolder> {

    private List<Mascota> listaMascotas;

    public AdaptadorMascotas(List<Mascota> lista) {
        this.listaMascotas = lista;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ly_item_cardview_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = listaMascotas.get(position);

        holder.tvEspecie.setText(mascota.getEspecie());
        holder.tvRaza.setText(mascota.getRaza());
        holder.tvEdad.setText(mascota.getEdad() + " meses");

        // Cargar la PRIMERA foto del List<String>
        List<String> fotos = mascota.getFotos();
        if (fotos != null && !fotos.isEmpty()) {
            String primeraFoto = fotos.get(0).trim();  // Tomamos la primera
            if (!primeraFoto.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(primeraFoto)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(holder.ivFoto);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaMascotas != null ? listaMascotas.size() : 0;
    }

    static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvEspecie, tvRaza, tvEdad;
        ImageView ivFoto;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEspecie = itemView.findViewById(R.id.tv_especie);
            tvRaza    = itemView.findViewById(R.id.tv_raza);
            tvEdad    = itemView.findViewById(R.id.tv_edad);
            ivFoto    = itemView.findViewById(R.id.iv_foto_mascota);
        }
    }
}