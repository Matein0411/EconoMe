package com.EconoMe.plantillas.servicios;

import com.EconoMe.movimientos.modelos.*;
import com.EconoMe.plantillas.dao.DAOPlantilla;
import com.EconoMe.plantillas.modelos.Plantilla;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServicioPlantilla {

    private final DAOPlantilla dao;
    private List<Plantilla> plantillas = new ArrayList<>();
    private static final Set<String> TIPOS_VALIDOS = Set.of("GASTO", "INGRESO");

    public ServicioPlantilla() {
        this.dao = new DAOPlantilla();
    }

    public ServicioPlantilla(DAOPlantilla dao) {
        this.dao = dao;
    }

    public void crearPlantilla(Plantilla plantilla) {
        validarPlantilla(plantilla);

        plantilla.setFechaCreacion(LocalDateTime.now());
        if (dao.existePlantillaPorNombre(plantilla.getNombre().trim())) {
            throw new IllegalStateException("Ya existe una plantilla con el mismo nombre");
        }
        dao.crear(plantilla);
    }

    private void validarPlantilla(Plantilla plantilla) {
        if (plantilla == null) {
            throw new IllegalArgumentException("Plantilla no puede ser nula");
        }

        validarCampoNoVacio(plantilla.getNombre(), "El nombre");

        validarMonto(plantilla.getMonto());
        validarTipo(plantilla.getTipo());

        Object categoriaEnum = plantilla.getCategoriaEnum();
        String categoriaStr = (categoriaEnum != null) ? categoriaEnum.toString() : null;
        validarCategoria(plantilla.getTipo(), categoriaStr);
    }

    public void validarMonto(double monto) {
        if (Double.isNaN(monto) || monto <= 0.0 || monto > 999_999.99) {
            throw new IllegalArgumentException("Monto no válido");
        }
        redondearMonto(monto);
    }

    public void validarTipo(String tipo) {
        validarCampoNoVacio(tipo, "El tipo");

        if (!TIPOS_VALIDOS.contains(tipo)) {
            throw new IllegalArgumentException("Tipo inválido");
        }
    }

    public void validarCategoria(String tipo, String categoria) {
        validarCampoNoVacio(categoria, "La categoría");

        try {
            if ("GASTO".equalsIgnoreCase(tipo)) {
                CategoriaGasto.valueOf(categoria.toUpperCase());
            } else if ("INGRESO".equalsIgnoreCase(tipo)) {
                CategoriaIngreso.valueOf(categoria.toUpperCase());
            } else {
                throw new IllegalArgumentException("Tipo inválido para validación de categoría");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida");
        }
    }

    private void validarCampoNoVacio(String valor, String mensajeCampo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensajeCampo + " no puede estar vacío o en blanco");
        }
    }

    public void eliminarPlantilla(Long plantilla_Id) {
        dao.borrar(plantilla_Id);
    }

    public Movimiento aplicarPlantilla(Plantilla plantilla) {
        if (plantilla == null) {
            throw new IllegalArgumentException("La plantilla no puede ser nula");
        }

        if (!plantilla.isActivo()) {
            throw new IllegalStateException("La plantilla debe estar activa");
        }

        String descripcion = plantilla.getNombre();
        Movimiento movimiento;

        if ("INGRESO".equals(plantilla.getTipo())) {
            movimiento = new Ingreso(
                    plantilla.getMonto(),
                    descripcion,
                    plantilla.getCuenta(),
                    (CategoriaIngreso) plantilla.getCategoriaEnum()
            );
        } else if ("GASTO".equals(plantilla.getTipo())) {
            movimiento = new Gasto(
                    plantilla.getMonto(),
                    descripcion,
                    plantilla.getCuenta(),
                    (CategoriaGasto) plantilla.getCategoriaEnum()
            );
        } else {
            throw new IllegalArgumentException("Tipo de plantilla inválido: " + plantilla.getTipo());
        }

        return movimiento;
    }

    public Plantilla duplicarPlantilla(Plantilla original) {
        if (original == null) {
            throw new IllegalArgumentException("Plantilla original requerida");
        }

        Plantilla copia = new Plantilla();
        copia.setNombre(generarNombreUnico(original.getNombre()));
        copia.setMonto(original.getMonto());
        copia.setTipo(original.getTipo());
        copia.setCategoria(original.getCategoria());
        copia.setCuenta(original.getCuenta());
        copia.setActivo(true);
        copia.setFechaCreacion(LocalDateTime.now());

        return copia;
    }

    private String generarNombreUnico(String nombreOriginal) {
        String nombreBase = extraerNombreBase(nombreOriginal);

        List<Plantilla> plantillasExistentes = new ArrayList<>(plantillas);

        try {
            List<Plantilla> plantillasBD = listarPlantillas();
            if (plantillasBD != null) {
                plantillasExistentes.addAll(plantillasBD);
            }
        } catch (Exception e) {
            // Continuar solo con la lista en memoria
        }

        int maxNumero = 0;

        for (Plantilla p : plantillasExistentes) {
            String nombreActual = p.getNombre();

            if (nombreActual.equals(nombreBase)) {
                maxNumero = Math.max(maxNumero, 1);
            } else if (nombreActual.startsWith(nombreBase + " (")) {
                try {
                    String numeroStr = nombreActual.substring(
                            nombreBase.length() + 2,
                            nombreActual.length() - 1
                    );
                    int numero = Integer.parseInt(numeroStr);
                    maxNumero = Math.max(maxNumero, numero);
                } catch (Exception e) {
                    // Ignorar errores de parsing
                }
            }
        }

        return maxNumero == 0 ? nombreBase : nombreBase + " (" + (maxNumero) + ")";
    }

    private String extraerNombreBase(String nombreCompleto) {
        if (nombreCompleto.matches(".+ \\(\\d+\\)")) {
            return nombreCompleto.substring(0, nombreCompleto.lastIndexOf(" ("));
        }
        return nombreCompleto;
    }

    public double redondearMonto(double monto) {
        return Math.round(monto * 100.0) / 100.0;
    }

    public void actualizarPlantilla(Plantilla plantilla) {
        dao.actualizar(plantilla);
    }

    public Plantilla buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public List<Plantilla> listarPlantillas() {
        return dao.listar();
    }

    public void verificarNombreUnico(Plantilla plantilla1) {
        plantillas.forEach(plantilla -> {
            if(plantilla.getNombre().trim().equals(plantilla1.getNombre().trim())){
                throw new IllegalArgumentException("Los nombres de las plantillas deben ser unicos");
            }
        });
        plantillas.add(plantilla1);
    }

    public List<Plantilla> buscarPorNombre(String nombreABuscar) {
        List<Plantilla> plantillasEncontradas = new ArrayList<>();
        plantillas.forEach(plantilla -> {
            if(plantilla.getNombre().contains(nombreABuscar)){
                plantillasEncontradas.add(plantilla);
            }
        });
        return plantillasEncontradas;
    }

    public List<Plantilla> buscarPorCategoriaGasto(CategoriaGasto categoriaGasto) {
        List<Plantilla> plantillasEncontradas = new ArrayList<>();
        plantillas.forEach(plantilla -> {
            if(plantilla.getCategoria().contains(categoriaGasto.toString())){
                plantillasEncontradas.add(plantilla);
            }
        });
        return plantillasEncontradas;
    }

    public List<Plantilla> buscarPorTipo(String tipo) {
        List<Plantilla> plantillasEncontradas = new ArrayList<>();
        plantillas.forEach(plantilla -> {
            if(plantilla.getTipo().contains(tipo)){
                plantillasEncontradas.add(plantilla);
            }
        });
        return plantillasEncontradas;
    }

    public void guardarEnLista(Plantilla plantilla) {
        plantillas.add(plantilla);
    }

    public List<Plantilla> buscarPlantillasConFiltros(String nombre, String tipo, String categoria) {
        return dao.buscarPorFiltros(nombre, tipo, categoria);
    }
}