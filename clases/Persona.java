/**
 * Esta clase abstracta corresponde a Persona en donde se introducirán todos aquellos datos 
 * que tengan relación en común con las entidades que pertenezcan a este sistema de gestión clínica 
 * @author José Alejandro Díaz Delgado 
 * @since 2026/04/09
 */
import java.time.LocalDateTime;

public abstract class Persona {

    private final String dni;
    private String nombreComp;
    private LocalDateTime fechaNaci;
    private String sexo;

    /**
     * Este es el método constructor que utilizarán las clases hijas para poder instanciar objetos 
     * @param dni 
     * @param nombreComp            // Nombre Completo de la persona
     * @param fechaNaci             // Fecha de nacimiento de la persona 
     * @param sexo
     */
    public Persona(String dni, String nombreComp, LocalDateTime fechaNaci, String sexo) {
        this.dni        = validarDni(dni);
        this.nombreComp = validarNombreComp(nombreComp);
        this.fechaNaci  = validarFechaNaci(fechaNaci);
        this.sexo       = validarSexo(sexo);
    }

    //============================== MÉTODOS DE VALIDACIÓN PRIVADOS ==============================

    /**
     * Valida que el DNI cumpla con el formato correcto: 8 dígitos numéricos + 1 letra mayúscula
     * @param dni
     * @return dni en mayúsculas si es válido
     */
    private String validarDni(String dni) {

        if (dni == null) {
            throw new IllegalArgumentException("El DNI no puede ser nulo");
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
     * @param nombreComp
     * @return nombreComp si es válido
     */
    private String validarNombreComp(String nombreComp) {

        /* Esta excepción evita que el nombre sea nulo o esté vacío */
        if (nombreComp == null || nombreComp.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni estar vacío");
        }

        return nombreComp;
    }

    /**
     * Valida que la fecha de nacimiento no sea nula ni futura
     * @param fechaNaci
     * @return fechaNaci si es válida
     */
    private LocalDateTime validarFechaNaci(LocalDateTime fechaNaci) {

        /* Esta excepción evita que la fecha de nacimiento sea nula */
        if (fechaNaci == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }

        /* Esta excepción no permite colocar fechas que sean futuras */
        if (fechaNaci.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        return fechaNaci;
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

    //============================== MÉTODOS ==============================

    /**
     * Este método se encargará de mostrar todos los datos personales de cada Persona 
     */
    public void mostrarDatos() {
        System.out.println("====================");
        System.out.println("| DATOS PERSONALES |");
        System.out.println("====================");
        System.out.println();
        System.out.println("DNI: "              + getDni());
        System.out.println("Nombre Completo: "  + getNombreComp());
        System.out.println("Fecha Nacimiento: " + getFechaNaci());
        System.out.println("Sexo: "             + getSexo());
    }

    //============================== GETTERS ==============================

    public String getDni()                { return dni;       }
    public String getNombreComp()         { return nombreComp; }
    public LocalDateTime getFechaNaci()   { return fechaNaci;  }
    public String getSexo()              { return sexo;       }

    //============================== SETTERS ==============================

    /*
     * El personal administrativo puede modificar los datos personales en caso de que sea 
     * necesario, por ese motivo se realizan los setters de esos atributos que lo conforman.
     * Al reutilizar los métodos de validación privados, no se repite ninguna lógica.
     */

    public void setNombreComp(String nombreComp) {
        this.nombreComp = validarNombreComp(nombreComp);
    }

    public void setFechaNaci(LocalDateTime fechaNaci) {
        this.fechaNaci = validarFechaNaci(fechaNaci);
    }

    public void setSexo(String sexo) {
        this.sexo = validarSexo(sexo);
    }
}