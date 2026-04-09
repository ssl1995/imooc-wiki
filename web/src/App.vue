<template>
  <a-layout style="min-height: 100vh;">
    <the-header/>
    <router-view style="flex: 1;"/>
    <the-footer/>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import TheHeader from '@/components/the-header.vue';
import TheFooter from '@/components/the-footer.vue';

export default defineComponent({
  name: 'app',
  components: {
    TheHeader, TheFooter
  },
});
</script>


