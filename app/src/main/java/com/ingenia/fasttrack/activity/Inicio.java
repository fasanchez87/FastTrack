package com.ingenia.fasttrack.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.iid.InstanceID;
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
import com.ingenia.fasttrack.util.NotificationUtils;
import com.ingenia.fasttrack.vars.vars;
import com.ingenia.fasttrack.volley.ControllerSingleton;

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

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


public class Inicio extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,OnPermissionCallback
{

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String LogCatClass = "Inicio";

    private static final int REQUEST_SCAN = 100;
    private String numberCreditCardCifrada;
    private String numberCreditCardNoCifrado;
    private String MesNumberCreditCard;
    private String AñoNumberCreditCard;
    private String CVVNumberCreditCard;
    private String TipoTarjetaCreditCard;

    private RadioButton radio_x1_fasttrack;
    private RadioButton radio_x2_fasttrack;
    private RadioButton radio_x3_fasttrack;
    private RadioButton radio_x4_fasttrack;
    private RadioButton radio_x5_fasttrack;
    private RadioButton radio_cantidad_ft;

    private boolean checkTerminos;

    Context context;

    RadioGroup opciones_packs_compras;

    private String codPrecio;
    private String valorTicket;

    private SharedPreferences pref;
    private String tokenFCM, desMensaje;
    private boolean ticketEsAsumido = true;

    private ArrayList<Cliente> listaClientes;
    private ArrayList<Sede> sedesCliente;
    private ArrayList<Sede> sedesClienteAux;
    private ArrayList<Sede> sedesClienteMostrarSnack;
    HashMap<String, ArrayList<Sede>> hashTableSedesCliente;
    HashMap<Integer, ArrayList<Sede>> listaSedesCliente;

    private NumberFormat numberFormat;

    private String skuPagoSede;

    private String temporalCodigoSede;

    private String temporalValorTicket;

    private String orderIDComprarGoogle;

    double radiusInMeters = 100.0;
    int strokeColor = Color.rgb(153, 249, 1); //red outline
    int shadeColor = Color.argb(50, 153, 189, 48); //opaque red fill

    private boolean showMessage = false;
    private String codSede="";

    private Boolean mRequestingLocationUpdates;
    private String REQUESTING_LOCATION_UPDATES_KEY;
    private String LOCATION_KEY;
    private String LAST_UPDATED_TIME_STRING_KEY;

    private String _urlWebService;

    public vars vars;

    GoogleApiClient mGoogleApiClient;

    public String currentVersion = null;


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
    //IabHelper mHelper;

    private static final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnñopqrstuvwxyz!#$%&/()=?¡*[]{}-_+";

    private String mLastUpdateTime;

    gestionSharedPreferences sharedPreferences;
    int locationCount = 10;
    PendingIntent pendingIntent;

    public static AlertDialog alertDialogNombreCliente;

    private TelephonyManager telephonyManager;

    private Circle mCircle;
    private ArrayList<Circle> circulosSedes;

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

    Dialog dialog;



    // private final static String SINGLE_PERMISSION = Manifest.permission.GET_ACCOUNTS;

    private final static String[] MULTI_PERMISSIONS = new String[]
            {
                    Manifest.permission.ACCESS_FINE_LOCATION
                    //Manifest.permission.READ_PHONE_STATE
            };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkTerminos=false;

        //getSupportActionBar().hide();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ticketEsAsumido = true;

        context = this;

        opciones_packs_compras = (RadioGroup) findViewById(R.id.opciones_packs_compras);

        updateValuesFromBundle(savedInstanceState);

        pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        tokenFCM = pref.getString("regId", null);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
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

        sedesCliente = new ArrayList<Sede>();
        sedesClienteAux = new ArrayList<Sede>();
        sedesClienteMostrarSnack = new ArrayList<Sede>();
        listaClientes = new ArrayList<Cliente>();
        circulosSedes = new ArrayList<Circle>();

        hashTableSedesCliente = new HashMap<String, ArrayList<Sede>>();
        listaSedesCliente = new HashMap<Integer, ArrayList<Sede>>();

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

       /* String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyvWPPTajqfMy/qf3OJ8eSdzMFyIClqweMY8LqvZne0WxxD4L4nHHfKaWS6OKl7IrAJwCVStCfxvZKv9kRH9MvsBnlrdGLT39veT+QXTVbwtbjGJZgLRGxf3OnYZ6fKXRrFfzAoeoBU6Gg4C1BjSK1Gqc3TMr/C+oOYC38gMEM09qy9SbY8jwrxC39U4yAWDlCjZVoeUo1kS/XX3Hmi7zPLLhOq4a0aFNobp6h1EYq8lS1ue7Tv10eP5JV1VPaTNQc6u+/2mSfDG73XPr/wOKvFmnucAOkZ1o8xFX74R8S1m8Yw13ALiKL4XzfU99Lo+kQFG1lamJzOjLVsVld8TvuQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });*/


        if (!isGooglePlayServicesAvailable()) {
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
        }

        coordinatorLayoutView = findViewById(R.id.coordinator_layout);

