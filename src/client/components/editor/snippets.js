export default {
  suggestions: [
    {
      label: "Create Product",
      insertText: ["CREATE PRODUCT ${1:x} USING ${2:4327};"].join("\n"),
      detail: "Inserts a CREATE PRODUCT statement",
    },

    {
      label: "Create Spatial Dimension",
      insertText: [
        "CREATE SPATIAL DIMENSION ${1:dimensionName} (",
        "\tgeometry: ${2:Polygon}",
        ") WITH PROPERTIES (",
        "\t${3:name} ${4:type} DISPLAY_STRING",
        ");",
      ].join("\n"),
      detail: "Inserts a CREATE SPATIAL DIMENSION statement",
    },

    {
      label: "Create Categorical Dimension",
      insertText: [
        "CREATE CATEGORICAL DIMENSION ${1:dimensionName} (",
        "\tfield: ${2:fieldName}",
        ");",
      ].join("\n"),
      detail: "Inserts a CREATE CATEGORICAL DIMENSION statement",
    },

    {
      label: "Create Sensor",
      insertText: [
        "CREATE SENSOR ${1:sensorName} (",
        "\tinterval: ${2:500},",
        "\tdatasource: ${3:elasticsearch},",
        "\tgeometry: ${4:Point}",
        ") WITH PROPERTIES (",
        "\t${5:name} ${6:type}",
        ") WITH MEASUREMENT DATA (",
        "\t${7:fieldName} ${8:type} UNITS ${9:'unit'}, ICON ${10:'icon'} RANGE ${11:rangeName}",
        ") WITH SPATIAL DIMENSIONS ${12:spatialGroupName} (",
        "\t${13:spatialName}",
        ") WITH CATEGORICAL DIMENSIONS (",
        "\t${14:catName}",
        ") WITH BBOX ( [ ${15:lat}, ${16:lon} ], ${17:zoom} );",
      ].join("\n"),
      detail: "Inserts a CREATE SENSOR statement",
    },

    {
      label: "Create Range",
      insertText: [
        "CREATE RANGE ${1:rangeName} (",
        "\t${2:x} TO ${3:y} AS ${4:rangePropertyName}",
        ");",
      ].join("\n"),
      detail: "Inserts a CREATE RANGE statement",
    },
  ],
};
