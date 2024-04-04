<template>
  <h1>Home page</h1>
<!--  <router-link :to="{ name: 'login' }">Login</router-link> or <router-link :to="{ name: 'register' }">Register</router-link>-->
  <p>{{msg}}</p>
</template>

<script>
export default {
  name: "HomePageComponent",
  data(){
    return{
      msg: ''
    }
  },
  mounted() {
    this.msg = localStorage.getItem('jwtToken')
  }
}
</script>

<style scoped>

</style>