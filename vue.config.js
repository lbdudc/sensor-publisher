const path = require("path");

module.exports = {
  chainWebpack: (config) => {
    config.entry("app").clear().add("./src/client/main.js").end();
    config.resolve.alias.set("@", path.join(__dirname, "./src/client"));
    config.module.rule("svg").uses.clear();
    config.module
      .rule("svg")
      .use("svg-inline-loader")
      .loader("svg-inline-loader");
  },

  configureWebpack: {
    devtool: "source-map",
    plugins: [],
  },
};
