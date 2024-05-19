<template>
  <div class="justify-content-center">
    <h2 align="center">Enter the code sent to your email and change your password</h2>

    <div class="col-sm-6 mx-auto">
      <form @submit.prevent="resetPassword">
        <div class="form-group">
          <label for="codeInput">Code</label>
          <input type="text" v-model="code" @input="filterNonDigits" class="form-control" id="codeInput" placeholder="Enter code">
        </div>
        <div class="form-group">
          <label for="passwordInput">Password</label>
          <input type="password" v-model="password" class="form-control" id="passwordInput" placeholder="Enter password">
        </div>
        <div class="form-group">
          <label for="repeatPasswordInput">Repeat Password</label>
          <input type="password" v-model="repeatPassword" class="form-control" id="repeatPasswordInput" placeholder="Repeat password">
        </div>
        <br>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <button type="submit" class="btn btn-primary" :disabled="!code||!password||!repeatPassword">Submit</button>
      </form>
      <div class="text-center">
        <p>No need to change password? <router-link :to="{ name: 'login' }">Sign in</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "ResetPasswordComponent",
  data() {
    return {
      email: '',
      code: '',
      password: '',
      repeatPassword: '',
      errorMessage: null
    };
  },
  methods: {
    resetPassword() {
      this.errorMessage = null;

      axios.post("/api/public/reset", {
        email: localStorage.getItem('email'),
        code: this.code,
        password: this.password,
        repeatPassword: this.repeatPassword
      })
          .then(response => {
            if (response.data) {
              this.$router.push({ name: 'login' });
            }
          })
          .catch(error => {
            console.error('Login error:', error);
            if (error.response) {
              console.log(error.response)
              this.errorMessage = error.response.data.message;
            }
          });
    },
    filterNonDigits() {
      this.code = this.code.replace(/\D/g, '');
    }
  }
}
</script>

<style scoped>

</style>
