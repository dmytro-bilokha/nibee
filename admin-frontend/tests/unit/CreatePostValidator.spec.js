import sinon from 'sinon';
import { expect } from 'chai';
import { quickCheckField
       , quickCheckFields
       , fullCheckField
       , fullCheckFields
       } from '@/views/CreatePostValidator.js';

const fields = { file: 'file'
               , title: 'title'
               , webPath: 'webPath'
               , fsPath: 'fsPath'
               };
                             
describe('CreatePostValidator.js', () => {
  
  const validatorTestData = [ { field: 'title', validQuick: 'Titl', invalidQuick: 'Tїtl', invalidFull: 'Titl' }
                            , { field: 'webPath', validQuick: 'web-', invalidQuick: 'web_path', invalidFull: 'w-b' }
                            , { field: 'fsPath', validQuick: 'fs.p', invalidQuick: 'fs/path', invalidFull: 'fsp' }
                            , { field: 'file', validQuick: 'someFile', invalidFull: null }
                            ];

  validatorTestData.forEach(data => {
    
    it(`passes valid ${data.field} with quick check`, () => {
      expect(quickCheckField(data.field, data.validQuick)).to.be.undefined;
    });
    
    if (data.invalidQuick !== undefined) {
      it(`blocks invalid ${data.field} with quick check`, () => {
        expect(quickCheckField(data.field, data.invalidQuick)).to.be.not.empty;
      });
    }
    
    it(`blocks invalid ${data.field} with full check`, () => {
      expect(fullCheckField(data.field, data.invalidFull)).to.be.not.empty;
    });
  
  });
  
  it('passes valid data with quick check all fields', () => {
    const validFormValues = {};
    validatorTestData.forEach(data => {
      validFormValues[data.field] = data.validQuick;
    });
    const validationResult = quickCheckFields(validFormValues);
    validatorTestData.forEach(data => {
      expect(validationResult[data.field]).to.be.undefined;
    });
  });
  
  it('blocks invalid data with quick check all fields', () => {
    const invalidFormValues = {};
    validatorTestData.forEach(data => {
      invalidFormValues[data.field] = data.invalidQuick;
    });
    const validationResult = quickCheckFields(invalidFormValues);
    validatorTestData.filter(d => d.invalidQuick !== undefined).forEach(data => {
      expect(validationResult[data.field]).to.be.not.empty;
    });
  });
  
  it('blocks invalid data with full check all fields', () => {
    const invalidFormValues = {};
    validatorTestData.forEach(data => {
      invalidFormValues[data.field] = data.invalidFull;
    });
    const validationResult = fullCheckFields(invalidFormValues);
    validatorTestData.forEach(data => {
      expect(validationResult[data.field]).to.be.not.empty;
    });
  });
  
});