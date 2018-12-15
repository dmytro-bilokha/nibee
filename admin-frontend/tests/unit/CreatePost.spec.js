
import { expect } from 'chai';
import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import Buefy from 'buefy';
import CreatePost from '@/views/CreatePost.vue';

const localVue = createLocalVue();
localVue.use(Vuex);
localVue.use(Buefy, {
  defaultIconPack: 'fas'
});

describe('CreatePost.vue', () => {
  let actions;
  let store;
  
  beforeEach(() => {
    actions = {
      fetchAvailableTags: () => {console.log('Hello');}
    };
    store = new Vuex.Store({
      modules: {
        post: {
          namespaced: true
          , state: {}
          , actions
        }
      }
    });
  });
  
  it('renders', () => {
    const wrapper = shallowMount(CreatePost, { store, localVue });
    
  });
});