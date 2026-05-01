package com.EconoMe.resumen_financiero.dao;


import com.EconoMe.comun.DAOBase;
import com.EconoMe.resumen_financiero.modelos.DocumentoPDF;

public class DAODocumentoPDF extends DAOBase<DocumentoPDF> {

    public DAODocumentoPDF(){
        super(DocumentoPDF.class);
    }

    public DocumentoPDF guardarPDF(String nombre, byte[] archivoPdf) {
        Long tamanio = (long) archivoPdf.length;
        DocumentoPDF documento = new DocumentoPDF(nombre, archivoPdf, tamanio);

        executeInTransaction(session -> {
            session.persist(documento);
            session.flush();
        });

        return documento;
    }
}
