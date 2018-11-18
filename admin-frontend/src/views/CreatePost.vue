<template>
  <div class="about">
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
            />
            <b-timepicker
              placeholder="Set time..."
              icon="clock"
              editable
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
            <b-checkbox
            >
              Shareable
            </b-checkbox>
            <b-checkbox>
              Comment allowed
            </b-checkbox>
          </b-field>
        </div>
      </section>
  </div>
</template>

<script>
export default {
  data() {
    return {
      file: null
      , availableTags: []
    };
  }
}
</script>