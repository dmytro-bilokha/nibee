
const fieldsValidationData =
  { title: 
      { quickCheck: (value) => 
          /^[A-Za-z0-9]*$/.test(value) ? undefined : 'Title must contain only ASCII characters'
      , fullCheck: (value) =>
          /^[A-Za-z0-9]{5,}$/.test(value) ? undefined : 
            'Title must contain only ASCII characters and be at least 5 characters long'
      }
  , webPath:
      { quickCheck: (value) => 
          /^[A-Za-z0-9]*$/.test(value) ? undefined : 'Title must contain only ASCII characters'
      , fullCheck: (value) =>
          /^[A-Za-z0-9]{5,}$/.test(value) ? undefined : 
            'Title must contain only ASCII characters and be at least 5 characters long'
      }
  , fsPath:
      { quickCheck: (value) => 
          /^[A-Za-z0-9]*$/.test(value) ? undefined : 'Title must contain only ASCII characters'
      , fullCheck: (value) =>
          /^[A-Za-z0-9]{5,}$/.test(value) ? undefined : 
            'Title must contain only ASCII characters and be at least 5 characters long'
      }
  , file:
      { quickCheck: (value) => undefined
      , fullCheck: (value) => 
          value ? undefined : "Zip archive with post assets is mandatory to upload" 
      }
  };

export const quickCheckField = (fieldName, value) => {
  const validationData = fieldsValidationData[fieldName];
  if (validationData) {
    return validationData.quickCheck(value);
  }
  return undefined;
};

export const quickCheckFields = (fieldValuesObject) => {
  const validationMessages = {};
  for (let fieldName in fieldValuesObject) {
    validationMessages[fieldName] = quickCheckField(fieldName, fieldValuesObject[fieldName]);
  }
  return validationMessages;
};

export const fullCheckField = (fieldName, value) => {
  const validationData = fieldsValidationData[fieldName];
  if (validationData) {
    return validationData.fullCheck(value);
  }
  return undefined;
};

export const fullCheckFields = (fieldValuesObject) => {
  const validationMessages = {};
  for (let fieldName in fieldValuesObject) {
    validationMessages[fieldName] = fullCheckField(fieldName, fieldValuesObject[fieldName]);
  }
  return validationMessages;
};