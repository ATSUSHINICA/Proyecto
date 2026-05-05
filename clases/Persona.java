/**
 * Creo la clase persona de forma abstracta ya que será la genérica del hospital y solo servirá para que la hereden
 * las clases PersonalAdmin, Medico y Paciente
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.time.LocalDate;

public abstract class Persona {
    protected final String dni;  //Clase final ya que es único de cada persona y no modificable
    protected String nombreCompleto;
    protected LocalDate fechaNacimiento;
    protected String sexo;

    public Persona(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo) {
        // Valido que el DNI tenga al menos 8 caracteres, si no lanzo un error
        if (dni == null || dni.length() < 8) {
            throw new IllegalArgumentException("DNI inválido: debe tener al menos 8 caracteres");
        }
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    // Método que pueden usar las clases hijas (y algunas lo van a modificar a su manera)
    public void mostrarDatos() {
        System.out.println("DNI: " + dni);
        System.out.println("Nombre: " + nombreCompleto);
        System.out.println("Fecha Nacimiento: " + fechaNacimiento.getDayOfMonth() + "/" + fechaNacimiento.getMonthValue() + "/" + fechaNacimiento.getYear());
        System.out.println("Sexo: " + sexo);
    }

    // Getters básicos para que otros puedan leer los datos
    public String getDni() { return dni; }
    public String getNombreCompleto() { return nombreCompleto; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
}