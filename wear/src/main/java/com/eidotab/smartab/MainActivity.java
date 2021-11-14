package com.eidotab.smartab;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.eidotab.smartab.Adapter.MyAdapter;
import com.eidotab.smartab.Interfaz.IRequestMensaje;
import com.eidotab.smartab.Models.Mensaje;
import com.eidotab.smartab.Models.Mesa;
import com.eidotab.smartab.Models.MesaSorter;
import com.eidotab.smartab.SQlite.DBHelper;
import java.util.ArrayList;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.content.Context;




public class MainActivity extends WearableActivity {
    RecyclerView myRecycler;
    MyAdapter adp;
    TextView habtimertxt;

    Spinner combobox;

    Button smesa;

    Vibrator vibrator;

    DBHelper myDB;
    LinearLayout lymesa;

    ArrayList<String> atencion;

    Boolean centrar;

    Boolean primera;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        myRecycler  = findViewById(R.id.myRecyclerView);
        habtimertxt = findViewById(R.id.habtimertxt);
        combobox    = findViewById(R.id.combobox);
        smesa       = findViewById(R.id.btn_smesa);
        lymesa      = findViewById(R.id.lymesa);

        vibrator    = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        myDB = DBHelper.GetDBHelper(this);

        atencion = new ArrayList<>();

        centrar = true;

        primera = false;


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mesas_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        combobox.setAdapter(adapter);
        Mesa status = myDB.getMesa();

        if(status.getMesa() == null)
        {
            habtimertxt.setText("false");
            myRecycler.setVisibility(View.INVISIBLE);
            lymesa.setVisibility(View.VISIBLE);
            final Mesa mesa = new Mesa();
            smesa.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mesa.setMesa(combobox.getSelectedItem().toString());
                    myDB.addMesa(mesa);
                    lymesa.setVisibility(View.GONE);
                    myRecycler.setVisibility(View.VISIBLE);
                    setAdapter();
                    setdata(atencion);
                    habtimertxt.setText("true");
                }
            });
        }
        else
        {
            setAdapter();
            setdata(atencion);
            habtimertxt.setText("true");
        }



        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {

                if(habtimertxt.getText().equals("true"))
                {
                    habtimertxt.setText("false");

                    loadRetrofitMensaje(atencion);

                }// LO QUE SE EJECUTA ACA DENTRO SE EJECUTA SIEMPRE Y CUANDO LA ETIQUETA SEA TRUE

                mHandler.postDelayed(this, 2000);
            }
        },0);

    }

    private ArrayList<String> setdata(ArrayList<String> atencion)
    {

        Mesa mesa;
        mesa = myDB.getMesa();
        String[] separated = mesa.getMesa().split("–");
        String ini = separated[0].trim();
        String fin = separated[1].trim();

        for (int i = Integer.parseInt(ini); i <= Integer.parseInt(fin) ; i++)
        {

            atencion.add("0" + i);

        }

        return atencion;

    }

    private void loadRetrofitMensaje(final ArrayList<String> atencion)
    {

        final ArrayList<String> att = new ArrayList<>();
        final ArrayList<Mensaje>filtrados = new ArrayList<>();

        for (String agg : atencion)
        {

            att.add(agg);

        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.iptab))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IRequestMensaje request = retrofit.create(IRequestMensaje.class);

        Call<ArrayList<Mensaje>> call = request.getJSONMensajes();

        call.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Mensaje>> call, @NonNull Response<ArrayList<Mensaje>> response) {

                ArrayList<Mensaje> recib = new ArrayList<>();

                for (Mensaje llego: response.body())
                {
                    if(llego.getEstadomensaje().equals("PENDIENTE"))
                    {
                        recib.add(llego);
                    }
                }

                if(adp.data.size() > 0)
                {
                    recib.removeAll(adp.data);
                    primera = true;

                }

                if (recib.size() >  0)
                {
                    Collections.sort(recib, new MesaSorter());

                    for (int i = 0; i < att.size(); i++)
                    {

                        for (int j = 0; j < recib.size(); j++)
                        {
                            String[] separated = recib.get(j).getRemitente().split("/");

                            int sep = separated.length;

                            String rem = separated[0].trim();

                            String destino = " ";

                            if(sep > 1)
                            {
                                destino = separated[1].trim();
                            }


                            Boolean esmesa = rem.contains(att.get(i));

                            if(esmesa)// || smstab)
                            {

                                filtrados.add(recib.get(j));
                                recib.remove(j);
                                i = 0;

                            }
                            else
                            {
                                Boolean esotro = destino.contains(att.get(i));

                                if(esotro)
                                {
                                    recib.get(j).setRemitente(rem);

                                    filtrados.add(recib.get(j));
                                    recib.remove(j);
                                    i = 0;

                                }
                            }
                        }
                    }
                }


                Boolean llego = false;
                ArrayList<Mensaje> aux = new ArrayList<>();
                for(Mensaje mensaje : filtrados)
                {

                    Boolean urgente = false;

                    if(mensaje.getRemitente().contains("dpto"))
                    {
                        urgente = true;
                        aux.add(mensaje);
                    }

                    if(!urgente)
                    {
                        adp.AddNew(mensaje);
                    }


                    if(!primera && filtrados.indexOf(mensaje) == (filtrados.size() - 1))
                    {
                        long[] vibrationPattern = {0, 200};
                        //-1 - don't repeat
                        final int indexInPatternToRepeat = -1;
                        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

                    }

                    llego = true;

                }

                if(aux.size() > 0)
                {

                    for(Mensaje mensa : adp.data)
                    {
                        aux.add(mensa);
                    }

                    adp.data.clear();

                    for(Mensaje mensa : aux)
                    {
                        adp.data.add(mensa);
                    }

                    adp.notifyDataSetChanged();

                    myRecycler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {

                            myRecycler.scrollToPosition(0);

                        }
                    }, 50);
                }

                if(llego && primera)
                {

                    Intent dismissIntent = new Intent(getApplicationContext(), MainActivity.class);

                    PendingIntent piDismiss = PendingIntent.getActivity(getApplicationContext(), 0, dismissIntent, 0);

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_flag_notification)
                                    .setContentTitle("Alerta")
                                    .setContentText("Mensajes Nuevos " + filtrados.size())
                                    .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                                    .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                                    .addAction(R.drawable.ic_flag_notification,"Abrir",piDismiss)
                                    .setAutoCancel(true)
                                    .setChannelId("eidoTab");

                    // Gets an instance of the NotificationManager service
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
                    notificationManager.notify(0, builder.build());

                }


