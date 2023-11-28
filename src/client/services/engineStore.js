// import DerivationEngine from "spl-js-engine/dist/derivation-engine";
// import JSZip from "jszip";
// import Vue from "vue";
// import featureLabel from "./feature-label";
// import defaultSpec from "@/default.json";

// const store = {
//   engine: null,
//   rootFeature: {},
//   basicData: {
//     name: "",
//     version: "",
//     database: {},
//     extra: {},
//     languages: ["en", "es", "gl"],
//     SRID: "4326",
//   },
//   dataModel: {
//     cells: [],
//   },
//   mapViewer: { maps: [], layers: [], styles: [] },
//   statics: [],
//   loaded: false,
//   selection: [],
//   error: "",
//   errorFeature: "",
//   init: _checkInit(),
//   selects,
//   deselects,
//   load,
//   getSpec,
//   setBasicData,
//   resetSpec,
//   addStaticPage,
//   syncSelection,
// };

// function load(json, loadBasicData = true) {
//   if (loadBasicData) {
//     store.basicData.name = json.basicData.name;
//     store.basicData.version = json.basicData.version;
//   }
//   store.basicData.database = json.basicData.database || {};
//   store.basicData.extra = json.basicData.extra || {};
//   store.basicData.SRID = json.basicData.SRID || "4326"; // Default value
//   if (!json.basicData.languages?.length > 0) {
//     json.basicData.languages = ["en", "es", "gl"]; // Default values
//   }
//   store.basicData.languages = json.basicData.languages;

//   store.dataModel = json.dataModel;
//   store.statics = json.statics || [];
//   store.mapViewer = json.mapViewer || {};

//   deselects(store.rootFeature);
//   const errors = [];
//   json.features.forEach((f) => {
//     try {
//       store.engine.featureModel.get(f);
//       _addSelection(f, store.selection);
//     } catch (err) {
//       console.error(err);
//       errors.push(err.message);
//     }
//   });
//   if (errors.length) {
//     Vue.notify({
//       text:
//         "There were errors loading the feature selection. Check the feature model. Details in the console:<br/><br/>" +
//         errors.join(",<br/>"),
//       type: "error",
//       duration: -1,
//     });
//   }
//   syncSelection();
//   store.loaded = true;
// }

// function getSpec() {
//   return {
//     basicData: store.basicData,
//     dataModel: store.dataModel,
//     features: store.selection,
//     mapViewer: store.mapViewer,
//     statics: store.statics,
//   };
// }

// function setBasicData(name, version) {
//   store.basicData.name = name;
//   store.basicData.version = version;
// }

// function resetSpec() {
//   load(defaultSpec);
//   store.loaded = false;
// }

// function _checkInit() {
//   return fetch("/platform.zip")
//     .then((data) => JSZip.loadAsync(data.arrayBuffer()))
//     .then((zip) => new DerivationEngine({ zip: zip }))
//     .then((engine) => (store.engine = engine))
//     .then(() => (store.rootFeature = store.engine.featureModel.getFeatures()))
//     .then(() => _initFeature(store.rootFeature, null));
// }

// function selects(feature) {
//   _selects(feature);
//   syncSelection();
// }

// function deselects(feature) {
//   _deselects(feature);
//   syncSelection();
// }

// function addStaticPage(static_page) {
//   store.statics.push(static_page);
// }

// function _initFeature(feature, parent) {
//   // creating properties required for the UI
//   Vue.set(feature, "expanded", false);
//   Vue.set(feature, "selected", false);
//   // setting parent relationships by name to avoid infinite recursion
//   feature.parent = parent ? parent.name : null;
//   // init child features recursively
//   if (feature.features) {
//     feature.features.forEach((f) => _initFeature(f, feature));
//   }
// }

// function _selects(feature, selection = store.selection) {
//   feature.selected = true;
//   _selectsXOR(feature);
//   if (feature.features) {
//     feature.expanded = true;
//   }
//   _addSelection(feature.name, selection);
// }

// function _deselects(feature, selection = store.selection) {
//   feature.selected = false;
//   if (feature.features) {
//     feature.features.forEach((f) => _deselects(f));
//   }
//   _deleteSelection(feature.name, selection);
// }

// function _handleSelectionError(ex) {
//   let featureName, semantic;
//   switch (ex.errorType) {
//     case "MISSING_CHILD":
//       featureName = featureLabel[ex.featureName]
//         ? featureLabel[ex.featureName].label
//         : ex.featureName;
//       semantic = ex.featureType == "XOR" ? "one" : "at least one";
//       store.error = `'${ex.featureType}' feature with label '${featureName}' requires ${semantic} child selected`;
//       store.errorFeature = ex.featureName;
//       break;
//     case "TOO_MANY_CHILDS":
//       featureName = featureLabel[ex.featureName]
//         ? featureLabel[ex.featureName].label
//         : ex.featureName;
//       store.error = `'${ex.featureType}' feature with label '${featureName}' requires only one child selected`;
//       store.errorFeature = ex.featureName;
//       break;
//     default:
//       store.error = ex.message;
//       console.error(ex);
//       break;
//   }
// }

// function syncSelection() {
//   _deselectsHidden(store.rootFeature);

//   try {
//     store.error = "";
//     store.errorFeature = "";
//     store.engine.featureModel
//       .completeFeatureSelection(Array.from(store.selection))
//       .forEach((fName) => {
//         _addSelection(fName, store.selection);
//       });
//   } catch (e) {
//     if (Array.isArray(e.featureSelection)) {
//       e.featureSelection.forEach((fName) => {
//         _addSelection(fName, store.selection);
//       });
//       _handleSelectionError(e);
//     } else {
//       store.error = e;
//       throw e;
//     }
//   }

//   _updateSelectedFeaturesFromSelection(store.rootFeature);
// }

// /**
//  * Removes all sibling features for XOR features
//  *
//  * @param {Feature} feature
//  */
// function _selectsXOR(feature) {
//   const parent = _getParent(feature);
//   if (parent && parent.type == "XOR") {
//     parent.features.filter((f) => f != feature).forEach((f) => _deselects(f));
//   }
// }

// function _deselectsHidden(feature, selection = store.selection) {
//   if (feature.hidden) {
//     feature.selected = false;
//   }
//   if (feature.features) {
//     feature.features.forEach((f) => _deselectsHidden(f));
//   }
//   if (feature.hidden) {
//     _deleteSelection(feature.name, selection);
//   }
// }

// function _findFeature(fName, feature = store.rootFeature) {
//   if (feature.name == fName) return feature;
//   let aux, f;
//   if (feature.features) {
//     for (f of feature.features) {
//       if ((aux = _findFeature(fName, f))) {
//         return aux;
//       }
//     }
//   }
//   return null;
// }

// function _getParent(feature) {
//   return feature.parent ? _findFeature(feature.parent) : null;
// }

// function _updateSelectedFeaturesFromSelection(
//   feature,
//   selection = store.selection
// ) {
//   if (selection.indexOf(feature.name) != -1) {
//     feature.selected = true;
//     if (feature.features) {
//       feature.expanded = true;
//       feature.features.forEach((f) => _updateSelectedFeaturesFromSelection(f));
//     }
//   }
// }

// function _addSelection(fName, selection = store.selection) {
//   if (selection.indexOf(fName) === -1) selection.push(fName);
// }

// function _deleteSelection(fName, selection = store.selection) {
//   if (selection.indexOf(fName) != -1)
//     selection.splice(selection.indexOf(fName), 1);
// }

// export default store;
