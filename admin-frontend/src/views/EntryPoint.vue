<template>
  <section class="section">
    <div class="container has-text-centered">
      <h1 class="title is-2">Welcome to the admin space!</h1>
    </div>
  </section>
</template>

<script>
    export default {
        created() {
          this.openLoading();
          this.$store.dispatch('fetchAvailableTags');
        },
        methods: {
            openLoading() {
                this.$store.dispatch('incrementLoadingCount');
                setTimeout(() => {
                  this.$store.dispatch('decrementLoadingCount');
                }, 10 * 1000)
            }
        }
    }
</script>
