/*% if (feature.UserManagement) { %*/
import store from "./store";
import i18n from "@/plugins/i18n";

import RepositoryFactory from "@/repositories/RepositoryFactory";
const AccountRepository = RepositoryFactory.get("AccountRepository");

const user = store.state.user;

function login(credentials) {
  return AccountRepository.login(credentials).then(response => {
    _saveToken(response.token);
    _authenticate();
  });
}

function logout() {
  return new Promise(res => {
    _removeToken();
    user.login = "";
    user.authorities = [];
    user.logged = false;
    user.langKey = "";
    user.employee = null;
    user.token = null;
    res(true);
  });
}

function getUser() {
  return user;
}

function getToken() {
  return localStorage.getItem("token");
}

function _saveUserData(data) {
  user.login = data.login;
  user.authorities = data.authorities;
  user.logged = true;
  user.employee = data.employee;
  user.email = data.email;
  user.firstName = data.firstName;
  user.lastName = data.lastName;

  // Setting the user's language to the interface
  if (user.langKey !== data.langKey.toUpperCase()) {
    i18n.locale = data.langKey.toUpperCase();
    localStorage.setItem("lang", data.langKey.toUpperCase());
  }
  user.langKey = data.langKey.toUpperCase();

  user.token = getToken();
}

// usamos localStorage para guardar el token, de forma
// que sea persistente (se inhabilita con el tiempo o
// al hacer logout)
function _saveToken(token) {
  user.token = token;
  return localStorage.setItem("token", token);
}

function _removeToken() {
  localStorage.removeItem("token");
}

// si tenemos el token guardado, esta petición se hará
// con el filtro definido en http-common y por tanto nos
// devolverá el usuario logueado
function _authenticate() {
  return AccountRepository.getUser().then(response => {
    _saveUserData(response);
    return user;
  });
}

// este método devuelve una promesa que se resuelve cuando
// se haya comprobado si el token, de existir, es válido o no
function isAuthenticationChecked() {
  return new Promise(res => {
    if (getToken()) {
      _authenticate()
        .catch(() => logout())
        .finally(() => res(true));
    } else {
      res(true);
    }
  });
}

function changeUserLang(newLang) {
  if (getToken()) {
    AccountRepository.changeLanguage(user.login, newLang)
  }
  localStorage.setItem("lang", newLang);
}

export default {
  getUser,
  login,
  logout,
  getToken,
  isAuthenticationChecked,
  changeUserLang,
};
/*% } %*/
