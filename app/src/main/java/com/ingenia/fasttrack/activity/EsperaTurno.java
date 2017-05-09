package com.ingenia.fasttrack.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button botonDestruirTicket;
    public static TextView textViewTextoMostraClienteAsesorAtencion;
    RelativeLayout relativeLayoutEsperaTurnoAsesor;
    public static RelativeLayout relativeLayoutMostrarClienteTurnoAsesor;
    public final static int WHITE = Color.rgb(38,37,36);
    public final static int BLACK = 0xFFFFFFFF;
    public final static int WIDTH = 2000;
    public final static int HEIGHT =2000;
    private String turnoCliente, nombreCliente, desMensaje, ticketString, orderId, messageDialog, codPrecio, numTickets;
    private boolean ticketEsAsumido;
    private JSONObject ticketJSONObject;
    private String codEstado;

    private ProgressDialog progressDialog;


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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        qrCodeImageview = (ImageView) findViewById(R.id.img_qr_turno);
        textViewnombreCliente = (TextView) findViewById(R.id.textViewNombreCliente);
        textViewTextoEsperaTurno = (TextView) findViewById(R.id.textViewTextoEsperaTurno);
        //textViewTextoMostraClienteAsesorAtencion = (TextView) findViewById(R.id.textViewTextoMostraClienteAsesorAtencion);
        //relativeLayoutEsperaTurnoAsesor = (RelativeLayout) findViewById(R.id.relativeLayoutEsperaAtencionAsesor);
        //relativeLayoutMostrarClienteTurnoAsesor = (RelativeLayout) findViewById(R.id.relativeLayoutMostrarTurnoAsesorCliente);
        botonDestruirTicket =(Button) findViewById(R.id.botonDestruirTicket);
        botonDestruirTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                builder
                        .setTitle("ADVERTENCIA")
                        .setMessage("El Ticket Fast Track será destruido y no podrá volverse a usar, ¿está seguro de realizar esta acción?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                detruirTicket();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        }).setCancelable(false).show();
            }});


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
                orderId = null;
                codPrecio = null;
                numTickets = null;
            }
            else
            {
                turnoCliente = extras.getString("turnoCliente");
                nombreCliente = extras.getString("nombreCliente");
                desMensaje = extras.getString("message");
                ticketEsAsumido = extras.getBoolean("ticketEsAsumido");
                codEstado = extras.getString("codEstado");
                orderId = extras.getString("orderId");
                codPrecio = extras.getString("codPrecio");
                numTickets = extras.getString("numTickets");

                Log.i(TAG, "Turno Cliente -> "+turnoCliente);
                Log.i(TAG, "Nombre Cliente Seguimiento -> "+nombreCliente);
                Log.i(TAG, "desMensaje -> "+desMensaje);
                Log.i(TAG, "existeTicketSinProcesar -> "+ticketEsAsumido);

                Log.i(TAG, "orderId -> "+orderId);
                Log.i(TAG, "codPrecio -> "+codPrecio);
                Log.i(TAG, "numTickets -> "+numTickets);
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
            //textViewnombreCliente.setText("A"+nombreCliente.toString().toUpperCase());
            textViewnombreCliente.setText(nombreCliente);
            //textViewTextoEsperaTurno.setText(""+desMensaje);

           /* if(ticketEsAsumido)//EL TICKET NO SE HA ASUMIDO.
            {
                relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
                textViewTextoMostraClienteAsesorAtencion.setText(""+desMensaje);
            }*/

           /* textViewTextoEsperaTurno.setText("Muestre su Fast Track al recepcionista del establecimiento. " +
                    "Valido para ("+numTickets+")+ Fast Track.");*/


            // Creating MultiColor Text
            SpannableStringBuilder snackbarText = new SpannableStringBuilder();
            snackbarText.append("Muestre su Fast Track al recepcionista del establecimiento. Válido para ");
            int boldStart = snackbarText.length();
            snackbarText.append("("+numTickets+")");
            snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(153,189,43)), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackbarText.append(" Fast Track.");

            textViewTextoEsperaTurno.setText(snackbarText);


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

                  /*  relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                    relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
                    textViewTextoMostraClienteAsesorAtencion.setText(TextUtils.isEmpty(desMensaje)?message:desMensaje);*/

                }

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_USO_TICKET))
                {
                    //HABILITO PARA COMPRAR UN TICKET DE NUEVO Y MUESTRO MENSAJE DE QUE EL TICKET ACTUAL YA SE USO

                    String message = intent.getExtras().getString("message");

                    AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                    builder
                            .setTitle("NOTIFIFACIÓN TICKET")
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

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_CLIENTE_ANULAR))
                {
                    //HABILITO PARA COMPRAR UN TICKET DE NUEVO Y MUESTRO MENSAJE DE QUE EL TICKET ACTUAL YA SE USO

                    String message = intent.getExtras().getString("message");

                    AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                    builder
                            .setTitle("TICKET CANCELADO")
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

        Log.d("save","CONSULTANDO ONRESUME");


        serviceConsultarDisponibilidadTicket();
        //consultarUsoTicket(); //pendiente

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_OCULTAR_PANTALLA_ESPERA_TURNO_ASESOR));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_USO_TICKET));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_CLIENTE_ANULAR));

        NotificationUtils.clearNotifications(getApplicationContext());

        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        //Salvamos el estado de la activity.
        sharedPreferences.putBoolean("saveEstado", true);
        Log.d("save", ""+sharedPreferences.getBoolean("saveEstado"));
        super.onSaveInstanceState(savedInstanceState);

    }

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d("save", "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras != null)
        {

            Log.i("extras","flag"+messageDialog);

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
             /*   relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                textViewTextoMostraClienteAsesorAtencion.setText(""+message);
                relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);*/

            }

            if (extras.containsKey("message"))
            {
                //setContentView(R.layout.viewmain);
                // extract the extra-data in the Notification
                messageDialog = extras.getString("message");
                Log.i("flag","flag"+messageDialog);
                /*txtView = (TextView) findViewById(R.id.txtMessage);
                txtView.setText(msg);*/


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

            if (extras.containsKey("notificarAnulacionTicket"))
            {

                String message = extras.getString("message");

                AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                builder
                        .setTitle("TICKET CANCELADO")
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
             /*   relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
*/
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

        Log.d("save","CONSULTANDO ONRESUME");

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

                                Log.d("save","xx->"+codEstado);


                                if(TextUtils.isEmpty(desMensaje))//SI desMensaje ES NULL ES PORQUE NO SE HA ASUMIDO.
                                {
                                    ticketEsAsumido = false;
                                    //relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                                    //relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
                                    //textViewTextoMostraClienteAsesorAtencion.setText(""+desMensaje);
                                }

                                else
                                {
                                   /* relativeLayoutEsperaTurnoAsesor.setVisibility(View.GONE);
                                    relativeLayoutMostrarClienteTurnoAsesor.setVisibility(View.VISIBLE);
                                    textViewTextoMostraClienteAsesorAtencion.setText(""+desMensaje);*/




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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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

    private void consultarUsoTicket()//REVISAMOS SI EXISTE UN TICKET DISPONIBLE
    {
        String _urlWebServiceConsultarDisponibilidadTicket = vars.ipServer.concat("/ws/usoTicket");

        String TAG = "serviceConsultarDisponibilidadTicket";

        Log.d("save","CONSULTANDO consultarUsoTicket");

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
                                desMensaje = ticket.getString("desMensaje");
                                codEstado = ticket.getString("codEstado");

                                Log.d("save","xx->"+codEstado);

                                if(codEstado.equals("2"))//VALIDAR PENDIENTE
                                {
                                    Intent intent = getIntent();

                                    String message = intent.getExtras().getString("message");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                                    builder
                                            .setTitle("NOTIFIFACIÓN TICKET")
                                            .setMessage("xx"+messageDialog)
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                headers.put("orderId",orderId);
                headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }

    private void detruirTicket()//Desechamos el ticket.
    {
        String _urlWebServiceConsultarDisponibilidadTicket = vars.ipServer.concat("/ws/destruirTicket");

        progressDialog = new ProgressDialog(EsperaTurno.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Estamos cancelando su Ticket, por favor espera un momento...");
        progressDialog.show();
        progressDialog.setCancelable(false);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceConsultarDisponibilidadTicket, null,
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

                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                                    builder
                                            .setTitle("NOTIFIFACIÓN TICKET")
                                            .setMessage("El Fast Track N°"+turnoCliente+", ha sido destruido para un próximo uso.")
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
                            else
                            {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
                                builder
                                        .setTitle("NOTIFIFACIÓN TICKET")
                                        .setMessage("Error eliminando su Ticket, contacte al administrador FastTrack de inmediato.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                            }
                                        }).setCancelable(false).show();

                            }

                        }
                        catch (JSONException e)
                        {
                            progressDialog.dismiss();
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
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EsperaTurno.this);
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
                headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
                headers.put("codTicket",turnoCliente);
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }
}
