package com.ingenia.fasttrack.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ingenia.fasttrack.R;
import com.ingenia.fasttrack.SnackBar.MultilineSnackbar;
import com.ingenia.fasttrack.app.Config;
import com.ingenia.fasttrack.beans.Cliente;
import com.ingenia.fasttrack.beans.Sede;
import com.ingenia.fasttrack.beans.Ticket;
import com.ingenia.fasttrack.permisions.PermissionUtils;
import com.ingenia.fasttrack.sharedPreferences.gestionSharedPreferences;
import com.ingenia.fasttrack.util.IabHelper;
import com.ingenia.fasttrack.util.IabResult;
import com.ingenia.fasttrack.util.Inventory;
import com.ingenia.fasttrack.util.NotificationUtils;
import com.ingenia.fasttrack.util.Purchase;
import com.ingenia.fasttrack.vars.vars;
import com.ingenia.fasttrack.volley.ControllerSingleton;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class Inicio extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,OnPermissionCallback
{

    //private static final String TAG = Inicio.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String LogCatClass = "Inicio";

    private SharedPreferences pref;
    private String tokenFCM, desMensaje;
    private boolean ticketEsAsumido = true;

    private ArrayList <Cliente> listaClientes;
    private ArrayList <Sede> sedesCliente;
    private ArrayList <Sede> sedesClienteAux;
    private ArrayList <Sede> sedesClienteMostrarSnack;
    HashMap <String, ArrayList<Sede>> hashTableSedesCliente;
    HashMap <Integer, ArrayList<Sede>> listaSedesCliente;

    private NumberFormat numberFormat;

    private String skuPagoSede;

    private String temporalCodigoSede;

    private String temporalValorTicket;

    private String orderIDComprarGoogle;

    double radiusInMeters = 20.0;
    int strokeColor = 0xffff0000; //red outline
    int shadeColor = 0x44ff0000; //opaque red fill

    private boolean showMessage = false;

    private Boolean mRequestingLocationUpdates;
    private String REQUESTING_LOCATION_UPDATES_KEY;
    private String LOCATION_KEY;
    private String LAST_UPDATED_TIME_STRING_KEY;

    private String _urlWebService;

    public vars vars;

    GoogleApiClient mGoogleApiClient;

    private Marker marker;
    private MarkerOptions markerOptions;

    private String messageAlert = "";

    GoogleMap mGoogleMap;

    View coordinatorLayoutView;

    Location mCurrentLocation;

    LocationManager locationManager;

    private boolean mPermissionDenied = false;

    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_DEVICE = 2;

    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};
    public static final int MULTIPLE_PERMISSIONS = 2; // code you want.

    private static final String TAG = "InAppBilling FastTrack";
    IabHelper mHelper;

    private static final String ALLOWED_CHARACTERS ="0123456789abcdefghijklmnñopqrstuvwxyz!#$%&/()=?¡*[]{}-_+";

    private String mLastUpdateTime;

    gestionSharedPreferences sharedPreferences;
    int locationCount = 10;
    PendingIntent pendingIntent;

    public static AlertDialog alertDialogNombreCliente;

    private TelephonyManager telephonyManager;

    private Circle mCircle;
    private ArrayList<Circle>circulosSedes;

    public static Snackbar snackBar;
    private String mensajeSnackBar = "";

    Ticket ticket;

    private MultilineSnackbar multilineSnackbar;
    private ProgressDialog progressDialog;

    private String telephonyManagerDevice, telephonyManagerSerial, telephonyManagerAndroidId;

    private String deviceId;

    private TextView result;
    private PermissionHelper permissionHelper;
    private boolean isSingle;
    private android.support.v7.app.AlertDialog builder;
    private String[] neededPermission;

    private final static String SINGLE_PERMISSION = Manifest.permission.GET_ACCOUNTS;


    private final static String[] MULTI_PERMISSIONS = new String[]
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
            };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().hide();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ticketEsAsumido = true;

        updateValuesFromBundle(savedInstanceState);

        pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        tokenFCM = pref.getString("regId", null);

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE))
                {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                }

                else

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };



        numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);

        sharedPreferences = new gestionSharedPreferences(this);
        vars = new vars();

    /*    telephonyManager=(TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManagerDevice = "" + telephonyManager.getDeviceId();
        telephonyManagerSerial = "" + telephonyManager.getSimSerialNumber();
        telephonyManagerAndroidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(telephonyManagerAndroidId.hashCode(), ((long)telephonyManagerDevice.hashCode() << 32) |
                telephonyManagerSerial.hashCode());

        setDeviceId(deviceUuid.toString());
        sharedPreferences.putString("deviceID",getDeviceId());*/

        sedesCliente = new ArrayList<Sede>();
        sedesClienteAux = new ArrayList<Sede>();
        sedesClienteMostrarSnack = new ArrayList<Sede>();
        listaClientes = new ArrayList<Cliente>();
        circulosSedes = new ArrayList<Circle>();

        hashTableSedesCliente =  new HashMap <String, ArrayList<Sede>>();
        listaSedesCliente =  new HashMap <Integer, ArrayList<Sede>>();



        mRequestingLocationUpdates = false;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyvWPPTajqfMy/qf3OJ8eSdzMFyIClqweMY8LqvZne0WxxD4L4nHHfKaWS6OKl7IrAJwCVStCfxvZKv9kRH9MvsBnlrdGLT39veT+QXTVbwtbjGJZgLRGxf3OnYZ6fKXRrFfzAoeoBU6Gg4C1BjSK1Gqc3TMr/C+oOYC38gMEM09qy9SbY8jwrxC39U4yAWDlCjZVoeUo1kS/XX3Hmi7zPLLhOq4a0aFNobp6h1EYq8lS1ue7Tv10eP5JV1VPaTNQc6u+/2mSfDG73XPr/wOKvFmnucAOkZ1o8xFX74R8S1m8Yw13ALiKL4XzfU99Lo+kQFG1lamJzOjLVsVld8TvuQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener()
                                   {
                                       public void onIabSetupFinished(IabResult result)
                                       {
                                           if (!result.isSuccess())
                                           {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           }
                                           else
                                           {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });


        /*if (!isGooglePlayServicesAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("SIN SOPORTE DE GOOGLE PLAY SERVICES.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                            //startActivity(intent);
                            //finish();
                        }
                    }).show();
        }*/

        coordinatorLayoutView = findViewById(R.id.coordinator_layout);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {*/



        /*Snackbar.make(coordinatorLayoutView, "¿Deseas comprar tu FastTrack y así evitar la fila ahora?", Snackbar.LENGTH_INDEFINITE)
                .setAction("Comprar", undoOnClickListener).show();*/
        //Snackbar.dismiss();

/*
            }
        });*/



        //displayFirebaseRegId();



    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String tokenFCM = pref.getString("regId", null);

        Log.i("TokenFCM", "Firebase reg id: " + tokenFCM);
        Log.i("TokenFCM", "xx"+sharedPreferences.getString("deviceID"));
        Log.i("TokenFCM", "xx"+sharedPreferences.getString("MyTokenAPI"));

        sendRegistrationTokenFCMToServer(tokenFCM);

    }

    public String getTemporalCodigoSede()
    {
        return temporalCodigoSede;
    }

    public void setTemporalCodigoSede(String temporalCodigoSede)
    {
        this.temporalCodigoSede = temporalCodigoSede;
    }

    public String getOrderIDComprarGoogle()
    {
        return orderIDComprarGoogle;
    }

    public void setOrderIDComprarGoogle(String orderIDComprarGoogle)
    {
        this.orderIDComprarGoogle = orderIDComprarGoogle;
    }

    public String getTemporalValorTicket() {
        return temporalValorTicket;
    }

    public void setTemporalValorTicket(String temporalValorTicket) {
        this.temporalValorTicket = temporalValorTicket;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private static String getRandomSkuIdPurchaseToGoogle(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public String getSkuPagoSede()
    {
        return skuPagoSede;
    }

    public void setSkuPagoSede(String skuPagoSede)
    {
        this.skuPagoSede = skuPagoSede;
    }

    public View.OnClickListener undoOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
                /*Snackbar.make(view, "Item removed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

           /* Snackbar.make(coordinatorLayoutView, "¿Deseas comprar tu FastTrack y así evitar la fila ahora?", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Comprar", this).show();*/


           /* multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, "Estás en Banco de Occidente,\n"+
                    "evita la fila justo ahora\n"+"pagando solo 3.000$", Snackbar.LENGTH_INDEFINITE);
            multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            multilineSnackbar.setAction("Comprar", undoOnClickListener);
            multilineSnackbar.show();
            showMessage = true;
*/

            mostrarDialogoNombreCliente();

            Log.i("DISPOSITIVO","XXX"+sharedPreferences.getString("deviceID"));
            Log.i("DISPOSITIVO","XXX"+getSkuPagoSede());
            Log.i("DISPOSITIVO","XXX"+getRandomSkuIdPurchaseToGoogle(55));


        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
            permissionHelper.onActivityForResult(requestCode);
        }

    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure())
            {
                // Handle error
                Log.i(LogCatClass, "Error: "+result.getMessage().toString() );
                //Log.i(LogCatClass, "Error: "+purchase.getOriginalJson().toString() );


                return;
            }
            else
            if (purchase.getSku().equals(getSkuPagoSede()))
            {
                Log.i(LogCatClass, "SKU: "+result.getMessage().toString() );
                consumeItem();

                ticket = new Ticket();
                //buyButton.setEnabled(false);
                Log.i(LogCatClass, "Pagaste! Salta la fila ahora!!!" + purchase.getOriginalJson().toString());
                Log.i(LogCatClass, "Datos Compra: " + purchase.getOriginalJson().toString());

                String datosComparTicketGoogle = purchase.getOriginalJson().toString();

                if (datosComparTicketGoogle != null)
                {
                    try
                    {
                        JSONObject jsonObj = new JSONObject(datosComparTicketGoogle);
                        ticket.setOrderId(jsonObj.getString("orderId"));
                        ticket.setPackageName(jsonObj.getString("packageName"));
                        ticket.setProductId(jsonObj.getString("productId"));
                        ticket.setDeveloperPayload(jsonObj.getString("developerPayload"));
                        ticket.setPurchaseToken(jsonObj.getString("purchaseToken"));

                        serviceComprarTicket();
                    }
                    catch (final JSONException e)
                    {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }

                }
            }
        }
    };

    public void consumeItem()
    {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory)
        {

            if (result.isFailure())
            {
                // Handle failure
                Log.i(LogCatClass, "ERROR CONSUME: "+result.getMessage().toString() );

            }
            else
            {
                //NOTIFICAMOS QUE SE CONSUMIO LA COMPRA PARA PODER VOLVER A COMPRAR.
                Log.i(LogCatClass, "CONSUME: "+result.getMessage().toString() );

                mHelper.consumeAsync(inventory.getPurchase(getSkuPagoSede()),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener()
            {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result)
                {

                    if (result.isSuccess())
                    {
                        //clickButton.setEnabled(true);
                        Log.i(LogCatClass,"Pago Exitoso!!!");

                    }
                    else
                    {
                        // handle error
                        Log.i(LogCatClass,"Upps, Error Pago");
                    }
                }
            };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }

    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Su GPS esta apagado, para que FastTrack funcione correctamente debe encenderlo, ¿desea hacerlo?")
                .setCancelable(false)
                .setPositiveButton("Activar GPS",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);


                            }
                        }).
                setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private boolean checkPermissions()
    {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions)
        {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    private boolean isGooglePlayServicesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
        {
            return true;
        }
        else
        {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings)
        {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(LogCatClass, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(LogCatClass, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(LogCatClass, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        createLocationRequest();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            permissionHelper = PermissionHelper.getInstance(this);
            isSingle = false;
            permissionHelper
                    .setForceAccepting(true) // default is false. its here so you know that it exists.
                    .request(isSingle ? SINGLE_PERMISSION : MULTI_PERMISSIONS);
        }

        else
        {
            //serviceRegistroDispositivo();


            if(enableIDDevice())
            {
                serviceRegistroDispositivo();
            }


        }

        showMessage=true;
        setUpShowMessage(false,"","");

    }






    public void setUpShowMessage(boolean mostrar,String nombreSede, String ValorTicket)
    {
        if(mostrar)
        {
            if(!showMessage)
            {

                // Creating MultiColor Text
                SpannableStringBuilder snackbarText = new SpannableStringBuilder();
                snackbarText.append("Justo ahora estás en ");
                int boldStart = snackbarText.length();
                snackbarText.append(nombreSede);
                snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(244,11,82)), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append(".");
                snackbarText.append(" Evita la Fila pagando ");
                int boldStart2 = snackbarText.length();
                snackbarText.append("$"+ValorTicket);
                snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(244,11,82)), boldStart2, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart2, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append(" y haz tu diligencia rápido.");

                multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, snackbarText, Snackbar.LENGTH_INDEFINITE);
                multilineSnackbar.setAction("Aceptar", undoOnClickListener);
                multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                multilineSnackbar.show();
                showMessage = true;
            }
        }
        else
        {
            if(showMessage)
            {

                // Creating MultiColor Text
                SpannableStringBuilder snackbarText = new SpannableStringBuilder();
                snackbarText.append("Ubica los puntos ");
                int boldStart = snackbarText.length();
                snackbarText.append("FastTrack ");
                snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(244,11,82)), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append("en el mapa, haz tu diligencia y evita la fila justo ahora.");

                multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, snackbarText, Snackbar.LENGTH_INDEFINITE);
                multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                multilineSnackbar.show();
                showMessage = false;
            }
        }
    }

    boolean encontrado;

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(LogCatClass, "Firing onLocationChanged..............................................");

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mRequestingLocationUpdates = true;

        float[] distance = new float[2];

        encontrado = false;

        for(int k=0; k < circulosSedes.size(); k++)
        {
            Location.distanceBetween( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                    circulosSedes.get(k).getCenter().latitude, circulosSedes.get(k).getCenter().longitude, distance);

            if (distance[0] <= circulosSedes.get(k).getRadius())//ADENTRO
            {
                Log.d(LogCatClass, ""+"dentro");

                encontrado = true;
                setUpShowMessage(true, sedesClienteMostrarSnack.get(k).getNomSede().toString(),
                        numberFormat.format(Double.parseDouble(sedesClienteMostrarSnack.get(k).getValorTurno().toString())));
                setSkuPagoSede(sedesClienteMostrarSnack.get(k).getSkuPago().toString());

                setTemporalCodigoSede(sedesClienteMostrarSnack.get(k).getCodSede().toString());
                setTemporalValorTicket(sedesClienteMostrarSnack.get(k).getValorTurno().toString());
            }

        }

        if(!encontrado)
        {
            setUpShowMessage(false,"","");
        }


    }

    /*public void circle()
    {
        double radiusInMeters = 500.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        mCircle = mGoogleMap.addCircle (new CircleOptions()
                .center(new LatLng(3.43991, -76.5382))
                .radius(radiusInMeters)
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(1));
    }*/

    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        else
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.d(LogCatClass, "Location update started ..............: ");
        }
    }

    private boolean enableIDDevice()
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.READ_PHONE_STATE, true);


            }

            else

            {
                telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

                telephonyManagerDevice = "" + telephonyManager.getDeviceId();
                telephonyManagerSerial = "" + telephonyManager.getSimSerialNumber();
                telephonyManagerAndroidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);

                UUID deviceUuid = new UUID(telephonyManagerAndroidId.hashCode(), ((long) telephonyManagerDevice.hashCode() << 32) |
                        telephonyManagerSerial.hashCode());

                setDeviceId(deviceUuid.toString());
                sharedPreferences.putString("deviceID", deviceUuid.toString());
                Log.i("deviceid", "" + sharedPreferences.getString("deviceID"));
            }
            return true;

        }

        else
        {
            telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            telephonyManagerDevice = "" + telephonyManager.getDeviceId();
            telephonyManagerSerial = "" + telephonyManager.getSimSerialNumber();
            telephonyManagerAndroidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(telephonyManagerAndroidId.hashCode(), ((long) telephonyManagerDevice.hashCode() << 32) |
                    telephonyManagerSerial.hashCode());

            setDeviceId(deviceUuid.toString());
            sharedPreferences.putString("deviceID", deviceUuid.toString());
            Log.i("deviceid", "" + sharedPreferences.getString("deviceID"));
            return true;


        }
    }
    private void enableMyLocation()
    {

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

        }

        if (mGoogleMap != null)
        {
            // Access to the location has been granted to the app.
            // mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            //mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

            serviceObtenerSedes();
            mGoogleMap.setMyLocationEnabled(true);
            //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(mGoogleMap.getCameraPosition().zoom - 0.5f));

        }
    }

    @Override
    protected void onPause()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                //setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY))
            {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY))
            {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            //updateUI();
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.d(LogCatClass, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.d(LogCatClass, "Connection suspended: ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(LogCatClass, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    /*@Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        if (mPermissionDenied)
        {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    *//**
 * Displays a dialog with error message explaining that the location permission is missing.
 *//*
    private void showMissingPermissionError()
    {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }*/

public void mostrarDialogoNombreCliente()
{
    LayoutInflater inflater = getLayoutInflater();
    View alertLayout = inflater.inflate(R.layout.dialog_nombre_cliente, null);
    final TextInputLayout inputLayoutNombreCliente = (TextInputLayout) alertLayout.findViewById(R.id.input_layout_nombre_cliente);
    final EditText editTextNombreCliente = (EditText) alertLayout.findViewById(R.id.edit_text_nombre_cliente);

    //editTextNombreCliente.setText(""+sharedPreferences.getString("nombreCliente").toString());
   // Log.d("DIRECCION", "" + sharedPreferences.getString("nombreCliente").toString());

    final Button botonConfirmarNombreCliente = (Button) alertLayout.findViewById(R.id.btn_confirmar_nombre_cliente);



    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    //alert.setTitle("Nombre Cliente");
    alert.setView(alertLayout);
    alert.setCancelable(false);
    alertDialogNombreCliente = alert.create();

    botonConfirmarNombreCliente.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String nomCliente = editTextNombreCliente.getText().toString();

            if (nomCliente.isEmpty())
            {
                inputLayoutNombreCliente.setError("Debe de digitar su nombre");//cambiar a edittext en register!!
                view.requestFocus();
            }

            else
            {
                //_webServiceEnviarNotificacionPushATodos(sharedPreferences.getString("serialUsuario"));

             /*  mHelper.launchPurchaseFlow(Inicio.this, getSkuPagoSede(), 10001, mPurchaseFinishedListener,
                        getRandomSkuIdPurchaseToGoogle(55));//mypurchasetoken: TOKEN QUE DEBE GENERARSE PARA IDENTIFICAR LA COMPRA EN CASO DE RECLAMO.
*/

                ticket = new Ticket();
                //buyButton.setEnabled(false);

                sharedPreferences.putString("nombreCliente", nomCliente);

                        ticket.setOrderId("orderId");
                        ticket.setPackageName("packageName");
                        ticket.setProductId("productId");
                        ticket.setDeveloperPayload("developerPayload");
                        ticket.setPurchaseToken("purchaseToken");

                        serviceComprarTicket();

                /*  Intent intent = new Intent(Inicio.this, EsperaTurno.class);
                intent.putExtra("turnoCliente","PEPE");
                intent.putExtra("nombreCliente", "PEPE");
                startActivity(intent);*/

                Toast.makeText(getApplicationContext(), "token: "+tokenFCM,Toast.LENGTH_LONG).show();



                editTextNombreCliente.setText(""+sharedPreferences.getString("nombreCliente").toString());

                //dialog.dismiss();
                alertDialogNombreCliente.dismiss();
            }

        }
    });

    alertDialogNombreCliente.show();
}

private void sendRegistrationTokenFCMToServer(final String refreshedToken)
{
    // Add custom implementation, as needed.
    String _urlWebServiceUpdateToken = vars.ipServer.concat("/ws/UpdateTokenFCM");

    Log.e("tokenFCM", ""+refreshedToken);
    Log.e("idDevice", ""+sharedPreferences.getString("deviceID"));
    Log.e("MyToken", ""+sharedPreferences.getString("MyTokenAPI"));

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceUpdateToken, null,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        Boolean status = response.getBoolean("status");
                        String message = response.getString("message");

                        if(status)
                        {

                            Log.e(TAG, "Se registro exitosamente GCM al Server::Inicio Activity");
                            Toast.makeText(Inicio.this, "Se registro exitosamente FCM al Server", Toast.LENGTH_SHORT).show();
                            serviceConsultarDisponibilidadTicket();

                        }

                        else
                        {
                            Log.e(TAG, "Fallo al registrar GCM al Server");
                            Toast.makeText(Inicio.this, "Error, FCM al Server", Toast.LENGTH_SHORT).show();


                        }
                    }
                    catch (JSONException e)
                    {
                        //progressBar.setVisibility(View.GONE);
                        Log.e(TAG, ""+ e.getMessage().toString());
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                }

            })
    {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError
        {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json; charset=utf-8");
            headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
            headers.put("tokenFCM", refreshedToken);
            headers.put("idDevice", sharedPreferences.getString("deviceID"));
            headers.put("MyToken", sharedPreferences.getString("MyTokenAPI"));
            return headers;
        }
    };

    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

}

