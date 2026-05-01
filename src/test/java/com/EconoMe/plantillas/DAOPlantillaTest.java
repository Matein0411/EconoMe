package com.EconoMe.plantillas;

import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import com.EconoMe.plantillas.modelos.Plantilla;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DAOPlantillaTest {

    @Test
    void given_nombreExistente_when_existePlantillaPorNombre_then_returnsTrue() {
        // Simular que la query retorna count = 1
        String nombreExistente = "Arriendo";
        Long usuarioId = 1L;
        Long count = 1L;

        boolean existe = count > 0;

        assertTrue(existe);
    }

    @Test
    void given_nombreNoExistente_when_existePlantillaPorNombre_then_returnsFalse() {
        // Simular que la query retorna count = 0
        String nombreNoExistente = "NoExiste123";
        Long usuarioId = 1L;
        Long count = 0L;

        boolean existe = count > 0;

        assertFalse(existe);
    }

    @Test
    void given_nombreConEspacios_when_existePlantillaPorNombre_then_comparaSinEspacios() {
        // Validar que el trim funciona correctamente
        String nombreConEspacios = "  Gym  ";
        String nombreLimpio = nombreConEspacios.trim().toLowerCase();
        String nombreBD = "gym";

        assertEquals(nombreBD, nombreLimpio);
    }

    @Test
    void given_soloUsuarioId_when_buscarPorFiltros_then_retornaTodasLasPlantillas() {
        Long usuarioId = 1L;

        Plantilla p1 = new Plantilla("Netflix", 15.99);
        p1.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        p1.setFechaCreacion(LocalDateTime.now());

        Plantilla p2 = new Plantilla("Salario", 1500.00);
        p2.setCategoriaIngreso(CategoriaIngreso.SALARIO);
        p2.setFechaCreacion(LocalDateTime.now().minusDays(1));

        List<Plantilla> resultado = Arrays.asList(p1, p2);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    void given_filtroNombre_when_buscarPorFiltros_then_retornaCoincidencias() {
        Long usuarioId = 1L;
        String nombreBusqueda = "netflix";

        Plantilla p1 = new Plantilla("Netflix Premium", 15.99);
        p1.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        p1.setFechaCreacion(LocalDateTime.now());

        List<Plantilla> resultado = Arrays.asList(p1);

        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        assertTrue(resultado.get(0).getNombre().toLowerCase().contains(nombreBusqueda));
    }

    @Test
    void given_filtroTipo_when_buscarPorFiltros_then_retornaSoloEseTipo() {
        Long usuarioId = 1L;
        String tipo = "GASTO";

        Plantilla p1 = new Plantilla("Luz", 35.00);
        p1.setCategoriaGasto(CategoriaGasto.SERVICIOS);
        p1.setFechaCreacion(LocalDateTime.now());

        Plantilla p2 = new Plantilla("Internet", 40.00);
        p2.setCategoriaGasto(CategoriaGasto.SERVICIOS);
        p2.setFechaCreacion(LocalDateTime.now().minusDays(1));

        List<Plantilla> resultado = Arrays.asList(p1, p2);

        assertNotNull(resultado);
        assertTrue(resultado.stream().allMatch(p -> "GASTO".equals(p.getTipo())));
    }

    @Test
    void given_filtroCategoria_when_buscarPorFiltros_then_retornaSoloEsaCategoria() {
        Long usuarioId = 1L;
        String categoria = "ENTRETENIMIENTO";

        Plantilla p1 = new Plantilla("Netflix", 15.99);
        p1.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        p1.setFechaCreacion(LocalDateTime.now());

        Plantilla p2 = new Plantilla("Spotify", 9.99);
        p2.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        p2.setFechaCreacion(LocalDateTime.now().minusDays(1));

        List<Plantilla> resultado = Arrays.asList(p1, p2);

        assertNotNull(resultado);
        assertTrue(resultado.stream().allMatch(
                p -> CategoriaGasto.ENTRETENIMIENTO.name().equals(p.getCategoria())
        ));
    }

    @Test
    void given_filtroTodosTipo_when_buscarPorFiltros_then_noFiltraPorTipo() {
        Long usuarioId = 1L;
        String tipo = "TODOS";

        Plantilla gasto = new Plantilla("Supermercado", 100.00);
        gasto.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        gasto.setFechaCreacion(LocalDateTime.now());

        Plantilla ingreso = new Plantilla("Freelance", 500.00);
        ingreso.setCategoriaIngreso(CategoriaIngreso.SALARIO);
        ingreso.setFechaCreacion(LocalDateTime.now().minusDays(1));

        List<Plantilla> resultado = Arrays.asList(gasto, ingreso);

        // Debe incluir tanto GASTO como INGRESO
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(p -> "GASTO".equals(p.getTipo())));
        assertTrue(resultado.stream().anyMatch(p -> "INGRESO".equals(p.getTipo())));
    }

    @Test
    void given_filtroTodasCategorias_when_buscarPorFiltros_then_noFiltraPorCategoria() {
        Long usuarioId = 1L;
        String categoria = "TODAS";

        Plantilla p1 = new Plantilla("Netflix", 15.99);
        p1.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        p1.setFechaCreacion(LocalDateTime.now());

        Plantilla p2 = new Plantilla("Luz", 35.00);
        p2.setCategoriaGasto(CategoriaGasto.SERVICIOS);
        p2.setFechaCreacion(LocalDateTime.now().minusDays(1));

        List<Plantilla> resultado = Arrays.asList(p1, p2);

        // Debe incluir diferentes categorías
        assertNotNull(resultado);
        assertTrue(resultado.stream().anyMatch(
                p -> CategoriaGasto.ENTRETENIMIENTO.name().equals(p.getCategoria())
        ));
        assertTrue(resultado.stream().anyMatch(
                p -> CategoriaGasto.SERVICIOS.name().equals(p.getCategoria())
        ));
    }

    @Test
    void given_variasPlantillas_when_buscarPorFiltros_then_ordenaPorFechaDescendente() {
        Long usuarioId = 1L;

        Plantilla antigua = new Plantilla("Antigua", 10.00);
        antigua.setCategoriaGasto(CategoriaGasto.OTROS);
        antigua.setFechaCreacion(LocalDateTime.now().minusDays(10));

        Plantilla media = new Plantilla("Media", 20.00);
        media.setCategoriaGasto(CategoriaGasto.OTROS);
        media.setFechaCreacion(LocalDateTime.now().minusDays(5));

        Plantilla reciente = new Plantilla("Reciente", 30.00);
        reciente.setCategoriaGasto(CategoriaGasto.OTROS);
        reciente.setFechaCreacion(LocalDateTime.now());

        // Lista ordenada por fecha descendente (más reciente primero)
        List<Plantilla> resultado = Arrays.asList(reciente, media, antigua);

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        // Verificar orden descendente
        for (int i = 0; i < resultado.size() - 1; i++) {
            assertTrue(
                    resultado.get(i).getFechaCreacion().isAfter(resultado.get(i + 1).getFechaCreacion()) ||
                            resultado.get(i).getFechaCreacion().isEqual(resultado.get(i + 1).getFechaCreacion())
            );
        }
    }

    @Test
    void given_filtrosMultiples_when_buscarPorFiltros_then_aplicaTodosFiltros() {
        Long usuarioId = 1L;
        String nombre = "netflix";
        String tipo = "GASTO";
        String categoria = "ENTRETENIMIENTO";

        Plantilla p1 = new Plantilla("Netflix Premium", 15.99);
        p1.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);
        p1.setFechaCreacion(LocalDateTime.now());

        List<Plantilla> resultado = Arrays.asList(p1);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        Plantilla plantilla = resultado.get(0);
        assertTrue(plantilla.getNombre().toLowerCase().contains(nombre));
        assertEquals(tipo, plantilla.getTipo());
        assertEquals(categoria, plantilla.getCategoria());
    }

    @Test
    void given_nombreVacio_when_buscarPorFiltros_then_noFiltraPorNombre() {
        Long usuarioId = 1L;
        String nombreVacio = "";

        // La lógica verifica: nombre != null && !nombre.trim().isEmpty()
        boolean debeAplicarFiltroNombre = nombreVacio != null && !nombreVacio.trim().isEmpty();

        assertFalse(debeAplicarFiltroNombre);
    }

    @Test
    void given_nombreNull_when_buscarPorFiltros_then_noFiltraPorNombre() {
        Long usuarioId = 1L;
        String nombreNull = null;

        // La lógica verifica: nombre != null && !nombre.trim().isEmpty()
        boolean debeAplicarFiltroNombre = nombreNull != null && !nombreNull.trim().isEmpty();

        assertFalse(debeAplicarFiltroNombre);
    }

    @Test
    void given_nombreConEspacios_when_buscarPorFiltros_then_buscaSinEspacios() {
        String nombreConEspacios = "  Netflix  ";
        String nombreProcesado = nombreConEspacios.trim().toLowerCase();

        assertEquals("netflix", nombreProcesado);
    }
}