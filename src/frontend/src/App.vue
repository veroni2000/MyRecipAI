<template>
  <div id="app">
    <MDBNavbar
        container
        expand="lg"
        bg="light"
        class="d-flex justify-content-between"
    >
      <MDBNavbarBrand>
        <MDBIcon icon="cookie-bite" size="2x" class="mx-3">MyRecipAI</MDBIcon>
      </MDBNavbarBrand>
      <MDBNavbarNav class="mb-2 mb-lg-0">
        <MDBNavbarItem to="/" linkClass="link-secondary">Home</MDBNavbarItem>
        <MDBNavbarItem to="/calculator" linkClass="link-secondary">Metrics calculator</MDBNavbarItem>
        <MDBNavbarItem v-if="isLoggedIn" to="/addRecipe" linkClass="link-secondary">Add Recipe</MDBNavbarItem>
        <MDBNavbarItem v-if="isLoggedIn" to="/generate" onclick="window.location.href=''" linkClass="link-secondary">Generate Recipe</MDBNavbarItem>
      </MDBNavbarNav>
      <MDBNavbarNav id="navbarSupportedContent">
        <!-- Search form -->
        <form class="d-flex input-group w-auto">
          <input
              type="search"
              class="form-control"
              placeholder="Search"
              aria-label="Search"
          />
          <MDBIcon icon="search" size="2x" id="searchIcon" class="mx-3"></MDBIcon>
        </form>
        <!-- Search form -->
      </MDBNavbarNav>
      <MDBNavbarNav class="mb-2 mb-lg-0 d-flex flex-row">
        <MDBNavbarItem v-if="isLoggedIn" onclick="window.location.href=''" :to="profileLink" class="me-3 me-lg-0">
          <img v-if="profilePic" :src="require(`../../main/resources/images/${this.profilePic}`)"
                class="rounded-circle"
               height="40"
               alt="Profile"
               loading="lazy">
          <img v-else :src="require(`../../main/resources/images/default.png`)"
               class="rounded-circle"
               height="40"
               alt="Profile"
               loading="lazy">
        </MDBNavbarItem>
        <MDBNavbarItem v-if="isLoggedIn" @click="logout" id="logoutButton" class="me-3 me-lg-0">
          <MDBIcon icon="as fa-sign-out-alt" size="2x" class="mx-3" style="margin-top: 10px"></MDBIcon>
        </MDBNavbarItem>
        <MDBNavbarItem v-if="!isLoggedIn" to="/login" linkClass="link-secondary">Login</MDBNavbarItem>
      </MDBNavbarNav>
    </MDBNavbar>
    <router-view></router-view>
  </div>
</template>

<script>
import {
  MDBNavbar,
  MDBNavbarBrand,
  MDBNavbarItem,
  MDBNavbarNav,
  MDBIcon,
} from 'mdb-vue-ui-kit';
import {ref} from "vue";
import axiosInstance from "@/components/apiClient";

export default {
  name: 'App',
  components: {
    MDBNavbar,
    MDBNavbarBrand,
    MDBNavbarItem,
    MDBNavbarNav,
    MDBIcon,
  },
  setup() {
    const collapse7 = ref(false);

    return {
      collapse7,
    }
  },
  data() {
    return {
      isLoggedIn: false,
      profilePic: null,
    };
  },
  computed: {
    profileLink() {
      const userId = localStorage.getItem('id');
      this.fetchUserDetails();
      return {name: 'user', params: {userId}};
    }
  },
  methods: {
    logout() {
      localStorage.clear();
      // location.reload();
      // this.$router.push({ name: 'login' });
      window.location.href = '/';
    },
    async checkLoggedIn() {
      const token = localStorage.getItem('jwtToken');
      this.isLoggedIn = !!token;
    },
    fetchUserDetails() {
      if (localStorage.getItem('id')) {
        const userId = localStorage.getItem('id');

        axiosInstance.get(`/user/${userId}`)
            .then(response => {
              this.profilePic = response.data.image;
            })
            .catch(error => {
              console.error('Error fetching user details:', error);
            });
      }
    },
  },
  async created() {
    await this.checkLoggedIn();
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  margin-bottom: 20px;
}

nav a {
  margin-right: 10px;
}
#logoutButton {
  margin-top: 2px;
}
#logoutButton:hover, #searchIcon:hover {
  transform: translateY(-5px);
  transition: transform 0.3s ease-in-out;
}
</style>
