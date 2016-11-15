package com.ingenia.fasttrack.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ingenia.fasttrack.R;
import com.ingenia.fasttrack.app.Config;
import com.ingenia.fasttrack.sharedPreferences.gestionSharedPreferences;
import com.ingenia.fasttrack.util.NotificationUtils;
import com.ingenia.fasttrack.vars.vars;
import com.ingenia.fasttrack.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EsperaTurno extends AppCompatActivity
{

    boolean ifBack = true;
    ImageView qrCodeImageview;
    TextView textViewnombreCliente;
    TextView textViewTextoEsperaTurno;
    public static TextView textViewTextoMostraClienteAsesorAtencion;
    RelativeLayout relativeLayoutEsperaTurnoAsesor;
    public static RelativeLayout relativeLayoutMostrarClienteTurnoAsesor;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 600;
    public final static int HEIGHT =600;
    private String turnoCliente, nombreCliente, desMensaje,  ticketString;
    private boolean ticketEsAsumido;
    private JSONObject ticketJSONObject;
    private String codEstado;

    public vars vars;


    private static final String TAG = EsperaTurno.class.getSimpleName();
    private gestionSharedPreferences sharedPreferences;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espera_turno);
        //getSupportActionBar().hide();


        qrCodeImageview = (ImageView) findViewById(R.id.img_qr_turno);
        textViewnombreCliente = (TextView) findViewById(R.id.textViewNombreCliente);
        textViewTextoEsperaTurno = (TextView) findViewById(R.id.textViewTextoEsperaTurno);
        textViewTextoMostraClienteAsesorAtencion = (TextView) findViewById(R.id.textViewTextoMostraClienteAsesorAtencion);
        relativeLayoutEsperaTurnoAsesor = (RelativeLayout) findViewById(R.id.relativeLayoutEsperaAtencionAsesor);
        relativeLayoutMostrarClienteTurnoAsesor = (RelativeLayout) findViewById(R.id.relativeLayoutMostrarTurnoAsesorCliente);

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        vars = new vars();


        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
            {
                turnoCliente = null;
                nombreCliente = null;
                desMensaje = null;
                ticketEsAsumido = true;
                codEstado = null;
            }
            else
            {
                turnoCliente = extras.getString("turnoCliente");
                nombreCliente = extras.getString("nombreCliente");
                desMensaje = extras.getString("message");
                ticketEsAsumido = extras.getBoolean("ticketEsAsumido");
                codEstado = extras.getString("codEstado");

                Log.i(TAG, "Turno Cliente -> "+turnoCliente);
                Log.i(TAG, "Nombre Cliente -> "+nombreCliente);
                Log.i(TAG, "desMensaje -> "+desMensaje);
                Log.i(TAG, "existeTicketSinProcesar -> "+ticketEsAsumido);
            }

            if (sharedPreferences.getBoolean("saveEstado"))
            {

                //serviceConsultarDisponibilidadTicket();

/*
                turnoCliente = extras.getString("turnoCliente");
                nombreCliente = extras.getString("nombreCliente");
                desMensaje = extras.getString("message");
                ticketEsAsumido = extras.getBoolean("ticketEsAsumido");
                codEstado = extras.getString("codEstado");

                Log.i(TAG, "Turno Cliente -> "+turnoCliente);
                Log.i(TAG, "Nombre Cliente -> "+nombreCliente);
                Log.i(TAG, "desMensaje -> "+desMensaje);
                Log.i(TAG, "existeTicketSinProcesar -> "+ticketEsAsumido);*/

                Log.d("savedInstanceState", "" + "true");
            }
        }

        try
        {
            Bitmap bitmap = encodeAsBitmap(turnoCliente);
            qrCodeImageview.setImageBitmap(bitmap);
            textViewnombreCliente.setText(nombreCliente.toString().toUpperCase());
            //textViewTextoEsperaTurno.setText(""+desMensaje);

            if(ticketEsAsumido)//EL TICKET NO SE HA ASUMIDO.
            {
                relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
                textViewTextoMostraClienteAsesorAtencion.setText(""+desMensaje);
            }


        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_OCULTAR_PANTALLA_ESPERA_TURNO_ASESOR))
                {
                   //OCULTO PANTALLA ESPERA TURNO ASESOR Y MUESTRO MODULO CON EL ASESOR QUE LO ATENDERA
                    String message = intent.getExtras().getString("message");

                    relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                    relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
                    textViewTextoMostraClienteAsesorAtencion.setText(TextUtils.isEmpty(desMensaje)?message:desMensaje);

                }

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_USO_TICKET))
                {
                    //HABILITO PARA COMPRAR UN TICKET DE NUEVO Y MUESTRO MENSAJE DE QUE EL TICKET ACTUAL YA SE USO

                    String message = intent.getExtras().getString("message");

                    AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                    builder
                            .setTitle("NOTITIFACIÓN TICKET")
                            .setMessage(""+message)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    Intent intent = new Intent(EsperaTurno.this, Inicio.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setCancelable(false).show();
/*

                    relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                    relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);*/
                }
            }
        };
    }

    Bitmap encodeAsBitmap(String str) throws WriterException
    {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_OCULTAR_PANTALLA_ESPERA_TURNO_ASESOR));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_USO_TICKET));

        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        //Salvamos el estado de la activity.
        sharedPreferences.putBoolean("saveEstado", true);
        Log.d("save", ""+sharedPreferences.getBoolean("saveEstado"));
        super.onSaveInstanceState(savedInstanceState);

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            if (extras.containsKey("ocultarModuloEsperaCliente"))
            {
                //setContentView(R.layout.viewmain);
                // extract the extra-data in the Notification
                String msg = extras.getString("ocultarModuloEsperaCliente");
                String message = extras.getString("message");
                Log.i("flag","flag"+message);
                /*txtView = (TextView) findViewById(R.id.txtMessage);
                txtView.setText(msg);*/
                //OCULTO PANTALLA ESPERA TURNO ASESOR Y MUESTRO MODULO CON EL ASESOR QUE LO ATENDERA
                relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                textViewTextoMostraClienteAsesorAtencion.setText(""+message);
                relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);

            }

            if (extras.containsKey("notificarUsoTicket"))
            {

                String message = extras.getString("message");

                AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                builder
                        .setTitle("NOTIFICACIÓN TICKET")
                        .setMessage(""+message)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent intent = new Intent(EsperaTurno.this, Inicio.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setCancelable(false).show();

            }

            if (extras.containsKey("ticket"))
            {
                //setContentView(R.layout.viewmain);
                // extract the extra-data in the Notification
                ticketString = extras.getString("ticket");
                try
                {
                    ticketJSONObject = new JSONObject(ticketString);
                    Log.i("ticket_espera",ticketJSONObject.toString());


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                /*txtView = (TextView) findViewById(R.id.txtMessage);
                txtView.setText(msg);*/
                //OCULTO PANTALLA ESPERA TURNO ASESOR Y MUESTRO MODULO CON EL ASESOR QUE LO ATENDERA
                relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (ifBack)
        {
            //DISABLED BUTTON BACK
            // Toast.makeText(this,"TRUE.", Toast.LENGTH_LONG).show();
        }

        else
        {
            //Toast.makeText(this,"false.", Toast.LENGTH_LONG).show();
            super.onBackPressed(); // Process Back key  default behavior.

        }

    }


    private void serviceConsultarDisponibilidadTicket()//REVISAMOS SI EXISTE UN TICKET DISPONIBLE
    {
        String _urlWebServiceConsultarDisponibilidadTicket = vars.ipServer.concat("/ws/disponibilidadTicket");

        String TAG = "serviceConsultarDisponibilidadTicket";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebServiceConsultarDisponibilidadTicket, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONArray clientes;

                            if(response.getBoolean("status"))
                            {
                                final JSONObject ticket = response.getJSONObject("ticket");
                                final String turnoCliente, nombreCliente, codEstado;

                                turnoCliente = ticket.getString("codTicket");
                                nombreCliente = ticket.getString("nomCliente");
                                desMensaje = ticket.getString("desMensaje");
                                codEstado = ticket.getString("codEstado");

                                if(TextUtils.isEmpty(desMensaje))//SI desMensaje ES NULL ES PORQUE NO SE HA ASUMIDO.
                                {
                                    ticketEsAsumido = false;
                                }

                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                                    builder
                                            .setTitle("TICKET DISPONIBLE")
                                            .setMessage("Vaya! Se ha encontrado un Ticket que debe ser usado, de lo "+
                                                    "contrario no podrás comprar más Tickets, Usalo justo ahora!")
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    Intent intent = new Intent(EsperaTurno.this, Inicio.class);
                                                    intent.putExtra("turnoCliente", turnoCliente);
                                                    intent.putExtra("nombreCliente", nombreCliente);
                                                    intent.putExtra("message", desMensaje);
                                                    intent.putExtra("ticketEsAsumido", ticketEsAsumido);
                                                    intent.putExtra("codEstado", codEstado);
                                                    startActivity(intent);
                                                    EsperaTurno.this.finish();

                                                }
                                            }).setCancelable(false).show();
                                }



                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof ServerError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NetworkError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof ParseError)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                        }
                                    }).show();
                        }

                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                //headers.put("idDevice",getDeviceId());
                headers.put("idDevice",sharedPreferences.getString("deviceID"));
                headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }
}