private void serviceObtenerSedes()//OBTENEMOS LAS SEDES DINAMICAMENTE SEGUN LA UBICACION
{
    _urlWebService = vars.ipServer.concat("/ws/getClientes");

    progressDialog = new ProgressDialog(Inicio.this);
    progressDialog.setIndeterminate(true);
    progressDialog.setMessage("Cargando Sitios FastTrack, espera un momento ...");
    progressDialog.show();
    progressDialog.setCancelable(false);

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
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
                            clientes = response.getJSONArray("result");
                            JSONObject clienteObject;
                            JSONArray sedes;

                            for( int i=0; i <= clientes.length()-1; i++ )
                            {
                                Cliente cliente = new Cliente();

                                clienteObject = clientes.getJSONObject(i);

                                sharedPreferences.putString("MyToken",clienteObject.getString("MyToken"));

                                cliente.setCodCliente(clienteObject.getString("codCliente"));
                                cliente.setNomCliente(clienteObject.getString("nomCliente"));
                                cliente.setDirCliente(clienteObject.getString("dirCliente"));
                                cliente.setTelCliente(clienteObject.getString("telCliente"));
                                cliente.setCorCliente(clienteObject.getString("corCliente"));
                                cliente.setImgCliente(clienteObject.getString("imgCliente"));
                                cliente.setNomEncargado(clienteObject.getString("nomEncargado"));
                                cliente.setValturno(clienteObject.getString("valTurno"));
                                cliente.setIndicaActivo(clienteObject.getString("indicaActivo"));

                                sedes = clienteObject.getJSONArray("sedes");

                                sedesCliente = new ArrayList<Sede>();

                                JSONObject sedeObject;

                                for( int j=0; j <= sedes.length()-1; j++ )
                                {
                                    Sede sede = new Sede();
                                    sede.setImgSede(cliente.getImgCliente());

                                    sedeObject = sedes.getJSONObject(j);
                                    sede.setCodCliente(sedeObject.getString("codCliente"));
                                    sede.setCodSede(sedeObject.getString("codSede"));
                                    sede.setNomSede(sedeObject.getString("nomSede"));
                                    sede.setDirSede(sedeObject.getString("dirSede"));
                                    sede.setTelSede(sedeObject.getString("telSede"));
                                    sede.setCorSede(sedeObject.getString("corSede"));
                                    sede.setLonSede(sedeObject.getString("lonSede"));
                                    sede.setLatSede(sedeObject.getString("latSede"));
                                    //sede.setValorTurno(cliente.getValturno());
                                    sede.setValorTurno(sedeObject.getString("valTurno"));
                                    sede.setSkuPago(sedeObject.getString("skuPago"));
                                    sedesCliente.add(sede);
                                    sedesClienteMostrarSnack.add(sede);
                                }

                                cliente.setSedes(sedesCliente);
                                listaClientes.add(cliente);
                            }

                            //AGREGAMOS LOS MARKERS AL MAP.

                            for( int m=0; m < listaClientes.size(); m++ )
                            {
                                Log.i("Cliente",listaClientes.get(m).getNomCliente());
                                sedesClienteAux = listaClientes.get(m).getSedes();
                                for(int k=0; k<sedesClienteAux.size(); k++)
                                {
                                    markerOptions = new MarkerOptions();
                                    final LatLng latLng = new LatLng(Double.parseDouble(sedesClienteAux.get(k).getLatSede()),
                                            Double.parseDouble(sedesClienteAux.get(k).getLonSede()));
                                    markerOptions.position(latLng);
                                    markerOptions.title(sedesClienteAux.get(k).getNomSede());
                                    markerOptions.snippet(sedesClienteAux.get(k).getDirSede());

                                    try
                                    {
                                        Bitmap bmImg = Ion.with(getApplicationContext())
                                                .load(sedesClienteAux.get(k).getImgSede()).asBitmap().get();
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmImg));
                                    }

                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }

                                    catch (ExecutionException e)
                                    {
                                        e.printStackTrace();
                                    }

                                    mCircle = mGoogleMap.addCircle (new CircleOptions()
                                            .center(latLng)
                                            .radius(radiusInMeters)
                                            .fillColor(shadeColor)
                                            .strokeColor(strokeColor)
                                            .strokeWidth(1));

                                    circulosSedes.add(mCircle);

                                    mGoogleMap.addMarker(markerOptions);

                                }
                            }
                            progressDialog.dismiss();
                        }

                        else

                        {
                            progressDialog.dismiss();
                            Snackbar.make(coordinatorLayoutView, "Error al consultar Clientes, favor consulte al Administrador de FastTrack",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Comprar",null).show();
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
            headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
            return headers;
        }
    };

    ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
}