/*                if (centrar)
                {
                    centrar = false;
                    myRecycler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {

                            myRecycler.scrollToPosition(0);

                        }
                    }, 50);

                }*/

                for (int i = 0; i < adp.data.size(); i++)
                {
                    String sacar = adp.data.get(i).getEstadomensaje();
                    if(sacar.equals("alisto"))
                    {
                        adp.data.remove(i);
                        adp.notifyDataSetChanged();
                    }
                }

                habtimertxt.setText("true");
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Mensaje>> call, @NonNull Throwable t) {
                // mostrarMensaje("Error: " + t.getMessage());
                habtimertxt.setText("true");
            }
        });
    }

    void setAdapter()  //TODO
    {
        adp = new MyAdapter(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecycler.setLayoutManager(linearLayoutManager);
        myRecycler.setAdapter(adp);

        myRecycler.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            boolean top;
            //boolean ch = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);



                if(adp.data.size() > 0)
                {

                    if (top)
                    {
                        int posiscroll = linearLayoutManager.findFirstVisibleItemPosition();
                        myRecycler.smoothScrollToPosition(posiscroll);

                    }
                    else
                    {
                        int posiscroll = linearLayoutManager.findLastVisibleItemPosition();
                        myRecycler.smoothScrollToPosition(posiscroll);
                    }

                }



            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    top = false;
                } else {

                    if (dy < 0)
                    {
                        top = true;
                    }

                }

            }
        });



    }

}
