<template>
  <div class="row">
    <h2 align="center">User Login</h2>

    <div class="col-sm-6">
      <form @submit.prevent="loginUser">
        <div class="form-group">
          <label for="loginEmailInput">Email</label>
          <input type="email" v-model="user.email" class="form-control" id="loginEmailInput" placeholder="Enter email">
        </div>

        <div class="form-group">
          <label for="loginPasswordInput">Password</label>
          <input type="password" v-model="user.password" class="form-control" id="loginPasswordInput" placeholder="Enter password">
        </div>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <br>
        <button type="submit" class="btn btn-primary">Login</button>
      </form>
      <router-link :to="{ name: 'register' }">Register</router-link>
      <br>
      <router-link :to="{ name: 'forgotten' }">Forgotten password</router-link>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'LoginComponent',
  data() {
    return {
      user: {
        email: '',
        password: ''
      },
      errorMessage: null
    };
  },
  methods: {
    loginUser() {
      this.errorMessage = null;

      axios.post("/api/public/login", this.user)
          .then(response => {
            if (response.data && response.data.token) {
              console.log(response.data)
              localStorage.clear();
              localStorage.setItem('id', response.data.id);
              localStorage.setItem('jwtToken', response.data.token);

              console.log(response.data)
              console.log(localStorage.getItem('user'))
              // this.$router.push({ name: 'home'});
              window.location.href = '/';
            } else {
              this.errorMessage = "Login failed. Check your credentials.";
            }
          })
          .catch(error => {
            console.error('Login error:', error);
            if (error.response) {
              console.log(error.response)
              const errorMessage = error.response.data.message;
              if (errorMessage === 'User is not verified') {
                localStorage.setItem('email', this.user.email)
                this.$router.push({ name: 'verify' });
              } else {
                this.errorMessage = errorMessage;
              }
            } else {
              // Display generic error message for other errors
              this.errorMessage = "An unexpected error occurred. Please try again later.";
            }
          });
    }
  }
};
</script>
