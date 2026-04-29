/**
 * Clase PersonalAdmin que hereda de Persona
 * El personal administrativo gestiona el registro, modificaciones y bajas de pacientes
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.time.LocalDate;

public final class PersonalAdmin extends Persona {
    
    private final int idEmpleado;  // int según diagrama actualizado
    
    /**
     * COLA (Queue) para pacientes en espera de registro
     * Implementación propia con array circular
     * Uso una cola porque el primero en llegar es el primero en ser atendido (FIFO)
     */
    private static ColaPacientes colaPacientesRegistro = new ColaPacientes(100);
    
    /**
     * PILA (Stack) para guardar el historial de acciones
     * Implementación propia con array
     * Uso una pila porque la última acción es la más relevante (LIFO)
     * Útil para posibles funciones de "deshacer"
     */
    private static PilaAcciones historialAcciones = new PilaAcciones(100);

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
        this.idEmpleado = idEmpleado;
    }

    /**
     * Añadir paciente a la cola de espera para registrar
     * 
     * @param paciente El paciente a registrar
     */
    public void registrarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new NullPointerException("El paciente no puede ser nulo");
        }
        
        // encolar() mete al paciente al final de la cola (mi implementación)
        colaPacientesRegistro.encolar(paciente);
        
        // apilar() guarda la acción en la pila (mi implementación)
        historialAcciones.apilar("Paciente registrado: " + paciente.getNumeroHistoriaClinica());
        
        System.out.println("Paciente " + paciente.getNombreCompleto() + " añadido a cola de registro");
        System.out.println("Pacientes en espera: " + colaPacientesRegistro.getTamano());
    }
    
    /**
     * Sacar al siguiente paciente de la cola para atenderlo
     * 
     * @return El paciente atendido, o null si no hay nadie
     */
    public Paciente atenderSiguientePaciente() {
        // desencolar() saca al primero de la cola y lo devuelve
        Paciente p = colaPacientesRegistro.desencolar();
        
        if (p == null) {
            System.out.println("No hay pacientes en cola de espera");
        } else {
            System.out.println("Atendiendo a: " + p.getNombreCompleto());
        }
        return p;
    }
    
    /**
     * Modificar los datos personales de un paciente
     * 
     * @param paciente El paciente a modificar
     * @param direccion Nueva dirección
     * @param telefono Nuevo teléfono
     * @param email Nuevo email
     * @param telefonoEmergencia Nuevo teléfono de emergencia
     */
    public void modificarDatosPaciente(Paciente paciente, String direccion, int telefono, String email, int telefonoEmergencia) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        // Uso los setters del paciente para cambiar sus datos
        paciente.setDireccion(direccion);
        paciente.setNumeroTelefono(telefono);
        paciente.setCorreoElectronico(email);
        paciente.setTelefonoEmergencia(telefonoEmergencia);
        
        // apilar() guarda esta acción en la pila
        historialAcciones.apilar("Datos modificados para: " + paciente.getNumeroHistoriaClinica());
        
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
        paciente.setActivo(false);  // Ya no está activo en el sistema
        historialAcciones.apilar("Alta médica: " + paciente.getNumeroHistoriaClinica());
        
        System.out.println("Paciente dado de ALTA médica");
    }
    
    /**
     * Dar de baja administrativa a un paciente (se va del hospital)
     * 
     * @param paciente El paciente a dar de baja
     * @param temporal true = baja temporal, false = baja permanente
     */
    public void darBajaPaciente(Paciente paciente, boolean temporal) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        if (temporal) {
            paciente.setEstado("BAJA_TEMPORAL");
            historialAcciones.apilar("Baja TEMPORAL: " + paciente.getNumeroHistoriaClinica());
            System.out.println("Baja TEMPORAL aplicada");
        } else {
            paciente.setEstado("BAJA_PERMANENTE");
            historialAcciones.apilar("Baja PERMANENTE: " + paciente.getNumeroHistoriaClinica());
            System.out.println("Baja PERMANENTE aplicada");
        }
        paciente.setActivo(false);
    }
    
    /**
     * Mostrar todo el historial de acciones (lo que hay en la pila)
     * Se muestran desde la más antigua a la más nueva
     */
    public static void mostrarHistorialAcciones() {
        System.out.println("\n=== HISTORIAL DE ACCIONES (PILA LIFO) ===");
        if (historialAcciones.estaVacia()) {
            System.out.println("No hay acciones registradas");
            return;
        }
        historialAcciones.mostrarTodas();
    }
    
    /**
     * Operación agregada: saber cuántos pacientes esperan en la cola
     * 
     * @return Número de pacientes en cola
     */
    public static int getTamanoCola() {
        return colaPacientesRegistro.getTamano();
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

// Implementación propia de la pila

/**
 * Clase Pila para guardar las acciones del historial
 * Implementación con array y cima (LIFO - Last In, First Out)
 */
class PilaAcciones {
    private static final int MAXIMO = 100;
    private String[] v;      // array para guardar los elementos
    private int cima;        // indica la posición del último elemento
    
    /**
     * Constructor con capacidad personalizada
     * @param capacidad Tamaño máximo de la pila
     */
    public PilaAcciones(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que 0");
        }
        v = new String[capacidad];
        cima = 0;  // La pila empieza vacía (cima = 0)
    }
    
    /**
     * Constructor por defecto (capacidad = 100)
     */
    public PilaAcciones() {
        this(MAXIMO);
    }
    
    /**
     * Apilar un elemento en la pila
     * @param elemento Elemento a guardar
     */
    public void apilar(String elemento) {
        if (cima == v.length) {
            System.out.println("Pila llena, no se puede guardar más acciones");
            return;
        }
        v[cima] = elemento;
        cima = cima + 1;
    }
    
    /**
     * Desapilar (sacar) el último elemento de la pila
     * @return El último elemento, o null si está vacía
     */
    public String desapilar() {
        if (estaVacia()) {
            return null;
        }
        cima = cima - 1;
        return v[cima];
    }
    
    /**
     * Ver el último elemento sin sacarlo
     * @return El último elemento
     */
    public String verTope() {
        if (estaVacia()) {
            return null;
        }
        return v[cima - 1];
    }
    
    /**
     * Comprobar si la pila está vacía
     * @return true si está vacía, false si no
     */
    public boolean estaVacia() {
        return cima == 0;
    }
    
    /**
     * Mostrar todos los elementos de la pila (desde el primero hasta el último)
     */
    public void mostrarTodas() {
        for (int i = 0; i < cima; i++) {
            System.out.println("  - " + v[i]);
        }
    }
    
    /**
     * Obtener el tamaño actual de la pila
     * @return Número de elementos
     */
    public int getTamano() {
        return cima;
    }
}

