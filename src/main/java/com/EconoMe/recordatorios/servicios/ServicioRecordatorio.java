package com.EconoMe.recordatorios.servicios;

import com.EconoMe.recordatorios.dao.DAORecordatorio;
import com.EconoMe.recordatorios.modelos.Recordatorio;

import java.time.LocalDate;
import java.util.List;

public class ServicioRecordatorio {
    private DAORecordatorio recordatorioDAO;

    public ServicioRecordatorio() {
        this.recordatorioDAO = new DAORecordatorio();
    }

    /**
     * Crea un nuevo recordatorio validando las reglas de negocio
     */
    public void crearRecordatorio(Recordatorio recordatorio) {
        // Validamos las fechas antes de llamar al DAO
        validarFechas(recordatorio);
        recordatorioDAO.crear(recordatorio);
    }

    /**
     * Actualiza un recordatorio existente validando las reglas de negocio
     */
    public void actualizarRecordatorio(Recordatorio recordatorio) {
        if (recordatorio.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar un recordatorio");
        }

        // Validamos las fechas antes de actualizar
        validarFechas(recordatorio);
        recordatorioDAO.actualizar(recordatorio);
    }

    /**
     * Eliminación de un recordatorio
     */
    public void eliminarRecordatorio(Long recordatorioId) {
        if (recordatorioId == null) {
            throw new IllegalArgumentException("El ID del recordatorio es obligatorio");
        }
        recordatorioDAO.borrar(recordatorioId);
    }

    /**
     * Lista los recordatorios activos
     */
    public List<Recordatorio> listarActivos() {
        return recordatorioDAO.listarActivos();
    }

    public Recordatorio buscarPorId(Long id) {
        return recordatorioDAO.buscarPorId(id);
    }

    public void validarFechas(Recordatorio r) {
        LocalDate hoy = LocalDate.now();

        if (r.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }

        if (r.getFechaInicio().isBefore(hoy)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        if (r.getFechaFin() != null && r.getFechaFin().isBefore(r.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha de inicio");
        }
    }
}