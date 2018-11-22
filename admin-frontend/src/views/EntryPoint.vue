<template>
  <section class="section">
    <div class="container has-text-centered">
      <h1 class="title is-2">Welcome to the admin space!</h1>
      <b-loading :is-full-page="true" :active.sync="isLoading" :can-cancel="false"></b-loading>
    </div>
  </section>
</template>

<script>
    export default {
        data() {
            return {
                isLoading: false,
            }
        },
        created() {
          this.openLoading();
          this.$store.dispatch('fetchAvailableTags');
        },
        methods: {
            openLoading() {
                this.isLoading = true
                setTimeout(() => {
                    this.isLoading = false
                }, 10 * 1000)
            }
        }
    }
</script>
