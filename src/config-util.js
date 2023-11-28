export const setDatabaseConfig = (config) => {
  const databaseConfig = {
    host: config?.database?.host || "localhost",
    database: config?.database?.database || "postgres",
    username: config?.database?.username || "postgres",
    password: config?.database?.password || "postgres",
  };
  return databaseConfig;
}

export const setElasticsearchConfig = (config) => {
  const elasticsearchConfig = {
    uris: config?.elasticsearch?.uris || "http://localhost:9200",
    username: config?.elasticsearch?.username || "elastic",
    password: config?.elasticsearch?.password || "changeme",
  };
  return elasticsearchConfig;
}

export const setFeaturesToProduct = (features, product) => {
  const finalProduct = product;

  finalProduct.features = features;
  return finalProduct;
}
