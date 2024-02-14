<template>
  <div class="row">
    <h2 align="center">Enter the code sent to your email</h2>

    <div class="col-sm-6">
      <form @submit.prevent="verifyUser">
        <div class="form-group">
          <label for="verofyCodeInput">Code</label>
          <input type="text" v-model="user.code" class="form-control" id="verifyCodeInput" placeholder="Enter code">
        </div>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <br>
        <button type="submit" class="btn btn-primary">Submit</button>
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
    }
  }
}
</script>

<style scoped>

</style>