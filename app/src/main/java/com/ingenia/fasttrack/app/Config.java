package com.ingenia.fasttrack.app;

/**
 * Created by FABiO on 07/10/2016.
 */

public class Config
{

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String PUSH_NOTIFICATION_OCULTAR_PANTALLA_ESPERA_TURNO_ASESOR = "pushNotificationOcultarPantallaEsperaTurnoAsesor";
    public static final String PUSH_NOTIFICATION_USO_TICKET = "pushNotificaClienteUso";
    public static final String PUSH_NOTIFICATION_CLIENTE_ANULAR = "pushNotificaClienteAnular";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
