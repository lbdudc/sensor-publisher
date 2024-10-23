export default class SensorBuilder {
  constructor(dslSpec, config) {
    this.dslSpec = dslSpec;

    this.config = config || {
      database: {
        host: "localhost",
        database: "postgres",
        username: "postgres",
        password: "postgres",
      },
    };
  }

  setConfig(config) {
    this.config = config;
  }

  getDSLSpec() {
    const finalJson = this.dslSpec;

    finalJson.data.basicData.database = this.config.database;

    return finalJson;
  }
}
