<template>
  <div class="verify">
    <h2 align="center">Enter the code sent to your email</h2>

    <div class="col-sm-6 mx-auto">
      <form @submit.prevent="verifyUser">
        <div class="form-group">
          <label for="verifyCodeInput">Code</label>
          <input type="text" v-model="user.code" class="form-control" id="verifyCodeInput" @input="validateInput" placeholder="Enter code">
        </div>
        <br>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <button type="submit" class="btn btn-primary" :disabled="!user.code">Submit</button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "VerifyUserComponent",
  data() {
    return {
      user: {
        email: localStorage.getItem('email'),
        code: '',
      }
      ,
      errorMessage: null
    };
  },
  methods: {
    verifyUser() {
      this.errorMessage = null;
      if(!localStorage.getItem('email'))
        this.$router.push({ name: 'login' });


      axios.post("/api/public/verify", this.user)
          .then(response => {
            if (response) {
              console.log(response)
              localStorage.clear();
              localStorage.setItem('msg', 'Login again after verifying account')
              this.$router.push({ name: 'login' });
            }else {
              this.errorMessage = "Incorrect code";
            }
          })
          .catch(error => {
            if (error.response) {
              this.errorMessage = error.response.data.message;
            } else {
              this.errorMessage = "An unexpected error occurred. Please try again later.";
            }
          });
    },
    validateInput() {
      // Remove non-numeric characters from the input value
      this.user.code = this.user.code.replace(/\D/g, '');
    }
  }
}
</script>

<style>
.verify {
  max-width: 60%;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>