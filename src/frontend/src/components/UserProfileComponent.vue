<template>
  <div>
    <h2>User Profile</h2>
    <div>
      <strong>Email:</strong> {{ user.email }}
    </div>
    <div>
      <strong>First Name:</strong> {{ user.firstName }}
    </div>
    <div>
      <strong>Last Name:</strong> {{ user.lastName }}
    </div>
  <button @click="logout">Logout</button>
  </div>
</template>

<script>
import axiosInstance from "@/components/apiClient";

export default {
  name: "UserProfileComponent",
  data() {
    return {
      user: {},
    };
  },
  mounted() {
    this.fetchUserDetails();
  },
  methods: {
    fetchUserDetails() {
      // Retrieve user ID from route parameters
      console.log(this.$route.params.userId)
      const userId = this.$route.params.userId;

      axiosInstance.get(`/user/${userId}`)
          .then(response => {
            this.user = response.data;
            console.log(this.user)
          })
          .catch(error => {
            console.error('Error fetching user details:', error);
          });
    },
    logout() {
      localStorage.removeItem('jwtToken');
      this.$router.push({ name: 'login' });
    }
  }
}
</script>

<style scoped>

</style>