private void serviceRegistroDispositivo()//REGISTRAMOS EL DEVICE SEGUN SU IMEI Y OTROS DATOS DEL TELEFONO
{
    _urlWebService = vars.ipServer.concat("/ws/registroDispositivo");

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {

                        sharedPreferences.putString("MyTokenAPI",response.getString("MyToken"));

                        Log.i("fabio",""+getDeviceId());
                        Log.i("fabio",""+sharedPreferences.getString("MyTokenAPI"));

                       /* SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        String tokenFCM = pref.getString("regId", null);
*/
                        Log.i("TokenFCM", "Firebase reg id: " + tokenFCM);

                        sendRegistrationTokenFCMToServer(tokenFCM);//REGISTRO POR PRIMERA VEZ TOKEN FCM.

                        enableMyLocation();
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
            headers.put("idDevice",getDeviceId());
            return headers;
        }
    };

    ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
}

private void serviceComprarTicket()//COMPRA DE TICKET
{
    _urlWebService = vars.ipServer.concat("/ws/generarTicket");

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                    /*sharedPreferences.putString("MyTokenAPI",response.getString("MyToken"));

                    Log.i("fabio",""+getDeviceId());
                    Log.i("fabio",""+sharedPreferences.getString("MyTokenAPI"));

                    enableMyLocation();*/

                        if(response.getBoolean("status"))
                        {
                            //consumeItem();
                            Log.i("Inicio","Compra Exitosa!");
                            Toast.makeText(getApplicationContext(), "Compra Exitosa!!!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Turno: "+response.getString("turnoCliente"), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "NomCliente: "+response.getString("nomCliente"), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "token: "+tokenFCM,Toast.LENGTH_LONG).show();

                            //editTextNombreCliente.setText(""+sharedPreferences.getString("nombreCliente").toString());


                            Intent intent = new Intent(Inicio.this, EsperaTurno.class);
                            intent.putExtra("turnoCliente", response.getString("turnoCliente"));
                            intent.putExtra("nombreCliente", response.getString("nomCliente"));
                            startActivity(intent);
                            //finish();
                        }

                        else
                        {
                            Log.i("Inicio","Compra NO Exitosa!");
                            Toast.makeText(getApplicationContext(), "Compra NO Exitosa!", Toast.LENGTH_LONG).show();

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
            headers.put("codSede",getTemporalCodigoSede());
            headers.put("idDevice",getDeviceId());
            headers.put("nomCliente",sharedPreferences.getString("nombreCliente"));
            headers.put("codEstado","1");
            headers.put("valTicket",getTemporalValorTicket());
            headers.put("tokenFCM",tokenFCM);
            //DATOS COMPRA INAPP BILLING GOOGLE
            headers.put("orderId",ticket.getOrderId());
            headers.put("packageName",ticket.getPackageName());
            headers.put("productId", ticket.getProductId());
            headers.put("developerPayload",ticket.getDeveloperPayload());
            headers.put("purchaseToken",ticket.getPurchaseToken());
            headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
            return headers;
        }
    };

    ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                                Intent intent = new Intent(Inicio.this, EsperaTurno.class);
                                intent.putExtra("turnoCliente", turnoCliente);
                                intent.putExtra("nombreCliente", nombreCliente);
                                intent.putExtra("message", desMensaje);
                                intent.putExtra("ticketEsAsumido", ticketEsAsumido);
                                intent.putExtra("codEstado", codEstado);
                                startActivity(intent);
                                Inicio.this.finish();
                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                                builder
                                        .setTitle("TICKET DISPONIBLE")
                                        .setMessage("Vaya! Se ha encontrado un Ticket que debe ser usado, de lo "+
                                                "contrario no podrás comprar más Tickets, Usalo justo ahora!")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                Intent intent = new Intent(Inicio.this, EsperaTurno.class);
                                                intent.putExtra("turnoCliente", turnoCliente);
                                                intent.putExtra("nombreCliente", nombreCliente);
                                                intent.putExtra("message", desMensaje);
                                                intent.putExtra("ticketEsAsumido", ticketEsAsumido);
                                                intent.putExtra("codEstado", codEstado);
                                                startActivity(intent);
                                                Inicio.this.finish();

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


