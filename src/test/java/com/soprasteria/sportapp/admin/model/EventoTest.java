package com.soprasteria.sportapp.admin.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventoActividadTest {

    @Test
    void constructor_conParametros_asignaCorrectamente() {
        EventoActividad e = new EventoActividad(
                "ev-1", "Fútbol 7", "Fútbol", "Partido amistoso",
                "Campo Norte", "2026-05-20", "18:00",
                "usr-1", "Víctor Blat", 12, 14, 39.47, -0.37, "⚽");

        assertEquals("ev-1",        e.getId());
        assertEquals("Fútbol 7",    e.getTitulo());
        assertEquals("Fútbol",      e.getDeporte());
        assertEquals("Campo Norte", e.getUbicacion());
        assertEquals("2026-05-20",  e.getFecha());
        assertEquals("18:00",       e.getHora());
        assertEquals("usr-1",       e.getCreadorId());
        assertEquals("Víctor Blat", e.getCreadorNombre());
        assertEquals(12,            e.getParticipantes());
        assertEquals(14,            e.getMaxParticipantes());
        assertEquals(39.47,         e.getLatitud(), 0.001);
        assertEquals(-0.37,         e.getLongitud(), 0.001);
        assertEquals("⚽",          e.getEmoji());
    }

    @Test
    void constructorVacio_devuelveNulos() {
        EventoActividad e = new EventoActividad();
        assertNull(e.getId());
        assertNull(e.getTitulo());
        assertEquals(0, e.getParticipantes());
    }

    @Test
    void toString_contieneTituloYDeporte() {
        EventoActividad e = new EventoActividad(
                "ev-1", "Fútbol 7", "Fútbol", "", "", "2026-05-20", "18:00",
                "usr-1", "Víctor", 5, 10, 0, 0, "⚽");
        String str = e.toString();
        assertTrue(str.contains("Fútbol 7"));
        assertTrue(str.contains("Fútbol"));
    }
}

class EventoEspecialTest {

    @Test
    void constructor_conParametros_asignaCorrectamente() {
        EventoEspecial e = new EventoEspecial(
                "ee-1", "Torneo Primavera", "torneo", "Torneo oficial",
                "Polideportivo", "2026-06-01", "09:00",
                "adm-1", 12, 64, 39.46, -0.38, "🏆", "2026-05-01");

        assertEquals("ee-1",              e.getId());
        assertEquals("Torneo Primavera",  e.getTitulo());
        assertEquals("torneo",            e.getTipo());
        assertEquals("Polideportivo",     e.getUbicacion());
        assertEquals("adm-1",             e.getAdminCreadorId());
        assertEquals(12,                  e.getParticipantes());
        assertEquals(64,                  e.getMaxParticipantes());
        assertEquals("🏆",               e.getEmoji());
    }

    // ── getEmojiPorTipo ───────────────────────────────────────────────────

    @Test
    void getEmojiPorTipo_benefico_devuelveCorazon() {
        assertEquals("❤️", EventoEspecial.getEmojiPorTipo("benefico"));
    }

    @Test
    void getEmojiPorTipo_torneo_devuelveTrofeo() {
        assertEquals("🏆", EventoEspecial.getEmojiPorTipo("torneo"));
    }

    @Test
    void getEmojiPorTipo_especial_devuelveEstrella() {
        assertEquals("⭐", EventoEspecial.getEmojiPorTipo("especial"));
    }

    @Test
    void getEmojiPorTipo_exhibicion_devuelveCarpa() {
        assertEquals("🎪", EventoEspecial.getEmojiPorTipo("exhibicion"));
    }

    @Test
    void getEmojiPorTipo_desconocido_devuelveEstrellaPorDefecto() {
        assertEquals("⭐", EventoEspecial.getEmojiPorTipo("desconocido"));
    }

    @Test
    void toString_contieneTituloYTipo() {
        EventoEspecial e = new EventoEspecial(
                "ee-1", "Torneo Primavera", "torneo", "", "", "2026-06-01", "09:00",
                "adm-1", 0, 64, 0, 0, "🏆", "2026-05-01");
        String str = e.toString();
        assertTrue(str.contains("Torneo Primavera"));
        assertTrue(str.contains("torneo"));
    }
}
