import engineStore from "./engineStore";
import modelTransformation from "./transformation";

const customization = {
  customizing: false,
  data: {},
  startCustomization,
  endCustomization,
  getProperties,
  getForms,
  cleanJson,
};

function startCustomization() {
  customization.customizing = true;
  customization.data = modelTransformation(engineStore.getSpec());
}

function endCustomization() {
  customization.customizing = false;
}

/**
 * Devuelve las propiedades de una entidad
 * @param {nombre de la entidad} entityName
 */
function getProperties(entityName) {
  const entity = this.data.data.dataModel.entities.find(
    (el) => el.name == entityName
  );
  return entity.properties;
}

/**
 * Devuelve los formularios de una entidad
 * @param {nombre de la entidad} entityName
 */
function getForms(entityName) {
  return this.data.data.forms.filter((el) => el.entity === entityName);
}

/**
 * Formatea el JSON limpiando datos inncecesarios: se usará antes de la exportación/generacion
 */
function cleanJson() {
  var formated = {};
  Object.assign(formated, this.data);

  // Formateo de los formularios
  formated.data.forms.forEach((form) => {
    form.properties.forEach(function (element, index, theArray) {
      var newProp = {
        property: theArray[index].property,
        viewing: theArray[index].viewing,
        editing: theArray[index].editing,
      };
      theArray[index] = newProp;
      // Object.assign(prop, newProp);
    });
  });

  // Formateo de las listas
  formated.data.lists.forEach((element) => {
    element.properties.forEach(function (element, index, theArray) {
      var newProp = {};
      if (theArray[index].form) {
        newProp = {
          property: theArray[index].property,
          form: theArray[index].form,
        };
      } else {
        newProp = {
          property: theArray[index].property,
        };
      }

      theArray[index] = newProp;
    });
  });

  return formated;
}

export default customization;
