package modelos;
/*
===================================
|             REVISADA            |
===================================

*/


/**
 * Creo la clase persona de tipo abstract, ya que será la que herede los datos que comparten en común
 * las clases PersonalAdmin, Medico y Paciente
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 * @since 2026/04/16
 */

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String dni;  //Clase final ya que es único de cada persona y no modificable
    private String nombreCompleto;
    private final LocalDate fechaNacimiento;
    private String sexo;
    
    
    //===================================== CONSTRUCTOR ============================================== 

    public Persona(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo) {

        this.dni = validarDni(dni);
        this.nombreCompleto = validarNombreCompleto(nombreCompleto);
        this.fechaNacimiento = validarFechaNacimiento(fechaNacimiento);
        this.sexo = validarSexo(sexo);
    }

    //============================== MÉTODOS DE VALIDACIÓN PRIVADOS ==============================

    /**
     * Valida que el DNI cumpla con el formato correcto: 8 dígitos numéricos + 1 letra mayúscula
     * @param dni
     * @return dni en mayúsculas si es válido
     */
    private String validarDni(String dni) {

        if (dni == null) {
            throw new NullPointerException("El DNI no puede ser nulo");
        }

        if (dni.length() != 9) {
            throw new IllegalArgumentException("Error de longitud al introducir el DNI");
        }

        dni = dni.toUpperCase(); // Convertimos la letra del dni final en mayúscula

        /* Esta excepción es para que cumpla que el último carácter sea una letra */
        if (dni.charAt(8) < 'A' || dni.charAt(8) > 'Z') {
            throw new IllegalArgumentException("DNI inválido debido al último carácter");
        }

        /* Esta excepción valida que los primeros 8 dígitos son caracteres numéricos */
        for (int i = 0; i < 8; i++) {
            if (dni.charAt(i) < '0' || dni.charAt(i) > '9') {
                throw new IllegalArgumentException("Los primeros 8 caracteres no son numéricos");
            }
        }

        return dni;
    }

    /**
     * Valida que el nombre completo no sea nulo ni esté vacío
     * @param nombreCompleto
     * @return nombreComp si es válido
     */
    private String validarNombreCompleto(String nombreCompleto) {

        /* Esta excepción evita que el nombre sea nulo o esté vacío */
        if (nombreCompleto == null ) {
            throw new NullPointerException("El nombre no puede ser nulo");
        }

        return nombreCompleto;
    }

    /**
     * Valida que la fecha de nacimiento no sea nula ni futura
     * @param fechaNacimiento
     * @return fechaNaci si es válida
     */
    private LocalDate validarFechaNacimiento(LocalDate fechaNacimiento) {

        /* Esta excepción evita que la fecha de nacimiento sea nula */
        if (fechaNacimiento == null) {
            throw new NullPointerException("La fecha de nacimiento no puede ser nula");
        }

        /* Esta excepción no permite colocar fechas que sean futuras */
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        return fechaNacimiento;
    }

    /**
     * Valida que el sexo sea "M" o "F" y tenga longitud 1
     * @param sexo
     * @return sexo en mayúsculas si es válido
     */
    private String validarSexo(String sexo) {

        /* Esta excepción evita que el sexo sea nulo */
        if (sexo == null) {
            throw new IllegalArgumentException("El dato de sexo no puede ser nulo");
        }

        /* Esta excepción comprueba que solo se ha introducido un dígito como longitud de texto */
        if (sexo.length() != 1) {
            throw new IllegalArgumentException("El tamaño de texto no corresponde con lo pedido");
        }

        sexo = sexo.toUpperCase(); // El dígito sexo solo puede ser masculino (M) o femenino (F)

        /* Esta excepción verifica que se ha utilizado el carácter "M" o "F" */
        if (sexo.charAt(0) != 'M' && sexo.charAt(0) != 'F') {
            throw new IllegalArgumentException("El sexo introducido es inválido, debe ser M o F");
        }

        return sexo;
    }

    //========================================= MÉTODOS PÚBLICOS =============================================== 

    /**
     * Este método muesta todos los datos de cada persona que se encuentra dentro 
     * del sistema.
     */

    public void mostrarDatos() {
        System.out.println("DNI: " + dni);
        System.out.println("Nombre: " + nombreCompleto);
        System.out.println("Fecha Nacimiento: " + fechaNacimiento.getDayOfMonth() + "/" + fechaNacimiento.getMonthValue() + "/" + fechaNacimiento.getYear());
        System.out.println("Sexo: " + sexo);
    }

    //======================================= SETTERS Y GETTERS =======================================

    // Getters 
    public String getDni() { return dni; }
    public String getNombreCompleto() { return nombreCompleto; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }


    /* Setters
        No se añaden los setters de Fecha de nacimiento y DNI ya que son dos datos que no deberían de modificarse por ninguna circunstancia.

        Se reutilizan las validaciones, de esta forma en caso de realizar las modificaciones pasen por el mismo control que al crear el objeto
    */

    public void setNombreCompleto(String nombreCompleto){this.nombreCompleto = validarNombreCompleto(nombreCompleto);}
    public void setSexo(String sexo){this.sexo = validarSexo(sexo);}
}