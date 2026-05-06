package modelos;

/*
===================================
|            REVISADO             |
===================================

*/

/**
 * Clase PersonalAdmin que hereda de Persona
 * El personal administrativo gestiona el registro, modificaciones y bajas de pacientes
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.io.Serializable;
import java.time.LocalDate;

public final class PersonalAdmin extends Persona implements Serializable {

    private static final long serialVersionUID = 5L;
    
    private final int idEmpleado; // int según diagrama actualizado
    
    
    //================================ CONSTRUCTOR ==============================================

    /**
     * Constructor del Personal Administrativo
     * 
     * @param dni DNI del administrativo
     * @param nombreCompleto Nombre completo
     * @param fechaNacimiento Fecha de nacimiento (LocalDate)
     * @param sexo Sexo
     * @param idEmpleado ID único de empleado
     */
    public PersonalAdmin(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo, int idEmpleado) {
        super(dni, nombreCompleto, fechaNacimiento, sexo);
        this.idEmpleado = validarIdEmpleado(idEmpleado);
    }

    
    //============================== METODOS DE VALIDACIÓN PRIVADOS =========================================
    
    /**
     * Este método validará si el código del empleado introducido es correctamente válido 
     * 
     * @param idEmpleado    Número del empleado implementado correctamente  
     */

    private int validarIdEmpleado(int idEmpleado) {
        
        // Convertimos el número del empleado en una variable auxuliar de tipo String para verficar su longitud, carácteres etc 
        String aux = String.valueOf(idEmpleado);

        // utilizamos el método isEmpty para verificar si el String esta vacío

        if (aux == null|| aux.isEmpty()) {
            throw new IllegalArgumentException("El código del empleado no puede estar vacío");
        }

        boolean error = false;

        for (int i = 0; i < aux.length(); i++){

            if(aux.charAt(i) < '0' || aux.charAt(i) > '9' ){
                error = true;
            }
        }

        // en caso de que exista un dato erroneo lanzará la excepción 
        if (error) {
            throw new IllegalArgumentException("El código del empleado solo puede contener números");
        }

        return idEmpleado;
    }
    
    //===================================== METODOS PUBLICOS ==============================================
    
    
    /**
     * Proceso para añadir un nuevo paciente a nuestro sistema 
     * 
     * @param paciente El paciente a registrar
     */
    public void registrarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new NullPointerException("El paciente no puede ser nulo");
        }

        System.out.println("Paciente " + paciente.getNombreCompleto() + " registrado correctamente en el sistema");

    }
    
    /**
     * Modificar los datos personales de un paciente
     * 
     * @param nombreCompleto El paciente a modificar
     * @param sexo El género del paciente 
     */
    public void modificarDatosPersonalesPaciente(Paciente paciente, String nombreCompleto, String sexo) {

        // validar que el paciente no es nulo 
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        // Uso los setters del paciente para cambiar sus datos PERSONALES

        paciente.setNombreCompleto(nombreCompleto);
        paciente.setSexo(sexo);
        
        System.out.println("Datos del paciente actualizados correctamente");
    }
    
    /**
     * Dar el alta médica a un paciente (ya no está en tratamiento)
     * 
     * @param paciente El paciente a dar de alta
     */
    public void darAltaPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        paciente.setEstado("ALTA_MEDICA");
        
        System.out.println("Paciente dado de ALTA médica");
    }
    
    /**
     * Dar de baja administrativa a un paciente (se va del hospital)
     * 
     * @param paciente El paciente a dar de baja
     * @param temporal true = baja temporal, false = baja permanente
     */
    public void darBajaPermanentePaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
            paciente.setEstado("BAJA_PERMANENTE");
            System.out.println("Baja PERMANENTE aplicada");

        paciente.setActivo(false);
    }
    
        /**
     * Dar de baja administrativa a un paciente (se va del hospital)
     * 
     * @param paciente El paciente a dar de baja
     * @param temporal true = baja temporal, false = baja permanente
     */
    public void darBajatemporalPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }

            paciente.setEstado("BAJA_TEMPORAL");
            System.out.println("Baja TEMPORAL aplicada");
        
        paciente.setActivo(true);
    }
    /**
     * Sobrescribo el método mostrarDatos() de Persona
     */
    @Override
    public void mostrarDatos() {
        super.mostrarDatos();
        System.out.println("ID Empleado: " + idEmpleado);
    }
    
    // Getters
    public int getIdEmpleado() { return idEmpleado; }
}
