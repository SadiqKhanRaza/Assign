/**
 *
 * @author macbook
 */
public class Employee {
    String empId,name,salPerDay;
    double salary;

    public Employee(String empId, String name, String salPerDay, double salary) {
        this.empId = empId;
        this.name = name;
        this.salPerDay = salPerDay;
        this.salary = salary;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalPerDay() {
        return salPerDay;
    }

    public void setSalPerDay(String salPerDay) {
        this.salPerDay = salPerDay;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}