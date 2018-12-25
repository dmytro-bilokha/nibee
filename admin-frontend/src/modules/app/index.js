const state = { loadingCounter: 0
              };

const mutations = {
  incrementLoadingCount(state) {
    state.loadingCounter = state.loadingCounter + 1;
  }
  , decrementLoadingCount(state) {
    state.loadingCounter = (state.loadingCounter <= 0) ? 0 : state.loadingCounter - 1;
  }
};

const actions = {
  incrementLoadingCount({ commit }) {
    commit('incrementLoadingCount');
  }
  , decrementLoadingCount({ commit }) {
    commit('decrementLoadingCount');
  }
};

const getters = {
  loadingCounter: state => state.loadingCounter
};

const appModule = { namespaced: true
                  , state
                  , mutations
                  , actions
                  , getters
                  };

export default appModule;
