package com.soprasteria.sportapp.admin.util;

/**
 * Configuración centralizada de Supabase.
 * Contiene las constantes de URL y clave anónima.
 */
public class SupabaseConfig {

    public static final String URL = "https://mxxvdcmvpincdgupqrdw.supabase.co";
    public static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14eHZkY212cGluY2RndXBxcmR3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzgyMDE5NTEsImV4cCI6MjA5Mzc3Nzk1MX0.xjZj93m8_obStOwP_MwG49xkuFlX-vsdl-k0eqytXFc";
    public static final String REST_URL = URL + "/rest/v1";

    private SupabaseConfig() {
        // Clase de utilidad, no instantiable
    }
}