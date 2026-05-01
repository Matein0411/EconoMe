package com.EconoMe.comun;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorTexto {

    public static String extraerTextoDePDF(String rutaPDF) throws IOException {
        try(PDDocument documento = PDDocument.load(new File(rutaPDF))){
            PDFTextStripper stripper = new PDFTextStripper();
            return  stripper.getText(documento);
        }
    }

    public static String extraerFragmentoDeUnTexto(String texto, String patron, int grupo){
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(texto);

        return (matcher.find()) ? matcher.group(grupo) : null;
    }
}
