<!-- LoginComponent.vue -->

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
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/components/apiClient";

export default {
  name: 'LoginComponent',
  data() {
    return {
      user: {
        email: '',
        password: ''
      }
    };
  },
  methods: {
    loginUser() {
      axiosInstance.post("/public/login", this.user)
          .then(response => {
            if (response.data && response.data.token) {
              console.log(response.data)
              // Store the token in localStorage or a more secure storage mechanism
              localStorage.clear();
              localStorage.setItem('user', response.data)
              localStorage.setItem('jwtToken', response.data.token);

              alert("Login successful");
              // You can redirect to another page after successful login
              this.$router.push({ name: 'user', params: { userId: response.data.id } });
            } else {
              alert("Login failed. Check your credentials.");
            }
          })
    }
  }
};
</script>
