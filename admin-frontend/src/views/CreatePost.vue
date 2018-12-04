<template>
  <div>
      <section class="section">
        <div class="container has-text-left">
          <b-field
            horizontal
            label="Post file"
            class="file"
            :type="getType('file')"
            :message="fieldErrors.file"
          >
            <b-upload
              name="file"
              accept=".zip,application/zip,application/octet-stream,application/x-zip-compressed,multipart/x-zip"
              :value="file"
              @input="onFileChoosen"
            >
              <a
                class="button is-primary"
                v-if="!file"
              >
                <b-icon icon="upload"/>
                <span>Click to choose</span>
              </a>
              <a
                class="button is-primary"
                v-if="file"
              >
                <b-icon icon="edit"/>
                <span>{{ file.name }}</span>
              </a>
            </b-upload>
          </b-field>
          <b-field
            horizontal
            label="Title"
            :type="getType('title')"
            :message="fieldErrors.title"
          >
            <b-input
              name="title"
              expanded
              :value="title"
              @input.native="onInputChange"
              @blur="onInputBlur"
            />
          </b-field>
          <b-field
            horizontal
            label="Web path"
            :type="getType('webPath')"
            :message="fieldErrors.webPath"
          >
            <b-input
              name="webPath"
              expanded
              :value="webPath"
              @input.native="onInputChange"
              @blur="onInputBlur"
            />
          </b-field>
          <b-field
            horizontal
            label="FS path"
            :type="getType('fsPath')"
            :message="fieldErrors.fsPath"
          >
            <b-input
              name="fsPath"
              expanded
              :value="fsPath"
              @input.native="onInputChange"
              @blur="onInputBlur"
            />
          </b-field>
          <b-field
            horizontal
            label="Flags"
          >
            <p class="control">
            <b-checkbox
              name="shareable"
              :value="shareable"
              @change.native="onCheckBoxChange"
            >
              Shareable
            </b-checkbox>
            <b-checkbox
              name="commentAllowed"
              :value="commentAllowed"
              @change.native="onCheckBoxChange"
            >
              Comment allowed
            </b-checkbox>
            </p>
          </b-field>
          <b-field
            horizontal
            label="Post tags"
          >
            <b-taginput
                name="tags"
                :value="tags"
                @input="onTagsChange"
                :data="filteredTags"
                autocomplete
                :allow-new="false"
                field="name"
                icon="terminal"
                placeholder="Add a tag"
                @typing="updateFilteredTags">
            </b-taginput>
          </b-field>
          <b-field
            horizontal
            grouped
          >
            <p class="control">
              <button
                class="button is-primary with-right-space-margin"
                @click="submitForm"
              >
                Submit
              </button>
              <button
                class="button is-primary with-right-space-margin"
                @click="onFormClear"
              >
                Clear
              </button>
            </p>
          </b-field>
        </div>
      </section>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { capitalizeString
       , hasAllUndefined
       } from '@/utils';
import { quickCheckField
       , quickCheckFields
       , fullCheckField
       , fullCheckFields
       } from './CreatePostValidator.js';

const initialFieldErrors = () => (
  { title: undefined
  , file: undefined
  , webPath: undefined
  , fsPath: undefined
  }
);

export default {
  data() {
    return { filteredTags: []
           , fieldErrors: initialFieldErrors()
           };
  }
  , computed: {
    ...mapGetters('createPost', [
      'file'
      , 'title'
      , 'webPath'
      , 'fsPath'
      , 'shareable'
      , 'commentAllowed'
      , 'tags'
    ])
    , availableTags() {
      return this.$store.getters['post/availableTags'];
    }
  }
  , created() {
    this.$store.dispatch('post/fetchAvailableTags');
    Object.assign(this.fieldErrors, quickCheckFields(this.$store.state.createPost));
  }
  , methods: {
    updateFilteredTags(text) {
        this.filteredTags = this.availableTags
          .filter(tag => tag.name.toLowerCase().indexOf(text.toLowerCase()) >= 0);
    }
    , onInputChange(evt) {
      const element = evt.target;
      const fieldName = element.name;
      const setter = 'set' + capitalizeString(fieldName);
      const value = element.value;
      this.$store.commit(`createPost/${setter}`, value);
      this.fieldErrors[fieldName] = quickCheckField(fieldName, value);
    }
    , onInputBlur(evt) {
      const element = evt.target;
      const fieldName = element.name;
      this.fieldErrors[fieldName] = fullCheckField(fieldName, element.value);
    }
    , onCheckBoxChange(evt) {
      const element = evt.target;
      const setter = 'set' + capitalizeString(element.name);
      this.$store.commit(`createPost/${setter}`, element.checked)
    }
    , onFileChoosen(file) {
      this.$store.commit('createPost/setFile', file);
      this.fieldErrors.file = quickCheckField('file', file);
    }
    , onTagsChange(tags) {
      this.$store.commit('createPost/setTags', tags);
    }
    , onFormClear() {
      this.$store.commit('createPost/clearForm');
      this.fieldErrors = initialFieldErrors();
    }
    , submitForm(evt) {
      if (!this.validateForm()) {
        return;
      }
      this.$store.dispatch('createPost/submitNewPost');
    }
    , validateForm() {
      Object.assign(this.fieldErrors, fullCheckFields(this.$store.state.createPost));
      return hasAllUndefined(this.fieldErrors);
    }
    , getType(fieldName) {
      return this.fieldErrors[fieldName] ? 'is-danger' : '';
    }
  }
}
</script>

<style scoped lang="scss">
.with-right-space-margin {
  margin-right: 1.5em;
};
</style>