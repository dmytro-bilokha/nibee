<template>
  <div id="app">
    <div class="columns">
      <div class="column is-1 is-pulled-left right-line">
        <aside
          id="main-menu"
          class="menu"
        >
          <p class="menu-label">Post</p>
          <ul class="menu-list">
            <li><router-link to="/create">Create</router-link></li>
            <li><a>Delete</a></li>
            <li><a>Modify</a></li>
          </ul>
        </aside>
      </div>
      <div class="column">
          <router-view/>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
};

#main-menu {
  padding-top: 1em;
  padding-left: 0.5em;
};
</style>
