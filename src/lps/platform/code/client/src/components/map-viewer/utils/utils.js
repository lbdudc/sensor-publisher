/*% if (feature.MV_MS_GJ_Paginated) { %*/
function boundingBoxIntersects(bbox1, bbox2) {
  const bbox1L = L.latLngBounds(
    L.latLng(bbox1.ymin, bbox1.xmin),
    L.latLng(bbox1.ymax, bbox1.xmax)
  );
  const bbox2L = L.latLngBounds(
    L.latLng(bbox2.ymin, bbox2.xmin),
    L.latLng(bbox2.ymax, bbox2.xmax)
  );

  return Object.keys(bbox1L).length === 0 || Object.keys(bbox2L).length === 0
    ? false
    : bbox1L.intersects(bbox2L);
}

function incrementBBox(xmin, xmax, ymin, ymax) {
  var incrementNS = (ymax - ymin) * 0.3;
  var incrementEW = (xmax - xmin) * 0.3;

  var augmentedXmin = xmin - incrementEW;
  var augmentedXmax = xmax + incrementEW;
  var augmentedYmin = ymin - incrementNS;
  var augmentedYmax = ymax + incrementNS;

  return {
    xmin: augmentedXmin,
    xmax: augmentedXmax,
    ymin: augmentedYmin,
    ymax: augmentedYmax,
  };
}

export { boundingBoxIntersects, incrementBBox };
/*% } %*/
