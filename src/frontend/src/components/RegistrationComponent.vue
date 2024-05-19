<template>
  <div class="justify-content-center">
    <h2 align="center">User Registration</h2>

    <div class="col-sm-6 mx-auto">
      <form @submit.prevent="saveData">
        <div class="form-group">
          <label for="userEmailInput">Email</label>
          <MDBInput type="email" v-model="user.email" class="form-control" id="userEmailInput"
                    placeholder="Enter email"/>
        </div>

        <div class="form-group">
          <label for="userFirstNameInput">First Name</label>
          <MDBInput type="text" v-model="user.firstName" class="form-control" id="userFirstNameInput"
                    placeholder="Enter first name"/>
        </div>

        <div class="form-group">
          <label for="userLastNameInput">Last Name</label>
          <MDBInput type="text" v-model="user.lastName" class="form-control" id="userLastNameInput"
                    placeholder="Enter last name"/>
        </div>

        <div class="form-group">
          <label for="userPasswordInput">Password</label>
          <MDBInput type="password" v-model="user.password" class="form-control" id="userPasswordInput"
                    placeholder="Enter password"/>
        </div>
        <br>
        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>
        <br>
        <MDBBtn type="submit" class="btn btn-primary"
                :disabled="!user.email||!user.firstName||!user.lastName||!user.password">Register
        </MDBBtn>
      </form>
      <div class="text-center">
        <p>Already have an account?
          <router-link :to="{ name: 'login' }">Sign in</router-link>
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
  name: 'RegistrationComponent',
  components: {
    MDBInput,
    MDBBtn,
  },
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
  setup() {
    const loginEmailInput = ref("");
    const loginPasswordInput = ref("");

    return {
      loginEmailInput,
      loginPasswordInput,
    };
  },
  methods: {
    saveData() {
      // Clear previous error message
      this.errorMessage = null;

      axios.post("/api/public/register", this.user)
          .then(response => {
            if (response.data) {
              localStorage.setItem('email', this.user.email)
              this.$router.push({name: 'verify'});
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