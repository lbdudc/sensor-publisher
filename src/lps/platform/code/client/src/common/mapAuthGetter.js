/*% if (feature.UserManagement) { %*/
import store from "@/common/store";

const isAdmin = () => {
  return store.state.user.authorities.indexOf("ROLE_ADMIN") != -1;
}

const isLogged = () => {
  return store.state.user.logged;
}

const isEmployee = () => {
  return store.state.user.employee != null;
}

export function mapAuthGetter(options) {
  const object = {};
  options.forEach(option => {
    object[option] = eval(option);
  });
  return object;
}
/*% } %*/
