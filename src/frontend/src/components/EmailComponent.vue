<template>
  <div class="row">
    <h2 align="center">Enter your email to reset password</h2>

    <div class="col-sm-6">
      <form @submit.prevent="sendCode">
        <div class="form-group">
          <label for="loginEmailInput">Email</label>
          <input type="email" v-model="email" class="form-control" id="loginEmailInput" placeholder="Enter email">
        </div>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <br>
        <button type="submit" class="btn btn-primary">Send code</button>
      </form>
      <router-link :to="{ name: 'login' }">Login</router-link>
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
              alert("Password reset code was sent");
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