<template>
  <div class="about">
    <div class="columns">
      <div class="column is-1 is-pulled-left right-line">
        <aside class="menu">
          <p class="menu-label">
            Post
          </p>
          <ul class="menu-list">
            <li><a>Create</a></li>
            <li><a>Delete</a></li>
            <li><a>Modify</a></li>
          </ul>
        </aside>
      </div>
      <div class="column">
        <section class="section">
        <div class="container has-text-left">
          <b-field horizontal label="Post file" class="file">
            <b-upload v-model="file">
              <a class="button is-primary" v-if="!file">
                <b-icon icon="upload"></b-icon>
                <span>Click to choose</span>
              </a>
              <a class="button is-primary" v-if="file">
                <b-icon icon="edit"></b-icon>
                <span>{{ file.name }}</span>
              </a>
            </b-upload>
          </b-field>
          <b-field horizontal label="Select a date">
            <b-datepicker
              placeholder="Click to select..."
              icon="calendar">
            </b-datepicker>
          </b-field>
          <b-field horizontal label="Subject" type="is-danger" message="Please enter a subject">
            <b-input name="subject" expanded></b-input>
          </b-field>
          <b-field horizontal label="From">
            <b-input name="name" placeholder="Name" expanded></b-input>
            <b-input name="email" type="email" placeholder="nobody@nowhere.com" expanded></b-input>
          </b-field>
        </div>
        </section>
      </div>        
    </div>
  </div>
</template>

<script>
    export default {
        data() {
            return {
                file: null
            }
        }
    }
</script>