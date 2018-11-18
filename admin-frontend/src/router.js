import Vue from 'vue';
import Router from 'vue-router';
import CreatePost from './views/CreatePost.vue';

Vue.use(Router);

export default new Router({
  routes: [
    { path: '/create'
    , name: 'create'
    , component: CreatePost
    }
  ]
  , linkActiveClass: 'is-active'
});
