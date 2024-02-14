<template>
  <div>
    <h2>Edit User Profile</h2>
    <form @submit.prevent="updateUser">
      <div>
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="user.email" required>
      </div>
      <div>
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" v-model="user.firstName" required>
      </div>
      <div>
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" v-model="user.lastName" required>
      </div>
      <div v-if="errorMessage" class="alert alert-danger" role="alert">
        {{ errorMessage }}
      </div>
      <button type="submit">Save Changes</button>
    </form>
  </div>
</template>

<script>
import axiosInstance from "@/components/apiClient";

export default {
  name: "UserEditComponent",
  data() {
    return {
      user: {
      },
      errorMessage: null
    };
  },
  mounted() {
    this.fetchUserDetails();
  },
  methods: {
    fetchUserDetails() {
      const userId = localStorage.getItem('id');

      axiosInstance.get(`/user/${userId}`)
          .then(response => {
            this.user = response.data;
          })
          .catch(error => {
            console.error('Error fetching user details:', error);
          });
      console.log(this.user)
    },
    updateUser() {
      axiosInstance.post(`/user/edit`, this.user)
          .then(response => {
            // Optionally handle success response
            console.log('User profile updated:', response.data);
            this.$router.push({ name: 'user', params: { userId: localStorage.getItem('id') } });
          })
          .catch(error => {
            console.error('Error updating user profile:', error);
          });
    }
  }
}
</script>

