package com.patitasalrescate.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.patitasalrescate.Controllers.ActividadPerfilMascota;
import com.patitasalrescate.R;
import com.patitasalrescate.model.Mascota;

import java.util.List;

public class AdaptadorMascotas extends RecyclerView.Adapter<AdaptadorMascotas.MascotaViewHolder> {

    private final List<Mascota> listaMascotas;
    private final boolean esModoRefugio; // TRUE = Refugio (editar), FALSE = Adoptante (adoptar)

    public AdaptadorMascotas(List<Mascota> lista, boolean esModoRefugio) {
        this.listaMascotas = lista;
        this.esModoRefugio = esModoRefugio;
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
        Context context = holder.itemView.getContext();

        // Nombre (si está vacío, fallback)
        String nombre = (mascota.getNombre() != null && !mascota.getNombre().trim().isEmpty())
                ? mascota.getNombre()
                : (mascota.getEspecie() + " #" + mascota.getIdMascota());
        holder.txtNombre.setText(nombre);

        // Raza - Edad
        holder.txtRaza.setText(mascota.getRaza() + " - " + mascota.getEdad() + " meses");

        // Estado: si está adoptado lo mostramos
        holder.txtEstado.setText(mascota.isEsAdoptado() ? "ADOPTADO ✅" : mascota.getTemperamento());

        // Foto
        List<String> fotos = mascota.getFotos();
        if (fotos != null && !fotos.isEmpty() && fotos.get(0) != null && !fotos.get(0).trim().isEmpty()) {
            String url = fotos.get(0).trim();
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Botones según rol
        holder.btnEditar.setVisibility(esModoRefugio ? View.VISIBLE : View.GONE);
        holder.btnAdoptar.setVisibility(esModoRefugio ? View.GONE : View.VISIBLE);

        // Click en toda la tarjeta -> abre PERFIL (según rol)
        holder.itemView.setOnClickListener(v ->
                abrirPerfil(context, mascota.getIdMascota(), esModoRefugio)
        );

        // Click en editar -> PERFIL en modo edición
        holder.btnEditar.setOnClickListener(v ->
                abrirPerfil(context, mascota.getIdMascota(), true)
        );

        // Click en adoptar -> PERFIL en modo adoptante (el botón adoptar sale ahí)
        holder.btnAdoptar.setOnClickListener(v ->
                abrirPerfil(context, mascota.getIdMascota(), false)
        );
    }

    private void abrirPerfil(Context context, int idMascota, boolean modoEdicion) {
        Intent intent = new Intent(context, ActividadPerfilMascota.class);
        intent.putExtra("id_mascota_key", idMascota);
        intent.putExtra("es_modo_edicion", modoEdicion); // TRUE refugio / FALSE adoptante
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listaMascotas != null ? listaMascotas.size() : 0;
    }

    static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtRaza, txtEstado;
        ImageView imgFoto;
        Button btnAdoptar, btnEditar;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_foto_mascota);
            txtNombre = itemView.findViewById(R.id.txt_nombre_mascota);
            txtRaza = itemView.findViewById(R.id.txt_raza_mascota);
            txtEstado = itemView.findViewById(R.id.txt_estado_mascota);
            btnAdoptar = itemView.findViewById(R.id.btn_adoptar);
            btnEditar = itemView.findViewById(R.id.btn_editar_estado);
        }
    }
}
