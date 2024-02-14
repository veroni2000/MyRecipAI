<template>
  <div v-if="isCurrentUser">
    <router-link to="/user/edit">Edit profile</router-link>
  </div>
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
  computed: {
    isCurrentUser() {
      const userIdFromParams = this.$route.params.userId;
      const loggedInUserId = localStorage.getItem("id");
      return userIdFromParams === loggedInUserId;
    },
  },
  mounted() {
    this.fetchUserDetails();
  },
  methods: {
    fetchUserDetails() {
      console.log(this.$route.params.userId)
      const userId = this.$route.params.userId;
      console.log('User ID: ' + localStorage.getItem('id'))

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
      localStorage.clear();
      this.$router.push({ name: 'login' });
    }
  }
}
</script>

<style scoped>

</style>