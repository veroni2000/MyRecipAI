<template>
  <div class="justify-content-center">
    <h2 align="center">Enter your email to reset password</h2>

    <div class="col-sm-6 mx-auto">
      <form @submit.prevent="sendCode">
        <div class="form-group">
          <label for="loginEmailInput">Email</label>
          <input type="email" v-model="email" class="form-control" id="loginEmailInput" placeholder="Enter email">
        </div>
        <br>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <button type="submit" class="btn btn-primary" :disabled="!email">Send code</button>
      </form>
      <div class="text-center">
        <p>No need to reset? <router-link :to="{ name: 'login' }">Sign in</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "EmailComponent",
  data() {
    return {
      email: '',
      errorMessage: null
    };
  },
  methods: {
    sendCode() {
      this.errorMessage = null;


      axios.post("/api/public/reset/email", { email: this.email })
          .then(response => {
            if (response.data) {
              localStorage.setItem('email', this.email)
              this.$router.push({ name: 'reset' });
            }
          })
          .catch(error => {
            console.error('Login error:', error);
            if (error.response) {
              console.log(error.response)
              this.errorMessage = error.response.data.message;
            }
          });
    }
  }
}
</script>

<style scoped>

</style>