@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
{
    permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
}

@Override
public void onPermissionGranted(@NonNull String[] permissionName)
{
    Log.i("onPermissionGranted", "Permission(s) " + Arrays.toString(permissionName) + " Granted");
    // Enable the my location layer if the permission has been granted.
    if(enableIDDevice())
    {
        serviceRegistroDispositivo();
    }
}

@Override
public void onPermissionDeclined(@NonNull String[] permissionName)
{
    Log.i("onPermissionDeclined", "Permission(s) " + Arrays.toString(permissionName) + " Declined");
}

@Override
public void onPermissionPreGranted(@NonNull String permissionsName)
{
    Log.i("onPermissionPreGranted", "Permission( " + permissionsName + " ) preGranted");
}

@Override
public void onPermissionNeedExplanation(@NonNull String permissionName)
{
    Log.i("NeedExplanation", "Permission( " + permissionName + " ) needs Explanation");
    if (!isSingle)
    {
        neededPermission = PermissionHelper.declinedPermissions(this, MULTI_PERMISSIONS);
        StringBuilder builder = new StringBuilder(neededPermission.length);
        if (neededPermission.length > 0)
        {
            for (String permission : neededPermission)
            {
                builder.append(permission).append("\n");
            }
        }

        android.support.v7.app.AlertDialog alert = getAlertDialog(neededPermission, builder.toString());
        if (!alert.isShowing())
        {
            alert.show();
        }
    }
    else
    {
        getAlertDialog(permissionName).show();
    }
}

