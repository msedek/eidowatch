package com.eidotab.smartab.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.BoxInsetLayout;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.eidotab.smartab.Interfaz.IRequestMensaje;
import com.eidotab.smartab.Models.Mensaje;
import com.eidotab.smartab.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.eidotab.smartab.R.color.abc_background_cache_hint_selector_material_dark;
import static com.eidotab.smartab.R.color.black;
import static com.eidotab.smartab.R.color.caja;
import static com.eidotab.smartab.R.color.letras;
import static com.eidotab.smartab.R.color.white;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH>
{

    private Context mContext;
    public ArrayList<Mensaje> data;



    public MyAdapter(Context context)
    {
        this.mContext = context;
        data = new ArrayList<>();
    }

    public int AddNew(Mensaje mensaje)
    {
        data.add(mensaje);
        int position = data.indexOf(mensaje);
        notifyItemInserted(position);
        return position;
    }

    @Override
    public MyAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyAdapter.VH(v);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.VH holder, final int position)
    {
        final Mensaje mensaje = data.get(position);
        holder.setdata(mensaje, position);

    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder
    {

        TextView txrem;
        TextView txfech;
        TextView txtext;
        TextView txestado;
        Button   chbutton;
        BoxInsetLayout elpadre;

        TimeZone tz;
        DateFormat df;



        public VH(View itemView)
        {

            super(itemView);

            txrem    = itemView.findViewById(R.id.txrem);
            txfech   = itemView.findViewById(R.id.txfech);
            txtext   = itemView.findViewById(R.id.txtext);
            txestado = itemView.findViewById(R.id.txestado);
            chbutton = itemView.findViewById(R.id.chbutton);
            elpadre  = itemView.findViewById(R.id.elpadre);

            Calendar cal = Calendar.getInstance();
            tz = cal.getTimeZone();
            df = new SimpleDateFormat("h:mm a");
            df.setTimeZone(tz);



        }

        @SuppressLint("ClickableViewAccessibility")
        private void setdata(final Mensaje mensaje, final int position)
        {

            String rem ;


            String[] separated = mensaje.getRemitente().split("-");
            rem = separated[0].trim(); // this will contain "Fruit"
            String rereal = " ";

            if(separated.length > 1)
            {
                rereal = separated[1].trim();
            }


            rem = rem.substring(0,1).toUpperCase() + rem.substring(1).toLowerCase();
            String date = df.format(mensaje.getFechamensaje());
            String texto = mensaje.getTexto();
            texto = texto.substring(0,1).toUpperCase() + texto.substring(1).toLowerCase();
            String estado = mensaje.getEstadomensaje();

            elpadre.setBackground(setcolor(rem, MotionEvent.ACTION_UP));

            if(rereal.equals(" "))
            {
                txrem.setText(rem);
            }
            else
            {
                rereal = rereal.substring(0,1).toUpperCase() + rereal.substring(1).toLowerCase();
                txrem.setText(rereal);
            }

            txfech.setText(date.toLowerCase());
            txtext.setText(texto);
            txestado.setText(estado);

            final String finalRem = rem;
            chbutton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int evento = event.getAction();

                    switch (evento)
                    {
                        case MotionEvent.ACTION_DOWN :


                            elpadre.setBackground(setcolor(finalRem, event.getAction()));

                            chbutton.setBackground(mContext.getDrawable(R.drawable.chbuttp));

                            txtext.setTextColor(mContext.getColor(R.color.black));
                            txrem.setTextColor(mContext.getColor(R.color.black));
                            txfech.setTextColor(mContext.getColor(R.color.black));



                            break;

                        case MotionEvent.ACTION_UP :


                            elpadre.setBackground(setcolor(finalRem, event.getAction()));

                            chbutton.setBackground(mContext.getDrawable(R.drawable.chbutt));

                            txtext.setTextColor(mContext.getColor(R.color.letras));
                            txrem.setTextColor(mContext.getColor(R.color.letras));
                            txfech.setTextColor(mContext.getColor(R.color.letras));


                            txestado.setText("alisto");
                            mensaje.setEstadomensaje("alisto");
                            updateRetrofitEstado(mensaje.get_id(), mensaje);
                            data.remove(position);
                            elpadre.performClick();
                            notifyDataSetChanged();


                            break;

                            default:

                                elpadre.setBackground(setcolor(finalRem,  MotionEvent.ACTION_UP));

                                chbutton.setBackground(mContext.getDrawable(R.drawable.chbutt));

                                txtext.setTextColor(mContext.getColor(R.color.letras));
                                txrem.setTextColor(mContext.getColor(R.color.letras));
                                txfech.setTextColor(mContext.getColor(R.color.letras));


                                break;

                    }

                    return true;
                }
            });
        }

        private Drawable setcolor(String selector, int motionEvent)
        {
            Drawable fondo = null;

            if(motionEvent == MotionEvent.ACTION_UP )
            {
                if(selector.toLowerCase().contains("caja"))
                {

                    fondo  = mContext.getDrawable(R.drawable.caja);

                }

                if(selector.toLowerCase().contains("mesa"))
                {

                    fondo  = mContext.getDrawable(R.drawable.mesa);

                }

                if(selector.toLowerCase().contains("cocina"))
                {

                    fondo  = mContext.getDrawable(R.drawable.cocina);

                }

                if(selector.toLowerCase().contains("bar"))
                {

                    fondo  = mContext.getDrawable(R.drawable.bar);

                }

                if(selector.toLowerCase().contains("dpto"))
                {

                    fondo  = mContext.getDrawable(R.drawable.geren);

                }
            }
            else
            {
                if(selector.toLowerCase().contains("caja"))
                {

                    fondo  = mContext.getDrawable(R.drawable.cajap);

                }

                if(selector.toLowerCase().contains("mesa"))
                {

                    fondo  = mContext.getDrawable(R.drawable.mesap);

                }

                if(selector.toLowerCase().contains("cocina"))
                {

                    fondo  = mContext.getDrawable(R.drawable.cocinap);

                }

                if(selector.toLowerCase().contains("bar"))
                {

                    fondo  = mContext.getDrawable(R.drawable.barp);

                }

                if(selector.toLowerCase().contains("dpto"))
                {

                    fondo  = mContext.getDrawable(R.drawable.gerenp);

                }
            }

            return fondo;
        }
    }



    private void updateRetrofitEstado(String id, Mensaje mensaje)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.iptab))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IRequestMensaje request = retrofit.create(IRequestMensaje.class);

        Call<Mensaje> call = request.updateMensaje(id, mensaje);

        call.enqueue(new Callback<Mensaje>()
        {
            @Override
            public void onResponse(@NonNull Call<Mensaje> call, @NonNull Response<Mensaje> response)
            {
                Log.i("Update Estado Orden", "Se Actualizo la Orden Correctamente");
            }

            @Override
            public void onFailure(@NonNull Call<Mensaje> call, @NonNull Throwable t)
            {
                Log.d("Error " , t.getMessage());
            }
        });
    }

}
