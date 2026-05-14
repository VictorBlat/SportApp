package com.soprasteria.sportapp.admin.util;

/**
 * Configuración centralizada de Supabase.
 * Contiene las constantes de URL y clave anónima.
 */
public class SupabaseConfig {

    public static final String URL = "https://mxxvdcmvpincdgupqrdw.supabase.co";
    public static final String ANON_KEY = "PEGA_AQUI_TU_NUEVA_ANON_KEY";
    public static final String REST_URL = URL + "/rest/v1";

    private SupabaseConfig() {
        // Clase de utilidad, no instantiable
    }
}