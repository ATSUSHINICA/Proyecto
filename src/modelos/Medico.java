package modelos;
/*
===================================
|             REVISADA            |
===================================

*/


/**
 * Clase Medico que hereda de Persona
 * Un médico es una persona con atributos específicos: número de colegiado y especialidad
 * 
 * @author: Laura Leciñena, Alejandro Díaz 
 * @since 2026/04/16
 */

import java.io.Serializable;
import java.time.LocalDate;

public final class Medico extends Persona implements Serializable {

    private static final long serialVersionUID = 3L;
    
    private final int numeroColegiado;  // int en lugar de String (según diagrama actualizado)
    private String especialidad;
    

    //===================================== CONSTRUCTOR ===================================================
    /**
     * Constructor del médico
     * Llama al constructor de Persona con super() para heredar dni, nombre, fecha y sexo
     * Luego asigna los atributos propios del médico
     * 
     * @param dni DNI del médico
     * @param nombreCompleto Nombre completo
     * @param fechaNacimiento Fecha de nacimiento (LocalDate)
     * @param sexo Sexo del médico
     * @param numeroColegiado Número de colegiado (único e inmutable)
     * @param especialidad Especialidad médica (Cardiología, Pediatría, etc.)
     */
    public Medico(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo, int numeroColegiado, String especialidad) {

        // Llamo al constructor de la clase padre (Persona)

        super(dni, nombreCompleto, fechaNacimiento, sexo);
        this.numeroColegiado = validarNumeroColegiado(numeroColegiado);
        this.especialidad = validarEspecialidad(especialidad);
    }

    //=========================================== METODOS DE VALIDACIÓN PRIVADOS =======================================
    
    /**
     * Valida que el número de colegiado tenga 9 dígitos y solo contenga números.
     * @param numeroColegiado Número de colegiado
     * @return El número validado
     */

    private int validarNumeroColegiado(int numeroColegiado) {

        // Convertimos a String para poder analizarlo carácter a carácter
        String aux = String.valueOf(numeroColegiado);

        // Debe tener exactamente 9 dígitos
        if (aux.length() != 9) {
            throw new IllegalArgumentException("El número de colegiado debe tener 9 dígitos");
        }

        // Comprobamos que todos los caracteres sean numéricos
        for (int i = 0; i < aux.length(); i++) {

            if (aux.charAt(i) < '0' || aux.charAt(i) > '9') {
                throw new IllegalArgumentException("El número de colegiado solo puede contener números");
            }
        }

        return numeroColegiado;
    }

    /**
     * Valida la especialidad del médico.
     * No puede ser nula, ni vacía, ni contener solo espacios.
     * @param especialidad Especialidad médica
     * @return La especialidad validada
     */
    private String validarEspecialidad(String especialidad) {

        if (especialidad == null) {
            throw new NullPointerException("La especialidad no puede ser nula");
        }

        if (especialidad.length() == 0 ) {
            throw new IllegalArgumentException("La especialidad no puede estar vacía");
        }

        return especialidad;
    }



    //======================================== MÉTODOS PÚBLICOS =========================================================
    
    /**
     * Consultar el historial clínico completo de un paciente
     * 
     * @param paciente El paciente a consultar
     */

    public void consultarHistorial(Paciente paciente) {
        // Valido que el paciente no sea nulo
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        System.out.println("\n=== HISTORIAL CLÍNICO COMPLETO ===");
        System.out.println("Médico: " + this.getNombreCompleto() + " (" + this.especialidad + ")");
        System.out.println("Paciente: " + paciente.getNombreCompleto());
        System.out.println("Historia Clínica Nº: " + paciente.getNumeroHistoriaClinica());
        
        // El paciente tiene su propio historial (composición), lo muestro
        paciente.getHistorial().mostrarHistorial();
    }
    
    /**
     * Añadir un diagnóstico al historial del paciente
     * 
     * @param paciente El paciente a diagnosticar
     * @param diagnostico El diagnóstico a añadir
     */

    public void añadirDiagnostico(Paciente paciente, String diagnostico) {
        // Valido que los datos no sean nulos o vacíos
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (diagnostico == null) {
            throw new IllegalArgumentException("El diagnóstico no puede estar vacío");
        }
        
        // Añado el diagnóstico al historial del paciente
        paciente.getHistorial().agregarDiagnostico(diagnostico);
        System.out.println("Diagnóstico añadido: " + diagnostico);
        
    }
    
    /**
     * Añadir un tratamiento al historial del paciente
     * 
     * @param paciente El paciente a tratar
     * @param tratamiento El tratamiento a añadir
     */
    public void añadirTratamiento(Paciente paciente, String tratamiento) {
        // Valido los datos
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (tratamiento == null) {
            throw new IllegalArgumentException("El tratamiento no puede estar vacío");
        }
        
        // Añado el tratamiento al historial del paciente
        paciente.getHistorial().agregarTratamiento(tratamiento);
        System.out.println("Tratamiento añadido: " + tratamiento);
    }

    /**
     * Sobrescribo el método mostrarDatos() de Persona
     * Muestro los datos de persona + los datos específicos de médico
     */
    @Override
    public void mostrarDatos() {
        super.mostrarDatos();  // Primero muestro los datos de Persona
        System.out.println("Número Colegiado: " + numeroColegiado);
        System.out.println("Especialidad: " + especialidad);
    }
    
    //============================================= GETTERS Y SETTERS ==========================================

    // Getters
    public int getNumeroColegiado() { return numeroColegiado; }  
    public String getEspecialidad() { return especialidad; }
}
