import sinon from 'sinon';
import { expect } from 'chai';
import { SynchronousPromise } from 'synchronous-promise';
import createPostStore from '@/views/CreatePostStore.js';
import { INIT
  , REQUESTED
  , REQUEST_SUCCESS
  , REQUEST_FAIL
} from '@/modules/constants.js';
import { getTagsMock } from '../utils';

const getFailPromise = () => SynchronousPromise.reject({ response: { data: { messages: [ 'FAIL' ] } } });

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
    mockAxios = { post: sinon.stub().returns(SynchronousPromise.resolve({ data: { messages: [ 'OK' ] } })) };
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

  it('Issues submit request with data from state', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    const postData = JSON.parse(mockAxios.post.getCall(0).args[1].get('postData'));
    expect(postData.title).to.equal(mockState.title);
    expect(postData.webPath).to.equal(mockState.webPath);
    expect(postData.fsPath).to.equal(mockState.fsPath);
    expect(postData.tagIds).to.deep.equal([1, 2, 3]);
  });

  it('Issues submit request to the right endpoint', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    const endpointUrl = mockAxios.post.getCall(0).args[0];
    expect(endpointUrl).to.equal('/blog/admin/post-upload');
  });

  it('Increments and decrements loading counter on submit request', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledTwice(mockDispatch);
    sinon.assert.calledWith(mockDispatch.firstCall, 'app/incrementLoadingCount', null, { root: true });
    sinon.assert.calledWith(mockDispatch.secondCall, 'app/decrementLoadingCount', null, { root: true });
  });

  it('Shows message from the server on successful submit request', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledOnce(mockShowMessage);
    sinon.assert.calledWith(mockShowMessage, 'OK');
    sinon.assert.notCalled(mockShowError);
  });

  it('Clears form state on successful submit request', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledWith(mockCommit, 'clearForm');
  });

  it('Sets request status on successful submit', () => {
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledWith(mockCommit, 'setPostSubmitStatus', REQUEST_SUCCESS);
  });

  it('Increments and decrements loading counter on submit failed', () => {
    mockAxios.post.returns(getFailPromise());
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledTwice(mockDispatch);
    sinon.assert.calledWith(mockDispatch.firstCall, 'app/incrementLoadingCount', null, { root: true });
    sinon.assert.calledWith(mockDispatch.secondCall, 'app/decrementLoadingCount', null, { root: true });
  });

  it('Shows message from the server on failed submit request', () => {
    mockAxios.post.returns(getFailPromise());
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledOnce(mockShowError);
    sinon.assert.calledWith(mockShowError, 'FAIL');
    sinon.assert.notCalled(mockShowMessage);
  });

  it('Does not clear form state on failed submit request', () => {
    mockAxios.post.returns(getFailPromise());
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.neverCalledWith(mockCommit, 'clearForm');
  });

  it('Sets request status on failed submit', () => {
    mockAxios.post.returns(getFailPromise());
    createPostStore.actions.submitNewPost({ commit: mockCommit
      , dispatch: mockDispatch
      , state: mockState
    });
    sinon.assert.calledWith(mockCommit, 'setPostSubmitStatus', REQUEST_FAIL);
  });
});
