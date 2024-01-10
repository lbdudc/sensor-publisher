/*% if (feature.MWM_PM_Clients) { %*/
import ClientList from "./components/ClientList";
import ClientForm from "./components/ClientForm";
import ClientDetail from "./components/ClientDetail";

const routes = [
  {
    path: "/staff/clients",
    name: "ClientList",
    meta: {
      label: "staff_crud.clientList"
    },
    component: ClientList,
  },
  {
    path: "/staff/clients/:id/edit",
    name: "ClientForm",
    component: ClientForm,
    meta: {
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      authority: 'ROLE_ADMIN',
      /*% } %*/
      label: "staff_crud.clientForm"
    }
  },
  {
    path: "/staff/clients/new",
    name: "ClientCreate",
    component: ClientForm,
    meta: {
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      authority: 'ROLE_ADMIN',
      /*% } %*/
      label: "staff_crud.clientForm"
    }
  },
  {
    path: "/staff/clients/:id(\\d+)",
    name: "ClientDetail",
    meta: {
      label: "staff_crud.clientDetail"
    },
    component: ClientDetail
  }
];

export default routes;
/*% } %*/
