<template>
  <div id="app">
    <nav>
      <router-link to="/">Home</router-link>
      <router-link to="/calculator">Metrics calculator</router-link>
      <router-link v-if="isLoggedIn" to="/addRecipe">Add Recipe</router-link>
      <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
      <router-link v-if="isLoggedIn" onclick="window.location.href=''" :to="profileLink">Profile</router-link>
      <button v-if="isLoggedIn"  @click="logout">Logout</button>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      isLoggedIn: false
    };
  },
  computed: {
    profileLink() {
      const userId = localStorage.getItem('id');
      return { name: 'user', params: { userId } };
    }
  },
  methods: {
    logout() {
      localStorage.clear();
      // location.reload();
      // this.$router.push({ name: 'login' });
      window.location.href = '/login';
    },
    async checkLoggedIn() {
      const token = localStorage.getItem('jwtToken');
      this.isLoggedIn = !!token;
    }
  },
  async created() {
    await this.checkLoggedIn();
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

nav {
  margin-bottom: 20px;
}

nav a {
  margin-right: 10px;
}
</style>
