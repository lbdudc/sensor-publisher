<template>
  <div ref="editor" class="monaco-editor"></div>
</template>

<script>
import * as monaco from "monaco-editor";

export default {
  name: "MonacoEditor",
  props: {
    code: {
      type: String,
      default: "",
    },
    language: {
      type: String,
      default: "javascript",
    },
  },
  data() {
    return {
      editor: null,
    };
  },
  mounted() {
    this.initEditor();
    // this.registerCustomLanguage();
  },
  beforeDestroy() {
    this.disposeEditor();
  },
  methods: {
    initEditor() {
      this.editor = monaco.editor.create(this.$refs.editor, {
        value: this.code,
        language: "plaintext",
        automaticLayout: true, // Set automaticLayout option to true
      });

      this.registerSnippets();

      this.editor.onDidChangeModelContent(() => {
        this.$emit("update:code", this.editor.getValue());
      });
    },
    registerCustomLanguage() {
      // TODO: Register custom language
      monaco.languages.register({ id: "customLanguage" });

      // monaco.languages.setMonarchTokensProvider("customLanguage", {
      //   tokenizer: {
      //     root: [
      //       // Patterns translated from your tmLanguage.json
      //       { include: "#comments" },
      //       { include: "#strings" },
      //       { include: "#keywords" },
      //       { include: "#datatypes" },
      //       { include: "#identifiers" },
      //       { include: "#numbers" },
      //       { include: "#operators" },
      //     ],
      //     // Definitions of patterns from tmLanguage.json
      //     ...customLanguageDefinition.repository,
      //   },
      //   // Additional language configuration if needed
      //   language: "customLanguage",
      // });

      // monaco.editor.defineTheme("customLanguageTheme", {
      //   base: "vs", // Base theme to inherit from
      //   inherit: true, // Inherit theme rules
      //   rules: [
      //     // Define token styles based on your language patterns
      //     // Adjust these rules to match your language specifications
      //     {
      //       token: "comment.line.double-slash.sensorGrammar",
      //       foreground: "green",
      //     },
      //     {
      //       token: "string.quoted.double.sensorGrammar",
      //       foreground: "#ff0000",
      //     },
      //     { token: "keyword.control.sensorGrammar", foreground: "#0000ff" },
      //     { token: "support.type.sensorGrammar", foreground: "#0000ff" },
      //     { token: "variable.sensorGrammar", foreground: "#0000ff" },
      //     { token: "constant.numeric.sensorGrammar", foreground: "#0000ff" },
      //     { token: "keyword.operator.sensorGrammar", foreground: "#0000ff" },
      //     // Add more token rules as needed based on your language specifications
      //   ],
      //   // Other theme settings if needed
      // });

      console.log(this.editor.getModel());
      console.log(monaco.languages.getLanguages());

      this.editor.setModelLanguage(this.editor.getModel(), "customLanguage");
      this.editor.updateOptions({ theme: "customLanguageTheme" });
    },
    registerSnippets() {
      // Register a completion item provider
      monaco.languages.registerCompletionItemProvider("plaintext", {
        provideCompletionItems: (model, position) => {
          return {
            suggestions: [
              {
                label: "Create Product",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: ["CREATE PRODUCT ${1:x} USING ${2:4327};"].join(
                  "\n"
                ),
                detail: "Inserts a CREATE PRODUCT statement",
                insertTextRules:
                  monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              },
              {
                label: "Create Spatial Dimension",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: [
                  "CREATE SPATIAL DIMENSION ${1:dimensionName} (",
                  "\tgeometry: ${2:Polygon}",
                  ") WITH PROPERTIES (",
                  "\t${3:name} ${4:type} DISPLAY_STRING",
                  ");",
                ].join("\n"),
                detail: "Inserts a CREATE SPATIAL DIMENSION statement",
                insertTextRules:
                  monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              },
              {
                label: "Create Categorical Dimension",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: [
                  "CREATE CATEGORICAL DIMENSION ${1:dimensionName} (",
                  "\tfield: ${2:fieldName}",
                  ");",
                ].join("\n"),
                detail: "Inserts a CREATE CATEGORICAL DIMENSION statement",
                insertTextRules:
                  monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              },
              {
                label: "Create Sensor",
                kind: monaco.languages.CompletionItemKind.Snippet,
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
                insertTextRules:
                  monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              },
              {
                label: "Create Range",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: [
                  "CREATE RANGE ${1:rangeName} (",
                  "\t${2:x} TO ${3:y} AS ${4:rangePropertyName}",
                  ");",
                ].join("\n"),
                detail: "Inserts a CREATE RANGE statement",
                insertTextRules:
                  monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              },
            ],
          };
        },
      });
    },
    disposeEditor() {
      if (this.editor) {
        this.editor.dispose();
      }
    },
  },
  watch: {
    code(newCode) {
      if (this.editor && this.editor.getValue() !== newCode) {
        this.editor.setValue(newCode);
      }
    },
    language(newLanguage) {
      if (
        this.editor &&
        this.editor.getModel().getLanguageId() !== newLanguage
      ) {
        monaco.editor.setModelLanguage(this.editor.getModel(), newLanguage);
      }
    },
  },
};
</script>

<style>
.monaco-editor {
  width: 100%;
  height: 100%; /* Set the height as per your requirement */
}
</style>
