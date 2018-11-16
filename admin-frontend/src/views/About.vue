<template>
  <div class="about">
    <h1>This is an about page</h1>
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
        This column should be wider than previous one.<i class="fas fa-archive"></i> Icon?
        <b-field label="Select a date">
          <b-datepicker
            placeholder="Click to select..."
            icon="calendar">
          </b-datepicker>
        </b-field>
      </div>        
    </div>
  </div>
</template>
