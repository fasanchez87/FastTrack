package com.ingenia.fasttrack.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.ingenia.fasttrack.R;
import com.ingenia.fasttrack.SnackBar.MultilineSnackbar;
import com.ingenia.fasttrack.beans.Cliente;
import com.ingenia.fasttrack.beans.Sede;
import com.ingenia.fasttrack.permisions.PermissionUtils;
import com.ingenia.fasttrack.sharedPreferences.gestionSharedPreferences;
import com.ingenia.fasttrack.util.IabHelper;
import com.ingenia.fasttrack.util.IabResult;
import com.ingenia.fasttrack.util.Inventory;
import com.ingenia.fasttrack.util.Purchase;
import com.ingenia.fasttrack.vars.vars;
import com.ingenia.fasttrack.volley.ControllerSingleton;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class Inicio extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    String LogCatClass = "Inicio";

    private ArrayList <Cliente> listaClientes;
    private ArrayList <Sede> sedesCliente;
    private ArrayList <Sede> sedesClienteAux;
    private ArrayList <Sede> sedesClienteMostrarSnack;
    HashMap <String, ArrayList<Sede>> hashTableSedesCliente;
    HashMap <Integer, ArrayList<Sede>> listaSedesCliente;

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

    GoogleMap mGoogleMap;

    View coordinatorLayoutView;

    Location mCurrentLocation;

    LocationManager locationManager;

    private boolean mPermissionDenied = false;

    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    private static final String TAG = "InAppBilling FastTrack";
    IabHelper mHelper;
    private static final String ITEM_SKU = "com.ingenia.fasttrack.pago";

    private String mLastUpdateTime;

    gestionSharedPreferences sharedPreferences;
    int locationCount = 10;
    PendingIntent pendingIntent;

    private Circle mCircle;
    private ArrayList<Circle>circulosSedes;




    public static Snackbar snackBar;
    private String mensajeSnackBar = "";

    private MultilineSnackbar multilineSnackbar;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().hide();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        updateValuesFromBundle(savedInstanceState);

        sharedPreferences = new gestionSharedPreferences(this);
        vars = new vars();

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

        progressDialog = new ProgressDialog(Inicio.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando Sitios FastTrack, espera un momento ...");
        progressDialog.show();

        serviceObtenerSedes();

    }

    public View.OnClickListener undoOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
                /*Snackbar.make(view, "Item removed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

           /* Snackbar.make(coordinatorLayoutView, "¿Deseas comprar tu FastTrack y así evitar la fila ahora?", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Comprar", this).show();*/
            multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, "Estás en Banco de Occidente,\n"+
                    "evita la fila justo ahora\n"+"pagando solo 3.000$", Snackbar.LENGTH_INDEFINITE);
            multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            multilineSnackbar.setAction("Comprar", undoOnClickListener);
            multilineSnackbar.show();
            showMessage = true;


            mHelper.launchPurchaseFlow(Inicio.this, ITEM_SKU, 10001,
                    mPurchaseFinishedListener, "mypurchasetoken");
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
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
                return;
            }
            else

            if (purchase.getSku().equals(ITEM_SKU))
            {
                consumeItem();
                //buyButton.setEnabled(false);
                Log.i(LogCatClass,"Pagaste! Salta la fila ahora!!!");

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
            }
            else
            {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
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
        enableMyLocation();
        
        /*circle();*/

        showMessage=true;
        setUpShowMessage(false,"","");

    }




    public void setUpShowMessage(boolean mostrar,String nombreSede, String ValorTicket)
    {
        if(mostrar)
        {
            if(!showMessage)
            {
                multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, "Estás en: "+nombreSede+" "+
                        "evita la fila justo ahora "+"pagando "+ValorTicket+"$", Snackbar.LENGTH_INDEFINITE);
                multilineSnackbar.setAction("Comprar", undoOnClickListener);
                multilineSnackbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                multilineSnackbar.show();
                showMessage = true;
            }
        }
        else
        {
            if(showMessage)
            {
                multilineSnackbar = MultilineSnackbar.make(coordinatorLayoutView, "Ubica los puntos FastTrack"+
                        " en el mapa y haz tu diligencia y evita la fila.", Snackbar.LENGTH_INDEFINITE);
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
                        sedesClienteMostrarSnack.get(k).getValorTurno().toString());
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

    private void enableMyLocation()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        else if (mGoogleMap != null)
        {
            // Access to the location has been granted to the app.
            // mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            //mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

            mGoogleMap.setMyLocationEnabled(true);
            //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(mGoogleMap.getCameraPosition().zoom - 0.5f));

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
        {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        }
        else
        {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
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
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError()
    {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    private void serviceObtenerSedes()
    {
        _urlWebService = vars.ipServer.concat("/ws/getClientes");

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
                                        sede.setValorTurno(cliente.getValturno());
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
                                        Log.i("Cliente_Sede_CodSede: ", ""+sedesClienteAux.get(k).getCodSede());
                                        Log.i("Cliente_Sede_CodCliente: ", ""+sedesClienteAux.get(k).getCodCliente());
                                        Log.i("Cliente_Sede_nomSede: ", ""+sedesClienteAux.get(k).getNomSede());
                                        Log.i("Cliente_Sede_dirSede: ", ""+sedesClienteAux.get(k).getDirSede());
                                        Log.i("Cliente_Sede_corSede: ", ""+sedesClienteAux.get(k).getCorSede());
                                        Log.i("Cliente_Sede_imgSede: ", "vvv"+sedesClienteAux.get(k).getImgSede());
                                        Log.i("Cliente_Sede_lonSede: ", ""+sedesClienteAux.get(k).getLonSede());
                                        Log.i("Cliente_Sede_latSede: ", ""+sedesClienteAux.get(k).getLatSede());

                                        markerOptions = new MarkerOptions();
                                        final LatLng latLng = new LatLng(Double.parseDouble(sedesClienteAux.get(k).getLatSede()),
                                                                         Double.parseDouble(sedesClienteAux.get(k).getLonSede()));
                                        markerOptions.position(latLng);
                                        markerOptions.title(sedesClienteAux.get(k).getNomSede());
                                        markerOptions.snippet(sedesClienteAux.get(k).getDirSede());

                                       /* if(sedesClienteAux.get(k).getCodCliente().equals("1"))
                                        {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bbancolombia));
                                        }

                                        if(sedesClienteAux.get(k).getCodCliente().equals("2"))
                                        {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bbdo));
                                        }

                                        if(sedesClienteAux.get(k).getCodCliente().equals("3"))
                                        {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bbogota));
                                        }

                                        if(sedesClienteAux.get(k).getCodCliente().equals("4"))
                                        {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bpopular));
                                        }*/






                                        try
                                        {
                                            Bitmap bmImg = Ion.with(getApplicationContext())
                                                    .load(sedesClienteAux.get(k).getImgSede()).asBitmap().get();
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmImg));

                                       } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
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

                            //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            Log.i("errorsisimo",e.getMessage().toString());

                        /*    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();*/

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
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
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
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
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
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
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
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
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
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
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
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                        }

                        //progressBar.setVisibility(View.GONE);
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
        {

//                  GESTION DE PARAMETROS POR VIA GET.
//				    @Override
//		            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError
//		            {
//				    	HashMap<String, String> params = new HashMap<String, String>();
//				    	//params.put("Content-Type", "application/json");
//				    	params.put("email_cliente", "MMM" );
//				    	params.put("pass_cliente", "MMM" );
//				    	params.put("name_cliente", "MMM");
//				    	params.put("ape_cliente", "MMM" );
//
//
//		                return params;
//		            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
/*
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
*/
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}
