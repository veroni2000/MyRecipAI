<template>
  <div class="login">
    <h2 align="center">User Login</h2>
    <div class="col-sm-6 mx-auto">
      <form @submit.prevent="loginUser">
        <div class="form-group">
          <label for="loginEmailInput">Email</label>
          <MDBInput type="email" v-model="user.email" class="form-control" id="loginEmailInput"
                    placeholder="Enter email"/>
        </div>
        <div class="form-group">
          <label for="loginPasswordInput">Password</label>
          <MDBInput type="password" v-model="user.password" class="form-control" id="loginPasswordInput"
                    placeholder="Enter password"/>
        </div>
        <div class="text-center">
          <router-link :to="{ name: 'forgotten' }">Forgot password?</router-link>
        </div>
        <br>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>

        <MDBBtn type="submit" class="btn btn-primary" :disabled="!user.email||!user.password">Sign in</MDBBtn>
      </form>
      <div class="text-center">
        <p>Not a member?
          <router-link :to="{ name: 'register' }">Register</router-link>
        </p>
      </div>

    </div>
  </div>
</template>

<script>
import axios from 'axios';
import {
  MDBInput,
  MDBBtn,
} from "mdb-vue-ui-kit";
import {ref} from "vue";

export default {
  name: 'LoginComponent',
  components: {
    MDBInput,
    MDBBtn,
  },
  data() {
    return {
      user: {
        email: '',
        password: ''
      },
      errorMessage: null
    };
  },
  setup() {
    const loginEmailInput = ref("");
    const loginPasswordInput = ref("");

    return {
      loginEmailInput,
      loginPasswordInput,
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
                this.$router.push({name: 'verify'});
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
<style>
.login {
  max-width: 60%;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>

