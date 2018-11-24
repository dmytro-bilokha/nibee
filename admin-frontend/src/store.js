import Vue from 'vue';
import Vuex from 'vuex';
import post from './modules/post';
import app from './modules/app';
import createPost from './views/CreatePostStore.js';

Vue.use(Vuex);

export default new Vuex.Store({
  modules:
    { post
    , app
    , createPost
    }
});
