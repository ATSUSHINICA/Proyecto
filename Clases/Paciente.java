/**
 * Clase Paciente que hereda de Persona
 * Un paciente es una persona con datos de contacto y un historial médico
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.time.LocalDate;

public class Paciente extends Persona {
    
    private final int numeroHistoriaClinica; 
    private String direccion;
    private int numeroTelefono;            
    private String correoElectronico;
    private int telefonoEmergencia;           
    private String estado;      // Puede ser: ACTIVO, ALTA_MEDICA, BAJA_TEMPORAL, BAJA_PERMANENTE
    private boolean activo;     // true = está en el sistema, false = no está
    
    /**
     * COMPOSICIÓN: el paciente "tiene un" historial médico
     * Si el paciente desaparece, su historial también (se borra con él)
     * Por eso se crea aquí dentro y no fuera
     */
    private HistorialMedico historial;

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
    public Paciente(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo, int numeroHistoriaClinica, String direccion, int numeroTelefono, String correoElectronico, int telefonoEmergencia) {
        super(dni, nombreCompleto, fechaNacimiento, sexo);
        
        this.numeroHistoriaClinica = numeroHistoriaClinica;
        this.direccion = direccion;
        this.numeroTelefono = numeroTelefono;
        this.correoElectronico = correoElectronico;
        this.telefonoEmergencia = telefonoEmergencia;
        this.estado = "ACTIVO";      // Al crearlo, empieza activo
        this.activo = true;
        
        // Creo su historial médico vacío (luego se irá llenando)
        this.historial = new HistorialMedico();
    }
    
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
        System.out.println("Activo: " + (activo ? "Sí" : "No"));
    }
    
    // Setters (para poder modificar datos desde fuera)
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setNumeroTelefono(int numeroTelefono) { this.numeroTelefono = numeroTelefono; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public void setTelefonoEmergencia(int telefonoEmergencia) { this.telefonoEmergencia = telefonoEmergencia; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    // Getters
    public int getNumeroHistoriaClinica() { return numeroHistoriaClinica; }  // Retorna int
    public String getDireccion() { return direccion; }
    public int getNumeroTelefono() { return numeroTelefono; }                // Retorna int
    public String getCorreoElectronico() { return correoElectronico; }
    public int getTelefonoEmergencia() { return telefonoEmergencia; }        // Retorna int
    public String getEstado() { return estado; }
    public boolean isActivo() { return activo; }
    public HistorialMedico getHistorial() { return historial; }
}