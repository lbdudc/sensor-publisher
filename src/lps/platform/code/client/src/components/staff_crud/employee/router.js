/*% if (feature.MWM_PM_Employees) { %*/
import EmployeeList from "./components/EmployeeList";
import EmployeeForm from "./components/EmployeeForm";
import EmployeeDetail from "./components/EmployeeDetail";

const routes = [
  {
    path: "/staff/employees",
    name: "EmployeeList",
    meta: {
      label: "staff_crud.employeeList",
    },
    component: EmployeeList,
  },
  {
    path: "/staff/employees/:id/edit",
    name: "EmployeeForm",
    component: EmployeeForm,
    meta: {
      authority: "ROLE_ADMIN",

      label: "staff_crud.employeeForm",
    },
  },
  {
    path: "/staff/employees/new",
    name: "EmployeeCreate",
    component: EmployeeForm,
    meta: {
      authority: "ROLE_ADMIN",
      label: "staff_crud.employeeForm",
    },
  },
  {
    path: "/staff/employees/:id(\\d+)",
    name: "EmployeeDetail",
    meta: {
      label: "staff_crud.employeeDetail",
    },
    component: EmployeeDetail,
  },
];

export default routes;
/*% } %*/
