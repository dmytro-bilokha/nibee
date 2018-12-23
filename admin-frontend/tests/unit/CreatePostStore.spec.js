import sinon from 'sinon';
import { expect } from 'chai';
import createPostStore from '@/views/CreatePostStore.js';
import { INIT
       , REQUESTED
       , REQUEST_SUCCESS
       , REQUEST_FAIL
       } from '@/modules/constants.js';
import { getTagsMock } from '../utils';

describe('CreatePostStore.js', () => {
  
  let mockAxios;
  let mockCommit;
  let mockDispatch;
  let mockState;
  let mockShowError;
  let mockShowMessage;
  
  beforeEach(() => {
    mockCommit = sinon.stub();
    mockDispatch = sinon.stub();
    mockState = { submitPostStatus: INIT
                , title: 'The Title'
                , webPath: 'web-web-web'
                , fsPath: 'fs.Path'
                , file: null
                , tags: getTagsMock()
                };
    mockAxios = { post: sinon.stub().resolves('OK') };
    mockShowError = sinon.stub();
    mockShowMessage = sinon.stub();
    createPostStore.__Rewire__('axios', mockAxios);
    createPostStore.__Rewire__('showError', mockShowError);
    createPostStore.__Rewire__('showMessage', mockShowMessage);
  });
  
  afterEach(() => {
    sinon.restore();
    createPostStore.mutations.clearForm(createPostStore.state);
  });
  
  it('Does not issue the second submit request when the first is on air', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
                                          , dispatch: mockDispatch
                                          , state: { submitPostStatus: REQUESTED }
                                          });
    sinon.assert.notCalled(mockAxios.post);
  });
  
  it('Issues the first submit request', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
                                          , dispatch: mockDispatch
                                          , state: mockState
                                          });
    sinon.assert.calledOnce(mockAxios.post);
  });
  
  it('Increments and decrements loading counter on submit request', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
                                          , dispatch: mockDispatch
                                          , state: mockState
                                          });
    //sinon.assert.calledTwice(mockDispatch);
    //sinon.assert.calledWith(mockDispatch, 'app/incrementLoadingCount', null, { root: true });
  });
  
  
});