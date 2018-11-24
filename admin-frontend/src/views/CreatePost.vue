<template>
  <div>
      <section class="section">
        <div class="container has-text-left">
          <b-field
            horizontal
            label="Post file"
            class="file"
          >
            <b-upload v-model="file">
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
            label="Creation date/time"
          >
            <b-datepicker
              placeholder="Set date..."
              icon="calendar"
              editable
              v-model="creationDate"
            />
            <b-timepicker
              placeholder="Set time..."
              icon="clock"
              editable
              v-model="creationTime"
            />
          </b-field>
          <b-field
            horizontal
            label="Title"
            type="is-danger"
            message="Please enter a title"
          >
            <b-input
              name="title"
              expanded
            />
          </b-field>
          <b-field
            horizontal
            label="Web path"
          >
            <b-input
              name="web-path"
              expanded
            />
          </b-field>
          <b-field
            horizontal
            label="FS path"
          >
            <b-input
              name="fs-path"
              expanded
            />
          </b-field>
          <b-field
            horizontal
            label="Flags"
          >
            <p class="control">
            <b-checkbox
            >
              Shareable
            </b-checkbox>
            <b-checkbox>
              Comment allowed
            </b-checkbox>
            </p>
          </b-field>
          <b-field
            horizontal
            label="Post tags"
          >
            <b-taginput
                v-model="tags"
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
              <button class="button is-primary with-right-space-margin">Submit</button>
              <button class="button is-primary with-right-space-margin">Clear</button>
            </p>
          </b-field>
        </div>
      </section>
  </div>
</template>

<script>
export default {
  data() {
    return { file: null
           , creationDate: new Date()
           , creationTime: new Date()
           , filteredTags: []
           , tags: []
           };
  }
  , computed: {
    availableTags() {
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
  }
}
</script>

<style scoped lang="scss">
.with-right-space-margin {
  margin-right: 1.5em;
};
</style>