/*% if (feature.UserManagement) { %*/
const store = {
  state: {
    user: {
      authorities: [],
      login: "",
      logged: false,
      langKey: "",
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      employee: null,
      /*% } %*/
      token: localStorage.getItem("token"),
    },
  },
};

export default store;
/*% } %*/
