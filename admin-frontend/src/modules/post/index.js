import axios from 'axios';

const state = 
  { availableTags: []
  };

const mutations = {
  UPDATE_AVAILABLE_TAGS(state, payload) {
      state.availableTags = payload;
  }
};

const actions = {
    fetchAvailableTags({commit}) {
      axios.get('/blog/api/tags')
        .then(response => commit('UPDATE_AVAILABLE_TAGS', response.data));
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