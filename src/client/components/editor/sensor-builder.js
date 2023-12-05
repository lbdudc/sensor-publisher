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
      elastic: {
        uris: "http://localhost:9200",
        username: "elastic",
        password: "changeme",
      },
    };
  }

  setConfig(config) {
    this.config = config;
  }

  getDSLSpec() {
    const finalJson = this.dslSpec;

    finalJson.data.basicData.database = this.config.database;
    finalJson.data.basicData.elastic = this.config.elastic;

    return finalJson;
  }
}
