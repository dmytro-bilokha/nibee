import axios from 'axios';
import { INIT
       , REQUESTED
       , REQUEST_SUCCESS
       } from '@/modules/constants.js';

const state = 
  { availableTags: []
  , availableTagsStatus: INIT
  };

const mutations = {
  updateAvailableTags(state, payload) {
      state.availableTags = payload;
      state.availableTagsStatus = REQUEST_SUCCESS;
  }
};

const actions = {
    fetchAvailableTags({ commit, dispatch, state }) {
      if (state.availableTagsStatus === REQUESTED || state.availabelTagsStatus == REQUEST_SUCCESS) {
        return;
      }
      dispatch('app/incrementLoadingCount', null, { root: true });
      axios.get('/blog/api/tags')
        .then(response => {
          commit('updateAvailableTags', response.data);
          dispatch('app/decrementLoadingCount', null, { root: true });
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