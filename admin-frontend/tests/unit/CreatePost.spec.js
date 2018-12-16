
import { expect, assert } from 'chai';
import sinon from 'sinon';
import { mount
       , shallowMount
       , createLocalVue
       } from '@vue/test-utils';
import Vue from 'vue';
import Vuex from 'vuex'
import Buefy from 'buefy';
import CreatePost from '@/views/CreatePost.vue';
import postStore from '@/modules/post';
import createPostStore from '@/views/CreatePostStore.js';
import mockStoreModuleActions from '../utils/mockStoreModuleActions.js';

Vue.config.silent = true;
const localVue = createLocalVue();
localVue.use(Vuex);
localVue.use(Buefy, {
  defaultIconPack: 'fas'
});

describe('CreatePost.vue', () => {
  let store;
  let postActions;
  let createPostActions;
  
  beforeEach(() => {
    const mockPostStore = mockStoreModuleActions(postStore);
    postActions = mockPostStore.actions;
    const mockCreatePostStore = mockStoreModuleActions(createPostStore);
    createPostActions = mockCreatePostStore.actions;
    store = new Vuex.Store({
      modules: { post: mockPostStore
               , createPost: mockCreatePostStore
               }
    });
  });
  
  afterEach(() => {
    sinon.restore();
  });
  
  it('triggers fetching available tags', () => {
    const wrapper = shallowMount(CreatePost, { store, localVue });
    sinon.assert.calledOnce(postActions.fetchAvailableTags);
  });
  
  it('propagates title to the input field', () => {
    const wrapper = mount(CreatePost, { store, localVue });
    store.commit('createPost/setTitle', 'new_title');
    assert.equal(wrapper.find('input[name="title"]').element.value, 'new_title');
  });
});