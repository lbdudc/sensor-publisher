export const generateProduct = async (engine, path, spec) => {
  try {
    await engine.generateProduct(path, spec);
  } catch (error) {
    console.error(error);
    throw new Error("Error generating product");
  }
};
