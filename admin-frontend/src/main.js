import Vue from 'vue';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faCoffee } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import 'bulma/css/bulma.css';
import App from './App.vue';
import router from './router';
import store from './store';

library.add(faCoffee);
Vue.component('font-awesome-icon', FontAwesomeIcon);
Vue.config.productionTip = false;
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
