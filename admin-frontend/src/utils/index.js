import { Snackbar } from 'buefy/dist/components/snackbar';

export const capitalizeString = (string) => string.charAt(0).toUpperCase() + string.slice(1);

export const hasAllUndefined = (obj) => {
  for (let key in obj) {
    if (obj[key] !== undefined) {
      return false;
    }
  }
  return true;
};

export const showError = (errorMessage) => {
  Snackbar.open(
    { indefinite: true
    , type: 'is-danger'
    , position: 'is-top-right'
    , message: errorMessage
    }
  );
};

export const showMessage = (textMessage) => {
  Snackbar.open(
    { indefinite: true
    , position: 'is-top-right'
    , message: textMessage
    }
  );
};