        _webServicecheckVersionAppPlayStore();
    }

    public String getCodPrecio() {
        return codPrecio;
    }

    public void setCodPrecio(String codPrecio) {
        this.codPrecio = codPrecio;
    }

    public String getTemporalCodigoSede() {
        return temporalCodigoSede;
    }

    public void setTemporalCodigoSede(String temporalCodigoSede) {
        this.temporalCodigoSede = temporalCodigoSede;
    }

    public String getOrderIDComprarGoogle() {
        return orderIDComprarGoogle;
    }

    public void setOrderIDComprarGoogle(String orderIDComprarGoogle) {
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

    private static String getRandomSkuIdPurchaseToGoogle(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public String getSkuPagoSede() {
        return skuPagoSede;
    }

    public void setSkuPagoSede(String skuPagoSede) {
        this.skuPagoSede = skuPagoSede;
    }

    public View.OnClickListener undoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mostrarDialogoNombreCliente();
        }
    };

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
            permissionHelper.onActivityForResult(requestCode);
        }
    }*/

   /* IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                Log.i(LogCatClass, "Error: " + result.getMessage().toString());
                //Log.i(LogCatClass, "Error: "+purchase.getOriginalJson().toString() );
                // mHelper.queryInventoryAsync(mReceivedInventoryListener);
                Toast.makeText(getApplicationContext(),
                        "Error: Compra NO efectuada: " + result.getMessage().toString(),
                        Toast.LENGTH_LONG)
                        .show();
                return;
            } else if (purchase.getSku().equals(getSkuPagoSede())) {
                Log.i(LogCatClass, "SKU: " + result.getMessage().toString());
                consumeItem();
            }
        }
    };*/

    /*public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }*/

   /* IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
                Log.i(LogCatClass, "ERROR CONSUME: " + result.getMessage().toString());
               *//* mHelper.consumeAsync(inventory.getPurchase(getSkuPagoSede()),
                        mConsumeFinishedListener);
*//*
            } else {
                //NOTIFICAMOS QUE SE CONSUMIO LA COMPRA PARA PODER VOLVER A COMPRAR.
                Log.i(LogCatClass, "CONSUME: " + result.getMessage().toString());

              *//*  Toast.makeText(Inicio.this,
                        getSkuPagoSede(), Toast.LENGTH_SHORT).show();*//*

                mHelper.consumeAsync(inventory.getPurchase(getSkuPagoSede()),
                        mConsumeFinishedListener);
            }
        }
    };*/

    /*IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result)
                {

                    if (result.isSuccess())
                    {
                        //clickButton.setEnabled(true);
                        Log.i(LogCatClass, "Pago Exitoso!!!");
                        ticket = new Ticket();
                        Log.i(LogCatClass, "Pagaste! Salta la fila ahora!!!" + purchase.getOriginalJson().toString());
                        Log.i(LogCatClass, "Datos Compra: " + purchase.getOriginalJson().toString());

                        String datosComparTicketGoogle = purchase.getOriginalJson().toString();

                        if (datosComparTicketGoogle != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(datosComparTicketGoogle);
                                ticket.setOrderId(jsonObj.getString("orderId"));
                                ticket.setPackageName(jsonObj.getString("packageName"));
                                ticket.setProductId(jsonObj.getString("productId"));
                                ticket.setDeveloperPayload(jsonObj.getString("developerPayload"));
                                ticket.setPurchaseToken(jsonObj.getString("purchaseToken"));
                                serviceComprarTicket();
                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Json parsing error: " + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error" + "Sku diferente",
                                Toast.LENGTH_LONG)
                                .show();
                    }


                }
            };*/



    @Override
    public void onDestroy()
    {
        super.onDestroy();
       /* Log.i(LogCatClass,"Destroy: "+mHelper);

        if (mHelper != null)
            mHelper.dispose();
            mHelper = null;*/
    }

    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Su GPS esta apagado, para que Fast Track funcione correctamente debe encenderlo, ¿desea hacerlo?")
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


    public boolean isGooglePlayServicesAvailable()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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
            permissionHelper
                    .setForceAccepting(true) // default is false. its here so you know that it exists.
                    .request(MULTI_PERMISSIONS);
        }

        else
        {
            serviceRegistroDispositivo();
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
                snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(153,189,43)), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append(".");
                snackbarText.append(" Evita la Fila pagando ");
                int boldStart2 = snackbarText.length();
                snackbarText.append("$"+ValorTicket);
                snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(153,189,43)), boldStart2, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart2, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append(".");

                multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, snackbarText, Snackbar.LENGTH_INDEFINITE);
                multilineSnackbar.setAction("COMPRAR", undoOnClickListener);
                multilineSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
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
                snackbarText.append("Fast Track ");
                snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(153,189,43)), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                snackbarText.append("en el mapa, y evita la fila.");

                multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, snackbarText, Snackbar.LENGTH_INDEFINITE);
                multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                multilineSnackbar.show();
                showMessage = false;
            }
        }
    }

    boolean encontrado;
    boolean initMap=false;
    float distanciaMinima=150;
    boolean seleccionManual=false;
    private double constanteDistancia= 150.0;
    String nameSede;

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(LogCatClass, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        if(!initMap)
        {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            initMap=true;
        }

        Location locationA = new Location("actual");
        locationA.setLatitude(mCurrentLocation.getLatitude());
        locationA.setLongitude(mCurrentLocation.getLongitude());

        mRequestingLocationUpdates = true;

        float[] distance = new float[2];

        encontrado = false;

        for (int k = 0; k < circulosSedes.size(); k++)
        {
            Location locationB = new Location("index");
            locationB.setLatitude(circulosSedes.get(k).getCenter().latitude);
            locationB.setLongitude(circulosSedes.get(k).getCenter().longitude);

            double metros = locationA.distanceTo(locationB);

            if( metros <= constanteDistancia )//ADENTRO
            {
                encontrado = true;

                constanteDistancia = metros;

                if(!this.codSede.equals(sedesClienteMostrarSnack.get(k).getCodSede())&&!seleccionManual)
                {
                    showMessage=false;
                    setUpShowMessage(true, sedesClienteMostrarSnack.get(k).getNomSede().toString(),
                            numberFormat.format(Double.parseDouble(sedesClienteMostrarSnack.get(k).getValPrecio().toString())));
                    this.codSede=sedesClienteMostrarSnack.get(k).getCodSede();
                    setCodPrecio(sedesClienteMostrarSnack.get(k).getCodPrecio().toString());

                    setTemporalCodigoSede(sedesClienteMostrarSnack.get(k).getCodSede().toString());
                    //setTemporalValorTicket(sedesClienteMostrarSnack.get(k).getValorTurno().toString());
                    setTemporalValorTicket(sedesClienteMostrarSnack.get(k).getValPrecio().toString());

                    Log.d("Fastracker", "Codigo Sede: "+sedesClienteMostrarSnack.get(k).getCodSede()+ "- Nombre Sede: "+sedesClienteMostrarSnack.get(k).getNomSede());
                }
            }
        }

        if( !encontrado )
        {
            setUpShowMessage( false, "", "" );
        }
    }

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

    public static int compareVersions(String version1, String version2)//COMPARAR VERSIONES
    {
        String[] levels1 = version1.split("\\.");
        String[] levels2 = version2.split("\\.");

        int length = Math.max(levels1.length, levels2.length);
        for (int i = 0; i < length; i++){
            Integer v1 = i < levels1.length ? Integer.parseInt(levels1[i]) : 0;
            Integer v2 = i < levels2.length ? Integer.parseInt(levels2[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0){
                return compare;
            }
        }
        return 0;
    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public String getCantidadTicket() {
        return cantidadTicket;
    }

    public void setCantidadTicket(String cantidadTicket) {
        this.cantidadTicket = cantidadTicket;
    }

    private String cantidadTicket;

    public void mostrarDialogoNombreCliente()
    {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.dialog_nombre_cliente, null);
        final TextInputLayout inputLayoutNombreCliente = (TextInputLayout) alertLayout.findViewById(R.id.input_layout_nombre_cliente);
        final EditText editTextNombreCliente = (EditText) alertLayout.findViewById(R.id.edit_text_nombre_cliente);
        opciones_packs_compras = (RadioGroup) alertLayout.findViewById(R.id.opciones_packs_compras);

        /*editTextNombreCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("nombreClienteFT"))?null:
                sharedPreferences.getString("nombreClienteFT"));
        // Log.d("DIRECCION", "" + sharedPreferences.getString("nombreCliente").toString());
*/
        final Button botonConfirmarNombreCliente = (Button) alertLayout.findViewById(R.id.btn_confirmar_nombre_cliente);
        final Button botonCancelarNombreCliente = (Button) alertLayout.findViewById(R.id.btn_cancelar_nombre_cliente);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle("ESCRIBA SU NOMBRE COMPLETO");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alertDialogNombreCliente = alert.create();

        botonConfirmarNombreCliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //String nomCliente = editTextNombreCliente.getText().toString();
                // get selected radio button from radioGroup

                int selectedId = opciones_packs_compras.getCheckedRadioButtonId();

                radio_cantidad_ft = (RadioButton) alertLayout.findViewById(selectedId);

                if(radio_cantidad_ft.getText().equals("x1 Fast Track"))
                {
                    //VALOR
                    valorTicket=""+((Integer.parseInt(getTemporalValorTicket()))*(1));
                    setCantidadTicket("1");
                    Log.i("valorTicket",""+valorTicket);


                }

                if(radio_cantidad_ft.getText().equals("x2 Fast Track"))
                {
                    //VALOR
                    valorTicket=""+((Integer.parseInt(getTemporalValorTicket()))*(2));
                    setCantidadTicket("2");
                    Log.i("valorTicket",""+valorTicket);

                }

                if(radio_cantidad_ft.getText().equals("x3 Fast Track"))
                {
                    //VALOR
                    valorTicket=""+((Integer.parseInt(getTemporalValorTicket()))*(3));
                    setCantidadTicket("3");
                    Log.i("valorTicket",""+valorTicket);

                }

                if(radio_cantidad_ft.getText().equals("x4 Fast Track"))
                {
                    //VALOR
                    valorTicket=""+((Integer.parseInt(getTemporalValorTicket()))*(4));
                    setCantidadTicket("4");
                    Log.i("valorTicket",""+valorTicket);

                }

                if(radio_cantidad_ft.getText().equals("x5 Fast Track"))
                {
                    //VALOR
                    valorTicket=""+((Integer.parseInt(getTemporalValorTicket()))*(5));
                    setCantidadTicket("5");
                    Log.i("valorTicket",""+valorTicket);

                }

          /*  Toast.makeText(Inicio.this,
                    valorTicket, Toast.LENGTH_SHORT).show();*/

                setSkuPagoSede("sku.pago.produccion."+valorTicket); //ASIGNAMOS DESDE EL APP EL SKU DE PAGO.

          /*  Toast.makeText(Inicio.this,
                    getSkuPagoSede(), Toast.LENGTH_SHORT).show();*/

               /* if (nomCliente.isEmpty())
                {
                    inputLayoutNombreCliente.setError("Debe de digitar su nombre");//cambiar a edittext en register!!
                    view.requestFocus();
                }

                else
                {*/
                //_webServiceEnviarNotificacionPushATodos(sharedPreferences.getString("serialUsuario"));


              /* mHelper.launchPurchaseFlow(Inicio.this, getSkuPagoSede(), 10001, mPurchaseFinishedListener,
                        getRandomSkuIdPurchaseToGoogle(55));//mypurchasetoken: TOKEN QUE DEBE GENERARSE PARA IDENTIFICAR LA COMPRA EN CASO DE RECLAMO.

*//*
                Toast.makeText(Inicio.this,
                        "Valor ticket unitario: "+valorTicket+" - Sku valor: "+getSkuPagoSede(), Toast.LENGTH_SHORT).show();

                Toast.makeText(Inicio.this,
                        "Codigo Precio: "+getCodPrecio()+" - # Tickets: "+getCantidadTicket(), Toast.LENGTH_SHORT).show();
*/

                    /*mHelper.launchPurchaseFlow(Inicio.this,getSkuPagoSede(), 10001, mPurchaseFinishedListener,
                            getRandomSkuIdPurchaseToGoogle(55));//mypurchasetoken: TOKEN QUE DEBE GENERARSE PARA IDENTIFICAR LA COMPRA EN CASO DE RECLAMO.
*/

          /*    ////////////////saltarse el pago//////////////////////
                ticket = new Ticket();
                //buyButton.setEnabled(false);


                        ticket.setOrderId("orderId");
                        ticket.setPackageName("packageName");
                        ticket.setProductId("productId");
                        ticket.setDeveloperPayload("developerPayload");
                        ticket.setPurchaseToken("purchaseToken");
                        serviceComprarTicket();
              //saltarse el pago*/


               /* valorTicket=""+((Integer.parseInt(getTemporalValorTicket()))*(5));
                setCantidadTicket("5");*/






                //sharedPreferences.putString("nombreClienteFT", nomCliente);
                alertDialogNombreCliente.dismiss();

                mostrarDialogoCompraCliente();
            }

            //}
        });


        botonCancelarNombreCliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialogNombreCliente.dismiss();
                finish();
                startActivity(getIntent());
            }
        });

        alertDialogNombreCliente.show();
    }

    public void checkEventTerminos(View v)
    {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.dialog_comprar, null);

        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked())
        {
            checkTerminos=true;
        }
        else
        {
            checkTerminos=false;
        }
    }


    public void mostrarDialogoCompraCliente()
    {

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.dialog_comprar, null);

        Spanned Text;

        Text = Html.fromHtml("Click para ver terminos y condiciones. <br />" +
                "<a href='http://ingeniapps.com.co///'>Acepto los términos y condiciones.</a>");

        final TextInputLayout inputLayoutNombreCliente = (TextInputLayout) alertLayout.findViewById(R.id.input_layout_nombre_cliente);

        //DATOS PAGO TARJETA CREDITO CLIENTE
        final EditText editTextNombreCliente = (EditText) alertLayout.findViewById(R.id.edit_text_nombre_cliente);
        final EditText editTextApellidoCliente = (EditText) alertLayout.findViewById(R.id.edit_text_apellido_cliente);
        final EditText editTextNumeroTarjetaCliente = (EditText) alertLayout.findViewById(R.id.edit_text_numero_tarjeta);
        final EditText editTextMesFechaVencimientoCliente = (EditText) alertLayout.findViewById(R.id.edit_text_mes_tarjeta_credito_registro);
        final EditText editTextAñoFechaVencimientoCliente = (EditText) alertLayout.findViewById(R.id.edit_text_año_tarjeta_credito_registro);
        final EditText editTextVCCCliente = (EditText) alertLayout.findViewById(R.id.edit_text_cvv_tarjeta_credito_registro);
        final EditText editTextCedulaCliente = (EditText) alertLayout.findViewById(R.id.edit_text_cedula_cliente);
        final EditText editTextEmailCliente = (EditText) alertLayout.findViewById(R.id.edit_text_layout_email_cliente);
        final EditText editTextTelefonoCliente = (EditText) alertLayout.findViewById(R.id.edit_text_layout_telefono_cliente);
        final ImageButton botonEscanearTarjeta = (ImageButton) alertLayout.findViewById(R.id.imageButtonScanTarjeta);
        final Button botonCancelarNombreCliente = (Button) alertLayout.findViewById(R.id.btn_cancelar_nombre_cliente);
        final Button btn_confirmar_pago_cliente = (Button) alertLayout.findViewById(R.id.btn_confirmar_pago_cliente);

        //TERMINOS Y CONDICIONES
        final TextView textoTerminos = (TextView) alertLayout.findViewById(R.id.editTextTerminos);
        textoTerminos.setMovementMethod(LinkMovementMethod.getInstance());
        textoTerminos.setText(Text);


        //RECORDAR DATOS TARJETA DE CREDITO
        editTextNombreCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("nombreClienteFT"))?null:
                sharedPreferences.getString("nombreClienteFT"));
        editTextApellidoCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("apellidoClienteFT"))?null:
                sharedPreferences.getString("apellidoClienteFT"));


        editTextNumeroTarjetaCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("numeroTarjetaClienteSinCifrarFT"))?null:
                sharedPreferences.getString("numeroTarjetaClienteSinCifrarFT"));
        editTextMesFechaVencimientoCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("mesTarjetaClienteFT"))?null:
                sharedPreferences.getString("mesTarjetaClienteFT"));
        editTextAñoFechaVencimientoCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("añoTarjetaClienteFT"))?null:
                sharedPreferences.getString("añoTarjetaClienteFT"));
        /*editTextVCCCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("CVVClienteFT"))?null:
                sharedPreferences.getString("CVVClienteFT"));*/


        editTextCedulaCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("cedulaClienteFT"))?null:
                sharedPreferences.getString("cedulaClienteFT"));
        editTextEmailCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("emailClienteFT"))?null:
                sharedPreferences.getString("emailClienteFT"));
        editTextTelefonoCliente.setText(TextUtils.isEmpty(sharedPreferences.getString("telefonoClienteFT"))?null:
                sharedPreferences.getString("telefonoClienteFT"));

       /* editTextNumeroTarjetaCliente.setText(TextUtils.isEmpty(numberCreditCardCifrada)?null:
                numberCreditCardCifrada);

        editTextMesFechaVencimientoCliente.setText(TextUtils.isEmpty(MesNumberCreditCard)?null:
                MesNumberCreditCard);

        editTextAñoFechaVencimientoCliente.setText(TextUtils.isEmpty(AñoNumberCreditCard)?null:
                AñoNumberCreditCard);

        editTextVCCCliente.setText(TextUtils.isEmpty(CVVNumberCreditCard)?null:
                CVVNumberCreditCard);*/

        editTextVCCCliente.setText(TextUtils.isEmpty(CVVNumberCreditCard)?null:
                CVVNumberCreditCard);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle("ESCRIBA SU NOMBRE COMPLETO");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alertDialogNombreCliente = alert.create();

        botonEscanearTarjeta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent scanIntent = new Intent(Inicio.this, CardIOActivity.class);
                //OBTENEMOS ESTOS DATOS PARA RECORDARLO DE VUELTA DEL ESCANER DE TARJETA
                String nomCliente=editTextNombreCliente.getText().toString();
                String apeCliente=editTextApellidoCliente.getText().toString();
                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                //scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                startActivityForResult(scanIntent, REQUEST_SCAN);
                setSkuPagoSede("sku.pago.produccion."+valorTicket); //ASIGNAMOS DESDE EL APP EL SKU DE PAGO.
                //RECORDAMOS ESTOS DATOS APENAS VUELVA DEL ESCANER
                sharedPreferences.putString("nombreClienteFT", nomCliente);
                sharedPreferences.putString("apellidoClienteFT", apeCliente);
                alertDialogNombreCliente.dismiss();
            }
        });

        btn_confirmar_pago_cliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String nomCliente=editTextNombreCliente.getText().toString();
                String apeCliente=editTextApellidoCliente.getText().toString();

                String tarjetaCreditoCliente=editTextNumeroTarjetaCliente.getText().toString();
                String tarjetaMesCreditoCliente=editTextMesFechaVencimientoCliente.getText().toString();
                String tarjetaAñoCreditoCliente=editTextAñoFechaVencimientoCliente.getText().toString();
                CVVNumberCreditCard=editTextVCCCliente.getText().toString();

                String cedulaCliente=editTextCedulaCliente.getText().toString();
                String mailCliente=editTextEmailCliente.getText().toString();
                String telefonoCliente=editTextTelefonoCliente.getText().toString();

                if (TextUtils.isEmpty(nomCliente))
                {
                    editTextNombreCliente.setError("Digite su nombre");
                    view.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(apeCliente))
                {
                    editTextApellidoCliente.setError("Digite su apellido");
                    view.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(tarjetaCreditoCliente))
                {
                    editTextNumeroTarjetaCliente.setError("Digite número tarjeta");
                    view.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(tarjetaMesCreditoCliente))
                {
                    editTextMesFechaVencimientoCliente.setError("Digite mes tarjeta");
                    view.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(tarjetaAñoCreditoCliente))
                {
                    editTextAñoFechaVencimientoCliente.setError("Digite año tarjeta");
                    view.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(CVVNumberCreditCard))
                {
                    editTextVCCCliente.setError("Digite VCC tarjeta");
                    view.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(cedulaCliente))
                {
                    editTextCedulaCliente.setError("Digite cedula");
                    view.requestFocus();
                    return;
                }

                if(!isValidEmail(mailCliente))
                {
                    editTextEmailCliente.setError("Digite email valido");
                    view.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(telefonoCliente))
                {
                    editTextTelefonoCliente.setError("Digite télefono");
                    view.requestFocus();
                    return;
                }

                if(!checkTerminos)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                    builder
                            .setTitle("TERMINOS Y CONDICIONES")
                            .setMessage("Debe aceptar los términos y condiciones para concretar su compra.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {

                                }
                            }).show();
                    return;
                }




                sharedPreferences.putString("nombreClienteFT", nomCliente);
                sharedPreferences.putString("apellidoClienteFT", apeCliente);
                sharedPreferences.putString("numeroTarjetaClienteSinCifrarFT",tarjetaCreditoCliente );
                sharedPreferences.putString("mesTarjetaClienteFT", tarjetaMesCreditoCliente);
                sharedPreferences.putString("añoTarjetaClienteFT", tarjetaAñoCreditoCliente);
                //sharedPreferences.putString("CVVClienteFT", vccCliente);
                sharedPreferences.putString("cedulaClienteFT", cedulaCliente);
                sharedPreferences.putString("emailClienteFT", mailCliente);
                sharedPreferences.putString("telefonoClienteFT", telefonoCliente);




                //SI TODO SE VALIDA CORRECTAMENTE INVOCAMOS EL WS DE PAGO EPAYCO
                AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                builder
                        .setTitle("CONFIRMACIÓN COMPRA")
                        .setMessage("Deseo confirmar la compra de ("+getCantidadTicket()+")"+" tickets"+" por el valor de $"+numberFormat.format(Double.parseDouble(valorTicket))+" pesos.")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                            }
                        }).setNegativeButton("Confirmar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        serviceComprarTicket();
                    }
                }).setCancelable(false).show();
            }
            //}
        });

        botonCancelarNombreCliente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialogNombreCliente.dismiss();
                finish();
                startActivity(getIntent());
            }
        });

        alertDialogNombreCliente.show();
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public final static boolean isValidEmail(CharSequence target)
    {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCAN)
        {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))
            {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                numberCreditCardCifrada=scanResult.getRedactedCardNumber();
                numberCreditCardNoCifrado=scanResult.getFormattedCardNumber();
                MesNumberCreditCard=""+scanResult.expiryMonth;
                MesNumberCreditCard= Integer.parseInt(MesNumberCreditCard)<10?"0"+(MesNumberCreditCard):MesNumberCreditCard;
                AñoNumberCreditCard=""+scanResult.expiryYear;
                AñoNumberCreditCard=AñoNumberCreditCard.substring(2);
                CVVNumberCreditCard=""+scanResult.cvv;
                TipoTarjetaCreditCard=""+scanResult.getCardType();



                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid())
                {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null)
                {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null)
                {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);


            if(!(numberCreditCardNoCifrado==null))
            {
                sharedPreferences.putString("numeroTarjetaClienteSinCifrarFT",numberCreditCardNoCifrado );
                Log.i("tarjeta",sharedPreferences.getString("numeroTarjetaClienteSinCifrarFT")+" : "+MesNumberCreditCard+" : "+AñoNumberCreditCard);
            }

            if(!(MesNumberCreditCard == null))
            {
                sharedPreferences.putString("mesTarjetaClienteFT", MesNumberCreditCard);
            }

            if(!(AñoNumberCreditCard == null))
            {
                sharedPreferences.putString("añoTarjetaClienteFT", AñoNumberCreditCard);
            }

            mostrarDialogoCompraCliente();
        }
        // else handle other activity results
    }

    private void sendRegistrationTokenFCMToServer(final String refreshedToken)
    {
        // Add custom implementation, as needed.
        String _urlWebServiceUpdateToken = vars.ipServer.concat("/ws/UpdateTokenFCM");

        Log.e("tokenFCM", ""+refreshedToken);
        Log.e("idDevice", ""+InstanceID.getInstance(getApplicationContext()).getId());
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
                                //Toast.makeText(Inicio.this, "Se registro exitosamente FCM al Server", Toast.LENGTH_SHORT).show();
                                serviceConsultarDisponibilidadTicket();
                            }

                            else
                            {
                                Log.e(TAG, "Fallo al registrar GCM al Server");
                                //Toast.makeText(Inicio.this, "Error, FCM al Server", Toast.LENGTH_SHORT).show();
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
                headers.put("idDevice", InstanceID.getInstance(getApplicationContext()).getId());
                headers.put("MyToken", sharedPreferences.getString("MyTokenAPI"));
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    }

    HashMap <String, Sede> mapper;

    private void serviceObtenerSedes()//OBTENEMOS LAS SEDES DINAMICAMENTE SEGUN LA UBICACION
    {
        _urlWebService = vars.ipServer.concat("/ws/getClientes");

        progressDialog = new ProgressDialog(Inicio.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando Sitios Fast Track, espera un momento ...");
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
                                        //DATOS PARA PAGOS MULTIPLES***********************
                                        sede.setCodPrecio(sedeObject.getString("codPrecio"));
                                        sede.setValPrecio(sedeObject.getString("valPrecio"));
                                        sede.setSkuPago(sedeObject.getString("skuPrecio"));

                                        sedesCliente.add(sede);
                                        sedesClienteMostrarSnack.add(sede);
                                    }

                                    cliente.setSedes(sedesCliente);
                                    listaClientes.add(cliente);
                                }

                                //AGREGAMOS LOS MARKERS AL MAP
                                mapper = new HashMap<String, Sede>();

                                for( int  m=0; m < listaClientes.size(); m++ )
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
                                        markerOptions.snippet(sedesClienteAux.get(k).getValPrecio());

                                        //OBTENEMOS LOS DATOS DE ESA SEDE PARA EL EVENTO DEL CLICK
                                        Sede sede = new Sede();
                                        sede.setCodCliente(sedesClienteAux.get(k).getCodCliente());
                                        sede.setCodSede(sedesClienteAux.get(k).getCodSede());
                                        sede.setNomSede(sedesClienteAux.get(k).getNomSede());
                                        sede.setDirSede(sedesClienteAux.get(k).getDirSede());
                                        sede.setTelSede(sedesClienteAux.get(k).getTelSede());
                                        sede.setCorSede(sedesClienteAux.get(k).getCorSede());
                                        sede.setLonSede(sedesClienteAux.get(k).getLonSede());
                                        sede.setLatSede(sedesClienteAux.get(k).getLatSede());
                                        //sede.setValorTurno(cliente.getValturno());
                                        sede.setValorTurno(sedesClienteAux.get(k).getValTurno());
                                        sede.setSkuPago(sedesClienteAux.get(k).getSkuPago());
                                        //DATOS PARA PAGOS MULTIPLES***********************
                                        sede.setCodPrecio(sedesClienteAux.get(k).getCodPrecio());
                                        sede.setValPrecio(sedesClienteAux.get(k).getValPrecio());
                                        mapper.put(sedesClienteAux.get(k).getNomSede(),sede);

                                      /*  encontrado = false;
                                        setUpShowMessage(false, sedesClienteAux.get(k).getNomSede().toString(),
                                                numberFormat.format(Double.parseDouble(sedesClienteAux.get(k).getValPrecio().toString())));
*/

                                        //PUESTA DE ICONOS AL MARKER DESDE LA WEB DINAMICAMENTE
                                   /* try
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
                                    }*/
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map));

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

                                //EVENTO CLICK MARKER
                                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                                {
                                    @Override
                                    public boolean onMarkerClick(Marker arg0)
                                    {
                                        encontrado = true;
                                        seleccionManual=true;
                                        // setUpShowMessage(true, arg0.getTitle(),"xxxxx");


                                        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
                                        snackbarText.append("Justo ahora estás en ");
                                        int boldStart = snackbarText.length();
                                        snackbarText.append(arg0.getTitle());
                                        snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(153,189,43)), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        snackbarText.append(".");
                                        snackbarText.append(" Evita la Fila pagando ");
                                        int boldStart2 = snackbarText.length();
                                        snackbarText.append("$"+numberFormat.format(Double.parseDouble(arg0.getSnippet().toString())));
                                        snackbarText.setSpan(new ForegroundColorSpan(Color.rgb(153,189,43)), boldStart2, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        snackbarText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart2, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        snackbarText.append(".");

                                        multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, snackbarText, Snackbar.LENGTH_INDEFINITE);
                                        multilineSnackbar.setAction("COMPRAR", undoOnClickListener);
                                        multilineSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                                        multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                        multilineSnackbar.show();

                                                /*setCodPrecio(sedesClienteAux.get(k).getCodPrecio().toString());

                                                setTemporalCodigoSede(sedesClienteAux.get(k).getCodSede().toString());
                                                //setTemporalValorTicket(sedesClienteMostrarSnack.get(k).getValorTurno().toString());
                                                setTemporalValorTicket(sedesClienteAux.get(k).getValPrecio().toString());*/

                                                /*Toast.makeText(Inicio.this,""+sedesClienteAux.get(k).getCodSede().toString(),Toast.LENGTH_LONG).show();
                                                Toast.makeText(Inicio.this,""+sedesClienteAux.get(k).getValPrecio().toString(),Toast.LENGTH_LONG).show();
*/
                                        Sede sede=(Sede)mapper.get(arg0.getTitle());
                                        Toast.makeText(getApplicationContext(),
                                                ""+sede.getNomSede().toString(), Toast.LENGTH_SHORT).show();


                                        setCodPrecio(sede.getCodPrecio().toString());

                                        setTemporalCodigoSede(sede.getCodSede().toString());
                                        //setTemporalValorTicket(sedesClienteMostrarSnack.get(k).getValorTurno().toString());
                                        setTemporalValorTicket(sede.getValPrecio().toString());


                                        return true;
                                    }

                                });

                                progressDialog.dismiss();


                            }

                            else

                            {
                                progressDialog.dismiss();
                                Snackbar.make(coordinatorLayoutView, "Error al consultar Clientes, favor consulte al Administrador de Fast Track",
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                headers.put("idDevice", InstanceID.getInstance(getApplicationContext()).getId());
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void serviceComprarTicket()//COMPRA DE TICKET
    {
        _urlWebService = vars.ipServer.concat("/ws/generarTicket");
        final String[] message = new String[1];

        final String numberTarjeta=sharedPreferences.getString("numeroTarjetaClienteSinCifrarFT").replace(" ","");

        progressDialog = new ProgressDialog(Inicio.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Estamos gestionando su pago, por favor espera un momento ...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {

                            message[0] =""+response.getString("message");

                            Log.i("Datos TC: ","-----------------");
                            Log.i("Datos TC: ","-----------------"+response.toString());
                            Log.i("Datos TC: ","number: "+numberTarjeta);
                            Log.i("Datos TC: ","fecha: "+sharedPreferences.getString("mesTarjetaClienteFT")+"/"+sharedPreferences.getString("añoTarjetaClienteFT"));
                            Log.i("Datos TC: ","cvc: "+CVVNumberCreditCard);
                            Log.i("Datos TC: ","name: "+sharedPreferences.getString("nombreClienteFT"));
                            Log.i("Datos TC: ","apellidos: "+sharedPreferences.getString("apellidoClienteFT"));
                            Log.i("Datos TC: ","email: "+sharedPreferences.getString("emailClienteFT"));
                            Log.i("Datos TC: ","phone: "+sharedPreferences.getString("telefonoClienteFT"));
                            Log.i("Datos TC: ","docnumber: "+sharedPreferences.getString("cedulaClienteFT"));
                            Log.i("Datos TC: : ","codPrecio: "+getCodPrecio());
                            Log.i("Datos TC: : ","numTickets: "+getCantidadTicket());
                            Log.i("Datos TC: : ","valTicket: "+valorTicket);

                            Log.i("Datos TC: ","-----------------");
                            Log.i("Datos TC: : ","turnoCliente: "+response.getString("turnoCliente"));
                            Log.i("Datos TC: : ","nombreCliente: "+response.getString("nomCliente"));




                            if(response.getBoolean("status"))
                            {

                                alertDialogNombreCliente.dismiss();
                                progressDialog.dismiss();



                                Intent intent = new Intent(Inicio.this, EsperaTurno.class);
                                intent.putExtra("turnoCliente", response.getString("turnoCliente"));
                                intent.putExtra("nombreCliente", response.getString("nomCliente"));
                                //intent.putExtra("orderId", ticket.getOrderId());
                                //DATOS COMPRAS
                                intent.putExtra("codPrecio", getCodPrecio());
                                intent.putExtra("numTickets", getCantidadTicket());
                                startActivity(intent);
                                //finish();

                            }

                            else
                            {
                                progressDialog.dismiss();
                                //Log.i("Inicio","Compra NO Exitosa!");
                                //Toast.makeText(getApplicationContext(), "Compra NO Exitosa!", Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                                builder
                                        .setTitle("ESTADO TRANSACCIÓN")
                                        .setMessage(response.getString("message"))
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                            }
                                        }).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();

                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                            builder
                                    .setTitle("ESTADO TRANSACCIÓN")
                                    .setMessage(""+ message[0])
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {

                                        }
                                    }).show();

                            Log.i("Inicio","Compra NO Exitosa! "+e.getMessage());
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                headers.put("codSede",getTemporalCodigoSede());//ok
                headers.put("idDevice",InstanceID.getInstance(getApplicationContext()).getId());//ok
                headers.put("nomCliente",sharedPreferences.getString("nombreClienteFT"));//ok
                headers.put("codEstado","1");//pend
                headers.put("valTicket",valorTicket);//ok
                headers.put("codPrecio",getCodPrecio());//ok
                headers.put("cantidadTicket",cantidadTicket);//ok
                headers.put("tokenFCM",tokenFCM);//ok
                //DATOS COMPRA INAPP BILLING GOOGLE
               /* headers.put("orderId",ticket.getOrderId());
                headers.put("packageName",ticket.getPackageName());
                headers.put("productId", ticket.getProductId());
                headers.put("developerPayload",ticket.getDeveloperPayload());
                headers.put("purchaseToken",ticket.getPurchaseToken());*/
                headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
                //DATOS TARJETA DE CREDITO PAGO EPAYCO
                headers.put("number",numberTarjeta);
                headers.put("fecha",sharedPreferences.getString("mesTarjetaClienteFT")+"/"+sharedPreferences.getString("añoTarjetaClienteFT"));
                headers.put("cvc",CVVNumberCreditCard);
                headers.put("name",sharedPreferences.getString("nombreClienteFT"));
                headers.put("apellidos",sharedPreferences.getString("apellidoClienteFT"));
                headers.put("email",sharedPreferences.getString("emailClienteFT"));
                headers.put("phone",sharedPreferences.getString("telefonoClienteFT"));
                headers.put("docnumber",sharedPreferences.getString("cedulaClienteFT"));


                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void _webServicecheckVersionAppPlayStore()
    {
        _urlWebService = "http://carreto.pt/tools/android-store-version/?package=com.ingenia.fasttrack";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            boolean status = response.getBoolean("status");

                            if(status)
                            {
                                if(compareVersions(currentVersion,response.getString("version")) == -1)
                                {
                                    if(!((Activity) context).isFinishing())
                                    {
                                        //show dialog
                                        dialog = new Dialog(Inicio.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setCancelable(false);
                                        dialog.setContentView(R.layout.custom_dialog);

                                        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
                                        //text.setText(msg);

                                        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
                                        dialogButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("market://details?id=com.ingenia.fasttrack"));
                                                startActivity(intent);
                                            }
                                        });

                                        dialog.show();
                                    }
                                }
                            }
                        }

                        catch (JSONException e)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                            builder
                                    .setTitle("ERROR")
                                    .setMessage("Error consultando versiones en Play Store, contacte al admin de Beya.")
                                    .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {

                                        }
                                    }).setCancelable(true).show();

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
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void serviceConsultarDisponibilidadTicket()//REVISAMOS SI EXISTE UN TICKET DISPONIBLE
    {
        String _urlWebServiceConsultarDisponibilidadTicket = vars.ipServer.concat("/ws/disponibilidadTicket");

        final String TAG = "serviceConsultarDisponibilidadTicket";

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
                                final String turnoCliente, nombreCliente, codEstado, numTickets;

                                turnoCliente = ticket.getString("codTicket");
                                nombreCliente = ticket.getString("nomCliente");
                                desMensaje = ticket.getString("desMensaje");
                                codEstado = ticket.getString("codEstado");
                                numTickets = ticket.getString("numCantidad");

                                Log.i("numTickets",""+numTickets);

                                if(TextUtils.isEmpty(desMensaje))//SI desMensaje ES NULL ES PORQUE NO SE HA ASUMIDO.
                                {
                               /* ticketEsAsumido = false;
                                Intent intent = new Intent(Inicio.this, EsperaTurno.class);
                                intent.putExtra("turnoCliente", turnoCliente);
                                intent.putExtra("nombreCliente", nombreCliente);
                                intent.putExtra("message", desMensaje);
                                intent.putExtra("ticketEsAsumido", ticketEsAsumido);
                                intent.putExtra("codEstado", codEstado);
                                startActivity(intent);
                                Inicio.this.finish();*/

                                    AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                                    builder
                                            .setTitle("TICKET PENDIENTE")
                                            .setMessage("Vaya! Se ha encontrado un Ticket que esta pendiente por atender, favor atento al llamado.")
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
                                                    intent.putExtra("numTickets", numTickets);
                                                    startActivity(intent);
                                                    Inicio.this.finish();

                                                }
                                            }).setCancelable(false).show();
                                }

                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                                    builder
                                            .setTitle("TICKET EN USO")
                                            .setMessage("¡Vaya! Se ha encontrado un Ticket en uso, proceda a consumirlo.")
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
                                                    intent.putExtra("numTickets", numTickets);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios."+error.getMessage().toString())
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
                headers.put("idDevice",InstanceID.getInstance(getApplicationContext()).getId());
                headers.put("MyToken",sharedPreferences.getString("MyTokenAPI"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        // Enable the my location layer if the permission has been granted
        serviceRegistroDispositivo();
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
            messageAlert = "Fast Track necesita que apruebe el permiso de Geolocalización, a continuación será dirigido a ajustes de Aplicación "
                    +"y active el permiso de Ubicación.";
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