@Override public void onPermissionReallyDeclined(@NonNull String permissionName)
{
    //result.setText("Permission " + permissionName + " can only be granted from SettingsScreen");
    Log.i("ReallyDeclined", "Permission " + permissionName + " can only be granted from settingsScreen");
    /** you can call  {@link PermissionHelper#openSettingsScreen(Context)} to open the settings screen */
    getAlertDialog(permissionName).show();
    /*new AlertDialog.Builder(Inicio.this)
            .setMessage(R.string.location_permission_denied)
            .setPositiveButton(android.R.string.ok, null)
            .create();*/
}

@Override
public void onNoPermissionNeeded()
{
    //result.setText("Permission(s) not needed");
    Log.i("onNoPermissionNeeded", "Permission(s) not needed");
}

/* @Override public void onClick(View v) {
    if (v.getId() == R.id.single || v.getId() == R.id.multi) {
        isSingle = v.getId() == R.id.single;
        permissionHelper
                .setForceAccepting(false) // default is false. its here so you know that it exists.
                .request(isSingle ? SINGLE_PERMISSION : MULTI_PERMISSIONS);
    } else {
        permissionHelper
                .request(Manifest.permission.SYSTEM_ALERT_WINDOW);*//*you can pass it along other permissions,
                 just make sure you override OnActivityResult so you can get a callback.
                 ignoring that will result to not be notified if the user enable/disable the permission*//*
    }
}*/

