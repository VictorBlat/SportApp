package com.soprasteria.sportapp.admin.util;

/**
 * Configuración centralizada de Supabase.
 */
public class SupabaseConfig {

    public static final String URL = "https://mxxvdcmvpincdgupqrdw.supabase.co";

    // Clave anónima — para operaciones normales con RLS
    public static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14eHZkY212cGluY2RndXBxcmR3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzgyMDE5NTEsImV4cCI6MjA5Mzc3Nzk1MX0.xjZj93m8_obStOwP_MwG49xkuFlX-vsdl-k0eqytXFc";

    // Service role key — solo para el panel admin, salta RLS
    // Obtenerla en: Supabase → Settings → API → service_role (secret)
    public static final String SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14eHZkY212cGluY2RndXBxcmR3Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3ODIwMTk1MSwiZXhwIjoyMDkzNzc3OTUxfQ.Qm-GJIBeMgsPzaLf8CaNSYoNR_XjgP1MntGnZ7vwWHA";

    public static final String REST_URL = URL + "/rest/v1";

    private SupabaseConfig() {}
}