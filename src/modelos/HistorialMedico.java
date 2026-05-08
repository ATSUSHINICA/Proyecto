package modelos;

/*
===================================
|            EN REVISION          |
===================================

*/

/**
 * Clase que guarda todo el historial médico de un paciente
 * Esta clase es POSEE por Paciente (composición)
 * 
 * @author: Laura Leciñena, Alejandro Díaz 
 */

import java.io.Serializable;

public class HistorialMedico implements Serializable{
    
    private static final long serialVersionUID = 6L;

    /**
     * Strings con formato (separador |) para guardar los datos
     * Ejemplo: "Asma | Diabetes |" 
     * Uso String porque así lo indica el diagrama de clases
     * Los separadores permiten guardar múltiples valores en un solo String
     */
    private String enfermedadesPrevias = "";     // Formato que usaremos "enfermedad1 | enfermedad2|"
    private String alergias = "";               // Formato que usaremos "alergia1 | alergia2|"
    private String intervencionesQuirurgicas = "";
    private String enfermedadesCronicas = "";
    private String medicacion = "";
    private String diagnosticos = "";
    private String tratamientos = "";

    //====================================================== METODOS ==================================================================

    /**
     * Añade un elemento a uno de los apartados necesario para el historial médico con separadores '|'
     * 
     * @param campo String actual (ej: "Asma | Diabetes | ")
     * @param nuevo Elemento a añadir (ej: "Hipertension")
     * @return String actualizado (ej: "Asma | Diabetes | Hipertension | ")
     */
    private String agregarNuevoDato(String campo, String nuevo) {
        if (campo == null || campo.isEmpty()) {
            return nuevo + " | ";
        } else {
            return campo + nuevo + " | ";
        }
    }
    

    
    /**
     * Imprime todos los elementos de un String con separadores
     * Recorre el String extrayendo substrings entre |
     * 
     * @param texto String con formato "dato1 | dato2 | dato3 |"
     * @param titulo Título a mostrar antes de la lista
     */
    private void imprimirElementos(String texto, String titulo) {

        // Este será el titulo de que se decida utilizar para cada apartado necesario del historial

        System.out.println(titulo);
        
        if (texto == null || texto.isEmpty()) {
            System.out.println("  (Ninguno registrado)");
            return;
        }
        
        int inicio = 0;
        int fin = texto.indexOf("|"); // Según el formato que hemos decidido el elemento será hastas que aparezca el carácter ';
        // '
        boolean hayAlguno = false;
        
        while (fin != -1) {

            String parte = texto.substring(inicio, fin);

            if (!parte.isEmpty()) {
                System.out.println("  - " + parte);
                hayAlguno = true;
            }
            inicio = fin + 1;
            fin = texto.indexOf("|", inicio);
        }
        
        if (!hayAlguno) {
            System.out.println("  (Ninguno registrado)");
        }
    }
    

    //============================================== MÉTODOS PARA AÑADIR DATOS =========================================================
    
    public void agregarEnfermedadPrevia(String enfermedad) { 
        enfermedadesPrevias = agregarNuevoDato(enfermedadesPrevias, enfermedad); 
    }
    
    public void agregarAlergia(String alergia) { 
        alergias = agregarNuevoDato(alergias, alergia); 
    }
    
    public void agregarIntervencion(String intervencion) { 
        intervencionesQuirurgicas = agregarNuevoDato(intervencionesQuirurgicas, intervencion); 
    }
    
    public void agregarEnfermedadCronica(String enfermedad) { 
        enfermedadesCronicas = agregarNuevoDato(enfermedadesCronicas, enfermedad); 
    }
    
    public void agregarMedicacion(String medicamento) { 
        medicacion = agregarNuevoDato(medicacion, medicamento); 
    }
    
    public void agregarDiagnostico(String diagnostico) { 
        diagnosticos = agregarNuevoDato(diagnosticos, diagnostico); 
    }
    
    public void agregarTratamiento(String tratamiento) { 
        tratamientos = agregarNuevoDato(tratamientos, tratamiento); 
    }

    
    // MOSTRAR HISTORIAL 
    
    /**
     * Muestra todo el historial clínico de forma ordenada
     */
    public void mostrarHistorial() {
        imprimirElementos(enfermedadesPrevias, "--- ENFERMEDADES PREVIAS ---");
        imprimirElementos(alergias, "--- ALERGIAS ---");
        imprimirElementos(intervencionesQuirurgicas, "--- INTERVENCIONES QUIRÚRGICAS ---");
        imprimirElementos(enfermedadesCronicas, "--- ENFERMEDADES CRÓNICAS ---");
        imprimirElementos(medicacion, "--- MEDICACIÓN ---");
        imprimirElementos(diagnosticos, "--- DIAGNÓSTICOS ---");
        imprimirElementos(tratamientos, "--- TRATAMIENTOS ---");
        
    }
}