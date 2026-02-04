package com.patitasalrescate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.patitasalrescate.R;
import com.patitasalrescate.model.Refugio;

import java.util.List;

public class AdaptadorRefugios extends RecyclerView.Adapter<AdaptadorRefugios.RefugioViewHolder> {

    private Context context;
    private List<Refugio> listaRefugios;

    // Constructor
    public AdaptadorRefugios(Context context, List<Refugio> listaRefugios) {
        this.context = context;
        this.listaRefugios = listaRefugios;
    }

    @NonNull
    @Override
    public RefugioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ly_item_cardview_refugio, parent, false);
        return new RefugioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RefugioViewHolder holder, int position) {
        Refugio refugioActual = listaRefugios.get(position);

        // 1. Asignamos los Textos
        holder.txtNombre.setText(refugioActual.getNombre());
        holder.txtDireccion.setText(refugioActual.getDireccion());
        holder.txtTelefono.setText(refugioActual.getNumCelular());

        // 2. Cargamos la IMAGEN usando GLIDE
        // Verificamos que la URL no sea nula ni vacía
        if (refugioActual.getFotoUrl() != null && !refugioActual.getFotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(refugioActual.getFotoUrl())
                    .centerCrop() // Recorta la imagen para llenar el cuadro sin deformar
                    .placeholder(R.drawable.ic_launcher_foreground) // Imagen de espera
                    .error(R.drawable.ic_launcher_foreground) // Imagen si falla la carga
                    .into(holder.imgFoto);
        } else {
            // Si no tiene URL, ponemos una imagen por defecto
            holder.imgFoto.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 3. Configuración del BOTÓN MAPA
        holder.btnMapa.setOnClickListener(v -> {
            if (refugioActual.getLatitud() != 0 && refugioActual.getLongitud() != 0) {

                // URI para abrir la ubicación exacta con una etiqueta (nombre del refugio)
                String uriMapa = "geo:" + refugioActual.getLatitud() + "," + refugioActual.getLongitud() +
                        "?q=" + refugioActual.getLatitud() + "," + refugioActual.getLongitud() +
                        "(" + Uri.encode(refugioActual.getNombre()) + ")";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriMapa));
                intent.setPackage("com.google.android.apps.maps"); // Intentamos abrir Google Maps directo

                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    // Si no tiene Maps, intentamos abrir cualquier navegador
                    Intent intentGenerico = new Intent(Intent.ACTION_VIEW, Uri.parse(uriMapa));
                    try {
                        context.startActivity(intentGenerico);
                    } catch (Exception ex) {
                        Toast.makeText(context, "No se encontró aplicación de mapas", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRefugios.size();
    }

    // --- CLASE VIEWHOLDER ---
    // Aquí enlazamos las variables Java con los IDs del XML (txt_, img_, btn_)
    public static class RefugioViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtDireccion, txtTelefono;
        ImageView imgFoto;
        ImageButton btnMapa;

        public RefugioViewHolder(@NonNull View itemView) {
            super(itemView);

            // Buscamos los IDs que definimos en el XML actualizado
            txtNombre = itemView.findViewById(R.id.txt_nombre_refugio);
            txtDireccion = itemView.findViewById(R.id.txt_direccion_refugio);
            txtTelefono = itemView.findViewById(R.id.txt_telefono_refugio);
            imgFoto = itemView.findViewById(R.id.img_foto_refugio);
            btnMapa = itemView.findViewById(R.id.btn_ver_mapa);
        }
    }
}