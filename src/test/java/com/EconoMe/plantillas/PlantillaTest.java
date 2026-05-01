package com.EconoMe.plantillas;

import com.EconoMe.movimientos.modelos.CategoriaGasto;
import com.EconoMe.movimientos.modelos.CategoriaIngreso;
import com.EconoMe.plantillas.modelos.Plantilla;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantillaTest {
    @Test
    void given_categoriaGasto_when_setCategoriaGasto_then_tipoYCategoriaSetCorrectly() {
        Plantilla plantilla = new Plantilla("Pago Netflix", 15.99);

        plantilla.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);

        assertEquals("GASTO", plantilla.getTipo());
        assertEquals("ENTRETENIMIENTO", plantilla.getCategoria());
    }

    @Test
    void given_categoriaIngreso_when_setCategoriaIngreso_then_tipoYCategoriaSetCorrectly() {
        Plantilla plantilla = new Plantilla("Salario mensual", 1500.00);

        plantilla.setCategoriaIngreso(CategoriaIngreso.SALARIO);

        assertEquals("INGRESO", plantilla.getTipo());
        assertEquals("SALARIO", plantilla.getCategoria());
    }

    @Test
    void given_plantillaGasto_when_getCategoriaEnum_then_returnsCategoriaGasto() {
        Plantilla plantilla = new Plantilla("Supermercado", 120.50);
        plantilla.setCategoriaGasto(CategoriaGasto.ENTRETENIMIENTO);

        Object categoria = plantilla.getCategoriaEnum();

        assertTrue(categoria instanceof CategoriaGasto);
        assertEquals(CategoriaGasto.ENTRETENIMIENTO, categoria);
    }

    @Test
    void given_plantillaIngreso_when_getCategoriaEnum_then_returnsCategoriaIngreso() {
        Plantilla plantilla = new Plantilla("Freelance", 500.00);
        plantilla.setCategoriaIngreso(CategoriaIngreso.SALARIO);

        Object categoria = plantilla.getCategoriaEnum();

        assertTrue(categoria instanceof CategoriaIngreso);
        assertEquals(CategoriaIngreso.SALARIO, categoria);
    }

    @Test
    void given_nuevaPlantilla_when_created_then_activoIsTrueByDefault() {
        Plantilla plantilla = new Plantilla("Arriendo", 800.00);

        assertTrue(plantilla.isActivo());
    }

    @Test
    void given_plantilla_when_setActivo_then_activoChanges() {
        Plantilla plantilla = new Plantilla("Gym", 45.00);

        plantilla.setActivo(false);

        assertFalse(plantilla.isActivo());
    }

    @Test
    void given_montoConDecimales_when_setMonto_then_montoSetCorrectly() {
        Plantilla plantilla = new Plantilla();

        plantilla.setMonto(123.456789);

        assertEquals(123.456789, plantilla.getMonto(), 0.000001);
    }

    @Test
    void given_nombreYMonto_when_createPlantilla_then_fieldsSetCorrectly() {
        Plantilla plantilla = new Plantilla("Luz", 35.75);

        assertEquals("Luz", plantilla.getNombre());
        assertEquals(35.75, plantilla.getMonto(), 0.01);
    }

    @Test
    void given_tipoManual_when_setTipo_then_tipoSetCorrectly() {
        Plantilla plantilla = new Plantilla("Test", 100.00);

        plantilla.setTipo("INGRESO");

        assertEquals("INGRESO", plantilla.getTipo());
    }

    @Test
    void given_categoriaManual_when_setCategoria_then_categoriaSetCorrectly() {
        Plantilla plantilla = new Plantilla("Test", 50.00);

        plantilla.setCategoria("TRANSPORTE");

        assertEquals("TRANSPORTE", plantilla.getCategoria());
    }
}