//Implementación propia de la cola circular

/**
 * Clase Cola para pacientes en espera de registro
 * Implementación con array circular (FIFO - First In, First Out)
 */
class ColaPacientes {
    private static final int MAXIMO = 100;
    private Paciente[] v;   // array para guardar los pacientes
    private int ini;        // índice del primer elemento (por donde se saca)
    private int fin;        // índice donde se añade el siguiente elemento
    
    /**
     * Constructor con capacidad personalizada
     * @param capacidad Tamaño máximo de la cola
     */
    public ColaPacientes(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que 0");
        }
        v = new Paciente[capacidad];
        ini = 0;
        fin = 0;
    }
    
    /**
     * Constructor por defecto (capacidad = 100)
     */
    public ColaPacientes() {
        this(MAXIMO);
    }
    
    /**
     * Encolar (añadir) un paciente al final de la cola
     * @param paciente Paciente a añadir
     */
    public void encolar(Paciente paciente) {
        if (estaLlena()) {
            System.out.println("Cola llena, no se pueden registrar más pacientes");
            return;
        }
        v[fin] = paciente;
        fin = fin + 1;
        // Si llegamos al final del array, volvemos al principio (circular)
        if (fin == v.length) {
            fin = 0;
        }
    }
    
    /**
     * Desencolar (sacar) el primer paciente de la cola
     * @return El primer paciente, o null si está vacía
     */
    public Paciente desencolar() {
        if (estaVacia()) {
            return null;
        }
        Paciente aux = v[ini];
        ini = ini + 1;
        // Si llegamos al final del array, volvemos al principio (circular)
        if (ini == v.length) {
            ini = 0;
        }
        return aux;
    }
    
    /**
     * Comprobar si la cola está vacía
     * @return true si está vacía, false si no
     */
    public boolean estaVacia() {
        return ini == fin;
    }
    
    /**
     * Comprobar si la cola está llena
     * @return true si está llena, false si no
     */
    public boolean estaLlena() {
        return ini == fin + 1;
    }
    
    /**
     * Obtener el tamaño actual de la cola
     * @return Número de pacientes en cola
     */
    public int getTamano() {
        if (ini <= fin) {
            return fin - ini;
        } else {
            return (v.length - ini) + fin;
        }
    }
    
    /**
     * Ver el primer paciente sin sacarlo
     * @return El primer paciente
     */
    public Paciente verPrimero() {
        if (estaVacia()) {
            return null;
        }
        return v[ini];
    }
}