public android.support.v7.app.AlertDialog getAlertDialog(final String[] permissions, final String permissionName)
{
    if (builder == null)
    {
        builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Permission Needs Explanation").setCancelable(false)
                .create();
    }
    builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            permissionHelper.requestAfterExplanation(permissions);
            finish();
        }
    });
    builder.setMessage("Permissions need explanation (" + permissionName + ")");
    return builder;
}

public android.support.v7.app.AlertDialog getAlertDialog(final String permission)
{
    if (builder == null)
    {
        builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Habilitar Permiso").setCancelable(false)
                .create();
    }
    builder.setButton(DialogInterface.BUTTON_POSITIVE, "Entiendo", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            permissionHelper.requestAfterExplanation(permission);
            openSettingsScreen(Inicio.this);
        }
    });

    if(permission.equals("android.permission.ACCESS_FINE_LOCATION"))
    {
        messageAlert = "FastTrack necesita que apruebe el permiso de Geolocalización, a continuación será dirigido a ajustes de Aplicación "
                +"y active el permiso de Ubicación.";
    }

    if(permission.equals("android.permission.READ_PHONE_STATE"))
    {
        messageAlert = "FastTrack necesita que apruebe el permiso de Acceso al télefono, a continuación será dirigido a ajustes de Aplicación "
                +"y active el permiso de Télefono y Almacenamiento.";
    }

    builder.setMessage(messageAlert);
    return builder;
}

public static void openSettingsScreen(@NonNull Context context)
{
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.parse("package:" + context.getPackageName());
    intent.setData(uri);
    context.startActivity(intent);
}
}
