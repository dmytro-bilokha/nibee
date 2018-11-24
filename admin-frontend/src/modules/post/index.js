import axios from 'axios';

const state = 
  { availableTags: []
  , availableTagsStatus: 0
  };

const mutations = {
  updateAvailableTags(state, payload) {
      state.availableTags = payload;
      state.availableTagsStatus = 1;
  }
};

const actions = {
    fetchAvailableTags({ commit, dispatch, state }) {
      if (state.availableTagsStatus !== 0) {
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