
const state = 
  { loadingCounter: 0
  };

const mutations = {
  INCREMENT_LOADING_COUNT(state) {
    state.loadingCounter = state.loadingCounter + 1;
  }
  , DECREMENT_LOADING_COUNT(state) {
    state.loadingCounter = (state.loadingCounter <= 0) ? 0 : state.loadingCounter - 1;
  }
};

const actions = {
  incrementLoadingCount({commit}) {
    commit('INCREMENT_LOADING_COUNT');
  }
  , decrementLoadingCount({commit}) {
    commit('DECREMENT_LOADING_COUNT');
  }
};

const getters = {
  loadingCounter: state => state.loadingCounter
};

const appModule =
  { state
  , mutations
  , actions
  , getters
  };

export default appModule;
