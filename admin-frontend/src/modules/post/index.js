import axios from 'axios';
import { INIT
       , REQUESTED
       , REQUEST_SUCCESS
       , REQUEST_FAIL
       } from '@/modules/constants.js';
import { showError } from '@/utils';

const state = 
  { availableTags: []
  , availableTagsStatus: INIT
  };

const mutations =
  { updateAvailableTags(state, payload) {
      state.availableTags = payload;
      state.availableTagsStatus = REQUEST_SUCCESS;
    }
  , setTagsRequestFailed(state) {
      state.availableTagsStatus = REQUEST_FAIL;
    }
  , setTagsRequested(state) {
      state.availableTagsStatus = REQUESTED;
  }
  };

const actions = {
    fetchAvailableTags({ commit, dispatch, state }) {
      if (state.availableTagsStatus === REQUESTED || state.availabelTagsStatus == REQUEST_SUCCESS) {
        return;
      }
      commit('setTagsRequested');
      dispatch('app/incrementLoadingCount', null, { root: true });
      axios.get('/blog/api/tags')
        .then(response => {
          commit('updateAvailableTags', response.data);
          dispatch('app/decrementLoadingCount', null, { root: true });
        }
        , error => {
          console.log(error);
          commit('setTagsRequestFailed');
          dispatch('app/decrementLoadingCount', null, { root: true });
          showError('Failed to get available tags list from the backend');
        }
      );
    }
};

const getters = {
  availableTags: state => state.availableTags
};

const postModule =
  { namespaced: true
  , state
  , mutations
  , actions
  , getters
  };
  
export default postModule;