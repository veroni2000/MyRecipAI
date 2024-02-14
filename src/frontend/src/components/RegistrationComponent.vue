<template>
  <div class="row">

    <h2 align="center">User Registration</h2>

    <div class="col-sm-6">
      <form @submit.prevent="saveData">
        <div class="form-group">
          <label for="userEmailInput">Email</label>
          <input type="email" v-model="user.email" class="form-control" id="userEmailInput" placeholder="Enter email">
        </div>

        <div class="form-group">
          <label for="userFirstNameInput">First Name</label>
          <input type="text" v-model="user.firstName" class="form-control" id="userFirstNameInput" placeholder="Enter first name">
        </div>

        <div class="form-group">
          <label for="userLastNameInput">Last Name</label>
          <input type="text" v-model="user.lastName" class="form-control" id="userLastNameInput" placeholder="Enter last name">
        </div>

        <div class="form-group">
          <label for="userPasswordInput">Password</label>
          <input type="password" v-model="user.password" class="form-control" id="userPasswordInput" placeholder="Enter password">
        </div>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
    <br>
        <button type="submit" class="btn btn-primary">Submit</button>
      </form>
      <router-link :to="{ name: 'login' }">Go to Login</router-link>
    </div>
  </div>
</template>

<script>

import axios from 'axios';

export default {
  name: 'RegistrationComponent',
  data() {
    return {
      user: {
        email: '',
        firstName: '',
        lastName: '',
        password: ''
      },
      errorMessage: null
    };
  },
  methods: {
    saveData() {
      // Clear previous error message
      this.errorMessage = null;

      axios.post("/api/public/register", this.user)
          .then(response => {
            if (response.data) {
              alert("User saved successfully");
              localStorage.setItem('email', this.user.email)
              this.$router.push({ name: 'verify' });
            }
          })
          .catch(error => {
            if (error.response && error.response.status === 400) {
              // Handle BadRequestException with a specific message
              const errorMessage = error.response.data.message || "Bad Request";
              if (errorMessage === "Enter a valid email!") {
                this.errorMessage = errorMessage;
              } else {
                // Handle other BadRequestException cases or show a generic message
                this.errorMessage = "Invalid request. Please check your input.";
              }
            } else {
              console.log(error.response)
              this.errorMessage = "Registration failed. Recheck your input.";
            }
          });
    }
  },
  mounted() {
    console.log(this.$router);
  }
};
</script>