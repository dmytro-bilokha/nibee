import axios from 'axios';
import { INIT
       , REQUESTED
       , REQUEST_SUCCESS
       , REQUEST_FAIL
       } from '@/modules/constants.js';
import { showError
       , showMessage
       } from '@/utils';

const getInitialState = () => (
  { file: null
  , title: ''
  , webPath: ''
  , fsPath: ''
  , shareable: true
  , commentAllowed: true
  , tags: []
  , submitPostStatus: INIT
  }
);

const state = { ...getInitialState() };

const mutations = 
  { setFile(state, file) {
      state.file = file;
    }
  , setTitle(state, title) {
      state.title = title;
    }
  , setWebPath(state, webPath) {
      state.webPath = webPath;
    }
  , setFsPath(state, fsPath) {
      state.fsPath = fsPath;
    }
  , setShareable(state, shareable) {
      state.shareable = shareable;
    }
  , setCommentAllowed(state, commentAllowed) {
      state.commentAllowed = commentAllowed;
    }
  , setTags(state, tags) {
      state.tags = tags;
    }
  , clearForm(state) {
      Object.assign(state, getInitialState()); 
    }
  , setPostSubmitStatus(state, status) {
      state.submitPostStatus = status;
    }  
  };

const actions =
  { submitNewPost({ commit, dispatch, state }) {
      if (state.submitPostStatus === REQUESTED) {
        return;
      }
      dispatch('app/incrementLoadingCount', null, { root: true });
      commit('setPostSubmitStatus', REQUESTED);
      const newPostForm = new FormData();
      newPostForm.append('file', state.file);
      const postData = { ...state };
      delete postData.file;
      delete postData.submitPostStatus;
      delete postData.tags;
      postData.tagIds = state.tags.map(t => t.id);
      newPostForm.append('postData', JSON.stringify(postData));
      axios.post('/blog/admin/post-upload', newPostForm)
        .then(response => {
          commit('clearForm');
          commit('setPostSubmitStatus', REQUEST_SUCCESS);
          dispatch('app/decrementLoadingCount', null, { root: true });
          if (response && response.data && Array.isArray(response.data.messages)) {
            response.data.messages.forEach(msg => showMessage(msg));
            return;
          }
          showMessage('The post has been successfuly submitted');
        }
        , error => {
          console.log(error);
          commit('setPostSubmitStatus', REQUEST_FAIL);
          dispatch('app/decrementLoadingCount', null, { root: true });
          if (error.response && error.response.data && Array.isArray(error.response.data.messages)) {
            error.response.data.messages.forEach(msg => showError(msg));
            return;
          }
          showError('Post submit failed');
        });
    }
  };

const getters = 
  { file: state => state.file
  , title: state => state.title
  , webPath: state => state.webPath
  , fsPath: state => state.fsPath
  , shareable: state => state.shareable
  , commentAllowed: state => state.commentAllowed
  , tags: state => state.tags
  };

const createPostStore =
  { namespaced: true
  , state
  , mutations
  , actions
  , getters
  };

export default createPostStore;
