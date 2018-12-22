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
import { mockStoreModuleActions
       , getTagsMock
       } from '../utils';

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

const findErrorMessage = wrap => {
  return wrap.find('p.help.is-danger');
};

const controlNames = { file: 'file'
                     , title: 'title'
                     , webPath: 'webPath'
                     , fsPath: 'fsPath'
                     , shareable: 'shareable'
                     , commentAllowed: 'commentAllowed'
                     , tags: 'tags'
                     , clear: 'clear'
                     , submit: 'submit'
                     };

describe('CreatePost.vue', () => {
  let store;
  let postActions;
  let createPostActions;
  let mockValidator;
  
  beforeEach(() => {
    const mockPostStore = mockStoreModuleActions(postStore);
    mockValidator = { quickCheckField: sinon.stub()
                    , quickCheckFields: sinon.stub()
                    , fullCheckField: sinon.stub()
                    , fullCheckFields: sinon.stub()
                    };
    CreatePost.__Rewire__('quickCheckField', mockValidator.quickCheckField);
    CreatePost.__Rewire__('quickCheckFields', mockValidator.quickCheckFields);
    CreatePost.__Rewire__('fullCheckField', mockValidator.fullCheckField);
    CreatePost.__Rewire__('fullCheckFields', mockValidator.fullCheckFields);
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
    store.commit('createPost/clearForm');
    store.commit('post/reset');
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
  
  const checkButtonClearsInput = inputName => {
    const wrapper = mount(CreatePost, { store, localVue });
    const input = wrapper.find(`input[name="${inputName}"]`);
    const newFieldValue = 'this-value-fits-all-inputs';
    input.setValue(newFieldValue);
    expect(input.element.value).to.not.be.empty;
    wrapper.find(`button[name="${controlNames.clear}"]`).trigger('click');
    expect(input.element.value).to.be.empty;
  };
  
  const checkValidatorCalledOnInputChange = inputName => {
    const wrapper = mount(CreatePost, { store, localVue });
    const input = wrapper.find(`input[name="${inputName}"]`);
    const newFieldValue = 'this-value-fits-all-inputs';
    input.setValue(newFieldValue);
    sinon.assert.calledOnce(mockValidator.quickCheckField);
    sinon.assert.calledWith(mockValidator.quickCheckField, inputName, newFieldValue);
  };
  
  const checkShowsError = inputName => {
    const errorMessage = `${inputName}-validation-failed`;
    mockValidator.quickCheckFields.returns({ [inputName]: errorMessage });
    const wrapper = mount(CreatePost, { store, localVue });
    expect(findErrorMessage(wrapper).html()).to.include(errorMessage);
  };
  
  it('triggers fetching available tags', () => {
    const wrapper = shallowMount(CreatePost, { store, localVue });
    sinon.assert.calledOnce(postActions.fetchAvailableTags);
  });
  
  it('propagates title from store to the input field', () => {
    checkStoreToInputFlow(controlNames.title, 'new_Title');
  });
  
  it('propagates title input change to the store', () => {
    checkInputToStoreFlow(controlNames.title, 'TheTitle');
  });
  
  it('propagates web path from store to the input field', () => {
    checkStoreToInputFlow(controlNames.webPath, 'new-web-path');
  });
  
  it('propagates web path input change to the store', () => {
    checkInputToStoreFlow(controlNames.webPath, 'the-web-path-as-is');
  });
  
  it('propagates fs path from store to the input field', () => {
    checkStoreToInputFlow(controlNames.fsPath, 'new-fs-path');
  });
  
  it('propagates fs path input change to the store', () => {
    checkInputToStoreFlow(controlNames.fsPath, 'the-fs-path-as-is');
  });
  
  it('propagates commentAllowed from store to the input field', () => {
    checkStoreToBoxFlow(controlNames.commentAllowed);
  });
  
  it('propagates commentAllowed input change to the store', () => {
    checkBoxToStoreFlow(controlNames.commentAllowed);
  });
  
  it('propagates shareable from store to the input field', () => {
    checkStoreToBoxFlow(controlNames.shareable);
  });
  
  it('propagates shareable input change to the store', () => {
    checkBoxToStoreFlow(controlNames.shareable);
  });
    
  it('clears title on clear button clicked', () => {
    checkButtonClearsInput(controlNames.title);
  });
  
  it('clears web path on clear button clicked', () => {
    checkButtonClearsInput(controlNames.webPath);
  });
  
  it('clears fs path on clear button clicked', () => {
    checkButtonClearsInput(controlNames.fsPath);
  });
  
  it('calls quick check fields on form opened', () => {
    const wrapper = shallowMount(CreatePost, { store, localVue });
    sinon.assert.calledOnce(mockValidator.quickCheckFields);
  });
    
  it('calls quick check on title change', () => {
    checkValidatorCalledOnInputChange(controlNames.title);
  });
  
  it('calls quick check on web path change', () => {
    checkValidatorCalledOnInputChange(controlNames.webPath);
  });
  
  it('calls quick check on fs path change', () => {
    checkValidatorCalledOnInputChange(controlNames.fsPath);
  });
  
  it('calls full check on submit clicked', () => {
    const wrapper = mount(CreatePost, { store, localVue });
    wrapper.find(`button[name="${controlNames.submit}"]`).trigger('click');
    sinon.assert.calledOnce(mockValidator.fullCheckFields);
  });
  
  it('shows validation error for title field', () => {
    checkShowsError(controlNames.title);
  });
  
  it('shows validation error for web path field', () => {
    checkShowsError(controlNames.webPath);
  });

  it('shows validation error for fs path field', () => {
    checkShowsError(controlNames.fsPath);
  });
  
  it('shows validation error for file field', () => {
    checkShowsError(controlNames.file);
  });
    
});
