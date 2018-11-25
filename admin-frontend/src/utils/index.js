
export const capitalizeString = (string) => string.charAt(0).toUpperCase() + string.slice(1);
export const hasAllUndefined = (obj) => {
  for (let key in obj) {
    if (obj[key] !== undefined) {
      return false;
    }
  }
  return true;
};