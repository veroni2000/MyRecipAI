<template>
  <div class="home-page">
<!--    <p>{{msg}}</p>-->
    <div class="tabs">
      <button :class="{ active: activeTab === 'all' }" @click="setActiveTab('all')">All Recipes</button>
      <button v-if="isLoggedIn" :class="{ active: activeTab === 'following' }" @click="setActiveTab('following')">Following</button>
    </div>
    <div v-for="recipe in recipes" :key="recipe.id">
      <RecipeComponent :recipe="recipe" />
    </div>
    <div ref="sentinel" class="sentinel"></div>
  </div>
</template>

<script>
import axiosInstance from "@/services/apiClient";
import RecipeComponent from "@/components/RecipeComponent"; // Import the new component

export default {
  name: "HomePageComponent",
  components: {
    RecipeComponent, // Register the component
  },
  data() {
    return {
      msg: '',
      recipes: [],
      isLoggedIn: false,
      activeTab: 'all', // Track the active tab
      page: 0, // Track the current page
      pageSize: 10, // Number of recipes to fetch each time
      loading: false, // Track loading state to prevent multiple requests
    };
  },
  mounted() {
    this.msg = localStorage.getItem('jwtToken');
    this.checkLoggedIn();
    this.getRecipes();
    this.setupObserver();
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      this.page = 0;
      this.recipes = [];
      if (tab === 'all') {
        this.getRecipes();
      } else {
        this.getFollowedUsersRecipes();
      }
    },
    getRecipes() {
      if (this.loading) return;

      this.loading = true;
      axiosInstance.get(`/public/recipes/all`, {
        params: {
          page: this.page,
          size: this.pageSize
        }
      })
          .then(response => {
            this.recipes = [...this.recipes, ...response.data.content];
            this.page += 1;
            this.loading = false;
          })
          .catch(error => {
            console.error('Error fetching recipes:', error);
            this.loading = false;
          });
    },
    async checkLoggedIn() {
      const token = localStorage.getItem('jwtToken');
      this.isLoggedIn = !!token;
    },
    getFollowedUsersRecipes() {
      if (localStorage.getItem('id')) {
        const userId = localStorage.getItem('id');

        axiosInstance.get(`/recipe/followedUsersRecipe`, {
          params: {
            userId,
            page: this.page,
            size: this.pageSize
          }
        })
            .then(response => {
              this.recipes = [...this.recipes, ...response.data.content];
              this.page += 1;
              this.loading = false;
            })
            .catch(error => {
              console.error('Error fetching recipes:', error);
              this.loading = false;
            });
      }
    },
    setupObserver() {
      const sentinel = this.$refs.sentinel;

      const observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting && !this.loading) {
          if (this.activeTab === 'all') {
            this.getRecipes();
          } else {
            this.getFollowedUsersRecipes();
          }
        }
      });

      observer.observe(sentinel);
    }
  }
};
</script>

<style scoped>
.home-page {
  text-align: center;
  padding: 20px;
}

.tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.tabs button {
  background: none;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 1.2em;
  font-weight: bold;
}

.tabs button.active {
  border-bottom: 2px solid #000;
}

.title {
  font-size: 2em;
  margin-bottom: 20px;
}

.message {
  margin-bottom: 20px;
  font-size: 1.2em;
}

.sentinel {
  height: 1px;
}
</style>
