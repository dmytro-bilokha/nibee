import Vue from 'vue';
import Buefy from 'buefy';
import 'buefy/dist/buefy.css';
import './assets/styles.scss';
import App from './App.vue';
import router from './router';
import store from './store';

Vue.use(Buefy, {
  defaultIconPack: 'fas'
});
Vue.config.productionTip = false;
new Vue({ router
  , store
  , render: h => h(App)
}).$mount('#app');
