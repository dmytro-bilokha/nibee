
import { expect } from 'chai';
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
import { capitalizeString } from '@/utils';
import mockStoreModuleActions from '../utils/mockStoreModuleActions.js';

//This suppresses warnings about $listener is read only, should be fixed in the next Vue version
Vue.config.silent = true;
const localVue = createLocalVue();
localVue.use(Vuex);
localVue.use(Buefy, {
  defaultIconPack: 'fas'
});

const findCheckboxByName = (wrap, checkboxName) => {
  return wrap.findAll({ name: 'BCheckbox' })
    .filter(componentWrapper => componentWrapper.props().name === checkboxName).at(0);
};

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
    store.commit('createPost/clearForm');
  });
  
  afterEach(() => {
    sinon.restore();
  });
  
  const checkStoreToInputFlow = (fieldName, fieldValue) => {
    const wrapper = mount(CreatePost, { store, localVue });
    const setterName = 'set' + capitalizeString(fieldName);
    store.commit(`createPost/${setterName}`, fieldValue);
    expect(wrapper.find(`input[name="${fieldName}"]`).element.value).to.equal(fieldValue);
  };
  
  const checkInputToStoreFlow = (fieldName, fieldValue) => {
    const wrapper = mount(CreatePost, { store, localVue });
    wrapper.find(`input[name="${fieldName}"]`).setValue(fieldValue);
    expect(store.getters[`createPost/${fieldName}`]).to.equal(fieldValue);
  };
  
  const checkStoreToBoxFlow = boxName => {
    const wrapper = mount(CreatePost, { store, localVue });
    const checkBox = findCheckboxByName(wrapper, boxName);
    const setterName = 'set' + capitalizeString(boxName);
    store.commit(`createPost/${setterName}`, false);
    expect(checkBox.props().value).to.equal(false);
    store.commit(`createPost/${setterName}`, true);
    expect(checkBox.props().value).to.equal(true);
  };
  
  const checkBoxToStoreFlow = boxName => {
    const wrapper = mount(CreatePost, { store, localVue });
    const valueBefore = store.getters[`createPost/${boxName}`];
    wrapper.find(`input[name="${boxName}"]`).trigger('click');
    expect(store.getters[`createPost/${boxName}`]).to.equal(!valueBefore);
  };
  
  it('triggers fetching available tags', () => {
    const wrapper = shallowMount(CreatePost, { store, localVue });
    sinon.assert.calledOnce(postActions.fetchAvailableTags);
  });
  
  it('propagates title from store to the input field', () => {
    checkStoreToInputFlow('title', 'new_Title');
  });
  
  it('propagates title input change to the store', () => {
    checkInputToStoreFlow('title', 'TheTitle');
  });
  
  it('propagates web path from store to the input field', () => {
    checkStoreToInputFlow('webPath', 'new-web-path');
  });
  
  it('propagates web path input change to the store', () => {
    checkInputToStoreFlow('webPath', 'the-web-path-as-is');
  });
  
  it('propagates fs path from store to the input field', () => {
    checkStoreToInputFlow('fsPath', 'new-fs-path');
  });
  
  it('propagates fs path input change to the store', () => {
    checkInputToStoreFlow('fsPath', 'the-fs-path-as-is');
  });
  
  it('propagates commentAllowed from store to the input field', () => {
    checkStoreToBoxFlow('commentAllowed');
  });
  
  it('propagates commentAllowed input change to the store', () => {
    checkBoxToStoreFlow('commentAllowed');
  });
  
  it('propagates shareable from store to the input field', () => {
    checkStoreToBoxFlow('shareable');
  });
  
  it('propagates shareable input change to the store', () => {
    checkBoxToStoreFlow('shareable');
  });
    
});