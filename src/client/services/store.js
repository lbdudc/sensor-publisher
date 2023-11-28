const store = {
  auth: {
    user: null,
    logged: false,
  },
  navigation: {
    pageTitle: "", // se cambia en cada componente para que lo lea la barra
    thereIsPageBeforeLogin: true, // si no hay página antes de login, se controla y se redirige a home
  },
};

export default store;
