function normalize(str, upper) {
  return str
    .normalize("NFKD")
    .replace(/[\u0300-\u036F]/g, "")
    .replace(/[-_]+/g, " ")
    .replace(/[^\w\s]/gi, "")
    .replace(/\s(.)/g, function ($1) {
      return $1.toUpperCase();
    })
    .replace(/\s/g, "")
    .replace(/^(.)/, function ($1) {
      return upper ? $1.toUpperCase() : $1.toLowerCase();
    });
}

export default normalize;
