package com.EconoMe.listas_de_compras;

import com.EconoMe.comun.DAOBase;
import com.EconoMe.listas_de_compras.dao.DAOArticuloDeCompra;
import com.EconoMe.listas_de_compras.dao.DAOListaDeCompra;
import com.EconoMe.listas_de_compras.modelos.ArticuloDeCompras;
import com.EconoMe.listas_de_compras.modelos.EstadoCompra;
import com.EconoMe.listas_de_compras.modelos.ListaDeCompras;
import com.EconoMe.listas_de_compras.servicios.ServicioArticuloDeCompras;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListaDeComprasTest {

    @Mock
    private DAOListaDeCompra daoListaMock;

    @Mock
    private DAOArticuloDeCompra daoArticuloMock;

    @InjectMocks
    private ServicioArticuloDeCompras servicio;

    @BeforeEach
    void setUp() {
        daoListaMock = Mockito.mock(DAOListaDeCompra.class);
        daoArticuloMock = Mockito.mock(DAOArticuloDeCompra.class);

        // Crear servicio con mocks
        servicio = new ServicioArticuloDeCompras();

        // Usar reflexión para inyectar mocks (solo para tests)
        try {
            var fieldLista = ServicioArticuloDeCompras.class.getDeclaredField("daoLista");
            var fieldArticulo = ServicioArticuloDeCompras.class.getDeclaredField("daoArticulo");

            fieldLista.setAccessible(true);
            fieldArticulo.setAccessible(true);

            fieldLista.set(servicio, daoListaMock);
            fieldArticulo.set(servicio, daoArticuloMock);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void given_listaValida_when_registrarListaDeCompras_then_ok() {
        // Arrange
        String nombreLista = "nombreListaTest";

        // Act
        boolean resultado = servicio.registrarListaDeCompras(nombreLista);

        // Assert
        assertTrue(resultado);
        // Verificar que se creó la lista
        verify(daoListaMock).crear(any(ListaDeCompras.class));
    }

    @Test
    void given_listaNula_when_registrarListaDeCompras_then_excepcion() {
        ServicioArticuloDeCompras servicio = new ServicioArticuloDeCompras();

        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarListaDeCompras(null);
        });
    }

    @Test
    void given_listaSinNombre_when_registrarListaDeCompras_then_excepcion() {
        ServicioArticuloDeCompras servicio = new ServicioArticuloDeCompras();

        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarListaDeCompras("");
        });
    }

    @Test
    void given_articuloValido_when_registrarArticuloDeCompras_then_ok() {
        // Arrange
        String nombreArticulo = "nombreArticuloTest";
        double precioUnitario = 1.5;
        ListaDeCompras listaDeCompras = new ListaDeCompras("Lista Test", EstadoCompra.PENDIENTE);
        listaDeCompras.setPrecioTotal(0.0);

        // Act
        boolean resultado = servicio.registrarArticuloDeCompras(nombreArticulo, precioUnitario, listaDeCompras);

        // Assert
        assertTrue(resultado);
        // Verificar que se creó el artículo
        verify(daoArticuloMock).crear(any(ArticuloDeCompras.class));
        // Verificar que se actualizó la lista
        verify(daoListaMock).actualizar(listaDeCompras);
        // Verificar el precio total
        assertEquals(1.5, listaDeCompras.getPrecioTotal(), 0.001);
    }

    @Test
    void given_articuloSinNombre_when_registrarArticuloDeCompras_then_excepcion() {
        ListaDeCompras lista = new ListaDeCompras("Lista Test", EstadoCompra.PENDIENTE);
        String nombreArticulo = "";
        double precioUnitario = 1.5;
        ServicioArticuloDeCompras servicio = new ServicioArticuloDeCompras();

        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarArticuloDeCompras(nombreArticulo, precioUnitario, lista);
        });
    }

    @Test
    void given_articuloConPrecioNegativo_when_registrarArticuloDeCompras_then_excepcion() {
        ListaDeCompras lista = new ListaDeCompras("Lista Test", EstadoCompra.PENDIENTE);
        String nombreArticulo = "nombreArticuloTest";
        double precioUnitario = -1.5;
        ServicioArticuloDeCompras servicio = new ServicioArticuloDeCompras();

        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarArticuloDeCompras(nombreArticulo, precioUnitario, lista);
        });
    }

    @Test
    void given_articuloConPrecioCero_when_registrarArticuloDeCompras_then_excepcion() {
        ListaDeCompras lista = new ListaDeCompras("Lista Test", EstadoCompra.PENDIENTE);
        String nombreArticulo = "nombreArticuloTest";
        double precioUnitario = 0;
        ServicioArticuloDeCompras servicio = new ServicioArticuloDeCompras();

        assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrarArticuloDeCompras(nombreArticulo, precioUnitario, lista);
        });
    }

    @Test
    void given_variosArticulos_when_calcularPrecioTotal_then_sumaCorrecta() {
        // Arrange
        ListaDeCompras lista = new ListaDeCompras("Supermercado", EstadoCompra.PENDIENTE);
        lista.setPrecioTotal(0.0); // Asegurar que empieza en 0

        // Act - Registrar múltiples artículos
        servicio.registrarArticuloDeCompras("Articulo1", 1.5, lista);
        servicio.registrarArticuloDeCompras("Articulo2", 2.5, lista);
        servicio.registrarArticuloDeCompras("Articulo3", 3.5, lista);

        // Assert
        // Cada registro debe llamar a actualizar
        verify(daoListaMock, times(3)).actualizar(lista);

        // El precio total acumulado debería ser 7.5
        // Como el servicio modifica la lista por referencia, podemos verificarlo:
        assertEquals(7.5, lista.getPrecioTotal(), 0.001);
    }

    @Test
    void given_articulosConIdsValidos_when_actualizar_estado_then_ok() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // Act & Assert - Verifica que no lanza excepciones
        assertDoesNotThrow(() ->
                daoArticuloMock.actualizarEstadoArticulos(ids, EstadoCompra.COMPLETADA)
        );
    }

    @Test
    void given_listaVacia_when_actualizarEstadoArticulos_then_ok() {
        // Arrange
        List<Long> ids = List.of();

        // Act & Assert - No debe lanzar excepción con lista vacía
        assertDoesNotThrow(() ->
                daoArticuloMock.actualizarEstadoArticulos(ids, EstadoCompra.COMPLETADA)
        );
    }

    @Test
    void testRegistrarListaDeCompras_ConNombreValido() {
        // Arrange
        String nombre = "Lista de Supermercado";

        // Act
        boolean resultado = servicio.registrarListaDeCompras(nombre);

        // Assert
        assertTrue(resultado);
        verify(daoListaMock, times(1)).crear(any(ListaDeCompras.class));
    }

    @Test
    void testRegistrarListaDeCompras_ConNombreNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarListaDeCompras(null)
        );

        assertEquals("El nombre de la lista es requerido", exception.getMessage());
    }

    @Test
    void testGenerarGastoDesdeLista_ConArticulosCompletados() {
        // Arrange
        Long listaId = 1L;
        ListaDeCompras listaMock = Mockito.mock(ListaDeCompras.class);
        ArticuloDeCompras articulo1 = new ArticuloDeCompras();
        articulo1.setPrecioUnitario(100.0);
        articulo1.setEstado(EstadoCompra.COMPLETADA);

        ArticuloDeCompras articulo2 = new ArticuloDeCompras();
        articulo2.setPrecioUnitario(50.0);
        articulo2.setEstado(EstadoCompra.COMPLETADA);

        when(daoListaMock.buscarPorId(listaId)).thenReturn(listaMock);
        when(listaMock.getArticulos()).thenReturn(Arrays.asList(articulo1, articulo2));

        // Act
        double gasto = servicio.generarGastoDesdeLista(listaId);

        // Assert
        assertEquals(150.0, gasto, 0.001);
    }

    @Test
    void testGenerarGastoDesdeLista_ConIdNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.generarGastoDesdeLista(null)
        );

        assertEquals("El ID de la lista es requerido", exception.getMessage());
    }

    @Test
    void testMarcarArticuloComoComprado_ConIdValido() {
        // Arrange
        Long articuloId = 1L;

        // Act
        boolean resultado = servicio.marcarArticuloComoComprado(articuloId);

        // Assert
        assertTrue(resultado);
        verify(daoArticuloMock, times(1))
                .actualizarEstadoArticulo(eq(articuloId), eq(EstadoCompra.COMPLETADA));
    }
}