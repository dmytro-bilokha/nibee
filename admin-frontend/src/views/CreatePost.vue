<template>
  <div>
      <section class="section">
        <div class="container has-text-left">
          <b-field
            horizontal
            label="Post file"
            class="file"
          >
            <b-upload
              name="File"
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
            type="is-danger"
            message="Please enter a title"
          >
            <b-input
              name="Title"
              expanded
              :value="title"
              @input.native="onInputChange"
            />
          </b-field>
          <b-field
            horizontal
            label="Web path"
          >
            <b-input
              name="WebPath"
              expanded
              :value="webPath"
              @input.native="onInputChange"
            />
          </b-field>
          <b-field
            horizontal
            label="FS path"
          >
            <b-input
              name="FsPath"
              expanded
              :value="fsPath"
              @input.native="onInputChange"
            />
          </b-field>
          <b-field
            horizontal
            label="Flags"
          >
            <p class="control">
            <b-checkbox
              name="Shareable"
              :value="shareable"
              @change.native="onCheckBoxChange"
            >
              Shareable
            </b-checkbox>
            <b-checkbox
              name="CommentAllowed"
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
                name="Tags"
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

export default {
  data() {
    return { filteredTags: [] };
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
  }
  , methods: {
    updateFilteredTags(text) {
        this.filteredTags = this.availableTags
          .filter(tag => tag.name.toLowerCase().indexOf(text.toLowerCase()) >= 0);
    }
    , onInputChange(evt) {
      const element = evt.target;
      this.$store.commit(`createPost/set${element.name}`, element.value);
    }
    , onCheckBoxChange(evt) {
      const element = evt.target;
      this.$store.commit(`createPost/set${element.name}`, element.checked)
    }
    , onFileChoosen(file) {
      this.$store.commit('createPost/setFile', file);
    }
    , onTagsChange(tags) {
      this.$store.commit('createPost/setTags', tags);
    }
    , onFormClear() {
      this.$store.commit('createPost/clearForm');
    }
  }
}
</script>

<style scoped lang="scss">
.with-right-space-margin {
  margin-right: 1.5em;
};
</style>