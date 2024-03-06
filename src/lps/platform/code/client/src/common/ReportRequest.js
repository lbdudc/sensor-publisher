/*% if (feature.GUI_L_Export) { %*/
export default function (repository, entity) {
  repository.getReport()
  .then((data) => {
    var fileURL = window.URL.createObjectURL(new Blob([data]));
    var fileLink = document.createElement('a');
    fileLink.href = fileURL;
    fileLink.setAttribute('download', entity + 'List.xlsx');
    document.body.appendChild(fileLink);
    fileLink.click();
  });
}
/*% } %*/
