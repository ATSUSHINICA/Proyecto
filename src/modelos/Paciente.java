package modelos;
/*
===================================
|             REVISADA            |
===================================

*/

/**
 * Clase Paciente que hereda de Persona
 * Un paciente es una persona con datos de contacto y un historial médico
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 * @since 2026/04/16
 */

import java.io.Serializable;
import java.time.LocalDate;

public class Paciente extends Persona implements Serializable {

    private static final long serialVersionUID = 2L;
    
    private int numeroHistoriaClinica = 0; 
    private String direccion;
    private int numeroTelefono;            
    private String correoElectronico;
    private int telefonoEmergencia;           
    private String estado;      // Puede ser: ACTIVO, ALTA_MEDICA, BAJA_TEMPORAL, BAJA_PERMANENTE
    private boolean perteneceSistema;     // true = está en el sistema, false = no está
    
    
    /*
        COMPOSICIÓN: el paciente "tiene un" historial médico
        Si el paciente desaparece, su historial también (se borra con él)
        Por eso se crea aquí dentro y no fuera
     */
    private HistorialMedico historial;


    // ============================================== CONSTRUCTOR ============================================================ 

    /**
     * Constructor del paciente
     * 
     * @param dni DNI del paciente
     * @param nombreCompleto Nombre completo
     * @param fechaNacimiento Fecha de nacimiento
     * @param sexo Sexo
     * @param numeroHistoriaClinica Número de historia clínica (único)
     * @param direccion Dirección
     * @param numeroTelefono Teléfono
     * @param correoElectronico Email
     * @param telefonoEmergencia Teléfono de contacto de emergencia
     */

    public Paciente(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo, String direccion, int numeroTelefono, String correoElectronico, int telefonoEmergencia) {
        
        /*  Se coloca un contador, de esta forma a medida que van ingresando los pacientes en nuestro hospital se les asignara al número que se encuentre disponible por consecuencia */
        
        
        super(dni, nombreCompleto, fechaNacimiento, sexo);
        
        this.numeroHistoriaClinica++;
        this.direccion = validarDireccion(direccion);
        this.numeroTelefono = validarTelefono(numeroTelefono);
        this.correoElectronico = validarCorreo(correoElectronico);
        this.telefonoEmergencia = validarTelefono(telefonoEmergencia); // Reutilizamos el mismo método para el teléfono de emergencia
        this.estado = "ACTIVO";      // Al crearlo, empieza activo
        this.perteneceSistema = true;
        
        // Creo su historial médico vacío (luego se irá llenando)
        this.historial = new HistorialMedico();
    }

    //====================================== MÉTODOS DE VALIDACIÓN PRIVADOS =========================================
    /**
     * Este método valida si la dirección a sido introducida correctamente
     * @param direccion     dirección en donde reside el paciente
     * @return La dirección validada
     */
    private String validarDireccion(String direccion){
        
        if (direccion == null){
            throw new NullPointerException("Él campo de dirección no puede ser nula");
        }
        
        return direccion;
    }

    /**
     * Valida que un número de teléfono tenga 9 dígitos (formato español).
     * @param telefono Número de teléfono
     * @return El número validado
     */
    private int validarTelefono(int telefono) {
        /* Mediante valueOf convertiremos telefono de int a string y lo almacenaremos dentro de la variable creada llamada t
            de esta forma podremos medir la longitud de carácteres que posee.
        */

        
        String aux = String.valueOf(telefono);

        if (aux.length() != 9) {
            throw new IllegalArgumentException("El número de teléfono debe tener 9 dígitos");
        }

        boolean error = false; 

        /* De esta forma revisamos carácter a carácter el número de teléfono y comprobamos si es numérico*/
        
        for (int i = 0; i < aux.length(); i++){

            if(aux.charAt(i) < '0' || aux.charAt(i) > '9' ){
                error = true;
            }
        }

        // en caso de que exista un dato erroneo lanzará la excepción 
        if (error) {
            throw new IllegalArgumentException("El teléfono solo puede contener números");
        }

        return telefono;
    }

    /**
     * Valida el correo electrónico con un patrón básico.
     * @param correo Correo electrónico
     * @return El correo validado
     */
    private String validarCorreo(String correo) {
        if (correo == null) {
            throw new NullPointerException("El correo electrónico no puede estar vacío");
        }

        boolean error = true;

        /* Este bucle revisa de que se encuentre el caracter @ revisando uno por uno, en caso de aparecer no se ejecutará la excepción, asi nos aseguramos que es un correo*/

        for(int i = 0; i < correo.length(); i++){
            if(correo.substring(i, i+1).equals("@")){
                error = false;
            }
        }

        if (error) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido");
        }

        return correo;
    }

    /**
     * Valida el estado del paciente.
     * Estados permitidos: ACTIVO, ALTA_MEDICA, BAJA_TEMPORAL, BAJA_PERMANENTE
     * @param estado Estado del paciente
     * @return El estado validado
     */
    private String validarEstado(String estado) {
        if (estado == null) {
            throw new NullPointerException("El estado no puede ser nulo");
        }

        // En caso de cumplir con una de las opciones que se muestra devolvera el estado 

        switch (estado) {
            case "ACTIVO": return estado;
            case "ALTA_MEDICA": return estado; 
            case "BAJA_TEMPORAL": return estado;
            case "BAJA_PERMANENTE": return estado; 
    
            default:
                throw new IllegalArgumentException("Estado inválido: " + estado);
        }
    }


    //=========================================== MÉTODOS PÚBLICOS ==================================================
    
    /**
     * POLIMORFISMO: Sobrescribo el método mostrarDatos() de Persona
     * Muestro los datos de persona + los datos específicos de paciente
     */
    @Override
    public void mostrarDatos() {
        super.mostrarDatos();
        System.out.println("Historia Clínica Nº: " + numeroHistoriaClinica);
        System.out.println("Dirección: " + direccion);
        System.out.println("Teléfono: " + numeroTelefono);
        System.out.println("Email: " + correoElectronico);
        System.out.println("Tel. Emergencia: " + telefonoEmergencia);
        System.out.println("Estado: " + estado);
        System.out.println("Pertenece al sistema: " + (perteneceSistema ? "Sí" : "No"));
    }
    
    

    //============================================= SETTERS Y GETTERS ==================================================


    /* Setters  
        Reutilizamos la validaciones en caso de que se generen de cambios, de esta forma pasarán por el mismo control que cuando se crea el objeto
    */
    public void setDireccion(String direccion) { this.direccion = validarDireccion(direccion); }
    public void setNumeroTelefono(int numeroTelefono) { this.numeroTelefono = validarTelefono(numeroTelefono); }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = validarCorreo(correoElectronico); }
    public void setTelefonoEmergencia(int telefonoEmergencia) { this.telefonoEmergencia = validarTelefono(telefonoEmergencia); }
    public void setEstado(String estado) { this.estado = validarEstado(estado); }
    public void setActivo(boolean activo) { this.perteneceSistema = activo; }
    
    // Getters
    public int getNumeroHistoriaClinica() { return numeroHistoriaClinica; }  
    public String getDireccion() { return direccion; }
    public int getNumeroTelefono() { return numeroTelefono; }                
    public String getCorreoElectronico() { return correoElectronico; }
    public int getTelefonoEmergencia() { return telefonoEmergencia; }        
    public String getEstado() { return estado; }
    public boolean getPerteneceSistema() { return perteneceSistema; }
    public HistorialMedico getHistorial() { return historial; }
}
