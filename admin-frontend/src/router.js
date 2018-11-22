import Vue from 'vue';
import Router from 'vue-router';
import CreatePost from './views/CreatePost.vue';
import EntryPoint from './views/EntryPoint.vue';

Vue.use(Router);

export default new Router({
  routes:
    [ { path: '/create'
      , name: 'create'
      , component: CreatePost
      }
    , { path: '/'
      , name: 'entry-point'
      , component: EntryPoint
      }
    ]
  , linkActiveClass: 'is-active'
});
