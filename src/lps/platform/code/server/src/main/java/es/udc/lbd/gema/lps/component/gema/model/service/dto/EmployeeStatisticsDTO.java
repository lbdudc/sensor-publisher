/*% if (feature.MWM_TE_VisitsRecord) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;

public class EmployeeStatisticsDTO {

  private EmployeeDTO employee;
  private Long visitsNumber;

  public EmployeeStatisticsDTO() {}

  public EmployeeStatisticsDTO(EmployeeDTO employee, Long visitsNumber) {
    this.employee = employee;
    this.visitsNumber = visitsNumber;
  }

  public EmployeeStatisticsDTO(Employee employee, Long visitsNumber) {
    this.employee = new EmployeeDTO(employee);
    this.visitsNumber = visitsNumber;
  }

  public EmployeeDTO getEmployee() {
    return employee;
  }

  public void setEmployee(EmployeeDTO employee) {
    this.employee = employee;
  }

  public Long getVisitsNumber() {
    return visitsNumber;
  }

  public void setVisitsNumber(Long visitsNumber) {
    this.visitsNumber = visitsNumber;
  }
}
/*% } %*/