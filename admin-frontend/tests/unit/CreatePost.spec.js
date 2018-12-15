
import { expect } from 'chai';
import sinon from 'sinon';
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
      fetchAvailableTags: sinon.fake()
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
  
  afterEach(() => {
    sinon.restore();
  });
  
  it('triggers fetching available tags', () => {
    const wrapper = shallowMount(CreatePost, { store, localVue });
    sinon.assert.calledOnce(actions.fetchAvailableTags);
  });
});