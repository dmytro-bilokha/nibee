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
      dispatch('incrementLoadingCount');
      axios.get('/blog/api/tags')
        .then(response => {
          commit('updateAvailableTags', response.data);
          dispatch('decrementLoadingCount');
        }
      );
    }
};

const getters = {
  availableTags: state => state.availableTags
};

const postModule =
  { state
  , mutations
  , actions
  , getters
  };
  
export default postModule;