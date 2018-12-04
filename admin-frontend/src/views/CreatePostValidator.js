
const fieldsValidationData =
  { title: 
      { quickCheck: (value) => 
          /^[\x00-\x7F]*$/.test(value) ? undefined : 'Must contain only ASCII characters'
      , fullCheck: (value) =>
          /^[\x00-\x7F]{5,}$/.test(value) ? undefined : 
            'Must contain only ASCII characters and be at least 5 characters long'
      }
  , webPath:
      { quickCheck: (value) => 
          /^[._a-zA-Z0-9-]*$/.test(value) ? undefined : 
            'Must contain only numbers, latin letters and minus sign'
      , fullCheck: (value) =>
          /^[._a-zA-Z0-9-]{5,}$/.test(value) ? undefined : 
            'Should be at least 5 characters long and must contain only numbers,'
                    + ' latin letters and minus sign'
      }
  , fsPath:
      { quickCheck: (value) => 
          /^[._a-zA-Z0-9-]*$/.test(value) ? undefined : 
            'Must contain only numbers, latin letters, dot, underscore and minus sign'
      , fullCheck: (value) =>
          /^[._a-zA-Z0-9-]{5,}$/.test(value) ? undefined : 
            'Should be at least 5 characters long and must contain only numbers,'
                            + ' latin letters, dot, underscore and minus sign'
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